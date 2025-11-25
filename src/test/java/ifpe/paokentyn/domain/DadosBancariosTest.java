/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ifpe.paokentyn.domain;

import ifpe.paokentyn.util.DbUnitUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author isabe
 */
public class DadosBancariosTest {

    private static final Logger logger = LoggerFactory.getLogger(DadosBancariosTest.class);
    
    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction et;

    @BeforeAll
    public static void setUpClass() {
        logger.info("Inicializando EntityManagerFactory...");
        emf = Persistence.createEntityManagerFactory("DSC");
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("Finalizando EntityManagerFactory...");
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    public void setUp() {
        logger.info("Carregando dataset.xml e iniciando transação...");
        DbUnitUtil.insertData();
        em = emf.createEntityManager();
        et = em.getTransaction();
        et.begin();
    }

    @AfterEach
    public void tearDown() {
        logger.info("Finalizando transação e liberando EntityManager...");
        if (et != null && et.isActive()) {
            et.commit();
        }
        if (em != null) {
            em.close();
        }
    }

    @Test
    public void testEncontrarDadosBancariosDoDataSet() {
        logger.info("--- Executando testEncontrarDadosBancariosDoDataSet ---");
        
        DadosBancarios dados = em.find(DadosBancarios.class, 801L); 

        assertNotNull(dados, "DadosBancarios 801 deveriam existir no dataset");
        assertEquals("Banco Teste S.A.", dados.getBanco());

        assertEquals(201L, dados.getFuncionario().getId());
        assertEquals("João Silva", dados.getFuncionario().getNome());
        
        logger.info("Encontrada conta: {} do func. {}", 
            dados.getConta(), 
            dados.getFuncionario().getNome()
        );
    }
}
