package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.ItemPedido;
import ifpe.paokentyn.domain.Pao;
import ifpe.paokentyn.domain.Pedido;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemPedidoPersistenceTest extends GenericTest {

    private ItemPedido criarItemPedidoValido() {
        Pao pao = new Pao();
        pao.setNomePao("Pao de Queijo"); // Nome válido
        pao.setPreco(2.50);
        em.persist(pao);

        Pedido pedido = new Pedido();
        pedido.setDataPedido(new Date());
        pedido.setValorTotal(100.00);
        em.persist(pedido);

        ItemPedido item = new ItemPedido();
        item.setQuantidade(5); 
        item.setPao(pao);      
        item.setPedido(pedido); 
        
        return item;
    }

    @Test
    public void testPersistirItemPedidoValido() {
        ItemPedido item = criarItemPedidoValido();
        
        assertDoesNotThrow(() -> {
            em.persist(item);
            em.flush();
        });
        
        assertNotNull(item.getId());
    }

    @Test
    public void testPersistirQuantidadeZeroDeveFalhar() {
        ItemPedido item = criarItemPedidoValido();
        item.setQuantidade(0); // Inválido (@Min(1))

        assertThrows(Exception.class, () -> {
            em.persist(item);
            em.flush();
        }, "Deveria falhar com quantidade menor que 1");
    }

    @Test
    public void testPersistirSemPaoDeveFalhar() {
        ItemPedido item = criarItemPedidoValido();
        item.setPao(null); // Inválido (@NotNull)

        assertThrows(Exception.class, () -> {
            em.persist(item);
            em.flush();
        }, "Deveria falhar ao persistir item sem pão associado");
    }

    @Test
    public void testPersistirSemPedidoDeveFalhar() {
        ItemPedido item = criarItemPedidoValido();
        item.setPedido(null); // Inválido (@NotNull)

        assertThrows(Exception.class, () -> {
            em.persist(item);
            em.flush();
        }, "Deveria falhar ao persistir item sem pedido associado");
    }

    @Test
    public void testAtualizarQuantidadeParaNegativoDeveFalhar() {
        // 1. Salva item válido (Quantidade = 5)
        ItemPedido item = criarItemPedidoValido();
        em.persist(item);
        em.flush();

        // 2. Tenta ATUALIZAR para valor inválido
        item.setQuantidade(-10); // Viola @Min(1)

        assertThrows(Exception.class, () -> {
            em.merge(item);
            em.flush();
        }, "Deveria falhar ao atualizar item para quantidade negativa");
    }
}