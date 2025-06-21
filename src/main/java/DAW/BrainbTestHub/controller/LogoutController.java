package DAW.BrainbTestHub.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;

@Controller
public class LogoutController {

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.clientId}")
    private String clientId;

    
    //@Value("${auth0.logoutRedirectUrl:http://localhost:8080}")
    @Value("${auth0.logoutRedirectUrl:http://localhost:8080}")
    private String logoutRedirectUrl;

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Cierra la sesión local
        request.logout();
        request.getSession().invalidate();

        // Redirige a Auth0 para cerrar la sesión global
        String logoutUrl = String.format(
            "https://%s/v2/logout?client_id=%s&returnTo=%s",
            domain,
            clientId,
            java.net.URLEncoder.encode(logoutRedirectUrl, "UTF-8")
        );
        response.sendRedirect(logoutUrl);
    }
} 