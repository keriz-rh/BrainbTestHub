package DAW.BrainbTestHub.repository;

import DAW.BrainbTestHub.model.IntentoCuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IntentoCuestionarioRepository extends JpaRepository<IntentoCuestionario, Long> {
    List<IntentoCuestionario> findByUserId(String userId);
    List<IntentoCuestionario> findByCuestionarioId(Long cuestionarioId);
}
