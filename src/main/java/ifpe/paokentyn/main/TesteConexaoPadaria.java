package ifpe.paokentyn.main;

import ifpe.paokentyn.domain.Padaria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class TesteConexaoPadaria {

    public static void main(String[] args) {
        
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction et = null;

        try {
            emf = Persistence.createEntityManagerFactory("DSC"); 
            
            em = emf.createEntityManager(); 
            
            et = em.getTransaction(); 

            System.out.println(">>> Criando objeto Padaria...");
            Padaria novaPadaria = new Padaria();
            novaPadaria.setNome("Pão Kentyn - Matriz");
            novaPadaria.setCep("50740-000");
          
            novaPadaria.setCnpj("12345678000199");
            
            et.begin();
            
            em.persist(novaPadaria);
            
            et.commit(); 
            
            System.out.println(">>> SUCESSO! Padaria salva com o ID: " + novaPadaria.getId());

        } catch (Exception e) {
            
            System.err.println(">>> FALHA! Erro ao tentar salvar.");
            e.printStackTrace(); 
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