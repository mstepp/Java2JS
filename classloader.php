<?php
   $DEBUG = False;
   if (isset($_GET["class"])) {
      $dotname = $_GET["class"];
      $slashname = str_replace(".", "/", $dotname);
      $resourcename = $slashname . ".class";
      $jsfilename = "js/$slashname.js";

      if ($DEBUG) {
         echo "Dotname is $dotname<br/>";
         echo "Slashname is $slashname<br/>";
         echo "Resourcename is $resourcename<br/>";
         echo "Jsfilename is $jsfilename<br/>";
      }      

      $lastline = system("make -s $jsfilename", $retval);

      if ($DEBUG) {
         echo "Lastline is $lastline<br/>" ;
         echo "Retval is $retval<br/>" ;
      }

      if ($retval == 0) {
         header('Content-type: text/javascript'); 
         ob_start();
         passthru("cat $jsfilename");
         ob_end_flush();
      }
   }
?>
