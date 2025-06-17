package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.service.CuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Controller
@RequestMapping("/cuestionarios")
public class CuestionarioController {

    @Autowired
    private CuestionarioService cuestionarioService;

    @GetMapping
    public String listarCuestionarios(Model model, @AuthenticationPrincipal OidcUser principal) {
        List<Cuestionario> cuestionarios = cuestionarioService.getCuestionariosPorUserId(principal.getSubject());
        System.out.println("DEBUG - Cuestionarios encontrados: " + cuestionarios.size());
        cuestionarios.forEach(c -> System.out.println(c.getTitulo()));

        model.addAttribute("cuestionarios", cuestionarios);
        model.addAttribute("titulo", "Lista de Cuestionarios");
        return "cuestionarios/lista";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cuestionario", new Cuestionario());
        model.addAttribute("titulo", "Crear Cuestionario");
        return "cuestionarios/formulario";
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/guardar")
    public String guardarCuestionario(@ModelAttribute Cuestionario cuestionario, Model model,
            RedirectAttributes redirectAttributes, @AuthenticationPrincipal OidcUser principal) {
        if (cuestionario.getHoraInicio().isAfter(cuestionario.getHoraFin())) {
            model.addAttribute("error", "La hora de inicio debe ser antes de la hora de finalización.");
            model.addAttribute("cuestionario", cuestionario);
            model.addAttribute("titulo", "Crear Cuestionario");
            return "cuestionarios/formulario";
        }

        long duracionCalculada = java.time.Duration.between(cuestionario.getHoraInicio(), cuestionario.getHoraFin())
                .toMinutes();

        if (cuestionario.getDuracion() > duracionCalculada || cuestionario.getDuracion() <= 0) {
            model.addAttribute("error",
                    "La duración debe ser mayor a 0 y menor o igual al tiempo entre inicio y finalización.");
            model.addAttribute("cuestionario", cuestionario);
            model.addAttribute("titulo", "Crear Cuestionario");
            return "cuestionarios/formulario";
        }

        cuestionario.setUserId(principal.getSubject());
        cuestionarioService.saveCuestionario(cuestionario);
        redirectAttributes.addFlashAttribute("mensaje", "El cuestionario se ha guardado correctamente");
        return "redirect:/cuestionarios";
    }

    @PreAuthorize("hasRole('admin')")
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

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/eliminar/{id}")
    public String eliminarCuestionario(@PathVariable Long id) {
        cuestionarioService.deleteCuestionario(id);
        return "redirect:/cuestionarios";
    }

    @GetMapping("/elegir")
    public String mostrarFormularioEleccion() {
        return "cuestionarios/resolver"; // Vista con el campo para ingresar el ID
    }

    @GetMapping("/resolver")
    public String mostrarCuestionario(@RequestParam("cuestionarioId") Long id,
            Model model) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(id);

        if (cuestionario == null) {
            model.addAttribute("error", "No se encontró ningún cuestionario con ese ID.");
            return "cuestionarios/resolver";
        }

        boolean estaDisponible = cuestionarioService.estaDisponibleParaResolver(cuestionario);
        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("estaDisponible", estaDisponible);

        return "cuestionarios/informacion";
    }

}
