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

public class TarefaTest {

    private static final Logger logger = LoggerFactory.getLogger(TarefaTest.class);
    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory para testes de Tarefa");
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Fechando EntityManagerFactory");
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        logger.debug("Configurando teste - inserindo dados do dataset");
        DbUnitUtil.insertData();
        
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
        logger.debug("Transação iniciada");
    }

    @AfterEach
    public void tearDown() {
        logger.debug("Finalizando teste");
        if (et != null && et.isActive()) {
            et.commit();
            logger.debug("Transação commitada");
        }
        if (em != null) {
            em.close();
            logger.debug("EntityManager fechado");
        }
    }

    @Test
    public void testEncontrarTarefaDoDataSet() {
        logger.info("Executando testEncontrarTarefaDoDataSet");

        Tarefa tarefa = em.find(Tarefa.class, 501L); 
        logger.debug("Tarefa buscada com ID 501: {}", tarefa);

        assertNotNull(tarefa, "Tarefa 501 deveria existir no dataset");
        assertEquals("Checar estoque de farinha", tarefa.getDescricao());
        assertEquals(false, tarefa.getConcluida());

        assertNotNull(tarefa.getFuncionario(), "O funcionário da tarefa não deveria ser nulo");
        assertEquals(201L, tarefa.getFuncionario().getId());
        assertEquals("João Silva", tarefa.getFuncionario().getNome());
        
        assertNotNull(tarefa.getFuncionario().getPadaria(), "A padaria do funcionário não deveria ser nula");
        assertEquals(101L, tarefa.getFuncionario().getPadaria().getId());
        assertEquals("Padaria do Melhor Teste", tarefa.getFuncionario().getPadaria().getNome());
        
        logger.info("Tarefa encontrada: '{}' para o funcionário {}", 
                   tarefa.getDescricao(), tarefa.getFuncionario().getNome());
        logger.info("Padaria associada: {}", tarefa.getFuncionario().getPadaria().getNome());
    }

    @Test
    public void testPersistirTarefa() {
        logger.info("Executando testPersistirTarefa");

        Funcionario funcExistente = em.find(Funcionario.class, 201L); 
        logger.debug("Funcionário buscado com ID 201: {}", funcExistente);
        assertNotNull(funcExistente, "Funcionário 201 (do dataset) não foi encontrado");

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setDescricao("Limpar forno");
        novaTarefa.setDataInicio(new Date());
        novaTarefa.setDataPrevisao(new Date(System.currentTimeMillis() + 86400000));
        novaTarefa.setConcluida(false);

        novaTarefa.setFuncionario(funcExistente);

        logger.debug("Persistindo nova tarefa: {}", novaTarefa);
        em.persist(novaTarefa); 
        em.flush(); 
        logger.debug("Tarefa persistida com sucesso");

        assertNotNull(novaTarefa.getId(), "ID da nova tarefa não pode ser nulo");
        assertTrue(novaTarefa.getId() > 0, "ID deve ser positivo");
        assertNotEquals(501L, novaTarefa.getId(), "ID não deve ser o mesmo do dataset");

        logger.info("Tarefa persistida: '{}' com ID: {}", 
                   novaTarefa.getDescricao(), novaTarefa.getId());
    }
}