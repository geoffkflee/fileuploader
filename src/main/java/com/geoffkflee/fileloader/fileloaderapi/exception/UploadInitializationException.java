package com.geoffkflee.fileloader.fileloaderapi.exception;

import lombok.RequiredArgsConstructor;

public class UploadInitializationException extends RuntimeException {

    public UploadInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
