package org.andlemro.test.sprintboot.app.springboot_test.services;

import java.math.BigDecimal;

import org.andlemro.test.sprintboot.app.springboot_test.models.Banco;
import org.andlemro.test.sprintboot.app.springboot_test.models.Cuenta;
import org.andlemro.test.sprintboot.app.springboot_test.repositories.BancoRepository;
import org.andlemro.test.sprintboot.app.springboot_test.repositories.CuentaRepository;
import org.springframework.stereotype.Service;

/***
 * Mediante la anotacion @Service, nos permite indicar que vamos a registrar esta clase
 * en el contenedor de spring, para indicar de que sera un compoenente y lo podamos utilziar
 * mediante la injeccion de dependencias por medio de la anotacion @Autowired.
 * 
 */
@Service
public class CuentaServiceImpl implements CuentaService {
	
	private CuentaRepository cuentaRepository;
	private BancoRepository bancoRepository;
	
	public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
		super();
		this.cuentaRepository = cuentaRepository;
		this.bancoRepository = bancoRepository;
	}

	@Override
	public Cuenta findById(Long id) {
		return cuentaRepository.findById(id).orElseThrow();
	}

	@Override
	public int revisarTotalTransferencias(Long bancoId) {
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		return banco.getTotalTransferencias();
	}

	@Override
	public BigDecimal revisarSaldo(Long cuentaId) {
		Cuenta cuenta = cuentaRepository.findById(cuentaId).orElseThrow();
		return cuenta.getSaldo();
	}

	@Override
	public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto,
			Long bancoId) {
		
		Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen).orElseThrow();
		cuentaOrigen.debito(monto);
		cuentaRepository.save(cuentaOrigen);
		
		Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino).orElseThrow();
		cuentaDestino.credito(monto);
		cuentaRepository.save(cuentaDestino);
		
		Banco banco = bancoRepository.findById(bancoId).orElseThrow();
		int totalTransferencia = banco.getTotalTransferencias();
		banco.setTotalTransferencias(++totalTransferencia);
		bancoRepository.save(banco);
	}
	
}
