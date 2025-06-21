package DAW.BrainbTestHub.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
            Model model,
            RedirectAttributes redirectAttributes) {

        IntentoCuestionario intento = intentoCuestionarioService.obtenerPorId(intentoId);
        Cuestionario cuestionario = intento.getCuestionario();

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

            redirectAttributes.addFlashAttribute("mensaje", "Intento guardado");
            return "redirect:/cuestionarios/resolver?cuestionarioId=" + cuestionario.getId();
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

            redirectAttributes.addFlashAttribute("mensaje", "Intento guardado");
            return "redirect:/cuestionarios/resolver?cuestionarioId=" + cuestionario.getId();
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
    public String verIntentosPorCuestionario(@PathVariable Long id, Model model,
            @AuthenticationPrincipal OidcUser principal, RedirectAttributes redirectAttributes) {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(id);
        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error",
                    "No tienes permisos para ver los intentos de este cuestionario.");
            return "redirect:/cuestionarios";
        }
        List<IntentoCuestionario> intentos = intentoCuestionarioService.obtenerIntentosPorCuestionario(id);

        model.addAttribute("cuestionario", cuestionario);
        model.addAttribute("intentos", intentos);
        return "cuestionarios/intentos";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/detalle/{id}")
    public String verDetalleIntento(@PathVariable Long id, Model model, @AuthenticationPrincipal OidcUser principal,
            RedirectAttributes redirectAttributes) {
        IntentoCuestionario intento = intentoCuestionarioService.obtenerPorId(id);
        Cuestionario cuestionario = intento.getCuestionario();
        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            redirectAttributes.addFlashAttribute("error",
                    "No tienes permisos para ver los intentos de este cuestionario.");
            return "redirect:/cuestionarios";
        }

        List<RespuestaUsuario> respuestas = respuestaUsuarioService.obtenerRespuestasPorIntento(id);

        model.addAttribute("intento", intento);
        model.addAttribute("respuestas", respuestas);
        return "cuestionarios/detalle-intento";
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarCuestionario(@PathVariable Long id, @AuthenticationPrincipal OidcUser principal) throws IOException {
        Cuestionario cuestionario = cuestionarioService.getCuestionarioById(id);
        String userId = principal.getSubject();
        boolean esPropietario = cuestionario.getUserId().equals(userId);

        if (!esPropietario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<IntentoCuestionario> intentos = intentoCuestionarioService.obtenerIntentosPorCuestionario(id);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet hoja = workbook.createSheet("Intentos");

            Row encabezado = hoja.createRow(0);
            encabezado.createCell(0).setCellValue("Correo");
            encabezado.createCell(1).setCellValue("Nombre");
            encabezado.createCell(2).setCellValue("Inicio");
            encabezado.createCell(3).setCellValue("Fin");
            encabezado.createCell(4).setCellValue("Puntaje");

            int filaNum = 1;
            for (IntentoCuestionario intento : intentos) {
                Row fila = hoja.createRow(filaNum++);
                fila.createCell(0).setCellValue(intento.getCorreo());
                fila.createCell(1).setCellValue(intento.getNombre());
                fila.createCell(2).setCellValue(intento.getFechaHoraInicio().toString());
                fila.createCell(3).setCellValue(intento.getFechaHoraFin().toString());
                fila.createCell(4).setCellValue(intento.getPuntaje());
            }

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            ByteArrayResource recurso = new ByteArrayResource(outStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cuestionario_" + id + ".xlsx\"")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(recurso.contentLength())
                    .body(recurso);
        }
    }
}
