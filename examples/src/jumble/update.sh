#!/bin/bash
(cd /cse/htusers/grad/mstepp/jumble/ ;

FULL_DATE=`date +"%c"` ;

# determine date and URLs
URL_PREFIX="http://picayune.uclick.com/comics/tmjmf/puzzles/" ;
DATE=`date --date="$FULL_DATE" "+%y%m%d"` ;
XML_FILENAME="tmjmf${DATE}-data.xml" ;
XML_URL="${URL_PREFIX}${DATE}-data.xml" ;
GIF_FILENAME="tmjmf${DATE}.gif" ;

echo "Downloading files..." ;

# download today's files
wget -nv "${URL_PREFIX}/${XML_FILENAME}" ;
wget -nv "${URL_PREFIX}/${GIF_FILENAME}" ;

echo "Building today's files" ;
# rename and fix files
mv "${GIF_FILENAME}" today.gif ;
cp header.xml today.xml ;
cat "${XML_FILENAME}" >> today.xml ;
chmod a+r today.xml today.gif ;
rm -f "${XML_FILENAME}" ;

echo "Saving in archive" ;

# Save in archive
cp today.gif "archive/puzzle-${DATE}.gif" ;
cp today.xml "archive/puzzle-${DATE}.xml" ;
chmod a+r "archive/puzzle-${DATE}.gif" ;
chmod a+r "archive/puzzle-${DATE}.xml" ;

echo "Running make_daily.sh" ;

./make_daily.sh "$FULL_DATE" ;

) &> /cse/htusers/grad/mstepp/jumble/cron.output ;
