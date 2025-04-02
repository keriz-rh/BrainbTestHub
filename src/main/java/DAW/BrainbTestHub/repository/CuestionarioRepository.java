package DAW.BrainbTestHub.repository;

import DAW.BrainbTestHub.model.Cuestionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuestionarioRepository extends JpaRepository<Cuestionario, Long> {
}
