package com.cfs.Drive_Service.repo;

import com.cfs.Drive_Service.model.DriveItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface ItemRepository extends JpaRepository<DriveItem, UUID> {


    List<DriveItem> findByParentIdAndStatusOrderByNameAsc(UUID parentId,DriveItem.Status status);

    boolean existsByParentId(UUID parentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from DriveItem i where i.id=:id")
    java.util.Optional<DriveItem> lockById(@Param("id") UUID id);
}
