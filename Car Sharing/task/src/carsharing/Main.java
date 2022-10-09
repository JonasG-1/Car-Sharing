package carsharing;

public class Main {

    public static void main(String[] args) {
        Database database = new Database(args[1]);
        database.execute("CREATE TABLE COMPANY (" +
                "ID INT," +
                "NAME VARCHAR(30)" +
                ");");
    }
}