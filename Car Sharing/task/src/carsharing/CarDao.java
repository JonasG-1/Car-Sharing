package carsharing;

import java.util.List;
import java.util.Optional;

public interface CarDao {

    void addCar(Car car);

    Optional<Car> getCarById(int id);

    List<Car> getCarsByCompany(int companyId);

    List<Car> getCarsByCompanyNotRented(int companyId);

}
