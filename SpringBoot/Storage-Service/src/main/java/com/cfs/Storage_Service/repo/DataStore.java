package com.cfs.Storage_Service.repo;

import com.cfs.Storage_Service.config.StorageProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.InputMismatchException;
import java.util.UUID;

@Repository
public class DataStore {

    private final Path root;

    public DataStore(StorageProperties properties) throws IOException {
        root=Path.of(properties.directory()).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public void put(UUID id, InputStream input) throws IOException
    {
        Path temporary = Files.createTempFile(root,"upload-","temp");
        try
        {
            Files.copy(input,temporary, StandardCopyOption.REPLACE_EXISTING);
            Files.move(temporary,root.resolve(id.toString()),StandardCopyOption.REPLACE_EXISTING);
        }
        finally {
            Files.deleteIfExists(temporary);
        }
    }


    public Resource get(UUID id)
    {
        Path path=root.resolve(id.toString());
        if(!Files.isRegularFile(path))
        {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"content not found");
        }
        return new FileSystemResource(path);
    }

    public void delete(UUID id) throws IOException
    {
        Files.deleteIfExists(root.resolve(id.toString()));
    }
}
