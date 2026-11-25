package com.fatec.siga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityAuthorizationIntegrationTest {

	private static final Pattern CSRF_TOKEN_PATTERN =
			Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"");

	@LocalServerPort
	private int port;

	@Test
	void devePermitirLoginPublico() throws Exception {
		HttpResponse<String> response = novoCliente().send(
				HttpRequest.newBuilder(uri("/login")).GET().build(),
				HttpResponse.BodyHandlers.ofString());

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("Acesse sua conta"));
	}

	@Test
	void deveRejeitarCredenciaisInvalidas() throws Exception {
		HttpClient client = novoCliente();
		String csrf = obterTokenCsrf(client);
		HttpResponse<String> response = enviarLogin(client, "nao-existe@siga.local", "senha-incorreta", csrf);

		assertEquals(302, response.statusCode());
		assertTrue(response.headers().firstValue("location").orElse("").contains("/login?error"));
	}

	@Test
	void administradorPodeAcessarRecursosAdministrativos() throws Exception {
		SessaoAutenticada sessao = autenticar("admin@siga.local", "admin123");

		assertEquals(200, get(sessao, "/admin").statusCode());
		assertEquals(400, post(sessao, "/api/cursos", obterTokenCsrf(sessao.client()), "{}").statusCode());
		assertEquals(403, get(sessao, "/professor").statusCode());
		assertEquals(403, get(sessao, "/aluno").statusCode());
	}

	@Test
	void professorPodeAcessarSomenteSuaArea() throws Exception {
		SessaoAutenticada sessao = autenticar("professor@siga.local", "professor123");

		assertEquals(200, get(sessao, "/professor").statusCode());
		assertEquals(403, get(sessao, "/admin").statusCode());
		assertEquals(403, get(sessao, "/aluno").statusCode());
		assertEquals(403, post(sessao, "/api/cursos", obterTokenCsrf(sessao.client()), "{}").statusCode());
	}

	@Test
	void alunoPodeAcessarSomenteSuaArea() throws Exception {
		SessaoAutenticada sessao = autenticar("aluno@siga.local", "aluno123");

		assertEquals(200, get(sessao, "/aluno").statusCode());
		assertEquals(403, get(sessao, "/admin").statusCode());
		assertEquals(403, get(sessao, "/professor").statusCode());
	}

	@Test
	void deveSolicitarLoginParaRotaProtegidaSemAutenticacao() throws Exception {
		HttpResponse<String> response = novoCliente().send(
				HttpRequest.newBuilder(uri("/admin")).GET().build(),
				HttpResponse.BodyHandlers.ofString());

		assertEquals(302, response.statusCode());
		assertTrue(response.headers().firstValue("location").orElse("").contains("/login"));
	}

	@Test
	void deveNegarRotasSemRegraExplicita() throws Exception {
		SessaoAutenticada sessao = autenticar("admin@siga.local", "admin123");

		assertEquals(403, get(sessao, "/rota-nao-configurada").statusCode());
	}

	@Test
	void deveExigirCsrfEmOperacoesDeEscrita() throws Exception {
		SessaoAutenticada sessao = autenticar("admin@siga.local", "admin123");

		assertEquals(403, post(sessao, "/api/cursos", null, "{}").statusCode());
	}

	private SessaoAutenticada autenticar(String email, String senha) throws Exception {
		HttpClient client = novoCliente();
		String csrf = obterTokenCsrf(client);
		HttpResponse<String> response = enviarLogin(client, email, senha, csrf);

		assertEquals(302, response.statusCode());
		assertTrue(response.headers().firstValue("location").orElse("").endsWith("/"));
		return new SessaoAutenticada(client);
	}

	private String obterTokenCsrf(HttpClient client) throws Exception {
		HttpResponse<String> response = client.send(
				HttpRequest.newBuilder(uri("/login")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());

		Matcher matcher = CSRF_TOKEN_PATTERN.matcher(response.body());
		assertTrue(matcher.find(), "A tela de login deve conter o token CSRF");
		return matcher.group(1);
	}

	private HttpResponse<String> enviarLogin(HttpClient client, String email, String senha, String csrf)
			throws Exception {
		String body = "username=" + encode(email)
				+ "&password=" + encode(senha)
				+ "&_csrf=" + encode(csrf);
		HttpRequest request = HttpRequest.newBuilder(uri("/login"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> get(SessaoAutenticada sessao, String path) throws Exception {
		return sessao.client().send(
				HttpRequest.newBuilder(uri(path)).GET().build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> post(SessaoAutenticada sessao, String path, String csrf, String body)
			throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
				.header("Content-Type", "application/json");
		if (csrf != null) {
			builder.header("X-CSRF-TOKEN", csrf);
		}
		return sessao.client().send(
				builder.POST(HttpRequest.BodyPublishers.ofString(body)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private HttpClient novoCliente() {
		CookieManager cookies = new CookieManager();
		cookies.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		return HttpClient.newBuilder()
				.cookieHandler(cookies)
				.followRedirects(HttpClient.Redirect.NEVER)
				.build();
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + port + path);
	}

	private String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	private record SessaoAutenticada(HttpClient client) {
	}
}
