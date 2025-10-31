
package pe.grupoantonio.gestion.demo.controller;

import jakarta.validation.Valid;
import java.util.List;
import pe.grupoantonio.gestion.demo.model.Producto;
import pe.grupoantonio.gestion.demo.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.grupoantonio.gestion.demo.dto.ProductoDTO;

            @RestController
            @RequestMapping("/productos")
            public class ProductoController {

                private final ProductoRepository repo;

                public ProductoController(ProductoRepository repo){
                 this.repo=repo;
                }

                //Listar a todos//
                @GetMapping
                public List<Producto> listar(){
                return repo.findAll();
                }

                //Obtener//
                @PostMapping

                public ResponseEntity<Producto> crearProducto(@RequestBody @Valid ProductoDTO dto) {
    Producto producto = new Producto();
    producto.setNombre(dto.getNombre());
    producto.setDescripcion(dto.getDescripcion());
    producto.setPrecio(dto.getPrecio());
    
    
    Producto nuevo = repo.save(producto);
    return ResponseEntity.ok(nuevo);
}
                //Actualizar
          
                @PutMapping("/{id}")

                public ResponseEntity<Producto> actualizar (@PathVariable Long id, @RequestBody Producto datos){
                        return repo.findById(id).map(p -> {
                        p.setNombre(datos.getNombre());
                        p.setDescripcion(datos.getDescripcion());
                        p.setPrecio(datos.getPrecio());
                        Producto guardado = repo.save(p);
                        return ResponseEntity.ok(guardado);
                        }).orElse(ResponseEntity.notFound().build());
                }

                //Eliminar

                @DeleteMapping ("/{id}")

                public ResponseEntity<Void> eliminar(@PathVariable Long id){

                    if(repo.existsById(id)){
                        repo.deleteById(id);
                        return ResponseEntity.noContent().build();

                    }

                    return ResponseEntity.notFound().build();
                }
                
                // Obtener por ID
                @GetMapping("/{id}")
                public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
                return repo.findById(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
                }
 

                //Buscar nombre exacto

                @GetMapping("/buscar")
                public List<Producto> buscarPorNombre(@RequestParam String nombre){
                return repo.findByNombre(nombre);
                }

                //Buscar palabra en el nombre
                public List<Producto> buscarPorPalabra(@RequestParam String palabra){
                return repo.findByNombreContaining(palabra);}


                //Filtrar productos con precio mayor a X

                @GetMapping("/precio-mayor")

                public List<Producto> filtrarPorPrecioMayor(@RequestParam double mayor){
                return repo.findByPrecioGreaterThan(mayor);
                }

                //Filtrar producto entre rango de precios
                @GetMapping("/precio-rango")
                public List<Producto> filtrarPorRangoPrecio (@RequestParam double min,@RequestParam double max){
                return repo.findByPrecioBetween(min, max);}

            }
