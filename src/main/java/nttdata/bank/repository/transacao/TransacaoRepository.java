package nttdata.bank.repository.transacao;

import nttdata.bank.domain.entities.transacao.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {


    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.usuario.id = :idClienteContaOrigem")
    Optional<List<Transacao>> findByIdClienteContaOrigem(Long idClienteContaOrigem);
}
