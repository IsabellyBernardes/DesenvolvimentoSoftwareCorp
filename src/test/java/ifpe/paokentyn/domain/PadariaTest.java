package ifpe.paokentyn.domain;

import ifpe.paokentyn.domain.Padaria; 
import ifpe.paokentyn.util.DbUnitUtil; 
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; 
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PadariaTest {

    private static final Logger logger = LoggerFactory.getLogger(PadariaTest.class);

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;
    
    private final static String CNPJ_NOVO_TESTE = "11223344000199"; 
    private final static String CNPJ_DATASET = "99887766000199"; 

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory...");
        emf = Persistence.createEntityManagerFactory("DSC");
        logger.info("EntityManagerFactory inicializado.");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Finalizando EntityManagerFactory...");
        if (emf != null) {
            emf.close();
        }
        logger.info("EntityManagerFactory fechado.");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Carregando dataset.xml via DbUnit...");
        DbUnitUtil.insertData();
        
        logger.info("Criando EntityManager e iniciando transação...");
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
        logger.info("Transação iniciada.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Finalizando transação e fechando EntityManager...");
        
        if (et != null && et.isActive()) {
            et.commit();
            logger.info("Transação commitada.");
        }
        if (em != null) {
            em.close();
            logger.info("EntityManager fechado.");
        }
    }

    @Test
    public void testPersistirPadaria() {
        logger.info("--- Executando testPersistirPadaria (independente) ---");
        
        Padaria novaPadaria = new Padaria();
        novaPadaria.setNome("Pão Kentyn - Filial Agreste");
        novaPadaria.setCep("55000000");
        novaPadaria.setCnpj(CNPJ_NOVO_TESTE);

        logger.info("Persistindo nova padaria: nome={}, cnpj={}", 
            novaPadaria.getNome(), novaPadaria.getCnpj());

        em.persist(novaPadaria);
        em.flush();

        assertNotNull(novaPadaria.getId(), "ID não deveria ser nulo após persistir");
        assertEquals(1L, novaPadaria.getId(), "O primeiro ID gerado deve ser 1");
        assertEquals(CNPJ_NOVO_TESTE, novaPadaria.getCnpj());

        logger.info("Padaria persistida com sucesso! ID gerado={}", novaPadaria.getId());
    }

    @Test
    public void testEncontrarPadariaDoDataSet() {
        logger.info("--- Executando testEncontrarPadariaDoDataSet (independente) ---");
        
        Padaria padaria = em.find(Padaria.class, 101L);

        logger.info("Padaria buscada pelo ID 101 → {}", padaria);

        assertNotNull(padaria);
        assertEquals("Padaria do Melhor Teste", padaria.getNome());
        assertEquals(CNPJ_DATASET, padaria.getCnpj());

        logger.info("Padaria encontrada: nome={}, cnpj={}", 
            padaria.getNome(), padaria.getCnpj());
    }
}
