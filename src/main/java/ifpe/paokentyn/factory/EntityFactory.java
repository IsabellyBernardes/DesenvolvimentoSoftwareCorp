package ifpe.paokentyn.factory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityFactory {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("DSC"); // nome igual ao do persistence.xml

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
