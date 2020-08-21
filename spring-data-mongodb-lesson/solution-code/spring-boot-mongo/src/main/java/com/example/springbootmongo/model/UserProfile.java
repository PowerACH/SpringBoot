package com.example.springbootmongo.model;

import org.springframework.data.mongodb.core.index.Indexed;

public class UserProfile {

    @Indexed(unique = true)
    private String email;

    private String mobile;

    private String address;

    public UserProfile() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
