define([ 'dojo/_base/declare', 'dojo/_base/lang' ], //

function(declare, lang) {
    return declare('app.manager.MessagesManager', null, {
        messages : null,

        constructor : function(args) {
            lang.mixin(this, args);

            if (!this.messages) {
                this.messages = {};
            }
        },
        get : function(key) {
            return this.messages[key] || key;
        }
    });
});
