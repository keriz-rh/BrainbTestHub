package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.service.CuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cuestionarios")
public class CuestionarioController {

    @Autowired
    private CuestionarioService cuestionarioService;

    @GetMapping
    public String listarCuestionarios(Model model) {
        List<Cuestionario> cuestionarios = cuestionarioService.getAllCuestionarios();
        System.out.println("DEBUG - Cuestionarios encontrados: " + cuestionarios.size());
        cuestionarios.forEach(c -> System.out.println(c.getTitulo()));
        
        model.addAttribute("cuestionarios", cuestionarios);
        model.addAttribute("titulo", "Lista de Cuestionarios");
        return "cuestionarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cuestionario", new Cuestionario());
        model.addAttribute("titulo", "Crear Cuestionario");
        return "cuestionarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCuestionario(@ModelAttribute Cuestionario cuestionario, Model model) {
        String mensajeError = cuestionario.validar();
    
        if (mensajeError != null) {
            // Si hay errores, regresar al formulario con el mensaje
            model.addAttribute("cuestionario", cuestionario); // Mantener los datos ingresados por el usuario
            model.addAttribute("mensajeError", mensajeError); // Enviar el mensaje de error a la vista
            model.addAttribute("titulo", "Crear Cuestionario"); // Título de la vista
            return "cuestionarios/formulario"; // Volver al formulario
        }
        cuestionarioService.saveCuestionario(cuestionario);
        return "redirect:/cuestionarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(id);
        if (cuestionario != null) {
            model.addAttribute("cuestionario", cuestionario);
            model.addAttribute("titulo", "Editar Cuestionario");
            return "cuestionarios/formulario";
        }
        return "redirect:/cuestionarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCuestionario(@PathVariable Long id) {
        cuestionarioService.deleteCuestionario(id);
        return "redirect:/cuestionarios";
    }
}
