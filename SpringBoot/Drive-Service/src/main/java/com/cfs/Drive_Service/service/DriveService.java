package com.cfs.Drive_Service.service;

import com.cfs.Drive_Service.client.StorageClient;
import com.cfs.Drive_Service.dto.FileDownload;
import com.cfs.Drive_Service.model.DriveItem;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class DriveService {

    private static final Logger log= LoggerFactory.getLogger(DriveService.class);
    private final ItemService itemService;
    private final StorageClient storageClient;


    public DriveService(ItemService itemService, StorageClient storageClient) {
        this.itemService = itemService;
        this.storageClient = storageClient;
    }

    public DriveItem createFolder(String name, UUID parentId)
    {
        return itemService.create(name,parentId,DriveItem.Kind.FOLDER,0,null);
    }

    public List<DriveItem> list(UUID parentId)
    {
        return itemService.list(parentId);
    }

    public DriveItem upload(MultipartFile file,UUID parentId)
    {
        DriveItem item=itemService.create(file.getOriginalFilename(),parentId,DriveItem.Kind.FILE, file.getSize(), file.getContentType());
        try {
            storageClient.put(item.getId(),file);
            return itemService.ready(item.getId());
        }
        catch (RuntimeException e)
        {
            try {
                storageClient.delete(item.getId());
                itemService.remove(item.getId());
            }
            catch (RuntimeException cleanUpFailure)
            {
                log.warn("Pending upload {} needs cleanup",item.getId(),cleanUpFailure);
            }
            throw e;
        }
    }

    public FileDownload download(UUID id)
    {
        DriveItem item=itemService.get(id);
        if(item.getKind()!=DriveItem.Kind.FILE)
        {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"A folder has no content");
        }
        Response response = storageClient.get(id);
        return new FileDownload(item.getName(), item.getSize(), response);
    }

    public void delete(UUID id)
    {
        DriveItem item=itemService.delete(id);
        itemService.remove(id);
    }
}
