define([ 'dojo/_base/declare', 'dojo/_base/lang', 'dijit/registry', 'dijit/form/Button', 'dijit/form/ToggleButton', 'dgrid/OnDemandGrid', 'dgrid/Keyboard',
        'dgrid/Selection', 'dgrid/Selector', 'dstore/Request', 'dojo/string', 'dojo/aspect', 'dojo/dom-class', 'dojo/_base/array', 'Clipboard',
        'dijit/ToolbarSeparator', 'dijit/form/TextBox' ], //

function(declare, lang, registry, Button, ToggleButton, OnDemandGrid, Keyboard, Selection, Selector, Request, string, aspect, domClass, array, Clipboard,
        ToolbarSeparator, TextBox) {
    return declare('app.controller.MainController', null, {
        LIST_URL : 'file/list',
        HIDE_URL : 'file/hide',
        SHOW_URL : 'file/show',
        DOWNLOAD_URL : 'file/download/',
        KEY_UP_TIMEOUT : 300,

        properties : null,
        xhrManager : null,
        messagesManager : null,

        container : null,

        toolbar : null,
        refreshButton : null,
        hideButton : null,
        showButton : null,
        clipboardButton : null,
        clipboard : null,
        toggleShowHiddenButton : null,
        searchTextBox : null,

        grid : null,
        collection : null,

        constructor : function(args) {
            lang.mixin(this, args);
        },
        init : function() {
            this.container = registry.byId('container');

            this.initToolbar();
            this.initGrid();

            this.container.layout();
        },
        initToolbar : function() {
            this.toolbar = registry.byId('toolbar');

            this.refreshButton = new Button({
                iconClass : 'toolbarIcon refreshButtonIcon',
                label : this.messagesManager.get('main.toolbar.refresh'),
                onClick : lang.hitch(this, this.onRefreshButtonClick)
            });
            this.refreshButton.startup();
            this.toolbar.addChild(this.refreshButton);

            this.toolbar.addChild(new ToolbarSeparator());

            this.hideButton = new Button({
                iconClass : 'toolbarIcon hideButtonIcon',
                label : this.messagesManager.get('main.toolbar.hide'),
                onClick : lang.hitch(this, this.onHideButtonClick),
                disabled : true
            });
            this.hideButton.startup();
            this.toolbar.addChild(this.hideButton);

            this.showButton = new Button({
                iconClass : 'toolbarIcon showButtonIcon',
                label : this.messagesManager.get('main.toolbar.show'),
                onClick : lang.hitch(this, this.onShowButtonClick),
                disabled : true
            });
            this.showButton.startup();
            this.toolbar.addChild(this.showButton);

            this.toolbar.addChild(new ToolbarSeparator());

            this.clipboardButton = new Button({
                iconClass : 'toolbarIcon clipboardButtonIcon',
                label : this.messagesManager.get('main.toolbar.clipboard'),
                disabled : true
            });
            this.clipboardButton.startup();
            this.toolbar.addChild(this.clipboardButton);

            this.clipboard = new Clipboard(this.clipboardButton.domNode, {
                text : lang.hitch(this, this.onClipboardButtonClick)
            });

            this.searchTextBox = new TextBox({
                'class': 'toolbarRight',
                placeHolder : this.messagesManager.get('main.toolbar.search'),
                onKeyUp : lang.hitch(this, this.onSearchTextBoxKeyUp),
                style : 'margin: 4px'
            });
            this.toolbar.addChild(this.searchTextBox);

            this.toolbar.addChild(new ToolbarSeparator({
                'class' : 'toolbarRight',
                'style' : 'margin-top: 6px'
            }));

            this.toggleShowHiddenButton = new ToggleButton({
                iconClass : 'toolbarIcon toggleShowHiddenButtonOffIcon',
                'class' : 'toolbarRight',
                label : this.messagesManager.get('main.toolbar.toggleShowHidden'),
                checked : false,
                onChange : lang.hitch(this, this.onToggleShowHiddenButtonChange)
            });
            this.toggleShowHiddenButton.startup();
            this.toolbar.addChild(this.toggleShowHiddenButton);
        },
        initGrid : function() {
            this.collection = new Request({
                target : this.LIST_URL,
                sortParam : 'sort',
                rangeStartParam : 'start',
                rangeCountParam : 'count',
                ascendingPrefix : 'ASC-',
                descendingPrefix : 'DESC-'
            });

            var CustomGrid = declare([ OnDemandGrid, Keyboard, Selection, Selector ]);

            this.grid = new CustomGrid({
                collection : this.getFilteredCollection(),
                columns : [ {
                    field : 'selector',
                    selector : 'checkbox'
                }, {
                    field : 'download',
                    label : '',
                    get : function(item) {
                        return item;
                    },
                    formatter : lang.hitch(this, function(item) {
                        return string.substitute('<a href="${url}"><i class="fa fa-download" aria-hidden="true"></i></a>', {
                            url : this.createDownloadUrl(item)
                        });
                    }),
                    sortable : false
                }, {
                    field : 'id',
                    label : this.messagesManager.get('main.grid.id')
                }, {
                    field : 'path',
                    label : this.messagesManager.get('main.grid.path')
                } ],
                loadingMessage : '<div style="padding: 5px;"><i class="fa fa-spinner fa-spin fa-lg"></i></div>',
                noDataMessage : this.messagesManager.get('main.grid.noData'),
                allowSelectAll : true,
                sort : 'path'
            }, 'grid');

            aspect.after(this.grid, "renderRow", function(row, args) {
                if (args[0].hidden === true) {
                    domClass.add(row, 'hiddenRow');
                }
                return row;
            });

            this.grid.on('dgrid-select,dgrid-deselect', lang.hitch(this, this.onGridSelectDeselect));
        },
        onGridSelectDeselect : function() {
            var flag = this.getGridSelection().length === 0;

            this.hideButton.set('disabled', flag);
            this.showButton.set('disabled', flag);
            this.clipboardButton.set('disabled', flag);
        },
        onRefreshButtonClick : function() {
            this.grid.refresh();
        },
        onHideButtonClick : function() {
            this.xhrManager.post(this.HIDE_URL, {
                data : this.getSelectedIds()
            }).then(lang.hitch(this, function() {
                this.grid.refresh();
            }));
        },
        onShowButtonClick : function() {
            this.xhrManager.post(this.SHOW_URL, {
                data : this.getSelectedIds()
            }).then(lang.hitch(this, function() {
                this.grid.refresh();
            }));
        },
        onClipboardButtonClick : function() {
            var text = '';
            array.forEach(this.getGridSelection(), function(item) {
                text += this.createDownloadUrl(item) + '\n';
            }, this);
            return text;
        },
        onToggleShowHiddenButtonChange : function(checked) {
            var iconClass = checked ? 'toggleShowHiddenButtonOnIcon' : 'toggleShowHiddenButtonOffIcon';
            this.toggleShowHiddenButton.set('iconClass', 'toolbarIcon ' + iconClass);
            this.grid.set('collection', this.getFilteredCollection());
        },
        onSearchTextBoxKeyUp : function() {
            clearTimeout(this.kyeUpHandle);
            this.kyeUpHandle = setTimeout(lang.hitch(this, function() {
                this.grid.set('collection', this.getFilteredCollection());
            }), this.KEY_UP_TIMEOUT);
        },
        getFilteredCollection : function() {
            return this.collection.filter({
                showHidden : this.toggleShowHiddenButton.get('checked'),
                search : this.searchTextBox.get('value')
            });
        },
        getGridSelection : function() {
            var selection = [];
            for ( var rowId in this.grid.selection) {
                if (this.grid.selection[rowId] === true) {
                    selection.push(this.grid.row(rowId).data);
                }
            }
            return selection;
        },
        getSelectedIds : function() {
            var ids = [];
            array.forEach(this.getGridSelection(), function(item) {
                ids.push(item.id);
            });
            return ids;
        },
        createDownloadUrl : function(item) {
            return string.substitute('${base}${download}${id}?${path}', {
                base : this.properties.url,
                download : this.DOWNLOAD_URL,
                id : item.id,
                path : item.path
            });
        }
    });
});
