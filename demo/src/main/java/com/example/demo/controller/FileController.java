package com.example.demo.controller;

import com.example.demo.entity.FileEntity;
import com.example.demo.entity.FileInfoDTO;
import com.example.demo.service.FileService;
import com.example.demo.service.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;
    private final MessagePublisher messagePublisher;

    @Autowired
    public FileController(FileService fileService, MessagePublisher messagePublisher) {
        this.fileService = fileService;
        this.messagePublisher = messagePublisher;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        var saved = fileService.storeFile(file);
        if (saved == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File with this name already exists!");
        FileInfoDTO body = new FileInfoDTO(saved.getId(), saved.getName(), saved.getType());
        messagePublisher.publish("your-channel", body);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<FileInfoDTO> uploaded = new ArrayList<>();
        for (MultipartFile file : files) {
            var saved = fileService.storeFile(file);
            uploaded.add(new FileInfoDTO(saved.getId(), saved.getName(), saved.getType()));
        }
        return ResponseEntity.ok(uploaded);
    }

    @GetMapping()
    public ResponseEntity<List<FileInfoDTO>> listAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @GetMapping(params = {"sort"})
    ResponseEntity<List<FileInfoDTO>> listAllFiles(@RequestParam(defaultValue = "asc") String sort) {
        return ResponseEntity.ok(fileService.getAllFilesSortedByName(sort));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        Optional<FileEntity> file = fileService.getFileById(id);

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        boolean deleted = fileService.deleteFileById(id);
        if (deleted) {
            return ResponseEntity.ok("The file with " + id + " was deleted.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
