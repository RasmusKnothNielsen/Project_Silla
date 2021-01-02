package dk.dmi.silla.repository;

import dk.dmi.silla.model.Measurement;
import dk.dmi.silla.repository.labelupdate.LabelUpdate;
import dk.dmi.silla.repository.labelupdate.UpdateType;

import java.sql.SQLException;
import java.util.List;

public class MeasurementRepo {

    public MeasurementRepo() {
    }

    public void setQcFlag(List<Measurement> measurementList) throws SQLException {
        LabelUpdate labelUpdate = new LabelUpdate(measurementList, UpdateType.QC);
        labelUpdate.updateDatabase();
    }

    public void setExcludeAndQcFlags(List<Measurement> measurementList) throws SQLException {
        LabelUpdate labelUpdate = new LabelUpdate(measurementList, UpdateType.EXCLUDE);
        labelUpdate.updateDatabase();
    }

}
