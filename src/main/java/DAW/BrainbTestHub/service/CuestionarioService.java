package DAW.BrainbTestHub.service;

import DAW.BrainbTestHub.model.Cuestionario;
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

    public long calcularTiempoDisponible(Cuestionario cuestionario, LocalDateTime ahora) {
        LocalDateTime horaFin = LocalDateTime.of(cuestionario.getFecha(), cuestionario.getHoraFin());
        long segundosHastaFin = Duration.between(ahora, horaFin).getSeconds();
        long duracionEnSegundos = cuestionario.getDuracion() * 60;

        if (segundosHastaFin <= 0) {
            return 0;
        }

        long resultado = Math.min(duracionEnSegundos, segundosHastaFin);
        return resultado;
    }

}
