package tangram;

public class Tangram {
   private static final boolean debug = true;

   private static final int PLAYING_STATE = 0;
   private static final int LOADING_STATE = 1;
   private static final int WON_STATE = 2;
   private static final int ERROR_STATE = 3;

   private static final int SMALL_TYPE   = 0;
   private static final int SQUARE_TYPE  = 1;
   private static final int DIAMOND_TYPE = 2;
   private static final int MEDIUM_TYPE  = 3;
   private static final int LARGE_TYPE   = 4;
   private static final int NUM_PIECE_TYPES = 5;

   private static final int DRAGGING_ZINDEX = 4;
   private static final int DOWN_ZINDEX = 3;

   private static final int MAIN_WIDTH = 500;
   private static final int MAIN_HEIGHT = 600;

   private static final int PUZZLE_X, PUZZLE_Y;
   private static final int BOARD_WIDTH = 400;
   private static final int BOARD_HEIGHT = 500;
   private static final int MAIN_X = 0;
   private static final int MAIN_Y = 20;
   
   private static final int NUM_ORIENTS = 8;
   private static final int JUMP_THRESHOLD = 10;

   private static class PieceInfo {
      public final int width, height;
      public final String url;
      private final Point[] points, extra;
      
      PieceInfo(int width, int height, String url, Point[] points) {
         this(width, height, url, points, null);
      }
      
      PieceInfo(int width, int height, String url, Point[] points, Point[] extra) {
         this.width = width;
         this.height = height;
         this.url = url;
         this.points = points;
         this.extra = extra;
      }

      public int getNumPoints() {return this.points.length;}
      public Point getPoint(int i) {return this.points[i];}
      public boolean hasExtra() {return this.extra != null;}
      public int getNumExtra() {return this.extra.length;}
      public Point getExtra(int i) {return this.extra[i];}
   }

   private static class Puzzle {
      public final String imgURL;
      public final int width, height;
      private final Point[] points;
      public Puzzle(String imgURL, int width, int height, Point[] points) {
         this.imgURL = imgURL;
         this.width = width;
         this.height = height;
         this.points = points;
      }
      public int getNumPoints() {return this.points.length;}
      public Point getPoints(int i) {return this.points[i];}
   }

   private static final PieceInfo[] small_imgs = {
      new PieceInfo(53, 53, "small0.gif", 
                    new Point[]{new Point(0,0), new Point(0,52), new Point(52,52)}),
      new PieceInfo(73, 37, "small45.gif", 
                    new Point[]{new Point(0,0), new Point(36,36), new Point(72,0)}),
      new PieceInfo(53, 53, "small90.gif", 
                    new Point[]{new Point(0,52), new Point(52,52), new Point(52,0)}),
      new PieceInfo(37, 73, "small135.gif",
                    new Point[]{new Point(0,72), new Point(36,36), new Point(0,0)}),
      new PieceInfo(53, 53, "small180.gif", 
                    new Point[]{new Point(52,52), new Point(52,0), new Point(0,0)}),
      new PieceInfo(73, 37, "small225.gif",
                    new Point[]{new Point(72,36), new Point(36,0), new Point(0,36)}),
      new PieceInfo(53, 53, "small270.gif",
                    new Point[]{new Point(52,0), new Point(0,0), new Point(0,52)}),
      new PieceInfo(37, 73, "small315.gif", 
                    new Point[]{new Point(36,0), new Point(0,36), new Point(36,72)})};

   private static final PieceInfo[] square_imgs = {
      new PieceInfo(53, 53, "square0.gif",
                    new Point[]{new Point(0,0), new Point(0,52), new Point(52,52), new Point(52,0)}),
      new PieceInfo(73, 73, "square45.gif", 
                    new Point[]{new Point(0,36), new Point(36,72), new Point(72,36), new Point(36,0)}),
      new PieceInfo(53, 53, "square0.gif",
                    new Point[]{new Point(0,52), new Point(52,52), new Point(52,0), new Point(0,0)}),
      new PieceInfo(73, 73, "square45.gif",
                    new Point[]{new Point(36,72), new Point(72,36), new Point(36,0), new Point(0,36)}),
      new PieceInfo(53, 53, "square0.gif",
                    new Point[]{new Point(52,52), new Point(52,0), new Point(0,0), new Point(0,52)}),
      new PieceInfo(73, 73, "square45.gif",
                    new Point[]{new Point(72,36), new Point(36,0), new Point(0,36), new Point(36,72)}),
      new PieceInfo(53, 53, "square0.gif",
                    new Point[]{new Point(52,0), new Point(0,0), new Point(0,52), new Point(52,52)}),
      new PieceInfo(73, 73, "square45.gif", 
                    new Point[]{new Point(36,0), new Point(0,36), new Point(36,72), new Point(72,36)})};

