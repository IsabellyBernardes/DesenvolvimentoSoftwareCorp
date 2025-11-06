package ifpe.paokentyn;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class TesteJPA {

    public static void main(String[] args) {
        Usuario usuario = new Usuario();
        preencherUsuario(usuario);
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction et = null;
        try {
            //EntityManagerFactory realiza a leitura das informaÃ§Ãµes relativas Ã  "persistence-unit".
            emf = Persistence.createEntityManagerFactory("DSC");
            em = emf.createEntityManager(); //CriaÃ§Ã£o do EntityManager.
            et = em.getTransaction(); //Recupera objeto responsÃ¡vel pelo gerenciamento de transaÃ§Ã£o.
            et.begin();
            em.persist(usuario);
            et.commit();
        } catch (RuntimeException ex) {
            if (et != null && et.isActive())
                et.rollback();
        } finally {
            if (em != null)
                em.close();       
            if (emf != null)
                emf.close();
        }
    }

    private static void preencherUsuario(Usuario usuario) {
        usuario.setNome("Fulano da Silva");
        usuario.setEmail("fulano@gmail.com");
        usuario.setLogin("fulano");
        usuario.setSenha("teste");
    }
}