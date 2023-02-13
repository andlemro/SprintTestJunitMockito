package org.andlemro.test.sprintboot.app.springboot_test.repositories;

import java.util.List;

import org.andlemro.test.sprintboot.app.springboot_test.models.Cuenta;

public interface CuentaRepository {

	List<Cuenta> findAll();
	Cuenta findById(Long id);
	void update(Cuenta cuenta);

}
