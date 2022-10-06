package com.geoffkflee.fileloader.fileloaderapi.factory;

import com.geoffkflee.fileloader.fileloaderapi.domain.MultipartSegment;
import com.geoffkflee.fileloader.fileloaderapi.domain.MultipartUpload;
import com.geoffkflee.fileloader.fileloaderapi.dtos.MultipartUploadResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MultipartUploadApiFactory {

    public MultipartUploadResponse toResponse(MultipartUpload<?> multipartUpload) {

        return MultipartUploadResponse.builder()
                .id(multipartUpload.getId())
                .createdAt(multipartUpload.getCreatedAt())
                .lastModifiedAt(multipartUpload.getLastModifiedAt())
                .destination(multipartUpload.getDestination())
                .segments(multipartUpload.getSegments().stream().map(MultipartSegment::getStatus).collect(Collectors.toList()))
                .status(multipartUpload.getStatus())
                .fileName(multipartUpload.getFileName())
                .build();

    }

}
