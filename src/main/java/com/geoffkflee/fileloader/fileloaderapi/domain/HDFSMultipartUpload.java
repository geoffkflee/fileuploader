package com.geoffkflee.fileloader.fileloaderapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.apache.hadoop.fs.UploadHandle;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class HDFSMultipartUpload extends MultipartUpload {

    UploadHandle uploadHandle;

    List<HDFSMultipartSegment> segments;

}