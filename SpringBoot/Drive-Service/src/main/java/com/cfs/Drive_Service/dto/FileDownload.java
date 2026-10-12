package com.cfs.Drive_Service.dto;

import feign.Response;

import java.io.IOException;
import java.io.OutputStream;

public record FileDownload(String name, long size, Response response) implements AutoCloseable {

    public void writeTo(OutputStream outputStream) throws IOException
    {
        try(this; var input = response.body().asInputStream())
        {
            input.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        response.close();
    }
}
