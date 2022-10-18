package com.geoffkflee.fileloader.fileloaderapi.domain;

import com.geoffkflee.fileloader.fileloaderapi.enums.UploadStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import java.util.List;

@MappedSuperclass
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class MultipartUpload<T extends MultipartSegment> extends GenericEntity {

    UploadStatus status = UploadStatus.PENDING;

    String destination;

    String fileName;

    Long fileSize;

    Long chunkSize;

    @ElementCollection
    List<T> segments;
}
