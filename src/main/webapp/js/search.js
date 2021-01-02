class Search {

    constructor(config, resultInstance) {
        this.config = config;
        // TODO: Temporary list for authentication until a proper user list is in place
        this.users = ['nikhan@dmi.dk', 'cdj@dmi.dk', 'rod@dmi.dk', 'jc@dmi.dk', 'cdj', 'jc', 'rod'];

        if (resultInstance) this.resultTable = resultInstance;
        else this.resultTable = new Result();

        const queryParams = this.getQueryParameters();

        this.initDatePickers(queryParams.firstDate, queryParams.lastDate);

        this.addEmptySynops = queryParams.emptySynops;

        this.initButtons();

        this.populateStationDropdown(config.getAvailableStationNumbers());
        $('#station_dropdown').multiselect('select', queryParams.statids)

        if (queryParams.firstDate) {
            if (queryParams.pointClick.timeobs) this.resultTable.addPointClick(queryParams.pointClick);
            this.clickSubmitButton();
        }

        // TODO: Change and use this when Grafana is updated to version 7.3
        // Fetch a list of users from Grafana
        // fetch("api/users")
        //     .then(response => response.json())
        //     .then(data => {
        //         console.log("data", data);
        //         this.users = data;
        //     })
        //     .catch(() => {
        //         console.log('Failed to get users from Grafana')
        //     })
    }

    getQueryParameters() {
        const urlParams = new URLSearchParams(window.location.search);
        const queryParams = {};

        queryParams.firstDate = parseInt(urlParams.get("from"));
        queryParams.lastDate = parseInt(urlParams.get("to"));
        queryParams.login = urlParams.get("login");
        this.users.forEach(item => {
            if (queryParams.login === item) {
                Cookies.set('login', queryParams.login, {expires: 365})
            }
        })

        queryParams.statids = [];

        // get all statid's from the url
        urlParams.getAll('var-statid').forEach((item) => {
            const splitStatId = item.split(" ");
            const statId = parseInt(splitStatId[0]);
            queryParams.statids.push(statId);
        })

        // Get a single statid and time from the url
        const pointClick = {
            statid: urlParams.get("statid"),
            timeobs: urlParams.get("time"),
        }
        if (pointClick.statid && pointClick.timeobs) {
            const splitStatId = pointClick.statid.split(" ");
            pointClick.elemNo = parseInt(splitStatId[1].slice(1, 4));
            pointClick.statid = parseInt(splitStatId[0]);
            pointClick.timeobs = pointClick.timeobs.slice(0, 16);
            queryParams.statids.push(pointClick.statid);
        }
        queryParams.pointClick = pointClick;

        const allowEmptySynops = urlParams.get("var-allowemptysynops");
        queryParams.emptySynops = allowEmptySynops !== undefined && allowEmptySynops === "true";

        return queryParams;
    }

    initDatePickers(firstDate, lastDate) {
        this.startPicker = new DateHourPicker("dp1", "from_hour")
        this.endPicker = new DateHourPicker("dp2", "to_hour")

        this.startPicker.onChange(this.startDateChanged.bind(this));
        this.endPicker.onChange(this.endDateChanged.bind(this));

        if (firstDate) this.startPicker.setEpoch(DateHourPicker.roundUp(firstDate));
        if (lastDate) this.endPicker.setEpoch(DateHourPicker.roundDown(lastDate));

        // Set datepickers to get the last 24 hours
        if (!firstDate && !lastDate) {
            const now = Date.now();
            this.endPicker.setEpoch(now);

            let from = now - MS_PER_HOUR * 24;
            // If exactly on top of the hour there could be 25 hours
            if (from % MS_PER_HOUR === 0) from++;
            this.startPicker.setEpoch(from);
        }
    }

    initButtons() {
        $('#btn-login').click(this.clickLoginButton.bind(this));
        $("#submitbtn").click(this.clickSubmitButton.bind(this));
        $("#submitsqlbtn").click(this.clickSubmitSQLButton.bind(this));

        $('#add-empty-synops-span').on('click', this.clickEmptySynops.bind(this));

        $("#sql-search-tab").on('show.bs.tab', () => this.updateSQLTextArea());

        for (let i = 0; i < this.config.presetQueries.length; i++) {
            const button = $("#preset-btn-" + i);
            button[0].innerText = this.config.presetQueries[i].title;
            button.click(this.clickPresetButton.bind(this, i));
        }

        $('#station_dropdown').multiselect({
            includeSelectAllOption: true,
            nonSelectedText: 'Select stations',
            allSelectedText: 'All stations',
            maxHeight: 300
        });
    }

    clickEmptySynops() {
        this.addEmptySynops = !this.addEmptySynops;
        $('#add-empty-synops').prop('checked', this.addEmptySynops)
    }


    populateStationDropdown(statIds) {
        statIds.forEach(statId => {
            $("#station_dropdown").append(
                '<option value="' + statId + '">' + statId + '</option>'
            );
        });
        $("#station_dropdown").multiselect('rebuild');
    }

    // Set Login cookie on login
    clickLoginButton(event) {
        event.preventDefault();
        Cookies.remove('login');
        const loginInput = $('#login-input').val();
        Cookies.set('login', loginInput, {expires: 365})
    }

    clickPresetButton(buttonNumber) {
        const query = this.config.presetQueries[buttonNumber].query;

        // Update free-text SQL area
        $("#free_sql_textarea").val(query);

        this.executeFreeSQL(query);
    }

    clickSubmitButton() {
        this.updateSQLTextArea();
        this.clickSubmitSQLButton();
    }

    startDateChanged(ev) {
        const changedTime = this.startPicker.getEpoch();
        if (changedTime > this.endPicker.getEpoch()) this.endPicker.setEpoch(changedTime);
    }

    endDateChanged() {
        const changedTime = this.endPicker.getEpoch();
        if (changedTime < this.startPicker.getEpoch()) this.startPicker.setEpoch(changedTime);
    }

    createSqlSelector(list) {
        if (list === "all") {
            return null;
        } else if (list.length === 0) {
            return 0;
        } else if (list.length === 1) {
            return list[0];
        } else {
            let selector = "ANY ('{" + list[0];
            for (let i = 1; i < list.length; i++) {
                selector += ", " + list[i];
            }
            selector += "}')";
            return selector;
        }
    }

    updateSQLTextArea() {
        // Get selected stations from dropdown
        const selectedStations = $('#station_dropdown option:selected')
            .map(function (a, item) {
                return item.value;
            });

        let sqlCreator;
        if (this.addEmptySynops) {
            sqlCreator = this.queryWithHoles.bind(this);
        }
        else {
            sqlCreator = this.queryIgnoringHoles.bind(this);
        }

        const sql = sqlCreator(
            selectedStations,
            this.createSqlSelector(this.config.getEnabledParameterNumbers()),
            this.startPicker.getTimeString(),
            this.endPicker.getTimeString());
        $("#free_sql_textarea").val(sql);
    }

    queryIgnoringHoles(stations, parametersSQLString, firstTimeobs, lastTimeobs) {
        const stationsSQLString = this.createSqlSelector(stations);
        let sql = "SELECT * FROM view_basis_hourly_na_timeobs\n";
        sql += "WHERE statid = " + stationsSQLString + "\n";
        if (parametersSQLString !== null) {
            sql += "AND elem_no = " + parametersSQLString + "\n";
        }
        sql += "AND timeobs BETWEEN '" + firstTimeobs + "' AND '" + lastTimeobs + "';";
        return sql;
    }

    queryWithHoles(stations, parametersSQLString, firstTimeobs, lastTimeobs) {
        const stationsSQLString = this.createSqlSelector(stations);
        let sql = "SELECT * FROM (SELECT * FROM\n";
        sql += "(GENERATE_SERIES('" + firstTimeobs + "', '" + lastTimeobs + "', '1 hour'::INTERVAL) as timeobs\n";
        sql +="CROSS JOIN\n";
        sql += "(SELECT UNNEST('{ ";
        for(const statid of stations) sql += statid+",";
        sql = sql.slice(0,sql.length-1);
        sql += "}'::integer[]) AS statid) AS stations_table) ) as timestat \n";
        sql += "LEFT JOIN (SELECT * FROM view_basis_hourly_na_timeobs\n";
        sql += "WHERE statid = " + stationsSQLString + "\n";
        if (parametersSQLString !== null) {
            sql += "AND elem_no = " + parametersSQLString + "\n";
        }
        sql += "AND timeobs BETWEEN '" + firstTimeobs + "' AND '" + lastTimeobs + "') AS measurement\n";
        sql += "USING (timeobs, statid) ORDER BY timeobs;"

        return sql;
    }

    clickSubmitSQLButton() {
        const query = $("#free_sql_textarea").val();
        this.executeFreeSQL(query);
    }

    executeFreeSQL(query) {
        let startTiming, endTiming, elapsedTime;
        spinner_on("Searching");
        if (this.config.TIMING_MODE) startTiming = performance.now();
        $.ajax({
            type: "POST",
            url: "api/sql",
            data: query,
            contentType: "text/plain",
        }).done(data => {
            if (this.config.TIMING_MODE) {
                this.resultTable.update(data);
                endTiming = performance.now();
                elapsedTime = (endTiming - startTiming).toFixed(0);
                console.log("Total Ajax request + backend took: " + elapsedTime + "ms.");
            } else {
                this.resultTable.update(data);
            }
            spinner_off();
            $('#result_tab').click();
        }).fail(() => {
            spinner_off();
            $.notify("Failed to get search result", {
                className: "error",
                autoHide: false,
            });
        });
    }

}
