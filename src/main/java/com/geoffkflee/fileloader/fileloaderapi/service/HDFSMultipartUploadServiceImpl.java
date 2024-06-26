package com.geoffkflee.fileloader.fileloaderapi.service;

import com.geoffkflee.fileloader.fileloaderapi.domain.HDFSMultipartSegment;
import com.geoffkflee.fileloader.fileloaderapi.domain.HDFSMultipartUpload;
import com.geoffkflee.fileloader.fileloaderapi.enums.SegmentStatus;
import com.geoffkflee.fileloader.fileloaderapi.enums.UploadStatus;
import com.geoffkflee.fileloader.fileloaderapi.exception.UploadFinalizationException;
import com.geoffkflee.fileloader.fileloaderapi.exception.UploadInitializationException;
import com.geoffkflee.fileloader.fileloaderapi.exception.UploadSegmentException;
import com.geoffkflee.fileloader.fileloaderapi.repository.HDFSMultipartSegmentRepository;
import com.geoffkflee.fileloader.fileloaderapi.repository.HDFSMultipartUploadRepository;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class HDFSMultipartUploadServiceImpl implements MultipartUploadService<HDFSMultipartUpload> {

    private final FileSystem hdfsFileSystem;
    private final HDFSMultipartUploadRepository hdfsMultipartUploadRepository;
    private final HDFSMultipartSegmentRepository hdfsMultipartSegmentRepository;

    @Override
    public HDFSMultipartUpload initialize(String destination, String fileName, Long fileSize, Long chunkSize) {
        log.debug("Initializing a new HDFS multipart upload to [{}]", destination);

        Path destinationPath = new Path(destination);
        UploadHandle uploadHandle;
        try {
            hdfsFileSystem.mkdirs(destinationPath);
            MultipartUploader multipartUploader = hdfsFileSystem.createMultipartUploader(destinationPath).build();
            uploadHandle = multipartUploader.startUpload(
                new Path(hdfsFileSystem.getUri().toString().concat(destination).concat(File.separator).concat(fileName))
            ).get();
        } catch (IOException | ExecutionException | InterruptedException e) {
            log.error("Could not initiate an HDFS multipart upload to [{}]", destination);
            throw new UploadInitializationException(e.getLocalizedMessage(), e.getCause());
        }

        // Create HDFSMultipartUpload record instance to refer back to.
        HDFSMultipartUpload hdfsMultipartUpload = new HDFSMultipartUpload().toBuilder()
            .uploadHandle(uploadHandle)
            .destination(destination)
            .fileName(fileName)
            .fileSize(fileSize)
            .chunkSize(chunkSize)
            .build();

        // Create all the multipart segments based on file size / chunk size rounded up.
        int numberOfChunks = (int) Math.ceil(fileSize / chunkSize);
        List<HDFSMultipartSegment> segments = IntStream
            .rangeClosed(0, numberOfChunks)
            .mapToObj(
                index -> new HDFSMultipartSegment().toBuilder()
                    .chunkSize(index == numberOfChunks - 1 ? fileSize % chunkSize : chunkSize)
                    .hdfsMultipartUpload(hdfsMultipartUpload)
                    .build()
            )
            .collect(Collectors.toList());

        hdfsMultipartUpload.setSegments(segments);

        return hdfsMultipartUploadRepository.save(hdfsMultipartUpload);
    }

    @Override
    public HDFSMultipartUpload uploadPart(UUID multipartId, Integer segmentIndex, InputStream inputStream) {
        log.debug("Streaming HDFS upload segment [#{}] for multipart upload [{}]", segmentIndex, multipartId);

        HDFSMultipartUpload hdfsMultipartUpload = hdfsMultipartUploadRepository.findById(multipartId).orElseThrow();
        HDFSMultipartSegment targetSegment = hdfsMultipartUpload.getSegments().get(segmentIndex);
        Path destinationPath = new Path(hdfsMultipartUpload.getDestination());
        try {
            MultipartUploader multipartUploader = hdfsFileSystem.createMultipartUploader(destinationPath).build();
            targetSegment.setPartHandle(multipartUploader.putPart(
                hdfsMultipartUpload.getUploadHandle(),
                segmentIndex,
                new Path(hdfsMultipartUpload.getDestination()),
                inputStream,
                targetSegment.getChunkSize()
            ).get());
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error("Could not upload part [#{}] for HDFS multipart upload [{}]", segmentIndex, multipartId);
            throw new UploadSegmentException(e.getLocalizedMessage(), e.getCause());
        }

        targetSegment.setStatus(SegmentStatus.COMPLETED);
        return hdfsMultipartUploadRepository.save(hdfsMultipartUpload);
    }

    @Override
    public HDFSMultipartUpload complete(UUID multipartId) {
        log.debug("Notifying to HDFS that multipart upload [{}] is complete", multipartId);

        HDFSMultipartUpload hdfsMultipartUpload = hdfsMultipartUploadRepository.findById(multipartId).orElseThrow();
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        hdfsMultipartUpload.getSegments().forEach(segment -> partHandles.put(partHandles.size(), segment.getPartHandle()));
        Path destinationPath = new Path(hdfsMultipartUpload.getDestination());
        try {
            MultipartUploader multipartUploader = hdfsFileSystem.createMultipartUploader(destinationPath).build();
            multipartUploader.complete(hdfsMultipartUpload.getUploadHandle(), destinationPath, partHandles).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error("Could not notify HDFS of the completed multipart upload [{}]", multipartId);
            throw new UploadFinalizationException(e.getLocalizedMessage(), e.getCause());
        }

        hdfsMultipartUpload.setStatus(UploadStatus.COMPLETED);
        return hdfsMultipartUploadRepository.save(hdfsMultipartUpload);
    }

    @Override
    public List<HDFSMultipartUpload> retrieveAll() {
        log.debug("Retrieving all HDFS Multipart Uploads");

        List<HDFSMultipartUpload> hdfsMultipartUploads = new ArrayList<>();
        hdfsMultipartUploadRepository.findAll().forEach(hdfsMultipartUploads::add);

        return hdfsMultipartUploads;
    }

}
