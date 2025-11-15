package ifpe.paokentyn.test;

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

public class PadariaTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;
    
    // CNPJ de exemplo p/ teste de persistência
    private final static String CNPJ_NOVO_TESTE = "11223344000199"; 
    // CNPJ esperado do registro 101 no dataset 
    private final static String CNPJ_DATASET = "99887766000199"; 

    @BeforeAll
    public static void setUpClass() {
        // Inicializa o gerenciador de entidades (EntityManagerFactory)
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        // Fecha o gerenciador de entidades
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        // Carrega o dataset.xml 
        DbUnitUtil.insertData();
        
        // Inicia o contexto de persistência
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @AfterEach
    public void tearDown() {
        // Finaliza a transação com commit
        if (et != null && et.isActive()) {
            et.commit();
        }
        // Fecha o EntityManager
        if (em != null) {
            em.close();
        }
    }

    @Test
    public void testPersistirPadaria() {
        System.out.println("--- Executando testPersistirPadaria (independente) ---");
        
        Padaria novaPadaria = new Padaria();
        novaPadaria.setNome("Pão Kentyn - Filial Agreste");
        novaPadaria.setCep("55000000"); 
        
        // NOVO: Adiciona o CNPJ obrigatório para que a persistência funcione
        novaPadaria.setCnpj(CNPJ_NOVO_TESTE); 

        em.persist(novaPadaria);
        em.flush(); // Força o INSERT para o banco de dados

        assertNotNull(novaPadaria.getId(), "ID não deveria ser nulo após persistir");
        assertEquals(1L, novaPadaria.getId(), "O primeiro ID gerado deve ser 1");
        // Verifica o novo campo obrigatório
        assertEquals(CNPJ_NOVO_TESTE, novaPadaria.getCnpj(), "O CNPJ salvo deve ser o mesmo do objeto.");

        System.out.println("Padaria persistida com ID: " + novaPadaria.getId());
    }

    @Test
    public void testEncontrarPadariaDoDataSet() {
        System.out.println("--- Executando testEncontrarPadariaDoDataSet (independente) ---");
        
        // Busca o ID 101, que veio do no dataset
        Padaria padaria = em.find(Padaria.class, 101L);
        
        assertNotNull(padaria, "Deveria encontrar a padaria com ID 101 do dataset");
        assertEquals("Padaria do Melhor Teste", padaria.getNome());
        // NOVO: Verifica o CNPJ do registro 101
        assertEquals(CNPJ_DATASET, padaria.getCnpj(), "O CNPJ recuperado deve ser o do dataset.");
        
        System.out.println("Padaria encontrada: " + padaria.getNome() + " | CNPJ: " + padaria.getCnpj());
    }
}
