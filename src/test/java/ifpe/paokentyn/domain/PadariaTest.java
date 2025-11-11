package ifpe.paokentyn.domain;

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

public class PadariaTest {

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
        // Carrega o dataset.xml com IDs altos (101, 201...)
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
    public void testPersistirPadaria() {
        System.out.println("--- Executando testPersistirPadaria (independente) ---");
        
        Padaria novaPadaria = new Padaria();
        novaPadaria.setNome("Pão Kentyn - Filial Agreste");
        novaPadaria.setCep("55000-000");

        em.persist(novaPadaria);
        em.flush(); // Força o INSERT

        // O Derby vai (corretamente) gerar o ID = 1
        // (Já que o 101 está muito longe na sequência)
        assertNotNull(novaPadaria.getId(), "ID não deveria ser nulo após persistir");
        assertEquals(1L, novaPadaria.getId(), "O primeiro ID gerado pelo Identity deve ser 1");

        System.out.println("Padaria persistida com ID: " + novaPadaria.getId());
    }

    @Test
    public void testEncontrarPadariaDoDataSet() {
        System.out.println("--- Executando testEncontrarPadaria (independente) ---");
        
        // Agora procuramos o ID 101, que veio do nosso dataset
        Padaria padaria = em.find(Padaria.class, 101L);
        
        assertNotNull(padaria, "Deveria encontrar a padaria com ID 101 do dataset");
        assertEquals("Padaria do Melhor Teste", padaria.getNome());
        
        System.out.println("Padaria encontrada: " + padaria.getNome());
    }
}