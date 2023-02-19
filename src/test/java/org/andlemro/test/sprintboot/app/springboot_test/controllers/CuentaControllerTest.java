package org.andlemro.test.sprintboot.app.springboot_test.controllers;

import static org.mockito.Mockito.*;
import static org.andlemro.test.sprintboot.app.springboot_test.Datos.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andlemro.test.sprintboot.app.springboot_test.models.Cuenta;
import org.andlemro.test.sprintboot.app.springboot_test.models.TransaccionDTO;
import org.andlemro.test.sprintboot.app.springboot_test.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CuentaController.class)
public class CuentaControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private CuentaService cuentaService;
	
	ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
	}
	
	@Test
	void testDetalle() throws Exception {
		// Given
		when(cuentaService.findById(1L)).thenReturn(crearCuenta001().orElseThrow());
		
		// When
		mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
		//Then
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.persona").value("Andres"))
		.andExpect(jsonPath("$.saldo").value("1000"));
	
		verify(cuentaService).findById(1L);
	}
	
	@Test
	void transferir() throws JsonProcessingException, Exception {
		//Given
		TransaccionDTO dto = new TransaccionDTO();
		dto.setCuentaOrigenId(1L);
		dto.setCuentaDestinoid(2L);
		dto.setMonto(new BigDecimal("100"));
		dto.setBancoId(1L);
		
		System.out.println(objectMapper.writeValueAsString(dto));
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con exito!");
		response.put("transaccion", dto);
		
		System.out.println(objectMapper.writeValueAsString(response));
		
		// When
		mvc.perform(post("/api/cuentas/transferir")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
		// Then
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
		.andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito!"))
		.andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(dto.getCuentaOrigenId()))
		.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}
	
	@Test
	void testListar() throws Exception {
		// Given
		List<Cuenta> cuentas = Arrays.asList(
				crearCuenta001().orElseThrow(),
				crearCuenta002().orElseThrow()
		);
		when(cuentaService.findAll()).thenReturn(cuentas);
		
		// When
		mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
		// Then
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$[0].persona").value("Andres"))
		.andExpect(jsonPath("$[1].persona").value("Jhon"))
		.andExpect(jsonPath("$[0].saldo").value("1000"))
		.andExpect(jsonPath("$[1].saldo").value("2000"))
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
		
		verify(cuentaService).findAll();
	}
	
	@Test
	void guardar() throws JsonProcessingException, Exception {
		// Given
		Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaService.save(any())).then(Invocation -> {
			Cuenta c = Invocation.getArgument(0);
			c.setId(3L);
			return c;
		});
		
		// When
		mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(cuenta)))
		// Then
		.andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id", is(3)))
		.andExpect(jsonPath("$.persona", is("Pepe")))
		.andExpect(jsonPath("$.saldo", is(3000)));
		
		verify(cuentaService).save(any());
	}

}
