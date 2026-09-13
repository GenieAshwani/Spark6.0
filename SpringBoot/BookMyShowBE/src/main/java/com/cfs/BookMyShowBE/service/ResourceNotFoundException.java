package com.cfs.BookMyShowBE.service;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String msg)
    {
        super(msg);
    }
}
