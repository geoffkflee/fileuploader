package com.geoffkflee.fileloader.fileloaderapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.hadoop.fs.PartHandle;

import javax.persistence.*;

@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class HDFSMultipartSegment extends MultipartSegment {

    PartHandle partHandle;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    HDFSMultipartUpload hdfsMultipartUpload;

}
