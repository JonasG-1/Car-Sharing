package carsharing;

import java.util.Optional;

public class Car {

    private final String carName;
    private final int companyId;
    private final Integer carId;

    public Car(String carName, int companyId) {
        this.carName = carName;
        this.companyId = companyId;
        this.carId = null;
    }

    public Car(String carName, int companyId, int carId) {
        this.carName = carName;
        this.companyId = companyId;
        this.carId = carId;
    }

    public String getCarName() {
        return carName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public Optional<Integer> getCarId() {
        return Optional.ofNullable(carId);
    }
}
