package com.piinalpin.minio.service;

import com.piinalpin.minio.http.dto.FileDto;
import io.minio.*;
import io.minio.messages.Item;
import java.io.FileOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import com.piinalpin.minio.http.exeption.MinioException;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.File;

@Slf4j
@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    // MinioClient minioClient = MinioClient.builder()
    //                         .endpoint("http://127.0.0.1:9000/")
    //                         .credentials("HDsSDUgd54Alvcn0", "lYfQbgnSJDBIlp89F0mYIEIlBCdD0aW5")
    //                         .build();

    public List<FileDto> getListObjects() {
        List<FileDto> objects = new ArrayList<>();
        try {
            Iterable<Result<Item>> result = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build());
            for (Result<Item> item : result) {
                objects.add(FileDto.builder()
                        .filename(item.get().objectName())
                        .size(item.get().size())
                        .url(getPreSignedUrl(item.get().objectName()))
                        .build());
            }
            return objects;
        } catch (Exception e) {
            log.error("Happened error when get list objects from minio: ", e);
        }

        return objects;
    }

    //Download
    public InputStream getObject(String filename) {
        InputStream stream;
        try {
            stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build());
        } catch (Exception e) {
            log.error("Happened error when get list objects from minio: ", e);
            return null;
        }

        return stream;
    }

    public void getAndSave(Path source, String fileName) throws MinioException {
        try {
            DownloadObjectArgs args = DownloadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(source.toString())
                    .filename(fileName)
                    .build();
            minioClient.downloadObject(args);
        } catch (Exception e) {
            throw new MinioException("Error while fetching files in Minio", e);
        }
    }
    
    //Upload
    public FileDto uploadFile(FileDto request) {
        log.debug("loggg minioClient []", minioClient);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object("ktp/2023/test/"+request.getFile().getOriginalFilename())
                    .stream(request.getFile().getInputStream(), request.getFile().getSize(), -1)
                    .build());
        } catch (Exception e) {
            log.error("Happened error when upload file: ", e);
        }
        return FileDto.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .size(request.getFile().getSize())
                .url(getPreSignedUrl(request.getFile().getOriginalFilename()))
                .filename(request.getFile().getOriginalFilename())
                .build();
    }

    private String getPreSignedUrl(String filename) {
        return "http://localhost:8080/file/".concat(filename);
    }

    public InputStream get(Path path) throws MinioException {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path.toString())
                    .build();
            return minioClient.getObject(args);
        } catch (Exception e) {
            throw new MinioException("Error while fetching files in Minio", e);
        }
    }

}
