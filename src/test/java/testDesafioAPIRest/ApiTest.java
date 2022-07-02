package testDesafioAPIRest;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiTest {

    @BeforeAll
    public static void setup () {
        baseURI= "http://localhost:8080";
    }

    @Test
    @DisplayName("Consultar uma restrição pelo CPF - status code 200 com problema")
    public void consultaRestricaoCPFComProblema () {
        given()
                .log().all()
        .when()
                .get("api/v1/restricoes/97093236014")
        .then()
                .log().all()
                .statusCode(200)
                .body("mensagem", is("O CPF 97093236014 tem problema"));
    }

    @Test
    @DisplayName("Consultar uma restrição pelo CPF - status code 204 sem restrição")
    public void consultaRestricaoCPFSemRestricao () {
        given()
                .log().all()
        .when()
                .get("api/v1/restricoes/99999999999")
        .then()
                .log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("Cadastro de CPF duplicado - status code 400")
    public void cadastroCPFDuplicado () {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nome\": \"Fulano de Tal\",\n" +
                        "  \"cpf\": 19626829001,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 1200,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
        .when()
                .post("/api/v1/simulacoes");

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nome\": \"Fulano de Tal\",\n" +
                        "  \"cpf\": 19626829001,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 1200,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
        .when()
                .post("/api/v1/simulacoes")// método POST para consulta de restrição
        .then()
                .log().all()
                .statusCode(400)
                .body("mensagem", is("CPF duplicado"));
    }

    @Test
    @DisplayName("Cadastro de CPF com SUCESSO - status code 201")
    public void cadastroCPFSucesso () {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nome\": \"Fulano de Tal\",\n" +
                        "  \"cpf\": 32271549000,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 1200,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
        .when()
                .post("/api/v1/simulacoes")// método POST para consulta de restrição
        .then()
                .log().all()
                .statusCode(201)
                .body("nome", is("Fulano de Tal"))
                .body("cpf", is("32271549000"))
                .body("email", is("email@email.com"));
    }

    @Test
    @DisplayName("Cadastro de CPF sem email - status code 400")
    public void cadastroCPFComErro () {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nome\": \"Fulano de Tal\",\n" +
                        "  \"cpf\": 32271549000,\n" +
                        "  \"email\": \"\",\n" +
                        "  \"valor\": 1200,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
        .when()
                .post("/api/v1/simulacoes")// método POST para consulta de restrição
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.email", is("E-mail deve ser um e-mail válido"));
    }

    @Test
    @DisplayName("Alteração de CPF não cadastrado - status code 400")
    public void aterarCPFNaoCadastrado () {
        given()
        .when()
                .put("api/v1/simulacoes/1223342")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Tentativa de alteração de CPF - status code 400")
    public void alterarCPFComExisitente () {
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nome\": \"Fulano de Tal Alterado\",\n" +
                        "  \"cpf\": 19626829001,\n" +
                        "  \"email\": \"email@email.com\",\n" +
                        "  \"valor\": 1200,\n" +
                        "  \"parcelas\": 3,\n" +
                        "  \"seguro\": true\n" +
                        "}")
        .when()
                .put("/api/v1/simulacoes/75832361008")
        .then()
                .log().all()
                .statusCode(400)
                .body("mensagem", is("CPF duplicado"));
    }

    @Test
    @DisplayName("Consultar todas simulações existentes - status code 200")
    public void consultaSimulacoes () {
        given()
        .when()
                .get("/api/v1/simulacoes")
        .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(14)); // valida a qtde de objetos na lista
    }

    @Test
    @DisplayName("Consultar simulação de CPF existentes - status code 200")
    public void consultaSimulacaoCPF () {
        given()
        .when()
                .get("/api/v1/simulacoes/17822386034")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", is(12))
                .body("nome", is("Deltrano"))
                .body("cpf", is("17822386034"))
                .body("email", is("deltrano@gmail.com"))
                .body("valor", is(20000.00F))
                .body("parcelas", is(5));
    }

    @Test
    @DisplayName("Consultar simulação de CPF inexistente - status code 404")
    public void consultaSimulacaoCPFNaoCadastrado () {
        given()
        .when()
                .get("/api/v1/simulacoes/06756384023")
        .then()
                .log().all()
                .statusCode(404)
                .body("mensagem", is("CPF 06756384023 não encontrado"));
    }

    @Test
    @DisplayName("Removendo uma simulação por ID - status code 204")
    public void removeSimulacao () {
        given()
        .when()
                .delete("/api/v1/simulacoes/11")
        .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Removendo uma simulação por ID inexistente - status code 404")
    public void removeSimulacaoIdInexistente () {
        given()
        .when()
                .delete("/api/v1/simulacoe/1")
        .then()
                .log().all()
                .statusCode(404)
                .body("error", is("Not Found"))
                .body("message", is("No message available"))
                .body("path", is("/api/v1/simulacoe/1"));
    }

}

