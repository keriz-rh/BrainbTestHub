package DAW.BrainbTestHub.service;

import DAW.BrainbTestHub.model.Usuario;
import DAW.BrainbTestHub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    
    public Usuario getUsuarioByNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre).orElse(null);
    }

    public Usuario saveUsuario(Usuario usuario) {
        // Validar correo duplicado para nuevos usuarios
        if(usuario.getId() == null || usuario.getId() == 0) {
            if(usuarioRepository.existsByCorreo(usuario.getCorreo())) {
                throw new IllegalStateException("El correo ya está registrado");
            }
            // Encriptar contraseña solo para usuarios nuevos
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        // Validar correo duplicado para ediciones
        else {
            Usuario usuarioExistente = usuarioRepository.findByCorreo(usuario.getCorreo())
                .orElse(null);
                
            if(usuarioExistente != null && !usuarioExistente.getId().equals(usuario.getId())) {
                throw new IllegalStateException("El correo ya está registrado por otro usuario");
            }
            
            // Si la contraseña está vacía en una edición, mantener la contraseña anterior
            if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
                Usuario usuarioActual = usuarioRepository.findById(usuario.getId()).orElse(null);
                if (usuarioActual != null) {
                    usuario.setContrasena(usuarioActual.getContrasena());
                }
            } else {
                // Si se proporciona una nueva contraseña, encriptarla
                usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            }
        }
        
        return usuarioRepository.save(usuario);
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
