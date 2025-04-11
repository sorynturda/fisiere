package com.example.demo;

import com.example.demo.entity.FileEntity;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.util.Optional;

@RestController
@SpringBootApplication
public class DemoApplication {


    @Autowired
    private FileRepository fileRepository;

    @PostMapping(path = "/upload2", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> handleFileUploadInDB(@RequestParam("file") MultipartFile file) {
        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(file.getOriginalFilename());
            fileEntity.setType(file.getContentType());
            fileEntity.setData(file.getBytes());

            fileRepository.save(fileEntity);

            return ResponseEntity.ok("The upload was saved in database successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        Optional<FileEntity> file = fileRepository.findById(id);

        if (file.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("FIle not found");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.get().getName() + "\"")
                .contentType(MediaType.parseMediaType(file.get().getType()))
                .body(file.get().getData());
    }


    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {

        String fileName = file.getOriginalFilename();
        try {
            file.transferTo(new File("C:\\Users\\Sorin Turda\\Desktop\\temp\\fisiere\\demo\\uploads\\" + fileName));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("File uploaded successfully");
    }


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
