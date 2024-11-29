package nttdata.bank.mappers.conta;

import nttdata.bank.controllers.conta.requests.ContaRequest;
import nttdata.bank.controllers.conta.responses.ContaResponse;
import nttdata.bank.domain.entities.conta.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContaMapper {

    @Mapping(target = "codAgencia", source = "codigoAgencia")
    @Mapping(target = "codConta", source = "codigoConta")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "codBanco", source = "codigoBanco")
    Conta toConta(ContaRequest contaRequest);

    @Mapping(target = "idConta", source = "id")
    @Mapping(target = "codigoBanco", source = "codBanco")
    @Mapping(target = "codigoAgencia", source = "codAgencia")
    @Mapping(target = "codigoConta", source = "codConta")
    @Mapping(target = "idUsuario", source = "usuario.id")
    ContaResponse toContaResponse(Conta conta);

    List<ContaResponse> toUsuarioResponseList(List<Conta> usuarioList);

}