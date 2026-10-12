package com.cfs.Drive_Service.service;

import com.cfs.Drive_Service.model.DriveItem;
import com.cfs.Drive_Service.repo.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public DriveItem create(String name, UUID parentId,DriveItem.Kind kind,long size, String mediaType)
    {
        if(name==null || name.isBlank() || name.length() > 255 || name.equals(".") || name.equals("..")
            || name.contains("/")||name.contains("\\") || name.chars().anyMatch(Character::isISOControl)
        )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Use proper naming");
        }
        if (parentId!=null)
        {
            DriveItem parent=itemRepository.lockById(parentId).orElseThrow(ItemService::missing);
            if(parent.getKind()!=DriveItem.Kind.FOLDER || parent.getStatus()!=DriveItem.Status.READY)
            {
                throw  new ResponseStatusException(HttpStatus.CONFLICT,"Parent must be an available folder");
            }
        }

        return itemRepository.saveAndFlush(new DriveItem(name,parentId,kind,size,mediaType));
    }

    @Transactional
    public List<DriveItem> list(UUID parentId)
    {
        if(parentId!=null)
        {
            DriveItem parent=get(parentId);
            if(parent.getKind()!=DriveItem.Kind.FOLDER)
            {
                throw  new ResponseStatusException(HttpStatus.CONFLICT,"Parent must be a folder");
            }
        }

        return itemRepository.findByParentIdAndStatusOrderByNameAsc(parentId,DriveItem.Status.READY);
    }

    @Transactional
    public DriveItem get(UUID id)
    {
        DriveItem item = itemRepository.findById(id).orElseThrow(ItemService::missing);
        if(item.getStatus()!=DriveItem.Status.READY)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"item is not ready");
        }
        return item;
    }

    @Transactional
    public DriveItem ready(UUID id)
    {
        DriveItem item = itemRepository.lockById(id).orElseThrow(ItemService::missing);
        if(item.getStatus()!=DriveItem.Status.PENDING)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"upload is no longer pending");
        }
         item.setStatus(DriveItem.Status.READY);
        return item;
    }

    @Transactional
    public DriveItem delete(UUID id)
    {
        DriveItem item = itemRepository.lockById(id).orElseThrow(ItemService::missing);
        if(item.getStatus()==DriveItem.Status.PENDING)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"upload Still pending");
        }
        if(item.getKind()==DriveItem.Kind.FOLDER && itemRepository.existsByParentId(id))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Folder is not empty");
        }

        item.setStatus(DriveItem.Status.DELETING);
        return item;
    }

    @Transactional
    public void remove(UUID uuid)
    {
        itemRepository.deleteById(uuid);
    }


    private static ResponseStatusException missing()
    {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,"Item not found");
    }
}
