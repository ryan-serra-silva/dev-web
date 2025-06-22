package com.mycompany.webapplication.model;

import com.mycompany.webapplication.entity.Users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO implements Dao<Users> {

    @Override
    public Users get(int id) {
        JDBC conexao = new JDBC();
        Users user = null;
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM Users WHERE id = ?");
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
        } finally {
            conexao.closeConexao();
        }
        return user;
    }

    @Override
    public ArrayList<Users> getAll() {
        ArrayList<Users> users = new ArrayList<>();
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM Users");
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
        } finally {
            conexao.closeConexao();
        }
        return users;
    }

    @Override
    public void insert(Users user) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "INSERT INTO Users (name, email, password_user) VALUES (?, ?, ?)");
            sql.setString(1, user.getName());
            sql.setString(2, user.getEmail());
            sql.setString(3, user.getPassword());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de insert (users) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void update(Users user) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "UPDATE Users SET name = ?, email = ?, password_user = ? WHERE id = ?");
            sql.setString(1, user.getName());
            sql.setString(2, user.getEmail());
            sql.setString(3, user.getPassword());
            sql.setLong(4, user.getId());
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de update (users) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }

    @Override
    public void delete(int id) {
        JDBC conexao = new JDBC();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("DELETE FROM Users WHERE id = ?");
            sql.setInt(1, id);
            sql.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Query de delete (users) incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
    }

    public Users login(String email, String password) {
        JDBC conexao = new JDBC();
        Users user = null;
        try {
            System.out.println("Conectado ao banco: " + conexao.getConexao().getMetaData().getURL());
            PreparedStatement sql = conexao.getConexao().prepareStatement(
                "SELECT * FROM Users WHERE email = ? AND password_user = ? LIMIT 1");
                sql.setString(1, email.trim());
                sql.setString(2, password.trim());  
            ResultSet resultado = sql.executeQuery();
            if (resultado.next()) {
                user = new Users(
                    resultado.getLong("id"),
                    resultado.getString("name"),
                    resultado.getString("email"),
                    resultado.getString("password_user")
                );
                            System.out.println("Usuário autenticado: " + user.getName());
            }
            else {
             System.out.println("Email recebido: [" + email + "]");
             System.out.println("Senha recebida: [" + password + "]");

            System.out.println("Nenhum usuário encontrado com essas credenciais.");
        }
        } catch (SQLException e) {
            System.err.println("Query de login incorreta: " + e.getMessage());
        } finally {
            conexao.closeConexao();
        }
        return user;
    }
}
