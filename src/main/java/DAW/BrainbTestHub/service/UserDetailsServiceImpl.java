package DAW.BrainbTestHub.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import DAW.BrainbTestHub.model.SecurityUser;
import DAW.BrainbTestHub.model.Usuario;
import DAW.BrainbTestHub.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UsuarioRepository usuarioRepository;
    
    @Override
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
        Usuario usuarioEncontrado = usuarioRepository.findByNombre(usuario)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new SecurityUser(usuarioEncontrado);
    }

} 