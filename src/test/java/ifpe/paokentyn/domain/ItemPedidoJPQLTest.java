package ifpe.paokentyn.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemPedidoJPQLTest extends GenericTest {

    /**
     * Teste 1 – Manipulação de String com LIKE Busca itenm pão que tenha
     * "Integral" no nome
     */
    @Test
    public void testBuscarItemPedidoPorNomeDoPaoLike() {
        String jpql = """
            SELECT ip
            FROM ItemPedido ip
            WHERE LOWER(ip.pao.nomePao) LIKE '%integral%'
        """;

        List<ItemPedido> itens = em.createQuery(jpql, ItemPedido.class)
                .getResultList();

        assertFalse(itens.isEmpty());
        assertEquals("Pão Integral", itens.get(0).getPao().getNomePao());
    }

    /**
     * Teste 2 – Manipulação de String com LENGTH Busca itens cujo nome do pão
     * tem mais de 10 caracteres
     */
    @Test
    public void testBuscarItemPedidoPorTamanhoNomePao() {
        String jpql = """
            SELECT ip
            FROM ItemPedido ip
            WHERE LENGTH(ip.pao.nomePao) > 10
        """;

        List<ItemPedido> itens = em.createQuery(jpql, ItemPedido.class)
                .getResultList();

        assertFalse(itens.isEmpty());
        assertEquals(3, itens.size());
    }

    /**
     * Teste 3 – AVG Média da quantidade de itens por pedido
     */
    @Test
    public void testMediaQuantidadeItensPedido() {
        String jpql = """
            SELECT AVG(ip.quantidade)
            FROM ItemPedido ip
        """;

        Double media = em.createQuery(jpql, Double.class)
                .getSingleResult();

        assertNotNull(media);
        assertTrue(media > 0);
        assertEquals(6, media);
    }

    /**
     * Teste 4 – BETWEEN usando data do Pedido Busca itens de pedidos feitos em
     * um intervalo de datas
     */
    @Test
    public void testBuscarItemPedidoPorPeriodoDoPedido() {
        String jpql = """
        SELECT ip
        FROM ItemPedido ip
        WHERE ip.pedido.dataPedido BETWEEN :inicio AND :fim
    """;

        Calendar cal = Calendar.getInstance();

        cal.set(2025, Calendar.NOVEMBER, 10, 0, 0, 0);
        Date inicio = cal.getTime();

        cal.set(2025, Calendar.NOVEMBER, 15, 23, 59, 59);
        Date fim = cal.getTime();

        List<ItemPedido> itens = em.createQuery(jpql, ItemPedido.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();

        assertEquals(4, itens.size());
    }

    /**
     * Teste 5 – SUM Soma total das quantidades de todos os itens de pedido
     */
    @Test
    public void testSomaTotalQuantidadeItensPedido() {
        String jpql = """
            SELECT SUM(ip.quantidade)
            FROM ItemPedido ip
        """;

        Long soma = em.createQuery(jpql, Long.class)
                .getSingleResult();

        assertNotNull(soma);
        assertTrue(soma > 0);
        assertEquals(25, soma);
    }
}
