package by.magofrays.shop.unittest;

import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.service.FileStorageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@TestPropertySource(
       properties = {
               "spring.servlet.multipart.max-file-size=5MB"
       }
)
public class FileStorageServiceTest {

    @TempDir
    Path tempDir;
    private final String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};
    private FileStorageService fileStorageService;
    private final UUID itemId = UUID.randomUUID();

    @BeforeEach
    public void setUp(){
        fileStorageService = new FileStorageService(tempDir.toString(), allowedExtensions);
        ReflectionTestUtils.setField(
                fileStorageService,
                "maxFileSize",
                DataSize.ofMegabytes(5)
        );
    }

    @Test
    @SneakyThrows
    public void saveAndDeleteItemImageTest(){

        MultipartFile image = createMockImage();
        byte[] imageContent = image.getBytes();
        String savedPath = fileStorageService.saveItemImage(image, itemId);
        assertNotNull(savedPath);
        assertTrue(savedPath.startsWith("images/items/item-"));
        assertTrue(savedPath.endsWith(".jpg"));
        Path expectedFilePath = tempDir.resolve("upload/images/items")
                .resolve(savedPath.substring("images/items/".length()));
        assertTrue(Files.exists(expectedFilePath));
        assertArrayEquals(imageContent, Files.readAllBytes(expectedFilePath));
        fileStorageService.deleteItemImage(itemId);
        assertFalse(Files.exists(expectedFilePath));
    }

    @Test
    @SneakyThrows
    public void validateImageFileTest(){
        MultipartFile image = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                (byte[]) null
        );
        MultipartFile image1 = new MockMultipartFile(
                "image",
                "test-image",
                "image/jpeg",
                (byte[]) null
        );
        MultipartFile image2 = new MockMultipartFile(
                    "image",
                null,
                null,
                (byte[]) null
        );
        assertThrows(BusinessException.class, () -> fileStorageService.validateImageFile(image));
        assertThrows(BusinessException.class, () -> fileStorageService.validateImageFile(image1));
        assertThrows(BusinessException.class, () -> fileStorageService.validateImageFile(image2));
    }

    @Test
    @SneakyThrows
    public void updateImageTest(){
        MultipartFile image = createMockImage();

        MultipartFile newImage = createMockImage();
        byte[] imageContent = newImage.getBytes();
        String oldImagePath = fileStorageService.saveItemImage(image, itemId);
        String newImagePath = fileStorageService.saveItemImage(newImage, itemId);
        Path oldPath = tempDir.resolve("upload/").resolve(oldImagePath);
        Path newPath = tempDir.resolve("upload/").resolve(newImagePath);
        assertTrue(Files.exists(newPath));
        assertFalse(Files.exists(oldPath));
        assertArrayEquals(imageContent, Files.readAllBytes(newPath));
    }

    private MultipartFile createMockImage(){
        byte[] imageContent = UUID.randomUUID().toString().getBytes();
        return new MockMultipartFile(
                "image",
                UUID.randomUUID()+".jpg",
                "image/jpeg",
                imageContent
        );
    }

    @Test
    @SneakyThrows
    public void saveAndLoadImageTest(){
        MultipartFile file = createMockImage();
        String url = fileStorageService.saveItemImage(file, itemId);
        Resource img = fileStorageService.getImageByPath(url);
        byte[] loadedBytes;
        try (InputStream is = img.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] tmp = new byte[1024];
            int n;
            while ((n = is.read(tmp)) != -1) {
                buffer.write(tmp, 0, n);
            }
            loadedBytes = buffer.toByteArray();
        }
        assertArrayEquals(file.getBytes(), loadedBytes);
    }
}
