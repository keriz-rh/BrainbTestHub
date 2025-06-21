package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Respuesta;
import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.service.RespuestaService;
import DAW.BrainbTestHub.service.PreguntaService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/respuestas")
public class RespuestaController {

    private final RespuestaService respuestaService;
    private final PreguntaService preguntaService;

    public RespuestaController(RespuestaService respuestaService, PreguntaService preguntaService) {
        this.respuestaService = respuestaService;
        this.preguntaService = preguntaService;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/{preguntaId}")
    public String mostrarRespuestas(@PathVariable Long preguntaId, Model model, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Pregunta pregunta = preguntaService.getPreguntaById(preguntaId);

        if (pregunta == null) {
            return "redirect:/preguntas";
        }

        Cuestionario cuestionario = pregunta.getCuestionario();

        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para ver las respuestas de este cuestionario.");
            return "redirect:/cuestionarios";
        }

        List<Respuesta> respuestas = respuestaService.obtenerRespuestasPorPregunta(preguntaId);
        model.addAttribute("pregunta", pregunta);
        model.addAttribute("respuestas", respuestas);
        model.addAttribute("titulo", "Respuestas de la Pregunta: " + pregunta.getEnunciado());
        return "respuestas/lista";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/eliminar/{id}")
    public String eliminarRespuesta(@PathVariable Long id, @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Respuesta respuesta = respuestaService.getRespuestaById(id);
        Pregunta pregunta = respuesta.getPregunta();
        Cuestionario cuestionario = pregunta.getCuestionario();
        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar las respuestas de este cuestionario.");
            return "redirect:/cuestionarios";
        }
        respuestaService.deleteRespuesta(id);
        return "redirect:/preguntas/editar/"+pregunta.getId();
    }
}
