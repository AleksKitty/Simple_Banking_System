package banking;

import java.sql.*;

public class Database {

    protected String url;

    protected Database(String dbName) {
        this.url = "jdbc:sqlite:" + dbName;
    }


    protected void createTableIfNotExists() {
        Connection connection = null;
        try {
            // create a connection to the database
            connection = DriverManager.getConnection(url);

            try (Statement statement = connection.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER PRIMARY KEY, " +
                        "number TEXT NOT NULL, " +
                        "pin TEXT NOT NULL, " +
                        "balance INTEGER DEFAULT 0)");


            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    protected void addAccountDB(BankAccount bankAccount) {
        String sql = "INSERT INTO card (id, number, pin) VALUES " + String.format("(%d, '%s', '%s')",
                bankAccount.getId(), bankAccount.getCardNumber(), bankAccount.getPin());
        executeStatement(sql);
    }


    protected BankAccount checkAccountWithPin(String cardNumber, String pin) {
        String sql = "SELECT * FROM card WHERE number = " + cardNumber + " AND pin = " + pin + ";";

        System.out.println("check cardNumber: " + cardNumber);
        System.out.println("check pin: " + pin);

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("before rs.next");

            if (rs.next()) {

                System.out.println("in rs.next");

                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("number") + "\t" +
                        rs.getString("pin") + "\t" +
                        rs.getInt("balance"));

                return new BankAccount(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    protected boolean checkAccountWithOutPin(String cardNumber) {
        String sql = "SELECT * FROM card WHERE number = " + cardNumber;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    protected void addBalance(BankAccount bankAccount, int inputBalance) {
        String sql = "UPDATE card SET balance = balance + " + inputBalance + " WHERE number = " + bankAccount.getCardNumber();
        this.executeStatement(sql);
        bankAccount.setBalance(this.returnIntFromQuery("SELECT balance FROM card WHERE number = " + bankAccount.getCardNumber()));
    }

    protected void deleteAccount(BankAccount bankAccount) {
        String sql = "DELETE FROM card WHERE number = " + bankAccount.getCardNumber();
        executeStatement(sql);
    }

    protected void selectAll() {
        String sql = "SELECT * FROM card";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            System.out.println("id\tcard number\t\t\tpin\t\tbalance");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("number") + "\t" +
                        rs.getString("pin") + "\t" +
                        rs.getInt("balance"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected int returnIntFromQuery(String sql) {

        try (Connection conn = this.connect();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    private void executeStatement(String sql) {
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void transferMoneyFromOneAccountToAnother(BankAccount accountFrom, String cardNumberTo, int amountOfMoney) {

        if (amountOfMoney > accountFrom.getBalance()) {
            System.out.println("Not enough money!");
            return;
        }

        if (cardNumberTo.equals(accountFrom.getCardNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }

        String updateAccountFrom = "UPDATE card SET balance = balance - " + amountOfMoney + " WHERE number = " + accountFrom.getCardNumber();

        String updateAccountTo = "UPDATE card SET balance = balance + " + amountOfMoney + " WHERE number = " + cardNumberTo;

        try (Connection con = this.connect()) {

            // Disable auto-commit mode
            con.setAutoCommit(false);

            this.executeStatement(updateAccountFrom);
            this.executeStatement(updateAccountTo);

            con.commit();

            accountFrom.setBalance(this.returnIntFromQuery("SELECT balance FROM card WHERE number = " + accountFrom.getCardNumber()));
            System.out.println("Success!\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
