package com.pichincha.apirest.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pichincha.apirest.constants.Constantes;
import com.pichincha.apirest.dto.Reporte;
import com.pichincha.apirest.entity.Cuenta;
import com.pichincha.apirest.entity.Movimiento;
import com.pichincha.apirest.exception.ResourceNotFoundException;
import com.pichincha.apirest.repository.ClienteRepository;
import com.pichincha.apirest.repository.CuentaRepository;
import com.pichincha.apirest.repository.MovimientoRepository;
import com.pichincha.apirest.service.TransaccionService;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class MovimientoController {

	@Autowired
	ClienteRepository clienteRepository;

	@Autowired
	CuentaRepository cuentaRepository;

	@Autowired
	MovimientoRepository movimientoRepository;

	@Autowired
	TransaccionService transaccionService;

	@GetMapping("/cuenta/{id}/movimientos")
	public ResponseEntity<List<Movimiento>> buscarTodasMovimientosPorCuenta(@PathVariable("id") Integer id) {
		if (!cuentaRepository.existsById(id)) {
			throw new ResourceNotFoundException("No existe la cuenta con ID: " + id);
		}

		List<Movimiento> movimientos = movimientoRepository.findByCuentaId(id);
		return new ResponseEntity<>(movimientos, HttpStatus.OK);
	}

	@PostMapping("/cuenta/{id}/movimientos")
	public ResponseEntity<Movimiento> crearMovimiento(@PathVariable(value = "id") Integer id,
			@RequestBody Movimiento movimientoRequest) {
		Movimiento movimiento = cuentaRepository.findById(id).map(cuenta -> {
			if (movimientoRepository.findByCuentaId(cuenta.getId()).size() == 0) {
				validarDebitosPrimerMovimiento(movimientoRequest, cuenta);
			} else {
				List<Movimiento> movimientos = movimientoRepository.findByCuentaId(cuenta.getId());
				validarDebitosMovimientos(movimientos.get(movimientos.size() - 1), movimientoRequest);
			}
			movimientoRequest.setCuenta(cuenta);
			return movimientoRepository.save(movimientoRequest);
		}).orElseThrow(() -> new ResourceNotFoundException("No existe la cuenta con ID: " + id));

		return new ResponseEntity<>(movimiento, HttpStatus.CREATED);
	}

	private void validarDebitosPrimerMovimiento(Movimiento movimientoRequest, Cuenta cuenta) {
		switch (movimientoRequest.getTipoMovimiento()) {
		case "DEBITO":
			if (transaccionService.tieneMontoDisponile(cuenta.getSaldoInicial(), movimientoRequest.getValor())) {
				movimientoRequest.setFecha(new Date());
				movimientoRequest.setSaldo(cuenta.getSaldoInicial() - movimientoRequest.getValor());
			} else {
				throw new ResourceNotFoundException(Constantes.SALDO_NO_DISPONIBLE);
			}
			break;
		case "DEPOSITO":
			movimientoRequest.setFecha(new Date());
			movimientoRequest.setSaldo(cuenta.getSaldoInicial() + movimientoRequest.getValor());
		default:
			break;
		}
	}

	private void validarDebitosMovimientos(Movimiento ultimoMovimiento, Movimiento movimientoRequest) {
		switch (movimientoRequest.getTipoMovimiento()) {
		case "DEBITO":
			if (transaccionService.tieneMontoDisponile(ultimoMovimiento.getSaldo(), movimientoRequest.getValor())) {
				movimientoRequest.setFecha(new Date());
				movimientoRequest.setSaldo(ultimoMovimiento.getSaldo() - movimientoRequest.getValor());
			} else {
				throw new ResourceNotFoundException(Constantes.SALDO_NO_DISPONIBLE);
			}
			break;
		case "DEPOSITO":
			movimientoRequest.setFecha(new Date());
			movimientoRequest.setSaldo(ultimoMovimiento.getValor() + movimientoRequest.getValor());
		default:
			break;
		}
	}

	@PutMapping("/movimiento/{id}")
	public ResponseEntity<Movimiento> actualizarMovimiento(@PathVariable("id") Integer id,
			@RequestBody Movimiento movimientoRequest) {
		Movimiento movimiento = movimientoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Movimiento con ID: " + id + "no existe."));

		movimiento.setFecha(new Date());
		movimiento.setSaldo(movimientoRequest.getSaldo());
		movimiento.setTipoMovimiento(movimientoRequest.getTipoMovimiento());
		movimiento.setValor(movimientoRequest.getValor());

		return new ResponseEntity<>(movimientoRepository.save(movimiento), HttpStatus.OK);
	}

	@DeleteMapping("/movimiento/{id}")
	public ResponseEntity<HttpStatus> eliminarMovimiento(@PathVariable("id") Integer id) {
		movimientoRepository.deleteById(id);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("/cuenta/{id}/movimiento")
	public ResponseEntity<List<Movimiento>> eliminarTodosMovimientosPorCuenta(@PathVariable(value = "id") Integer id) {
		if (!cuentaRepository.existsById(id)) {
			throw new ResourceNotFoundException("No existe la cuenta con ID: " + id);
		}

		movimientoRepository.deleteByCuentaId(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/cliente/{id}/movimientos")
	public ResponseEntity<List<Movimiento>> buscarMovimientosPorCliente(@PathVariable("id") Integer id) {
		List<Cuenta> cuentas = cuentaRepository.findByClienteId(id);
		List<Movimiento> movimientos = new ArrayList<>();
		cuentas.forEach((final Cuenta cuenta) -> movimientos.addAll(cuenta.getMovimientos()));
		return new ResponseEntity<>(movimientos, HttpStatus.OK);
	}

	@GetMapping("/cliente/{id}/movimiento")
	public ResponseEntity<List<Reporte>> buscarMovimientosPorClienteFecha(@PathVariable("id") Integer id,
			@RequestParam("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		List<Cuenta> cuentas = cuentaRepository.findByClienteId(id);
		List<Reporte> listaReportes = new ArrayList<>();
		List<Movimiento> movimientos = new ArrayList<>();
		LocalDate localParam = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		cuentas.forEach((cuenta) -> movimientos.addAll(cuenta.getMovimientos()));
		movimientos.forEach((movimiento) -> {
			LocalDate local = movimiento.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (local.isEqual(localParam)) {
				Reporte reporte = new Reporte();
				reporte.setCliente(movimiento.getCuenta().getCliente().getNombre());
				reporte.setEstado(movimiento.getCuenta().getEstado());
				reporte.setFecha(local.toString());
				reporte.setMovimiento(movimiento.getValor());
				reporte.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
				reporte.setSaldoDisponible(movimiento.getSaldo());
				reporte.setSaldoInicial(movimiento.getCuenta().getSaldoInicial());
				reporte.setTipo(movimiento.getCuenta().getTipoCuenta());
				listaReportes.add(reporte);
			}
		});
		return new ResponseEntity<>(listaReportes, HttpStatus.OK);
	}
}
