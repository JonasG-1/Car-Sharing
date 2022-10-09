package carsharing;

import java.sql.*;
import java.util.Optional;

public class Database {

    private final Connection connection;
    private final String driver = "org.h2.Driver";
    private final String url = "jdbc:h2:";
    private final String user = "sa";
    private final String pass = "";

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
        try {
            Statement statement = connection.createStatement();
            ResultSet ret = statement.executeQuery(sql);
            statement.close();
            return Optional.of(ret);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
