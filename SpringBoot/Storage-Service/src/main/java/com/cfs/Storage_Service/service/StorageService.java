package com.cfs.Storage_Service.service;

import com.cfs.Storage_Service.repo.DataStore;
import org.apache.hc.core5.io.IOCallback;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    private final DataStore store;

    public StorageService(DataStore store) {
        this.store = store;
    }

    public void put(UUID id, MultipartFile file) throws IOException
    {
        try(var input = file.getInputStream())
        {
            store.put(id,input);
        }
    }

    public Resource get(UUID id)
    {
        return store.get(id);
    }

    public void delete(UUID id) throws IOException
    {
        store.delete(id);
    }
}
