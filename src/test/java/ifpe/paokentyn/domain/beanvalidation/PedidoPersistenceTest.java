package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Pedido;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PedidoPersistenceTest extends GenericTest {

    private Pedido criarPedidoValido() {
        Pedido p = new Pedido();
        p.setValorTotal(150.50);
        p.setDataPedido(new Date()); // Data atual (válido)
        return p;
    }

    @Test
    public void testPersistirPedidoValido() {
        Pedido p = criarPedidoValido();
        assertDoesNotThrow(() -> {
            em.persist(p);
            em.flush();
        });
    }

    @Test
    public void testPersistirValorNegativoDeveFalhar() {
        Pedido p = criarPedidoValido();
        p.setValorTotal(-10.00); // Inválido (@PositiveOrZero)

        assertThrows(Exception.class, () -> {
            em.persist(p);
            em.flush();
        }, "Deveria falhar com valor total negativo");
    }

    @Test
    public void testAtualizarDataParaFuturoDeveFalhar() {
        Pedido p = criarPedidoValido();
        em.persist(p);
        em.flush();

        // 2. Atualiza para amanhã (Futuro -> Inválido @PastOrPresent)
        Calendar amanha = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_MONTH, 1);
        p.setDataPedido(amanha.getTime());

        assertThrows(Exception.class, () -> {
            em.merge(p);
            em.flush();
        }, "Deveria falhar ao atualizar data do pedido para o futuro");
    }
}