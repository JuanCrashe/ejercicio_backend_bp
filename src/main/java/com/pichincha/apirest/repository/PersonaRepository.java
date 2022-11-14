package com.pichincha.apirest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pichincha.apirest.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {

}
