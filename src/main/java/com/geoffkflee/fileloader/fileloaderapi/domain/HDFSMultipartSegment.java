package com.geoffkflee.fileloader.fileloaderapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.hadoop.fs.PartHandle;

import javax.persistence.Entity;

@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class HDFSMultipartSegment extends MultipartSegment {

    PartHandle partHandle;

}
