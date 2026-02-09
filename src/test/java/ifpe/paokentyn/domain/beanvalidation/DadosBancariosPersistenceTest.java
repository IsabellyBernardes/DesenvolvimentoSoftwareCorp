package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.DadosBancarios;
import ifpe.paokentyn.domain.GenericTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DadosBancariosPersistenceTest extends GenericTest {

    private DadosBancarios criarDadosValidos() {
        DadosBancarios db = new DadosBancarios();
        db.setBanco("Banco do Brasil"); 
        db.setAgencia("1234-5");       
        db.setConta("99888-7");        
        return db;
    }

    @Test
    public void testPersistirDadosBancariosValidos() {
        DadosBancarios db = criarDadosValidos();
        
        assertDoesNotThrow(() -> {
            em.persist(db);
            em.flush();
        });
        
        assertNotNull(db.getId());
    }

    @Test
    public void testPersistirAgenciaComLetrasDeveFalhar() {
        DadosBancarios db = criarDadosValidos();
        // O padrão é [0-9\-]+ (só números e traço)
        db.setAgencia("AgenciaUm"); 

        assertThrows(Exception.class, () -> {
            em.persist(db);
            em.flush();
        }, "Deveria falhar pois agência contém letras (@Pattern)");
    }

    @Test
    public void testPersistirBancoComNumeroDeveFalhar() {
        DadosBancarios db = criarDadosValidos();
        db.setBanco("Banco 24 Horas"); 

        assertThrows(Exception.class, () -> {
            em.persist(db);
            em.flush();
        }, "Deveria falhar pois nome do banco tem números (@SemNumero)");
    }

    @Test
    public void testAtualizarContaParaFormatoInvalidoDeveFalhar() {
        DadosBancarios db = criarDadosValidos();
        em.persist(db);
        em.flush();

        // 2. Tenta atualizar com caracteres inválidos
        db.setConta("Conta$Invalida");

        assertThrows(Exception.class, () -> {
            em.merge(db);
            em.flush();
        }, "Deveria falhar na atualização da conta com caracteres especiais");
    }
}