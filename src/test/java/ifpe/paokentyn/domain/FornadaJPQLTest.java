package ifpe.paokentyn.domain;

import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FornadaJPQLTest extends GenericTest {

    private static final Logger logger = LoggerFactory.getLogger(FornadaJPQLTest.class);


    @Test
    public void testJPQLBuscaSimplesPorId() {
        logger.info("--- Busca por ID ---");

        String jpql = "SELECT f FROM Fornada f WHERE f.id = :id";
        
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("id", 1L);

        Fornada f = query.getSingleResult();

        assertNotNull(f);
        assertEquals(1L, f.getId());
        logger.info("Fornada 1 encontrada: Padaria {}", f.getPadaria().getNome());
    }

    @Test
    public void testJPQLBuscaComJoinPadaria() {
        logger.info("--- JOIN Explícito com Padaria ---");

        String jpql = "SELECT f FROM Fornada f JOIN f.padaria p WHERE p.nome = :nomePadaria";
        
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("nomePadaria", "Padaria do Melhor Teste");

        List<Fornada> lista = query.getResultList();

        assertEquals(1, lista.size(), "Deveria ter apenas 1 fornada nesta padaria");
        logger.info("Sucesso no JOIN. Fornada ID: {}", lista.get(0).getId());
    }

    @Test
    public void testJPQLBuscaPadariaLike() {
        logger.info("--- Busca (LIKE) na Padaria ---");

        String jpql = "SELECT f FROM Fornada f JOIN f.padaria p WHERE p.nome LIKE :parteNome";
        
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("parteNome", "%Teste DOIS%");

        List<Fornada> lista = query.getResultList();

        assertEquals(2, lista.size(), "Padaria 2 tem 2 fornadas");
        logger.info("Sucesso no LIKE. Encontradas: {}", lista.size());
    }

    @Test
    public void testJPQLBuscaPorData() {
        logger.info("--- Busca por Data ---");

        LocalDate localDate = LocalDate.of(2025, 11, 9);
        Date dataAlvo = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        String jpql = "SELECT f FROM Fornada f WHERE f.dataFornada = :data";
        
        TypedQuery<Fornada> query = em.createQuery(jpql, Fornada.class);
        query.setParameter("data", dataAlvo);

        List<Fornada> lista = query.getResultList();

        assertEquals(3, lista.size(), "Todas as 3 fornadas do dataset são desta data");
        logger.info("Sucesso na busca por Data: {}", dataAlvo);
    }

    @Test
    public void testJPQLCountFornadas() {
        logger.info("--- COUNT (Agregação) ---");

        String jpql = "SELECT COUNT(f) FROM Fornada f WHERE f.padaria.nome = :nomePadaria";
        
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("nomePadaria", "Padaria do Melhor Teste DOIS");

        Long qtd = query.getSingleResult();

        assertEquals(2L, qtd);
        logger.info("Contagem correta: {} fornadas.", qtd);
    }


    @Test
    public void testJPQLPathExpression() {
        logger.info("--- Path Expression (Navegação com ponto) ---");
        
        String jpql = "SELECT f FROM Fornada f WHERE f.padaria.cnpj = :cnpj";
        
        List<Fornada> lista = em.createQuery(jpql, Fornada.class)
                                .setParameter("cnpj", "99887766000199")
                                .getResultList();
                                
        assertEquals(1, lista.size());
        assertEquals("Padaria do Melhor Teste", lista.get(0).getPadaria().getNome());
    }

    @Test
    public void testJPQLMaxMinId() {
        logger.info("--- Funções MAX e MIN ---");
        
        String jpql = "SELECT MIN(f.id), MAX(f.id) FROM Fornada f";
        
        Object[] resultado = (Object[]) em.createQuery(jpql).getSingleResult();
        
        logger.info("Primeira Fornada (Min ID): {}", resultado[0]);
        logger.info("Última Fornada (Max ID): {}", resultado[1]);
        
        assertNotNull(resultado[0]);
        assertNotNull(resultado[1]);
    }

    @Test
    public void testJPQLDistinctPadarias() {
        logger.info("--- DISTINCT (Quais padarias tiveram fornadas?) ---");
        
        String jpql = "SELECT DISTINCT f.padaria FROM Fornada f";
        
        List<Padaria> padariasAtivas = em.createQuery(jpql, Padaria.class).getResultList();
        
        assertEquals(2, padariasAtivas.size());
        logger.info("Foram encontradas {} padarias produzindo pão.", padariasAtivas.size());
    }

    @Test
    public void testJPQLLeftJoinItens() {
        logger.info("--- LEFT JOIN (Fornadas e seus Itens de Pedido) ---");
        
        String jpql = "SELECT f.id, i.quantidade FROM Fornada f LEFT JOIN f.itensPedidos i";
        
        List<Object[]> resultados = em.createQuery(jpql).getResultList();
        
        for (Object[] linha : resultados) {
            logger.info("Fornada ID: {} | Qtd Item Vendido: {}", linha[0], linha[1]);
        }
        assertTrue(resultados.size() >= 3);
    }

    @Test
    public void testJPQLCollectionManipulation() {
        logger.info("--- Manipulação de Coleção (SIZE) ---");
        
        String jpql = "SELECT f FROM Fornada f WHERE SIZE(f.itensPedidos) > 0";
        
        List<Fornada> fornadasVendidas = em.createQuery(jpql, Fornada.class).getResultList();
        
        assertTrue(fornadasVendidas.size() > 0);
        logger.info("Fornadas com itens vendidos: {}", fornadasVendidas.size());
    }
}