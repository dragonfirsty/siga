package com.fatec.siga.config;

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
		String username = null;
		String password = null;
		String url = configuredUrl == null || configuredUrl.isBlank()
			? "postgresql://localhost:5432/siga"
			: configuredUrl.trim();

		while (url.startsWith("jdbc:")) {
			url = url.substring("jdbc:".length());
		}
		if (url.startsWith("postgres://")) {
			url = "postgresql://" + url.substring("postgres://".length());
		}

		int atIndex = url.lastIndexOf('@');
		String hostPart;
		String pathPart;

		if (atIndex >= 0) {
			String credentialsPart = url.substring(0, atIndex);
			hostPart = url.substring(atIndex + 1);

			int credentialsSeparator = credentialsPart.lastIndexOf("//");
			if (credentialsSeparator >= 0) {
				credentialsPart = credentialsPart.substring(credentialsSeparator + 2);
			} else if (credentialsPart.startsWith("postgresql://")) {
				credentialsPart = credentialsPart.substring("postgresql://".length());
			}

			int colon = credentialsPart.indexOf(':');
			if (colon >= 0) {
				username = credentialsPart.substring(0, colon);
				password = credentialsPart.substring(colon + 1);
			}
		} else {
			hostPart = url;
		}

		if (hostPart.startsWith("postgresql://")) {
			hostPart = hostPart.substring("postgresql://".length());
		}
		if (hostPart.startsWith("postgres://")) {
			hostPart = hostPart.substring("postgres://".length());
		}

		int slash = hostPart.indexOf('/');
		String hostAndPort;
		if (slash >= 0) {
			hostAndPort = hostPart.substring(0, slash);
			pathPart = hostPart.substring(slash + 1);
		} else {
			hostAndPort = hostPart;
			pathPart = "siga";
		}

		String host = hostAndPort;
		int port = 5432;
		int hostColon = hostAndPort.indexOf(':');
		if (hostColon >= 0) {
			host = hostAndPort.substring(0, hostColon);
			try {
				port = Integer.parseInt(hostAndPort.substring(hostColon + 1));
			} catch (NumberFormatException ignored) {
				port = 5432;
			}
		}

		int pathColon = pathPart.indexOf(':');
		if (pathColon >= 0) {
			pathPart = pathPart.substring(0, pathColon);
		}
		int pathSlash = pathPart.indexOf('/');
		if (pathSlash >= 0) {
			pathPart = pathPart.substring(0, pathSlash);
		}
		int question = pathPart.indexOf('?');
		if (question >= 0) {
			pathPart = pathPart.substring(0, question);
		}

		if (pathPart.isBlank()) {
			pathPart = "siga";
		}

		String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + pathPart;
		return new DatabaseLocation(jdbcUrl, username, password);
	}

	private record DatabaseLocation(String jdbcUrl, String username, String password) {
	}
}
