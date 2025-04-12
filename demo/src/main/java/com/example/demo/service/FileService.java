package com.example.demo.service;

import com.example.demo.entity.FileEntity;
import com.example.demo.entity.FileInfoDTO;
import com.example.demo.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileEntity storeFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileRepository.findByName(fileName).isPresent())
            return null;
        if(file.getSize() > MAX_FILE_SIZE)
            throw new IllegalArgumentException("FIle is too large");
        FileEntity entity = new FileEntity();
        entity.setName(file.getOriginalFilename());
        entity.setType(file.getContentType());
        entity.setData(file.getBytes());
        return fileRepository.save(entity);
    }

    public List<FileInfoDTO> getAllFiles() {
        return fileRepository.findAll().stream()
                .map(file -> new FileInfoDTO(file.getId(), file.getName(), file.getType()))
                .toList();
    }

    public Optional<FileEntity> getFileById(Long id) {
        return fileRepository.findById(id);
    }

    public List<FileInfoDTO> getAllFilesSortedByName(String sort) {
        Comparator<FileInfoDTO> comparator = Comparator.comparing(FileInfoDTO::getName, String.CASE_INSENSITIVE_ORDER);
        if ("desc".equalsIgnoreCase(sort))
            comparator = comparator.reversed();
        return fileRepository.findAll().stream()
                .map(file -> new FileInfoDTO(file.getId(), file.getName(), file.getType()))
                .sorted(comparator)
                .toList();
    }

    public boolean deleteFileById(Long id) {
        if (fileRepository.existsById(id)) {
            fileRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
