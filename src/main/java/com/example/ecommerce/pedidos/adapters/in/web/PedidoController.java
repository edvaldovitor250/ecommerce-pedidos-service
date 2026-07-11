package com.example.ecommerce.pedidos.adapters.in.web;

import com.example.ecommerce.pedidos.adapters.in.web.dto.CriarPedidoRequest;
import com.example.ecommerce.pedidos.adapters.in.web.dto.DltMessageResponse;
import com.example.ecommerce.pedidos.adapters.in.web.dto.PedidoPublicadoResponse;
import com.example.ecommerce.pedidos.application.port.in.ConsultarMensagensUseCase;
import com.example.ecommerce.pedidos.application.port.in.CriarPedidoCommand;
import com.example.ecommerce.pedidos.application.port.in.CriarPedidoUseCase;
import com.example.ecommerce.pedidos.application.port.in.LimparMensagensUseCase;
import com.example.ecommerce.pedidos.application.port.in.PedidoPublicadoResultado;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CriarPedidoUseCase criarPedidoUseCase;
    private final ConsultarMensagensUseCase consultarMensagensUseCase;
    private final LimparMensagensUseCase limparMensagensUseCase;

    public PedidoController(
            CriarPedidoUseCase criarPedidoUseCase,
            ConsultarMensagensUseCase consultarMensagensUseCase,
            LimparMensagensUseCase limparMensagensUseCase
    ) {
        this.criarPedidoUseCase = criarPedidoUseCase;
        this.consultarMensagensUseCase = consultarMensagensUseCase;
        this.limparMensagensUseCase = limparMensagensUseCase;
    }

    @PostMapping
    public ResponseEntity<PedidoPublicadoResponse> criarPedido(@Valid @RequestBody CriarPedidoRequest request) {
        CriarPedidoCommand command = new CriarPedidoCommand(
                request.pedidoId(),
                request.clienteId(),
                request.valor()
        );

        PedidoPublicadoResultado result = criarPedidoUseCase.criar(command);

        return ResponseEntity.accepted().body(new PedidoPublicadoResponse(
                result.message(),
                result.eventId(),
                result.topic(),
                result.partition(),
                result.offset()
        ));
    }

    @GetMapping("/recebidos")
    public List<PedidoCriadoEvent> listarRecebidos() {
        return consultarMensagensUseCase.listarRecebidos();
    }

    @GetMapping("/dlt")
    public List<DltMessageResponse> listarDlt() {
        return consultarMensagensUseCase.listarDlt()
                .stream()
                .map(message -> new DltMessageResponse(
                        message.event(),
                        message.motivo(),
                        message.recebidoEm()
                ))
                .toList();
    }

    @DeleteMapping("/memoria")
    public ResponseEntity<Void> limparMemoria() {
        limparMensagensUseCase.limpar();
        return ResponseEntity.noContent().build();
    }
}
