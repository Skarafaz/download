define([ 'dojo/_base/declare', 'dojo/_base/lang', 'dojo/request/xhr', 'dojo/json' ], //

function(declare, lang, xhr, json) {
    return declare('app.manager.XhrManager', null, {
        TIMEOUT : 20000,

        get : function(url, opt) {
            return this.xhr(xhr.get, url, opt);
        },
        post : function(url, opt) {
            return this.xhr(xhr.post, url, opt);
        },
        put : function(url, opt) {
            return this.xhr(xhr.put, url, opt);
        },
        del : function(url, opt) {
            return this.xhr(xhr.del, url, opt);
        },
        xhr : function(method, url, opt) {
            return method(url, this.prepareOpt(opt ? opt : {}));
        },
        prepareOpt : function(opt) {
            return {
                data : opt.data ? json.stringify(opt.data) : null,
                query : opt.query ? opt.query : null,
                sync : false,
                preventCache : opt.preventCache ? opt.preventCache : false,
                timeout : opt.timeout ? opt.timeout : this.TIMEOUT,
                handleAs : 'json',
                headers : {
                    'Content-Type' : 'application/json'
                }
            };
        }
    });
});
