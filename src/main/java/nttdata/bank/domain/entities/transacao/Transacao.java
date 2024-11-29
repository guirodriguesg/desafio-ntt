package nttdata.bank.domain.entities.transacao;

import jakarta.persistence.*;
import nttdata.bank.domain.entities.conta.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACAO")
public class Transacao {

    @Id
    @Column(name = "ID_TRANSACAO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ID_CONTA_ORIGEM", referencedColumnName = "ID_CONTA")
    private Conta contaOrigem;//deposito e retirada
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ID_CONTA_DESTINO", referencedColumnName = "ID_CONTA")
    private Conta contaDestino; //transferencia
    @Column(name = "TIPO_TRANSACAO_FIN")
    private TipoTransacaoFinEnum tipoTransacaoFinanceira;
    @Column(name = "VALOR_TRANSACAO")
    private BigDecimal valorTransacao;
    @Column(name = "DATA_TRANSACAO")
    private LocalDateTime dataTransacao;
    @Column(name = "TAXA_CAMBIO")
    private BigDecimal taxaCambio;// buscar da api na hora do response
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DESPESA")
    private TipoDespesaEnum tipoDespesa;//Se for uma compra de um produto ou serviço
    @Column(name = "STATUS_TRANSACAO")
    private StatusTransacaoEnum statusTransacao;

    @Transient
    private Long idContaOrigem;
    @Transient
    private Long idContaDestino;

    public Long getId() {
        return id;
    }

    public Conta getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(Conta contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public Conta getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(Conta contaDestino) {
        this.contaDestino = contaDestino;
    }

    public TipoTransacaoFinEnum getTipoTransacaoFinanceira() {
        return tipoTransacaoFinanceira;
    }

    public void setTipoTransacaoFinanceira(TipoTransacaoFinEnum tipoTransacaoFinanceira) {
        this.tipoTransacaoFinanceira = tipoTransacaoFinanceira;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public BigDecimal getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(BigDecimal valorTransacao) {
        this.valorTransacao = valorTransacao;
    }

    public BigDecimal getTaxaCambio() {
        return taxaCambio;
    }

    public void setTaxaCambio(BigDecimal taxaCambio) {
        this.taxaCambio = taxaCambio;
    }

    public TipoDespesaEnum getTipoDespesa() {
        return tipoDespesa;
    }

    public void setTipoDespesa(TipoDespesaEnum tipoDespesa) {
        this.tipoDespesa = tipoDespesa;
    }

    public Long getIdContaOrigem() {
        return idContaOrigem;
    }

    public void setIdContaOrigem(Long idContaOrigem) {
        this.idContaOrigem = idContaOrigem;
    }

    public Long getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(Long idContaDestino) {
        this.idContaDestino = idContaDestino;
    }

    public StatusTransacaoEnum getStatusTransacao() {
        return statusTransacao;
    }

    public void setStatusTransacao(StatusTransacaoEnum statusTransacao) {
        this.statusTransacao = statusTransacao;
    }
}
