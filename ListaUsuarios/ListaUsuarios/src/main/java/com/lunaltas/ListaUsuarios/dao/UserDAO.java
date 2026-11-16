
package com.lunaltas.ListaUsuarios.dao;

import com.lunaltas.ListaUsuarios.config.DatabaseConnection;
import com.lunaltas.ListaUsuarios.model.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAO {

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        String sql = "SELECT id, name, arroba, cpf, email, idade " +
                "FROM users ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                users.add(mapearUsuario(result));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar usuários.", e);
        }

        return users;
    }

    public User findById(Long id) {
        String sql = "SELECT id, name, arroba, cpf, email, idade " +
                "FROM users WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapearUsuario(result);
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar usuário.", e);
        }

        return null;
    }

    public void save(User user) {
        String sql = "INSERT INTO users (name, arroba, cpf, email, idade) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            preencherCampos(statement, user);
            statement.executeUpdate();

            try (Statement idStatement = connection.createStatement();
                 ResultSet result = idStatement.executeQuery(
                         "SELECT last_insert_rowid()")) {

                if (result.next()) {
                    user.setId(result.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao cadastrar usuário.", e);
        }
    }

    public void update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("O ID do usuário é obrigatório.");
        }

        String sql = "UPDATE users SET name = ?, arroba = ?, cpf = ?, " +
                "email = ?, idade = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            preencherCampos(statement, user);
            statement.setLong(6, user.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar usuário.", e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir usuário.", e);
        }
    }

    private void preencherCampos(PreparedStatement statement, User user)
            throws SQLException {

        statement.setString(1, user.getName());
        statement.setString(2, user.getArroba());
        statement.setString(3, user.getCpf());
        statement.setString(4, user.getEmail());

        if (user.getIdade() != null) {
            statement.setInt(5, user.getIdade());
        } else {
            statement.setNull(5, Types.INTEGER);
        }
    }

    private User mapearUsuario(ResultSet result) throws SQLException {
        User user = new User();

        user.setId(result.getLong("id"));
        user.setName(result.getString("name"));
        user.setArroba(result.getString("arroba"));
        user.setCpf(result.getString("cpf"));
        user.setEmail(result.getString("email"));

        int idade = result.getInt("idade");

        if (!result.wasNull()) {
            user.setIdade(idade);
        }

        return user;
    }
}
