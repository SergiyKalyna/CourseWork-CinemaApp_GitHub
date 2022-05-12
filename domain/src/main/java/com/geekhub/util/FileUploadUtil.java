package com.geekhub.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class FileUploadUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);

    public void saveFile(String fileName,
                         MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get("web-app/src/main/resources/static/images/");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info(String.format("File with name [%s] was saved", fileName));
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public String getNewImageName(LocalDate release, String movieTitle, MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        logger.info("Was set a new name to upload file");

        return release.toString() + "_" + movieTitle +
                fileName.substring(fileName.length() - 4);
    }
}
