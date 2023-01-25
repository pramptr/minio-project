package com.piinalpin.minio.http.exeption;

public class MinioException extends Exception {
    public MinioException(String message, Throwable cause) {
        super(message, cause);
    }
}