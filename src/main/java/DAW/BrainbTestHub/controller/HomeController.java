package DAW.BrainbTestHub.controller;

import DAW.BrainbTestHub.service.CuestionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CuestionarioService cuestionarioService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mensaje", "¡Pon a prueba tu mente!");
        return "Inicio/index";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Obtener estadísticas básicas
            long totalCuestionarios = cuestionarioService.getAllCuestionarios().size();
            
            model.addAttribute("totalCuestionarios", totalCuestionarios);
            
        } catch (Exception e) {
            // En caso de error, usar valores por defecto
            model.addAttribute("totalCuestionarios", 0);
        }
        
        return "dashboard/index";
    }
} 