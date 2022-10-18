package com.geoffkflee.fileloader.fileloaderapi.repository;

import com.geoffkflee.fileloader.fileloaderapi.domain.HDFSMultipartSegment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HDFSMultipartSegmentRepository extends CrudRepository<HDFSMultipartSegment, UUID> {



}
