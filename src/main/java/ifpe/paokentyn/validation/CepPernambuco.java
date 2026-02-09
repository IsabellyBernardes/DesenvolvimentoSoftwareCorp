package ifpe.paokentyn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CepPernambucoValidator.class)
public @interface CepPernambuco {
    
    String message() default "A padaria deve estar localizada em Pernambuco (CEP começando com 5)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}