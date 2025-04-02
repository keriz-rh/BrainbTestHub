package DAW.BrainbTestHub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe ser válido")
    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = true)  // Si no es obligatorio, quita `required` del HTML
    private String carnet;

    @NotNull(message = "El rol no puede estar vacío")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}

enum Rol {
    DOCENTE,
    ESTUDIANTE,
    INVITADO
}