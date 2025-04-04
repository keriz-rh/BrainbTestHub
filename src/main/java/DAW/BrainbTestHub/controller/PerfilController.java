package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Usuario;
import DAW.BrainbTestHub.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String verPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        String nombreUsuario = "";
        if (auth.getPrincipal() instanceof UserDetails) {
            nombreUsuario = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            nombreUsuario = auth.getName();
        }

        Usuario usuario = usuarioService.getUsuarioByNombre(nombreUsuario);
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "perfil/ver";
    }

    @GetMapping("/editar")
    public String editarPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        String nombreUsuario = "";
        if (auth.getPrincipal() instanceof UserDetails) {
            nombreUsuario = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            nombreUsuario = auth.getName();
        }

        Usuario usuario = usuarioService.getUsuarioByNombre(nombreUsuario);
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "perfil/editar";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String nombreUsuario = "";
            if (auth.getPrincipal() instanceof UserDetails) {
                nombreUsuario = ((UserDetails) auth.getPrincipal()).getUsername();
            } else {
                nombreUsuario = auth.getName();
            }

            Usuario usuarioActual = usuarioService.getUsuarioByNombre(nombreUsuario);
            if (usuarioActual == null || !usuarioActual.getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar este perfil");
                return "redirect:/perfil";
            }

            // Establecer la contraseña a null para que el servicio mantenga la contraseña existente
            usuario.setContrasena(null);
            
            // Detectar si cambió el nombre de usuario
            boolean cambioNombre = !usuarioActual.getNombre().equals(usuario.getNombre());
            
            usuarioService.saveUsuario(usuario);
            
            // Si cambió el nombre de usuario, cerrar sesión y redirigir al login
            if (cambioNombre) {
                SecurityContextHolder.clearContext();
                return "redirect:/login?nombreCambiado";
            }
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado con éxito");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }

    @GetMapping("/cambiarContrasena")
    public String cambiarContrasenaForm() {
        return "perfil/cambiarContrasena";
    }

    @PostMapping("/cambiarContrasena")
    public String cambiarContrasena(@RequestParam String contrasenaActual, 
                                   @RequestParam String nuevaContrasena, 
                                   @RequestParam String confirmarContrasena,
                                   RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String nombreUsuario = "";
            if (auth.getPrincipal() instanceof UserDetails) {
                nombreUsuario = ((UserDetails) auth.getPrincipal()).getUsername();
            } else {
                nombreUsuario = auth.getName();
            }

            Usuario usuario = usuarioService.getUsuarioByNombre(nombreUsuario);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/perfil/cambiarContrasena";
            }

            // Verificar que la contraseña actual sea correcta
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(contrasenaActual, usuario.getContrasena())) {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/perfil/cambiarContrasena";
            }

            // Verificar que las nuevas contraseñas coincidan
            if (!nuevaContrasena.equals(confirmarContrasena)) {
                redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden");
                return "redirect:/perfil/cambiarContrasena";
            }

            // Verificar que la nueva contraseña tenga al menos 6 caracteres
            if (nuevaContrasena.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/perfil/cambiarContrasena";
            }

            // Actualizar la contraseña
            usuario.setContrasena(nuevaContrasena);
            usuarioService.saveUsuario(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada con éxito");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/perfil/cambiarContrasena";
        }
    }

    @PostMapping("/eliminar")
    public String eliminarCuenta(RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String nombreUsuario = "";
            if (auth.getPrincipal() instanceof UserDetails) {
                nombreUsuario = ((UserDetails) auth.getPrincipal()).getUsername();
            } else {
                nombreUsuario = auth.getName();
            }

            Usuario usuario = usuarioService.getUsuarioByNombre(nombreUsuario);
            if (usuario != null) {
                usuarioService.deleteUsuario(usuario.getId());
            }
            
            SecurityContextHolder.clearContext();
            return "redirect:/login?eliminado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la cuenta: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
} 