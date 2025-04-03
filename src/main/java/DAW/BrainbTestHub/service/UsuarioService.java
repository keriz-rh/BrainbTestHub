package DAW.BrainbTestHub.service;

import DAW.BrainbTestHub.model.Usuario;
import DAW.BrainbTestHub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario saveUsuario(Usuario usuario) {
        // Validar correo duplicado para nuevos usuarios
        if(usuario.getId() == null || usuario.getId() == 0) {
            if(usuarioRepository.existsByCorreo(usuario.getCorreo())) {
                throw new IllegalStateException("El correo ya está registrado");
            }
        }
        // Validar correo duplicado para ediciones
        else {
            Usuario usuarioExistente = usuarioRepository.findByCorreo(usuario.getCorreo())
                .orElse(null);
                
            if(usuarioExistente != null && !usuarioExistente.getId().equals(usuario.getId())) {
                throw new IllegalStateException("El correo ya está registrado por otro usuario");
            }
        }
        
        return usuarioRepository.save(usuario);
    }
      

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
