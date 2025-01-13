import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/db_project";
        String user = "root";
        String password = "teste1234";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexão bem-sucedida!");
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Driver não encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco.");
            e.printStackTrace();
        }
    }
}