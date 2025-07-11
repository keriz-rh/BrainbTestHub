package DAW.BrainbTestHub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.model.Pregunta;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long>{
 
    @EntityGraph(attributePaths = {"respuestas"})
    List<Pregunta> findByCuestionario(Cuestionario cuestionario);

}
