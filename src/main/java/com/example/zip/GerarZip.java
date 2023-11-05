package com.example.zip;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController

public class GerarZip {

    @GetMapping("/download")
    
    public ResponseEntity<StreamingResponseBody> download(@RequestParam(value = "name", defaultValue = "World") String name) {
        String directory = "D:\\pra_zipar\\";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=imagens.zip");

        ResponseEntity<StreamingResponseBody> responseEntity = new ResponseEntity<>(outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                File folder = new File(directory);
                File[] files = folder.listFiles();
                byte[] buffer = new byte[1024];

                for (File file : files) {
                    if (file.isFile()) {
                        FileInputStream fis = new FileInputStream(file);
                        zipOut.putNextEntry(new ZipEntry(file.getName()));
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zipOut.write(buffer, 0, length);
                        }
                        zipOut.closeEntry();
                        fis.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, headers, HttpStatus.OK);

        return responseEntity;
    }
}
