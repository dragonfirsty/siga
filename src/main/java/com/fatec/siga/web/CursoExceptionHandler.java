package com.fatec.siga.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fatec.siga.service.CursoService.CodigoDuplicadoException;
import com.fatec.siga.service.CursoService.RecursoNaoEncontradoException;

@RestControllerAdvice(assignableTypes = CursoRestController.class)
public class CursoExceptionHandler {

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> tratarNaoEncontrado(RecursoNaoEncontradoException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(Map.of("erro", exception.getMessage()));
	}

	@ExceptionHandler(CodigoDuplicadoException.class)
	public ResponseEntity<Map<String, String>> tratarCodigoDuplicado(CodigoDuplicadoException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(Map.of("erro", exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> tratarValidacao(MethodArgumentNotValidException exception) {
		String mensagem = exception.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(erro -> erro.getDefaultMessage())
			.orElse("Dados inválidos");

		return ResponseEntity.badRequest().body(Map.of("erro", mensagem));
	}
}
