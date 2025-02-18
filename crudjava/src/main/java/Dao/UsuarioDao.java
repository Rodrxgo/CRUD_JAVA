package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {
    public void criarUsuario(String nome, String email, String telefone, String sexo) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, telefone, sexo) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoDB.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, telefone);
            stmt.setString(4, sexo);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new SQLException("Erro ao inserir usuário no banco de dados", e);
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
                        resultado.getString("email"), resultado.getString("telefone"),
                        resultado.getString("sexo") });
            }

        }
        return usuarios;
    }

    public void attUsuario(int id, String nome, String email, String telefone, String sexo) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, telefone = ?, sexo = ? WHERE id = ?";
        try (Connection conexao = ConexaoDB.getConnection();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, telefone);
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

    public String[] buscarUsuarioPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{String.valueOf(rs.getInt("id")), rs.getString("nome"), rs.getString("email"),
                            rs.getString("telefone"), rs.getString("sexo")};
                }
            }
        }
        return null;
    }
}
