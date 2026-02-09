//package ifpe.paokentyn.domain;
//
//import jakarta.validation.ConstraintViolation;
//import java.time.LocalTime;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Set;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//
//public class FornadaValidationTest extends AbstractValidationTest {
//
//    private Fornada criarFornadaValida() {
//        Fornada fornada = new Fornada();
//        
//        fornada.setDataFornada(new Date()); 
//        
//        fornada.setHoraInicio(new Date()); 
//        
//        Padaria padariaMock = new Padaria();
//        padariaMock.setNome("Padaria Central");
//        padariaMock.setCnpj("05604099000154"); 
//        padariaMock.setCep("64000-450");       
//        fornada.setPadaria(padariaMock);
//
//        return fornada;
//    }
//
//    @Test
//    public void testFornadaValida() {
//        Fornada fornada = criarFornadaValida();
//
//        Set<ConstraintViolation<Fornada>> violacoes = validator.validate(fornada);
//
//        for (ConstraintViolation<Fornada> erro : violacoes) {
//            System.out.println("🚨 ATENÇÃO! O campo que está faltando é: '" + erro.getPropertyPath() + "' - Motivo: " + erro.getMessage());
//        }
//
//        assertTrue(violacoes.isEmpty(), "Uma fornada atual com padaria válida deve passar");
//    }
//
//    @Test
//    public void testFornadaNoFuturoInvalida() {
//        Fornada fornada = criarFornadaValida();
//        
//        // viola a anotação @PastOrPresent
//        Calendar cal = Calendar.getInstance();
//        cal.set(2099, Calendar.DECEMBER, 31);
//        fornada.setDataFornada(cal.getTime());
//
//        Set<ConstraintViolation<Fornada>> violacoes = validator.validate(fornada);
//        
//        // O sistema deve barrar previsões do futuro
//        assertEquals(1, violacoes.size(), "Deveria barrar data de fornada no futuro");
//    }
//
//    @Test
//    public void testFornadaSemPadariaInvalida() {
//        Fornada fornada = criarFornadaValida();
//        
//        //violação da chave estrangeira / @NotNull
//        fornada.setPadaria(null);
//
//        Set<ConstraintViolation<Fornada>> violacoes = validator.validate(fornada);
//        
//        // Uma fornada não pode existir no limbo, ela tem que pertencer a uma padaria
//        assertEquals(1, violacoes.size(), "Deveria barrar fornada sem padaria");
//    }
//}