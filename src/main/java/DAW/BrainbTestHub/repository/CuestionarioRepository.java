package DAW.BrainbTestHub.repository;

import DAW.BrainbTestHub.model.Cuestionario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuestionarioRepository extends JpaRepository<Cuestionario, Long> {

    List<Cuestionario> findByUserId(String userId);
}
