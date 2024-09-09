package com.suygecu.testpepsa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/yourdatabase";
    private static final String USER = "yourusername";
    private static final String PASSWORD = "yourpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void addEntry(String site, String username, String password) {
        String query = "INSERT INTO passwords (site, username, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, site);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeEntry(String site) {
        String query = "DELETE FROM passwords WHERE site = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, site);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEntry(String site, String username, String password) {
        String query = "UPDATE passwords SET username = ?, password = ? WHERE site = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, site);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PasswordEntry> getEntries() {
        List<PasswordEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM passwords";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String site = rs.getString("site");
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                entries.add(new PasswordEntry(site, username, encryptedPassword));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }
}