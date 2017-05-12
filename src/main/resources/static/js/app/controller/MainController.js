define([ 'dojo/_base/declare', 'dojo/_base/lang', 'dijit/registry', 'dijit/form/Button', 'dijit/form/ToggleButton', 'dgrid/OnDemandGrid', 'dgrid/Keyboard',
        'dgrid/Selection', 'dgrid/Selector', 'dstore/Request', 'dojo/string' ], //

function(declare, lang, registry, Button, ToggleButton, OnDemandGrid, Keyboard, Selection, Selector, Request, string) {
    return declare('app.controller.MainController', null, {
        LIST_URL : 'file/list',
        DOWNLOAD_URL : 'file/download/',
        xhrManager : null,
        messagesManager : null,
        properties : null,
        container : null,
        toolbar : null,
        refreshButton : null,
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
                iconClass : 'toolbarIcon toolbarRefreshIcon',
                label : this.messagesManager.get('main.toolbar.refresh'),
                onClick : lang.hitch(this, this.onRefreshButtonClick)
            });
            this.refreshButton.startup();
            this.toolbar.addChild(this.refreshButton);
        },
        initGrid : function() {
            var CustomGrid = declare([ OnDemandGrid, Keyboard, Selection, Selector ]);

            this.grid = new CustomGrid({
                collection : new Request({
                    target : this.LIST_URL,
                    sortParam : 'sort',
                    rangeStartParam : 'start',
                    rangeCountParam : 'count',
                    ascendingPrefix : 'ASC-',
                    descendingPrefix : 'DESC-'
                }),
                columns : [ {
                    field : 'selector',
                    selector : 'checkbox'
                }, {
                    field : 'id',
                    label : this.messagesManager.get('main.grid.id')
                }, {
                    field : 'path',
                    label : this.messagesManager.get('main.grid.path'),
                    get : function(item) {
                        return item;
                    },
                    formatter : lang.hitch(this, function(item) {
                        return string.substitute('<a href="${url}">${label}</a>', {
                            url : this.createDownloadUrl(item.id),
                            label : item.path
                        });
                    })
                } ],
                loadingMessage : '<div style="padding: 5px;"><i class="fa fa-spinner fa-spin fa-lg"></i></div>',
                noDataMessage : this.messagesManager.get('main.grid.noData'),
                allowSelectAll : true,
                sort : 'path'
            }, 'grid');
        },
        onRefreshButtonClick : function() {
            this.grid.refresh();
        },
        createDownloadUrl : function(id) {
            return string.substitute('${base}${download}${id}', {
                base : this.properties.url,
                download : this.DOWNLOAD_URL,
                id : id
            });
        }
    });
});
