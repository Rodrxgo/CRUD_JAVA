package Controller;

import java.sql.SQLException;
import java.util.List;

import Dao.UsuarioDao;

public class UsuarioController {

    private final UsuarioDao usuarioDao;

    public UsuarioController() {
        this.usuarioDao = new UsuarioDao();
    }

    public void criarUsuario(String nome, String email, int telefone, String sexo) throws SQLException {
        if (nome == null || nome.trim().isEmpty() || email == null || email.trim().isEmpty() || telefone != 0
                || sexo == null || sexo.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos precisam estar preenchidos.");
        }
        usuarioDao.criarUsuario(nome, email, telefone, sexo);
    }

    public void attUsuario(int id, String nome, String email, int telefone, String sexo) throws SQLException {
        if (id <= 0 || nome == null || nome.trim().isEmpty() || email == null || email.trim().isEmpty() || telefone != 0
                || sexo == null || sexo.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos precisam estar preenchidos.");
        }
        usuarioDao.attUsuario(id, nome, email, telefone, sexo);
    }

    public void excluirUsuario(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        usuarioDao.excluirUsuario(id);
    }

    public List<String[]> listarUsuarios() throws SQLException {
        return usuarioDao.listarUsuarios();
    }

}
