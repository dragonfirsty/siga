package com.fatec.siga.config;

import java.net.URI;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile("postgresql")
public class PostgreSqlDataSourceConfig {

	@Bean
	DataSource dataSource(Environment environment) {
		String configuredUrl = primeiroValor(environment,
			"JDBC_DATABASE_URL",
			"DATABASE_URL",
			"POSTGRES_URL");

		DatabaseLocation location = normalizarDatabaseUrl(configuredUrl);

		String username = primeiroValor(environment,
			"POSTGRES_USER",
			"PGUSER");
		String password = primeiroValor(environment,
			"POSTGRES_PASSWORD",
			"PGPASSWORD");

		if ((username == null || username.isBlank()) && location.username() != null) {
			username = location.username();
		}
		if ((password == null || password.isBlank()) && location.password() != null) {
			password = location.password();
		}

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(location.jdbcUrl());
		config.setDriverClassName("org.postgresql.Driver");
		config.setUsername(username);
		config.setPassword(password);
		return new HikariDataSource(config);
	}

	private String primeiroValor(Environment environment, String... nomes) {
		for (String nome : nomes) {
			String valor = environment.getProperty(nome);
			if (valor != null && !valor.isBlank()) {
				return valor.trim();
			}
		}
		return null;
	}

	private DatabaseLocation normalizarDatabaseUrl(String configuredUrl) {
		String url = configuredUrl == null || configuredUrl.isBlank()
			? "jdbc:postgresql://localhost:5432/siga"
			: configuredUrl.trim();

		if (url.startsWith("jdbc:")) {
			url = url.substring("jdbc:".length());
		}
		if (url.startsWith("postgres://")) {
			url = "postgresql://" + url.substring("postgres://".length());
		}

		URI uri = URI.create(url);
		String username = null;
		String password = null;

		if (uri.getUserInfo() != null) {
			String[] credenciais = uri.getUserInfo().split(":", 2);
			username = credenciais[0];
			if (credenciais.length == 2) {
				password = credenciais[1];
			}
		}

		int port = uri.getPort() > 0 ? uri.getPort() : 5432;
		String path = uri.getPath();
		if (path == null || path.isBlank()) {
			path = "/siga";
		}

		String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path;
		if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) {
			jdbcUrl += "?" + uri.getRawQuery();
		}

		return new DatabaseLocation(jdbcUrl, username, password);
	}

	private record DatabaseLocation(String jdbcUrl, String username, String password) {
	}
}
