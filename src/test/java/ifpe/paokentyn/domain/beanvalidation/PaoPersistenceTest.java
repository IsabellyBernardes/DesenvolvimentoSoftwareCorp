package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Pao;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaoPersistenceTest extends GenericTest {

    private Pao criarPaoValido() {
        Pao p = new Pao();
        p.setNomePao("Pao Frances Integral");
        p.setPreco(1.50); 
        return p;
    }

    @Test
    public void testPersistirPaoValido() {
        Pao p = criarPaoValido();
        
        assertDoesNotThrow(() -> {
            em.persist(p);
            em.flush();
        });
    }

    @Test
    public void testPersistirPaoComNumeroNoNomeDeveFalhar() {
        Pao p = criarPaoValido();
        p.setNomePao("Pao 12 Grãos"); // Contém número -> @SemNumero deve barrar

        assertThrows(Exception.class, () -> {
            em.persist(p);
            em.flush();
        }, "Deveria falhar ao salvar pão com números no nome");
    }

    @Test
    public void testAtualizarPrecoParaNegativoDeveFalhar() {
        Pao p = criarPaoValido();
        em.persist(p);
        em.flush();

        p.setPreco(-5.00); 

        assertThrows(Exception.class, () -> {
            em.merge(p);
            em.flush();
        }, "Deveria falhar ao atualizar para preço negativo");
    }
}