package com.geoffkflee.fileloader.fileloaderapi.domain;

import com.geoffkflee.fileloader.fileloaderapi.enums.UploadStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public abstract class MultipartUpload extends GenericEntity {

    UploadStatus status;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    List<? extends MultipartSegment> segments;

    String destination;

    String fileName;

    Long fileSize;

    Long chunkSize;

}
