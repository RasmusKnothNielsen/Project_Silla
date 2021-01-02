let searchView;
let resultView;
let cartView;
let spinner_timer;
const SPINNER_DELAY = 100;

$(document).ready(() => {
    const config = new Config();
    config.init().then(() => {
        initToolTips(config.presetQueries);
        cartView = new Cart(config);
        resultView = new Result(config, cartView);
        searchView = new Search(config, resultView);
    });
});

function initToolTips(presetQueries) {
    const instantToolTip = {trigger: "hover", delay: {hide: 100}};
    $("#standard-search-tooltip").tooltip({title: "Choose the date interval you would like to search for", ...instantToolTip});
    $("#preset-search-tooltip").tooltip({title: "Choose a preset to start searching for it", ...instantToolTip});
    $("#sql-search-tooltip").tooltip({title: "Write your own customized SQL query. <br>" +
            "If you change the syntax, it may not be supported by Silla", ...instantToolTip});
    $("#result_table_tooltip").tooltip({title: "Hold CTRL to select more than one cell at a time. <br>" +
            "Hold Shift to select a range of values", ...instantToolTip});
    $("#cart-tooltip").tooltip({title: "Hold CTRL to select more than one row at a time. <br>" +
            "Hold Shift to select a range of rows. ", ...instantToolTip});

    const toolTipProperties = {trigger: "hover", delay: {show: 1250, hide: 200}};
    const toolTipList = [
        {id: "#search_tab", tip: "Search"},
        {id: "#standard-search-tab", tip: "Standard search"},
        {id: "#submitbtn", tip: "Search"},
        {id: "#preset-search-tab", tip: "Preset search"},
        {id: "#sql-search-tab", tip: "SQL search"},
        {id: "#submitsqlbtn", tip: "Search"},
        {id: "#result_tab", tip: "Results"},
        {id: "#result-select-all", tip: "Select all"},
        {id: "#result-select-none", tip: "Select none"},
        {id: "#send-to-cart", tip: "Send selected data to cart"},
        {id: "#cart_tab", tip: "Shopping cart"},
        {id: "#cart_remove_row_button", tip: "Remove selected row(s)"},
        {id: "#cart_empty_button", tip: "Empty shopping cart"},
        {id: "#export_csv_button", tip: "Export CSV"},
        {id: "#import_csv_button", tip: "Import CSV"},
        {id: "#cart_exclude_button", tip: "Mark content of cart as excluded"},
        {id: "#cart_qc_button", tip: "Mark content of cart as quality controlled"},
    ];

    // Apply tooltips
    for (const t of toolTipList) {
        $(t.id).tooltip({title: t.tip, ...toolTipProperties});
    }

    // Make tooltips for preset searches
    for (let i = 0; i < presetQueries.length; i++) {
        $("#preset-btn-" + i).tooltip({title: presetQueries[i].tooltip, ...toolTipProperties});
    }
}

function spinner_on(text, delay) {
    document.getElementById("spinner-text").innerText = text ? text : "";

    const display_spinner = () => {
        document.getElementById("spinner-overlay").style.display = "flex";
    };
    spinner_timer = setTimeout(display_spinner, delay ? delay : SPINNER_DELAY);
}

function spinner_off() {
    if (spinner_timer) {
        clearTimeout(spinner_timer);
        spinner_timer = null;
    }
    document.getElementById("spinner-overlay").style.display = "none";
}
