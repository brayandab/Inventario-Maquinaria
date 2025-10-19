package com.example.Correccion.farmacia.repository;
import com.example.Correccion.farmacia.entities.cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
    public interface PacienteRepository extends JpaRepository<cliente, Long> {
// Repositorio que permite acceder y gestionar los datos de pacientes desde la base de datos.
cliente findByUsuario_IdUsuario(Long idUsuario);

    Optional<cliente> findByUsuario_Correo(String correo);


    //Optional<Paciente> findByCorreo(String correo);
    }


