class DateHourPicker {

    constructor(dateId, hourId) {
        this.datePicker = $('#'+dateId).datepicker({
            format: 'yyyy-mm-dd',
            autoclose: true
        });
        this.hourSelector =$('#'+hourId);
    }

    onChange(cb) {
        this.datePicker.on('changeDate', cb);
        this.hourSelector.on('change', cb);
    }

    static roundDown(epoch) {
        return epoch - (epoch % MS_PER_HOUR);
    }

    static roundUp(epoch) {
        const roundTime = epoch - (epoch % MS_PER_HOUR);
        if (roundTime === epoch) return roundTime;
        else return roundTime + MS_PER_HOUR;
    }

    setEpoch(time) {
        const date = new Date(time);
        this.hourSelector[0].options.selectedIndex = date.getUTCHours();

        date.setUTCHours(0, 0, 0, 0);
        const utcDate = new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate());
        this.datePicker.datepicker("setDate", utcDate);
    }

    getEpoch() {
        let date = (new Date(this.datePicker.datepicker("getDate").toString().slice(0,24)+'Z')).valueOf();
        const hour = this.hourSelector[0].options.selectedIndex;
        if (Number.isNaN(date)) date = 0
        return date + hour * MS_PER_HOUR;
    }

    getTimeString() {
        const timeString = new Date(this.getEpoch()).toISOString();
        return timeString.replace("T", " ").slice(0, -5) + "z";
    }
}