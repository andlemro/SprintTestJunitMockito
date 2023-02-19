package org.andlemro.test.sprintboot.app.springboot_test.models;

import java.math.BigDecimal;

public class TransaccionDTO {
	
	private Long cuentaOrigenId;
	private Long cuentaDestinoid;
	private BigDecimal monto;
	private Long bancoId;
	
	public Long getCuentaOrigenId() {
		return cuentaOrigenId;
	}
	public void setCuentaOrigenId(Long cuentaOrigenId) {
		this.cuentaOrigenId = cuentaOrigenId;
	}
	public Long getCuentaDestinoid() {
		return cuentaDestinoid;
	}
	public void setCuentaDestinoid(Long cuentaDestinoid) {
		this.cuentaDestinoid = cuentaDestinoid;
	}
	public BigDecimal getMonto() {
		return monto;
	}
	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}
	public Long getBancoId() {
		return bancoId;
	}
	public void setBancoId(Long bancoId) {
		this.bancoId = bancoId;
	}

}
