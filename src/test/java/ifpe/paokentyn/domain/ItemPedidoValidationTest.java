//package ifpe.paokentyn.domain;
//
//import jakarta.validation.ConstraintViolation;
//import java.util.Date;
//import java.util.Set;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//
//public class ItemPedidoValidationTest extends AbstractValidationTest {
//
//    private ItemPedido criarItemPedidoValido() {
//        ItemPedido item = new ItemPedido();
//        
//        item.setQuantidade(10); 
//        
//        Pao paoMock = new Pao();
//        paoMock.setNomePao("Pão Francês");
//        paoMock.setPreco(1.50); 
//        item.setPao(paoMock);
//        
//        Padaria padariaMock = new Padaria();
//        padariaMock.setNome("Padaria Central");
//        padariaMock.setCnpj("05604099000154"); // CNPJ Válido
//        padariaMock.setCep("64000-450"); // CEP Válido
//        
//        Fornada fornadaMock = new Fornada();
//        fornadaMock.setDataFornada(new Date());
//        fornadaMock.setPadaria(padariaMock); 
//        
//        item.setFornada(fornadaMock); 
//        
//        Pedido pedidoMock = new Pedido();
//        pedidoMock.setDataPedido(new Date()); // Apenas uma data válida para o pedido
//        item.setPedido(pedidoMock); // Ligando o item ao pedido
//        
//        return item;
//    }
//
//    @Test
//    public void testItemPedidoValido() {
//        ItemPedido item = criarItemPedidoValido();
//
//        Set<ConstraintViolation<ItemPedido>> violacoes = validator.validate(item);
//        assertTrue(violacoes.isEmpty(), "Um item completo com toda a árvore de dependências deve ser válido");
//    }
//
//    @Test
//    public void testQuantidadeZeroInvalida() {
//        ItemPedido item = criarItemPedidoValido();
//        
//        item.setQuantidade(0); 
//
//        Set<ConstraintViolation<ItemPedido>> violacoes = validator.validate(item);
//        assertEquals(1, violacoes.size(), "Deveria barrar EXATAMENTE 1 erro (quantidade zero)");
//    }
//
//    @Test
//    public void testQuantidadeNegativaInvalida() {
//        ItemPedido item = criarItemPedidoValido();
//        
//        item.setQuantidade(-5); 
//
//        Set<ConstraintViolation<ItemPedido>> violacoes = validator.validate(item);
//        assertEquals(1, violacoes.size(), "Deveria barrar EXATAMENTE 1 erro (quantidade negativa)");
//    }
//}