package by.magofrays.shop.unit;

import by.magofrays.shop.exception.BusinessException;
import by.magofrays.shop.service.FileStorageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        String savedPath = fileStorageService.uploadFile(image, "images/item", itemId, null);
        assertNotNull(savedPath);
        assertTrue(savedPath.startsWith("images/item/image-"));
        assertTrue(savedPath.endsWith(".jpg"));
        Path expectedFilePath = tempDir.resolve("upload/images/item")
                .resolve(savedPath.substring("images/item/".length()));
        assertTrue(Files.exists(expectedFilePath));
        assertArrayEquals(imageContent, Files.readAllBytes(expectedFilePath));
        fileStorageService.deleteFileForEntity(savedPath, itemId);
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
        String oldImagePath = fileStorageService.uploadFile(image, "images/item", itemId, null);
        String newImagePath = fileStorageService.uploadFile(newImage, "images/item", itemId, oldImagePath);
        Path oldPath = tempDir.resolve("upload/").resolve(oldImagePath);
        Path newPath = tempDir.resolve("upload/").resolve(newImagePath);
        assertTrue(Files.exists(newPath));
        assertFalse(Files.exists(oldPath));
        assertArrayEquals(imageContent, Files.readAllBytes(newPath));
    }

    private MultipartFile createMockImage(){
        byte[] imageContent = UUID.randomUUID().toString().getBytes();
        MultipartFile file = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                imageContent
        );
        assertDoesNotThrow(() -> fileStorageService.validateImageFile(file));
        return file;
    }

    @Test
    @SneakyThrows
    public void uploadAndLoadImageTest(){
        MultipartFile file = createMockImage();
        String url = fileStorageService.uploadFile(file, "images/item", itemId, null);
        Resource img = fileStorageService.getFileByPath(url);
        assertNotNull(img);
        assertTrue(img.exists());
        resourceEqualsTest(img, file.getBytes());
    }

    @SneakyThrows
    public void resourceEqualsTest(Resource file, byte[] bytes){
        byte[] loadedBytes;
        try (InputStream is = file.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] tmp = new byte[1024];
            int n;
            while ((n = is.read(tmp)) != -1) {
                buffer.write(tmp, 0, n);
            }
            loadedBytes = buffer.toByteArray();
        }
        assertArrayEquals(bytes, loadedBytes);
    }

    @Test
    @SneakyThrows
    public void updateAndLoadFileTest(){
        Resource file = new ByteArrayResource(
                "db/test".getBytes(StandardCharsets.UTF_8)
        ){
            @Override
            public String getFilename(){
                return "test.txt";
            }
        };
        String filename = file.getFilename();
        UUID entityId = UUID.randomUUID();
        String url = assertDoesNotThrow(() -> fileStorageService.saveFile(file, "db/test/test", entityId, null));
        Path saveDir = tempDir.resolve("save/");
        assertTrue(Files.exists(saveDir.resolve(url)));
        Resource savedFile = fileStorageService.getFileByPath(url);
        assertNotNull(savedFile);
        assertTrue(savedFile.exists());
        assertNotNull(savedFile.getFilename());
        resourceEqualsTest(savedFile, "db/test".getBytes(StandardCharsets.UTF_8));

        Resource newFile = new ByteArrayResource(
                "new file".getBytes(StandardCharsets.UTF_8)
        ){
            @Override
            public String getFilename(){
                return "test2.txt";
            }
        };
        String url2 = assertDoesNotThrow(() -> fileStorageService.saveFile(newFile, "db/test/test", entityId, url));
        assertFalse(Files.exists(saveDir.resolve(url)));
        assertTrue(Files.exists(saveDir.resolve(url2)));
        Resource savedFile2 = fileStorageService.getFileByPath(url2);
        assertNotNull(savedFile2);
        assertTrue(savedFile2.exists());
        assertNotNull(savedFile2.getFilename());
        resourceEqualsTest(savedFile2, "new file".getBytes(StandardCharsets.UTF_8));
    }
}
