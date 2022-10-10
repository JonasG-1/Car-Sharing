package carsharing;

import java.util.List;

public interface CarDao {

    void addCar(Car car);

    List<Car> getCarsByCompany(int companyId);

}
