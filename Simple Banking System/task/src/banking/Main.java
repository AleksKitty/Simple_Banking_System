package banking;

public class Main {

    public static void main(String[] args) {

        // connect to DB
        // arguments: -fileName db.s3db
        String dbName = args[1];

        Database database = new Database(dbName);
        database.createTableIfNotExists();

        // print current accounts
//        database.selectAll();

        new UserInterface(new Bank(database));
    }
}
