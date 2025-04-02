package DAW.BrainbTestHub.repository;

import DAW.BrainbTestHub.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCorreo(String correo);
    Optional<Usuario> findByCorreo(String correo);
}
