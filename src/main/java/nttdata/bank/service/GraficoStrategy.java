package nttdata.bank.service;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

public interface GraficoStrategy {

    ByteArrayOutputStream gerarGrafico(Optional<?> dados);
}
