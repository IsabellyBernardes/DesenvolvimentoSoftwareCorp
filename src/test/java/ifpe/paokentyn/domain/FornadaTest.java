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

public class FornadaTest {

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
        // Carrega o dataset.xml ANTES de cada teste
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

    // --- Nossos testes começam aqui ---

    @Test
    public void testEncontrarFornadaDoDataSet() {
        System.out.println("--- Executando testEncontrarFornadaDoDataSet ---");

        // 1. Busca a Fornada ID 401 (do seu dataset.xml)
        Fornada fornada = em.find(Fornada.class, 401L); 

        // 2. Verifica os dados dela
        assertNotNull(fornada, "Fornada 401 deveria existir no dataset");
        assertNotNull(fornada.getDataFornada()); // Podemos testar a data exata se precisarmos
        assertNotNull(fornada.getHoraInicio());

        // 3. VERIFICA O RELACIONAMENTO @ManyToOne
        assertNotNull(fornada.getPadaria(), "A padaria da fornada não deveria ser nula");
        
        // Testa o FetchType.LAZY. 
        // Ao chamar .getNome(), o JPA faz o SELECT na TB_PADARIA.
        assertEquals("Padaria do Melhor Teste", fornada.getPadaria().getNome());
        assertEquals(101L, fornada.getPadaria().getId());
        
        System.out.println("Encontrada fornada do dia: " + fornada.getDataFornada());
    }

    @Test
    public void testPersistirFornada() {
        System.out.println("--- Executando testPersistirFornada ---");

        // 1. PRECISAMOS DE UMA PADARIA (a dona do relacionamento)
        // Buscamos a padaria 101 que o DBUnit inseriu
        Padaria padariaExistente = em.find(Padaria.class, 101L); 
        assertNotNull(padariaExistente, "Padaria 101 (do dataset) não foi encontrada");

        // 2. Criamos a nova fornada
        Fornada novaFornada = new Fornada();
        novaFornada.setDataFornada(new Date()); // data de "agora"
        novaFornada.setHoraInicio(new Date()); // hora de "agora"

        // 3. Associamos a fornada à padaria
        novaFornada.setPadaria(padariaExistente);

        // 4. Persistimos a nova fornada
        em.persist(novaFornada);
        em.flush(); // Força o INSERT

        // 5. Verificamos se ela foi salva corretamente
        assertNotNull(novaFornada.getId(), "ID da nova fornada não pode ser nulo");
        assertTrue(novaFornada.getId() > 0, "ID deve ser positivo");
        assertNotEquals(401L, novaFornada.getId(), "ID não deve ser o mesmo do dataset");

        System.out.println("Persistida: Nova fornada com ID: " + novaFornada.getId());
    }
}