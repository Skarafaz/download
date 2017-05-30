require([ 'dojo/ready', 'dojo/parser', 'dojo/_base/window', 'dijit/layout/BorderContainer', 'dijit/Toolbar', 'dijit/layout/ContentPane',
        'app/controller/MainController', 'app/manager/XhrManager', 'app/manager/MessagesManager', 'app/dialog/ConfirmDialog' ], //

function(ready, parser, window, BorderContainer, Toolbar, ContentPane, MainController, XhrManager, MessagesManager, ConfirmDialog) {
    ready(function() {
        parser.parse(window.body());

        _global.objects.xhrManager = new XhrManager();

        _global.objects.messagesManager = new MessagesManager({
            messages : _global.messages
        });

        _global.objects.confirmDialog = new ConfirmDialog({
            style : 'width: 300px'
        });

        _global.objects.mainController = new MainController({
            properties : _global.properties,
            xhrManager : _global.objects.xhrManager,
            messagesManager : _global.objects.messagesManager,
            confirmDialog : _global.objects.confirmDialog
        });

        _global.objects.mainController.init();
    });
});
