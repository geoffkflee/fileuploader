package com.geoffkflee.fileloader.fileloaderapi.controller;

import com.geoffkflee.fileloader.fileloaderapi.domain.HDFSMultipartUpload;
import com.geoffkflee.fileloader.fileloaderapi.dtos.MultipartUploadResponse;
import com.geoffkflee.fileloader.fileloaderapi.dtos.MultipartUploadRequest;
import com.geoffkflee.fileloader.fileloaderapi.factory.MultipartUploadResponseFactory;
import com.geoffkflee.fileloader.fileloaderapi.service.MultipartUploadService;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/uploads/hdfs")
@RequiredArgsConstructor
@Slf4j
public class HDFSUploadsController {

    private final MultipartUploadService<HDFSMultipartUpload> hdfsMultipartUploadService;
    private final MultipartUploadResponseFactory multipartUploadResponseFactory;

    @PostMapping("/")
    public ResponseEntity<MultipartUploadResponse> initiateMultipartUpload(
        @RequestBody MultipartUploadRequest multipartUploadRequest
    ) {
        log.info(
            "Request received to initiate a new multipart upload at [{}] for the HDFS platform",
            multipartUploadRequest.getDestination()
        );

        HDFSMultipartUpload hdfsMultipartUpload = hdfsMultipartUploadService.initialize(
            multipartUploadRequest.getDestination(),
            multipartUploadRequest.getFileName(),
            multipartUploadRequest.getFileSize(),
            multipartUploadRequest.getChunkSize()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(multipartUploadResponseFactory.toResponse(hdfsMultipartUpload));
    }

    @PutMapping("/{multipartUploadId}")
    public CompletableFuture<ResponseEntity<MultipartUploadResponse>> uploadMultipartSegment(
        @PathVariable UUID multipartUploadId,
        @NotNull @RequestParam Integer segmentIndex,
        HttpServletRequest request
    ) {
        log.info(
            "Request received to upload multipart segment [#{}] for upload [{}].",
            segmentIndex,
            multipartUploadId
        );

        return CompletableFuture.supplyAsync(() -> {
            HDFSMultipartUpload hdfsMultipartUpload;
            try {
                hdfsMultipartUpload = hdfsMultipartUploadService.uploadPart(
                    multipartUploadId,
                    segmentIndex,
                    request.getInputStream()
                );
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(multipartUploadResponseFactory.toResponse(hdfsMultipartUpload));
        });
    }

    @PostMapping("/{multipartUploadId}/complete")
    public ResponseEntity<MultipartUploadResponse> completeMultipartUpload(
        @PathVariable UUID multipartUploadId
    ) {
        log.info("Request received to finalize multipart upload [{}]", multipartUploadId);

        HDFSMultipartUpload hdfsMultipartUpload = hdfsMultipartUploadService.complete(multipartUploadId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(multipartUploadResponseFactory.toResponse(hdfsMultipartUpload));
    }

    @GetMapping("/")
    public ResponseEntity<List<MultipartUploadResponse>> getAllMultipartUploads() {
        log.info("Request received to retrieve all multipart uploads");

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(hdfsMultipartUploadService.retrieveAll().stream().map(multipartUploadResponseFactory::toResponse).collect(Collectors.toList()));
    }

}
