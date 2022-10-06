package com.geoffkflee.fileloader.fileloaderapi.dtos;

import com.geoffkflee.fileloader.fileloaderapi.enums.SegmentStatus;
import com.geoffkflee.fileloader.fileloaderapi.enums.UploadStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MultipartUploadResponse {

    UUID id;

    Instant createdAt;

    Instant lastModifiedAt;

    List<SegmentStatus> segments;

    UploadStatus status;

    String fileName;

    String destination;

}
