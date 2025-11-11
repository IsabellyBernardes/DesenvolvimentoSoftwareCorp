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
import java.util.Date; // Para o teste de persistência

public class TarefaTest {

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
    public void testEncontrarTarefaDoDataSet() {
        System.out.println("--- Executando testEncontrarTarefaDoDataSet ---");

        // 1. Busca a Tarefa ID 501 (do seu dataset.xml)
        Tarefa tarefa = em.find(Tarefa.class, 501L); 

        // 2. Verifica os dados dela
        assertNotNull(tarefa, "Tarefa 501 deveria existir no dataset");
        assertEquals("Checar estoque de farinha", tarefa.getDescricao());
        assertEquals(false, tarefa.getConcluida());

        // 3. VERIFICA A CADEIA DE RELACIONAMENTO
        assertNotNull(tarefa.getFuncionario(), "O funcionário da tarefa não deveria ser nulo");
        assertEquals(201L, tarefa.getFuncionario().getId());
        assertEquals("João Silva", tarefa.getFuncionario().getNome());
        
        // 4. VERIFICA A CADEIA COMPLETA 
        assertNotNull(tarefa.getFuncionario().getPadaria(), "A padaria do funcionário não deveria ser nula");
        assertEquals(101L, tarefa.getFuncionario().getPadaria().getId());
        assertEquals("Padaria do Melhor Teste", tarefa.getFuncionario().getPadaria().getNome());
        
        System.out.println("Encontrada: '" + tarefa.getDescricao() + "' para o func. " + tarefa.getFuncionario().getNome());
    }

    @Test
    public void testPersistirTarefa() {
        System.out.println("--- Executando testPersistirTarefa ---");

        // 1. PRECISAMOS DE UM FUNCIONÁRIO (o dono do relacionamento)
        // Buscamos o funcionário 201 que o DBUnit inseriu
        Funcionario funcExistente = em.find(Funcionario.class, 201L); 
        assertNotNull(funcExistente, "Funcionário 201 (do dataset) não foi encontrado");

        // 2. Criamos a nova tarefa
        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setDescricao("Limpar forno");
        novaTarefa.setDataInicio(new Date()); // data de "agora"
        novaTarefa.setDataPrevisao(new Date(System.currentTimeMillis() + 86400000)); // data de "amanhã"
        novaTarefa.setConcluida(false);

        // 3. Associamos a tarefa ao funcionário
        novaTarefa.setFuncionario(funcExistente);

        // 4. Persistimos a nova tarefa
        em.persist(novaTarefa); 
        em.flush(); // Força o INSERT

        // 5. Verificamos se ela foi salva corretamente
        assertNotNull(novaTarefa.getId(), "ID da nova tarefa não pode ser nulo");
        assertTrue(novaTarefa.getId() > 0, "ID deve ser positivo");
        assertNotEquals(501L, novaTarefa.getId(), "ID não deve ser o mesmo do dataset");

        System.out.println("Persistida: '" + novaTarefa.getDescricao() + "' com ID: " + novaTarefa.getId());
    }
}