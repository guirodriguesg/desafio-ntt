package nttdata.bank.controllers.usuario.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import nttdata.bank.domain.entities.usuario.TipoUsuarioEnum;

public class UsuarioRequest {

    @Null
    private Long id;
    @NotBlank
    private String nome;
    @NotBlank
    private String login;
    @NotBlank
    private String senha;
    @Null
    private String email;
    private TipoUsuarioEnum tipoUsuarioEnum;

    public @Null Long getId() {
        return id;
    }

    public void setId(@Null Long id) {
        this.id = id;
    }

    public @NotBlank String getNome() {
        return nome;
    }

    public void setNome(@NotBlank String nome) {
        this.nome = nome;
    }

    public @NotBlank String getLogin() {
        return login;
    }

    public void setLogin(@NotBlank String login) {
        this.login = login;
    }

    public @NotBlank String getSenha() {
        return senha;
    }

    public void setSenha(@NotBlank String senha) {
        this.senha = senha;
    }

    public @Null String getEmail() {
        return email;
    }

    public void setEmail(@Null String email) {
        this.email = email;
    }

    public TipoUsuarioEnum getTipoUsuario() {
        return tipoUsuarioEnum;
    }

    public void setTipoUsuario(TipoUsuarioEnum tipoUsuarioEnum) {
        this.tipoUsuarioEnum = tipoUsuarioEnum;
    }
}
