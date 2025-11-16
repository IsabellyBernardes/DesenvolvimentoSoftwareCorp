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

public class PaoTest {

    private static final Logger logger = LoggerFactory.getLogger(PaoTest.class);

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory para testes de Pao...");
        emf = Persistence.createEntityManagerFactory("DSC"); 
        logger.info("EntityManagerFactory inicializado.");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Encerrando EntityManagerFactory...");
        if (emf != null) {
            emf.close(); 
        }
        logger.info("EntityManagerFactory encerrado.");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Carregando dataset.xml e iniciando contexto JPA...");
        DbUnitUtil.insertData();
        
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

    // --- Nossos testes começam aqui ---

    @Test
    public void testEncontrarPaoDoDataSet() {
        logger.info("--- Executando testEncontrarPaoDoDataSet ---");

        // 1. Busca o Pão ID 301 (do seu dataset.xml)
        Pao pao = em.find(Pao.class, 301L);

        // 2. Verifica os dados dele
        assertNotNull(pao, "Pão 301 deveria existir no dataset");
        assertEquals("Pão Integral", pao.getNomePao());
        assertEquals(5.50, pao.getPreco());

        logger.info("Encontrado pão no dataset: nome={}, preco={}", 
            pao.getNomePao(), pao.getPreco());
    }

    @Test
    public void testPersistirPao() {
        logger.info("--- Executando testPersistirPao ---");

        // 1. Criamos o novo pão
        Pao novoPao = new Pao();
        novoPao.setNomePao("Pão Francês");
        novoPao.setPreco(0.75);

        logger.info("Persistindo novo pão: nome={}, preco={}", 
            novoPao.getNomePao(), novoPao.getPreco());

        // 2. Persistimos o novo pão
        em.persist(novoPao); 
        em.flush(); // Força o INSERT

        // 3. Verificamos se ele foi salvo corretamente
        assertNotNull(novoPao.getId(), "ID do novo pão não pode ser nulo");
        assertTrue(novoPao.getId() > 0, "ID deve ser positivo");
        
        assertNotEquals(301L, novoPao.getId());
        assertNotEquals(302L, novoPao.getId());

        logger.info("Novo pão persistido com sucesso: nome={}, id={}", 
            novoPao.getNomePao(), novoPao.getId());
    }
}
