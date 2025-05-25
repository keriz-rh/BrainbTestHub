package DAW.BrainbTestHub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class OidcUserAuthoritiesConfig {

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

            // Extrae los roles del claim personalizado
            Object rolesClaim = oidcUser.getClaims().get("https://brainbtesthub.com/roles");
            if (rolesClaim instanceof List<?> rolesList) {
                mappedAuthorities.addAll(
                    rolesList.stream()
                        .map(String.class::cast)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet())
                );
            }

            // Devuelve un nuevo OidcUser con las authorities mapeadas
            return new org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser(
                mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo()
            );
        };
    }
} 