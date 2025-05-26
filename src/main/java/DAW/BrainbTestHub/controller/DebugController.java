package DAW.BrainbTestHub.controller;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {
    @GetMapping("/debug-token")
    public Object debugToken(@AuthenticationPrincipal OidcUser principal) {
        return principal != null ? principal.getClaims() : "No OidcUser principal found";
    }
} 