package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class TransactionRecord {
    private final long transactionId;
    private final int accountNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;
    private final Integer relatedAccountNumber;

    public TransactionRecord(long transactionId, int accountNumber, TransactionType type, BigDecimal amount,
                             LocalDateTime timestamp, String description, Integer relatedAccountNumber) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : amount.setScale(2, RoundingMode.HALF_UP);
        this.timestamp = timestamp;
        this.description = description;
        this.relatedAccountNumber = relatedAccountNumber;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public Integer getRelatedAccountNumber() {
        return relatedAccountNumber;
    }

    public String toCsv() {
        return Csv.join(
                String.valueOf(transactionId),
                String.valueOf(accountNumber),
                type.name(),
                amount.toPlainString(),
                timestamp.toString(),
                description,
                relatedAccountNumber == null ? "" : String.valueOf(relatedAccountNumber)
        );
    }

    public static TransactionRecord fromCsv(String[] row) {
        Integer related = row.length > 6 && !row[6].isBlank() ? Integer.parseInt(row[6]) : null;
        return new TransactionRecord(
                Long.parseLong(row[0]),
                Integer.parseInt(row[1]),
                TransactionType.valueOf(row[2]),
                new BigDecimal(row[3]),
                LocalDateTime.parse(row[4]),
                row[5],
                related
        );
    }
}

