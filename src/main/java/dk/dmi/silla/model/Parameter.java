package dk.dmi.silla.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Parameter implements Comparable<Parameter> {
    private Integer elemNo;
    private Integer decimals;
    private String dbTableName;
    private String description;

    public Parameter(Integer elemNo, Integer decimals, String dbTableName, String description) {
        this.elemNo = elemNo;
        setDecimals(decimals);
        setDbTableName(dbTableName);
        setDescription(description);
    }

    public Parameter setDescription(String description) {
        this.description = description.trim();
        return this;
    }

    public Parameter setDecimals(Integer decimals) {
        this.decimals = decimals;
        return this;
    }

    public Parameter setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName.trim();
        return this;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getElemNo() {
        return elemNo;
    }

    public JsonObject toJson() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        if (elemNo != null) jsonObjectBuilder.add("elem_no", elemNo);
        if (decimals != null) jsonObjectBuilder.add("decimals", decimals);
        if (dbTableName != null) jsonObjectBuilder.add("dbTableName", dbTableName);
        if (description != null) jsonObjectBuilder.add("description", description);

        return jsonObjectBuilder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Parameter other = (Parameter) obj;

        if((this.elemNo == null) != (other.elemNo == null)) return false;
        if((this.decimals == null) != (other.decimals == null)) return false;
        if((this.dbTableName == null) != (other.dbTableName == null)) return false;
        if((this.description == null) != (other.description == null)) return false;

        if(this.elemNo != null && !this.elemNo.equals(other.elemNo)) return false;
        if(this.decimals != null && !this.decimals.equals(other.decimals)) return false;
        if(this.dbTableName != null && !this.dbTableName.equals(other.dbTableName)) return false;
        if(this.description != null && !this.description.equals(other.description)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return elemNo.hashCode();
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    @Override
    public int compareTo(Parameter other) {
        return this.elemNo - other.elemNo;
    }
}
