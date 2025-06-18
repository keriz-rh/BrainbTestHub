package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.model.Respuesta;
import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.service.PreguntaService;
import DAW.BrainbTestHub.service.CuestionarioService;
import DAW.BrainbTestHub.service.RespuestaService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/preguntas")
public class PreguntaController {

    private final PreguntaService preguntaService;
    private final CuestionarioService cuestionarioService;
    private final RespuestaService respuestaService;

    public PreguntaController(PreguntaService preguntaService, CuestionarioService cuestionarioService,
            RespuestaService respuestaService) {
        this.preguntaService = preguntaService;
        this.cuestionarioService = cuestionarioService;
        this.respuestaService = respuestaService;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/nueva/{cuestionarioId}")
    public String mostrarFormularioNuevaPregunta(@PathVariable Long cuestionarioId, Model model, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(cuestionarioId);

        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar este cuestionario.");
            return "redirect:/cuestionarios";
        }
        if (cuestionario == null) {
            return "redirect:/cuestionarios";
        }

        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("pregunta", new Pregunta());
        model.addAttribute("respuestas", List.of(new Respuesta(), new Respuesta(), new Respuesta(), new Respuesta())); // 4
                                                                                                                       // respuestas
                                                                                                                       // por
                                                                                                                       // defecto
        model.addAttribute("titulo", "Agregar Pregunta y Respuestas");
        return "preguntas/formulario";
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/guardar")
    public String guardarPregunta(@ModelAttribute Pregunta pregunta, @RequestParam Long cuestionarioId,
            @RequestParam List<String> textos, @RequestParam int correctaIndex) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(cuestionarioId);
        if (cuestionario == null) {
            return "redirect:/cuestionarios";
        }

        pregunta.setCuestionario(cuestionario);
        preguntaService.savePregunta(pregunta);

        for (int i = 0; i < textos.size(); i++) {
            Respuesta respuesta = new Respuesta();
            respuesta.setTexto(textos.get(i));
            respuesta.setEsCorrecta(i == correctaIndex); // Solo una respuesta será correcta
            respuesta.setPregunta(pregunta);
            respuestaService.saveRespuesta(respuesta);
        }

        return "redirect:/preguntas/"+cuestionarioId;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/{cuestionarioId}")
    public String mostrarPreguntas(@PathVariable Long cuestionarioId, Model model, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(cuestionarioId);
        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para ver las preguntas de este cuestionario.");
            return "redirect:/cuestionarios";
        }
        if (cuestionario == null) {
            return "redirect:/cuestionarios";
        }

        List<Pregunta> preguntas = preguntaService.obtenerPreguntasPorCuestionario(cuestionarioId);
        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("titulo", "Preguntas del Cuestionario: " + cuestionario.getTitulo());
        return "preguntas/lista";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Pregunta pregunta = preguntaService.getPreguntaById(id);
        Cuestionario cuestionario = pregunta.getCuestionario();
        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar las preguntas de este cuestionario.");
            return "redirect:/cuestionarios";
        }
        if (pregunta == null) {
            return "redirect:/cuestionarios";
        }

        List<Respuesta> respuestas = respuestaService.obtenerRespuestasPorPregunta(id);

        model.addAttribute("pregunta", pregunta);
        model.addAttribute("respuestas", respuestas);
        model.addAttribute("titulo", "Editar Pregunta");
        return "preguntas/editar";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/eliminar/{id}")
    public String eliminarPregunta(@PathVariable Long id, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Pregunta pregunta = preguntaService.getPreguntaById(id);
        Cuestionario cuestionario = pregunta.getCuestionario();
        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar las preguntas de este cuestionario.");
            return "redirect:/cuestionarios";
        }
        preguntaService.deletePregunta(id);
        return "redirect:/preguntas/"+cuestionario.getId();
    }
}
