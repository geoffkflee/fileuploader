package com.geoffkflee.fileloader.fileloaderapi.controller;

import com.geoffkflee.fileloader.fileloaderapi.dtos.MultipartUploadRecordResponse;
import com.geoffkflee.fileloader.fileloaderapi.dtos.MultipartUploadRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploads")
public class UploadsController {


    @PostMapping("/")
    public ResponseEntity<MultipartUploadRecordResponse> initiateMultipartUpload(
            @RequestBody MultipartUploadRequest multipartUploadRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
