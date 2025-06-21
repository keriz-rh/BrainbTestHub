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

import java.time.Duration;
import java.util.List;

@Controller
@RequestMapping("/cuestionarios")
public class CuestionarioController {

    @Autowired
    private CuestionarioService cuestionarioService;

    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public String listarCuestionarios(Model model, @AuthenticationPrincipal OidcUser principal) {
        List<Cuestionario> cuestionarios = cuestionarioService.getCuestionariosPorUserId(principal.getSubject());
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
    public String guardarCuestionario(@ModelAttribute Cuestionario form,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal OidcUser principal) {
        Cuestionario original = (form.getId() != null)
                ? cuestionarioService.getCuestionarioById(form.getId())
                : new Cuestionario();

        if (form.getHoraInicio().isAfter(form.getHoraFin())) {
            model.addAttribute("error", "La hora de inicio debe ser antes de la hora de finalización.");
            model.addAttribute("cuestionario", form);
            model.addAttribute("titulo", original.getId() == null ? "Crear Cuestionario" : "Editar Cuestionario");
            return "cuestionarios/formulario";
        }

        long duracionCalculada = Duration.between(form.getHoraInicio(), form.getHoraFin()).toMinutes();
        if (form.getDuracion() > duracionCalculada || form.getDuracion() <= 0) {
            model.addAttribute("error",
                    "La duración debe ser mayor a 0 y menor o igual al tiempo entre inicio y finalización.");
            model.addAttribute("cuestionario", form);
            model.addAttribute("titulo", original.getId() == null ? "Crear Cuestionario" : "Editar Cuestionario");
            return "cuestionarios/formulario";
        }

        // Solo se actualizan estos campos
        original.setTitulo(form.getTitulo());
        original.setDescripcion(form.getDescripcion());
        original.setFecha(form.getFecha());
        original.setHoraInicio(form.getHoraInicio());
        original.setHoraFin(form.getHoraFin());
        original.setDuracion(form.getDuracion());
        original.setUserId(principal.getSubject());

        cuestionarioService.saveCuestionario(original);
        redirectAttributes.addFlashAttribute("mensaje", "El cuestionario se ha guardado correctamente");
        return "redirect:/cuestionarios";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(id);

        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar este cuestionario.");
            return "redirect:/cuestionarios";
        }

        if (cuestionario != null) {
            model.addAttribute("cuestionario", cuestionario);
            model.addAttribute("titulo", "Editar Cuestionario");
            return "cuestionarios/formulario";
        }
        return "redirect:/cuestionarios";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/eliminar/{id}")
    public String eliminarCuestionario(@PathVariable Long id, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(id);

        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar este cuestionario.");
            return "redirect:/cuestionarios";
        }
        
        cuestionarioService.deleteCuestionario(id);
        return "redirect:/cuestionarios";
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    @GetMapping("/elegir")
    public String mostrarFormularioEleccion() {
        return "cuestionarios/resolver"; // Vista con el campo para ingresar el ID
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
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
