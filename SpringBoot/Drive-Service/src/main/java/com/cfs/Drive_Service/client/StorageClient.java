package com.cfs.Drive_Service.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@FeignClient(name = "Storage-Service",url = "${drive.storage.url:}")
public interface StorageClient {

    @PutMapping(value = "/internal/objects/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void put(@PathVariable("id") UUID id, @RequestPart("file") MultipartFile file);

    @GetMapping("/internal/objects/{id}")
    Response get(@PathVariable("id") UUID id);


    @DeleteMapping("/internal/objects/{id}")
    void delete(@PathVariable("id") UUID id);
}
