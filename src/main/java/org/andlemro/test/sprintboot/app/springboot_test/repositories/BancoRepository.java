package org.andlemro.test.sprintboot.app.springboot_test.repositories;

import java.util.List;

import org.andlemro.test.sprintboot.app.springboot_test.models.Banco;

public interface BancoRepository {
	
	List<Banco> findAll();
	
	Banco findById(Long id);
	
	void update(Banco banco);
}
