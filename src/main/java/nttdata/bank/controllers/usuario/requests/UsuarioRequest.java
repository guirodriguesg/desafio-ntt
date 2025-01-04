package nttdata.bank.controllers.usuario.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import nttdata.bank.domain.entities.usuario.TipoUsuarioEnum;

import static nttdata.bank.utils.ConstatesUtils.REGEX_VALIDAR_EMAIL;

public record UsuarioRequest(
    Long id,
    @NotBlank(message = "Nome é obrigatório")
    String nome,
    @NotBlank(message = "Login é obrigatório")
    String login,
    @NotBlank(message = "Senha é obrigatória")
    String senha,
    @Null
//    @Email(regexp = REGEX_VALIDAR_EMAIL, message = "Email inválido")
    String email,
    @NotNull(message = "Tipo de usuário é obrigatório")
    TipoUsuarioEnum tipoUsuario
) {
}
