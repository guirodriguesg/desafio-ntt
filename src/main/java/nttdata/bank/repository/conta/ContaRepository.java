package nttdata.bank.repository.conta;

import nttdata.bank.domain.entities.conta.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query("SELECT c FROM Conta c WHERE c.usuario.id = :idUsuario")
    Optional<List<Conta>> findByIdUsuario(Long idUsuario);
}