   private static final PieceInfo[] diamond_imgs = {
      new PieceInfo(53, 105, "diamond0.gif",
                    new Point[]{new Point(0,0), new Point(0,52), new Point(52,104), new Point(52,52)}),
      new PieceInfo(109, 37, "diamond45.gif",
                    new Point[]{new Point(0,0), new Point(36,36), new Point(108,36), new Point(72,0)}),
      new PieceInfo(105, 153, "diamond90.gif",
                    new Point[]{new Point(0,52), new Point(52,52), new Point(104,0), new Point(52,0)}),
      new PieceInfo(37, 109, "diamond135.gif",
                    new Point[]{new Point(0,108), new Point(36,72), new Point(36,0), new Point(0,36)}),
      new PieceInfo(53, 105, "diamond180.gif",
                    new Point[]{new Point(52,104), new Point(52,52), new Point(0,0), new Point(0,52)}),
      new PieceInfo(109, 37, "diamond225.gif",
                    new Point[]{new Point(108,36), new Point(72,0), new Point(0,0), new Point(36,36)}),
      new PieceInfo(105, 53, "diamond270.gif",
                    new Point[]{new Point(104,0), new Point(52,0), new Point(0,52), new Point(52,52)}),
      new PieceInfo(37, 109, "diamond315.gif",
                    new Point[]{new Point(36,0), new Point(0,36), new Point(0,108), new Point(36,72)})};

   private static final PieceInfo[] medium_imgs = {
      new PieceInfo(53, 105, "medium0.gif",
                    new Point[]{new Point(0,0), new Point(0,104), new Point(52,52)},
                    new Point[]{new Point(0, 52)}),
      new PieceInfo(73, 73, "medium45.gif",
                    new Point[]{new Point(0,0), new Point(72,72), new Point(72,0)},
                    new Point[]{new Point(36,36)}),
      new PieceInfo(105, 53, "medium90.gif",
                    new Point[]{new Point(0,52), new Point(104,52), new Point(52,0)},
                    new Point[]{new Point(52,52)}),
      new PieceInfo(73, 73, "medium135.gif",
                    new Point[]{new Point(0,72), new Point(72,0), new Point(0,0)},
                    new Point[]{new Point(36,36)}),
      new PieceInfo(53, 105, "medium180.gif",
                    new Point[]{new Point(52,104), new Point(52,0), new Point(0,52)},
                    new Point[]{new Point(52,52)}),
      new PieceInfo(73, 73, "medium225.gif",
                    new Point[]{new Point(72,72), new Point(0,0), new Point(0,72)},
                    new Point[]{new Point(36,36)}),
      new PieceInfo(105, 53, "medium270.gif",
                    new Point[]{new Point(104,0), new Point(0,0), new Point(52,52)},
                    new Point[]{new Point(52,0)}),
      new PieceInfo(73, 73, "medium315.gif",
                    new Point[]{new Point(72,0), new Point(0,72), new Point(72,72)},
                    new Point[]{new Point(36,36)})};

   private static final PieceInfo[] large_imgs = {
      new PieceInfo(105, 105, "large0.gif",
                    new Point[]{new Point(0,0), new Point(0,104), new Point(104,104)},
                    new Point[]{new Point(0,52), new Point(52,52), new Point(52,104)}),
      new PieceInfo(145, 73, "large45.gif",
                    new Point[]{new Point(0,0), new Point(72,72), new Point(144,0)},
                    new Point[]{new Point(36,36), new Point(72,0), new Point(108,36)}),
      new PieceInfo(105, 105, "large90.gif",
                    new Point[]{new Point(0,104), new Point(104,104), new Point(104,0)},
                    new Point[]{new Point(52,52), new Point(52,104), new Point(104,52)}),
      new PieceInfo(73, 145, "large135.gif",
                    new Point[]{new Point(0,144), new Point(72,72), new Point(0,0)},
                    new Point[]{new Point(36,36), new Point(36,108), new Point(0,72)}),
      new PieceInfo(105, 105, "large180.gif",
                    new Point[]{new Point(104,104), new Point(104,0), new Point(0,0)},
                    new Point[]{new Point(52,52), new Point(52,0), new Point(104,52)}),
      new PieceInfo(145, 73, "large225.gif",
                    new Point[]{new Point(144,72), new Point(72,0), new Point(0,72)},
                    new Point[]{new Point(36,36), new Point(72,72), new Point(108,36)}),
      new PieceInfo(105, 105, "large270.gif",
                    new Point[]{new Point(104,0), new Point(0,0), new Point(0,104)},
                    new Point[]{new Point(52,52), new Point(52,0), new Point(0,52)}),
      new PieceInfo(73, 145, "large315.gif",
                    new Point[]{new Point(72,0), new Point(0,72), new Point(72,144)},
                    new Point[]{new Point(36,36), new Point(72,72), new Point(36,108)})};

