package ifpe.paokentyn.repository;

import ifpe.paokentyn.domain.ItemPedido;
// Importações fictícias necessárias para compilar o ItemPedido
import ifpe.paokentyn.domain.Pedido;
import ifpe.paokentyn.domain.Pao;
import ifpe.paokentyn.domain.Fornada;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de Teste para ItemPedidoRepositorio, simulando operações CRUD.
 * (Atenção: A execução real deste teste depende da implementação de EntityFactory e da configuração do JPA/Hibernate.)
 */
public class ItemPedidoRepositorioTest {

    private ItemPedidoRepositorio repositorio;
    private Pedido pedidoMock;
    private Pao paoMock;

    @BeforeEach
    void setup() {
        // 1. Inicializa o Repositório antes de cada teste
        repositorio = new ItemPedidoRepositorio();
        
        // 2. Cria instâncias mock/dummy das entidades relacionadas
        //    (Na vida real, estas entidades precisariam ser salvas primeiro ou mockadas/simuladas)
        pedidoMock = new Pedido(); // Supondo um construtor vazio
        pedidoMock.setId(10L); // Define um ID para simular que está persistido
        paoMock = new Pao();
        paoMock.setId(20L);

        // Se você estiver usando um ambiente de teste real (como H2 e JPA), 
        // os objetos pedidoMock e paoMock precisariam ser persistidos antes de serem usados.
    }

    @AfterEach
    void tearDown() {
        // Limpeza após cada teste (opcional, dependendo do ambiente de teste)
        // Por exemplo, deletar todos os dados inseridos no banco de dados de teste.
    }

    // --- Testes de Funcionalidade CRUD ---
    
    @Test
    void testSalvarEEncontrarPorID() {
        System.out.println("Testando salvar e encontrar ItemPedido por ID...");

        // 1. Cria um novo ItemPedido
        ItemPedido item = new ItemPedido();
        item.setQuantidade(5);
        item.setPedido(pedidoMock);
        item.setPao(paoMock);
        // fornada é opcional, deixamos como null

        // 2. Simula o salvamento
        repositorio.salvar(item);
        
        // **AQUI: O ID REAL DEVERIA SER POPULADO PELO JPA APÓS O em.persist()**
        // Em um teste real, precisaríamos buscar o item por algum critério ou usar o ID gerado.
        // Assumindo que o ID foi gerado com sucesso (ex: ID 1L)
        Long idGerado = 1L; 
        
        // 3. Busca o item salvo
        ItemPedido itemEncontrado = repositorio.encontrarPorID(idGerado);

        // 4. Verifica as asserções
        assertNotNull(itemEncontrado, "O ItemPedido não deve ser nulo após a busca.");
        assertEquals(5, itemEncontrado.getQuantidade(), "A quantidade deve ser 5.");
        assertEquals(pedidoMock.getId(), itemEncontrado.getPedido().getId(), "O ID do Pedido deve corresponder.");
    }
    
    @Test
    void testBuscarTodos() {
        System.out.println("Testando buscar todos os ItemPedidos...");

        // 1. Setup: Salva dois itens (assumindo que o banco de dados está limpo)
        ItemPedido item1 = new ItemPedido();
        item1.setQuantidade(2);
        item1.setPedido(pedidoMock);
        item1.setPao(paoMock);

        ItemPedido item2 = new ItemPedido();
        item2.setQuantidade(10);
        item2.setPedido(pedidoMock);
        item2.setPao(paoMock);

        repositorio.salvar(item1);
        repositorio.salvar(item2);

        // 2. Busca todos
        List<ItemPedido> lista = repositorio.buscarTodos();

        // 3. Verifica as asserções
        assertFalse(lista.isEmpty(), "A lista de itens não deve estar vazia.");
        // Assumindo que apenas 2 itens foram salvos no setup
        assertTrue(lista.size() >= 2, "A lista deve conter pelo menos dois ItemPedidos.");
    }

    @Test
    void testAtualizar() {
        System.out.println("Testando atualização de ItemPedido...");

        // 1. Cria e salva o item original
        ItemPedido item = new ItemPedido();
        item.setQuantidade(3);
        item.setPedido(pedidoMock);
        item.setPao(paoMock);
        repositorio.salvar(item);
        
        Long idOriginal = 1L; // Assumindo o ID gerado
        
        // 2. Altera o objeto e simula a atualização
        item.setId(idOriginal); // Garante que o ID está no objeto para o merge
        item.setQuantidade(7);
        repositorio.atualizar(item);

        // 3. Busca o item atualizado
        ItemPedido itemAtualizado = repositorio.encontrarPorID(idOriginal);

        // 4. Verifica a atualização
        assertNotNull(itemAtualizado, "Item deve ser encontrado.");
        assertEquals(7, itemAtualizado.getQuantidade(), "A quantidade deve ter sido atualizada para 7.");
    }
    
    @Test
    void testDeletar() {
        System.out.println("Testando deleção de ItemPedido...");
        
        // 1. Cria e salva um item
        ItemPedido item = new ItemPedido();
        item.setQuantidade(1);
        item.setPedido(pedidoMock);
        item.setPao(paoMock);
        repositorio.salvar(item);
        
        Long idParaDeletar = 1L; // Assumindo o ID gerado

        // 2. Deleta
        repositorio.deletar(idParaDeletar);

        // 3. Tenta encontrar o item deletado
        ItemPedido itemDeletado = repositorio.encontrarPorID(idParaDeletar);

        // 4. Verifica a deleção
        assertNull(itemDeletado, "O ItemPedido não deve ser encontrado após a deleção.");
    }
}
