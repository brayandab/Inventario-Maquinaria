package com.example.Correccion.farmacia.repository;

import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.entities.cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByCliente(cliente paciente);
}
