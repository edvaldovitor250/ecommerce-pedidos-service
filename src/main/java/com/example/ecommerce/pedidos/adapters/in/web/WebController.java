package com.example.ecommerce.pedidos.adapters.in.web;

import com.example.ecommerce.pedidos.application.port.in.ConsultarMensagensUseCase;
import com.example.ecommerce.pedidos.application.port.in.CriarPedidoCommand;
import com.example.ecommerce.pedidos.application.port.in.CriarPedidoUseCase;
import com.example.ecommerce.pedidos.application.port.in.PedidoPublicadoResultado;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import com.example.ecommerce.pedidos.domain.model.DltMessage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class WebController {

    private final ConsultarMensagensUseCase consultarMensagensUseCase;
    private final CriarPedidoUseCase criarPedidoUseCase;
    private final RestTemplate restTemplate;

    public WebController(
            ConsultarMensagensUseCase consultarMensagensUseCase,
            CriarPedidoUseCase criarPedidoUseCase
    ) {
        this.consultarMensagensUseCase = consultarMensagensUseCase;
        this.criarPedidoUseCase = criarPedidoUseCase;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        populateUserInfo(model);
        List<PedidoCriadoEvent> recebidos = consultarMensagensUseCase.listarRecebidos();
        List<DltMessage> dlt = consultarMensagensUseCase.listarDlt();
        model.addAttribute("totalPedidos", recebidos.size());
        model.addAttribute("totalDlt", dlt.size());
        Map<String, Object> health = getHealthDetails();
        model.addAttribute("healthStatus", String.valueOf(health.get("status")));
        model.addAttribute("healthDetails", health);
        return "dashboard";
    }

    @GetMapping("/app/pedidos/recebidos")
    public String pedidosRecebidos(Model model) {
        populateUserInfo(model);
        List<PedidoCriadoEvent> recebidos = consultarMensagensUseCase.listarRecebidos();
        model.addAttribute("pedidos", recebidos);
        model.addAttribute("totalPedidos", recebidos.size());
        return "pedidos/recebidos";
    }

    @PostMapping("/app/pedidos/criar")
    public String criarPedido(
            @RequestParam("pedidoId") Long pedidoId,
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("valor") BigDecimal valor,
            Model model
    ) {
        populateUserInfo(model);
        try {
            CriarPedidoCommand command = new CriarPedidoCommand(pedidoId, clienteId, valor);
            PedidoPublicadoResultado result = criarPedidoUseCase.criar(command);
            model.addAttribute("sucesso", true);
            model.addAttribute("mensagemSucesso",
                    "Pedido #" + pedidoId + " publicado com sucesso no Kafka!");
            String json = "{\n"
                    + "  \"message\": \"" + result.message() + "\",\n"
                    + "  \"eventId\": \"" + result.eventId() + "\",\n"
                    + "  \"topic\": \"" + result.topic() + "\",\n"
                    + "  \"partition\": " + result.partition() + ",\n"
                    + "  \"offset\": " + result.offset() + "\n"
                    + "}";
            model.addAttribute("resultadoJson", json);
        } catch (Exception e) {
            model.addAttribute("sucesso", false);
            model.addAttribute("mensagemErro", "Erro ao publicar pedido: " + e.getMessage());
        }

        List<PedidoCriadoEvent> recebidos = consultarMensagensUseCase.listarRecebidos();
        model.addAttribute("pedidos", recebidos);
        model.addAttribute("totalPedidos", recebidos.size());
        return "pedidos/recebidos";
    }

    @GetMapping("/app/pedidos/dlt")
    public String pedidosDlt(Model model) {
        populateUserInfo(model);
        List<DltMessage> dlt = consultarMensagensUseCase.listarDlt();
        model.addAttribute("mensagens", dlt);
        model.addAttribute("totalDlt", dlt.size());
        return "pedidos/dlt";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/app/health")
    public String healthCheck(Model model) {
        populateUserInfo(model);
        Map<String, Object> health = getHealthDetails();
        model.addAttribute("overallStatus", String.valueOf(health.get("status")));
        Map<String, Object> components = new LinkedHashMap<>();
        Object componentsObj = health.get("components");
        if (componentsObj instanceof Map<?, ?> componentsMap) {
            componentsMap.forEach((key, value) -> {
                if (value instanceof Map<?, ?> componentMap) {
                    Map<String, Object> component = new LinkedHashMap<>();
                    component.put("status", String.valueOf(componentMap.get("status")));
                    Object details = componentMap.get("details");
                    if (details instanceof Map<?, ?> detailMap) {
                        detailMap.forEach((dk, dv) -> component.put(String.valueOf(dk), dv));
                    }
                    components.put(String.valueOf(key), component);
                }
            });
        }
        model.addAttribute("components", components);
        return "actuator/health";
    }

    @GetMapping("/app/metrics")
    public String metrics(Model model) {
        populateUserInfo(model);
        List<Map<String, String>> metricsList = new ArrayList<>();
        String prometheusRaw = "";
        try {
            prometheusRaw = restTemplate.getForObject("http://localhost:8080/actuator/prometheus", String.class);
            if (prometheusRaw != null) {
                String[] lines = prometheusRaw.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("\\s+", 2);
                        if (parts.length == 2) {
                            Map<String, String> metric = new LinkedHashMap<>();
                            metric.put("name", parts[0]);
                            metric.put("value", parts[1]);
                            metricsList.add(metric);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        model.addAttribute("metricsList", metricsList);
        model.addAttribute("metricsCount", metricsList.size());
        return "actuator/metrics";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }

    private void populateUserInfo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            model.addAttribute("name", userDetails.getUsername());
            model.addAttribute("email", userDetails.getUsername() + "@exemplo.com");
            model.addAttribute("roles", userDetails.getAuthorities());
        } else {
            model.addAttribute("name", "Usuario");
            model.addAttribute("email", "usuario@exemplo.com");
            model.addAttribute("roles", List.of());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getHealthDetails() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    "http://localhost:8080/actuator/health", Map.class);
            if (response.getBody() != null) {
                return new LinkedHashMap<>(response.getBody());
            }
        } catch (Exception ignored) {
        }
        return Map.of("status", "DOWN");
    }
}
