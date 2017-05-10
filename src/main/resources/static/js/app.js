require([ 'dojo/ready', 'dojo/parser', 'dojo/_base/window', 'dijit/layout/BorderContainer', 'dijit/Toolbar', 'dijit/layout/ContentPane',
        'app/controller/MainController', 'app/manager/XhrManager' ], //

function(ready, parser, window, BorderContainer, Toolbar, ContentPane, MainController, XhrManager) {
    ready(function() {
        parser.parse(window.body());

        _global.objects.mainCtrl = new MainController({
            xhrManager : new XhrManager()
        });

        _global.objects.mainCtrl.init();
    });
});
