package org.andlemro.test.sprintboot.app.springboot_test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.andlemro.test.sprintboot.app.springboot_test.models.Cuenta;
import org.andlemro.test.sprintboot.app.springboot_test.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class IntegracionJpaTest {

	@Autowired
	CuentaRepository cuentaRepository;
	
	@Test
	void testFindById() {
		Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
		
		assertTrue(cuenta.isPresent());
		assertEquals("Andres", cuenta.orElseThrow().getPersona());
	}
	
	@Test
	void testFindByPersona() {
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andres");
		
		assertTrue(cuenta.isPresent());
		assertEquals("Andres", cuenta.orElseThrow().getPersona());
		assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
	}
	
	@Test
	void testFindByThrowException() {
		Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");
		assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
		assertFalse(cuenta.isPresent());
	}
	
	@Test
	void testFinAll() {
		List<Cuenta> cuentas = cuentaRepository.findAll();
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
	}
	
	@Test
	void TesSave() {
		// Given
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		
		// When
		Cuenta cuenta = cuentaRepository.save(cuentaPepe);
//		Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
//		Cuenta cuenta = cuentaRepository.findById(cuentaNueva.getId()).orElseThrow();
		
		// Then
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
//		assertEquals(3, cuenta.getId());
		
	}
	
	@Test
	void TesUpdate() {
		// Given
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		
		// When
		Cuenta cuenta = cuentaRepository.save(cuentaPepe);
	
		// Then
		assertEquals("Pepe", cuenta.getPersona());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		
		// When
		cuenta.setSaldo(new BigDecimal("3800"));
		Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
		
		// Then
		assertEquals("Pepe", cuentaActualizada.getPersona());
		assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
	}
	
	@Test
	void testDelete() {
		Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
		assertEquals("Jhon", cuenta.getPersona());
		
		cuentaRepository.delete(cuenta);
		
		assertThrows(NoSuchElementException.class, () -> {
//			cuentaRepository.findByPersona("Jhon").orElseThrow();
			cuentaRepository.findById(2L).orElseThrow();
		});
		assertEquals(1, cuentaRepository.findAll().size());
	}
}
