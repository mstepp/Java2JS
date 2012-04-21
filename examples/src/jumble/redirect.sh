#!/bin/bash

if [ "$#" -lt 1 ] ; then
   FULL_DATE=`date +%c` ;
else
   FULL_DATE="$1" ;
fi ;

DATE=`date --date="$FULL_DATE" "+%y%m%d"` ;

echo '<html>' ;
echo '   <head>' ;
echo "      <meta http-equiv=\"refresh\" content=\"0; url=archive/jumble${DATE}.html\">" ;
echo '   </head>' ;
echo '</html>' ;

