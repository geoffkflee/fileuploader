package com.geoffkflee.fileloader.fileloaderapi.service;

import com.geoffkflee.fileloader.fileloaderapi.domain.MultipartUpload;

import java.io.InputStream;
import java.util.UUID;

public interface MultipartUploadService<T extends MultipartUpload> {

    T initialize(String destination, String fileName, Long fileSize, Long chunkSize);

    T uploadPart(UUID multipartId, Integer index, InputStream inputStream);

    T complete(UUID multipartId);

}
