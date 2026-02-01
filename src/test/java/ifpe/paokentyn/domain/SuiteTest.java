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
    DadosBancariosCriteriaTest.class,
    DadosBancariosJPQLTest.class,
    FornadaTest.class,
    FornadaCriteriaTest.class,
    FornadaJPQLTest.class,
    FuncionarioTest.class,
    FuncionarioCriteriaTest.class,
    FuncionarioJPQLTest.class,
    IngredienteTest.class,
    IngredienteCriteriaTest.class,
    ItemPedidoTest.class,
    ItemPedidoCriteriaTest.class,
    PadariaTest.class,
    PadariaCriteriaTest.class,
    PaoTest.class,
    PedidoTest.class,
    TarefaTest.class
})
public class SuiteTest {
}