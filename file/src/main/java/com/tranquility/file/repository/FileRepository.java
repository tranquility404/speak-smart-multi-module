package com.tranquility.file.repository;

import com.tranquility.file.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<StoredFile, UUID> {
}
