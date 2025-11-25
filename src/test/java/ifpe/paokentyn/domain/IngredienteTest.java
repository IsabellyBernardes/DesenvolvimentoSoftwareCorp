package ifpe.paokentyn.domain;

import ifpe.paokentyn.util.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author isabe
 */
public class IngredienteTest {

    private static final Logger logger = LoggerFactory.getLogger(IngredienteTest.class);
    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory...");
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Finalizando EntityManagerFactory...");
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
    public void testEncontrarIngredienteDoDataSet() {
        logger.info("--- Executando testEncontrarIngredienteDoDataSet ---");

        Ingrediente ingrediente = em.find(Ingrediente.class, 902L);

        assertNotNull(ingrediente, "Ingrediente 902 deveria existir no dataset");
        assertEquals("Ovos", ingrediente.getNome());

        assertNotNull(ingrediente.getPaes(), "A lista de pães não deveria ser nula");
        assertEquals(1, ingrediente.getPaes().size(), "Ovos devem ser usados em 1 pão");
        assertEquals("Pão de Queijo", ingrediente.getPaes().get(0).getNomePao());
        
        logger.info("Encontrado: {} (usado em {} pão(es))", 
            ingrediente.getNome(), 
            ingrediente.getPaes().size()
        );
    }

    @Test
    public void testAtualizarIngredienteGerenciado() {
        logger.info("--- Executando testAtualizarIngredienteGerenciado (Sem Merge) ---");

        Ingrediente ingrediente = em.find(Ingrediente.class, 901L); 
        assertNotNull(ingrediente);
        
        ingrediente.setNome("Farinha de Trigo Premium");
        
        em.flush(); 
        em.clear(); 
        
        Ingrediente atualizado = em.find(Ingrediente.class, 901L);
        assertEquals("Farinha de Trigo Premium", atualizado.getNome());
        
        logger.info("Ingrediente atualizado automaticamente para: {}", atualizado.getNome());
    }

    @Test
    public void testAtualizarIngredienteComMerge() {
        logger.info("--- Executando testAtualizarIngredienteComMerge ---");

        Ingrediente ingrediente = em.find(Ingrediente.class, 903L); // Polvilho
        assertNotNull(ingrediente);
        
        em.clear(); 
        
        ingrediente.setNome("Polvilho Azedo");
        
        em.merge(ingrediente);
        
        em.flush();
        em.clear();

        Ingrediente atualizado = em.find(Ingrediente.class, 903L);
        assertEquals("Polvilho Azedo", atualizado.getNome());
        
        logger.info("Ingrediente atualizado via merge para: {}", atualizado.getNome());
    }

    @Test
    public void testRemoverIngrediente() {
        logger.info("--- Executando testRemoverIngrediente ---");

        Ingrediente ingrediente = em.find(Ingrediente.class, 901L);
        assertNotNull(ingrediente);

        em.remove(ingrediente);
        
        em.flush();
        em.clear(); //para ter certeza que não vai ficar no cache

        Ingrediente apagado = em.find(Ingrediente.class, 901L);
        assertNull(apagado, "O ingrediente deveria ter sido removido do banco");
        
        logger.info("Ingrediente 901 removido com sucesso.");
    }
}