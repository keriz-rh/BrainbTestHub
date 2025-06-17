package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.model.Respuesta;
import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.service.RespuestaService;
import DAW.BrainbTestHub.service.PreguntaService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String mostrarRespuestas(@PathVariable Long preguntaId, Model model) {
        Pregunta pregunta = preguntaService.getPreguntaById(preguntaId);
        if (pregunta == null) {
            return "redirect:/preguntas";
        }

        List<Respuesta> respuestas = respuestaService.obtenerRespuestasPorPregunta(preguntaId);
        model.addAttribute("pregunta", pregunta);
        model.addAttribute("respuestas", respuestas);
        model.addAttribute("titulo", "Respuestas de la Pregunta: " + pregunta.getEnunciado());
        return "respuestas/lista";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/eliminar/{id}")
    public String eliminarRespuesta(@PathVariable Long id) {
        respuestaService.deleteRespuesta(id);
        return "redirect:/cuestionarios";
    }
}
