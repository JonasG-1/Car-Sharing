package carsharing;

import java.util.Optional;

public class Customer {

    private final String customerName;
    private final Integer carId;
    private final Integer customerId;

    public Customer(String customerName) {
        this.customerName = customerName;
        this.carId = null;
        this.customerId = null;
    }

    public Customer(String customerName, int carId, Integer customerId) {
        this.customerName = customerName;
        this.carId = carId;
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Optional<Integer> getCarId() {
        return Optional.ofNullable(carId);
    }

    public Optional<Integer> getCustomerId() {
        return Optional.ofNullable(customerId);
    }
}
