# Banking Management System

Console-based Core Java capstone project for a simple banking workflow.

## What this project does

This application simulates the main operations of a small banking system without using any database.
It runs entirely from the console and stores data in CSV files so that accounts and transaction history
remain available after the program is closed and reopened.

## Features

- Create account
- Deposit money
- Withdraw money
- Transfer funds between accounts
- Check balance
- View transaction history
- List all accounts
- File-based persistence using plain CSV files

## Why file storage is used

The assignment brief explicitly allows a console-based solution and mentions no database integration as a limitation.
Using CSV files is the right scope here because:

- it keeps the project simple and easy to demo in a viva
- it preserves data across runs
- it supports transaction history properly
- it matches the stated future enhancement idea of moving to JDBC/MySQL later

## Project structure

```text
src/
  banking/
    BankAccount.java
    BankRepository.java
    BankSystem.java
    BankingException.java
    ConsoleApp.java
    ConsoleIO.java
    Csv.java
    TransactionRecord.java
    TransactionType.java
data/
  accounts.csv
  transactions.csv
```

## How to run

From the repo root:

```powershell
mkdir out
javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out banking.ConsoleApp
```

If you use an IDE, mark `src` as the source root and run `banking.ConsoleApp`.

## Menu flow

1. Create account
2. Deposit
3. Withdraw
4. Transfer funds
5. Balance inquiry
6. Transaction history
7. List all accounts
8. Exit

## Data files

- `data/accounts.csv` stores account master data
- `data/transactions.csv` stores every account-level transaction

Both files are updated after each successful operation.

### Problem statement

Manual banking operations are slow and error-prone. This project automates core banking actions in a small
console-based system.

### Objective

To demonstrate Core Java, OOP, exception handling, collections, file handling, and basic software design concepts.

### Core Java concepts used

- Classes and objects
- Encapsulation
- Collections (`Map`, `List`)
- File handling
- Exception handling
- `BigDecimal` for money-safe calculations
- `LocalDateTime` for timestamps

### Limitations

- Console-based only
- No database integration
- No login/authentication layer
- No network or online banking support

### Future enhancements

- JDBC/MySQL storage
- JavaFX GUI
- Authentication and roles
- Search/filter on transaction history
- Loan and account statement modules

## Sample test cases

- Create a new account with a valid name and opening balance
- Deposit into an existing account
- Withdraw within balance
- Attempt withdrawal above balance
- Transfer between two valid accounts
- Check balance after each operation
- Restart the app and confirm data still exists

