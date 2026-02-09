package ifpe.paokentyn.domain;

import ifpe.paokentyn.util.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericTest {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static EntityManagerFactory emf;
    protected EntityManager em;
    protected EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        System.out.println("--> [GenericTest] Criando o Banco de Dados do zero (1 vez)...");
        emf = Persistence.createEntityManagerFactory("DSC"); // Certifique-se que o nome da PU está correto
    }

    @AfterAll
    public static void tearDownClass() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        
        // 1. Inicia transação para limpeza e carga
        em.getTransaction().begin();
        logger.info("--> [GenericTest] Limpando dados antigos...");
        try {
            // Ordem de deleção: Filhos primeiro, Pais depois (para evitar erro de FK)
            em.createNativeQuery("DELETE FROM TB_ITEM_PEDIDO").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_PAO_INGREDIENTE").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_TAREFA").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_PEDIDO").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_FORNADA").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_PAO").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_INGREDIENTE").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_FUNCIONARIO").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_PADARIA").executeUpdate();
            em.createNativeQuery("DELETE FROM TB_DADOS_BANCARIOS").executeUpdate();

            // Reset de IDs (Sintaxe H2 Database - se usar MySQL muda um pouco)
            logger.info("--> [GenericTest] Resetando IDs para 1...");
            em.createNativeQuery("ALTER TABLE TB_PADARIA ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_DADOS_BANCARIOS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_FUNCIONARIO ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_PAO ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_INGREDIENTE ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_FORNADA ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_PEDIDO ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_ITEM_PEDIDO ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER TABLE TB_TAREFA ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            
            // Commita a limpeza para garantir banco zerado
            em.getTransaction().commit(); 
        } catch (Exception e) {
            logger.warn("Erro na limpeza: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }

        // 2. Insere dados do DbUnit (se houver XML configurado)
        logger.info("--> [GenericTest] Inserindo dados do DBUnit...");
        try {
            // Assume-se que o DbUnitUtil abre e fecha sua própria conexão ou usa JDBC direto
            DbUnitUtil.insertData(); 
        } catch (Exception e) {
            logger.error("Erro ao inserir dados do DbUnit: " + e.getMessage());
            // Não pare o teste, tente continuar, mas logue o erro
        }

        // 3. Inicia a transação DO TESTE
        // O EntityManager deve estar pronto para uso aqui
        et = em.getTransaction();
        et.begin();
    }

    @AfterEach
    public void tearDown() {
        logger.info("--> [GenericTest] Finalizando teste...");
        
        // CORREÇÃO CRÍTICA: Use ROLLBACK, não COMMIT.
        // Se houve erro de validação, a transação já está marcada para rollback.
        // Tentar commitar causaria erro. Rollback é sempre seguro.
        if (et != null && et.isActive()) {
            et.rollback();
        }
        
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}