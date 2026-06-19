package banking;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BankRepository {
    private static final String ACCOUNT_HEADER = "accountNumber,customerName,balance,createdAt";
    private static final String TRANSACTION_HEADER = "transactionId,accountNumber,type,amount,timestamp,description,relatedAccountNumber";

    private final Path accountsFile;
    private final Path transactionsFile;
    private final Map<Integer, BankAccount> accounts = new LinkedHashMap<>();
    private final List<TransactionRecord> transactions = new ArrayList<>();
    private int nextAccountNumber = 1000001;
    private long nextTransactionId = 1L;

    public BankRepository(Path dataDirectory) {
        this.accountsFile = dataDirectory.resolve("accounts.csv");
        this.transactionsFile = dataDirectory.resolve("transactions.csv");
        load();
    }

    public List<BankAccount> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public Optional<BankAccount> findAccount(int accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    public BankAccount saveNewAccount(String customerName, BigDecimal openingBalance) {
        BankAccount account = new BankAccount(nextAccountNumber++, customerName, openingBalance, LocalDateTime.now());
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public void saveTransaction(TransactionRecord record) {
        transactions.add(record);
    }

    public long nextTransactionId() {
        return nextTransactionId++;
    }

    public List<TransactionRecord> findTransactionsForAccount(int accountNumber) {
        List<TransactionRecord> result = new ArrayList<>();
        for (TransactionRecord record : transactions) {
            if (record.getAccountNumber() == accountNumber) {
                result.add(record);
            }
        }
        return result;
    }

    public void persist() {
        try {
            Files.createDirectories(accountsFile.getParent());
            writeCsv(accountsFile, ACCOUNT_HEADER, accounts.values().stream().map(BankAccount::toCsv).collect(Collectors.toList()));
            writeCsv(transactionsFile, TRANSACTION_HEADER, transactions.stream().map(TransactionRecord::toCsv).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new BankingException("Failed to save data: " + e.getMessage());
        }
    }

    private void load() {
        try {
            Files.createDirectories(accountsFile.getParent());
            readAccounts();
            readTransactions();
            nextAccountNumber = accounts.keySet().stream().mapToInt(Integer::intValue).max().orElse(1000000) + 1;
            nextTransactionId = transactions.stream().mapToLong(TransactionRecord::getTransactionId).max().orElse(0L) + 1L;
        } catch (IOException e) {
            throw new BankingException("Failed to load data: " + e.getMessage());
        }
    }

    private void readAccounts() throws IOException {
        if (!Files.exists(accountsFile)) {
            Files.writeString(accountsFile, ACCOUNT_HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            return;
        }

        for (String line : Files.readAllLines(accountsFile, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("accountNumber,")) {
                continue;
            }
            String[] row = Csv.parse(line);
            BankAccount account = BankAccount.fromCsv(row);
            accounts.put(account.getAccountNumber(), account);
        }
    }

    private void readTransactions() throws IOException {
        if (!Files.exists(transactionsFile)) {
            Files.writeString(transactionsFile, TRANSACTION_HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            return;
        }

        for (String line : Files.readAllLines(transactionsFile, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("transactionId,")) {
                continue;
            }
            String[] row = Csv.parse(line);
            transactions.add(TransactionRecord.fromCsv(row));
        }
    }

    private void writeCsv(Path file, String header, List<String> rows) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(header).append(System.lineSeparator());
        for (String row : rows) {
            builder.append(row).append(System.lineSeparator());
        }
        Files.writeString(file, builder.toString(), StandardCharsets.UTF_8);
    }
}
