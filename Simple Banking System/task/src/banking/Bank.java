package banking;

import java.util.Random;

public class Bank {

    Database database;

    public Bank(Database database) {
        this.database = database;

        // we need to reload max id to BankAccount
        if (BankAccount.currentId == 0) {
            BankAccount.currentId = database.returnIntFromQuery("SELECT MAX(id) FROM card");
        }
    }

    BankAccount createAccount() {

        BankAccount bankAccount = createAndCheckIfUnique();

        database.addAccountDB(bankAccount);

        return bankAccount;
    }

    private BankAccount createAndCheckIfUnique() {

        String iin = "400000";

        // generate account Id
        String accountIdentifier = generateRandomString(9);

        // without last
        String cardNumber = iin + accountIdentifier;
        // with
        cardNumber += generateLastNumber(cardNumber);

        return new BankAccount(cardNumber, generateRandomString(4));
    }

    private static String generateRandomString(int amountOfDigits) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        // 9 is length of accountIdentifier
        // 4 length of PIN

        for (int i = 0; i < amountOfDigits; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return String.valueOf(stringBuilder);
    }

    // Luhn algorithm
    protected char generateLastNumber(String cardNumber) {
        char[] charCardNumber = cardNumber.toCharArray();

        int sum = 0;

        for (int i = 0; i < charCardNumber.length; i++) {
            int currentNumber = Character.getNumericValue(charCardNumber[i]);

            if ((i + 1) % 2 != 0) {
                currentNumber *= 2;

                if (currentNumber > 9) {
                    currentNumber -= 9;
                }
            }

            sum += currentNumber;
        }

        int x = 0;

        while ((sum + x) % 10 != 0) {
            x++;
        }

        return Character.forDigit(x, 10);
    }
}
