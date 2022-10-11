package carsharing;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    void addCustomer(Customer customer);

    Optional<Customer> getCustomerById(int id);

    List<Customer> getCustomers();

    void addRentedCar(int customerId, int carId);

    void returnRentedCar(int customerId);

    Optional<Car> getRentedCar(int id);
}
