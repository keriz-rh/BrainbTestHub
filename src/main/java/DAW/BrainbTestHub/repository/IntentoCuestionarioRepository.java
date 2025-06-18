package DAW.BrainbTestHub.repository;

import DAW.BrainbTestHub.model.IntentoCuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IntentoCuestionarioRepository extends JpaRepository<IntentoCuestionario, Long> {
    List<IntentoCuestionario> findByUserId(String userId);
    List<IntentoCuestionario> findByCuestionarioId(Long cuestionarioId);
    Optional<IntentoCuestionario> findByCuestionarioIdAndUserId(Long cuestionarioId, String userId);
}