   private static final PieceInfo[][] type2data = {
      small_imgs,
      square_imgs,
      diamond_imgs,
      medium_imgs,
      large_imgs};

   private static final int NUM_PUZZLES = 15;
   //   var PUZZLE = null;
   private int STATE = LOADING_STATE;

   ////////////////////////////
   private Piece[] PIECES;
   private JSObject selected = null;
   private int selectedDX, selectedDY;

   //////////////////////////

   private void centerMain() {
      // center main
      int width;
      if (width = window.document.innerWidth) {}
      else if (width = window.document.documentElement.innerWidth) {}
      else if (width = window.document.body.clientWidth) {}
      else throw "No width attribute to be found!";
      
      var maindiv = Util.$("maindiv");
      MAIN_X = Math.floor((width-MAIN_WIDTH)/2);
      maindiv.style.left = MAIN_X + "px";
      
      if (STATE == PLAYING_STATE) {
         selected = null;
      }
   }

   private void setStatus(String str) {
      Element status = Util.$("status");
      while (status.getProperty("firstChild").asBoolean())
         status.invokeMethod("removeChild", status.getProperty("firstChild"));
      status.appendChild(Util.createTextNode(str));
   }
   
   private Point getCoords(JSObject event) {
      if (e.hasProperty("pageX")) {
         return new Point(e.getProperty("pageX").asNumber().intValue(), 
                          e.getProperty("pageY").asNumber().intValue());
      } else {
         // when using strict doctype, need document.documentElement.*,
         // otherwise it would be document.body.*
         JSObject document = JSObject.globalObject("document");
         return new Point(event.getProperty("clientX").asNumber().intValue() +
                          document.getProperty("documentElement").getProperty("scrollLeft").asNumber().intValue(),
                          event.getProperty("clientY").asNumber().intValue() + 
                          document.getProperty("documentElement").getProperty("scrollTop").asNumber().intValue());
      }
   }
   
   private EventCallback myMouseDown = new EventCallback() {
         public boolean onevent(Element source, JSObject event) {
            if (STATE != PLAYING_STATE)
               return true;
            // find if any was clicked
            Piece clickedPiece = null;
            
            for (int i = 0; i < PIECES.length; i++) {
               Piece piece = PIECES[i];
               Point coords = getCoords(e);
               Point relative = new Point(coords.x-(MAIN_X+50)-piece.x,
                                          coords.y-(MAIN_Y+50)-piece.y);
               PieceInfo mydata = type2data[piece.type][piece.orient];
               if (mydata.points.length == 3) {
                  // 3 points
                  if (Util.inTriangle(mydata.points[0], mydata.points[1], 
                                      mydata.points[2], relative)) {
                     clickedPiece = piece;
                     break;
                  } else {
                     continue;
                  }
               } else {
                  // 4 points
                  if (Util.inTriangle(mydata.points[0], mydata.points[1], mydata.points[2], relative) ||
                      Util.inTriangle(mydata.points[2], mydata.points[3], mydata.points[0], relative)) {
                     clickedPiece = piece;
                     break;
                  } else {
                     continue;
                  }
               }
            }
            if (!clickedPiece)
               return false;
            
            if (selected != null)
               selected.div.style.zIndex = DOWN_ZINDEX;
            
            selected = clickedPiece;
            selectedDX = relative.x;
            selectedDY = relative.y;
            selected.div.style.zIndex = DRAGGING_ZINDEX;
            
            return false;
         }
      };
         
function myMouseUp(e) {
   if (STATE != PLAYING_STATE)
      return true;
   if (!e) var e = window.event;

   if (selected == null)
      return true;

   if (selected != null) {
      // dropping a piece
      selected.div.style.zIndex = DOWN_ZINDEX;
      if (allInPlace()) {
         win();
      } else {
         var offset = selected.isInPlace();
         if (offset != null) {
            // translated!
            if ((Math.abs(offset.x) < JUMP_THRESHOLD) &&
                (Math.abs(offset.y) < JUMP_THRESHOLD)) {
               // jump!
               selected.setX(selected.x + offset.x);
               selected.setY(selected.y + offset.y);

               // do another test to see if you've won
               if (allInPlace())
                  win();
            }
         }
      }
   }
   selected = null;
   return false;
}

