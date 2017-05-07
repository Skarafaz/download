<#import "spring.ftl" as spring />
<!DOCTYPE html>
<html lang="${locale}">
<head>
<title>${appName}</title>
<link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/dojo/resources/dojo.css'/>" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/dijit/themes/claro/claro.css'/>" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/dgrid/css/dgrid.css'/>" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/fontawesome/css/font-awesome.min.css'/>" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/css/app.css'/>" />
<script type="text/javascript">
    dojoConfig = {
        async : true,
        cacheBust: true,
        locale : '${locale}',
        packages : [ {
            name : 'dojo',
            location : '/webjars/dojo'
        }, {
            name : 'dijit',
            location : '/webjars/dijit'
        }, {
            name : 'dgrid',
            location : '/webjars/dgrid'
        }, {
            name : 'dstore',
            location : '/webjars/dstore'
        }, {
            name : 'app',
            location : '/js/app'
        } ]
    };
</script>
<script type="text/javascript" src="<@spring.url '/webjars/dojo/dojo.js'/>"></script>
<script type="text/javascript">
    app = {};
    app.name = '${appName}';
    app.version = '${appVersion}';
    app.url = '${appUrl}';
</script>
<script type="text/javascript" src="<@spring.url '/js/app.js'/>"></script>
</head>
<body class="claro app">
    <div id="container" data-dojo-type="dijit/layout/BorderContainer" data-dojo-props="gutters:false" style="width: 100%; height: 100%">
        <div id="toolbar" data-dojo-type="dijit/Toolbar" data-dojo-props="region:'top'"></div>
        <div data-dojo-type="dijit/layout/ContentPane" data-dojo-props="region:'center'" class="noPadding">
            <div id="grid"></div>
        </div>
    </div>
</body>
</html>
