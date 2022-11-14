package com.pichincha.apirest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pichincha.apirest.entity.Cuenta;
import com.pichincha.apirest.exception.ResourceNotFoundException;
import com.pichincha.apirest.repository.ClienteRepository;
import com.pichincha.apirest.repository.CuentaRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class CuentaController {

	@Autowired
	ClienteRepository clienteRepository;

	@Autowired
	CuentaRepository cuentaRepository;

	@GetMapping("/cliente/{id}/cuentas")
	public ResponseEntity<List<Cuenta>> buscarTodasCuentasPorCliente(@PathVariable("id") Integer id) {
		if (!clienteRepository.existsById(id)) {
			throw new ResourceNotFoundException("No existe el cliente con ID: " + id);
		}

		List<Cuenta> cuentas = cuentaRepository.findByClienteId(id);
		return new ResponseEntity<>(cuentas, HttpStatus.OK);
	}

	@GetMapping("/cuenta/{id}")
	public ResponseEntity<Cuenta> buscarCuenta(@PathVariable(value = "id") Integer id) {
		Cuenta cuenta = cuentaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la cuenta con ID: " + id));

		return new ResponseEntity<>(cuenta, HttpStatus.OK);
	}

	@PostMapping("/cliente/{id}/cuentas")
	public ResponseEntity<Cuenta> crearCuenta(@PathVariable(value = "id") Integer id,
			@RequestBody Cuenta cuentaRequest) {
		Cuenta cuenta = clienteRepository.findById(id).map(cliente -> {
			cuentaRequest.setCliente(cliente);
			return cuentaRepository.save(cuentaRequest);
		}).orElseThrow(() -> new ResourceNotFoundException("No existe el cliente con ID: " + id));

		return new ResponseEntity<>(cuenta, HttpStatus.CREATED);
	}

	@PutMapping("/cuenta/{id}")
	public ResponseEntity<Cuenta> actualizarCuenta(@PathVariable("id") Integer id, @RequestBody Cuenta cuentaRequest) {
		Cuenta cuenta = cuentaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cuenta con ID: " + id + "no existe."));

		cuenta.setEstado(cuentaRequest.getEstado());
		cuenta.setNumeroCuenta(cuentaRequest.getNumeroCuenta());
		cuenta.setSaldoInicial(cuentaRequest.getSaldoInicial());
		cuenta.setTipoCuenta(cuentaRequest.getTipoCuenta());

		return new ResponseEntity<>(cuentaRepository.save(cuenta), HttpStatus.OK);
	}

	@DeleteMapping("/cuenta/{id}")
	public ResponseEntity<HttpStatus> eliminarCuenta(@PathVariable("id") Integer id) {
		cuentaRepository.deleteById(id);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("/cliente/{id}/cuentas")
	public ResponseEntity<List<Cuenta>> eliminarTodasCuentasPorCliente(@PathVariable(value = "id") Integer id) {
		if (!clienteRepository.existsById(id)) {
			throw new ResourceNotFoundException("No existe el cliente con ID: " + id);
		}

		cuentaRepository.deleteByClienteId(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
