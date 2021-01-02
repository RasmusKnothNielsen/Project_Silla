package dk.dmi.silla;

import dk.dmi.silla.model.Measurement;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MeasurementTest {

    @Test
    public void correctKey() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(rs.getDouble("elem_val")).thenReturn(1.2);
        Mockito.when(rs.getInt("elem_no")).thenReturn(101);
        Mockito.when(rs.getLong("label")).thenReturn(1100L);
        Mockito.when(rs.getLong("interpolation_id")).thenReturn(54321L);
        Mockito.when(rs.getInt("statid")).thenReturn(422000);
        Mockito.when(rs.getObject("timeobs",OffsetDateTime.class)).thenReturn(OffsetDateTime.of(2020,10,10,1,0,0,0, ZoneOffset.UTC));
        Measurement measurement = new Measurement(rs);
        assertEquals("4220002020-10-10T01:00", measurement.getKey());
    }

    @Test
    public void correctValues() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(rs.getDouble("elem_val")).thenReturn(1.2);
        Mockito.when(rs.getInt("elem_no")).thenReturn(101);
        Mockito.when(rs.getLong("label")).thenReturn(1100L);
        Mockito.when(rs.getLong("interpolation_id")).thenReturn(54321L);
        Mockito.when(rs.getInt("statid")).thenReturn(422000);
        Mockito.when(rs.getObject("timeobs",OffsetDateTime.class)).thenReturn(OffsetDateTime.of(2020,10,10,1,0,0,0, ZoneOffset.UTC));
        Measurement measurement = new Measurement(rs);
        assertEquals((Double)1.2, measurement.getElemVal());
        assertEquals((Integer)101, measurement.getElemNo());
        assertEquals((Long)1100L, measurement.getLabel());
        assertEquals((Long)54321L, measurement.getInterpolationId());
        assertEquals((Integer)422000, measurement.getStatid());
        assertEquals("2020-10-10T01:00", measurement.getTimeobs().toString());
    }
}
