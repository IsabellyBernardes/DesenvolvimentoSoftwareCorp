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

public class PaoTest {

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
    public void testEncontrarPaoDoDataSet() {
        System.out.println("--- Executando testEncontrarPaoDoDataSet ---");

        // 1. Busca o Pão ID 301 (do seu dataset.xml)
        Pao pao = em.find(Pao.class, 301L);

        // 2. Verifica os dados dele
        assertNotNull(pao, "Pão 301 deveria existir no dataset");
        assertEquals("Pão Integral", pao.getNomePao());
        assertEquals(5.50, pao.getPreco());

        System.out.println("Encontrado: " + pao.getNomePao());
    }

    @Test
    public void testPersistirPao() {
        System.out.println("--- Executando testPersistirPao ---");

        // 1. Criamos o novo pão
        Pao novoPao = new Pao();
        novoPao.setNomePao("Pão Francês");
        novoPao.setPreco(0.75);

        // 2. Persistimos o novo pão
        em.persist(novoPao); 
        em.flush(); // Força o INSERT

        // 3. Verificamos se ele foi salvo corretamente
        assertNotNull(novoPao.getId(), "ID do novo pão não pode ser nulo");
        assertTrue(novoPao.getId() > 0, "ID deve ser positivo");
        
        // Garante que o ID gerado (provavelmente 1) não é um dos IDs do dataset
        assertNotEquals(301L, novoPao.getId());
        assertNotEquals(302L, novoPao.getId());

        System.out.println("Persistido: " + novoPao.getNomePao() + " com ID: " + novoPao.getId());
    }
}