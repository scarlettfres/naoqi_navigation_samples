<?xml version="1.0" encoding="UTF-8" ?>
<Package name="explore" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="patrol_panel" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="patrol" src="patrol/patrol.dlg" />
    </Dialogs>
    <Resources>
        <File name="index" src="html/index.html" />
        <File name="jquery-1.11.0.min" src="html/js/jquery-1.11.0.min.js" />
        <File name="main" src="html/js/main.js" />
        <File name="robotutils" src="html/js/robotutils.js" />
        <File name="icon" src="html/img/icon.png" />
        <File name="angular-animate.min" src="html/js/angular-animate.min.js" />
        <File name="angular-sanitize.min" src="html/js/angular-sanitize.min.js" />
        <File name="angular-touch" src="html/js/angular-touch.js" />
        <File name="angular-touch.min" src="html/js/angular-touch.min.js" />
        <File name="angular" src="html/js/angular.js" />
        <File name="angular.min" src="html/js/angular.min.js" />
        <File name="strings-enu" src="html/js/locale/strings-enu.json" />
        <File name="strings-frf" src="html/js/locale/strings-frf.json" />
        <File name="ngTouch" src="html/js/ngTouch.js" />
        <File name="robotutils.min" src="html/js/robotutils.min.js" />
        <File name="spinningwheel-min" src="html/js/spinningwheel-min.js" />
        <File name="spinningwheel" src="html/js/spinningwheel.js" />
        <File name="README" src="README.md" />
    </Resources>
    <Topics>
        <Topic name="patrol_enu" src="patrol/patrol_enu.top" topicName="patrol" language="en_US" />
        <Topic name="patrol_frf" src="patrol/patrol_frf.top" topicName="patrol" language="fr_FR" />
    </Topics>
    <IgnoredPaths />
</Package>
