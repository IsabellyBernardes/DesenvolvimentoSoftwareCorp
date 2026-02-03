package ifpe.paokentyn.domain;

import jakarta.validation.ConstraintViolation;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TarefaValidationTest extends AbstractValidationTest {

    private Tarefa criarTarefaValida() {
        Tarefa tarefa = new Tarefa();
        
        tarefa.setDescricao("Limpar o forno industrial");
        
        tarefa.setDataInicio(new Date()); 
        
        Calendar calAmanha = Calendar.getInstance();
        calAmanha.add(Calendar.DAY_OF_MONTH, 1);
        tarefa.setDataPrevisao(calAmanha.getTime()); 
        
        tarefa.setConcluida(false); 

        Funcionario funcionarioMock = new Funcionario();
        tarefa.setFuncionario(funcionarioMock);

        return tarefa;
    }

    @Test
    public void testTarefaValida() {
        Tarefa tarefa = criarTarefaValida();

        Set<ConstraintViolation<Tarefa>> violacoes = validator.validate(tarefa);
        assertTrue(violacoes.isEmpty(), "Uma tarefa com todos os dados corretos deve passar");
    }

    @Test
    public void testDescricaoEmBrancoInvalida() {
        Tarefa tarefa = criarTarefaValida();
        
        tarefa.setDescricao("   "); 

        Set<ConstraintViolation<Tarefa>> violacoes = validator.validate(tarefa);
        assertEquals(1, violacoes.size(), "Deveria barrar tarefa sem descrição");
    }

    @Test
    public void testDataInicioNoFuturoInvalida() {
        Tarefa tarefa = criarTarefaValida();
        
        // Tentando iniciar a tarefa no futuro (ano 2099) - Viola o @PastOrPresent
        Calendar calFuturo = Calendar.getInstance();
        calFuturo.set(2099, Calendar.DECEMBER, 31);
        tarefa.setDataInicio(calFuturo.getTime()); 

        Set<ConstraintViolation<Tarefa>> violacoes = validator.validate(tarefa);
        assertEquals(1, violacoes.size(), "Deveria barrar data de início no futuro");
    }

    @Test
    public void testDataPrevisaoNoPassadoInvalida() {
        Tarefa tarefa = criarTarefaValida();
        
        // Tentando colocar a previsão de entrega para o passado (ano 2000) - Viola o @Future
        Calendar calPassado = Calendar.getInstance();
        calPassado.set(2000, Calendar.JANUARY, 1);
        tarefa.setDataPrevisao(calPassado.getTime()); 

        Set<ConstraintViolation<Tarefa>> violacoes = validator.validate(tarefa);
        assertEquals(1, violacoes.size(), "Deveria barrar previsão de entrega no passado");
    }

    @Test
    public void testTarefaSemFuncionarioInvalida() {
        Tarefa tarefa = criarTarefaValida();
        
        // Removendo o responsável (violando o @NotNull)
        tarefa.setFuncionario(null); 

        Set<ConstraintViolation<Tarefa>> violacoes = validator.validate(tarefa);
        assertEquals(1, violacoes.size(), "Deveria barrar tarefa sem funcionário");
    }
}