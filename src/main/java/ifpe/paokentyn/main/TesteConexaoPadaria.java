package ifpe.paokentyn.main;

import ifpe.paokentyn.domain.Padaria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;


public class TesteConexaoPadaria {

    public static void main(String[] args) {
        
        // 1. Inicializa a fábrica e o gerente (começam como null)
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction et = null;

        try {
            // 2. Cria a Fábrica de Gerentes
            //    Usa "DSC", o nome da sua <persistence-unit> no persistence.xml 
            emf = Persistence.createEntityManagerFactory("DSC");
            
            // 3. Pede um Gerente para a Fábrica 
            em = emf.createEntityManager();
            
            // 4. Pega o controle de Transação (a "ordem de serviço")
            et = em.getTransaction();

            // --- Preparando o objeto para salvar ---
            System.out.println(">>> Criando objeto Padaria...");
            Padaria novaPadaria = new Padaria();
            novaPadaria.setNome("Pão Kentyn - Matriz");
            novaPadaria.setCep("50740-000");
            novaPadaria.setEndereco("Rua da Pamonha, 123");
            
            // 5. Inicia a transação 
            et.begin();
            
            // 6. DÁ A ORDEM: "Gerente, persista este objeto!" 
            em.persist(novaPadaria);
            
            // 7. Confirma a transação (salva no banco) 
            et.commit();
            
            System.out.println(">>> SUCESSO! Padaria salva com o ID: " + novaPadaria.getId());

        } catch (Exception e) {
            // 8. Se algo der errado, desfaz a transação
            System.err.println(">>> FALHA! Erro ao tentar salvar.");
            e.printStackTrace(); // Mostra o erro completo
            if (et != null && et.isActive()) {
                et.rollback();
            }
        } finally {
            // 9. Fecha o Gerente e a Fábrica 
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