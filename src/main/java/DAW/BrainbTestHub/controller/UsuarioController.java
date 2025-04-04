package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Usuario;
import DAW.BrainbTestHub.service.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("titulo", "Lista de Usuarios");
        return "usuarios/lista";
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("titulo", "Nuevo Usuario");
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute Usuario usuario, 
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de Usuario");
            return "usuarios/formulario";
        }
        try {
            usuarioService.saveUsuario(usuario);
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "usuarios/formulario";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "usuarios/lista";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Editar Usuario");
        return "usuarios/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, Model model) {
        usuarioService.deleteUsuario(id);
        model.addAttribute("mensaje", "Usuario eliminado exitosamente");
        return "redirect:/usuarios";
    }
}
