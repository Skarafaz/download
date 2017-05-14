define([ 'dojo/_base/declare', 'dojo/_base/lang', 'dijit/registry', 'dijit/form/Button', 'dijit/form/ToggleButton', 'dgrid/OnDemandGrid', 'dgrid/Keyboard',
        'dgrid/Selection', 'dgrid/Selector', 'dstore/Request', 'dojo/string', 'dojo/aspect', 'dojo/dom-class', 'dojo/_base/array', 'Clipboard' ], //

function(declare, lang, registry, Button, ToggleButton, OnDemandGrid, Keyboard, Selection, Selector, Request, string, aspect, domClass, array, Clipboard) {
    return declare('app.controller.MainController', null, {
        LIST_URL : 'file/list',
        DOWNLOAD_URL : 'file/download/',
        xhrManager : null,
        messagesManager : null,
        properties : null,
        container : null,
        toolbar : null,
        refreshButton : null,
        clipboardButton : null,
        clipboard : null,
        toggleShowHiddenButton : null,
        store : null,
        grid : null,

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

            this.toggleShowHiddenButton = new ToggleButton({
                iconClass : 'toolbarIcon toggleShowHiddenButtonIcon',
                label : this.messagesManager.get('main.toolbar.toggleShowHidden'),
                checked : false,
                onChange : lang.hitch(this, this.onToggleShowHiddenButtonChange)
            });
            this.toggleShowHiddenButton.startup();
            this.toolbar.addChild(this.toggleShowHiddenButton);
        },
        initGrid : function() {
            var CustomGrid = declare([ OnDemandGrid, Keyboard, Selection, Selector ]);

            this.store = new Request({
                target : this.LIST_URL,
                sortParam : 'sort',
                rangeStartParam : 'start',
                rangeCountParam : 'count',
                ascendingPrefix : 'ASC-',
                descendingPrefix : 'DESC-'
            });

            this.grid = new CustomGrid({
                collection : this.store,
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
                    })
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
            this.clipboardButton.set('disabled', this.getGridSelection().length === 0);
        },
        onRefreshButtonClick : function() {
            this.grid.refresh();
        },
        onClipboardButtonClick : function() {
            var text = '';
            array.forEach(this.getGridSelection(), function(item) {
                text += this.createDownloadUrl(item) + '\n';
            }, this);
            return text;
        },
        onToggleShowHiddenButtonChange : function(checked) {
            this.grid.set('collection', checked ? this.store.filter({
                showHidden : true
            }) : this.store);
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
