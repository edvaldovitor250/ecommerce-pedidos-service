package com.example.ecommerce.pedidos.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserAuthority;
import org.springframework.security.oauth2.core.oidc.idToken.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/pedidos/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .userInfoEndpoint(userInfo -> userInfo
                    .userAuthoritiesMapper(userAuthorities())
                )
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/pedidos/**")
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    private org.springframework.security.core.GrantedAuthorityMapper userAuthorities() {
        return user -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            if (user instanceof OidcUser oidcUser) {
                authorities.add(new OidcUserAuthority(oidcUser.getIdToken(), oidcUser.getUserInfo()));

                OidcIdToken idToken = oidcUser.getIdToken();
                Map<String, Object> claims = idToken.getClaims();

                extractRolesFromClaim(claims, "realm_access", "roles", authorities);
                extractRolesFromClaim(claims, "resource_access", "roles", authorities);
            }

            Collection<? extends GrantedAuthority> oauth2Authorities = user.getAuthorities();
            authorities.addAll(oauth2Authorities);

            return authorities;
        };
    }

    private void extractRolesFromClaim(Map<String, Object> claims, String claimKey, String subKey,
                                        Set<GrantedAuthority> authorities) {
        if (claims.containsKey(claimKey)) {
            Object claimValue = claims.get(claimKey);

            if (claimValue instanceof Map<?, ?> claimMap) {
                Object subValue = claimMap.get(subKey);
                if (subValue instanceof Collection<?> roles) {
                    authorities.addAll(
                        roles.stream()
                            .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + role.toString()))
                            .collect(Collectors.toList())
                    );
                }
            }
        }
    }
}
