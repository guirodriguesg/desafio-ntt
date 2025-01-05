package nttdata.bank.mappers.transacao;

import nttdata.bank.controllers.transacao.requests.TransacaoRequest;
import nttdata.bank.controllers.transacao.responses.TransacaoResponse;
import nttdata.bank.domain.dto.transacao.TransacaoDTO;
import nttdata.bank.domain.entities.transacao.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {

//    @Mapping(target = "contaOrigem.codBanco", source = "codBancoOrigem")
    @Mapping(target = "contaDestino", ignore = true)
    @Mapping(target = "contaOrigem", ignore = true)
    @Mapping(target = "statusTransacao", ignore = true)
    @Mapping(target = "dataTransacao", ignore = true)
    Transacao toTransacao(TransacaoRequest request);

    TransacaoResponse toTransacaoResponse(Transacao transacao);

    @Mapping(target = "contaOrigem", ignore = true)
    @Mapping(target = "contaDestino", ignore = true)
    List<TransacaoDTO> toListaTransacaoDTO(List<Transacao> transacoes);


}
