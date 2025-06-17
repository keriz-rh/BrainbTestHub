package DAW.BrainbTestHub.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.model.IntentoCuestionario;
import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.model.Respuesta;
import DAW.BrainbTestHub.service.CuestionarioService;
import DAW.BrainbTestHub.service.IntentoCuestionarioService;
import DAW.BrainbTestHub.service.PreguntaService;
import DAW.BrainbTestHub.service.RespuestaService;
import DAW.BrainbTestHub.service.RespuestaUsuarioService;

@Controller
@RequestMapping("/intentos")
public class IntentoController {

    @Autowired
    private CuestionarioService cuestionarioService;

    @Autowired
    private IntentoCuestionarioService intentoCuestionarioService;

    @Autowired
    private RespuestaService respuestaService;

    @Autowired
    private PreguntaService preguntaService;

    @Autowired
    private RespuestaUsuarioService respuestaUsuarioService;

    @PostMapping("/resolver/iniciar")
    public String iniciarIntento(@RequestParam("cuestionarioId") Long cuestionarioId,
            @AuthenticationPrincipal OidcUser principal,
            Model model) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(cuestionarioId);
        String userId = principal.getSubject();

        LocalDateTime ahora = LocalDateTime.now();
        long tiempoDisponible = cuestionarioService.calcularTiempoDisponible(cuestionario, ahora);

        IntentoCuestionario intento = new IntentoCuestionario();
        intento.setCuestionario(cuestionario);
        intento.setUserId(userId);
        intento.setFechaHoraInicio(ahora);

        intento = intentoCuestionarioService.guardarIntento(intento);

        List<Pregunta> preguntas = preguntaService.obtenerPreguntasPorCuestionario(cuestionarioId);

        model.addAttribute("preguntas", preguntas);
        model.addAttribute("intento", intento);
        model.addAttribute("tiempoDisponible", tiempoDisponible); // en segundos
        return "intentos/intento";
    }

    @PostMapping("/resolver/guardar")
    public String guardarRespuesta(@RequestParam Long intentoId,
            @RequestParam Long preguntaId,
            @RequestParam Long respuestaSeleccionadaId,
            @RequestParam int index,
            Model model) {

        IntentoCuestionario intento = intentoCuestionarioService.obtenerPorId(intentoId);
        Pregunta pregunta = preguntaService.getPreguntaById(preguntaId);
        Respuesta respuesta = respuestaService.getRespuestaById(respuestaSeleccionadaId);

        respuestaUsuarioService.guardarRespuesta(intento, pregunta, respuesta);

        List<Pregunta> preguntas = preguntaService.obtenerPreguntasPorCuestionario(intento.getCuestionario().getId());
        int siguiente = index + 1;

        if (siguiente >= preguntas.size()) {
            intento.setFechaHoraFin(LocalDateTime.now());
            intentoCuestionarioService.guardarIntento(intento); // marca como finalizado
            return "redirect:/intentos/resumen?intentoId=" + intentoId;
        }

        model.addAttribute("pregunta", preguntas.get(siguiente));
        model.addAttribute("index", siguiente);
        model.addAttribute("total", preguntas.size());
        model.addAttribute("intento", intento);
        model.addAttribute("tiempoDisponible",
                cuestionarioService.calcularTiempoDisponible(intento.getCuestionario(), LocalDateTime.now()));

        return "intentos/intento";
    }
}
