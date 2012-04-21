#!/bin/bash
if [ "$#" -lt 1 ] ; then
   FULL_DATE=`date +%c` ;
else 
   FULL_DATE="$1" ;
fi ; 

DATE=`date --date="$FULL_DATE" "+%y%m%d"` ;

cd /cse/htusers/grad/mstepp/jumble/ ;

FILENAME="archive/jumble${DATE}.html" ;
cat top-daily.html > $FILENAME;
echo "            <img id=\"image\" src=\"puzzle-${DATE}.gif\" alt=\"Daily Jumble Image\"/>" >> $FILENAME ;
echo "            <input id=\"dateinput\" type=\"hidden\" value=\"${DATE}\"/>" >> $FILENAME ;
cat bottom-daily.html >> $FILENAME ;
chmod a+r $FILENAME ;

./rss.sh "$FULL_DATE" ;

if ./redirect.sh "$FULL_DATE" > daily.html ; then
    chmod a+r daily.html ;
fi ;


