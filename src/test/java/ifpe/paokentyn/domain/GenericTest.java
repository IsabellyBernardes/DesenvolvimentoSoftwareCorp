package ifpe.paokentyn.domain;

import ifpe.paokentyn.util.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericTest {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected EntityManagerFactory emf;
    protected EntityManager em;
    protected EntityTransaction et;

    @BeforeEach
    public void setUp() {
        logger.info("--> [GenericTest] Recriando banco (Drop & Create)...");
        emf = Persistence.createEntityManagerFactory("DSC");
        
        logger.info("--> [GenericTest] Inserindo dados do DbUnit...");
        DbUnitUtil.insertData();
        
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
        logger.info("--> [GenericTest] Transação iniciada.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("--> [GenericTest] Finalizando recursos...");
        
        if (et != null && et.isActive()) {
            et.commit();
        }
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
}