package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    private static final String URL = "jdbc:mysql://localhost:3306/db_project?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSAWORD = "teste1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSAWORD);
    }
}
