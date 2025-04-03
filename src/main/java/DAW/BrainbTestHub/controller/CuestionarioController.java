package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.service.CuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.Duration;

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
        // Validar que la fecha de inicio sea anterior a la fecha final
        if (cuestionario.getFechaInicio().isAfter(cuestionario.getFechaFinal())) {
            model.addAttribute("error", "La fecha de inicio debe ser anterior a la fecha final.");
            return "cuestionarios/formulario";
        }
    
        // Validar que la duración sea positiva
        if (cuestionario.getDuracion() <= 0) {
            model.addAttribute("error", "La duración debe ser un valor positivo.");
            return "cuestionarios/formulario";
        }
    
        // Validar que la duración no exceda el tiempo entre fechaInicio y fechaFinal
        long minutosEntreHoras = Duration.between(cuestionario.getFechaInicio(), cuestionario.getFechaFinal()).toMinutes();
    
        if (cuestionario.getDuracion() > minutosEntreHoras) {
            model.addAttribute("error", "La duración no puede ser mayor al tiempo entre la fecha de inicio y la fecha final.");
            return "cuestionarios/formulario";
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
