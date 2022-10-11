package carsharing;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static boolean running;
    private static CompanyDaoImpl companyDao;
    private static CarDaoImpl carDao;
    private static CustomerDaoImpl customerDao;

    public static void main(String[] args) {
        Database database = new Database(args[1]);
        database.execute("DROP TABLE CAR");
        database.execute("DROP TABLE COMPANY");
        database.execute("CREATE TABLE COMPANY (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(30) NOT NULL UNIQUE" +
                ");");
        database.execute("CREATE TABLE CAR (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(30) UNIQUE NOT NULL," +
                "COMPANY_ID INT NOT NULL," +
                "CONSTRAINT FK_COMPANY FOREIGN KEY (COMPANY_ID) " +
                "REFERENCES COMPANY(ID) " +
                "ON DELETE CASCADE " +
                "ON UPDATE CASCADE" +
                ");");
        database.execute("CREATE TABLE CUSTOMER (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(30) UNIQUE NOT NULL," +
                "RENTED_CAR_ID INT DEFAULT NULL, " +
                "CONSTRAINT FK_CAR FOREIGN KEY (RENTED_CAR_ID) " +
                "REFERENCES CAR(ID) " +
                "ON DELETE CASCADE " +
                "ON UPDATE CASCADE" +
                ");");
        companyDao = new CompanyDaoImpl(database);
        carDao = new CarDaoImpl(database);
        customerDao = new CustomerDaoImpl(database);
        running = true;
        while (running) {
            openMenu();
        }
        database.execute("DROP TABLE CAR");
        database.execute("DROP TABLE COMPANY");
        database.close();
    }

    public static void openMenu() {
        System.out.println("1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit");
        switch (getOption()) {
            case 0 -> running = false;
            case 1 -> openManagerMenu();
            case 2 -> listCustomers();
            case 3 -> addCustomer();
        }
    }

    public static void listCustomers() {
        System.out.println();
        List<Customer> customers = customerDao.getCustomers();
        System.out.println(customers.isEmpty() ? "The customer list is empty!" : "Customer list:");
        if (!customers.isEmpty()) {
            AtomicInteger i = new AtomicInteger(1);
            customers.forEach(customer -> {
                System.out.println(i + ". " + customer.getCustomerName());
                i.getAndIncrement();
            });
            System.out.println("0. Back");
            int option = getOption();
            if (option != 0) {
                openCustomerMenu(customers.stream()
                        .skip(option - 1)
                        .findFirst()
                        .orElseThrow()
                        .getCustomerId()
                        .orElseThrow());
            }
        }
        System.out.println();
    }

    public static void addCustomer() {
        System.out.println();
        System.out.println("Enter the customer name:");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        customerDao.addCustomer(new Customer(name));
        System.out.println("The customer was added!");
        System.out.println();
    }

    public static void openCustomerMenu(int id) {
        System.out.println();
        var optional = customerDao.getCustomerById(id);
        if (optional.isEmpty()) {
            System.out.println("Not an option!");
            return;
        }
        boolean localLoop = true;
        while (localLoop) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");
            switch (getOption()) {
                case 1 -> rentCar(id);
                case 2 -> returnCar(id);
                case 3 -> outputRentedCar(id);
                case 0 -> localLoop = false;
            }
            if (localLoop) {
                System.out.println();
            }
        }
    }

    public static void rentCar(int id) {
        if (checkRentedCar(id)) {
            System.out.println();
            System.out.println("You've already rented a car!");
            return;
        }
        int companyId = listCompanies();
        if (companyId == 0) {
            return;
        }
        int carId = chooseCar(companyId);
        if (carId == 0) {
            return;
        }
        customerDao.addRentedCar(id, carId);
        Optional<Car> optional = carDao.getCarById(carId);
        System.out.println();
        System.out.println("You rented '" + optional.orElseThrow().getCarName() + "'");
    }

    public static int chooseCar(int companyId) {
        System.out.println();
        List<Car> cars = carDao.getCarsByCompanyNotRented(companyId);
        System.out.println(cars.isEmpty() ? "The car list is empty!" : "Car list:");
        if (!cars.isEmpty()) {
            AtomicInteger i = new AtomicInteger(1);
            cars.forEach(car -> {
                System.out.println(i + ". " + car.getCarName());
                i.getAndIncrement();
            });
            System.out.println("0. Back");
            int option = getOption();
            if (option != 0) {
                return cars.stream()
                        .skip(option - 1)
                        .findFirst()
                        .orElseThrow()
                        .getCarId()
                        .orElseThrow();
            }
        }
        return 0;
    }

    public static void returnCar(int id) {
        System.out.println();
        if (!checkRentedCar(id)) {
            System.out.println("You didn't rent a car!");
            return;
        }
        customerDao.returnRentedCar(id);
        System.out.println("You've returned a rented car!");
    }

    public static void outputRentedCar(int id) {
        System.out.println();
        if (!checkRentedCar(id)) {
            System.out.println("You didn't rent a car!");
            return;
        }
        System.out.println("Your rented car:");
        Optional<Car> optional = customerDao.getRentedCar(id);
        Car car = optional.orElseThrow();
        System.out.println(car.getCarName());
        System.out.println("Company:");
        Optional<Company> optionalCompany = companyDao.getCompanyById(car.getCompanyId());
        Company company = optionalCompany.orElseThrow();
        System.out.println(company.getCompanyName());
    }

    public static boolean checkRentedCar(int id) {
        Optional<Car> optional = customerDao.getRentedCar(id);
        return optional.isPresent();
    }

    public static void openManagerMenu() {
        System.out.println();
        System.out.println("1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back");
        switch (getOption()) {
            case 0 -> System.out.println();
            case 1 -> {
                int option = listCompanies();
                if (option != 0) {
                    openCompanyMenu(option);
                } else {
                    openManagerMenu();
                }
            }
            case 2 -> createCompany();
        }
    }

    public static int listCompanies() {
        System.out.println();
        List<Company> companies = companyDao.getCompanies();
        System.out.println(companies.isEmpty() ? "The company list is empty!" : "Choose the company:");
        if (!companies.isEmpty()) {
            AtomicInteger i = new AtomicInteger(1);
            companies.forEach(company -> {
                System.out.println(i + ". " + company.getCompanyName());
                i.getAndIncrement();
            });
            System.out.println("0. Back");
            int option = getOption();
            if (option != 0) {
                return companies.stream()
                        .skip(option - 1)
                        .findFirst()
                        .orElseThrow()
                        .getCompanyId()
                        .orElseThrow();
            }
        }
        return 0;
    }

    public static void createCompany() {
        System.out.println();
        System.out.println("Enter the company name:");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        companyDao.addCompany(new Company(name));
        System.out.println("The company was created!");
        openManagerMenu();
    }

    public static void openCompanyMenu(int id) {
        System.out.println();
        var optional = companyDao.getCompanyById(id);
        if (optional.isEmpty()) {
            System.out.println("Not an option!");
            return;
        }
        boolean localLoop = true;
        while (localLoop) {
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");
            switch (getOption()) {
                case 1 -> listCars(id);
                case 2 -> createCar(id);
                case 0 -> localLoop = false;
            }
            if (localLoop) {
                System.out.println();
            }
        }
        openManagerMenu();
    }

    public static void listCars(int id) {
        System.out.println();
        List<Car> cars = carDao.getCarsByCompany(id);
        System.out.println(cars.isEmpty() ? "The car list is empty!" : "Car list:");
        if (!cars.isEmpty()) {
            AtomicInteger i = new AtomicInteger(1);
            cars.forEach(car -> {
                System.out.println(i + ". " + car.getCarName());
                i.getAndIncrement();
            });
        }
    }

    public static void createCar(int id) {
        System.out.println();
        System.out.println("Enter the car name:");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        carDao.addCar(new Car(name, id));
        System.out.println("The car was added!");
    }

    public static int getOption() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}