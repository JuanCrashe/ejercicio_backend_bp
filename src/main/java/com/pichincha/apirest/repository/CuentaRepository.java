package com.pichincha.apirest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pichincha.apirest.entity.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

	List<Cuenta> findByClienteId(Integer clienteId);

	@Transactional
	void deleteByClienteId(Integer clienteId);
}
