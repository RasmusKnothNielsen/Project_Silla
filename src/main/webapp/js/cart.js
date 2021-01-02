class Cart {
    constructor(config) {
        this.config = config;

        this.initTable();
        this.openDatabase();
        this.initButtons();
    }

    initButtons() {
        // Prepare 'Upload CSV' button
        document.getElementById('fileinput').addEventListener('change', this.import_csv.bind(this));

        $('#cart_table_id tbody').on('click', 'tr', function () {
            $(this).toggleClass('selected');
        });

        $('#cart_remove_row_button').click(this.removeRowsFromCart.bind(this));

        $('#cart_empty_button').click(this.clearCart.bind(this));

        $('#cart_tab').on('shown.bs.tab', this.adjust.bind(this));

        $("#cart_exclude_button").click(this.updateLabelsExclude.bind(this));

        $("#cart_qc_button").click(this.updateLabelsQC.bind(this));

        $("#export_csv_button").click(this.export_csv.bind(this, "silla_" + (new Date()).toUTCString() + ".csv"));

        $("#import_csv_button").click(this.click_import_csv.bind(this));
    }

    initTable() {
        this.cartDatatable = $('#cart_table_id').DataTable({
            'ordering': false,
            'scrollY': '600px',
            'scrollCollapse': true,
            'paging': false,
            'searching': false,
            columnDefs: [
                {
                    targets: [0, 1, 2, 3, 4],
                    className: 'dt-center selectable',
                },
            ],
            columns: [
                {
                    title: 'Statid',
                    defaultContent: '',
                    data: null,
                    render: 'statid',
                }, {
                    title: 'Timeobs',
                    defaultContent: '',
                    data: null,
                    render: 'timeobs',
                }, {
                    title: 'Element',
                    defaultContent: '',
                    data: null,
                    render: 'elem_no',
                }, {
                    title: 'Value',
                    defaultContent: '',
                    data: null,
                    render: 'elem_val',
                }, {
                    title: 'Label',
                    defaultContent: '',
                    data: null,
                    render: 'label',
                },
            ],
            select: {
                targets: "_all",
                style: 'os',
                items: 'row',
            },
        });
    }

    openDatabase() {
        const request = window.indexedDB.open('silla', 3);

        request.onerror = event => {
            console.log("IndexedDB error:", event.target);
            console.log('Database failed to open');
        };

        request.onsuccess = event => {
            console.log('Database opened successfully');
            this.db = event.target.result;
            this.db.onerror = event => {
                if (this.config.DEBUG && event.target.error.code !== 0) {
                    console.log("DB error:", event.target);
                }
            };
            this.refreshCart();
        };

        // Create/update database
        request.onupgradeneeded = event => {
            const db = event.target.result;

            if (event.oldVersion < 1) {
                db.createObjectStore('cart');
            }
            if (event.oldVersion < 3) {
                db.deleteObjectStore('cart');
                db.createObjectStore('cart', {keyPath: ['statid', 'timeobs', 'elem_no']});
            }
            console.log('Database setup complete');
        };
    }

    removeRowsFromCart() {
        const rows = this.cartDatatable.rows('.selected').data();
        for (let rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            this.removeCartItem(rows[rowIndex]);
        }
    }

    // Update shopping cart
    async refreshCart() {
        let cartItems = await this.getCompleteCart();
        this.cartDatatable.clear().rows.add(cartItems).draw();
        document.getElementById('cart-size').innerText = cartItems.length;
    }

    // Add item to database and update shopping cart
    addCartItem(item) {
        // Insert new item in database and update shopping cart
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction(['cart'], 'readwrite');
            const objectStore = transaction.objectStore('cart');
            let request;
            try {
                request = objectStore.add(item);
            } catch (err) {
                if (this.config.DEBUG) {
                    console.log(err)
                    console.log("Error adding this measurement to IndexedDB: ", item)
                }
                reject("Malformed item");
            }
            let itemInserted = false;

            request.onsuccess = function () {
                itemInserted = true;
            };

            transaction.oncomplete = function () {
                if (itemInserted) resolve();
                else reject("Item not added");
            };

            transaction.onerror = function (e) {
                if (e.target.error.code === 0) resolve(); // item is already in cart
                else reject("Transaction error");
            };
        });
    }

    // Remove item from database and update shopping cart
    removeCartItem(item) {
        const that = this;

        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction(['cart'], 'readwrite');
            const objectStore = transaction.objectStore('cart');
            const key = [item.statid, item.timeobs, item.elem_no];
            const request = objectStore.delete(key);
            let itemDeleted = false;

            request.onsuccess = function () {
                itemDeleted = true;
                that.refreshCart();
            };

            transaction.oncomplete = function (event) {
                if (itemDeleted) resolve();
                else reject("Item not removed");
            };

            transaction.onerror = function () {
                reject("Transaction error");
            };
        });
    }

    // Remove all items from database and update shopping cart
    clearCart() {
        return new Promise((resolve, reject) => {
            const transaction = this.db.transaction(['cart'], 'readwrite');
            const objectStore = transaction.objectStore('cart');
            objectStore.clear();

            transaction.oncomplete = event => {
                this.refreshCart();
                resolve();
            };
        });
    }

    // Get array of all items in database
    getCompleteCart() {
        return new Promise((resolve, reject) => {
            let transaction = this.db.transaction(['cart']);
            let objectStore = transaction.objectStore('cart');
            let cart = [];
            objectStore.openCursor().onsuccess = function (e) {
                let cursor = e.target.result;
                if (cursor) {
                    cart.push(cursor.value);
                    cursor.continue();
                } else {
                    resolve(cart);
                }
            }
        });
    }

    async export_csv(filename) {
        let csv = '';
        const data = await this.getCompleteCart();

        data.forEach(function (row) {
            const time = row.timeobs.slice(8, 10) + row.timeobs.slice(4, 8) + row.timeobs.slice(0, 4) + row.timeobs.slice(10, 16);
            csv += row.statid + ";" + row.elem_no + ";" + time + ";" + row.elem_val + ";-9999.0;1;1\r\n";
        });

        const hiddenElement = document.createElement('a');
        hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(csv);
        hiddenElement.target = '_blank';
        if (!filename) filename = 'silla_' + (new Date()).toUTCString() + '.csv';
        hiddenElement.download = filename;
        hiddenElement.click();
    }

    import_csv(evt) {
        const file = evt.target.files[0];

        if (file) {
            spinner_on("Importing CSV");
            Papa.parse(file, {
                delimiter: ";",
                complete: async (results, file) => {
                    const data = results.data;
                    for (const row of data) {
                        if (row.length === 7) {
                            const item = {
                                statid: parseInt(row[0]),
                                elem_no: parseInt(row[1]),
                                timeobs: row[2].slice(6, 10) + row[2].slice(2, 6) + row[2].slice(0, 2) + row[2].slice(10, 16),
                                elem_val: row[3],
                                //unknown1: row[4],
                                //row: row[5],
                                //column: row[6],
                            };
                            await this.addCartItem(item);
                        }
                    }
                    await this.refreshCart();
                    spinner_off();
                },
            });
            document.getElementById('fileinput').value = '';
        } else {
            alert("Failed to load file");
        }
    }

    // Clicks the hidden file upload
    click_import_csv() {
        document.getElementById('fileinput').click();
    }

    adjust() {
        this.cartDatatable.tables().columns.adjust();
    }

    async updateLabelsExclude() {
        if (!Cookies.get('login')) {
            $.notify("Not authenticated", {autoHide: false,});
            return;
        }

        await this.export_csv("silla_exclude_" + (new Date()).toUTCString() + ".csv");
        let cartContents = await this.getCompleteCart();
        await this.updateLabels("api/update_labels/exclude", cartContents);
        for (const item of cartContents) {
            resultView.addExcludeLabel(item);
        }
        resultView.redraw();
    }

    async updateLabelsQC() {
        if (!Cookies.get('login')) {
            $.notify("Not authenticated", {autoHide: false,});
            return;
        }

        let cartContents = await this.getCompleteCart();
        await this.updateLabels("api/update_labels/qc", cartContents);
        for (const item of cartContents) {
            resultView.addQcLabel(item);
        }
        resultView.redraw();
    }

    async updateLabels(url, cartContents) {
        try {
            await $.ajax({
                type: "POST",
                url,
                contentType: 'application/json',
                data: JSON.stringify(cartContents),
            });
            this.clearCart();
            $.notify("Labels updated", {
                className: "success",
                autoHide: false,
            });

        } catch (err) {
            $.notify("Failed to update labels", {
                autoHide: false,
            });
        }
    }

}
