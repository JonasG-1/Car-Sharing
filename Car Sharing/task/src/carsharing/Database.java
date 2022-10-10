package carsharing;

import java.sql.*;
import java.util.Optional;

public class Database {

    private final Connection connection;
    private final String driver = "org.h2.Driver";
    private final String url = "jdbc:h2:";
    private final String user = "sa";
    private final String pass = "";
    private Statement statement;

    public Database(String name) {
        try {
            Class.forName(driver);
            String toTask = "./src/carsharing/db/";
            connection = DriverManager.getConnection(url + toTask + name);
            connection.setAutoCommit(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean execute(String sql) {
        try {
            Statement statement = connection.createStatement();
            boolean ret = statement.execute(sql);
            statement.close();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Optional<ResultSet> executeQuery(String sql) {
        closeQuery();
        try {
            Statement statement = connection.createStatement();
            ResultSet ret = statement.executeQuery(sql);
            this.statement = statement;
            return Optional.of(ret);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void closeQuery() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ignored) {
            }
            statement = null;
        }
    }

    public void restore() {
        execute("DROP TABLE COMPANY");
        execute("DROP TABLE CAR");
        execute("CREATE TABLE COMPANY (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(30) NOT NULL UNIQUE" +
                ");");
        execute("CREATE TABLE CAR (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(30) UNIQUE NOT NULL," +
                "COMPANY_ID INT NOT NULL," +
                "CONSTRAINT FK_COMPANY FOREIGN KEY (COMPANY_ID) " +
                "REFERENCES COMPANY(ID) " +
                "ON DELETE CASCADE " +
                "ON UPDATE CASCADE" +
                ");");
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
