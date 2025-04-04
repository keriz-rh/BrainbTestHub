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
        if (cuestionario.getHoraInicio().isAfter(cuestionario.getHoraFin())) {
            model.addAttribute("error", "La hora de inicio debe ser antes de la hora de finalización.");
            model.addAttribute("cuestionario", cuestionario);
            model.addAttribute("titulo", "Crear Cuestionario");
            return "cuestionarios/formulario";
        }

        int duracionCalculada = java.time.Duration.between(cuestionario.getHoraInicio(), cuestionario.getHoraFin())
                .toMinutesPart();
        if (cuestionario.getDuracion() > duracionCalculada || cuestionario.getDuracion() <= 0) {
            model.addAttribute("error",
                    "La duración debe ser mayor a 0 y menor o igual al tiempo entre inicio y finalización.");
            model.addAttribute("cuestionario", cuestionario);
            model.addAttribute("titulo", "Crear Cuestionario");
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
