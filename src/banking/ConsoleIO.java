package banking;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleIO {
    private final Scanner scanner;

    public ConsoleIO(Scanner scanner) {
        this.scanner = scanner;
    }

    public String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public String promptRequired(String message) {
        while (true) {
            String value = prompt(message);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Value cannot be blank.");
        }
    }

    public int promptInt(String message) {
        while (true) {
            String value = prompt(message);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    public BigDecimal promptAmount(String message) {
        while (true) {
            String value = prompt(message);
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }
}

