package nttdata.bank.service.adapters;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ClienteExternoService {

    Optional<?> buscarClienteExterno();
}
