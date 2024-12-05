package nttdata.bank.service.ports;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ClienteExternoService {

    Optional<?> buscarClienteExterno();
}
