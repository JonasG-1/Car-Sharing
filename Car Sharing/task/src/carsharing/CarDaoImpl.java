package carsharing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CarDaoImpl implements CarDao {

    private final Database database;

    public CarDaoImpl(Database database) {
        this.database = database;
    }

    @Override
    public void addCar(Car car) {
        database.execute(String.format("INSERT INTO CAR (NAME, COMPANY_ID)" +
                "VALUES ('%s', %s)", car.getCarName(), car.getCompanyId()));
    }

    @Override
    public Optional<Car> getCarById(int id) {
        var query = database.executeQuery("SELECT * " +
                "FROM CAR " +
                "WHERE ID = " + id);
        AtomicReference<Optional<Car>> optional = new AtomicReference<>(Optional.empty());
        query.ifPresent(resultSet -> {
            try {
                if (resultSet.next()) {
                    optional.set(Optional.of(new Car(resultSet.getString(2), resultSet.getInt(3),
                            resultSet.getInt(1))));
                }
                database.closeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return optional.get();
    }

    @Override
    public List<Car> getCarsByCompany(int companyId) {
        var query = database.executeQuery("SELECT ID, NAME " +
                "FROM CAR " +
                "WHERE COMPANY_ID = " + companyId);
        return getCarsFromQuery(companyId, query);
    }

    @Override
    public List<Car> getCarsByCompanyNotRented(int companyId) {
        var query = database.executeQuery("SELECT ID, NAME " +
                "FROM CAR " +
                "WHERE COMPANY_ID = " + companyId +
                "AND CAR.ID NOT IN (" +
                "SELECT CAR.ID " +
                "FROM CAR " +
                "JOIN CUSTOMER ON RENTED_CAR_ID=CAR.ID" +
                ");");
        return getCarsFromQuery(companyId, query);
    }

    private List<Car> getCarsFromQuery(int companyId, Optional<ResultSet> query) {
        List<Car> cars = new ArrayList<>();
        query.ifPresent(resultSet -> {
            try {
                while (resultSet.next()) {
                    Car car = new Car(resultSet.getString(2), companyId, resultSet.getInt(1));
                    cars.add(car);
                }
                database.closeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return cars;
    }
}
