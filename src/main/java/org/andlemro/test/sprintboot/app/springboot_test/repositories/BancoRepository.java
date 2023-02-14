package org.andlemro.test.sprintboot.app.springboot_test.repositories;

import java.util.List;

import org.andlemro.test.sprintboot.app.springboot_test.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
	
//	List<Banco> findAll();
//	Banco findById(Long id);
//	void update(Banco banco);
}
