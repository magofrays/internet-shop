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

import java.io.IOException;
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
    private final Path baseSavePath;
    private final HashSet<String> allowedExtensions;
    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ConcurrentHashMap<UUID, Object> fileLocks = new ConcurrentHashMap<>();

    @SneakyThrows
    public FileStorageService(@Value("${spring.servlet.multipart.location}") String baseLocation,
                              @Value("${file.upload.allowed-extensions}") String[] extensions){
        baseUploadPath = Paths.get(baseLocation, "upload").toAbsolutePath().normalize();
        baseSavePath = Paths.get(baseLocation, "save").toAbsolutePath().normalize();
        allowedExtensions = new HashSet<>(Arrays.asList(extensions));
        Files.createDirectories(baseUploadPath);
    }

    private void createDir(String url, boolean save){
        Path fullPath = save ? baseSavePath.resolve(url) : baseUploadPath.resolve(url);
        if(Files.exists(fullPath)){
            return;
        }
        try{
            Files.createDirectories(fullPath);
        } catch (IOException ignored){
        }
    }

    @SneakyThrows
    public String saveFile(Resource file, String url, UUID entityId, String oldPath){
        log.debug("Trying to save file for entity: {}", entityId);
        createDir(url, true);
        Object fileLock = fileLocks.computeIfAbsent(entityId, k -> new Object());
        synchronized (fileLock){
            String newFileName = generateFileName(entityId, file.getFilename());
            if(oldPath != null){
                Path pathToDelete = baseSavePath.resolve(oldPath);
                deleteFile(pathToDelete);
            }
            Path targetPath = baseSavePath.resolve(url).resolve(newFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}. For entity: {}", targetPath, entityId);
            return url + "/" + newFileName;
        }
    }


    @SneakyThrows
    public String uploadFile(MultipartFile file, String url, UUID entityId, String oldPath){
        log.debug("Trying to upload file for entity: {}", entityId);
        createDir(url, false);
        Object fileLock = fileLocks.computeIfAbsent(entityId, k -> new Object());
        synchronized (fileLock){
            String newFileName = generateFileName(entityId, file.getOriginalFilename());
            if(oldPath != null){
                Path pathToDelete = baseUploadPath.resolve(oldPath);
                deleteFile(pathToDelete);
            }
            Path targetPath = baseUploadPath.resolve(url).resolve(newFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded: {}. For entity: {}", targetPath, entityId);
            return url + "/" + newFileName;
        }
    }

    public void deleteFileForEntity(String url, UUID entityId){
        log.info("Trying to delete file: {} for entity: {}", url, entityId);
        Object fileLock = fileLocks.computeIfAbsent(entityId, k -> new Object());
        if(url == null){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "File not found!");
        }
        Path path = baseUploadPath.resolve(url);
        synchronized (fileLock){
            if(!Files.exists(path)){
                log.warn("No file found for path {} for entity {}", path, entityId);
                throw new BusinessException(HttpStatus.NOT_FOUND, "File not found!");
            }
            deleteFile(path);
        }
    }

    @SneakyThrows
    private void deleteFile(Path path){
        log.info("Deleting image: {}", path);
        Path filePath = baseUploadPath.resolve(path);
        Files.delete(filePath);
    }

    private String generateFileName(UUID id, String filename){
        log.debug("Generating filename for file {} for entity {}", filename, id);
        String extension = getFileExtension(filename);
        int unique = counter.getAndIncrement();
        String removeEx = removeFileExtension(filename);
        return removeEx + String.format("-%s-%d-%d.%s",
                id.toString(),
                System.currentTimeMillis(),
                unique,
                extension
        );
    }

    public void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("File is empty to save image.");
            throw new BusinessException(HttpStatus.BAD_REQUEST, "File is empty!");
        }

        if (file.getSize() > maxFileSize.toBytes()) {
            log.warn("File is too big to save image.");
            throw new BusinessException(HttpStatus.BAD_REQUEST, "File is too big!");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("File is not image.");
            throw new BusinessException(HttpStatus.BAD_REQUEST, "File is not image!");
        }
        if(file.getOriginalFilename() == null){
            log.warn("No file name.");
            throw new BusinessException(HttpStatus.BAD_REQUEST, "No file name!");
        }
        String extension = getFileExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            log.warn("Bad file extension. Probably not image.");
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "Bad file extension. Probably not image!"
            );
        }
    }

    private String getFileExtension(String originalFilename) {
        return originalFilename
                .substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase();
    }

    private String removeFileExtension(String filename){
        int lastDotIndex = filename.lastIndexOf(".");
        return filename.substring(0, lastDotIndex);
    }

    public Resource getFileByPath(String path){
        log.info("Trying to get file by path: {}", path);
        if(path == null){
            log.error("Path is empty");
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "File not found!");
        }

        Path uploadPath = baseUploadPath.resolve(path);
        if(Files.exists(uploadPath)){
            return new FileSystemResource(uploadPath.toFile());
        }
        Path savePath = baseSavePath.resolve(path);
        if(Files.exists(savePath)){
            return new FileSystemResource(savePath.toFile());
        }
        log.error("Path {} is wrong", path);
        throw new BusinessException(HttpStatus.NOT_FOUND, "File not found!");
    }

}
