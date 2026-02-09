package ifpe.paokentyn.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericTest {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static EntityManagerFactory emf;
    protected EntityManager em;
    protected EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        // Cria a fábrica de conexões apenas uma vez para todos os testes
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin(); // Inicia a transação antes de cada teste
    }

    @AfterEach
    public void tearDown() {
        // Independente se o teste passou ou falhou, sempre desfazer (Rollback)
        // Isso garante que o banco fique limpo para o próximo teste
        if (et != null && et.isActive()) {
            et.rollback();
        }
        
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}