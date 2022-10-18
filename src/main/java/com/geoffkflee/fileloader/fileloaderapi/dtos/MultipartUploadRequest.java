package com.geoffkflee.fileloader.fileloaderapi.dtos;

import lombok.Data;

@Data
public class MultipartUploadRequest {

    String destination;

    String fileName;

    Long chunkSize;

    Long fileSize;

}
