package ifpe.paokentyn.domain;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    DadosBancariosValidationTest.class,
    IngredienteValidationTest.class,
    TarefaValidationTest.class,
    FornadaValidationTest.class,
    FuncionarioValidationTest.class,
    PadariaValidationTest.class,
    PaoValidationTest.class,
    PedidoValidationTest.class,
    ItemPedidoValidationTest.class
})
public class ValidacaoTestSuite {
}