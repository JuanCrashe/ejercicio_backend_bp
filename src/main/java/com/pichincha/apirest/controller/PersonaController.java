package com.pichincha.apirest.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pichincha.apirest.entity.Persona;
import com.pichincha.apirest.repository.PersonaRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class PersonaController {

	@Autowired
	PersonaRepository personaRepository;

	@GetMapping("/persona/{id}")
	public ResponseEntity<Persona> buscarPersona(@PathVariable("id") Integer id) {
		Optional<Persona> persona = personaRepository.findById(id);

		if (persona.isPresent()) {
			return new ResponseEntity<>(persona.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
