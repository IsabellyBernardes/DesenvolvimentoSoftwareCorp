package ifpe.paokentyn.main;

// Sugestão de pacote para não misturar com seu domínio


// Imports das suas entidades
import ifpe.paokentyn.main.*;
import ifpe.paokentyn.domain.Padaria;

// Imports do Jakarta Persistence (JPA)
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * Classe de teste principal para verificar a persistência.
 * Baseado nos exemplos das Aulas 01 e 04.
 */
public class TesteConexaoPadariaTest {

    public static void main(String[] args) {
        
        // 1. Inicializa a fábrica e o gerente (começam como null)
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction et = null;

        try {
            // 2. Cria a Fábrica de Gerentes
            //    Usa "DSC", o nome da sua <persistence-unit> no persistence.xml [cite: 1071]
            emf = Persistence.createEntityManagerFactory("DSC");
            
            // 3. Pede um Gerente para a Fábrica [cite: 1072]
            em = emf.createEntityManager();
            
            // 4. Pega o controle de Transação (a "ordem de serviço") [cite: 1073]
            et = em.getTransaction();

            // --- Preparando o objeto para salvar ---
            System.out.println(">>> Criando objeto Padaria...");
            Padaria novaPadaria = new Padaria();
            novaPadaria.setNome("Pão Kentyn - Matriz");
            novaPadaria.setCep("50740-000");
            novaPadaria.setEndereco("Rua da Pamonha, 123");
            
            // 5. Inicia a transação [cite: 1074]
            et.begin();
            
            // 6. DÁ A ORDEM: "Gerente, persista este objeto!" [cite: 1075]
            em.persist(novaPadaria);
            
            // 7. Confirma a transação (salva no banco) [cite: 1076]
            et.commit();
            
            System.out.println(">>> SUCESSO! Padaria salva com o ID: " + novaPadaria.getId());

        } catch (Exception e) {
            // 8. Se algo der errado, desfaz a transação [cite: 1077-1079]
            System.err.println(">>> FALHA! Erro ao tentar salvar.");
            e.printStackTrace(); // Mostra o erro completo
            if (et != null && et.isActive()) {
                et.rollback();
            }
        } finally {
            // 9. Fecha o Gerente e a Fábrica (CRUCIAL!) [cite: 1080-1085]
            if (em != null) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
            System.out.println(">>> Conexões fechadas.");
        }
    }
}