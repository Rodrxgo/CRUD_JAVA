package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {
    public void criarUsuario(String nome, String email, int telefone, String sexo) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, telefone, sexo) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoDB.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setInt(3, telefone);
            stmt.setString(4, sexo);
        }
    }

    public List<String[]> listarUsuarios() throws SQLException {
        String sql = "SELECT * FROM usuarios";
        List<String[]> usuarios = new ArrayList<>();
        try (Connection conexao = ConexaoDB.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultado = stmt.executeQuery()) {
            while (resultado.next()) {
                usuarios.add(new String[] { String.valueOf(resultado.getInt("id")), resultado.getString("nome"),
                        resultado.getString("email"), String.valueOf(resultado.getInt("telefone")),
                        resultado.getString("sexo") });
            }

        }
        return usuarios;
    }

    public void attUsuario(int id, String nome, String email, int telefone, String sexo) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, telefone = ?, sexo = ? WHERE id = ?";
        try (Connection conexao = ConexaoDB.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setInt(3, telefone);
            stmt.setString(4, sexo);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }

    public void excluirUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conexao = ConexaoDB.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
