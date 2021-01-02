package dk.dmi.silla.repository.labelupdate;

import dk.dmi.silla.config.Config;
import dk.dmi.silla.model.Measurement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Transaction {
    private final PreparedStatement queueInsertPS;
    private final PreparedStatement queueUpdatePS;
    private final Connection connection;
    private static final String SQL_RECALCULATE_INSERT
            = "INSERT INTO work.basis_daily_na_to_be_calc(type, the_date) SELECT 'points', ? "
            + "WHERE NOT EXISTS (SELECT 1 FROM work.basis_daily_na_to_be_calc WHERE type='points' AND the_date = ?)";
    private static final String SQL_RECALCULATE_UPDATE
            = "UPDATE work.basis_daily_na_to_be_calc SET ins_date=NOW() WHERE type='points' AND the_date = ?";
    private final PreparedStatementPool psPool;
    private final Set<LocalDate> daySet = new HashSet<>();

    public Transaction(Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(false);
        queueInsertPS = this.connection.prepareStatement(SQL_RECALCULATE_INSERT);
        queueUpdatePS = this.connection.prepareStatement(SQL_RECALCULATE_UPDATE);
        this.psPool = new PreparedStatementPool(this.connection);
    }

    public void add(Measurement measurement, UpdateType updateType) throws SQLException {
        PreparedStatement ps = psPool.get(measurement.getElemNo(), updateType);
        updateType.addStatementToBatch(ps, measurement);

        if (updateType == UpdateType.EXCLUDE) {
            daySet.add(measurement.getMeteoDay());
        }
    }

    private void generateUpdateQueue() throws SQLException {
        for (LocalDate localDate : daySet) {
            queueInsertPS.setObject(1, localDate);
            queueInsertPS.setObject(2, localDate);
            queueInsertPS.addBatch();

            queueUpdatePS.setObject(1, localDate);
            queueUpdatePS.addBatch();
        }
    }

    public void execute() throws SQLException {
        generateUpdateQueue();

        List<PreparedStatement> psList = psPool.getAll();
        try {
            for (PreparedStatement ps : psList) ps.executeBatch();
            queueInsertPS.executeBatch();
            queueUpdatePS.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            System.err.println("Label update transaction failed");
            if (Config.DEBUG) {
                e.printStackTrace();
            }
            try {
                connection.rollback();
            } catch (SQLException err) {
                System.err.println("Transaction rollback failed");
                err.printStackTrace();
                throw err;
            }
            throw e;
        }
    }

    public void close() throws SQLException {
        psPool.close();
        if (queueInsertPS != null) queueInsertPS.close();
        if (queueUpdatePS != null) queueUpdatePS.close();
    }

}
