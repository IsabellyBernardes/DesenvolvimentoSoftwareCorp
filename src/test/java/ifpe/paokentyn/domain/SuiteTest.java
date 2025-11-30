package ifpe.paokentyn.domain;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 *
 * @author isabe
 */
@Suite
@SelectClasses({
    DadosBancariosTest.class,
    FornadaTest.class,
    FuncionarioTest.class,
    IngredienteTest.class,
    ItemPedidoTest.class,
    PadariaTest.class,
    PaoTest.class,
    PedidoTest.class,
    TarefaTest.class
})
public class SuiteTest {
}