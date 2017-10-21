require([ 'dojo/ready', 'dojo/parser', 'dojo/_base/window', 'dijit/layout/BorderContainer', 'dijit/Toolbar', 'dijit/layout/ContentPane',
        'app/controller/MainController', 'app/manager/XhrManager', 'app/manager/MessagesManager' ], //

function(ready, parser, window, BorderContainer, Toolbar, ContentPane, MainController, XhrManager, MessagesManager) {
    ready(function() {
        parser.parse(window.body());

        _global.objects.xhrManager = new XhrManager();

        _global.objects.messagesManager = new MessagesManager({
            messages : _global.messages
        });

        _global.objects.mainController = new MainController({
            properties : _global.properties,
            xhrManager : _global.objects.xhrManager,
            messagesManager : _global.objects.messagesManager
        });

        _global.objects.mainController.init();
    });
});
