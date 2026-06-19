package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class BankAccount {
    private final int accountNumber;
    private final String customerName;
    private BigDecimal balance;
    private final LocalDateTime createdAt;

    public BankAccount(int accountNumber, String customerName, BigDecimal balance, LocalDateTime createdAt) {
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = normalize(balance);
        this.createdAt = createdAt;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new BankingException("Insufficient balance.");
        }
        balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public String toCsv() {
        return Csv.join(
                String.valueOf(accountNumber),
                customerName,
                balance.toPlainString(),
                createdAt.toString()
        );
    }

    public static BankAccount fromCsv(String[] row) {
        return new BankAccount(
                Integer.parseInt(row[0]),
                row[1],
                new BigDecimal(row[2]),
                LocalDateTime.parse(row[3])
        );
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be greater than zero.");
        }
    }

    private static BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : value.setScale(2, RoundingMode.HALF_UP);
    }
}

