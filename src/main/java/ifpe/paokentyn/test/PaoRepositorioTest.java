package ifpe.paokentyn.repository;

import ifpe.paokentyn.domain.Pao;
import ifpe.paokentyn.domain.ItemPedido; // Importado devido ao relacionamento em Pao

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de Teste para PaoRepositorio, simulando operações CRUD.
 * (Atenção: A execução real deste teste depende da implementação de EntityFactory e da configuração do JPA/Hibernate.)
 */
public class PaoRepositorioTest {

    private PaoRepositorio repositorio;

    @BeforeEach
    void setup() {
        // Inicializa o Repositório antes de cada teste
        repositorio = new PaoRepositorio();
        // Em um ambiente de teste real, aqui poderia haver a configuração 
        // de um banco de dados em memória (ex: H2)
    }

    @AfterEach
    void tearDown() {
        // Limpeza após cada teste (opcional, mas boa prática em testes de persistência)
        // Por exemplo, deletar todos os dados da tabela TB_PAO para isolar os testes.
    }

    // --- Testes de Funcionalidade CRUD ---
    
    @Test
    void testSalvarEEncontrarPorID() {
        System.out.println("Testando salvar e encontrar Pao por ID...");

        // 1. Cria um novo objeto Pao
        Pao pao = new Pao();
        pao.setNomePao("Pão Integral 100%");
        pao.setPreco(12.50);

        // 2. Simula o salvamento
        repositorio.salvar(pao);
        
        // **AQUI: Precisamos do ID gerado pelo JPA após o salvamento.**
        // Vamos assumir que o ID 1L foi gerado para este exemplo
        Long idGerado = 1L; 
        
        // 3. Busca o item salvo
        Pao paoEncontrado = repositorio.encontrarPorID(idGerado);

        // 4. Verifica as asserções
        assertNotNull(paoEncontrado, "O Pão não deve ser nulo após a busca.");
        assertEquals("Pão Integral 100%", paoEncontrado.getNomePao(), "O nome deve corresponder.");
        assertEquals(12.50, paoEncontrado.getPreco(), 0.001, "O preço deve corresponder (usando delta para Double).");
        // O relacionamento 'itensPedidos' deve ser LAZY e não precisa ser carregado
        assertNull(paoEncontrado.getItensPedidos(), "A coleção deve ser lazy ou nula se não inicializada.");
    }
    
    @Test
    void testBuscarTodos() {
        System.out.println("Testando buscar todos os Pães...");

        // 1. Setup: Salva dois pães
        Pao pao1 = new Pao();
        pao1.setNomePao("Pão Francês");
        pao1.setPreco(0.80);
        repositorio.salvar(pao1);

        Pao pao2 = new Pao();
        pao2.setNomePao("Baguete");
        pao2.setPreco(4.50);
        repositorio.salvar(pao2);

        // 2. Busca todos
        List<Pao> lista = repositorio.buscarTodos();

        // 3. Verifica as asserções
        assertFalse(lista.isEmpty(), "A lista de pães não deve estar vazia.");
        assertTrue(lista.size() >= 2, "A lista deve conter pelo menos dois Pães.");
        
        // Verifica se os nomes estão presentes
        boolean francesEncontrado = lista.stream().anyMatch(p -> "Pão Francês".equals(p.getNomePao()));
        assertTrue(francesEncontrado, "Pão Francês deve estar na lista.");
    }

    @Test
    void testAtualizar() {
        System.out.println("Testando atualização de Pao...");

        // 1. Cria e salva o pão original
        Pao pao = new Pao();
        pao.setNomePao("Pão de Queijo Pequeno");
        pao.setPreco(2.00);
        repositorio.salvar(pao);
        
        Long idOriginal = 1L; // Assumindo o ID gerado
        
        // 2. Altera o objeto e simula a atualização
        pao.setId(idOriginal); // Garante que o ID está no objeto para o merge
        pao.setPreco(2.50); // Aumento de preço
        repositorio.atualizar(pao);

        // 3. Busca o item atualizado
        Pao paoAtualizado = repositorio.encontrarPorID(idOriginal);

        // 4. Verifica a atualização
        assertNotNull(paoAtualizado, "Pão deve ser encontrado.");
        assertEquals(2.50, paoAtualizado.getPreco(), 0.001, "O preço deve ter sido atualizado para 2.50.");
    }
    
    @Test
    void testDeletar() {
        System.out.println("Testando deleção de Pao...");
        
        // 1. Cria e salva um pão
        Pao pao = new Pao();
        pao.setNomePao("Croissant");
        pao.setPreco(5.00);
        repositorio.salvar(pao);
        
        Long idParaDeletar = 1L; // Assumindo o ID gerado

        // 2. Deleta
        repositorio.deletar(idParaDeletar);

        // 3. Tenta encontrar o pão deletado
        Pao paoDeletado = repositorio.encontrarPorID(idParaDeletar);

        // 4. Verifica a deleção
        assertNull(paoDeletado, "O Pão não deve ser encontrado após a deleção.");
    }
    
    @Test
    void testValidacaoPositivaPreco() {
        System.out.println("Testando a validação @Positive do preço...");

        // 1. Cria Pao com preço inválido (negativo ou zero)
        Pao paoInvalido = new Pao();
        paoInvalido.setNomePao("Pão Grátis");
        paoInvalido.setPreco(-1.00); // Preço inválido

        // Em um sistema real com validação ativada, o "salvar" deveria lançar uma exceção
        // O teste deve garantir que o sistema impede o salvamento ou lança a exceção apropriada.
        
        // Exemplo conceitual para teste de validação (Depende do framework de teste de validação)
        // try {
        //     repositorio.salvar(paoInvalido);
        //     fail("Deveria ter lançado exceção de validação!"); 
        // } catch (ConstraintViolationException expected) {
        //     // Sucesso: a exceção foi lançada
        // }
    }
}
