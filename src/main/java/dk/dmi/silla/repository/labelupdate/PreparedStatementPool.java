package dk.dmi.silla.repository.labelupdate;

import dk.dmi.silla.config.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class PreparedStatementPool {

    private final List<Map<Integer, PreparedStatement>> psMaps = new ArrayList<>();
    private final Connection connection;

    public PreparedStatementPool(Connection connection) {
        this.connection = connection;
        for (int i = 0; i < UpdateType.values().length; i++) {
            psMaps.add(new HashMap<>());
        }
    }

    public PreparedStatement get(Integer elemNo, UpdateType updateType) throws SQLException {
        PreparedStatement ps = psMaps.get(updateType.ordinal()).get(elemNo);
        if (ps == null) {
            String psString = updateType.getUpdateLabelQuery(Config.getDbTableName(elemNo));
            ps = this.connection.prepareStatement(psString);
            psMaps.get(updateType.ordinal()).put(elemNo, ps);
        }
        return ps;
    }

    public void close() throws SQLException {
        for(Map<Integer, PreparedStatement> map : psMaps) {
            for (Map.Entry<Integer, PreparedStatement> entry : map.entrySet()) {
                PreparedStatement ps = entry.getValue();
                if (ps != null) ps.close();
            }
        }
    }

    public List<PreparedStatement> getAll() {
        List<PreparedStatement> psList = new ArrayList<>();
        for(Map<Integer, PreparedStatement> map : psMaps) {
            psList.addAll(map.values());
        }
        return psList;
    }
}
