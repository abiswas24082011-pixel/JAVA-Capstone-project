package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public class BankSystem {
    private final BankRepository repository;

    public BankSystem(BankRepository repository) {
        this.repository = repository;
    }

    public BankAccount createAccount(String customerName, BigDecimal openingBalance) {
        String name = normalizeName(customerName);
        BigDecimal balance = normalizeAmount(openingBalance);
        BankAccount account = repository.saveNewAccount(name, balance);
        LocalDateTime now = LocalDateTime.now();

        repository.saveTransaction(new TransactionRecord(
                repository.nextTransactionId(),
                account.getAccountNumber(),
                TransactionType.ACCOUNT_CREATED,
                BigDecimal.ZERO,
                now,
                "Account created",
                null
        ));

        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            repository.saveTransaction(new TransactionRecord(
                    repository.nextTransactionId(),
                    account.getAccountNumber(),
                    TransactionType.DEPOSIT,
                    balance,
                    now,
                    "Opening deposit",
                    null
            ));
        }

        repository.persist();
        return account;
    }

    public BankAccount deposit(int accountNumber, BigDecimal amount) {
        BankAccount account = getRequiredAccount(accountNumber);
        BigDecimal normalized = normalizeAmount(amount);
        account.deposit(normalized);
        repository.saveTransaction(new TransactionRecord(
                repository.nextTransactionId(),
                accountNumber,
                TransactionType.DEPOSIT,
                normalized,
                LocalDateTime.now(),
                "Cash deposit",
                null
        ));
        repository.persist();
        return account;
    }

    public BankAccount withdraw(int accountNumber, BigDecimal amount) {
        BankAccount account = getRequiredAccount(accountNumber);
        BigDecimal normalized = normalizeAmount(amount);
        account.withdraw(normalized);
        repository.saveTransaction(new TransactionRecord(
                repository.nextTransactionId(),
                accountNumber,
                TransactionType.WITHDRAWAL,
                normalized,
                LocalDateTime.now(),
                "Cash withdrawal",
                null
        ));
        repository.persist();
        return account;
    }

    public void transfer(int sourceAccountNumber, int destinationAccountNumber, BigDecimal amount) {
        if (sourceAccountNumber == destinationAccountNumber) {
            throw new BankingException("Source and destination accounts must be different.");
        }

        BankAccount source = getRequiredAccount(sourceAccountNumber);
        BankAccount destination = getRequiredAccount(destinationAccountNumber);
        BigDecimal normalized = normalizeAmount(amount);

        source.withdraw(normalized);
        destination.deposit(normalized);

        LocalDateTime now = LocalDateTime.now();
        repository.saveTransaction(new TransactionRecord(
                repository.nextTransactionId(),
                sourceAccountNumber,
                TransactionType.TRANSFER_OUT,
                normalized,
                now,
                "Transfer to account " + destinationAccountNumber,
                destinationAccountNumber
        ));
        repository.saveTransaction(new TransactionRecord(
                repository.nextTransactionId(),
                destinationAccountNumber,
                TransactionType.TRANSFER_IN,
                normalized,
                now,
                "Transfer from account " + sourceAccountNumber,
                sourceAccountNumber
        ));
        repository.persist();
    }

    public BankAccount getAccount(int accountNumber) {
        return getRequiredAccount(accountNumber);
    }

    public List<BankAccount> getAllAccounts() {
        return repository.getAllAccounts();
    }

    public List<TransactionRecord> getTransactionHistory(int accountNumber) {
        getRequiredAccount(accountNumber);
        return repository.findTransactionsForAccount(accountNumber);
    }

    private BankAccount getRequiredAccount(int accountNumber) {
        return repository.findAccount(accountNumber)
                .orElseThrow(() -> new BankingException("Account " + accountNumber + " not found."));
    }

    private String normalizeName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new BankingException("Customer name cannot be blank.");
        }
        return customerName.trim();
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Amount must be greater than zero.");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
