package ifpe.paokentyn.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SemNumeroValidator implements ConstraintValidator<SemNumero, String> {

    @Override
    public void initialize(SemNumero constraintAnnotation) {
    }

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null) {
            return true; // Deixa o @NotNull validar se é nulo
        }
        // Retorna falso se encontrar qualquer dígito (0-9)
        return !valor.matches(".*\\d.*");
    }
}