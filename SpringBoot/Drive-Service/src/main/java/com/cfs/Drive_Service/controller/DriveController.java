package com.cfs.Drive_Service.controller;

import com.cfs.Drive_Service.dto.FileDownload;
import com.cfs.Drive_Service.dto.FolderRequest;
import com.cfs.Drive_Service.model.DriveItem;
import com.cfs.Drive_Service.service.DriveService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DriveController {

    private final DriveService drive;

    public DriveController(DriveService drive)
    {
        this.drive=drive;
    }

    @PostMapping("/folders")
    @ResponseStatus(HttpStatus.CREATED)
    public DriveItem folder(@Valid @RequestBody FolderRequest request)
    {
        return drive.createFolder(request.name(),request.parentId());
    }

    @GetMapping("/items")
    public List<DriveItem> list(@RequestParam(required = false) UUID parentId)
    {
        return drive.list(parentId);
    }


    @PostMapping(value = "/files",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DriveItem upload(@RequestPart("file") MultipartFile file,@RequestParam(required = false) UUID parentId)
    {
        return drive.upload(file,parentId);
    }

    public ResponseEntity<StreamingResponseBody> download(@PathVariable UUID id) throws Exception {
        FileDownload download = drive.download(id);
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(download.name(), StandardCharsets.UTF_8).build().toString())
                    .header("X-Content-Type-Options","nosniff")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(download.size()).body(download::writeTo);
        }
        catch (RuntimeException failure)
        {
            download.close();
            throw  failure;
        }
    }

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id)
    {
        drive.delete(id);
    }

}
