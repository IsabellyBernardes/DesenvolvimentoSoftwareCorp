package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IngredienteTest extends GenericTest {

    private Ingrediente buscarPorNome(String nome) {
        String jpql = "SELECT i FROM Ingrediente i WHERE i.nome = :nome";
        TypedQuery<Ingrediente> query = em.createQuery(jpql, Ingrediente.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }
    
    private Ingrediente buscarPorId(int id) {
        String jpql = "SELECT i FROM Ingrediente i WHERE i.id = :id";
        TypedQuery<Ingrediente> query = em.createQuery(jpql, Ingrediente.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Test
    public void testEncontrarIngredientePorNome() {
        logger.info("--- Executando testEncontrarIngredientePorNome ---");

        Ingrediente ingrediente = buscarPorNome("Ovos");

        assertNotNull(ingrediente, "Deveria ter encontrado os Ovos");
        assertEquals("Ovos", ingrediente.getNome());

        assertNotNull(ingrediente.getPaes(), "A lista de pães não pode ser nula");
        assertFalse(ingrediente.getPaes().isEmpty(), "O ingrediente deve estar sendo usado em algum pão");
        
        Pao paoRelacionado = ingrediente.getPaes().get(0);
        assertEquals("Pão de Queijo", paoRelacionado.getNomePao());
        
        logger.info("Encontrado: '{}' (ID={}). Usado no pão: '{}'", 
            ingrediente.getNome(), 
            ingrediente.getId(), 
            paoRelacionado.getNomePao()
        );
    }

    @Test
    public void testAtualizarIngredienteComMerge() {
        logger.info("--- Executando testAtualizarIngredienteComMerge ---");

        Ingrediente ingrediente = buscarPorNome("Polvilho");
        assertNotNull(ingrediente);
        Long idOriginal = ingrediente.getId();

        em.clear(); 

        ingrediente.setNome("Polvilho Doce Premium");

        em.merge(ingrediente);

        em.flush();
        em.clear();

        Ingrediente atualizado = em.find(Ingrediente.class, idOriginal);
        assertEquals("Polvilho Doce Premium", atualizado.getNome());
        
        logger.info("Ingrediente atualizado para: {}", atualizado.getNome());
    }

    @Test
    public void testRemoverIngredienteComRelacionamento() {
        logger.info("--- Executando testRemoverIngredienteComRelacionamento ---");

        Ingrediente ingrediente = buscarPorNome("Farinha de Trigo");
        assertNotNull(ingrediente);
        
        List<Pao> paesQueUsam = ingrediente.getPaes();
        assertFalse(paesQueUsam.isEmpty());
        
        logger.info("O ingrediente {} é usado em {} pães. Removendo associações...", 
                ingrediente.getNome(), paesQueUsam.size());

        for (Pao p : paesQueUsam) {
            p.getIngredientes().remove(ingrediente);
            em.merge(p); 
        }
        
        em.flush(); 

        em.remove(ingrediente);
        
        em.flush();
        em.clear();

        String jpqlCheck = "SELECT i FROM Ingrediente i WHERE i.nome = :nome";
        List<Ingrediente> lista = em.createQuery(jpqlCheck, Ingrediente.class)
                                    .setParameter("nome", "Farinha de Trigo")
                                    .getResultList();
        
        assertTrue(lista.isEmpty(), "A Farinha de Trigo deveria ter sumido do banco");
        
        logger.info("Ingrediente removido com sucesso após limpar relacionamentos.");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        Ingrediente i1 = buscarPorId(2);
        Ingrediente i2 = buscarPorId(3);
        Ingrediente i3 = buscarPorId(2);
        
        assertFalse(i1.equals(i2), "Os objetos não devem ser iguais");
        assertTrue(i1.equals(i3), "Os objetos devem ser iguais");
        assertEquals(i1.hashCode(), i3.hashCode(), "Hashcodes devem ser iguais");
    }
}