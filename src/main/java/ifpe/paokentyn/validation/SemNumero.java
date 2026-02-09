package ifpe.paokentyn.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SemNumeroValidator.class)
public @interface SemNumero {
    String message() default "O campo não pode conter números";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}