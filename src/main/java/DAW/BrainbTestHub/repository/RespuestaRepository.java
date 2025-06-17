package DAW.BrainbTestHub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.model.Respuesta;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long>{
    List<Respuesta> findByPregunta(Pregunta pregunta);
}
