package dk.dmi.silla.model;

import dk.dmi.silla.config.Config;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeParseException;

public class Measurement {
    private Integer statid;
    private LocalDateTime timeobs;
    private Integer elemNo;
    private Double elemVal;
    private Label label;
    private Long interpolationId;

    public Measurement(JsonObject jsonObject) throws JsonException {
        statid = getJsonInteger(jsonObject, "statid");
        String rawTimeobs = getJsonString(jsonObject, "timeobs");
        elemNo = getJsonInteger(jsonObject, "elem_no");
        elemVal = getJsonDouble(jsonObject, "elem_val");
        Long rawLabel = getJsonLong(jsonObject, "label");
        interpolationId = getJsonLong(jsonObject, "interpolation_id");

        if (statid == null) {
            throw new JsonException("Statid is missing");
        }

        if (interpolationId == null && rawTimeobs == null) {
            throw new JsonException("One of 'interpolation_id' and 'timeobs' is required");
        }

        if (rawLabel != null) label = new Label(rawLabel);

        if (rawTimeobs != null) {
            try {
                timeobs = LocalDateTime.parse(rawTimeobs, Config.jsonFormatter);
            } catch (DateTimeParseException e) {
                throw new JsonException("'timeobs' has unknown format");
            }
        }
    }

    private Integer getJsonInteger(JsonObject jsonObject, String key) {
        try {
            return jsonObject.getInt(key);
        } catch (Exception e) {
            return null;
        }
    }

    private Long getJsonLong(JsonObject jsonObject, String key) {
        try {
            return jsonObject.getJsonNumber(key).longValue();
        } catch (Exception e) {
            return null;
        }
    }

    private Double getJsonDouble(JsonObject jsonObject, String key) {
        try {
            return Double.valueOf(jsonObject.getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    private String getJsonString(JsonObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Measurement(ResultSet resultSet) throws SQLException {
        elemVal = resultSet.getDouble("elem_val");
        elemNo = resultSet.getInt("elem_no");
        label = new Label(resultSet.getLong("label"));
        interpolationId = resultSet.getLong("interpolation_id");
        statid = resultSet.getInt("statid");
        OffsetDateTime offsetDateTime = resultSet.getObject("timeobs", OffsetDateTime.class);
        ZonedDateTime zonedDateTime = offsetDateTime.atZoneSameInstant(ZoneOffset.UTC);
        timeobs = zonedDateTime.toLocalDateTime();
    }

    public String getKey() {
        if (statid != null && timeobs != null) {
            return statid.toString() + timeobs.toString();
        } else {
            return null;
        }
    }

    public Long getLabel() {
        if (label == null) return null;
        else return this.label.get();
    }

    public LocalDate getMeteoDay() {
        Station station = Config.getStation(statid);
        Integer meteoHour = station.getMeteoHour();
        LocalDateTime meteoDay = timeobs.minusHours(meteoHour + 1);
        return meteoDay.toLocalDate();
    }

    public JsonObject toJson() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        if (interpolationId != null) jsonObjectBuilder.add("interpolation_id", interpolationId);
        if (statid != null) jsonObjectBuilder.add("statid", statid);
        if (timeobs != null) jsonObjectBuilder.add("timeobs", timeobs.format(Config.jsonFormatter));
        if (elemNo != null) jsonObjectBuilder.add("elem_no", elemNo);
        if (elemVal != null) {
            String formatStr = "%." + Config.getParamConfig().getDecimals(elemNo) + "f";
            jsonObjectBuilder.add("elem_val", String.format(formatStr, elemVal));
        }
        if (label != null) jsonObjectBuilder.add("label", label.get());

        return jsonObjectBuilder.build();
    }

    public void setExclude() {
        this.label.setExclude();
    }

    public void setQC() {
        this.label.setQC();
    }

    public Integer getStatid() {
        return statid;
    }

    public void setStatid(Integer statid) {
        this.statid = statid;
    }

    public LocalDateTime getTimeobs() {
        return timeobs;
    }

    public void setTimeobs(LocalDateTime timeobs) {
        this.timeobs = timeobs;
    }

    public Integer getElemNo() {
        return elemNo;
    }

    public void setElemNo(Integer elemNo) {
        this.elemNo = elemNo;
    }

    public Double getElemVal() {
        return elemVal;
    }

    public void setElemVal(Double elemVal) {
        this.elemVal = elemVal;
    }

    public void setLabel(Long label) {
        this.label = new Label(label);
    }

    public Long getInterpolationId() {
        return interpolationId;
    }

    public void setInterpolation_id(Long interpolationId) {
        this.interpolationId = interpolationId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Measurement other = (Measurement) obj;

        if ((this.elemVal == null) != (other.elemVal == null)) return false;
        if ((this.interpolationId == null) != (other.interpolationId == null)) return false;
        if ((this.statid == null) != (other.statid == null)) return false;
        if ((this.timeobs == null) != (other.timeobs == null)) return false;
        if ((this.label == null) != (other.label == null)) return false;
        if ((this.elemNo == null) != (other.elemNo == null)) return false;

        if (this.label != null && !this.label.equals(other.label)) return false;
        if (this.statid != null && !this.statid.equals(other.statid)) return false;
        if (this.timeobs != null && !this.timeobs.equals(other.timeobs)) return false;
        if (this.elemNo != null && !this.elemNo.equals(other.elemNo)) return false;
        if (this.elemVal != null && !this.elemVal.equals(other.elemVal)) return false;
        if (this.interpolationId != null && !this.interpolationId.equals(other.interpolationId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return interpolationId.hashCode();
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
