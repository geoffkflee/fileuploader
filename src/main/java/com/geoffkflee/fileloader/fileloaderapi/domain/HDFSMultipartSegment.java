package com.geoffkflee.fileloader.fileloaderapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.hadoop.fs.PartHandle;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class HDFSMultipartSegment extends MultipartSegment {

    PartHandle partHandle;

}
