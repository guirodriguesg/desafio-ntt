package nttdata.bank.mappers.usuario;

import nttdata.bank.controllers.usuario.requests.UsuarioRequest;
import nttdata.bank.controllers.usuario.responses.UsuarioResponse;
import nttdata.bank.domain.dto.usuario.UsuarioDTO;
import nttdata.bank.domain.entities.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "contas", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    Usuario toUsuarioEntity(UsuarioRequest usuarioRequest);

    UsuarioResponse toUsuarioResponse(Usuario usuario);

    @Mapping(target = "contas", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    List<Usuario> toUsuarios(List<UsuarioDTO> usuarioDTOS);

}
