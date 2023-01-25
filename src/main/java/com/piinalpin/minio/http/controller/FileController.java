package com.piinalpin.minio.http.controller;

import com.google.common.net.HttpHeaders;
import com.piinalpin.minio.http.dto.FileDto;
import com.piinalpin.minio.http.exeption.MinioException;
import com.piinalpin.minio.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;

import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

@Slf4j
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Autowired
    private MinioService minioService;

    @GetMapping
    public ResponseEntity<Object> getFiles() {
        return ResponseEntity.ok(minioService.getListObjects());
    }

    // @GetMapping(value = "/ktp/2023")
    // public ResponseEntity<Object> getFile(HttpServletRequest request) throws IOException {
    //     String pattern = (String) request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
    //     String filename = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
    //     return ResponseEntity.ok()
    //             .contentType(MediaType.APPLICATION_OCTET_STREAM)
    //             .body(IOUtils.toByteArray(minioService.getObject(filename)));
    // }

    @PostMapping(value = "/upload")
    public ResponseEntity<Object> upload(@ModelAttribute FileDto request) {
        log.debug("loggg request []", request);
        return ResponseEntity.ok().body(minioService.uploadFile(request));
    }


    @GetMapping(value = "/**")
    public ResponseEntity<Object> getFile(HttpServletRequest request) throws IOException {
    String pattern = (String) request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
    String filename = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
            .header(HttpHeaders.CONTENT_TYPE, URLConnection.guessContentTypeFromName(filename))
            // .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(IOUtils.toByteArray(minioService.getObject(filename)));
}


    // @GetMapping("/ktp/2023/test/{object}")
    // public void getObject(@PathVariable("object") String object, HttpServletResponse response) throws MinioException, IOException {
    //     InputStream inputStream = minioService.get(Path.of(object));
    //     InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

    //     // Set the content type and attachment header.
    //     response.addHeader("Content-disposition", "attachment;filename=" + object);
    //     response.setContentType(URLConnection.guessContentTypeFromName(object));

    //     // Copy the stream to the response's output stream.
    //     IOUtils.copy(inputStream, response.getOutputStream());
    //     response.flushBuffer();
    // }
}

