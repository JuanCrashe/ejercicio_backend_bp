package com.pichincha.apirest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pichincha.apirest.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

}
