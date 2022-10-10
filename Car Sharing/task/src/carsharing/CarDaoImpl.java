package carsharing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public List<Car> getCarsByCompany(int companyId) {
        var query = database.executeQuery("SELECT ID, NAME " +
                "FROM CAR " +
                "WHERE COMPANY_ID = " + companyId);
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
