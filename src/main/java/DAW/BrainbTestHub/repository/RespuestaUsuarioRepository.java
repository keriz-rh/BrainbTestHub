package DAW.BrainbTestHub.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DAW.BrainbTestHub.model.RespuestaUsuario;

@Repository
public interface RespuestaUsuarioRepository extends JpaRepository<RespuestaUsuario, Long> {
    List<RespuestaUsuario> findByIntentoId(Long intentoId);
    boolean existsByIntentoIdAndPreguntaId(Long intentoId, Long preguntaId);
}

