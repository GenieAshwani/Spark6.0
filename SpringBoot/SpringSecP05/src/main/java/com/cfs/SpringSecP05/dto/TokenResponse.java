package com.cfs.SpringSecP05.dto;

public record TokenResponse(String accessToken,String tokenType,
                        long expireInSeconds) {
}
