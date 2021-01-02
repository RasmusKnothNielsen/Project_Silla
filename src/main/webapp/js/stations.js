class Stations {

    constructor(config) {
        this.config = config;
        this.availableStations = [];
    }

    async fetch(url) {
        const response = await fetch(url);
        this.availableStations = await response.json();
    }

    async update() {
        await this.fetch(this.config.URL_STATIONS);
    }

    getStationIds() {
        const statIds = [];
        for(const station of this.availableStations) {
            statIds.push(station.statid);
        }
        return statIds;
    }

    getStationList() {
        return this.availableStations;
    }

}