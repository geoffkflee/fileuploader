package com.geoffkflee.fileloader.fileloaderapi.domain;

import com.geoffkflee.fileloader.fileloaderapi.enums.SegmentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class MultipartSegment extends GenericEntity {

    SegmentStatus status = SegmentStatus.PENDING;

    Long chunkSize;

}
