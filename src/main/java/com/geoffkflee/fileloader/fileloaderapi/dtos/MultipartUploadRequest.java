package com.geoffkflee.fileloader.fileloaderapi.dtos;

import com.geoffkflee.fileloader.fileloaderapi.enums.StoragePlatform;
import lombok.Data;

@Data
public class MultipartUploadRequest {

    String destination;

    String fileName;

    Long chunkSize;

    Long fileSize;

    StoragePlatform storagePlatform;

}
