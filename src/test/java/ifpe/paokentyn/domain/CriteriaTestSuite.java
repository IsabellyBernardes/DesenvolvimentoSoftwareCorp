package ifpe.paokentyn.domain;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    DadosBancariosJPQLTest.class, 
    PaoCriteriaTest.class,     
    PedidoCriteriaTest.class, 
    FuncionarioCriteriaTest.class
})
public class CriteriaTestSuite {
}