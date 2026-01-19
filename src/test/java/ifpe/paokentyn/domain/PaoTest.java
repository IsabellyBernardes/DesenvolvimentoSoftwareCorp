package ifpe.paokentyn.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaoTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(PaoTest.class);
    
    private List<Pao> buscarPaesComFiltroDinamico(String parteNome, Double precoMaximo) {
    
    // 1. Fábrica
    CriteriaBuilder cb = em.getCriteriaBuilder();
    
    // 2. Desenho da Query
    CriteriaQuery<Pao> query = cb.createQuery(Pao.class);
    Root<Pao> root = query.from(Pao.class);
    
    // 3. Lista de Predicados (Condições)
    List<Predicate> condicoes = new ArrayList<>();

    // CONDICIONAL 1: Se tem nome, adiciona LIKE
    if (parteNome != null && !parteNome.isEmpty()) {
        // lower() para ignorar maiúsculas/minúsculas
        condicoes.add(cb.like(cb.lower(root.get("nomePao")), "%" + parteNome.toLowerCase() + "%"));
    }

    // CONDICIONAL 2: Se tem preço máximo, adiciona LE (Less or Equal)
    if (precoMaximo != null) {
        condicoes.add(cb.le(root.get("preco"), precoMaximo));
    }

    // 4. Monta o WHERE (transforma a lista em array e une com AND)
    query.where(cb.and(condicoes.toArray(new Predicate[0])));
    
    // 5. Executa
    return em.createQuery(query).getResultList();
}

    private Pao buscarPaoPorNome(String nome) {
        String jpql = "SELECT p FROM Pao p WHERE p.nomePao = :nome";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("nome", nome);
        return query.getSingleResult();
    }

    private Pao buscarPaoPorId(int id) {
        String jpql = "SELECT p FROM Pao p WHERE p.id = :id";
        TypedQuery<Pao> query = em.createQuery(jpql, Pao.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private ItemPedido buscarItemPorNomeDoPao(String nomePao) {
        String jpql = "SELECT i FROM ItemPedido i WHERE i.pao.nomePao = :nomePao";
        TypedQuery<ItemPedido> query = em.createQuery(jpql, ItemPedido.class);
        query.setParameter("nomePao", nomePao);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Test
    public void testEncontrarPaoDoDataSetEIngredientes() {
        logger.info("--- Executando testEncontrarPaoDoDataSetEIngredientes ---");

        Pao pao = buscarPaoPorNome("Pão de Queijo");

        assertNotNull(pao, "Pão de Queijo deveria existir no dataset");
        assertEquals("Pão de Queijo", pao.getNomePao());
        assertEquals(3.00, pao.getPreco());

        assertNotNull(pao.getIngredientes(), "A lista de ingredientes não deveria ser nula");
        assertEquals(2, pao.getIngredientes().size(), "Pão de Queijo deve ter 2 ingredientes");
        
        boolean achouOvos = pao.getIngredientes().stream()
                                .anyMatch(ing -> ing.getNome().equals("Ovos"));
        boolean achouPolvilho = pao.getIngredientes().stream()
                                .anyMatch(ing -> ing.getNome().equals("Polvilho"));
        
        assertTrue(achouOvos, "Deveria ter encontrado Ovos na lista");
        assertTrue(achouPolvilho, "Deveria ter encontrado Polvilho na lista");

        logger.info("Encontrado: {} (com {} ingredientes)", pao.getNomePao(), pao.getIngredientes().size());
    }

@Test
    public void testBuscaDinamicaComCriteria() {
        logger.info("--- Executando testBuscaDinamicaComCriteria ---");

        // Cenário A: Filtrar APENAS por preço (Paes baratos <= 4.00)
        List<Pao> paesBaratos = buscarPaesComFiltroDinamico(null, 4.00);
        
        assertEquals(2, paesBaratos.size(), "Deveria vir 2 pães baratos (Queijo e Sal)");
        
        assertTrue(paesBaratos.stream().allMatch(p -> p.getPreco() <= 4.00));
        
        logger.info("Filtro Preço OK: Encontrou {} e {}", 
                paesBaratos.get(0).getNomePao(), 
                paesBaratos.get(1).getNomePao());

        List<Pao> paesIntegrais = buscarPaesComFiltroDinamico("Integral", null);
        
        assertEquals(1, paesIntegrais.size());
        assertEquals("Pão Integral", paesIntegrais.get(0).getNomePao());
        logger.info("Filtro Nome OK: Achou {}", paesIntegrais.get(0).getNomePao());

        // Cenário C: Filtrar por Nome E Preço (Inexistente)
        List<Pao> paesImpossiveis = buscarPaesComFiltroDinamico("Queijo", 1.00);
        assertTrue(paesImpossiveis.isEmpty(), "Não deveria achar nada com esses filtros restritos");

        // Cenário D: Sem filtros (Deve trazer TODOS: Integral, Queijo e Sal)
        List<Pao> todos = buscarPaesComFiltroDinamico(null, null);
        assertEquals(3, todos.size(), "Deveria trazer todos os 3 pães do dataset");
        
        logger.info("Teste de Criteria API (Filtros Dinâmicos) finalizado com sucesso.");
    }
    
    @Test
    public void testPersistirPaoComNovoIngrediente() {
        logger.info("--- Executando testPersistirPaoComNovoIngrediente ---");

        Ingrediente novoIngrediente = new Ingrediente();
        novoIngrediente.setNome("Fermento Biológico");
        
        Pao novoPao = new Pao();
        novoPao.setNomePao("Pão Francês");
        novoPao.setPreco(0.75);
        
        novoPao.setIngredientes(List.of(novoIngrediente));

        em.persist(novoIngrediente); 
        em.persist(novoPao);
        em.flush(); 

        assertNotNull(novoPao.getId(), "ID do novo pão não pode ser nulo");
        assertNotNull(novoIngrediente.getId(), "ID do novo ingrediente não pode ser nulo");
        
        Pao paoDoDataset = buscarPaoPorNome("Pão de Queijo");
        assertNotEquals(paoDoDataset.getId(), novoPao.getId());

        em.clear(); 
        
        Pao paoDoBanco = em.find(Pao.class, novoPao.getId());
        assertEquals(1, paoDoBanco.getIngredientes().size());
        assertEquals("Fermento Biológico", paoDoBanco.getIngredientes().get(0).getNome());

        logger.info("Persistido: {} com ID: {}", novoPao.getNomePao(), novoPao.getId());
    }

    @Test
    public void testAtualizarPaoComMerge() {
        logger.info("--- Executando testAtualizarPaoComMerge ---");

        Pao pao = buscarPaoPorNome("Pão Integral");
        assertNotNull(pao);
        Long idOriginal = pao.getId();
        Double precoAntigo = pao.getPreco();

        em.clear();

        pao.setPreco(8.50); 

        em.merge(pao);

        em.flush();
        em.clear();

        Pao paoAtualizado = em.find(Pao.class, idOriginal);
        assertEquals(8.50, paoAtualizado.getPreco());
        assertNotEquals(precoAntigo, paoAtualizado.getPreco());

        logger.info("Preço do pão atualizado via merge: {} -> {}", precoAntigo, paoAtualizado.getPreco());
    }

    //atualizado mas sem usar o merge
    @Test
    public void testAtualizarPaoAdicionarIngrediente() {
        logger.info("--- Executando testAtualizarPaoAdicionarIngrediente ---");

        Pao pao = buscarPaoPorNome("Pão de Queijo");
        assertNotNull(pao);
        Long idOriginal = pao.getId();
        
        Ingrediente sal = new Ingrediente();
        sal.setNome("Sal");
        em.persist(sal);

        pao.getIngredientes().add(sal); 
        
        em.flush();
        em.clear();
        
        Pao paoAtualizado = em.find(Pao.class, idOriginal);
        assertEquals(3, paoAtualizado.getIngredientes().size(), "Agora deve ter 3 ingredientes");
        
        boolean achouSal = paoAtualizado.getIngredientes().stream()
                .anyMatch(ing -> ing.getNome().equals("Sal"));
        assertTrue(achouSal, "Sal deveria estar na lista");
        
        logger.info("Pão atualizado com sucesso. Ingrediente adicionado.");
    }

    @Test
    public void testRemoverPaoMantendoIngredientes() {
        logger.info("--- Executando testRemoverPaoMantendoIngredientes ---");

        Pao pao = buscarPaoPorNome("Pão Integral");
        assertNotNull(pao);
        
        Ingrediente ing = pao.getIngredientes().get(0);
        Long idIngrediente = ing.getId();
        
        em.remove(pao); 
        
        em.flush();
        em.clear();
        
        String jpqlCheck = "SELECT p FROM Pao p WHERE p.nomePao = :nome";
        List<Pao> lista = em.createQuery(jpqlCheck, Pao.class)
                            .setParameter("nome", "Pão Integral")
                            .getResultList();
        assertTrue(lista.isEmpty());
        
        Ingrediente ingredienteSobrevivente = em.find(Ingrediente.class, idIngrediente);
        assertNotNull(ingredienteSobrevivente, "O ingrediente NÃO deveria ser apagado");
        
        logger.info("Pão removido, mas ingrediente {} preservado.", ingredienteSobrevivente.getNome());
    }
    
    @Test
    public void testRemoverPaoApagaItensDePedido() {
        logger.info("--- Executando testRemoverPaoApagaItensDePedido ---");

        Pao pao = buscarPaoPorNome("Pão Integral");
        
        ItemPedido itemAntes = buscarItemPorNomeDoPao("Pão Integral");
        assertNotNull(itemAntes, "Deveria existir um item de pedido com Pão Integral");
        Long idItem = itemAntes.getId();

        em.remove(pao); 

        em.flush();
        em.clear();

        ItemPedido itemDepois = em.find(ItemPedido.class, idItem);
        assertNull(itemDepois, "O ItemPedido deveria ter sido apagado em cascata!");
        
        logger.info("Sucesso: Ao apagar o Pão, o histórico de vendas foi apagado.");
    }

    @Test
    public void testEqualsAndHashCode() {
        logger.info("--- Executando testEqualsAndHashCode ---");

        Pao p1 = buscarPaoPorId(2);
        Pao p2 = buscarPaoPorId(3);
        Pao p3 = buscarPaoPorId(2);

        assertFalse(p1.equals(p2), "Os objetos não devem ser iguais");
        assertTrue(p1.equals(p3), "Os objetos devem ser iguais");
        assertEquals(p1.hashCode(), p3.hashCode(), "Hashcodes devem ser iguais");
    }
}
