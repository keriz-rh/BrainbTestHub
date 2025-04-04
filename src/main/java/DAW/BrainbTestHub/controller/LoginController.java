package DAW.BrainbTestHub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String registro, Model model) {
        if ("exitoso".equals(registro)) {
            model.addAttribute("registroExitoso", true);
        }
        return "login";
    }
} 