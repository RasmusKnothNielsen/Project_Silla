class Result {

    constructor(config, cartInstance) {
        this.config = config;

        if (cartInstance) this.cart = cartInstance;
        else this.cart = new Cart();

        this.synopList = [];
        this.pointClick = null;

        this.initTable(this.config.getAvailableParameterList());
        this.initButtons();

        // Adjust size of table when tab is shown
        $('#result_tab').on('shown.bs.tab', this.adjust.bind(this));
    }

    initTable(parameterList) {
        const columns = this.getColumnDefinition(parameterList);
        this.resultDataTable = $('#result_table_id').DataTable({
            'ordering': false,
            'scrollY': '600px',
            'scrollX': true,
            'sScrollX': "100%",
            'sScrollXInner': "100%",
            'scrollCollapse': true,
            'paging': false,
            'searching': false,
            rowCallback: (tr, data) => {
                // Function that handles the coloring of each cell
                let i = 2;
                for (let elemNo of this.config.getEnabledParameterNumbers()) {
                    elemNo = elemNo.toString();
                    if (data[elemNo] && data[elemNo].label !== undefined) {
                        const label = parseInt(data[elemNo].label);
                        const element = $(tr).find('td:eq(' + i + ')');

                        // If Quality controlled
                        if (Math.floor(label / 1000) % 10 === 2) {
                            element.css('background-color', '#109c00').addClass("qc");
                        }

                        // If Excluded
                        if (Math.floor(label / 100000000) % 10 === 3) {
                            element.css('background-color', '#bf0a16').addClass("excluded");
                        }

                        element.tooltip({
                            title: label,
                            trigger: 'hover',
                            delay: {show: 1000, hide: 100}
                        });
                    }
                    i++;
                }
            },
            columns,
            columnDefs: [
                {
                    targets: [0, 1],
                    className: "dt-body-center non-selectable"
                }, {
                    targets: 'selectable',
                    className: 'dt-body-right selectable',
                }, {
                    targets: '_all',
                    className: 'dt-head-center',
                }
            ],
            select: {
                selector: ".selectable",
                style: 'os',
                items: 'cell',
            },
            dom: 'Bfrtip',
            buttons: {
                dom: {
                    button: {
                        tag: 'button',
                        className: ''
                    },
                },
                buttons: [{
                    extend: 'selectAll',
                    className: 'btn btn-info',
                    text: 'Select all'
                }, {
                    extend: 'selectNone',
                    className: 'btn btn-info',
                    text: 'Select none'
                }]
            }
        });

        // Tooltips for parameters
        const toolTipProperties = {trigger: "hover", delay: {show: 500, hide: 200}};
        for (const parameter of this.config.getEnabledParameterList()) {
            const parameterElement = $('#param-title-' + parameter.elem_no);
            if (parameterElement) {
                const tooltip = parameter.description;
                parameterElement.tooltip({title: tooltip, ...toolTipProperties});
            }
        }
    }

    initButtons() {
        $('#send-to-cart').click(this.sendToCart.bind(this));
        $('#result-select-all').click(() => {
            $('.buttons-select-all')[0].click();
        });
        $('#result-select-none').click(() => {
            $('.buttons-select-none')[0].click();
        });
    }

    getColumnDefinition(parameterList) {
        const columns = [
            {title: "Station", data: "statid",},
            {
                title: "Time (UTC)",
                data: "timeobs",
                render: function (data) {
                    return '<div class="forceWidth">' + data + '</div>';
                }
            }
        ];
        for (const parameter of parameterList) {
            const item = {
                title: "<span id = 'param-title-" + parameter.elem_no + "'>" + parameter.elem_no + "</span>",
                className: "selectable",
                defaultContent: '',
                data: null,
                render: parameter.elem_no + ".elem_val",
            }
            columns.push(item);
        }
        return columns;
    }

    adjust() {
        this.resultDataTable.tables().columns.adjust();
    }

    update(data) {
        this.synopList = data;
        this.resultDataTable.destroy();
        $('#result_table_id').empty();
        this.initTable(this.config.getEnabledParameterList());
        this.resultDataTable.clear().rows.add(data).draw();
        if (this.pointClick) this.markPointClick();
    }

    redraw() {
        this.resultDataTable.clear().rows.add(this.synopList).draw();
    }

    addQcLabel(item) {
        for (const synop of this.synopList) {
            if (synop.timeobs === item.timeobs && synop.statid === item.statid) {
                const measurement = synop[item.elem_no];
                if (measurement) {
                    const label = measurement.label;
                    measurement.label = label - (label % 10000) + 2000 + (label % 1000);
                }
                break;
            }
        }
    }

    addExcludeLabel(item) {
        for (const synop of this.synopList) {
            if (synop.timeobs === item.timeobs && synop.statid === item.statid) {
                const measurement = synop[item.elem_no];
                if (measurement) {
                    let label = measurement.label;
                    // Set exclude flag
                    label = label - (label % 1000000000) + 300000000 + (label % 100000000);
                    // Set quality control flag
                    measurement.label = label - (label % 10000) + 2000 + (label % 1000);
                }
                break;
            }
        }
    }

    addPointClick(pointClick) {
        this.pointClick = pointClick;
    }

    markPointClick() {
        if (this.pointClick === null) return;
        let row = 0;
        for (const synop of this.synopList) {
            if (synop.timeobs === this.pointClick.timeobs && synop.statid === this.pointClick.statid) {
                const measurement = synop[this.pointClick.elemNo];
                if (measurement) {
                    const node = this.resultDataTable.cell(row, this.config.getEnabledParameterNumbers().indexOf(this.pointClick.elemNo) + 2).node();
                    node.classList.add("marked");
                    node.click();
                }
                break;
            }
            row++;
        }
        this.removePointClick();
    }

    removePointClick() {
        this.pointClick = null;
    }

    sendToCart() {
        spinner_on("Adding to cart");

        const selectedCells = this.resultDataTable.cells({selected: true});
        let counter = 0;
        for (const cell of selectedCells[0]) {
            const row = cell.row;
            const column = cell.column - 2;
            if (column >= 0) {
                const elem_no = this.config.getEnabledParameterList()[column].elem_no;
                const synop = this.synopList[row];
                if (synop[elem_no]) {
                    this.cart.addCartItem(synop[elem_no]);
                    counter++;
                }
            }
        }
        this.cart.refreshCart()
            .then(() => {
                spinner_off();
                if (counter > 1) {
                    $.notify("" + counter + " values added to cart.", {
                        className: "success",
                        autoHide: false,
                    });
                } else if (counter > 0) {
                    $.notify("One value added to cart.", {
                        className: "success",
                        autoHide: false,
                    });
                }
            });
    }

}
