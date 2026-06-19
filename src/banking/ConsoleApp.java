package banking;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    public static void main(String[] args) {
        BankRepository repository = new BankRepository(java.nio.file.Path.of("data"));
        BankSystem bankSystem = new BankSystem(repository);
        ConsoleIO io = new ConsoleIO(new Scanner(System.in));

        System.out.println("========================================");
        System.out.println(" Banking Management System");
        System.out.println("========================================");

        while (true) {
            printMenu();
            int choice = io.promptInt("Choose an option: ");
            try {
                handleChoice(choice, io, bankSystem);
                if (choice == 8) {
                    break;
                }
            } catch (BankingException e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("1. Create account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer funds");
        System.out.println("5. Balance inquiry");
        System.out.println("6. Transaction history");
        System.out.println("7. List all accounts");
        System.out.println("8. Exit");
    }

    private static void handleChoice(int choice, ConsoleIO io, BankSystem bankSystem) {
        switch (choice) {
            case 1 -> createAccount(io, bankSystem);
            case 2 -> deposit(io, bankSystem);
            case 3 -> withdraw(io, bankSystem);
            case 4 -> transfer(io, bankSystem);
            case 5 -> balanceInquiry(io, bankSystem);
            case 6 -> transactionHistory(io, bankSystem);
            case 7 -> listAccounts(bankSystem);
            case 8 -> System.out.println("Exiting application. Data has been saved.");
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    private static void createAccount(ConsoleIO io, BankSystem bankSystem) {
        String name = io.promptRequired("Customer name: ");
        BigDecimal openingBalance = io.promptAmount("Opening balance (0 if none): ");
        BankAccount account = bankSystem.createAccount(name, openingBalance);
        System.out.println("Account created successfully.");
        System.out.println("Account number: " + account.getAccountNumber());
        System.out.println("Balance: " + formatMoney(account.getBalance()));
    }

    private static void deposit(ConsoleIO io, BankSystem bankSystem) {
        int accountNo = io.promptInt("Account number: ");
        BigDecimal amount = io.promptAmount("Deposit amount: ");
        BankAccount account = bankSystem.deposit(accountNo, amount);
        System.out.println("Deposit successful.");
        System.out.println("New balance: " + formatMoney(account.getBalance()));
    }

    private static void withdraw(ConsoleIO io, BankSystem bankSystem) {
        int accountNo = io.promptInt("Account number: ");
        BigDecimal amount = io.promptAmount("Withdrawal amount: ");
        BankAccount account = bankSystem.withdraw(accountNo, amount);
        System.out.println("Withdrawal successful.");
        System.out.println("New balance: " + formatMoney(account.getBalance()));
    }

    private static void transfer(ConsoleIO io, BankSystem bankSystem) {
        int source = io.promptInt("Source account number: ");
        int destination = io.promptInt("Destination account number: ");
        BigDecimal amount = io.promptAmount("Transfer amount: ");
        bankSystem.transfer(source, destination, amount);
        System.out.println("Transfer successful.");
    }

    private static void balanceInquiry(ConsoleIO io, BankSystem bankSystem) {
        int accountNo = io.promptInt("Account number: ");
        BankAccount account = bankSystem.getAccount(accountNo);
        System.out.println("Account holder: " + account.getCustomerName());
        System.out.println("Balance: " + formatMoney(account.getBalance()));
    }

    private static void transactionHistory(ConsoleIO io, BankSystem bankSystem) {
        int accountNo = io.promptInt("Account number: ");
        List<TransactionRecord> records = bankSystem.getTransactionHistory(accountNo);
        if (records.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("Transaction history for account " + accountNo + ":");
        for (TransactionRecord record : records) {
            System.out.println(
                    record.getTimestamp() + " | " +
                    record.getType() + " | " +
                    formatMoney(record.getAmount()) + " | " +
                    record.getDescription()
            );
        }
    }

    private static void listAccounts(BankSystem bankSystem) {
        List<BankAccount> accounts = bankSystem.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (BankAccount account : accounts) {
            System.out.println(
                    account.getAccountNumber() + " | " +
                    account.getCustomerName() + " | " +
                    formatMoney(account.getBalance()) + " | " +
                    account.getCreatedAt()
            );
        }
    }

    private static String formatMoney(BigDecimal amount) {
        return "Rs. " + amount.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}

