package nttdata.bank.domain.entities.conta;

import jakarta.persistence.*;
import nttdata.bank.domain.entities.usuario.Usuario;

import java.math.BigDecimal;

@Entity
@Table(name = "CONTA")
public class Conta {

    @Id
    @Column(name = "ID_CONTA")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COD_BANCO")
    private Long codBanco;
    @Column(name = "COD_AGENCIA")
    private String codAgencia;
    @Column(name = "DIGITO_AGENCIA")
    private String digitoAgencia;
    @Column(name = "COD_CONTA")
    private String codConta;
    @Column(name = "DIGITO_CONTA")
    private String digitoConta;
    @Column(name = "SALDO")
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "ID_USUARIO")
    private Usuario usuario;

//    private String tipoConta;


    public Conta() {
    }

    public Conta(Long codBanco, String codAgencia, String digitoAgencia, String codConta, String digitoConta) {
        this.codBanco = codBanco;
        this.codAgencia = codAgencia;
        this.digitoAgencia = digitoAgencia;
        this.codConta = codConta;
        this.digitoConta = digitoConta;
    }

    public Long getId() {
        return id;
    }

    public String getCodAgencia() {
        return codAgencia;
    }

    public void setCodAgencia(String codAgencia) {
        this.codAgencia = codAgencia;
    }

    public String getDigitoAgencia() {
        return digitoAgencia;
    }

    public void setDigitoAgencia(String digitoAgencia) {
        this.digitoAgencia = digitoAgencia;
    }

    public String getCodConta() {
        return codConta;
    }

    public void setCodConta(String codConta) {
        this.codConta = codConta;
    }

    public String getDigitoConta() {
        return digitoConta;
    }

    public void setDigitoConta(String digitoConta) {
        this.digitoConta = digitoConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getCodBanco() {
        return codBanco;
    }

    public void setCodBanco(Long codBanco) {
        this.codBanco = codBanco;
    }

}
