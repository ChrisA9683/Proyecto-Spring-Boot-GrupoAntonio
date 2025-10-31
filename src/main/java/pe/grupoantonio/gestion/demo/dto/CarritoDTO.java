
package pe.grupoantonio.gestion.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data

public class CarritoDTO {
    private Long id;
    private LocalDateTime fechaCreacion;
    private String estado;
    private Long usuarioId;
    
    // Solo mostramos el id del usuario en lugar de todo el objeto Usuario

}


