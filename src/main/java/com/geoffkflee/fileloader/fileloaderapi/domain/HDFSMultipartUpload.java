package com.geoffkflee.fileloader.fileloaderapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.hadoop.fs.UploadHandle;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class HDFSMultipartUpload extends MultipartUpload<HDFSMultipartSegment> {

    UploadHandle uploadHandle;

    @OneToMany(mappedBy = "hdfsMultipartUpload", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<HDFSMultipartSegment> segments = new ArrayList<>();

}
