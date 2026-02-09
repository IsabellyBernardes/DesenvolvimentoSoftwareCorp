//package ifpe.paokentyn.domain;
//
//import jakarta.validation.ConstraintViolation;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Set;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//
//public class FuncionarioValidationTest extends AbstractValidationTest {
//
//    private Funcionario criarFuncionarioValido() {
//        Funcionario f = new Funcionario();
//        f.setNome("João Silva");
//        f.setCargo("Padeiro Senior");
//        f.setSalario(3200.00);
//        
//        f.setCpf("12345678909"); 
//        f.setEmail("joao@padaria.com"); 
//        f.setDataContratacao(new Date()); 
//        
//        Padaria padariaFicticia = new Padaria();
//        padariaFicticia.setNome("Padaria Central"); 
//        f.setPadaria(padariaFicticia);
//        
//        return f;
//    }
//
//    @Test
//    public void testFuncionarioTotalmenteValido() {
//        Funcionario func = criarFuncionarioValido();
//
//        Set<ConstraintViolation<Funcionario>> violacoes = validator.validate(func);
//        assertTrue(violacoes.isEmpty(), "O funcionário padrão deveria ser 100% válido");
//    }
//
//    @Test
//    public void testFuncionarioCpfMatematicamenteInvalido() {
//        Funcionario func = criarFuncionarioValido();
//        
//        // Um CPF com 11 dígitos, mas que a conta matemática da Receita não bate
//        func.setCpf("11111111111"); 
//
//        Set<ConstraintViolation<Funcionario>> violacoes = validator.validate(func);
//        assertEquals(1, violacoes.size(), "Deveria barrar EXATAMENTE 1 erro (CPF falso)");
//    }
//
//    @Test
//    public void testFuncionarioEmailInvalido() {
//        Funcionario func = criarFuncionarioValido();
//        
//        // E-mail sem o "@"
//        func.setEmail("joaopadaria.com"); 
//
//        Set<ConstraintViolation<Funcionario>> violacoes = validator.validate(func);
//        assertEquals(1, violacoes.size(), "Deveria barrar EXATAMENTE 1 erro (e-mail mal formatado)");
//    }
//
//    @Test
//    public void testFuncionarioDataContratacaoNoFuturo() {
//        Funcionario func = criarFuncionarioValido();
//        
//        // Criando uma data no ano de 2099 (viola a anotação @PastOrPresent)
//        Calendar cal = Calendar.getInstance();
//        cal.set(2099, Calendar.DECEMBER, 31);
//        func.setDataContratacao(cal.getTime());
//
//        Set<ConstraintViolation<Funcionario>> violacoes = validator.validate(func);
//        assertEquals(1, violacoes.size(), "Deveria barrar EXATAMENTE 1 erro (contratação no futuro)");
//    }
//}