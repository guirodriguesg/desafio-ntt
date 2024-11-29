package nttdata.bank.feign.impl;

import nttdata.bank.feign.clients.ContaExternaClient;
import nttdata.bank.feign.response.ClienteExterno;
import nttdata.bank.service.adapters.ClienteExternoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteExternoServiceImpl implements ClienteExternoService {

    private final ContaExternaClient contaExternaClient;

    public ClienteExternoServiceImpl(ContaExternaClient contaExternaClient) {
        this.contaExternaClient = contaExternaClient;
    }

    @Override
    public Optional<ClienteExterno> buscarClienteExterno() {
        return contaExternaClient.getContaClieneExterno();
    }
}
