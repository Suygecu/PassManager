package com.suygecu.testpepsa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordManager {

    private List<PasswordEntry> entries = new ArrayList<>();
    private String localFilePath;

    public PasswordManager(String localFilePath){
        this.localFilePath = localFilePath;
    }

    public  void addEntry(String site, String username, String password){
        String encryptedPassword = EncryptionUtils.encrypt(password);
        PasswordEntry entry = new PasswordEntry(site, username, encryptedPassword);
        entries.add(entry);
    }

    public void removeEntry (String site){
        entries.removeIf(entry -> entry.getSite().equals(site));
    }

    public void updateEntry (String site, String username, String password){
        for(PasswordEntry entry : entries){
            if(entry.getSite().equals(site)){
                entry.setUsername(username);
                entry.setEncryptedPassword(EncryptionUtils.encrypt(password));
                break;
            }
        }

    }

    public List<PasswordEntry>viewEntries(){
        return  new ArrayList<>(entries);
    }
    public void saveToLocal() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(localFilePath))) {
            oos.writeObject(entries);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadFromLocal() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(localFilePath))) {
            entries = (List<PasswordEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}


