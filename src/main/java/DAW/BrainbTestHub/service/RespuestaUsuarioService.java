package DAW.BrainbTestHub.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import DAW.BrainbTestHub.model.IntentoCuestionario;
import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.model.Respuesta;
import DAW.BrainbTestHub.model.RespuestaUsuario;
import DAW.BrainbTestHub.repository.RespuestaUsuarioRepository;

@Service
public class RespuestaUsuarioService {

    @Autowired
    private RespuestaUsuarioRepository respuestaUsuarioRepository;

    public void guardarRespuesta(IntentoCuestionario intento, Pregunta pregunta, Respuesta respuesta) {

        if (respuestaUsuarioRepository.existsByIntentoIdAndPreguntaId(intento.getId(), pregunta.getId())) {
            return;
        }

        RespuestaUsuario ru = new RespuestaUsuario();
        ru.setIntento(intento);
        ru.setPregunta(pregunta);
        ru.setRespuestaSeleccionada(respuesta);
        if (respuesta == null) {
            ru.setEsCorrecta(false);
        } else {
            ru.setEsCorrecta(respuesta.isEsCorrecta());
        }

        respuestaUsuarioRepository.save(ru);
    }

    public List<RespuestaUsuario> obtenerRespuestasPorIntento(Long intentoId) {
        return respuestaUsuarioRepository.findByIntentoId(intentoId);
    }

    public Set<Long> obtenerIdsPreguntasRespondidas(Long intentoId) {
        List<RespuestaUsuario> respuestas = respuestaUsuarioRepository.findByIntentoId(intentoId);
        return respuestas.stream()
                .map(r -> r.getPregunta().getId())
                .collect(Collectors.toSet());
    }

}
