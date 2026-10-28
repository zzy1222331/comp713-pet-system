package com.comp713;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PetRepository {

    @Resource(lookup = "jdbc/MySQLAnimalPool")
    private DataSource dataSource;

    public List<String> findPetsBySpecies(String species) {
        List<String> pets = new ArrayList<>();
        String sql = "SELECT name FROM pets WHERE species = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, species);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pets.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    public boolean addPet(String name, String species) {
        String sql = "INSERT INTO pets (name, species, owner_id) VALUES (?, ?, 1)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, species);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
    
    public boolean applyForAdoption(long petId, long userId) {
        String sql = "INSERT INTO adoptions (pet_id, user_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, petId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAdoptionStatus(long adoptionId, String status) {
        String sql = "UPDATE adoptions SET status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, adoptionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}