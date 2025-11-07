package ifpe.paokentyn.repository;

import ifpe.paokentyn.factory.EntityFactory;
import ifpe.paokentyn.repository.Repositorio;
import jakarta.persistence.EntityManager;

import java.util.List;

public abstract class RepositorioGenerico<T> implements Repositorio<T> {

    private final Class<T> entityClass;

    protected RepositorioGenerico(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void salvar(T entity) {
        EntityManager em = EntityFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar entidade: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public T encontrarPorID(Long id) {
        EntityManager em = EntityFactory.getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> buscarTodos() {
        EntityManager em = EntityFactory.getEntityManager();
        try {
            return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(T entity) {
        EntityManager em = EntityFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar entidade: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deletar(Long id) {
        EntityManager em = EntityFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar entidade: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
