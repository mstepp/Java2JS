package tangram;

public class Piece {
   private Element div;
   private int orient, x, y, type;

   public Piece(Element div, int type) {
      this.div = div;
      this.type = type;
      this.orient = 0;
      this.x = 0; // this is the left of the DIV within the board
      this.y = 0; // this is the top of the DIV within the board
   }

   /* Return value will be a {x,y} or null.
      If not null, then it means that there is some
      vector P such that for each of this piece's
      critical points C, C+P is a puzzle point.
      (i.e. this piece is oriented right but translated wrong)
   */
   Piece.prototype.isInPlace = function() {
      if (STATE != PLAYING_STATE)
         return false;
      
      // relative to PUZZLE_X and PUZZLE_Y
      var mypoints = [];
      var mydata = type2data[this.type][this.orient];
      // clone the points
      for (var i = 0; i < mydata.points.length; i++) {
         mypoints.push({x:mydata.points[i].x,
                  y:mydata.points[i].y});
      }
      if ("extra" in mydata) {
         for (var i = 0; i < mydata.extra.length; i++) {
            mypoints.push({x:mydata.extra[i].x,
                           y:mydata.extra[i].y});
         }
      }
      
      // translate the points
      for (var i = 0; i < mypoints.length; i++) {
         mypoints[i].x = mypoints[i].x + this.x - PUZZLE_X;
         mypoints[i].y = mypoints[i].y + this.y - PUZZLE_Y;
      }
      
      // test for inplaceness
      var foundAll = true;
      var offsets = [];
      for (var i = 0; i < mypoints.length; i++) {
         var myoffset = {};
         offsets.push(myoffset);
         for (var j = 0; j < PUZZLE.points.length; j++) {
            var dx = PUZZLE.points[j].x - mypoints[i].x;
            var dy = PUZZLE.points[j].y - mypoints[i].y;
            var str = "[" + dx + "," + dy + "]";
            myoffset[str] = {x:dx,y:dy};
         }
      }
      
      // check for offset of [0,0] in all
      var foundAll = true;
      for (var i = 0; i < offsets.length; i++) {
         if (!("[0,0]" in offsets[i])) {
            foundAll = false;
            break;
         }
      }
      if (foundAll)
         return {x:0,y:0};
      
      // not in place, check for same offset in all
      var validOffsets = [];
      for (var offset in offsets[0]) {
         foundAll = true;
         for (var i = 1; i < offsets.length; i++) {
            if (!(offset in offsets[i])) {
               foundAll = false;
               break;
            }
         }
         if (foundAll) 
            validOffsets.push(offsets[0][offset]);
      }
      if (validOffsets.length == 0)
         return null;
      
      var best = validOffsets[0];
      var bestmag = magnitude(best);
      for (var i = 1; i < validOffsets.length; i++) {
         var newmag = magnitude(validOffsets[i]);
         if (newmag < bestmag) {
            best = validOffsets[i];
            bestmag = newmag;
         }
      }
      return best;
   }

      public void randomize() {
         var mywidth = type2data[this.type][this.orient].width;
         var myheight = type2data[this.type][this.orient].height;
         this.setX(Math.floor(Math.random()*(BOARD_WIDTH-mywidth)));
         this.setY(Math.floor(Math.random()*(BOARD_HEIGHT-myheight)));
         this.setOrient(Math.floor(Math.random()*NUM_ORIENTS));
      }

   public void setOrient(int orient) {
      this.orient = orient;
      var mydata = type2data[this.type][this.orient];
      this.div.style.width = mydata.width + "px";
      this.div.style.height = mydata.height + "px";
      var img = document.createElement("IMG");
      img.src = mydata.url;
      while (this.div.firstChild)
         this.div.removeChild(this.div.firstChild);
      this.div.appendChild(img);
   }

      Piece.prototype.rotate = function() {
         this.setOrient((this.orient+1)%NUM_ORIENTS);
      }

   public Point transformGrab(Point grab) {
      // takes an {x,y} that is inside the current shape
      // and returns a corresponding {x,y} that is the same
      // points in the rotated shape (each point will be 
      // relative to its own img's {0,0}.
      // Precondition: grab is inside the current shape, 
      //    coordinates of grab are relative to the shape's div
      // Postcondition: result will be for the next orientation (o+1),
      //    coordinates will be relative to that shape's div
      
      var mydata = type2data[this.type][this.orient];
      var C1 = Util.subtract(grab, mydata.points[0]);
      var cosN45 = Util.sqrt(2.0)/2.0;
      var sinN45 = -cosN45;
      
      Point C2 = new Point((C1.x*cosN45 - C1.y*sinN45), (C1.x*sinN45 + C1.y*cosN45));
      
      var newdata = type2data[this.type][(this.orient+1)%NUM_ORIENTS];
      Point result = Util.add(newdata.points[0],C2);
      return result;
   }

   public void setX(int x) {
      this.x = x;
      this.div.setStyle("left", this.x + "px");
   }

   public void setY(int y) {
      this.y = y;
      this.div.setStyle("top"m this.y + "px");
   }
}
