#!/bin/bash
if [ "$#" -lt 1 ] ; then
   FULL_DATE=`date +%c` ;
else
   FULL_DATE="$1" ;
fi ;

DATE=`date --date="$FULL_DATE" "+%y%m%d"` ;
DISPLAY_DATE=`date --date="$FULL_DATE" +"%B %e, %Y"` ;

cd /cse/htusers/grad/mstepp/jumble/ ;


if ! grep "$DATE" items.xml &> /dev/null ; then
    # not in the file
    echo "      <item>"  >  new_items.xml ;
    echo "         <title>Daily Jumble for $DISPLAY_DATE</title>" >> new_items.xml ;
    echo "         <link>http://cseweb.ucsd.edu/~mstepp/jumble/archive/jumble${DATE}.html</link>" >> new_items.xml ;
    echo "         <description>&lt;img alt=\"Puzzle image\" src=\"http://cseweb.ucsd.edu/~mstepp/jumble/archive/puzzle-${DATE}.gif\"/&gt;</description>" >> new_items.xml ;
    echo "         <pubDate>$FULL_DATE</pubDate>" >> new_items.xml ;
    echo "         <guid>http://cseweb.ucsd.edu/~mstepp/jumble/archive/jumble${DATE}.html</guid>" >> new_items.xml ;
    echo "      </item>" >> new_items.xml ;

    cat items.xml >> new_items.xml ;
    mv new_items.xml items.xml ;
    chmod a+r items.xml ;
fi ;

# do this either way
cat rss_header.xml items.xml rss_trailer.xml > rss.xml ;
chmod a+r rss.xml ;

