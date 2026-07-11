package com.example.ecommerce.pedidos.infrastructure.config;

import com.example.ecommerce.pedidos.application.policy.PedidoCriadoProcessingRule;
import com.example.ecommerce.pedidos.application.port.out.ConsultarPedidoMensagensPort;
import com.example.ecommerce.pedidos.application.port.out.EventIdGeneratorPort;
import com.example.ecommerce.pedidos.application.port.out.LimparPedidoMensagensPort;
import com.example.ecommerce.pedidos.application.port.out.PublicarPedidoCriadoPort;
import com.example.ecommerce.pedidos.application.port.out.RegistrarPedidoRecebidoPort;
import com.example.ecommerce.pedidos.application.port.out.RelogioPort;
import com.example.ecommerce.pedidos.application.service.ConsultarMensagensService;
import com.example.ecommerce.pedidos.application.service.CriarPedidoService;
import com.example.ecommerce.pedidos.application.service.LimparMensagensService;
import com.example.ecommerce.pedidos.application.service.ProcessarPedidoCriadoService;
import com.example.ecommerce.pedidos.infrastructure.properties.ProcessamentoPedidoProperties;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfig {

    @Bean
    public CriarPedidoService criarPedidoService(
            PublicarPedidoCriadoPort publicarPedidoCriadoPort,
            EventIdGeneratorPort eventIdGeneratorPort,
            RelogioPort relogioPort
    ) {
        return new CriarPedidoService(publicarPedidoCriadoPort, eventIdGeneratorPort, relogioPort);
    }

    @Bean
    public ProcessarPedidoCriadoService processarPedidoCriadoService(
            RegistrarPedidoRecebidoPort registrarPedidoRecebidoPort,
            List<PedidoCriadoProcessingRule> processingRules
    ) {
        return new ProcessarPedidoCriadoService(registrarPedidoRecebidoPort, processingRules);
    }

    @Bean
    public ConsultarMensagensService consultarMensagensService(
            ConsultarPedidoMensagensPort consultarPedidoMensagensPort
    ) {
        return new ConsultarMensagensService(consultarPedidoMensagensPort);
    }

    @Bean
    public LimparMensagensService limparMensagensService(
            LimparPedidoMensagensPort limparPedidoMensagensPort
    ) {
        return new LimparMensagensService(limparPedidoMensagensPort);
    }

    @Bean
    public PedidoCriadoProcessingRule clienteIdFalhaSimuladaRule(
            ProcessamentoPedidoProperties properties
    ) {
        return event -> {
            Long clienteIdComFalha = properties.failWhenClienteId();

            if (clienteIdComFalha != null && clienteIdComFalha.equals(event.clienteId())) {
                throw new IllegalStateException(
                        "Erro simulado para testar retry e DLT. clienteId=" + event.clienteId()
                );
            }
        };
    }
}
