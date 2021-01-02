package dk.dmi.silla.repository.labelupdate;

import dk.dmi.silla.config.Config;
import dk.dmi.silla.model.Measurement;
import dk.dmi.silla.repository.DBConnections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class LabelUpdate {
    private static final String SQL_GET_INTERPOLATION_ID = "SELECT interpolation_id FROM basis_hourly_na_interpolation WHERE elem_no = ? AND timeobs = ? ;";

    private final List<Measurement> measurementList;
    private final UpdateType updateType;

    public LabelUpdate(List<Measurement> newMeasurementList, UpdateType updateType) throws SQLException {
        measurementList = newMeasurementList;
        this.updateType = updateType;
        validateList();
    }

    public void updateDatabase() throws SQLException {
        Connection writeConnection = DBConnections.getWriteConnection();
        writeConnection.setAutoCommit(false);

        Transaction transaction = new Transaction(writeConnection);

        for (Measurement m : measurementList) transaction.add(m, updateType);

        transaction.execute();
        transaction.close();
        writeConnection.close();
    }


    private void validateList() throws SQLException {
        try (Connection readConnection = DBConnections.getReadConnection()) {
            for (Measurement m : measurementList) {
                if (m == null) {
                    throw new NoSuchElementException("Null measurement");
                }
                if (!Config.getParamConfig().contains(m.getElemNo())) {
                    throw new NoSuchElementException("Unknown elem_no");
                }
                if (m.getInterpolationId() == null) {
                    m.setInterpolation_id(getInterpolationId(m, readConnection));
                }
            }
        }
    }

    private Long getInterpolationId(Measurement m, Connection connection) {
        try {
            PreparedStatement ps = connection.prepareStatement(SQL_GET_INTERPOLATION_ID);
            ps.setInt(1, m.getElemNo());
            ps.setString(2, m.getTimeobs().toString());
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (rs.next()) {
                Long interpolation_id = rs.getLong(1);
                rs.close();
                return interpolation_id;
            }
        } catch (SQLException e) {
            // NOP
        }

        throw new NoSuchElementException("Interpolation_id not found");
    }

}
