package nttdata.bank.feign.impl;

import nttdata.bank.feign.clients.ContaExternaClient;
import nttdata.bank.feign.response.mockapi.ClienteExterno;
import nttdata.bank.service.ports.ClienteExternoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public Optional<BigDecimal> buscarSaldoClienteExterno(String idClienteExterno) {
        return contaExternaClient.getSaldoClieneExterno(idClienteExterno).stream().map(saldo -> saldo.get("saldo")).findFirst();
    }
}
