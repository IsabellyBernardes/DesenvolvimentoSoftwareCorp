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
import java.util.Date;

public class PedidoTest {

    private static final Logger logger = LoggerFactory.getLogger(PedidoTest.class);

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory para testes de Pedido...");
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Encerrando EntityManagerFactory...");
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
        logger.info("Finalizando transação e fechando EntityManager...");
        if (et != null && et.isActive()) {
            et.commit();
        }
        if (em != null) {
            em.close();
        }
    }


    @Test
    public void testEncontrarPedidoDoDataSet() {
        logger.info("--- Executando testEncontrarPedidoDoDataSet ---");

        Pedido pedido = em.find(Pedido.class, 601L);

        assertNotNull(pedido, "Pedido 601 deveria existir no dataset");
        assertEquals(70.00, pedido.getValorTotal());

        logger.info("Pedido encontrado no dataset: id={}, valor={}",
                pedido.getId(), pedido.getValorTotal());
    }

    @Test
    public void testPersistirPedido() {
        logger.info("--- Executando testPersistirPedido ---");

        Pedido novoPedido = new Pedido();
        novoPedido.setDataPedido(new Date());
        novoPedido.setValorTotal(15.25);

        logger.info("Persistindo novo pedido: data={}, valor={}",
                novoPedido.getDataPedido(), novoPedido.getValorTotal());

        em.persist(novoPedido);
        em.flush();

        assertTrue(novoPedido.getId() > 0, "ID deve ser positivo");
        assertNotEquals(601L, novoPedido.getId(), "ID não deve ser o mesmo do dataset");

        logger.info("Novo pedido persistido com sucesso: id={}", novoPedido.getId());
    }
}
