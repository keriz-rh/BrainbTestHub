package DAW.BrainbTestHub.controller;
//https://copilot.microsoft.com/chats/djsCg5NsVnCtCZpvJEjTz
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import DAW.BrainbTestHub.model.RespuestaUsuario;
import DAW.BrainbTestHub.service.CuestionarioService;
import DAW.BrainbTestHub.service.IntentoCuestionarioService;
import DAW.BrainbTestHub.service.PreguntaService;
import DAW.BrainbTestHub.service.RespuestaService;

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

        List<Pregunta> preguntas = cuestionario.getPreguntas();
        preguntas.size(); // fuerza la carga si es LAZY
        model.addAttribute("preguntas", preguntas);

        model.addAttribute("intento", intento);
        model.addAttribute("preguntas", cuestionario.getPreguntas());
        model.addAttribute("tiempoDisponible", tiempoDisponible); // en minutos
        return "intentos/intento";
    }

    @PostMapping("/resolver/enviar")
    public String guardarRespuestas(@RequestParam("intentoId") Long intentoId,
            @RequestParam Map<String, String> respuestasMarcadas) {

        IntentoCuestionario intento = intentoCuestionarioService.obtenerPorId(intentoId);
        List<RespuestaUsuario> respuestas = new ArrayList<>();

        for (Map.Entry<String, String> entrada : respuestasMarcadas.entrySet()) {
            if (entrada.getKey().startsWith("respuestas[")) {
                Long idPregunta = Long.valueOf(entrada.getKey().replace("respuestas[", "").replace("]", ""));
                Long idRespuesta = Long.valueOf(entrada.getValue());

                Pregunta pregunta = preguntaService.getPreguntaById(idPregunta);
                Respuesta respuesta = respuestaService.getRespuestaById(idRespuesta);

                boolean correcta = respuesta.isEsCorrecta();

                RespuestaUsuario ru = new RespuestaUsuario();
                ru.setIntento(intento);
                ru.setPregunta(pregunta);
                ru.setRespuestaSeleccionada(respuesta);
                ru.setEsCorrecta(correcta);

                respuestas.add(ru);
            }
        }

        intento.setFechaHoraFin(LocalDateTime.now());
        intento.setRespuestasUsuario(respuestas);
        intentoCuestionarioService.guardarIntento(intento); // guarda también las respuestas

        return "redirect:/resolver/finalizado?id=" + intento.getId();
    }
}
