package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO implements Dao<Users> {

    private final JDBC jdbc;


    public UserDAO(JDBC jdbc) {

        this.jdbc = jdbc;
    }

    private Connection conn() {
        return jdbc.getConexao();
    }

    @Override
    public Users get(int id) {
        Users user = null;
        try {
            PreparedStatement sql = conn().prepareStatement("SELECT * FROM Users WHERE id = ?");
            sql.setInt(1, id);
            ResultSet resultado = sql.executeQuery();
            if (resultado.next()) {
                user = new Users(
                        resultado.getLong("id"),
                        resultado.getString("name"),
                        resultado.getString("email"),
                        resultado.getString("password_user")
                );
            }
        } catch (SQLException e) {
            System.err.println("Query de select (get user) incorreta: " + e.getMessage());
        }
        return user;
    }

    @Override
    public ArrayList<Users> getAll() {
        ArrayList<Users> users = new ArrayList<>();
        try {
            PreparedStatement sql = conn().prepareStatement("SELECT * FROM Users");
            ResultSet resultado = sql.executeQuery();
            while (resultado.next()) {
                Users user = new Users(
                        resultado.getLong("id"),
                        resultado.getString("name"),
                        resultado.getString("email"),
                        resultado.getString("password_user")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Query de select (getAll users) incorreta: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void insert(Users user) {
        try {
            PreparedStatement sql = conn().prepareStatement(
                    "INSERT INTO Users (name, email, password_user) VALUES (?, ?, ?)");
            sql.setString(1, user.getName());
            sql.setString(2, user.getEmail());
            sql.setString(3, user.getPassword());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de insert (users) incorreta: " + e.getMessage());
        }
    }

    @Override
    public void update(Users user) {
        try {
            PreparedStatement sql = conn().prepareStatement(
                    "UPDATE Users SET name = ?, email = ?, password_user = ? WHERE id = ?");
            sql.setString(1, user.getName());
            sql.setString(2, user.getEmail());
            sql.setString(3, user.getPassword());
            sql.setLong(4, user.getId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de update (users) incorreta: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            PreparedStatement sql = conn().prepareStatement("DELETE FROM Users WHERE id = ?");
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de delete (users) incorreta: " + e.getMessage());
        }
    }

    public Users login(String email, String password) {
        Users user = null;
        try {
            PreparedStatement sql = conn().prepareStatement(
                    "SELECT * FROM Users WHERE email = ? AND password_user = ? LIMIT 1");
            sql.setString(1, email.trim());
            sql.setString(2, password.trim());
            ResultSet rs = sql.executeQuery();
            if (rs.next()) {
                user = new Users(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password_user")
                );
            }
        } catch (SQLException e) {
            System.err.println("Query de login incorreta: " + e.getMessage());
        }
        return user;
    }

    public Users getByEmail(String email) {
        Users user = null;
        try {
            PreparedStatement sql = conn().prepareStatement(
                    "SELECT * FROM users WHERE email = ?");
            sql.setString(1, email.trim());
            ResultSet rs = sql.executeQuery();
            if (rs.next()) {
                user = new Users(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password_user")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por e-mail: " + e.getMessage());
        }
        return user;
    }

    public void updatePasswordByEmail(String email, String newPassword) {
        try {
            PreparedStatement sql = conn().prepareStatement(
                    "UPDATE Users SET password_user = ? WHERE email = ?");
            sql.setString(1, newPassword);
            sql.setString(2, email);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de update (password) incorreta: " + e.getMessage());
        }
    }
}
