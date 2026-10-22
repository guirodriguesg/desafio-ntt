package nttdata.bank.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Service
public class GraficoService {
    
    public ByteArrayOutputStream gerarGrafico(GraficoStrategy graficoStrategy, Optional<?> dados) {
        return graficoStrategy.gerarGrafico(dados);
    }
}
