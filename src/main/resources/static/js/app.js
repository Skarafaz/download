require([ 'dojo/ready', 'dojo/parser', 'dojo/_base/window', 'dijit/layout/BorderContainer', 'dijit/Toolbar', 'dijit/layout/ContentPane',
        'app/controller/MainController', 'app/manager/XhrManager', 'app/manager/MessagesManager' ], //

function(ready, parser, window, BorderContainer, Toolbar, ContentPane, MainController, XhrManager, MessagesManager) {
    ready(function() {
        parser.parse(window.body());

        _global.objects.mainCtrl = new MainController({
            xhrManager : new XhrManager(),
            messagesManager : new MessagesManager({
                messages : _global.messages
            })
        });

        _global.objects.mainCtrl.init();
    });
});
