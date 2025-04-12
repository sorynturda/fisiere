package com.example.demo.repository;

import com.example.demo.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource(path="files")
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query("SELECT name FROM FileEntity where name = ?1")
    Optional<FileEntity> findByName(String filename);
}