   private EventCallback myMouseMove = new EventCallback() {
         public boolean Onevent(Element source, JSObject event) {
            if (STATE != PLAYING_STATE)
               return true;
            if (selected == null)
               return true;
            Point coords = getCoords(e);
            selected = new Point(coords.x-(MAIN_X+50)-selectedDX,
                                 coords.y-(MAIN_Y+50)-selectedDY);
            return false;
         }
      };

function myKeyDown(e) {
   if (STATE != PLAYING_STATE)
      return true;
   if (!e) var e = window.event;

   if (selected == null)
      return true;

   var keynum;
   if (e.keyCode) {
      keynum = e.keyCode;
   } else if (window.event.keyCode) {
      keynum = window.event.keyCode;
      e = window.event;
   }

   if (keynum == 32) { // space
      var mouseX = selected.x + selectedDX;
      var mouseY = selected.y + selectedDY;
      var transform = selected.transformGrab({x:selectedDX, y:selectedDY});
      selected.rotate();
      selectedDX = Math.floor(transform.x);
      selectedDY = Math.floor(transform.y);
      selected.setX(mouseX - selectedDX);
      selected.setY(mouseY - selectedDY);
      return false;
   }

   return true;
}

   private boolean allInPlace() {
      for (int i = 0; i < PIECES.length; i++) {
         Point offset = PIECES[i].isInPlace();
         if (!((offset != null) && (offset.x == 0) && (offset.y == 0))) {
            return false;
         }
      }
      return true;
   }

   private void win() {
      STATE = WON_STATE;
      Util.$("windiv").setStyle("visibility", "visible");
   }

   function parsePuzzle(url) {
      var request = 
         (window.XMLHttpRequest ? 
          new XMLHttpRequest() :
          new ActiveXObject("Microsoft.HTTP"));
      
      var success;
      var input = null;
      var error = -1;
      
      var changefunc = function() {
         if (request.readyState == 4) {
            if (request.status == 200) {
               // all good!
               success = true;
               input = request.responseXML;
            } else {
               // error
               error = request.status;
               success = false;
            }
         }
      }; 
      request.onreadystatechange = changefunc;
      request.open("GET", url, false);
      request.send(null);
      
      changefunc();
      
      if (success) {
         try {
            var result = parsePuzzleXML(input);
            return result;
         } catch (e) {
            if (debug) {
               alert("Error parsing: " + e);
            }
            return null;
         }
      } else {
         if (debug) alert("XML request failed: " + error);
         return null;
      }
   }

   private Puzzle parsePuzzleXML(Element input) {
      String url = Util.getElementsByTagName(input, "url")[0].getAttribute("src");
      Element size = Util.getElementsByTagName(input, "size")[0];
      Element[] pointElements = input.getElementsByTagName(input, "point");
      
      Point[] points = new Point[pointElements.length];

      for (int i = 0; i < pointElements.length; i++) {
         points[i] = new Point(Util.parseInt(pointElements[i].getAttribute("x")),
                               Util.parseInt(pointElements[i].getAttribute("y")));
      }

      return new Puzzle(url, 
                        Util.parseInt(size.getAttribute("width")),
                        Util.parseInt(size.getAttribute("height")),
                        points);
   }

   private void setVisibility(String visibility) {
      String[] toHide = {"smalldiv0", "smalldiv1", "mediumdiv", "diamonddiv", "squarediv", "largediv0", "largediv1"};
      for (int i = 0; i < toHide.length; i++) {
         Util.$(toHide[i]).setStyle("visibility", visibility);
      }
   }

