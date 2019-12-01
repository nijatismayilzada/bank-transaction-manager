package com.revolut.revoluttransactionmanager.repository.testutil;


import com.revolut.revoluttransactionmanager.config.JdbcConnection;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionAction;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionState;
import com.revolut.revoluttransactionmanager.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Currency;

public class ITHelper {

    private final JdbcConnection jdbcConnection;

    public ITHelper(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }


    public ResultSet getTransaction(long transactionId) throws SQLException {
        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement("select transaction_id, account_id, reference, transaction_state, state_updated_at, transaction_type, transaction_action, currency, amount from transaction where transaction_id = ? ");
        statement.setLong(1, transactionId);

        return statement.executeQuery();
    }

    public long createTransaction(long accountId, String reference, TransactionType transactionType, TransactionAction transactionAction, Currency currency, BigDecimal amount) throws SQLException {
        PreparedStatement statement = jdbcConnection.getConnection().prepareStatement(
                "insert into transaction(account_id, reference, transaction_state, state_updated_at, transaction_type, transaction_action, currency, amount) values(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setLong(1, accountId);
        statement.setString(2, reference);
        statement.setString(3, TransactionState.CREATED.name());
        statement.setTimestamp(4, Timestamp.from(Instant.now()));
        statement.setString(5, transactionType.name());
        statement.setString(6, transactionAction.name());
        statement.setString(7, currency.getCurrencyCode());
        statement.setBigDecimal(8, amount);
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();
        return resultSet.getLong(1);
    }
}
