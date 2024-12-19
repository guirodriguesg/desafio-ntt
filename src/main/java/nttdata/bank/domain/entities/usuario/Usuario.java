package nttdata.bank.domain.entities.usuario;

import jakarta.persistence.*;
import nttdata.bank.domain.entities.conta.Conta;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "USUARIO")
public class Usuario implements UserDetails {

    @Id
    @Column(name = "ID_USUARIO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NOME")
    private String nome;
    @Column(name = "LOGIN")
    private String login;
    @Column(name = "SENHA")
    private String senha;
    @OneToMany
    @JoinColumn(name = "ID_USUARIO")
    private Set<Conta> contas;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "TP_USUARIO")
    private TipoUsuarioEnum tipoUsuarioEnum;

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Set<Conta> getContas() {
        return contas;
    }

    public void setContas(Set<Conta> contas) {
        this.contas = contas;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TipoUsuarioEnum getTipoUsuario() {
        return tipoUsuarioEnum;
    }

    public void setTipoUsuario(TipoUsuarioEnum tipoUsuarioEnum) {
        this.tipoUsuarioEnum = tipoUsuarioEnum;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.tipoUsuarioEnum.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
