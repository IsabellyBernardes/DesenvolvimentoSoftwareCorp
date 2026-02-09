package ifpe.paokentyn.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CepPernambucoValidator implements ConstraintValidator<CepPernambuco, String> {

    @Override
    public void initialize(CepPernambuco constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        // Se for nulo, deixa o @NotNull validar
        if (cep == null) {
            return true;
        }
        
        // Remove traços para garantir que estamos pegando o primeiro número
        String cepLimpo = cep.replace("-", "").trim();
        
        // Regra: Deve começar com '5' (Região de PE/AL/PB/RN, mas vamos focar em PE 5xxxx)
        return cepLimpo.startsWith("5");
    }
}