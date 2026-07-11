package com.example.ecommerce.pedidos.adapters.out.system;

import com.example.ecommerce.pedidos.application.port.out.EventIdGeneratorPort;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidEventIdGeneratorAdapter implements EventIdGeneratorPort {

    @Override
    public String gerar() {
        return UUID.randomUUID().toString();
    }
}
