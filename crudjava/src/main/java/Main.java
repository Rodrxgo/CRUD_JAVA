import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import Controller.UsuarioController;

public class Main {

    private static final UsuarioController controller = new UsuarioController();

    public static void main(String[] args) throws Exception {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);
        servidor.createContext("/", new StaticFileHandler());
        servidor.createContext("/criar", new CriarUsuarioHandler());
        servidor.createContext("/listarUsuario", new ListarUsuarioHandler());
        servidor.start();
        System.out.println("A aplicação está rodando na porta http://localhost:8080");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = "crudjava/src/main/java/View" + exchange.getRequestURI().getPath();
            File file = new File(
                    filePath.equals("crudjava/src/main/java/View/") ? "crudjava/src/main/java/View/criarUsuario.html"
                            : filePath);
            if (file.exists()) {
                exchange.sendResponseHeaders(200, file.length());
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(exchange.getResponseBody());
                }
            } else {
                exchange.sendResponseHeaders(404, 0);
            }
            exchange.close();
        }
    }

    static class CriarUsuarioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try (BufferedReader leitor = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                    String[] data = leitor.readLine().split("&");
                    String nome = data[0].split("=")[1].replace("+", " ");
                    String email = data[1].split("=")[1];
                    String telefone = data[2].split("=")[1];
                    String sexo = data[3].split("=")[1];

                    telefone = telefone.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos

                    controller.criarUsuario(nome, email, telefone, sexo);
                    exchange.getResponseHeaders().set("Location", "/listarUsuario");
                    exchange.sendResponseHeaders(302, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                    String response = "Erro ao criar o usuário.";
                    System.err.println("Erro ao criar o usuário: " + e.getMessage());
                    exchange.sendResponseHeaders(500, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                }
            } else {
                String response = "Método não permitido. Use POST.";
                exchange.sendResponseHeaders(405, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.close();
        }
    }

    static class ListarUsuarioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder html = new StringBuilder();

            // Iniciando o HTML com estilo
            html.append("<!DOCTYPE html>")
                    .append("<html lang=\"pt-BR\">")
                    .append("<head>")
                    .append("<meta charset=\"UTF-8\">")
                    .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                    .append("<title>Lista de Usuários</title>")
                    .append("<style>")
                    .append("body { font-family: Arial, sans-serif; background-color: #f3f4f6; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }")
                    .append(".list-container { background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); width: 100%; max-width: 800px; }")
                    .append(".list-container h2 { margin-bottom: 20px; color: #333; text-align: center; }")
                    .append(".user-table { width: 100%; border-collapse: collapse; }")
                    .append(".user-table th, .user-table td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }")
                    .append(".user-table th { background-color: #f8f9fa; color: #555; }")
                    .append(".user-table tr:hover { background-color: #f1f1f1; }")
                    .append(".action-buttons { display: flex; gap: 10px; }")
                    .append(".edit-button, .delete-button { padding: 5px 10px; border: none; border-radius: 5px; font-size: 14px; cursor: pointer; transition: background-color 0.3s; }")
                    .append(".edit-button { background-color: #007bff; color: #fff; }")
                    .append(".edit-button:hover { background-color: #0056b3; }")
                    .append(".delete-button { background-color: #dc3545; color: #fff; }")
                    .append(".delete-button:hover { background-color: #a71d2a; }")
                    .append("</style>")
                    .append("</head>")
                    .append("<body>")
                    .append("<div class=\"list-container\">")
                    .append("<h2>Lista de Usuários</h2>")
                    .append("<table class=\"user-table\">")
                    .append("<thead>")
                    .append("<tr><th>ID</th><th>Nome</th><th>E-mail</th><th>Telefone</th><th>Sexo</th><th>Ações</th></tr>")
                    .append("</thead>")
                    .append("<tbody>");

            try {
                List<String[]> users = controller.listarUsuarios();
                for (String[] user : users) {
                    html.append("<tr>")
                            .append("<td>").append(user[0]).append("</td>")
                            .append("<td>").append(user[1]).append("</td>")
                            .append("<td>").append(user[2]).append("</td>")
                            .append("<td>").append(user[3]).append("</td>")
                            .append("<td>").append(user[4]).append("</td>")
                            .append("<td>")
                            .append("<a class='edit-button' href='/edit?id=").append(user[0]).append("'>Editar</a> ")
                            .append("<a class='delete-button' href='/delete?id=").append(user[0])
                            .append("'>Excluir</a>")
                            .append("</td>")
                            .append("</tr>");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                html.append("<tr><td colspan='6'>Erro ao carregar os dados.</td></tr>");
            }

            html.append("</tbody></table>")
                    .append("<a href='/criarUsuario.html'>Criar Novo Usuário</a>")
                    .append("</div>")
                    .append("</body>")
                    .append("</html>");

            byte[] response = html.toString().getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        }
    }
}
