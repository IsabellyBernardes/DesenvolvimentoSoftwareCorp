package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Padaria;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PadariaPersistenceTest extends GenericTest {

    private Padaria criarPadariaValida() {
        Padaria p = new Padaria();
        p.setNome("Padaria Boa Viagem");
        p.setCnpj("72980105000165"); 
        p.setCep("51020-000"); // CEP de Recife/PE (Começa com 5, válido para @CepPernambuco)
        return p;
    }

    @Test
    public void testPersistirPadariaValida() {
        Padaria p = criarPadariaValida();
        
        assertDoesNotThrow(() -> {
            em.persist(p);
            em.flush();
        });
        
        assertNotNull(p.getId());
    }

    @Test
    public void testPersistirCepForaDePernambucoDeveFalhar() {
        Padaria p = criarPadariaValida();
        // Tenta salvar com CEP de São Paulo (Começa com 0)
        p.setCep("01310-100"); 

        assertThrows(Exception.class, () -> {
            em.persist(p);
            em.flush();
        }, "Deveria falhar pois o CEP não é de Pernambuco (@CepPernambuco)");
    }

    @Test
    public void testAtualizarPadariaParaCepInvalidoDeveFalhar() {
        // 1. Persiste uma padaria válida (Recife)
        Padaria p = criarPadariaValida();
        em.persist(p);
        em.flush(); 

        // 2. Tenta ATUALIZAR (Update) para um CEP do Rio de Janeiro (Começa com 2)
        p.setCep("22041-001");

        assertThrows(Exception.class, () -> {
            em.merge(p);
            em.flush();
        }, "Deveria falhar na atualização do CEP para outro estado");
    }
    
    @Test
    public void testPersistirCnpjInvalidoDeveFalhar() {
        Padaria p = criarPadariaValida();
        p.setCnpj("11122233300099"); // CNPJ com formato errado/inválido

        assertThrows(Exception.class, () -> {
            em.persist(p);
            em.flush();
        }, "Deveria falhar com CNPJ inválido");
    }
}