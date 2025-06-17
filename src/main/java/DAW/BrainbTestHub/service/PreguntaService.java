package DAW.BrainbTestHub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.repository.CuestionarioRepository;
import DAW.BrainbTestHub.repository.PreguntaRepository;

@Service
public class PreguntaService {
    
    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    public List<Pregunta> getAllPreguntas(){
        return preguntaRepository.findAll();
    }

    public Pregunta getPreguntaById(Long id){
        return preguntaRepository.findById(id).orElse(null);
    }

    public void savePregunta(Pregunta pregunta){
        preguntaRepository.save(pregunta);
    }

    public void deletePregunta(Long id){
        preguntaRepository.deleteById(id);
    }

    public List<Pregunta> obtenerPreguntasPorCuestionario(Long cuestionarioId) {
        Cuestionario cuestionario = cuestionarioRepository.findById(cuestionarioId).orElse(null);
        return (cuestionario != null) ? preguntaRepository.findByCuestionario(cuestionario) : List.of();
    }
}
