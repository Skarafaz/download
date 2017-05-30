define([ 'dojo/_base/declare', 'dojo/_base/lang', 'dijit/ConfirmDialog' ], //

function(declare, lang, ConfirmDialog) {
    return declare('app.dialog.ConfirmDialog', ConfirmDialog, {

        show : function(title, content, onExecute, onCancel) {
            this.set('title', title || '');
            this.set('content', content || '');

            if (onExecute) {
                this.set('onExecute', lang.hitch(this, function() {
                    onExecute();
                    this.hide();
                }));
            } else {
                this.set('onExecute', lang.hitch(this, this.hide));
            }

            if (onCancel) {
                this.set('onCancel', lang.hitch(this, function() {
                    onCancel();
                    this.hide();
                }));
            } else {
                this.set('onCancel', lang.hitch(this, this.hide));
            }

            this.inherited(arguments);
        }
    });
});
