package com.pichincha.apirest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pichincha.apirest.entity.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

	List<Movimiento> findByCuentaId(Integer cuentaId);

	@Transactional
	void deleteByCuentaId(Integer cuentaId);
}
