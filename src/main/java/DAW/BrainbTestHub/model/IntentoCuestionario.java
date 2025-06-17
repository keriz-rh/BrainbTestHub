package DAW.BrainbTestHub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "intentos_cuestionarios")
public class IntentoCuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId; // userId de Auth0

    @ManyToOne
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column
    private LocalDateTime fechaHoraFin;

    @Column
    private Integer puntaje;

    @OneToMany(mappedBy = "intento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespuestaUsuario> respuestasUsuario;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Cuestionario getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.cuestionario = cuestionario;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public List<RespuestaUsuario> getRespuestasUsuario() {
        return respuestasUsuario;
    }

    public void setRespuestasUsuario(List<RespuestaUsuario> respuestasUsuario) {
        this.respuestasUsuario = respuestasUsuario;
    }

}
