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

public class PedidoTest {

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
    public void testEncontrarPedidoDoDataSet() {
        System.out.println("--- Executando testEncontrarPedidoDoDataSet ---");

        // 1. Busca o Pedido ID 601 (do seu dataset.xml)
        Pedido pedido = em.find(Pedido.class, 601L); 

        // 2. Verifica os dados dele
        assertNotNull(pedido, "Pedido 601 deveria existir no dataset");
        assertEquals(70.00, pedido.getValorTotal());

        System.out.println("Encontrado: Pedido " + pedido.getId() + " com valor " + pedido.getValorTotal());
    }

    @Test
    public void testPersistirPedido() {
        System.out.println("--- Executando testPersistirPedido ---");

        // 1. Criamos o novo pedido
        Pedido novoPedido = new Pedido();
        novoPedido.setDataPedido(new Date()); // data de "agora"
        novoPedido.setValorTotal(15.25);

        // 2. Persistimos o novo pedido
        em.persist(novoPedido);
        em.flush(); // Força o INSERT

        // 3. Verificamos se ele foi salvo corretamente
        assertNotNull(novoPedido.getId(), "ID do novo pedido não pode ser nulo");
        assertTrue(novoPedido.getId() > 0, "ID deve ser positivo");
        assertNotEquals(601L, novoPedido.getId(), "ID não deve ser o mesmo do dataset");

        System.out.println("Persistido: Novo pedido com ID: " + novoPedido.getId());
    }
}