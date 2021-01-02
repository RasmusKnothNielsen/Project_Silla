const MS_PER_HOUR = 3_600_000;

class Config {

    DEBUG = true;
    TIMING_MODE = true;
    URL_API = "api/";
    URL_PARAMS = this.URL_API + "params";
    URL_STATIONS = this.URL_API + "stations";

    constructor() {
    };

    async init() {

        this.parametersAvailable = new Parameters(this);
        const parameterFetch = this.parametersAvailable.update();

        this.stations = new Stations(this);
        const stationFetch = this.stations.update();

        // Execute in parallel
        spinner_on("Loading parameters and stations");
        await parameterFetch;
        await stationFetch;
        spinner_off();

        this.parametersEnabled = new Parameters(this);
        for (const parameter of this.parametersAvailable.getParameterList()) {
            this.parametersEnabled.add(parameter);
        }

        return this;
    }

    getAvailableParameterNumbers = () => {
        return this.parametersAvailable.getParameterNumbers();
    };

    getAvailableParameterList = () => {
        return this.parametersAvailable.getParameterList();
    };

    getEnabledParameterNumbers = () => {
        return this.parametersEnabled.getParameterNumbers();
    };

    getEnabledParameterList = () => {
        return this.parametersEnabled.getParameterList();
    };

    getParameter = elemNo => {
        return this.parametersAvailable.getParameter(elemNo);
    }

    getAvailableStationNumbers = () => {
        return this.stations.getStationIds();
    };

    getAvailableStationList = () => {
        return this.stations.getStationList();
    };

    presetQueries = [
        {
            title: "NA 24h",
            tooltip : "All measurements from North Atlantic the last 24 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations )\n" +
                "AND timeobs >= NOW() - INTERVAL '1 DAY';"
        },
        {
            title: "NA 72h",
            tooltip : "All measurements from North Atlantic the last 72 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations )\n" +
                "AND timeobs >= NOW() - INTERVAL '3 DAYS';"
        },
        {
            title: "Greenland 24h",
            tooltip : "All measurements from Greenland the last 24 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations \n" +
                "\tWHERE country = 'Grønland' )\n" +
                "AND timeobs >= NOW() - INTERVAL '1 DAY';"
        },
        {
            title: "Greenland 72h",
            tooltip : "All measurements from Greenland the last 72 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations \n" +
                "\tWHERE country = 'Grønland' )\n" +
                "AND timeobs >= NOW() - INTERVAL '3 DAYS';"
        },
        {
            title: "N. Greenland 24h",
            tooltip : "All measurements from northern Greenland (N, NE, NW) the last 24 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations \n" +
                "\tWHERE country = 'Grønland'\n" +
                "\tAND zone in ('N', 'NE', 'NW') )\n" +
                "AND timeobs >= NOW() - INTERVAL '1 DAY';"
        },
        {
            title: "N. Greenland 72h",
            tooltip : "All measurements from northern Greenland (N, NE, NW) the last 72 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations \n" +
                "\tWHERE country = 'Grønland'\n" +
                "\tAND zone in ('N', 'NE', 'NW') )\n" +
                "AND timeobs >= NOW() - INTERVAL '3 DAYS';"
        },
        {
            title: "S. Greenland 24h",
            tooltip : "All measurements from southern Greenland (S, SE, SW) the last 24 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations \n" +
                "\tWHERE country = 'Grønland'\n" +
                "\tAND zone in ('S', 'SE', 'SW') )\n" +
                "AND timeobs >= NOW() - INTERVAL '1 DAY';"
        },
        {
            title: "S. Greenland 72h",
            tooltip : "All measurements from southern Greenland (S, SE, SW) the last 72 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations\n" +
                "\tWHERE country = 'Grønland'\n" +
                "\tAND zone in ('S', 'SE', 'SW') )\n" +
                "AND timeobs >= NOW() - INTERVAL '3 DAYS';"
        },
        {
            title: "Faroe Islands 24h",
            tooltip : "All measurements from Faroe Islands the last 24 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations\n" +
                "\tWHERE country = 'Færøerne' )\n" +
                "AND timeobs >= NOW() - INTERVAL '1 DAY';"
        },
        {
            title: "Faroe Islands 72h",
            tooltip : "All measurements from Faroe Islands the last 72 hours",
            query: "select * from view_basis_hourly_na_timeobs where statid in (\n" +
                "\tSELECT statid FROM statcat.view_qc_na_stations\n" +
                "\tWHERE country = 'Færøerne' )\n" +
                "AND timeobs >= NOW() - INTERVAL '3 DAYS';"
        },
        {
            title: "2020-08-08",
            tooltip : "All measurements from 42200 on 2020-08-08",
            query: "select * from view_basis_hourly_na_timeobs where statid=422000 and timeobs between '2020-08-08 00:00z' and '2020-08-08 23:00z';"
        },
        {
            title: "2020-08",
            tooltip : "All measurements from 42200 in August 2020",
            query: "select * from view_basis_hourly_na_timeobs where statid=422000 and timeobs between '2020-08-01 00:00:00+00' and '2020-08-31 23:00:00+00';"
        },
    ];

}
