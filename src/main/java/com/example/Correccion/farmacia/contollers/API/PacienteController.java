package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.dto.UsuarioPacienteCompraDTO;
import com.example.Correccion.farmacia.dto.UsuarioPacienteDTO;
import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.entities.cliente;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.CompraRepository;
import com.example.Correccion.farmacia.repository.PacienteRepository;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    /*
    * El restController sirve para las peticiones, osea las que interactuan con postman que
    * traen los valores y funcionan con formato JSON, se pone la ruta en un RequestMapping y se completa
    * esa ruta con lo que se pone en el  getMapping
    * */
    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CompraRepository compraRepository;

    //Con solo la ru del RequestMapping accede a este metodo que trae todos los paciente
    @GetMapping
    public ResponseEntity<List<cliente>> listarTodos() {
        return new ResponseEntity<>(pacienteRepository.findAll(), HttpStatus.OK);
    }
    //Trae un paciente por el id que este asociado
    @GetMapping("/{id}")
    public ResponseEntity<cliente> obtenerPacientePorId(@PathVariable Long id) {
        Optional<cliente> pacienteOptional = pacienteRepository.findById(id);
        return pacienteOptional
                .map(paciente -> new ResponseEntity<>(paciente, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Eliminar paciente y su usuario relacionado
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPaciente(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarPaciente(id);
        if (eliminado) {
            return ResponseEntity.ok("Paciente y usuario eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado.");
        }
    }
    //paciente detallado con compra y detalle de compra
    @GetMapping("/{id}/detalle")
    public ResponseEntity<UsuarioPacienteCompraDTO> obtenerDetallePaciente(@PathVariable Long id) {
        Optional<cliente> pacienteOptional = pacienteRepository.findById(id);

        if (pacienteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        cliente paciente = pacienteOptional.get();
        Usuario usuario = paciente.getUsuario();

        List<Compra> compras = compraRepository.findByCliente(paciente);

        UsuarioPacienteCompraDTO dto = new UsuarioPacienteCompraDTO();
        dto.setUsuario(usuario);


        return ResponseEntity.ok(dto);
    }


}
