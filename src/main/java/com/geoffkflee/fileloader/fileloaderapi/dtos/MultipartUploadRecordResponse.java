package com.geoffkflee.fileloader.fileloaderapi.dtos;

import com.geoffkflee.fileloader.fileloaderapi.enums.SegmentStatus;
import com.geoffkflee.fileloader.fileloaderapi.enums.UploadStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MultipartUploadRecordResponse {

    UUID id;

    LocalDateTime createdAt;

    LocalDateTime lastModifiedAt;

    List<SegmentStatus> segments;

    UploadStatus status;

    String fileName;

    String destination;

}