   function loadPuzzle(index) {
      Puzzle puzzle = parsePuzzle("puzzle" + index + ".xml");
      if (puzzle == null) {
         STATE = ERROR_STATE;
         setVisibility("hidden");
         return;
      }
      
      PUZZLE = puzzle;
      
      Element puzzlediv = Util.$("puzzlediv");
      while (puzzlediv.getProperty("firstChild").asBoolean())
         puzzlediv.invokeMethod("removeChild", puzzlediv.getProperty("firstChild"));
      
      Element puzzleimg = Util.createElement("IMG");
      puzzleimg.setProperty("src", PUZZLE.imgURL);
      puzzlediv.appendChild(puzzleimg);
      
      PUZZLE_X = Util.floor((BOARD_WIDTH-PUZZLE.width)/2);
      PUZZLE_Y = Util.floor((BOARD_HEIGHT-PUZZLE.height)/2);
      puzzlediv.setStyle("left", PUZZLE_X + "px");
      puzzlediv.setStyle("top", PUZZLE_Y + "px");
      
      for (int i = 0; i < PIECES.length; i++) {
         PIECES[i].randomize();
      }
      
      setVisibility("visible");
      Util.$("windiv").setStyle("visibility", "hidden");
      
      STATE = PLAYING_STATE;
   }

   void goButtonClick() {
      int index = Util.$("puzzleselector").getProperty("selectedIndex").asNumber().intValue();
      Util.$("gobutton").invokeMethod("blur");
      loadPuzzle(index+1);
   }

   private void init() {
      PIECES = new Piece[7];
      PIECES[0] = new Piece(Util.$("smalldiv0"),SMALL_TYPE);
      PIECES[1] = new Piece(Util.$("smalldiv1"),SMALL_TYPE);
      PIECES[2] = new Piece(Util.$("mediumdiv"),MEDIUM_TYPE);
      PIECES[3] = new Piece(Util.$("diamonddiv"),DIAMOND_TYPE);
      PIECES[4] = new Piece(Util.$("squarediv"),SQUARE_TYPE);
      PIECES[5] = new Piece(Util.$("largediv0"),LARGE_TYPE);
      PIECES[6] = new Piece(Util.$("largediv1"),LARGE_TYPE);
      
      // preload images
      for (int i = 0; i < NUM_PIECE_TYPES; i++) {
         for (int j = 0; j < NUM_ORIENTS; j++) {
            JSObject img = JSObject.newInstance("Image");
            img.setProperty("src", JSObject.fromString(type2data[i][j].url));
         }
      }
      
      Util.$("boarddiv").setProperty("onmousemove", myMouseMove;
      Util.$("boarddiv").onmouseup = myMouseUp;
      Util.$("boarddiv").onmousedown = myMouseDown;
      Util.$("gobutton").onclick = goButtonClick;
      
      var selector = Util.$("puzzleselector");
      while (selector.firstChild)
         selector.removeChild(selector.firstChild);
      for (var i = 0; i < NUM_PUZZLES; i++) {
         var option = document.createElement("OPTION");
         option.appendChild(document.createTextNode("Puzzle " + (i+1)));
         selector.appendChild(option);
      }
      
      centerMain();
      STATE = LOADING_STATE;
      
      var index = Math.floor(Math.random()*NUM_PUZZLES);
      selector.selectedIndex = index;
      loadPuzzle(1+index);
   }



   /*
   window.onload = init;
   window.onresize = centerMain;
   window.onkeydown = myKeyDown;
   if (document.attachEvent)
      document.attachEvent("onkeydown", myKeyDown);
   */




         private class Piece {
            private Element div;
            private int orient, x, y, type;
            
            public Piece(Element div, int type) {
               this.div = div;
               this.type = type;
               this.orient = 0;
               this.x = 0; // this is the left of the DIV within the board
               this.y = 0; // this is the top of the DIV within the board
            }
            
            public Element getDiv() {return this.div;}
            public int getOrient() {return this.orient;}
            public int getX() {return this.x;}
            public int getY() {return this.y;}
            public int getType() {return this.type;}

            /* Return value will be a {x,y} or null.
               If not null, then it means that there is some
               vector P such that for each of this piece's
               critical points C, C+P is a puzzle point.
               (i.e. this piece is oriented right but translated wrong)
            */
            public boolean isInPlace() {
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
      PieceInfo mywidth = type2data[this.type][this.orient].width;
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

}
