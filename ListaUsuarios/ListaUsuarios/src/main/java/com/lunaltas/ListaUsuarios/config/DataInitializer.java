package com.lunaltas.ListaUsuarios.config;

import org.javalite.activejdbc.Base;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 1. Abre a ligação no início da aplicação
        System.out.println("Ligar à base de dados SQLite...");
        try {
            Base.open("org.sqlite.JDBC", "jdbc:sqlite:meu_app.db", "", "");
            // 2. Garante que a tabela existe (Útil porque o ficheiro .db pode estar vazio)
            String sqlCreate = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "first_name VARCHAR(56), " +
                    "last_name VARCHAR(56), " +
                    "email VARCHAR(100))";
            Base.exec(sqlCreate);

            // A PARTIR DAQUI A LIGAÇÃO ESTÁ ABERTA E A TABELA EXISTE


        } catch (Exception e) {
            System.err.println("Ocorreu um erro: " + e.getMessage());
        } finally {
            // 4. É fundamental SEMPRE fechar a ligação no final
            // O bloco finally garante que fechará mesmo se der erro no meio do caminho
            Base.close();
            System.out.println("Ligação encerrada.");
        }
    }
}