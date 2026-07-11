package com.example.ecommerce.pedidos.adapters.in.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OidcUser user, Model model) {
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("name", user.getGivenName() != null
                    ? user.getGivenName()
                    : user.getPreferredUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("picture", user.getPicture());
            model.addAttribute("roles", user.getAuthorities());
        }
        return "dashboard";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
