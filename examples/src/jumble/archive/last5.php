<?php
  $backtime = array();
  $backtime[0] = time();
  $backtime[1] = $backtime[0] - 60*60*24;
  $backtime[2] = $backtime[1] - 60*60*24;
  $backtime[3] = $backtime[2] - 60*60*24;
  $backtime[4] = $backtime[3] - 60*60*24;
  $dispstr = array();
  $dispstr[0] = "Puzzle for " . date("l, F jS", $backtime[0]);
  $dispstr[1] = "Puzzle for " . date("l, F jS", $backtime[1]);
  $dispstr[2] = "Puzzle for " . date("l, F jS", $backtime[2]);
  $dispstr[3] = "Puzzle for " . date("l, F jS", $backtime[3]);
  $dispstr[4] = "Puzzle for " . date("l, F jS", $backtime[4]);
  $backstr = array();
  $backstr[0] = date("ymd", $backtime[0]);
  $backstr[1] = date("ymd", $backtime[1]);
  $backstr[2] = date("ymd", $backtime[2]);
  $backstr[3] = date("ymd", $backtime[3]);
  $backstr[4] = date("ymd", $backtime[4]);
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
      <meta name="keywords" content="jumble"/>
      <script type="text/javascript">
        var dates = [
           <?php echo '"' . $backstr[0] . '"'; ?>,
           <?php echo '"' . $backstr[1] . '"'; ?>,
           <?php echo '"' . $backstr[2] . '"'; ?>,
           <?php echo '"' . $backstr[3] . '"'; ?>,
           <?php echo '"' . $backstr[4] . '"'; ?>
        ];

        function change() {
           var index = document.getElementById("selector").selectedIndex;
           document.location = "puzzle.php?date=" + dates[index];
        }

        window.onload = function() {
           document.getElementById("selector").onchange = change;
        };
      </script>
   </head>
   <body style="text-align: center;">
     <h1>Choose a recent puzzle:</h1>
     <p>
       <select id="selector">
         <option><?php echo $dispstr[0]; ?></option>
         <option><?php echo $dispstr[1]; ?></option>
         <option><?php echo $dispstr[2]; ?></option>
         <option><?php echo $dispstr[3]; ?></option>
         <option><?php echo $dispstr[4]; ?></option>
       </select>
     </p>
   </body>
</html>