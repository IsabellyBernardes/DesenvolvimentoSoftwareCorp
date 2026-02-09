//package ifpe.paokentyn.domain;
//
//import jakarta.validation.ConstraintViolation;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Set;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//
//public class PedidoValidationTest extends AbstractValidationTest {
//
//    private Pedido criarPedidoValido() {
//        Pedido pedido = new Pedido();
//        
//        pedido.setDataPedido(new Date()); 
//        
//        pedido.setValorTotal(50.50); 
//        
//
//        return pedido;
//    }
//
//    @Test
//    public void testPedidoValido() {
//        Pedido pedido = criarPedidoValido();
//
//        Set<ConstraintViolation<Pedido>> violacoes = validator.validate(pedido);
//        assertTrue(violacoes.isEmpty(), "Um pedido com data atual e valor positivo deve ser válido");
//    }
//
//    @Test
//    public void testPedidoNoFuturoInvalido() {
//        Pedido pedido = criarPedidoValido();
//        
//        Calendar cal = Calendar.getInstance();
//        cal.set(2099, Calendar.DECEMBER, 31);
//        pedido.setDataPedido(cal.getTime());
//
//        Set<ConstraintViolation<Pedido>> violacoes = validator.validate(pedido);
//        
//        // A anotação @PastOrPresent deve barrar
//        assertEquals(1, violacoes.size(), "Deveria barrar pedido com data no futuro");
//    }
//
//    @Test
//    public void testPedidoComValorNegativoInvalido() {
//        Pedido pedido = criarPedidoValido();
//        
//        // Testando um valor negativo
//        pedido.setValorTotal(-10.00); 
//
//        Set<ConstraintViolation<Pedido>> violacoes = validator.validate(pedido);
//        
//        // A anotação @Positive deve barrar
//        assertEquals(1, violacoes.size(), "Deveria barrar pedido com valor total negativo");
//    }
//}