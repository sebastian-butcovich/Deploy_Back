package com.example.tryJwt.demo.FileRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(@JsonProperty("access_token")
                            String accessToken, @JsonProperty("refresh_token") String refreshToken, @JsonProperty("username") String name) {

}
