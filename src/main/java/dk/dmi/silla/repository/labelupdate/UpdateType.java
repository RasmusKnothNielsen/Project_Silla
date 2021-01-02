package dk.dmi.silla.repository.labelupdate;

import dk.dmi.silla.model.Measurement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum UpdateType {
    // Set QC flag to 2
    QC(" SET label = cast(overlay(lpad(cast ( label as char(15) ),9,'0') placing '2' from 6) as bigint) where interpolation_id = ? and statid = ?;"),
    // Set Exclude flag to 3 and QC flag to 2
    EXCLUDE(" SET label = cast(overlay(overlay(lpad(cast ( label as char(15) ),9,'0') placing '2' from 6) placing '3' from 1) as bigint) where interpolation_id = ? and statid = ?;");

    private final String query;

    UpdateType(String query) {
        this.query = query;
    }

    public String getUpdateLabelQuery(String table) {
        return "UPDATE " + table + this.query;
    }

    public void addStatementToBatch(PreparedStatement ps, Measurement measurement) throws SQLException {
        switch(this) {
            case QC:
            case EXCLUDE:
                ps.setLong(1, measurement.getInterpolationId());
                ps.setLong(2, measurement.getStatid());
        }
        ps.addBatch();
    }
}
