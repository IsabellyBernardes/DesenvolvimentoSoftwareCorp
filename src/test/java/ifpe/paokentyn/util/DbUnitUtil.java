package ifpe.paokentyn.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

public class DbUnitUtil {

    private static final String DRIVER = "org.apache.derby.jdbc.ClientDriver";
    private static final String URL = "jdbc:derby://localhost:1527/DSC";
    private static final String USER = "derby";
    private static final String PASSWORD = "admin";
    private static final String SCHEMA = "DERBY";
    
    private static final String DATASET_PATH = "/dataset.xml";

    private static IDatabaseConnection dbConn;

    public static void insertData() {
        try (Connection jdbcConnection = getConnection()) {

            dbConn = new DatabaseConnection(jdbcConnection, SCHEMA);
            
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            builder.setColumnSensing(true);
            
            try (InputStream in = DbUnitUtil.class.getResourceAsStream(DATASET_PATH)) {
                if (in == null) {
                    throw new RuntimeException("Não foi possível encontrar o dataset: " + DATASET_PATH);
                }
                IDataSet dataSet = builder.build(in);

                DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
            }

        } catch (Exception e) {

            throw new RuntimeException("Erro ao inicializar o DBUnit: " + e.getMessage(), e);
        } finally {
            if (dbConn != null) {
                try {
                    dbConn.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    private static Connection getConnection() throws Exception {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}