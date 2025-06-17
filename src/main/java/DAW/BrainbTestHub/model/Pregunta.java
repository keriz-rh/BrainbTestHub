package DAW.BrainbTestHub.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String enunciado;

    @ManyToOne
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Respuesta> respuestas;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public Cuestionario getCuestionario() { return cuestionario; }
    public void setCuestionario(Cuestionario cuestionario) { this.cuestionario = cuestionario; }

    public List<Respuesta> getRespuestas() { return respuestas; }
    public void setRespuestas(List<Respuesta> respuestas) { this.respuestas = respuestas; }
}
