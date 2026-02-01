package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PadariaCriteriaTest extends GenericTest {

    /**
     * Teste 1 – Manipulação de String com LIKE Busca padarias que contenham
     * "Melhor Teste" no nome
     */
    @Test
    public void testBuscarPadariaPorNomeLikeCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Padaria> cq = cb.createQuery(Padaria.class);
        Root<Padaria> root = cq.from(Padaria.class);

        cq.where(
                cb.like(
                        cb.lower(root.get("nome")),
                        "%melhor teste%"
                )
        );

        List<Padaria> padarias = em.createQuery(cq).getResultList();

        assertEquals(3, padarias.size());
    }

    /**
     * Teste 2 – Busca por CEP usando Criteria
     */
    @Test
    public void testBuscarPadariaPorCepCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Padaria> cq = cb.createQuery(Padaria.class);
        Root<Padaria> root = cq.from(Padaria.class);

        cq.where(cb.equal(root.get("cep"), "55555555"));

        List<Padaria> padarias = em.createQuery(cq).getResultList();

        assertEquals(2, padarias.size());
    }

    /**
     * Teste 3 – AVG Média de funcionários por padaria
     */
    @Test
    public void testMediaFuncionariosPorPadariaCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<Padaria> root = cq.from(Padaria.class);

        cq.select(cb.avg(cb.size(root.get("funcionarios"))));

        Double media = em.createQuery(cq).getSingleResult();

        assertNotNull(media);
        assertEquals(1.0, media, 0.01);
        // (3 + 0 + 0) / 3
    }

    /**
     * Teste 4 – BETWEEN Busca padarias cujo ID esteja entre 2 e 3
     */
    @Test
    public void testBuscarPadariaPorIntervaloDeIdCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Padaria> cq = cb.createQuery(Padaria.class);
        Root<Padaria> root = cq.from(Padaria.class);

        cq.where(cb.between(root.get("id"), 2L, 3L));

        List<Padaria> padarias = em.createQuery(cq).getResultList();

        assertEquals(2, padarias.size());
    }

    /**
     * Teste 5 – SUM Soma total de fornadas das padarias
     */
    @Test
    public void testSomaTotalFornadasCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Padaria> root = cq.from(Padaria.class);
        Join<Padaria, Fornada> joinFornada = root.join("fornadas", JoinType.LEFT);

        cq.select(cb.count(joinFornada));

        Long soma = em.createQuery(cq).getSingleResult();

        assertNotNull(soma);
        assertEquals(3L, soma);
    }
}
