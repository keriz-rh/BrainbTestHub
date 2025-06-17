package DAW.BrainbTestHub.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.model.IntentoCuestionario;
import DAW.BrainbTestHub.model.Pregunta;
import DAW.BrainbTestHub.model.Respuesta;
import DAW.BrainbTestHub.model.RespuestaUsuario;
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
            Model model, RedirectAttributes redirectAttributes) {
        String userId = principal.getSubject();
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(cuestionarioId);

        Optional<IntentoCuestionario> intentoExistente = intentoCuestionarioService
                .buscarIntentoExistente(cuestionarioId, userId);

        IntentoCuestionario intento;
        if (intentoExistente.isPresent()) {
            intento = intentoExistente.get();
        } else {
            intento = new IntentoCuestionario();
            intento.setCuestionario(cuestionario);
            intento.setUserId(userId);
            intento.setFechaHoraInicio(LocalDateTime.now());
            intento.setCorreo(principal.getEmail());
            intento.setNombre(principal.getFullName());
            intento = intentoCuestionarioService.guardarIntento(intento);
        }

        long tiempoDisponible = cuestionarioService.calcularTiempoRestante(intento, LocalDateTime.now());

        List<Pregunta> todas = preguntaService.obtenerPreguntasPorCuestionario(cuestionarioId);
        Set<Long> respondidas = respuestaUsuarioService.obtenerIdsPreguntasRespondidas(intento.getId());

        List<Pregunta> pendientes = todas.stream()
                .filter(p -> !respondidas.contains(p.getId()))
                .toList();

        if (pendientes.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Ya respondiste todas las preguntas de este cuestionario.");
            return "redirect:/cuestionarios/resolver?cuestionarioId=" + cuestionarioId;
        }

        if (tiempoDisponible <= 0) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "El tiempo para resolver este cuestionario ya ha finalizado.");
            return "redirect:/cuestionarios/resolver?cuestionarioId=" + cuestionarioId;
        }

        model.addAttribute("pregunta", pendientes.get(0));
        model.addAttribute("index", todas.indexOf(pendientes.get(0)));
        model.addAttribute("total", todas.size());
        model.addAttribute("intento", intento);
        model.addAttribute("tiempoDisponible", tiempoDisponible);

        return "intentos/intento";
    }

    @PostMapping("/resolver/guardar")
    public String guardarRespuesta(@RequestParam Long intentoId,
            @RequestParam Long preguntaId,
            @RequestParam(required = false) Long respuestaSeleccionadaId,
            @RequestParam int index,
            @RequestParam(required = false) Boolean tiempoAgotado,
            Model model) {

        IntentoCuestionario intento = intentoCuestionarioService.obtenerPorId(intentoId);

        if (Boolean.TRUE.equals(tiempoAgotado)) {

            intento.setFechaHoraFin(LocalDateTime.now());

            List<Pregunta> todas = preguntaService.obtenerPreguntasPorCuestionario(intento.getCuestionario().getId());

            Set<Long> respondidas = respuestaUsuarioService.obtenerIdsPreguntasRespondidas(intentoId);

            // Guardar respuestas vacias para preguntas no respondidas
            for (Pregunta p : todas) {
                if (!respondidas.contains(p.getId())) {
                    respuestaUsuarioService.guardarRespuesta(intento, p, null);
                }
            }

            // Calcular puntaje
            List<RespuestaUsuario> respuestas = respuestaUsuarioService.obtenerRespuestasPorIntento(intentoId);
            int correctas = (int) respuestas.stream()
                    .filter(r -> Boolean.TRUE.equals(r.isEsCorrecta()))
                    .count();
            intento.setPuntaje(correctas);

            intentoCuestionarioService.guardarIntento(intento);

            return "redirect:/intentos/detalle/" + intentoId;
        }

        Pregunta pregunta = preguntaService.getPreguntaById(preguntaId);
        Respuesta respuesta = respuestaService.getRespuestaById(respuestaSeleccionadaId);

        respuestaUsuarioService.guardarRespuesta(intento, pregunta, respuesta);

        List<Pregunta> preguntas = preguntaService.obtenerPreguntasPorCuestionario(intento.getCuestionario().getId());
        int siguiente = index + 1;

        if (siguiente >= preguntas.size()) {
            intento.setFechaHoraFin(LocalDateTime.now());

            List<RespuestaUsuario> respuestas = respuestaUsuarioService.obtenerRespuestasPorIntento(intentoId);
            int correctas = (int) respuestas.stream()
                    .filter(r -> Boolean.TRUE.equals(r.isEsCorrecta()))
                    .count();
            intento.setPuntaje(correctas);

            intentoCuestionarioService.guardarIntento(intento);

            return "redirect:/intentos/detalle/" + intentoId;
        }

        model.addAttribute("pregunta", preguntas.get(siguiente));
        model.addAttribute("index", siguiente);
        model.addAttribute("total", preguntas.size());
        model.addAttribute("intento", intento);
        model.addAttribute("tiempoDisponible",
                cuestionarioService.calcularTiempoRestante(intento, LocalDateTime.now()));

        return "intentos/intento";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/cuestionario/{id}")
    public String verIntentosPorCuestionario(@PathVariable Long id, Model model) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(id);
        List<IntentoCuestionario> intentos = intentoCuestionarioService.obtenerIntentosPorCuestionario(id);

        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("intentos", intentos);
        return "cuestionarios/intentos";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/detalle/{id}")
    public String verDetalleIntento(@PathVariable Long id, Model model) {
        IntentoCuestionario intento = intentoCuestionarioService.obtenerPorId(id);
        List<RespuestaUsuario> respuestas = respuestaUsuarioService.obtenerRespuestasPorIntento(id);

        model.addAttribute("intento", intento);
        model.addAttribute("respuestas", respuestas);
        return "cuestionarios/detalle-intento";
    }

}
