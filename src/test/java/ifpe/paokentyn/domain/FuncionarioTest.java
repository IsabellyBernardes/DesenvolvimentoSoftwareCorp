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

public class FuncionarioTest {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioTest.class);

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

    // --- TESTES ---

    @Test
    public void testEncontrarFuncionarioDoDataSet() {
        logger.info("--- Executando testEncontrarFuncionarioDoDataSet ---");

        Funcionario func = em.find(Funcionario.class, 201L);

        assertNotNull(func, "Funcionário 201 deveria existir no dataset");
        assertEquals("João Silva", func.getNome());
        assertEquals("Padeiro Senior", func.getCargo());

        assertNotNull(func.getPadaria());
        assertEquals("Padaria do Melhor Teste", func.getPadaria().getNome());
        assertEquals(101L, func.getPadaria().getId());

        logger.info("Encontrado funcionário {} da padaria {}", 
            func.getNome(), 
            func.getPadaria().getNome()
        );
    }

    @Test
    public void testPersistirFuncionario() {
        logger.info("--- Executando testPersistirFuncionario ---");

        Padaria padariaExistente = em.find(Padaria.class, 101L);
        assertNotNull(padariaExistente);

        Funcionario novoFunc = new Funcionario();
        novoFunc.setNome("Maria Souza");
        novoFunc.setCargo("Caixa");
        novoFunc.setSalario(2100.00);
        novoFunc.setPadaria(padariaExistente);

        em.persist(novoFunc);
        em.flush();

        assertNotNull(novoFunc.getId());
        assertTrue(novoFunc.getId() > 0);
        assertNotEquals(201L, novoFunc.getId());

        em.clear();

        Funcionario funcDoBanco = em.find(Funcionario.class, novoFunc.getId());
        assertNotNull(funcDoBanco);
        assertEquals("Maria Souza", funcDoBanco.getNome());
        assertEquals(101L, funcDoBanco.getPadaria().getId());

        logger.info("Persistido funcionário {} com ID {}", 
            funcDoBanco.getNome(), 
            funcDoBanco.getId()
        );
    }
}
