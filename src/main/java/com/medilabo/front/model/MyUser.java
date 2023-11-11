package com.medilabo.front.model;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MyUser {

    private String username;

    private String authHeader;

    public MyUser (UserDetails user) {
        this.username = user.getUsername();

        String stringToEncode = user.getUsername()+":"+user.getPassword();
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(stringToEncode.getBytes());
    }

    public MyUser (String username, String password) {
        this.username = username;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString((username+":"+password).getBytes());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }
}
