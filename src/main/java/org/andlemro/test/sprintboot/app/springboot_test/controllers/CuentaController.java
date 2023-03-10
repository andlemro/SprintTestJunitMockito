package org.andlemro.test.sprintboot.app.springboot_test.controllers;

import static org.springframework.http.HttpStatus.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.andlemro.test.sprintboot.app.springboot_test.models.Cuenta;
import org.andlemro.test.sprintboot.app.springboot_test.models.TransaccionDTO;
import org.andlemro.test.sprintboot.app.springboot_test.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {
	
	@Autowired
	private CuentaService cuentaService;
	
	@GetMapping
	@ResponseStatus(OK)
	public List<Cuenta> listar() {
		return cuentaService.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable(name = "id") Long id) {
		
		Cuenta cuenta = null;
		try {
			cuenta = cuentaService.findById(id);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(cuenta);
	}
	
	@PostMapping
	@ResponseStatus(CREATED)
	public Cuenta guardar(@RequestBody Cuenta cuenta) {
		return cuentaService.save(cuenta);
	}
	
	@PostMapping("/transferir")
	public ResponseEntity<?> transferir(@RequestBody TransaccionDTO dto){
		cuentaService.transferir(
				dto.getCuentaOrigenId(), dto.getCuentaDestinoid(), 
				dto.getMonto(), dto.getBancoId()
		);
		
		Map<String, Object> response = new HashMap<>();
		response.put("date", LocalDate.now().toString());
		response.put("status", "OK");
		response.put("mensaje", "Transferencia realizada con exito!");
		response.put("transaccion", dto);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		cuentaService.deleteById(id);
	}

}
