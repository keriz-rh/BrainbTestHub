package DAW.BrainbTestHub.service;

import DAW.BrainbTestHub.model.Cuestionario;
import DAW.BrainbTestHub.repository.CuestionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
