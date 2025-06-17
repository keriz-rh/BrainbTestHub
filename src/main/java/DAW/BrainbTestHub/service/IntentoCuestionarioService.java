package DAW.BrainbTestHub.service;

import DAW.BrainbTestHub.model.IntentoCuestionario;
import DAW.BrainbTestHub.repository.IntentoCuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntentoCuestionarioService {

    @Autowired
    private IntentoCuestionarioRepository intentoRepo;

    public List<IntentoCuestionario> obtenerIntentosPorUsuario(String userId) {
        return intentoRepo.findByUserId(userId);
    }

    public List<IntentoCuestionario> obtenerIntentosPorCuestionario(Long cuestionarioId) {
        return intentoRepo.findByCuestionarioId(cuestionarioId);
    }

    public IntentoCuestionario guardarIntento(IntentoCuestionario intento) {
        return intentoRepo.save(intento);
    }

    public IntentoCuestionario obtenerPorId(Long id) {
        return intentoRepo.findById(id).orElse(null);
    }
}
