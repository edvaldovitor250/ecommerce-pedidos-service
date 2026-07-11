package com.example.ecommerce.pedidos.adapters.in.web.dto;

import java.time.Instant;
import java.util.List;

public record ErroResponse(
        int status,
        String erro,
        List<String> detalhes,
        Instant timestamp
) {
}
