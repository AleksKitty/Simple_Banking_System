package banking;

import java.util.Scanner;

public class UserInterface {

    private static final Scanner scanner = new Scanner(System.in);

    private final Bank bank;

    private BankAccount loggedBankAccount;

    public UserInterface(Bank bank) {
        this.bank = bank;

        mainMenu();
    }

    private void mainMenu() {
        String command;

        boolean isExit = false;

        while (!isExit) {

            printMainMenu();

            command = scanner.nextLine();

            if (command.contains("1")) {

                createAccount();

            } else if (command.contains("2")) {

                BankAccount trytoLogInAccount = logIntoAccount();

                if (trytoLogInAccount != null) {
                    this.loggedBankAccount = trytoLogInAccount;
                    isExit = accountOptions();
                }

            } else if (command.contains("0")) {

                isExit = true;
                System.out.println("\nBye!");

            } else {
                System.out.println("Wrong input\n");
            }
        }
    }

    private void createAccount() {
        BankAccount bankAccount = bank.createAccount();

        System.out.println("\nYour card has been created");
        System.out.println("Your card number:\n" + bankAccount.getCardNumber());
        System.out.println("Your card PIN:\n" + bankAccount.getPin() + "\n");
    }

    private BankAccount logIntoAccount() {

        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.nextLine();

        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        BankAccount currentBankAccount = bank.database.checkAccountWithPin(cardNumber, pin);
        if (currentBankAccount != null) {
            System.out.println("\nYou have successfully logged in!\n");
            return currentBankAccount;
        } else {
            System.out.println("\nWrong card number or PIN!\n");
            return null;
        }
    }

    private boolean accountOptions() {
        String command;

        boolean localExit = false;

        while (!localExit) {

            printMenuAccount();

            command = scanner.nextLine();

            if (command.contains("1")) {
                System.out.println("\nBalance: " + this.loggedBankAccount.getBalance() + "\n");

            } else if (command.contains("2")) {
                System.out.println("\nEnter income:");

                try {
                    int inputBalance = Integer.parseInt(scanner.nextLine());
                    this.bank.database.addBalance(this.loggedBankAccount, inputBalance);
                    System.out.println("\nIncome was added!\n");
                } catch (NumberFormatException nfe) {
                    System.out.println("\nPlease, enter a number\n");
                }
            } else if (command.contains("3")) {
                System.out.println("\nTransfer:");
                System.out.println("\nEnter card number:");

                try {
                    String cardNumber = scanner.nextLine();
                    if (checkCardNumberForTransferring(cardNumber)) {
                        System.out.println("\nEnter how much money you want to transfer:");
                        int amountOfMoney = Integer.parseInt(scanner.nextLine());
                        this.bank.database.transferMoneyFromOneAccountToAnother(this.loggedBankAccount, cardNumber, amountOfMoney);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("\nPlease, enter a number\n");
                }
            } else if (command.contains("4")) {
                this.bank.database.deleteAccount(this.loggedBankAccount);
                this.loggedBankAccount = null;
                localExit = true;
                System.out.println("\nThe account has been closed!\n");
            } else if (command.contains("5")) {
                this.loggedBankAccount = null;
                localExit = true;
                System.out.println("\nYou have successfully logged out!\n");
            } else if (command.contains("0")) {
                System.out.println("\nBye!");
                return true;
            } else {
                System.out.println("Wrong input\n");
            }
        }

        return false;
    }


    private void printMainMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    private void printMenuAccount() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    private boolean checkCardNumberForTransferring(String cardNumber) {
        int lastSymbol = cardNumber.length() - 1;

        if (this.bank.generateLastNumber(cardNumber.substring(0, lastSymbol)) != cardNumber.toCharArray()[lastSymbol]) {
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return false;
        } else if (!this.bank.database.checkAccountWithOutPin(cardNumber)) {
            System.out.println("Such a card does not exist.\n");
            return false;
        }

        return true;
    }
}
