package com.cfs.Drive_Service.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class DriveItem {

    public enum Kind {FILE,FOLDER}

    public enum Status { PENDING, READY, DELETING}

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private UUID parentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kind kind;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private long size;
    private String mediaType;

    @Column(nullable = false)
    private Instant createdAt;

    @Version
    private Long version;

    protected DriveItem(){}

    public DriveItem(String name,UUID parentId,Kind kind,long size,String mediaType)
    {
        this.id=UUID.randomUUID();
        this.name=name;
        this.parentId=parentId;
        this.kind=kind;
        this.size=size;
        this.mediaType=mediaType;
        this.status = kind ==Kind.FOLDER ? Status.READY : Status.PENDING;
        this.createdAt=Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
