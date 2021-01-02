package dk.dmi.silla.model;

import dk.dmi.silla.config.Config;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Station {
    private Integer statid;
    private String name;
    private String country;
    private String report_type;
    private String zone;

    public Station(Integer statid, String name, String country, String report_type, String zone) {
        this.statid = statid;
        this.name = name;
        this.country = country;
        this.report_type = report_type;
        this.zone = zone;
    }

    public Integer getStatid() {
        return statid;
    }

    public void setStatid(Integer statid) {
        this.statid = statid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReport_type() {
        return report_type;
    }

    public void setReport_type(String report_type) {
        this.report_type = report_type;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Integer getMeteoHour() {
        return Config.meteoHour(country);
    }

    public JsonObject toJson() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        if (statid != null) jsonObjectBuilder.add("statid", statid);
        if (name != null) jsonObjectBuilder.add("name", name);
        if (country != null) jsonObjectBuilder.add("country", country);
        if (report_type != null) jsonObjectBuilder.add("report_type", report_type);
        if (zone != null) jsonObjectBuilder.add("zone", zone);

        return jsonObjectBuilder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Station other = (Station) obj;

        if((this.statid == null) != (other.statid == null)) return false;
        if((this.name == null) != (other.name == null)) return false;
        if((this.country == null) != (other.country == null)) return false;
        if((this.report_type == null) != (other.report_type == null)) return false;
        if((this.zone == null) != (other.zone == null)) return false;

        if(this.statid != null && !this.statid.equals(other.statid)) return false;
        if(this.name != null && !this.name.equals(other.name)) return false;
        if(this.country != null && !this.country.equals(other.country)) return false;
        if(this.report_type != null && !this.report_type.equals(other.report_type)) return false;
        if(this.zone != null && !this.zone.equals(other.zone)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return statid.hashCode();
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

}
