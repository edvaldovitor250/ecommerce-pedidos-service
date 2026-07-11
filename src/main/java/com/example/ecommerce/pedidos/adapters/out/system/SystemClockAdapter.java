package com.example.ecommerce.pedidos.adapters.out.system;

import com.example.ecommerce.pedidos.application.port.out.RelogioPort;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class SystemClockAdapter implements RelogioPort {

    @Override
    public Instant agora() {
        return Instant.now();
    }
}
