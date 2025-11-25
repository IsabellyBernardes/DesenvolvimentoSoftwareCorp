package ifpe.paokentyn.domain;

import ifpe.paokentyn.util.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class PaoTest {

    private static final Logger logger = LoggerFactory.getLogger(PaoTest.class);
    
    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory para testes de Pao...");
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Encerrando EntityManagerFactory...");
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        logger.info("Carregando dataset.xml e iniciando transação...");
        DbUnitUtil.insertData();
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @AfterEach
    public void tearDown() {
        logger.info("Finalizando transação e liberando EntityManager...");
        if (et != null && et.isActive()) {
            et.commit();
        }
        if (em != null) {
            em.close();
        }
    }

    @Test
    public void testEncontrarPaoDoDataSetEIngredientes() {
        logger.info("--- Executando testEncontrarPaoDoDataSetEIngredientes ---");

        Pao pao = em.find(Pao.class, 302L);

        assertNotNull(pao, "Pão 302 deveria existir no dataset");
        assertEquals("Pão de Queijo", pao.getNomePao());
        assertEquals(3.00, pao.getPreco());

        assertNotNull(pao.getIngredientes(), "A lista de ingredientes não deveria ser nula");
        assertEquals(2, pao.getIngredientes().size(), "Pão de Queijo deve ter 2 ingredientes");
        
        boolean achouOvos = pao.getIngredientes().stream()
                                .anyMatch(ing -> ing.getNome().equals("Ovos"));
        boolean achouPolvilho = pao.getIngredientes().stream()
                                .anyMatch(ing -> ing.getNome().equals("Polvilho"));
        
        assertTrue(achouOvos, "Deveria ter encontrado Ovos na lista de ingredientes");
        assertTrue(achouPolvilho, "Deveria ter encontrado Polvilho na lista de ingredientes");

        logger.info("Encontrado: {} (com {} ingredientes)", 
            pao.getNomePao(), 
            pao.getIngredientes().size()
        );
    }

    @Test
    public void testPersistirPaoComNovoIngrediente() {
        logger.info("--- Executando testPersistirPaoComNovoIngrediente ---");

        Ingrediente novoIngrediente = new Ingrediente();
        novoIngrediente.setNome("Fermento Biológico");
        
        Pao novoPao = new Pao();
        novoPao.setNomePao("Pão Francês");
        novoPao.setPreco(0.75);
        
        novoPao.setIngredientes(List.of(novoIngrediente));

        em.persist(novoIngrediente);
        em.persist(novoPao);
        em.flush(); 
        assertNotNull(novoPao.getId(), "ID do novo pão não pode ser nulo");
        assertNotNull(novoIngrediente.getId(), "ID do novo ingrediente não pode ser nulo");

        em.clear(); 
        
        Pao paoDoBanco = em.find(Pao.class, novoPao.getId());
        assertEquals(1, paoDoBanco.getIngredientes().size());
        assertEquals("Fermento Biológico", paoDoBanco.getIngredientes().get(0).getNome());

        logger.info("Persistido: {} com ID: {}", 
            novoPao.getNomePao(), 
            novoPao.getId()
        );
    }
}