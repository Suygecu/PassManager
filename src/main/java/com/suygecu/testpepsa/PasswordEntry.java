package com.suygecu.testpepsa;

import java.io.Serializable;

public class PasswordEntry implements Serializable {
    private String site;
    private String username;
    private String encryptedPassword;

    public PasswordEntry (String site, String username, String encryptedPassword){
        this.site = site;
        this.username = username;
        this.encryptedPassword = encryptedPassword;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
