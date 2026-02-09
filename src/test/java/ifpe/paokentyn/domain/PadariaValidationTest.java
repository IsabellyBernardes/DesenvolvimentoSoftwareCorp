//package ifpe.paokentyn.domain;
//
//import jakarta.validation.ConstraintViolation;
//import java.util.Set;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//
//public class PadariaValidationTest extends AbstractValidationTest {
//
//    @Test
//    public void testPadariaValida() {
//        Padaria padaria = new Padaria();
//        padaria.setNome("Padaria Central");
//        
//        padaria.setCnpj("05604099000154"); 
//        padaria.setCep("64000-450");
//
//        Set<ConstraintViolation<Padaria>> violacoes = validator.validate(padaria);
//        assertTrue(violacoes.isEmpty(), "Não deveria haver violações em uma padaria válida");
//    }
//
//    @Test
//    public void testPadariaSemNome() {
//        Padaria padaria = new Padaria();
//        padaria.setNome("");
//        padaria.setCnpj("05604099000154");
//        padaria.setCep("64000-450");
//
//        Set<ConstraintViolation<Padaria>> violacoes = validator.validate(padaria);
//        
//        assertEquals(1, violacoes.size(), "Deveria ter exatamente 1 erro (Nome em branco)");
//    }
//
//    @Test
//    public void testPadariaCnpjInvalido() {
//        Padaria padaria = new Padaria();
//        padaria.setNome("Padaria Invalida");
//        
//        // CNPJ Falso que NÃO passa na fórmula matemática da Receita
//        padaria.setCnpj("12345678901234"); 
//        padaria.setCep("64000-450");
//
//        Set<ConstraintViolation<Padaria>> violacoes = validator.validate(padaria);
//        assertEquals(1, violacoes.size(), "Deveria barrar o CNPJ falso");
//    }
//
//    @Test
//    public void testPadariaCepSemHifen() {
//        Padaria padaria = new Padaria();
//        padaria.setNome("Padaria Invalida");
//        padaria.setCnpj("05604099000154");
//        
//        // passa no @Size, mas falha perfeitamente no @Pattern.
//        padaria.setCep("64000_450"); 
//
//        Set<ConstraintViolation<Padaria>> violacoes = validator.validate(padaria);
//        assertEquals(1, violacoes.size(), "Deveria barrar o CEP com o caractere errado");
//    }
//}