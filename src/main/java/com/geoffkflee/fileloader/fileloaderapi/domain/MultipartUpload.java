package com.geoffkflee.fileloader.fileloaderapi.domain;

import com.geoffkflee.fileloader.fileloaderapi.enums.UploadStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public abstract class MultipartUpload<T extends MultipartSegment> extends GenericEntity {

    UploadStatus status;

    String destination;

    String fileName;

    Long fileSize;

    Long chunkSize;

    List<T> segments;
}
