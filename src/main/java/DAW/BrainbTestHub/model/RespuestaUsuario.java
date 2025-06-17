package DAW.BrainbTestHub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "respuestas_usuario")
public class RespuestaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "intento_id")
    private IntentoCuestionario intento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pregunta_id")
    private Pregunta pregunta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "respuesta_id")
    private Respuesta respuestaSeleccionada;

    @Column(nullable = false)
    private boolean esCorrecta;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IntentoCuestionario getIntento() { return intento; }
    public void setIntento(IntentoCuestionario intento) { this.intento = intento; }

    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }

    public Respuesta getRespuestaSeleccionada() { return respuestaSeleccionada; }
    public void setRespuestaSeleccionada(Respuesta respuestaSeleccionada) {
        this.respuestaSeleccionada = respuestaSeleccionada;
    }

    public boolean isEsCorrecta() { return esCorrecta; }
    public void setEsCorrecta(boolean esCorrecta) { this.esCorrecta = esCorrecta; }
}
