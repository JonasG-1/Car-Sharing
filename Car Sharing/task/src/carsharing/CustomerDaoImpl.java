package carsharing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CustomerDaoImpl implements CustomerDao {

    private final Database database;

    public CustomerDaoImpl(Database database) {
        this.database = database;
    }

    @Override
    public void addCustomer(Customer customer) {
        database.execute("INSERT INTO CUSTOMER (NAME)" +
                "VALUES ('" + customer.getCustomerName() + "');");
    }

    @Override
    public Optional<Customer> getCustomerById(int id) {
        var query = database.executeQuery("SELECT * " +
                "FROM CUSTOMER " +
                "WHERE ID = " + id);
        AtomicReference<Optional<Customer>> optional = new AtomicReference<>(Optional.empty());
        query.ifPresent(resultSet -> {
            try {
                resultSet.next();
                optional.set(Optional.of(new Customer(resultSet.getString(2),
                        resultSet.getInt(3), resultSet.getInt(1))));
                database.closeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return optional.get();
    }

    @Override
    public List<Customer> getCustomers() {
        var query = database.executeQuery("SELECT * " +
                "FROM CUSTOMER " +
                "ORDER BY ID");
        List<Customer> customers = new ArrayList<>();
        query.ifPresent(resultSet -> {
            try {
                while (resultSet.next()) {
                    Customer customer = new Customer(resultSet.getString(2),
                            resultSet.getInt(3), resultSet.getInt(1));
                    customers.add(customer);
                }
                database.closeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return customers;
    }

    @Override
    public void addRentedCar(int customerId, int carId) {
        database.execute("UPDATE CUSTOMER " +
                "SET RENTED_CAR_ID = " + carId +
                "WHERE ID = " + customerId);
    }

    @Override
    public void returnRentedCar(int id) {
        database.execute("UPDATE CUSTOMER " +
                "SET RENTED_CAR_ID = NULL " +
                "WHERE ID = " + id);
    }

    @Override
    public Optional<Car> getRentedCar(int id) {
        var query = database.executeQuery("SELECT CAR.NAME, COMPANY_ID " +
                "FROM CAR " +
                "JOIN CUSTOMER ON RENTED_CAR_ID=CAR.ID " +
                "WHERE CUSTOMER.ID = " + id);
        AtomicReference<Optional<Car>> optional = new AtomicReference<>(Optional.empty());
        query.ifPresent(resultSet -> {
            try {
                if (resultSet.next()) {
                    Car car = new Car(resultSet.getString(1), resultSet.getInt(2));
                    optional.set(Optional.of(car));
                }
                database.closeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return optional.get();
    }
}
