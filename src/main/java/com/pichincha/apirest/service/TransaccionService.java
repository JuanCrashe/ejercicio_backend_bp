package com.pichincha.apirest.service;

import org.springframework.stereotype.Service;

@Service
public class TransaccionService {

	public boolean tieneMontoDisponile(double monto, double montoRetirar) {
		return (monto - montoRetirar) >= 0;
	}
}
