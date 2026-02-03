package ifpe.paokentyn.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractValidationTest {

    protected static ValidatorFactory factory;
    protected static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void tearDownValidator() {
        if (factory != null) {
            factory.close();
        }
    }
}