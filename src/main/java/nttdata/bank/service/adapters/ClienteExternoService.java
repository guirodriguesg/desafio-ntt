package nttdata.bank.service.adapters;

import nttdata.bank.feign.response.ClienteExterno;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ClienteExternoService {

    Optional<ClienteExterno> buscarClienteExterno();
}
