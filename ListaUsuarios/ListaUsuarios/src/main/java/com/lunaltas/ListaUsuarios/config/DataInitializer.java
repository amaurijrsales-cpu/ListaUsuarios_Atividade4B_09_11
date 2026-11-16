
package com.lunaltas.ListaUsuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        try (Connection connection = DatabaseConnection.getConnection()) {

            String sqlCreate = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "arroba TEXT, " +
                    "cpf TEXT, " +
                    "email TEXT, " +
                    "idade INTEGER)";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sqlCreate);
            }

            Set<String> colunas = obterColunas(connection);

            adicionarColuna(connection, colunas, "name", "TEXT");
            adicionarColuna(connection, colunas, "arroba", "TEXT");
            adicionarColuna(connection, colunas, "cpf", "TEXT");
            adicionarColuna(connection, colunas, "email", "TEXT");
            adicionarColuna(connection, colunas, "idade", "INTEGER");

            if (colunas.contains("first_name") &&
                    colunas.contains("last_name")) {

                String sqlMigracao =
                        "UPDATE users SET name = TRIM(" +
                                "COALESCE(first_name, '') || ' ' || " +
                                "COALESCE(last_name, '')) " +
                                "WHERE name IS NULL OR TRIM(name) = ''";

                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sqlMigracao);
                }
            }

            System.out.println("Banco SQLite inicializado com sucesso.");

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Não foi possível inicializar o banco de dados.", e);
        }
    }

    private Set<String> obterColunas(Connection connection)
            throws SQLException {

        Set<String> colunas = new HashSet<>();

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("PRAGMA table_info(users)")) {

            while (result.next()) {
                colunas.add(result.getString("name"));
            }
        }

        return colunas;
    }

    private void adicionarColuna(
            Connection connection,
            Set<String> colunas,
            String nome,
            String tipo) throws SQLException {

        if (!colunas.contains(nome)) {

            String sql = "ALTER TABLE users ADD COLUMN " + nome + " " + tipo;

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }

            colunas.add(nome);
        }
    }
}
