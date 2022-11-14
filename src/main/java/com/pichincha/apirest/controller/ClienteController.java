package com.pichincha.apirest.controller;

import java.util.Optional;

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

import com.pichincha.apirest.entity.Cliente;
import com.pichincha.apirest.exception.ResourceNotFoundException;
import com.pichincha.apirest.repository.ClienteRepository;
import com.pichincha.apirest.repository.PersonaRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class ClienteController {

	@Autowired
	ClienteRepository clienteRepository;

	@Autowired
	PersonaRepository personaRepository;

	@GetMapping("/cliente/{id}")
	public ResponseEntity<Cliente> buscarCliente(@PathVariable("id") Integer id) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No se encontro el Cliente con ID: " + id));
		return new ResponseEntity<>(cliente, HttpStatus.OK);
	}

	@PostMapping("/cliente")
	public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
		try {
			clienteRepository.save(cliente);
			return new ResponseEntity<>(cliente, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/cliente/{id}")
	public ResponseEntity<Cliente> actualizarCliente(@PathVariable("id") Integer id, @RequestBody Cliente cliente) {
		Optional<Cliente> clienteActual = clienteRepository.findById(id);

		if (clienteActual.isPresent()) {
			Cliente clienteActualizado = clienteActual.get();
			clienteActualizado.setNombre(cliente.getNombre());
			clienteActualizado.setGenero(cliente.getGenero());
			clienteActualizado.setEdad(cliente.getEdad());
			clienteActualizado.setIdentificacion(cliente.getIdentificacion());
			clienteActualizado.setDireccion(cliente.getDireccion());
			clienteActualizado.setTelefono(cliente.getTelefono());
			clienteActualizado.setContrasena(cliente.getContrasena());
			clienteActualizado.setEstado(cliente.getEstado());

			return new ResponseEntity<>(clienteRepository.save(clienteActualizado), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/cliente/{id}")
	public ResponseEntity<HttpStatus> eliminarCliente(@PathVariable("id") Integer id) {
		try {
			clienteRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
