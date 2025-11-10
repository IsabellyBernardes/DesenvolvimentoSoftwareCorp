package ifpe.paokentyn.repository;

import ifpe.paokentyn.domain.Padaria;
// Importações adicionais para os relacionamentos (não utilizados diretamente no teste CRUD simples)
import ifpe.paokentyn.domain.Funcionario;
import ifpe.paokentyn.domain.Fornada;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de Teste para PadariaRepositorio, simulando operações CRUD.
 * (Atenção: A execução real depende da configuração do JPA/Hibernate e da EntityFactory.)
 */
public class PadariaRepositorioTest {

    private PadariaRepositorio repositorio;

    @BeforeEach
    void setup() {
        // Inicializa o Repositório antes de cada teste
        repositorio = new PadariaRepositorio();
    }

    @AfterEach
    void tearDown() {
        // Limpeza após cada teste (Boa prática para isolar os testes)
    }

    // --- Testes de Funcionalidade CRUD ---
    
    @Test
    void testSalvarEEncontrarPorID() {
        System.out.println("Testando salvar e encontrar Padaria por ID...");

        // 1. Cria um novo objeto Padaria
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria Sabores do Pão");
        padaria.setCep("55000-000"); // 9 caracteres, formato válido para @Size(min=9, max=9)
        padaria.setEndereco("Rua Principal, 123");

        // 2. Simula o salvamento
        repositorio.salvar(padaria);
        
        // **AQUI: Assumimos que o ID foi gerado com sucesso (ex: ID 1L)**
        Long idGerado = 1L; 
        
        // 3. Busca a entidade salva
        Padaria padariaEncontrada = repositorio.encontrarPorID(idGerado);

        // 4. Verifica as asserções
        assertNotNull(padariaEncontrada, "A Padaria não deve ser nula após a busca.");
        assertEquals("Padaria Sabores do Pão", padariaEncontrada.getNome(), "O nome deve corresponder.");
        assertEquals("55000-000", padariaEncontrada.getCep(), "O CEP deve corresponder.");
        // As coleções de Funcionários e Fornadas são LAZY e não são carregadas automaticamente
        assertNull(padariaEncontrada.getFuncionarios(), "A lista de funcionários deve ser lazy/nula.");
    }
    
    @Test
    void testBuscarTodos() {
        System.out.println("Testando buscar todas as Padarias...");

        // 1. Setup: Salva duas padarias
        Padaria p1 = new Padaria();
        p1.setNome("Padaria Central");
        p1.setCep("50000-001");
        repositorio.salvar(p1);

        Padaria p2 = new Padaria();
        p2.setNome("Padaria do Bairro");
        p2.setCep("50000-002");
        repositorio.salvar(p2);

        // 2. Busca todos
        List<Padaria> lista = repositorio.buscarTodos();

        // 3. Verifica as asserções
        assertFalse(lista.isEmpty(), "A lista de padarias não deve estar vazia.");
        assertTrue(lista.size() >= 2, "A lista deve conter pelo menos duas Padarias.");
    }

    @Test
    void testAtualizar() {
        System.out.println("Testando atualização de Padaria...");

        // 1. Cria e salva a padaria original
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria Velha");
        padaria.setCep("51000-000");
        repositorio.salvar(padaria);
        
        Long idOriginal = 1L; // Assumindo o ID gerado
        
        // 2. Altera o objeto e simula a atualização
        padaria.setId(idOriginal); // Garante que o ID está no objeto para o merge
        padaria.setNome("Padaria Nova Geração");
        padaria.setEndereco("Nova Avenida, 456");
        repositorio.atualizar(padaria);

        // 3. Busca a padaria atualizada
        Padaria padariaAtualizada = repositorio.encontrarPorID(idOriginal);

        // 4. Verifica a atualização
        assertNotNull(padariaAtualizada, "Padaria deve ser encontrada.");
        assertEquals("Padaria Nova Geração", padariaAtualizada.getNome(), "O nome deve ter sido atualizado.");
        assertEquals("Nova Avenida, 456", padariaAtualizada.getEndereco(), "O endereço deve ter sido atualizado.");
    }
    
    @Test
    void testDeletar() {
        System.out.println("Testando deleção de Padaria...");
        
        // 1. Cria e salva uma padaria
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria a Ser Deletada");
        padaria.setCep("59000-000");
        repositorio.salvar(padaria);
        
        Long idParaDeletar = 1L; // Assumindo o ID gerado

        // 2. Deleta
        repositorio.deletar(idParaDeletar);

        // 3. Tenta encontrar a padaria deletada
        Padaria padariaDeletada = repositorio.encontrarPorID(idParaDeletar);

        // 4. Verifica a deleção
        assertNull(padariaDeletada, "A Padaria não deve ser encontrada após a deleção.");
    }
}
