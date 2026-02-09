package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Tarefa;
import ifpe.paokentyn.domain.Funcionario;
import ifpe.paokentyn.domain.Padaria;

import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TarefaPersistenceTest extends GenericTest {

    private Tarefa criarTarefaBasica() {
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria Tarefa Teste");
        padaria.setCnpj("72980105000165"); 
        padaria.setCep("50000-000");
        em.persist(padaria);

        Funcionario f = new Funcionario();
        f.setNome("Funcionario Teste Persist");
        f.setCpf("743.076.430-46");
        f.setEmail("teste.persist@email.com");
        f.setCargo("Gerente");
        f.setSalario(2000.0);
        f.setPadaria(padaria); 
        
        em.persist(f); 
        em.flush(); 

        Tarefa t = new Tarefa();
        t.setDescricao("Teste de Persistência");
        t.setConcluida(false);
        t.setDataInicio(new Date());
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        t.setDataPrevisao(cal.getTime());
        
        t.setFuncionario(f);
        return t;
    }

    @Test
    public void testPersistirTarefaValida() {
        Tarefa tarefa = criarTarefaBasica();

        assertDoesNotThrow(() -> {
            em.persist(tarefa);
            em.flush(); 
        });
        
        assertNotNull(tarefa.getId());
    }

    @Test
    public void testPersistirTarefaComDescricaoVaziaDeveFalhar() {
        Tarefa tarefa = criarTarefaBasica();
        tarefa.setDescricao(""); 

        assertThrows(Exception.class, () -> {
            em.persist(tarefa);
            em.flush(); 
        }, "Deveria falhar ao persistir descrição vazia");
    }

    @Test
    public void testPersistirTarefaDataPassadaDeveFalhar() {
        Tarefa tarefa = criarTarefaBasica();
        
        Calendar passado = Calendar.getInstance();
        passado.add(Calendar.YEAR, -1);
        tarefa.setDataPrevisao(passado.getTime());

        assertThrows(Exception.class, () -> {
            em.persist(tarefa);
            em.flush();
        }, "Deveria falhar ao persistir data de previsão no passado");
    }

    @Test
    public void testAtualizarDescricaoParaVazioDeveFalhar() {
        Tarefa t = criarTarefaBasica();
        em.persist(t);
        em.flush();

        // 2. Tenta ATUALIZAR para inválido (Descrição vazia)
        t.setDescricao(""); 

        assertThrows(Exception.class, () -> {
            em.merge(t);
            em.flush();
        }, "Deveria falhar ao atualizar descrição para vazio");
    }
}