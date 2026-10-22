package nttdata.bank.controllers.autenticacao.requests;

import jakarta.validation.constraints.NotBlank;

public record AutenticacaoRequest(@NotBlank(message = "Login é obrigatório")
                                  String login,
                                  @NotBlank(message = "Senha é obrigatória")
                                  String senha) {
}
