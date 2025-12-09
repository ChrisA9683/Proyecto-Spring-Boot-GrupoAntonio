package pe.grupoantonio.gestion.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter 
@Setter 
@NoArgsConstructor
@Table(name = "usuario") 
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "El nombre es obligatorio")
    private String nombre;

    @Column(nullable = false, unique = true)
    @Email(message = "Debe ingresar un correo electrónico válido")
    @NotNull(message = "El correo electrónico es obligatorio")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "La contraseña es obligatoria")
    private String password;

    // ✅ CORRECCIÓN CRÍTICA: EAGER carga el rol inmediatamente para evitar fallos de sesión.
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "id_rol", nullable = false) 
    @ToString.Exclude 
    private Rol rol;
    
    @Transient  
    private String confirmPassword;
}