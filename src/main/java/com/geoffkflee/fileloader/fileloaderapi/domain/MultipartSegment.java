package com.geoffkflee.fileloader.fileloaderapi.domain;

import com.geoffkflee.fileloader.fileloaderapi.enums.SegmentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public abstract class MultipartSegment extends GenericEntity {

    SegmentStatus status;

    Long chunkSize;

}
