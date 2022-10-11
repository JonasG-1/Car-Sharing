package carsharing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CompanyDaoImpl implements CompanyDao {

    private final Database database;

    public CompanyDaoImpl(Database database) {
        this.database = database;
    }

    @Override
    public void addCompany(Company company) {
        database.execute("INSERT INTO COMPANY (NAME) " +
                "VALUES ('" + company.getCompanyName() + "');");
    }

    @Override
    public List<Company> getCompanies() {
        var query = database.executeQuery("SELECT * " +
                "FROM COMPANY " +
                "ORDER BY ID");
        List<Company> list = new ArrayList<>();
        query.ifPresent(resultSet -> {
            try {
                while (resultSet.next()) {
                    Company company = new Company(resultSet.getString(2), resultSet.getInt(1));
                    list.add(company);
                }
                database.closeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    @Override
    public Optional<Company> getCompanyById(int id) {
        var query = database.executeQuery("SELECT * " +
                "FROM COMPANY " +
                "WHERE ID = " + id);
        AtomicReference<Optional<Company>> optional = new AtomicReference<>(Optional.empty());
        query.ifPresent(resultSet -> {
            try {
                resultSet.next();
                optional.set(Optional.of(new Company(resultSet.getString(2), resultSet.getInt(1))));
                database.closeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return optional.get();
    }
}
