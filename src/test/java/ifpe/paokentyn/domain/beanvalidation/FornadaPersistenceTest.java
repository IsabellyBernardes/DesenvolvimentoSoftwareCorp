package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.Fornada;
import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Padaria;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FornadaPersistenceTest extends GenericTest {

    private Fornada criarFornadaValida() {
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria da Fornada");
        padaria.setCnpj("72980105000165"); 
        padaria.setCep("50000-000"); 
        em.persist(padaria);

        Fornada f = new Fornada();
        f.setPadaria(padaria);
        f.setHoraInicio(new Date());
        
        // Data no passado (Ontem) - para garantir @Past
        Calendar ontem = Calendar.getInstance();
        ontem.add(Calendar.DAY_OF_MONTH, -1);
        f.setDataFornada(ontem.getTime());
        
        return f;
    }

    @Test
    public void testPersistirFornadaValida() {
        Fornada f = criarFornadaValida();
        
        assertDoesNotThrow(() -> {
            em.persist(f);
            em.flush();
        });
        
        assertNotNull(f.getId());
    }

    @Test
    public void testPersistirDataFornadaFuturaDeveFalhar() {
        Fornada f = criarFornadaValida();
        
        // Data no futuro (Inválido @Past)
        Calendar amanha = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_MONTH, 1);
        f.setDataFornada(amanha.getTime());

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria falhar com data de fornada no futuro");
    }

    @Test
    public void testPersistirSemPadariaDeveFalhar() {
        Fornada f = criarFornadaValida();
        f.setPadaria(null); // Obrigatório @NotNull

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria falhar ao salvar fornada sem padaria");
    }
    
    @Test
    public void testAtualizarFornadaParaDataFuturaDeveFalhar() {
        Fornada f = criarFornadaValida();
        em.persist(f);
        em.flush();

        // 2. Tenta ATUALIZAR para data futura (Inválido)
        Calendar futuro = Calendar.getInstance();
        futuro.add(Calendar.DAY_OF_MONTH, 5);
        f.setDataFornada(futuro.getTime());

        assertThrows(Exception.class, () -> {
            em.merge(f);
            em.flush();
        }, "Deveria falhar ao atualizar data da fornada para o futuro");
    }
}