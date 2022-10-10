package carsharing;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static boolean running;
    private static CompanyDaoImpl companyDao;
    private static CarDaoImpl carDao;

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
        companyDao = new CompanyDaoImpl(database);
        carDao = new CarDaoImpl(database);
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
        System.out.println("0. Exit");
        switch (getOption()) {
            case 0 -> running = false;
            case 1 -> openManagerMenu();
        }
    }

    public static void openManagerMenu() {
        System.out.println();
        System.out.println("1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back");
        switch (getOption()) {
            case 0 -> System.out.println();
            case 1 -> listCompanies();
            case 2 -> createCompany();
        }
    }

    public static void listCompanies() {
        System.out.println();
        ArrayList<Company> companies = (ArrayList<Company>) companyDao.getCompanies();
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
                openCompanyMenu(companies.stream()
                        .skip(option - 1)
                        .findFirst()
                        .orElseThrow()
                        .getCompanyId()
                        .orElseThrow());
            }
        }
        openManagerMenu();
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