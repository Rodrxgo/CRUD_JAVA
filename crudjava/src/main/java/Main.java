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
        servidor.createContext("/criarUsuario", new criarUsuario());
        servidor.createContext("/listarUsuario", new listarUsuario());
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

    static class criarUsuario implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader leitor = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String[] data = leitor.readLine().split("&");
                String nome = data[0].split("=")[1].replace("+", " ");
                String email = data[1].split("=")[1];
                String telefoneStr = data[2].split("=")[1];
                String sexo = data[3].split("=")[1];

                telefoneStr = telefoneStr.replaceAll("[^0-9]", "");
                int telefone = Integer.parseInt(telefoneStr);

                try {
                    controller.criarUsuario(nome, email, telefone, sexo);
                } catch (SQLException | IllegalArgumentException e) {
                    e.printStackTrace();
                }

                exchange.getResponseHeaders().set("Location", "/listarUsuario");
                exchange.sendResponseHeaders(302, -1);
            }
        }
    }

    static class listarUsuario implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>")
                    .append("<html lang=\"pt-BR\">")
                    .append("<head>")
                    .append("<meta charset=\"UTF-8\">")
                    .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                    .append("<title>Lista de Usuários</title>")
                    .append("</head>")
                    .append("<body>")
                    .append("<div class=\"list-container\">")
                    .append("<h2>Lista de Usuários</h2>")
                    .append("<table class=\"user-table\">")
                    .append("<thead><tr><th>ID</th><th>Nome</th><th>E-mail</th><th>Telefone</th><th>Sexo</th><th>Ações</th></tr></thead>")
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
                    .append("</body></html>");

            byte[] response = html.toString().getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        }
    }

}