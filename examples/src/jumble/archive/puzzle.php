<?php 
  // date = "YYMMDD"
  $DATESTR = $_GET["date"]; 
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
   <head>
      <title>Daily Jumble</title>
      <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=no;"/>
      <meta name="apple-mobile-web-app-capable" content="yes" />
      <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <meta name="description" content="something"/>
      <meta name="keywords" content="something else"/>
      <script type="text/javascript" src="jumble-iphone.js"></script>
      <link href="../jumble.css" rel="stylesheet" type="text/css"/>
   </head>
   <body>
      <div id="iphone">
         <div id="main">
            <div id="tilediv"></div>
            <img id="image" src="puzzle-<?php echo $DATESTR; ?>.gif" alt="Daily Jumble Image"/>
            <input id="dateinput" type="hidden" value="<?php echo $DATESTR; ?>"/>

            <img id="winimg" src="../winner.gif" alt="You win!"/>
            <img id="errorimg" src="../error.jpg" alt="Error!"/>
         </div>
         <div id="back">
            <span id="status"></span>
         </div>
      </div>
   </body>
</html>
