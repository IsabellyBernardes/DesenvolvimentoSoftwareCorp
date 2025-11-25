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
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FornadaTest {

    private static final Logger logger = LoggerFactory.getLogger(FornadaTest.class);
    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        DbUnitUtil.insertData();

        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @AfterEach
    public void tearDown() {
        if (et != null && et.isActive()) {
            et.commit();
        }
        if (em != null) {
            em.close();
        }
    }
    
    @Test
    public void testEncontrarFornadaDoDataSet() {
        logger.info("--- Executando testEncontrarFornadaDoDataSet ---");

        Fornada fornada = em.find(Fornada.class, 401L);

        assertNotNull(fornada, "Fornada 401 deveria existir no dataset");
        assertNotNull(fornada.getDataFornada());
        assertNotNull(fornada.getHoraInicio());

        assertNotNull(fornada.getPadaria(), "A padaria da fornada não deveria ser nula");

        assertEquals("Padaria do Melhor Teste", fornada.getPadaria().getNome());
        assertEquals(101L, fornada.getPadaria().getId());

        logger.info("Encontrada fornada do dia: {}", fornada.getDataFornada());
    }

    @Test
    public void testPersistirFornada() {
        logger.info("--- Executando testPersistirFornada ---");

        Padaria padariaExistente = em.find(Padaria.class, 101L);
        assertNotNull(padariaExistente);

        Fornada novaFornada = new Fornada();
        novaFornada.setDataFornada(new Date());
        novaFornada.setHoraInicio(new Date());
        novaFornada.setPadaria(padariaExistente);

        em.persist(novaFornada);
        em.flush();

        assertNotNull(novaFornada.getId());
        assertTrue(novaFornada.getId() > 0);
        assertNotEquals(401L, novaFornada.getId());

        logger.info("Persistida: Nova fornada com ID {}", novaFornada.getId());
    }

}
