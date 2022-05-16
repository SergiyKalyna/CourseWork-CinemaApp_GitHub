package com.geekhub.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUploadUtilTest {

    @Mock
    MultipartFile multipartFile;

    @InjectMocks
    FileUploadUtil fileUploadUtil;

    @Test
    void getNewImageName_check_result_method() {
        LocalDate date = LocalDate.now();
        String title = "title";

        String expectedName = date + "_" + title + ".jpg";

        when(multipartFile.getOriginalFilename()).thenReturn("1.jpg");
        String actualName = fileUploadUtil.getNewImageName(date, title, multipartFile);

        assertEquals(expectedName,actualName);
    }

    @Test
    void getNewImageName_check_result_method_length() {
        LocalDate date = LocalDate.now();
        String title = "title";

        String expectedName = date + "_" + title + ".jpg";

        when(multipartFile.getOriginalFilename()).thenReturn("1.jpg");
        String actualName = fileUploadUtil.getNewImageName(date, title, multipartFile);

        assertEquals(expectedName.length(),actualName.length());
    }

}
