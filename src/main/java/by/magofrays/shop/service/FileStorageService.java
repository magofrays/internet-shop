package by.magofrays.shop.service;

import by.magofrays.shop.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class FileStorageService {

    private final Path baseUploadPath;
    private final HashSet<String> allowedExtensions;
    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ConcurrentHashMap<UUID, Object> fileLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> itemImagesPath = new ConcurrentHashMap<>();

    @SneakyThrows
    public FileStorageService(@Value("${spring.servlet.multipart.location}") String baseLocation,
                              @Value("${file.upload.allowed-extensions}") String[] extensions){
        baseUploadPath = Paths.get(baseLocation, "upload").toAbsolutePath().normalize();
        allowedExtensions = new HashSet<>(Arrays.asList(extensions));
        Files.createDirectories(baseUploadPath);
        Files.createDirectories(baseUploadPath.resolve("images/items"));

    }


    @SneakyThrows
    public String saveItemImage(MultipartFile image, UUID itemId){
        validateImageFile(image);
        Object fileLock = fileLocks.computeIfAbsent(itemId, k -> new Object());
        synchronized (fileLock){
            String newFileName = generateFileName(itemId, image, "item");
            String oldPath = itemImagesPath.get(itemId);
            if(oldPath != null){
                deleteFile(oldPath);
            }
            itemImagesPath.put(itemId, "images/items/" + newFileName);
            Path targetPath = baseUploadPath.resolve("images/items").resolve(newFileName);
            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Image saved: {}", targetPath);
            return "images/items/" + newFileName;
        }
    }

    public void deleteItemImage(UUID itemId){
        Object fileLock = fileLocks.computeIfAbsent(itemId, k -> new Object());
        synchronized (fileLock){
            String path = itemImagesPath.get(itemId);
            if(path == null){
                throw new BusinessException(HttpStatus.NOT_FOUND);
            }
            itemImagesPath.remove(itemId);
            deleteFile(path);
        }
    }

    @SneakyThrows
    private void deleteFile(String path){
        log.info("Deleting image: {}", path);
        Path filePath = baseUploadPath.resolve(path);
        Files.delete(filePath);
    }

    private String generateFileName(UUID id, MultipartFile file, String start){
        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        int unique = counter.getAndIncrement();
        return start + String.format("-%s-%d-%d.%s",
                id.toString(),
                System.currentTimeMillis(),
                unique,
                extension
        );
    }

    public void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > maxFileSize.toBytes()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        if(file.getOriginalFilename() == null){
            throw new BusinessException(HttpStatus.BAD_REQUEST);
        }
        String extension = getFileExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private String getFileExtension(String originalFilename) {
        return originalFilename
                .substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase();
    }

    public Resource getImageByPath(String imagePath){
        File file = baseUploadPath.resolve(imagePath).toFile();
        return new FileSystemResource(file);
    }

}
