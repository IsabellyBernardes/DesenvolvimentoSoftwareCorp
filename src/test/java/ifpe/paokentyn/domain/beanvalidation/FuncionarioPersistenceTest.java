package ifpe.paokentyn.domain.beanvalidation;

import ifpe.paokentyn.domain.GenericTest;
import ifpe.paokentyn.domain.Funcionario;
import ifpe.paokentyn.domain.Padaria;
import java.util.Calendar;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioPersistenceTest extends GenericTest {

    private Funcionario criarFuncionarioValido() {
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria do Teste Funcionario");
        // CNPJ informado (sem pontuação para length=14)
        padaria.setCnpj("72980105000165"); 
        padaria.setCep("50000-000"); 
        
        em.persist(padaria);

        Funcionario f = new Funcionario();
        f.setNome("José da Silva");
        // CPF informado (formatado para length=14)
        f.setCpf("743.076.430-46"); 
        f.setEmail("jose.silva@padaria.com");
        f.setCargo("Padeiro Senior");
        f.setSalario(2500.00); 
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        f.setDataContratacao(cal.getTime());

        f.setPadaria(padaria);
        
        return f;
    }

    @Test
    public void testPersistirFuncionarioValido() {
        Funcionario f = criarFuncionarioValido();

        assertDoesNotThrow(() -> {
            em.persist(f);
            em.flush();
        });
        
        assertNotNull(f.getId(), "O ID deveria ter sido gerado");
    }

    @Test
    public void testPersistirCpfInvalidoDeveFalhar() {
        Funcionario f = criarFuncionarioValido();
        f.setCpf("111.222.333-00"); // CPF inválido

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria barrar CPF inválido");
    }

    @Test
    public void testPersistirEmailInvalidoDeveFalhar() {
        Funcionario f = criarFuncionarioValido();
        f.setEmail("email-sem-arroba.com"); 

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria barrar Email sem formato correto");
    }

    @Test
    public void testPersistirSalarioAbaixoDoMinimoDeveFalhar() {
        Funcionario f = criarFuncionarioValido();
        f.setSalario(900.00); 

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria barrar salário menor que 1000.00");
    }

    @Test
    public void testPersistirDataContratacaoNoFuturoDeveFalhar() {
        Funcionario f = criarFuncionarioValido();
        
        Calendar amanha = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_MONTH, 1);
        f.setDataContratacao(amanha.getTime());

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria barrar data de contratação no futuro");
    }
    
    @Test
    public void testPersistirSemPadariaDeveFalhar() {
        Funcionario f = criarFuncionarioValido();
        f.setPadaria(null); 

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria barrar funcionário sem padaria vinculada");
    }
    
    @Test
    public void testPersistirNomeComNumeroDeveFalhar() {
        Funcionario f = criarFuncionarioValido();
        f.setNome("José da Silva 2"); 

        assertThrows(Exception.class, () -> {
            em.persist(f);
            em.flush();
        }, "Deveria barrar nome com números (@SemNumero)");
    }
    
    @Test
    public void testAtualizarNomeComNumeroDeveFalhar() {
        // 1. Cria e salva um funcionário VÁLIDO primeiro
        Funcionario f = criarFuncionarioValido();
        em.persist(f);
        em.flush(); // Garante que foi pro banco
        
        // 2. Tenta ATUALIZAR para um nome inválido
        f.setNome("José da Silva 2"); // Agora tem número (@SemNumero)

        assertThrows(Exception.class, () -> {
            em.merge(f);
            em.flush(); 
        }, "Deveria falhar ao atualizar nome para conter números");
    }
}