package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.dto.ProductoCarritoDTO;
import com.example.Correccion.farmacia.entities.Producto;
import com.example.Correccion.farmacia.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository repository;

    public ProductoController(ProductoRepository repository) {
        this.repository = repository;
    }

    // 🔹 Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return repository.findAll();
    }

    // 🔹 Crear un nuevo producto (por JSON)
    @PostMapping("/crear")
    public Producto guardar(@RequestBody Producto producto) {
        return repository.save(producto);
    }

    // 🔹 Subir foto desde el explorador (archivo físico)
    @PostMapping("/subir-foto")
    public ResponseEntity<?> subirFoto(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "uploads";
            Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 🔹 URL completa para que el navegador pueda mostrarla
            String fileUrl = "http://localhost:8080/uploads/" + fileName;

            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al subir la foto: " + e.getMessage()));
        }
    }



    // 🔹 Subir o actualizar foto para un producto existente (Base64)
    @PostMapping("/subir-foto/{id}")
    public ResponseEntity<?> subirFotoExistente(@PathVariable Long id, @RequestParam("foto") MultipartFile archivo) {
        Optional<Producto> optionalProducto = repository.findById(id);
        if (optionalProducto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }

        try {
            Producto producto = optionalProducto.get();
            // Guardar como Base64 (si no se usa la carpeta)
            String imagenBase64 = Base64.getEncoder().encodeToString(archivo.getBytes());
            producto.setFoto(imagenBase64);
            repository.save(producto);
            return ResponseEntity.ok("Foto actualizada correctamente.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la imagen: " + e.getMessage());
        }
    }

    // 🔹 Buscar producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado."));
    }

    // 🔹 Actualizar producto
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return repository.findById(id)
                .map(existingProducto -> {
                    existingProducto.setNombre(producto.getNombre());
                    existingProducto.setDescripcion(producto.getDescripcion());
                    existingProducto.setProveedor(producto.getProveedor());
                    existingProducto.setLote(producto.getLote());
                    existingProducto.setStock(producto.getStock());
                    existingProducto.setPrecioUnitario(producto.getPrecioUnitario());
                    existingProducto.setFoto(producto.getFoto());
                    return repository.save(existingProducto);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // 🔹 Actualizar stock
    @PutMapping("/actualizar-stock-fecha/{id}")
    public ResponseEntity<?> actualizarStockYFecha(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Producto> optionalProducto = repository.findById(id);
        if (optionalProducto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }

        Producto producto = optionalProducto.get();

        try {
            if (updates.containsKey("stock")) {
                Object stockObj = updates.get("stock");
                int stock = (stockObj instanceof Number)
                        ? ((Number) stockObj).intValue()
                        : Integer.parseInt(stockObj.toString());
                producto.setStock(stock);
            }

            Producto actualizado = repository.save(producto);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos inválidos: " + e.getMessage());
        }
    }

    // 🔹 Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Producto eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }
    }

    // 🔹 Verificar stock
    @PostMapping("/verificar-stock")
    public ResponseEntity<?> verificarStock(@RequestBody List<ProductoCarritoDTO> productos) {
        for (ProductoCarritoDTO dto : productos) {
            Optional<Producto> productoOpt = repository.findById(dto.getId());

            if (productoOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Producto con ID " + dto.getId() + " no encontrado."));
            }

            Producto producto = productoOpt.get();

            if (producto.getStock() < dto.getCantidad()) {
                return ResponseEntity.status(400)
                        .body(Map.of("error", "Stock insuficiente para el producto: " + producto.getNombre()));
            }
        }

        return ResponseEntity.ok(Map.of("mensaje", "Todos los productos tienen stock suficiente."));
    }
}
