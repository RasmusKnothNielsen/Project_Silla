package dk.dmi.silla.config;

import dk.dmi.silla.model.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParamConfig {

    private final HashMap<Integer, Parameter> parameterHashMap = new HashMap<>();

    public ParamConfig() {
    }

    public void add(Integer elem_no, Integer decimals, String dbTableName, String description) {
        Parameter paramMD = new Parameter(elem_no, decimals, dbTableName, description);
        parameterHashMap.put(elem_no, paramMD);
    }

    public void add(Parameter paramMD) {
        parameterHashMap.put(paramMD.getElemNo(), paramMD);
    }

    public String getDbTableName(Integer elemNo) {
        Parameter paramMD = parameterHashMap.get(elemNo);
        return paramMD.getDbTableName();
    }

    public String getDescription(Integer elemNo) {
        Parameter paramMD = parameterHashMap.get(elemNo);
        return paramMD.getDescription();
    }

    public Integer getDecimals(Integer elemNo) {
        Parameter paramMD = parameterHashMap.get(elemNo);
        return paramMD.getDecimals();
    }

    public List<Integer> getParameters() {
        return new ArrayList<>(parameterHashMap.keySet());
    }

    public Boolean contains(Integer elemNo) {
        return parameterHashMap.get(elemNo) != null;
    }

    public List<Parameter> getParameterList() {
        return new ArrayList<Parameter>(parameterHashMap.values());
    }
}
