package nttdata.bank.mappers.usuario;

import nttdata.bank.controllers.usuario.requests.UsuarioRequest;
import nttdata.bank.controllers.usuario.responses.UsuarioResponse;
import nttdata.bank.domain.dto.usuario.UsuarioDTO;
import nttdata.bank.domain.entities.usuario.Usuario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toUsuarioEntity(UsuarioRequest usuarioRequest);

    UsuarioResponse toUsuarioResponse(Usuario usuario);

    List<Usuario> toUsuarios(List<UsuarioDTO> usuarioDTOS);


}
