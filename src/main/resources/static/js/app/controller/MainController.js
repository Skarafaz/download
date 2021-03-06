define([ 'dojo/_base/declare', 'dojo/_base/lang', 'dijit/registry', 'dijit/form/Button', 'dijit/form/ToggleButton', 'dgrid/OnDemandGrid', 'dgrid/Keyboard',
        'dgrid/Selection', 'dgrid/Selector', 'dstore/Request', 'dojo/string', 'dojo/aspect', 'dojo/dom-class', 'dojo/_base/array', 'Clipboard',
        'dijit/ToolbarSeparator', 'dijit/form/TextBox', 'dojo/dom', 'dojo/dom-style', 'dojo/html' ], //

function(declare, lang, registry, Button, ToggleButton, OnDemandGrid, Keyboard, Selection, Selector, Request, string, aspect, domClass, array, Clipboard,
        ToolbarSeparator, TextBox, dom, domStyle, html) {
    return declare('app.controller.MainController', null, {
        LIST_URL : 'file/list',
        HIDE_URL : 'file/hide',
        SHOW_URL : 'file/show',
        ADD_URL : 'file/add',
        REMOVE_URL : 'file/remove',
        SHARE_URL : 'file/share',
        UNSHARE_URL : 'file/unshare',
        DOWNLOAD_URL : 'file/download',
        KEY_UP_TIMEOUT : 300,

        properties : null,
        xhrManager : null,
        messagesManager : null,

        container : null,

        toolbar : null,
        refreshButton : null,
        hideButton : null,
        showButton : null,
        addButton : null,
        removeButton : null,
        shareButton : null,
        unshareButton : null,
        clipboardButton : null,
        clipboard : null,
        toggleShowHiddenButton : null,
        searchTextBox : null,
        counter : null,

        grid : null,
        collection : null,

        constructor : function(args) {
            lang.mixin(this, args);
        },
        init : function() {
            this.container = registry.byId('container');

            this.initToolbar();
            this.initGrid();

            this.counter = dom.byId('counter');

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
                onClick : lang.hitch(this, this.onActionButtonClick, this.HIDE_URL),
                disabled : true
            });
            this.hideButton.startup();
            this.toolbar.addChild(this.hideButton);

            this.showButton = new Button({
                iconClass : 'toolbarIcon showButtonIcon',
                label : this.messagesManager.get('main.toolbar.show'),
                onClick : lang.hitch(this, this.onActionButtonClick, this.SHOW_URL),
                disabled : true
            });
            this.showButton.startup();
            this.toolbar.addChild(this.showButton);

            this.toolbar.addChild(new ToolbarSeparator());

            this.addButton = new Button({
                iconClass : 'toolbarIcon addButtonIcon',
                label : this.messagesManager.get('main.toolbar.add'),
                onClick : lang.hitch(this, this.onActionButtonClick, this.ADD_URL),
                disabled : true
            });
            this.showButton.startup();
            this.toolbar.addChild(this.addButton);

            this.removeButton = new Button({
                iconClass : 'toolbarIcon removeButtonIcon',
                label : this.messagesManager.get('main.toolbar.remove'),
                onClick : lang.hitch(this, this.onActionButtonClick, this.REMOVE_URL),
                disabled : true
            });
            this.removeButton.startup();
            this.toolbar.addChild(this.removeButton);

            this.toolbar.addChild(new ToolbarSeparator());

            this.shareButton = new Button({
                iconClass : 'toolbarIcon shareButtonIcon',
                label : this.messagesManager.get('main.toolbar.share'),
                onClick : lang.hitch(this, this.onActionButtonClick, this.SHARE_URL),
                disabled : true
            });
            this.shareButton.startup();
            this.toolbar.addChild(this.shareButton);

            this.unshareButton = new Button({
                iconClass : 'toolbarIcon unshareButtonIcon',
                label : this.messagesManager.get('main.toolbar.unshare'),
                onClick : lang.hitch(this, this.onActionButtonClick, this.UNSHARE_URL),
                disabled : true
            });
            this.unshareButton.startup();
            this.toolbar.addChild(this.unshareButton);

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
                }, {
                    field : 'feed',
                    label : '',
                    formatter : lang.hitch(this, function(value) {
                        return value ? '<i class="fa fa-file fa-flip-horizontal"></i>' : '';
                    })
                }, {
                    field : 'shared',
                    label : '',
                    formatter : lang.hitch(this, function(value) {
                        return value ? '<i class="fa fa-user"></i>' : '';
                    })
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
            var selected = this.getGridSelection().length;

            if (selected > 0) {
                html.set(this.counter, selected);
                domStyle.set(this.counter, 'display', '');
            } else {
                domStyle.set(this.counter, 'display', 'none');
            }

            this.hideButton.set('disabled', selected === 0);
            this.showButton.set('disabled', selected === 0);
            this.addButton.set('disabled', selected === 0);
            this.removeButton.set('disabled', selected === 0);
            this.shareButton.set('disabled', selected === 0);
            this.unshareButton.set('disabled', selected === 0);
            this.clipboardButton.set('disabled', selected === 0);
        },
        onRefreshButtonClick : function() {
            this.grid.refresh();
        },
        onActionButtonClick : function(url) {
            this.xhrManager.post(url, {
                data : this.getSelectedIds()
            }).then(lang.hitch(this, function() {
                this.grid.refresh();
            }));
        },
        onClipboardButtonClick : function() {
            var text = '';
            var selection = this.getGridSelection();
            array.forEach(selection, function(item, index) {
                text += this.createDownloadUrl(item);
                if (index != selection.length - 1) {
                    text += '\n';
                }
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
            return string.substitute('${base}${download}/${type}/${id}?${path}', {
                base : this.properties.url,
                download : this.DOWNLOAD_URL,
                type : item.shared ? 'shared' : 'regular',
                id : item.id,
                path : item.path
            });
        }
    });
});
