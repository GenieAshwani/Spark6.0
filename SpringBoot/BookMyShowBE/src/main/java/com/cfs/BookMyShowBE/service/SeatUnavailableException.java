package com.cfs.BookMyShowBE.service;

public class SeatUnavailableException extends RuntimeException {

    public SeatUnavailableException(String msg)
    {
        super(msg);
    }
}
