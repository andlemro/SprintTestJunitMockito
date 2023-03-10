package org.andlemro.test.sprintboot.app.springboot_test.controllers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andlemro.test.sprintboot.app.springboot_test.models.Cuenta;
import org.andlemro.test.sprintboot.app.springboot_test.models.TransaccionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

// Mediante la anotacion @TestMethodOrder indicamos a nuestros metodos
// cual sera la prioridad en ejecucion
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CuentaControllerWebTestClientTests {

	@Autowired
	private WebTestClient client;
	
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	@Order(1) // De esta manera indicamos el orden de ejecucion.
	void testTransferir() throws JsonProcessingException {
		
		// Given
		TransaccionDTO dto = new TransaccionDTO();
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoid(2L);
		dto.setBancoId(1L);
		dto.setMonto(new BigDecimal("100"));
		
		// Se crea el json de respuesta esperado para posterior mente ser evaluado en la prueba.
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con exito!");
		response.put("transaccion", dto);
		
		// When
		client.post().uri("/api/cuentas/transferir")
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(dto)
		.exchange()
		
		// Then
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		
		// Otra manera de validar es con consumeWith
		.consumeWith(respuesta -> {
			try {
				JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
				assertEquals("Transferencia realizada con exito!", json.path("mensaje").asText());
				assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
				assertEquals(LocalDate.now().toString(), json.path("date").asText());
				assertEquals("100", json.path("transaccion").path("monto").asText());
			} catch (IOException e) {
				e.printStackTrace();
			}
		})
		
		// se evalua los datos del json de manera independiente
		.jsonPath("$.mensaje").isNotEmpty()
		.jsonPath("$.mensaje").value(is("Transferencia realizada con exito!"))
		.jsonPath("$.mensaje").value( valor -> assertEquals("Transferencia realizada con exito!", valor))
		.jsonPath("$.mensaje").isEqualTo("Transferencia realizada con exito!")
		.jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
		.jsonPath("$.date").isEqualTo(LocalDate.now().toString())
		
		// De esta manera se evalua el Json completo.
		.json(objectMapper.writeValueAsString(response));
	}
	
	@Test
	@Order(2)
	void TestDetalle() throws JsonProcessingException {
		
		Cuenta cuenta = new Cuenta(1L, "Andres", new BigDecimal("900"));
		
		client.get().uri("/api/cuentas/1").exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.persona").isEqualTo("Andres")
		.jsonPath("$.saldo").isEqualTo(900)
		.json(objectMapper.writeValueAsString(cuenta));
	}
	
	@Test
	@Order(3)
	void TestDetalle2() {
		client.get().uri("/api/cuentas/2").exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Cuenta.class)
		.consumeWith( respuesta -> {
			Cuenta cuenta = respuesta.getResponseBody();
			assertNotNull(cuenta);
			assertEquals("Jhon", cuenta.getPersona());
			assertEquals("2100.00", cuenta.getSaldo().toPlainString());
		});
	}
	
	@Test
	@Order(4)
	void testListar() {
		client.get().uri("/api/cuentas").exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$[0].persona").isEqualTo("Andres")
		.jsonPath("$[0].id").isEqualTo(1)
		.jsonPath("$[0].saldo").isEqualTo(900)
		.jsonPath("$[1].persona").isEqualTo("Jhon")
		.jsonPath("$[1].id").isEqualTo(2)
		.jsonPath("$[1].saldo").isEqualTo(2100)
		.jsonPath("$").isArray()
		.jsonPath("$").value(hasSize(2));
	}
	
	@Test
	@Order(5)
	void testListar2() {
		client.get().uri("/api/cuentas").exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Cuenta.class)
		
		.consumeWith( respuesta -> {
			List<Cuenta> cuentas = respuesta.getResponseBody();
			assertNotNull(cuentas);
			assertEquals(2, cuentas.size());
			assertEquals(1L, cuentas.get(0).getId());
			assertEquals("Andres", cuentas.get(0).getPersona());
			assertEquals("900.0", cuentas.get(0).getSaldo().toPlainString());
			assertEquals(2L, cuentas.get(1).getId());
			assertEquals("Jhon", cuentas.get(1).getPersona());
			assertEquals("2100.0", cuentas.get(1).getSaldo().toPlainString());
		})
		.hasSize(2)
		.value(hasSize(2));
	}
	
	@Test
	@Order(6)
	void testGuardar() {
		
		//Given
		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		
		// When
		client.post().uri("/api/cuentas")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(cuenta)
			.exchange()
			
		// Then
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.id").isEqualTo(3)
			.jsonPath("$.persona").isEqualTo("Pepe")
			.jsonPath("$.persona").value(is("Pepe"))
			.jsonPath("$.saldo").isEqualTo(3000);
	}
	
	@Test
	@Order(7)
	void testGuardar2() {
		
		//Given
		Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3500"));
		
		// When
		client.post().uri("/api/cuentas")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(cuenta)
			.exchange()
			
		// Then
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Cuenta.class)
			.consumeWith(response -> {
				Cuenta c = response.getResponseBody();
				assertNotNull(c);
				assertEquals(4L, c.getId());
				assertEquals("Pepa", c.getPersona());
				assertEquals("3500", c.getSaldo().toPlainString());
			});
	}
	
	@Test
	@Order(8)
	void testEliminar() {
		client.get().uri("/api/cuentas").exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Cuenta.class)
			.hasSize(4);
		
		client.delete().uri("/api/cuentas/3")
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();
		
		client.get().uri("/api/cuentas").exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Cuenta.class)
			.hasSize(3);
		
		client.get().uri("/api/cuentas/3").exchange()
//			.expectStatus().is5xxServerError();
			.expectStatus().isNotFound()
			.expectBody().isEmpty();
	}
	
}
