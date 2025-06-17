package DAW.BrainbTestHub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.model.Respuesta;
import DAW.BrainbTestHub.repository.PreguntaRepository;
import DAW.BrainbTestHub.repository.RespuestaRepository;

@Service
public class RespuestaService {
    
    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    public List<Respuesta> getAllRespuestas(){
        return respuestaRepository.findAll();
    }

    public Respuesta getRespuestaById(Long id){
        return respuestaRepository.findById(id).orElse(null);
    }

    public void saveRespuesta(Respuesta respuesta){
        respuestaRepository.save(respuesta);
    }

    public void deleteRespuesta(Long id){
        respuestaRepository.deleteById(id);
    }

    public List<Respuesta> obtenerRespuestasPorPregunta(Long preguntaId) {
        Pregunta pregunta = preguntaRepository.findById(preguntaId).orElse(null);
        return (pregunta != null) ? respuestaRepository.findByPregunta(pregunta) : List.of();
    }
}
