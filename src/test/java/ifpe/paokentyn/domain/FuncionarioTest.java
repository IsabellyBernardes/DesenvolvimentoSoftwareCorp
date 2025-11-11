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

public class FuncionarioTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll // Roda UMA VEZ ANTES de todos os testes
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll // Roda UMA VEZ DEPOIS de todos os testes
    public static void tearDownClass() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach // Roda ANTES de CADA teste (@Test)
    public void setUp() {
        // Carrega o dataset.xml ANTES de cada teste
        DbUnitUtil.insertData();
        
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @AfterEach // Roda DEPOIS de CADA teste (@Test)
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
    public void testEncontrarFuncionarioDoDataSet() {
        System.out.println("--- Executando testEncontrarFuncionarioDoDataSet ---");

        // 1. Busca o funcionário ID 201 (do seu dataset.xml)
        Funcionario func = em.find(Funcionario.class, 201L); 

        // 2. Verifica os dados dele
        assertNotNull(func, "Funcionário 201 deveria existir no dataset");
        assertEquals("João Silva", func.getNome());
        assertEquals("Padeiro Senior", func.getCargo());

        // 3. VERIFICA O RELACIONAMENTO @ManyToOne
        assertNotNull(func.getPadaria(), "A padaria do funcionário não deveria ser nula");
        
        // Estamos testando o FetchType.LAZY aqui. 
        // Ao chamar .getNome(), o JPA faz o SELECT na TB_PADARIA.
        assertEquals("Padaria do Melhor Teste", func.getPadaria().getNome());
        assertEquals(101L, func.getPadaria().getId());
        
        System.out.println("Encontrado: " + func.getNome() + " na padaria " + func.getPadaria().getNome());
    }

    @Test
    public void testPersistirFuncionario() {
        System.out.println("--- Executando testPersistirFuncionario ---");

        // 1. PRECISAMOS DE UMA PADARIA (a dona do relacionamento)
        // Buscamos a padaria 101 que o DBUnit inseriu
        Padaria padariaExistente = em.find(Padaria.class, 101L);
        assertNotNull(padariaExistente, "Padaria 101 (do dataset) não foi encontrada");

        // 2. Criamos o novo funcionário
        Funcionario novoFunc = new Funcionario();
        novoFunc.setNome("Maria Souza");
        novoFunc.setCargo("Caixa");
        novoFunc.setSalario(2100.00);
        //novoFunc.setDataContratacao(new Date()); // Você pode adicionar isso

        // 3. Associamos o funcionário à padaria
        novoFunc.setPadaria(padariaExistente);

        // 4. Persistimos o novo funcionário
        em.persist(novoFunc); 
        em.flush(); // Força o INSERT

        // 5. Verificamos se ele foi salvo corretamente
        assertNotNull(novoFunc.getId(), "ID do novo funcionário não pode ser nulo");
        assertTrue(novoFunc.getId() > 0, "ID deve ser positivo");
        assertNotEquals(201L, novoFunc.getId(), "ID não deve ser o mesmo do dataset");

        // 6. [BÔNUS] Verificação pós-persistência: Limpa o cache e busca de novo
        em.clear(); // Limpa o cache de persistência
        
        Funcionario funcDoBanco = em.find(Funcionario.class, novoFunc.getId());
        assertNotNull(funcDoBanco);
        assertEquals("Maria Souza", funcDoBanco.getNome());
        assertEquals(101L, funcDoBanco.getPadaria().getId(), "A chave estrangeira foi salva corretamente");
        
        System.out.println("Persistido: " + funcDoBanco.getNome() + " com ID: " + funcDoBanco.getId());
    }
}