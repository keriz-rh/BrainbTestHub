package DAW.BrainbTestHub.service;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.model.IntentoCuestionario;
import DAW.BrainbTestHub.repository.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DAW.BrainbTestHub.model.Pregunta;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CuestionarioService {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    public List<Cuestionario> getAllCuestionarios() {
        return cuestionarioRepository.findAll();
    }

    public Cuestionario getCuestionarioById(Long id) {
        return cuestionarioRepository.findById(id).orElse(null);
    }

    public void saveCuestionario(Cuestionario cuestionario) {
        cuestionarioRepository.save(cuestionario);
    }

    public void deleteCuestionario(Long id) {
        cuestionarioRepository.deleteById(id);
    }

    public List<Pregunta> obtenerPreguntasPorCuestionario(Long idCuestionario) {
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario).orElse(null);
        return (cuestionario != null) ? cuestionario.getPreguntas() : List.of();
    }

    public List<Cuestionario> getCuestionariosPorUserId(String userId) {
        return cuestionarioRepository.findByUserId(userId);
    }

    public boolean estaDisponibleParaResolver(Cuestionario cuestionario) {
        LocalDateTime ahora = LocalDateTime.now();

        LocalDate fecha = cuestionario.getFecha();
        LocalTime inicio = cuestionario.getHoraInicio();
        LocalTime fin = cuestionario.getHoraFin();

        // Combina fecha con hora para comparaciones
        LocalDateTime inicioPrueba = LocalDateTime.of(fecha, inicio);
        LocalDateTime finPrueba = LocalDateTime.of(fecha, fin);

        return ahora.isAfter(inicioPrueba) && ahora.isBefore(finPrueba);
    }

    public long calcularTiempoRestante(IntentoCuestionario intento, LocalDateTime ahora) {
        LocalDateTime inicio = intento.getFechaHoraInicio();
        long segundosTranscurridos = Duration.between(inicio, ahora).getSeconds();

        long totalPermitido = Math.min(
                Duration.between(inicio, LocalDateTime.of(
                        intento.getCuestionario().getFecha(),
                        intento.getCuestionario().getHoraFin())).getSeconds(),
                intento.getCuestionario().getDuracion() * 60);

        long restante = totalPermitido - segundosTranscurridos;
        return Math.max(restante, 0);
    }

    /**
     * Verifica si un cuestionario está activo (en su ventana de tiempo)
     */
    public boolean estaActivo(Cuestionario cuestionario) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate fecha = cuestionario.getFecha();
        LocalTime inicio = cuestionario.getHoraInicio();
        LocalTime fin = cuestionario.getHoraFin();

        LocalDateTime inicioPrueba = LocalDateTime.of(fecha, inicio);
        LocalDateTime finPrueba = LocalDateTime.of(fecha, fin);

        return ahora.isAfter(inicioPrueba) && ahora.isBefore(finPrueba);
    }

    /**
     * Verifica si un cuestionario ha finalizado (pasó su hora de fin)
     */
    public boolean haFinalizado(Cuestionario cuestionario) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate fecha = cuestionario.getFecha();
        LocalTime fin = cuestionario.getHoraFin();

        LocalDateTime finPrueba = LocalDateTime.of(fecha, fin);
        return ahora.isAfter(finPrueba);
    }

    /**
     * Verifica si un cuestionario se puede editar (no está activo ni finalizado)
     */
    public boolean sePuedeEditar(Cuestionario cuestionario) {
        return !estaActivo(cuestionario) && !haFinalizado(cuestionario);
    }

    /**
     * Verifica si un cuestionario ha comenzado (ya pasó su hora de inicio)
     */
    public boolean haComenzado(Cuestionario cuestionario) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate fecha = cuestionario.getFecha();
        LocalTime inicio = cuestionario.getHoraInicio();

        LocalDateTime inicioPrueba = LocalDateTime.of(fecha, inicio);
        return ahora.isAfter(inicioPrueba);
    }

}
