package com.example.demo.controller;

import com.example.demo.service.UploadImageFile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/upload")
@RestController
@RequiredArgsConstructor
public class UploadFileController {

    private final UploadImageFile uploadImageFile;

    @PostMapping("/image")
    public String uploadImage(@RequestParam("file")MultipartFile file) throws IOException {
        return uploadImageFile.uploadImage(file);
    }
}
