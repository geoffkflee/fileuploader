package com.geoffkflee.fileloader.fileloaderapi.service;

import com.geoffkflee.fileloader.fileloaderapi.domain.HDFSMultipartSegment;
import com.geoffkflee.fileloader.fileloaderapi.domain.HDFSMultipartUpload;
import com.geoffkflee.fileloader.fileloaderapi.enums.SegmentStatus;
import com.geoffkflee.fileloader.fileloaderapi.enums.UploadStatus;
import com.geoffkflee.fileloader.fileloaderapi.exception.UploadFinalizationException;
import com.geoffkflee.fileloader.fileloaderapi.exception.UploadInitializationException;
import com.geoffkflee.fileloader.fileloaderapi.exception.UploadSegmentException;
import com.geoffkflee.fileloader.fileloaderapi.repository.HDFSMultipartUploadRepository;
import lombok.RequiredArgsConstructor;
import org.apache.hadoop.fs.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class HDFSMultipartUploadServiceImpl implements MultipartUploadService<HDFSMultipartUpload> {

    private final static Logger LOG = LogManager.getLogger(HDFSMultipartUploadServiceImpl.class);

    private final FileSystem hdfsFileSystem;
    private final HDFSMultipartUploadRepository hdfsMultipartUploadRepository;

    @Override
    public HDFSMultipartUpload initialize(String destination, String fileName, Long fileSize, Long chunkSize) {
        LOG.debug("Initializing a new HDFS multipart upload to [{}]", destination);

        Path destinationPath = new Path(destination);
        UploadHandle uploadHandle;
        try {
            MultipartUploader multipartUploader = hdfsFileSystem.createMultipartUploader(destinationPath).build();
            uploadHandle = multipartUploader.startUpload(destinationPath).get();
        } catch (IOException | ExecutionException | InterruptedException e) {
            LOG.error("Could not initiate an HDFS multipart upload to [{}]", destination);
            throw new UploadInitializationException(e.getLocalizedMessage(), e.getCause());
        }

        // Create all the multipart segments based on file size / chunk size rounded up.
        int numberOfChunks = (int) Math.ceil(fileSize / chunkSize);
        List<HDFSMultipartSegment> segments = IntStream
            .range(0, numberOfChunks)
            .mapToObj(
                index -> HDFSMultipartSegment.builder()
                    .chunkSize(index == numberOfChunks - 1 ? fileSize % chunkSize : chunkSize)
                    .build()
            )
            .collect(Collectors.toList());

        return hdfsMultipartUploadRepository.save(
            HDFSMultipartUpload.builder()
                .uploadHandle(uploadHandle)
                .destination(destination)
                .fileName(fileName)
                .fileSize(fileSize)
                .chunkSize(chunkSize)
                .segments(segments)
                .build()
        );
    }

    @Override
    public HDFSMultipartUpload uploadPart(UUID multipartId, Integer index, InputStream inputStream) {
        LOG.debug("Streaming HDFS upload segment [#{}] for multipart upload [{}]", index, multipartId);

        HDFSMultipartUpload hdfsMultipartUpload = hdfsMultipartUploadRepository.findById(multipartId).orElseThrow();
        HDFSMultipartSegment targetSegment = hdfsMultipartUpload.getSegments().get(index);
        Path destinationPath = new Path(hdfsMultipartUpload.getDestination());
        try {
            MultipartUploader multipartUploader = hdfsFileSystem.createMultipartUploader(destinationPath).build();
            targetSegment.setPartHandle(multipartUploader.putPart(
                hdfsMultipartUpload.getUploadHandle(),
                index,
                new Path(hdfsMultipartUpload.getDestination()),
                inputStream,
                targetSegment.getChunkSize()
            ).get());
            targetSegment.setStatus(SegmentStatus.COMPLETED);
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOG.error("Could not upload part [#{}] for HDFS multipart upload [{}]", index, multipartId);
            throw new UploadSegmentException(e.getLocalizedMessage(), e.getCause());
        }

        return hdfsMultipartUploadRepository.save(hdfsMultipartUpload);
    }

    @Override
    public HDFSMultipartUpload complete(UUID multipartId) {
        LOG.debug("Notifying to HDFS that multipart upload [{}] is complete", multipartId);

        HDFSMultipartUpload hdfsMultipartUpload = hdfsMultipartUploadRepository.findById(multipartId).orElseThrow();
        Map<Integer, PartHandle> partHandles = new HashMap<>();
        hdfsMultipartUpload.getSegments().forEach(segment -> partHandles.put(partHandles.size(), segment.getPartHandle()));
        Path destinationPath = new Path(hdfsMultipartUpload.getDestination());
        try {
            MultipartUploader multipartUploader = hdfsFileSystem.createMultipartUploader(destinationPath).build();
            multipartUploader.complete(hdfsMultipartUpload.getUploadHandle(), destinationPath, partHandles).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOG.error("Could not notify HDFS of the completed multipart upload [{}]", multipartId);
            throw new UploadFinalizationException(e.getLocalizedMessage(), e.getCause());
        }
        hdfsMultipartUpload.setStatus(UploadStatus.COMPLETED);

        return hdfsMultipartUploadRepository.save(hdfsMultipartUpload);
    }

}
