// http://picayune.uclick.com/comics/tmjmf/puzzles/tmjmf090804-data.xml
// http://picayune.uclick.com/comics/tmjmf/puzzles/tmjmf090804.gif

var DEBUG = false;

var MIKE_URL_PREFIX = "http://cseweb.ucsd.edu/~mstepp/jumble/" ;

var LOADING_STATE = 0;
var PLAYING_STATE = 1;
var WON_STATE = 2;
var ERROR_STATE = 3;
var STATE = LOADING_STATE;

var SELECTED_ZINDEX = 4;
var UNSELECTED_ZINDEX = 3;

var SELECTED_COLOR = "red";
var UNSELECTED_COLOR = "yellow";
var LOCKED_COLOR = "pink";

var IMG_WIDTH = 164;
var IMG_HEIGHT = 254;
var IPHONE_WIDTH = 320;
var IPHONE_HEIGHT = 356;

var LETTER_WIDTH = 10;
var SQUARE_OUTER_SIZE = 22;
var SQUARE_INNER_SIZE = 20;
var WORD_Y_MARGIN = 10;
var WORD_TOP_MARGIN = 50;
var WORD_LEFT_MARGIN = 15;
var WORD_Y_INC = SQUARE_OUTER_SIZE + SQUARE_OUTER_SIZE + WORD_Y_MARGIN;
var LETTER_Y_STARTS = [
   WORD_TOP_MARGIN, 
   WORD_TOP_MARGIN + WORD_Y_INC,
   WORD_TOP_MARGIN + 2*WORD_Y_INC,
   WORD_TOP_MARGIN + 3*WORD_Y_INC
];

var SLOT_Y_STARTS = [
   LETTER_Y_STARTS[0] + SQUARE_OUTER_SIZE,
   LETTER_Y_STARTS[1] + SQUARE_OUTER_SIZE,
   LETTER_Y_STARTS[2] + SQUARE_OUTER_SIZE,
   LETTER_Y_STARTS[3] + SQUARE_OUTER_SIZE
];

var SELECTED_POSITION = null; // {word:int, placed:bool, index:int}
var SELECTED_LETTER = null;   // {letter:string, div:DIV}

var PUZZLE = null;
var WORDS = null;
// [{placed:[{letter:string, locked:bool, div:DIV}], 
//   unplaced:[{letter:string, locked:bool, div:DIV}]}]
// 
// placed and unplaced will contain null placeholders
// locked can only be true when all are in placed and all in right place

var LAST_WORD = null;
// {invisible:[{letter:string, locked:bool, div:DIV}], placed:same, unplaced:same}
var LAST_WORD_LEFT_MARGIN = 15;
var LAST_LETTER_Y_START = 300;
var LAST_SLOT_Y_START = LAST_LETTER_Y_START + SQUARE_OUTER_SIZE;

////////////////////////////////////////

// result = {jumbled:string, clear:string, circles:[int]}
function getWordInfo(cN) {
   var result = {};
   result.jumbled = cN.getAttribute("j");
   result.clear = cN.getAttribute("a");
   if (result.jumbled.length != result.clear.length)
      throw "Uneven lengths";
   var circles = cN.getAttribute("circle").split(",");
   for (var i = 0; i < circles.length; i++) {
      circles[i] = parseInt(circles[i]);  // 1-based indexes
      if (circles[i] <= 0 || circles[i] > result.jumbled.length)
         throw ("Invalid circle value: " + circles[i]);
   }
   result.circles = circles;

   return result;
}

function trim(str) {
   var result = str;
   while (result.indexOf(" ") == 0)
      result = result.substring(1);
   while (result.length > 0 && result.lastIndexOf(" ") == result.length-1)
      result = result.substring(0,result.length-1);
   return result;
}

function Puzzle(doc) {
   var c1 = doc.getElementsByTagName("c1")[0];
   var c2 = doc.getElementsByTagName("c2")[0];
   var c3 = doc.getElementsByTagName("c3")[0];
   var c4 = doc.getElementsByTagName("c4")[0];

   // words = [{jumbled:string, clear:string, circles:[int]}]
   this.words = [getWordInfo(c1), getWordInfo(c2), getWordInfo(c3), getWordInfo(c4)];
   this.caption = doc.getElementsByTagName("v1")[0].getAttribute("t");
   this.lastword = [];  
   // lastword = [{letter:string, solution:bool}]
   this.lastwordLength = 0;

   var layout = doc.getElementsByTagName("s1")[0].getAttribute("layout");
   layout = trim(layout);

   var inbrack = false;
   for (var i = 0; i < layout.length; i++) {
      var c = layout.substring(i,i+1);
      if (c == "{") {
         if (inbrack) 
            throw "Nested brackets";
         inbrack = true;
      } else if (c == "}") {
         if (!inbrack)
            throw "Not inside brakcets";
         inbrack = false;
      } else {
         if (!inbrack)
            this.lastwordLength++;
         this.lastword.push({letter:c, solution:!inbrack});
      }
   }
}

////////////////////////////////////////

function makeLetterDiv(letter, width, height) {
   var div = document.createElement("DIV");
   div.setAttribute("class", "letter");
   div.style.width = width + "px";
   div.style.height = height + "px";

   var textdiv = document.createElement("DIV");
   textdiv.style.width = width + "px";
   textdiv.style.height = height + "px";
   textdiv.setAttribute("class", "textdiv");

   var text = document.createTextNode(letter);

   textdiv.appendChild(text);
   div.appendChild(textdiv);

   return div;
}

function getLetterDivs(word, index, parentElt) {
   // word = {jumbled:string, clear:string, circles:[int]}
   var divs = [];
   for (var i = 0; i < word.jumbled.length; i++) {
      var letter = word.jumbled.substring(i,i+1);
      var div = makeLetterDiv(letter, SQUARE_INNER_SIZE, SQUARE_INNER_SIZE);
      div.style.top = LETTER_Y_STARTS[index] + "px";
      div.style.left = (WORD_LEFT_MARGIN + i*SQUARE_OUTER_SIZE)+ "px";
      div.data = {word:word, index:index, letter:i};
      divs.push(div);

      parentElt.appendChild(div);
   }
   return divs;
}

function makeSlotDiv() {
   var div = document.createElement("DIV");
   div.setAttribute("class", "slot");
   div.style.width = SQUARE_INNER_SIZE + "px";
   div.style.height = SQUARE_INNER_SIZE + "px";
   return div;
}

function getSlotDivs(word, index, parentElt) {
   // word = {jumbled:string, clear:string, circles:[int]}
   var divs = [];
   for (var i = 0; i < word.clear.length; i++) {
      var letter = word.clear.substring(i,i+1);
      var div = makeSlotDiv();
      div.style.top = SLOT_Y_STARTS[index] + "px";
      div.style.left = (WORD_LEFT_MARGIN + i*SQUARE_OUTER_SIZE)+ "px";
      div.data = {word:word, index:index, letter:i};

      var circled = false;
      for (var j = 0; j < word.circles.length; j++) {
         if (word.circles[j]-1 == i) {
            circled = true;
            break;
         }
      }
      if (circled) {
         var imgtag = document.createElement("IMG");
         imgtag.setAttribute("src", "circle2.gif");
         imgtag.setAttribute("class", "circle");
         div.appendChild(imgtag);
      }

      divs.push(div);

      parentElt.appendChild(div);
   }
   return divs;
}

function clearChildren(elt) {
   while (elt.firstChild)
      elt.removeChild(elt.firstChild);
}

function setupPuzzle(puzzle) {
   STATE = LOADING_STATE;
   var tilediv = document.getElementById("tilediv");
   clearChildren(tilediv);

   // hide winimg and errorimg
   var winimg = document.getElementById("winimg");
   winimg.style.visibility = "hidden";

   var errorimg = document.getElementById("errorimg");
   errorimg.style.visibility = "hidden";

   WORDS = [];
   for (var i = 0; i < puzzle.words.length; i++) {
      var letter_divs = getLetterDivs(puzzle.words[i], i, tilediv);
      var slot_divs = getSlotDivs(puzzle.words[i], i, tilediv);

      var unplaced = [];
      var placed = [];
      for (var j = 0; j < letter_divs.length; j++) {
         unplaced.push({letter : puzzle.words[i].jumbled.substring(j,j+1), 
                        div : letter_divs[j], 
                        locked : false});
         placed.push(null);
      }
      
      WORDS.push({placed:placed, unplaced:unplaced});
   }

   LAST_WORD = {};
   var invisible = [];
   var placed = [];
   var unplaced = [];
   for (var i = 0; i < puzzle.lastword.length; i++) {
      if (puzzle.lastword[i].solution) {
         // real letter: make invisible letterdiv and slotdiv
         var letterdiv = makeLetterDiv(puzzle.lastword[i].letter, SQUARE_INNER_SIZE, SQUARE_INNER_SIZE);
         letterdiv.style.visibility = "hidden";
         tilediv.appendChild(letterdiv);
         invisible.push({letter:puzzle.lastword[i].letter, locked:false, div:letterdiv});

         var slotdiv = makeSlotDiv();
         slotdiv.style.top = (LAST_SLOT_Y_START + "px");
         slotdiv.style.left = lastwordIndex2XCoord(i) + "px";
         tilediv.appendChild(slotdiv);

         unplaced.push(null);
      } else {
         // display letter, make plain letter div
         var letterdiv = makeLetterDiv(puzzle.lastword[i].letter, LETTER_WIDTH, SQUARE_OUTER_SIZE);
         letterdiv.setAttribute("class", "plainletter");
         letterdiv.style.top = (LAST_SLOT_Y_START + "px");
         letterdiv.style.left = lastwordIndex2XCoord(i) + "px";
         tilediv.appendChild(letterdiv);
      }
      placed.push(null);
   }
   LAST_WORD.invisible = invisible;
   LAST_WORD.placed = placed;
   LAST_WORD.unplaced = unplaced;

   STATE = PLAYING_STATE;

   loadCookie();
}


function mytouchend(e) {
//   e.preventDefault();
//   e.stopPropagation();

   if (e.touches.length != 0) {
      if (DEBUG) setStatus("mytouchend: touches.length != 0");
      return false;
   }
   if (e.changedTouches.length != 1) {
      if (DEBUG) setStatus("mytouchend: changedTouches != 1");
      return false;
   }

   e = e.changedTouches.item(0);

   if (STATE != PLAYING_STATE) {
      if (DEBUG) setStatus("error: not playing");
      return true;
   }

   if (SELECTED_POSITION != null) {
      if (putDown(e)) {
         // game over
         won();
      }
   } else {
      pickUp(e);
   }

   return true;
}



function mymousedown(e) {
   e.preventDefault();

   if (STATE != PLAYING_STATE) {
      if (DEBUG) setStatus("error: not playing");
      return true;
   }

   if (SELECTED_POSITION != null) {
      if (putDown(e)) {
         // game over
         won();
      }
   } else {
      pickUp(e);
   }

   return false;
}

function won() {
   STATE = WON_STATE;

   // set all lastword tiles pink
   for (var i = 0; i < LAST_WORD.placed.length; i++) {
      if (LAST_WORD.placed[i] == null)
         continue;
      LAST_WORD.placed[i].div.style.backgroundColor = LOCKED_COLOR;
   }

   var winimg = document.getElementById("winimg");
   winimg.style.visibility = "visible";

   // clear cookie
   for (var i = 0; i < 4; i++) {
      setCookie("word" + (i+1), getWordCookie(i), -2);
   }
}

// assume that array[index] is 'not empty'
function shiftTiles(array, ycoord, index, index2xcoord, isEmpty, isFull) {
      // move it!
      // try to move right
      var firstRight = index;
      while (firstRight < array.length && !isEmpty(firstRight)) {
         firstRight++;
      }
      if (firstRight < array.length) {
         // shift!
         var empty = firstRight;
         while (empty > index) {
            var full = empty-1;
            while (!isFull(full)) full--;
            if (full < index)
               throw "Invalid full index: " + full;

            var moveword = array[full];
            array[empty] = moveword;
            array[full] = null;
            moveword.div.style.top = ycoord + "px";
            moveword.div.style.left = index2xcoord(empty) + "px";

            empty = full;
         }
      } else {
         // move left
         var firstLeft = index;
         while (firstLeft >= 0 && !isEmpty(firstLeft)) {
            firstLeft--;
         }
         if (firstLeft < 0)
            throw "Cannot find position to drop tile";

         var empty = firstLeft;
         while (empty < index) {
            var full = empty+1;
            while (!isFull(full)) full++;
            if (full > index)
               throw "Invalid full index: " + full;

            var moveword = array[full];
            array[empty] = moveword;
            array[full] = null;
            moveword.div.style.top = ycoord + "px";
            moveword.div.style.left = index2xcoord(empty) + "px";

            empty = full;
         }
      }
}

function lastwordIndex2XCoord(index) {
   var xstart = LAST_WORD_LEFT_MARGIN;
   for (var i = 0; i < index; i++) {
      xstart += (PUZZLE.lastword[i].solution ? SQUARE_OUTER_SIZE : LETTER_WIDTH);
   }
   return xstart;
}

// assume initial drop location is valid
function dropLastTile(placed, index, letter) {
   if (placed) {
      // placed
      // function shiftTiles(array, ycoord, index, index2xcoord, isEmpty, isFull) {
      if (LAST_WORD.placed[index] != null) {
         shiftTiles(LAST_WORD.placed, LAST_SLOT_Y_START, index, 
                    lastwordIndex2XCoord,
                    function(index) {return PUZZLE.lastword[index].solution && LAST_WORD.placed[index] == null;},
                    function(index) {return PUZZLE.lastword[index].solution && LAST_WORD.placed[index] != null;});
      }
      
      LAST_WORD.placed[index] = letter;
      letter.div.style.top = LAST_SLOT_Y_START + "px";
      letter.div.style.left = lastwordIndex2XCoord(index) + "px";
   } else {
      // unplaced
      if (LAST_WORD.unplaced[index] != null) {
         shiftTiles(LAST_WORD.unplaced, LAST_LETTER_Y_START, index, 
                    function(index) {return LAST_WORD_LEFT_MARGIN + index*SQUARE_OUTER_SIZE;},
                    function(index) {return LAST_WORD.unplaced[index] == null;},
                    function(index) {return LAST_WORD.unplaced[index] != null;});
      }

      LAST_WORD.unplaced[index] = letter;
      letter.div.style.top = LAST_LETTER_Y_START + "px";
      letter.div.style.left = (LAST_WORD_LEFT_MARGIN + index*SQUARE_OUTER_SIZE) + "px";
   }
}





function dropTile(word, placed, index, letter) {
   var pu = (placed ? "placed" : "unplaced");
   var STARTS = (placed ? SLOT_Y_STARTS : LETTER_Y_STARTS);

   if (WORDS[word][pu][index] != null) {
      shiftTiles(WORDS[word][pu], STARTS[word], index, 
                 function(index) {return WORD_LEFT_MARGIN + index*SQUARE_OUTER_SIZE;},
                 function(index) {return WORDS[word][pu][index] == null;},
                 function(index) {return WORDS[word][pu][index] != null;});
   }

   WORDS[word][pu][index] = letter;
   letter.div.style.top = STARTS[word] + "px";
   letter.div.style.left = (WORD_LEFT_MARGIN + index*SQUARE_OUTER_SIZE) + "px";
}

function getWordCookie(word) {
   var unplaced = "";
   var placed = "";
   for (var i = 0; i < WORDS[word].placed.length; i++) {
      if (WORDS[word].unplaced[i] == null) {
         unplaced += "_";
      } else {
         unplaced += WORDS[word].unplaced[i].letter;
      }
      if (WORDS[word].placed[i] == null) {
         placed += "_";
      } else {
         placed += WORDS[word].placed[i].letter;
      }
   }
   return unplaced + ":" + placed;
}


function putDown(e) {
   var info = getWordClick(e.pageX, e.pageY, SELECTED_POSITION.word);
   var word = WORDS[SELECTED_POSITION.word];

   var deselect = function() {
      SELECTED_POSITION = null;
      SELECTED_LETTER.div.style.zIndex = UNSELECTED_ZINDEX;
      SELECTED_LETTER.div.style.backgroundColor = UNSELECTED_COLOR;
      SELECTED_LETTER = null;
   };

   if (info == null) {
      // outside this word, drop in old position
      if (SELECTED_POSITION.word == 5) {
         dropLastTile(SELECTED_POSITION.placed, SELECTED_POSITION.index, SELECTED_LETTER);
      } else {
         dropTile(SELECTED_POSITION.word, SELECTED_POSITION.placed, SELECTED_POSITION.index, SELECTED_LETTER);
      }
      deselect();
   } else if (info.placed) {
      // in the placed
      if (info.word == 5) {
         // check that drop location is valid
         if (PUZZLE.lastword[info.index].solution) {
            dropLastTile(true, info.index, SELECTED_LETTER);
            deselect();

            // check for game won
            if (checkLastwordUnjumbled())
               return true;
         } else {
            // drop at last location
            dropLastTile(SELECTED_POSITION.placed, SELECTED_POSITION.index, SELECTED_LETTER);
            deselect();
         }
      } else {
         dropTile(SELECTED_POSITION.word, true, info.index, SELECTED_LETTER);
         deselect();

         // check for complete
         if (checkWordUnjumbled(info.word))
            lockWord(info.word);
         setCookie("word" + (info.word+1), getWordCookie(info.word), 1);
      }
   } else {
      // in the unplaced
      if (info.word == 5) {
         dropLastTile(false, info.index, SELECTED_LETTER);
      } else {
         dropTile(SELECTED_POSITION.word, false, info.index, SELECTED_LETTER);
         setCookie("word" + (info.word+1), getWordCookie(info.word), 1);
      }
      deselect();
   }

   return false;
}

function checkLastwordUnjumbled() {
   var unjumbled = "";
   var clear = "";
   for (var i = 0; i < PUZZLE.lastword.length; i++) {
      if (PUZZLE.lastword[i].solution) {
         clear += PUZZLE.lastword[i].letter;
         if (LAST_WORD.placed[i] != null)
            unjumbled += LAST_WORD.placed[i].letter;
         else
            unjumbled += "_";
      } else {
         clear += "_";
         unjumbled += "_";
      }
   }

   return unjumbled == clear;
}


function checkWordUnjumbled(word) {
   var unjumbled = "";
   for (var j = 0; j < WORDS[word].placed.length; j++) {
      if (WORDS[word].placed[j] == null)
         unjumbled += "_";
      else  
         unjumbled += WORDS[word].placed[j].letter;
   }
   return (unjumbled == PUZZLE.words[word].clear);
}

function lockWord(word) {
   for (var j = 0; j < WORDS[word].placed.length; j++) {
      WORDS[word].placed[j].locked = true;
      WORDS[word].placed[j].div.style.backgroundColor = LOCKED_COLOR;
   }

   // do the circles
   for (var i = 0; i < PUZZLE.words[word].circles.length; i++) {
      var index = PUZZLE.words[word].circles[i]-1;
      var letter = WORDS[word].placed[index].letter;

      // find an invisible with the same letter
      var invisible = null;
      for (var j = 0; j < LAST_WORD.invisible.length; j++) {
         if (LAST_WORD.invisible[j] == null)
            continue;
         if (LAST_WORD.invisible[j].letter == letter) {
            invisible = LAST_WORD.invisible[j];
            LAST_WORD.invisible[j] = null;
            break;
         }
      }
      if (invisible == null)
         throw "Can't find invisible for letter: " + letter;

      // concat the invisible to the unplaced
      for (var j = 0; j < LAST_WORD.unplaced.length; j++) {
         if (LAST_WORD.unplaced[j] == null) {
            LAST_WORD.unplaced[j] = invisible;
            invisible.div.style.top = (LAST_LETTER_Y_START) + "px";
            invisible.div.style.left = (LAST_WORD_LEFT_MARGIN + j*SQUARE_OUTER_SIZE) + "px";
            invisible.div.style.visibility = "visible";
            break;
         }
      }
   }
}


function getLastWordClick(x,y) {
   // check the last word
   if (LAST_LETTER_Y_START <= y &&
       y <= LAST_LETTER_Y_START + SQUARE_OUTER_SIZE &&
       LAST_WORD_LEFT_MARGIN <= x &&
       x < LAST_WORD_LEFT_MARGIN + PUZZLE.lastwordLength*SQUARE_OUTER_SIZE) {
      var index = Math.floor((x-LAST_WORD_LEFT_MARGIN)/SQUARE_OUTER_SIZE);
      return {word:5, placed:false, index:index};
   } else if (LAST_SLOT_Y_START <= y &&
              y <= LAST_SLOT_Y_START + SQUARE_OUTER_SIZE) {
      var xstart = LAST_WORD_LEFT_MARGIN;
      for (var i = 0; i < PUZZLE.lastword.length; i++) {
         var width = (PUZZLE.lastword[i].solution ? SQUARE_OUTER_SIZE : LETTER_WIDTH);
         if (xstart <= x && x < xstart+width) {
            return {word:5, placed:true, index:i};
         }
         xstart += width;
      }
   }

   return null;
}


// 'word' is optional
// returns {word:int, placed:bool, index:int}
function getWordClick(x,y,word) {
   if ((typeof word) == "number") {
      if (word == 5) {
         return getLastWordClick(x,y);
      } else {
         if (WORD_LEFT_MARGIN <= x &&
             x < WORD_LEFT_MARGIN + WORDS[word].unplaced.length*SQUARE_OUTER_SIZE) {
            var index = Math.floor((x-WORD_LEFT_MARGIN)/SQUARE_OUTER_SIZE);
   
            if (LETTER_Y_STARTS[word] <= y &&
                y <= LETTER_Y_STARTS[word]+SQUARE_OUTER_SIZE) {
               return {word:word, placed:false, index:index};
            } else if (SLOT_Y_STARTS[word] <= y &&
                       y <= SLOT_Y_STARTS[word]+SQUARE_OUTER_SIZE) {
               return {word:word, placed:true, index:index};
            }
         }
      }
   } else {
      // find the word
      for (var word = 0; word < WORDS.length; word++) {
         if (WORD_LEFT_MARGIN <= x &&
             x < WORD_LEFT_MARGIN + WORDS[word].unplaced.length*SQUARE_OUTER_SIZE) {
            var index = Math.floor((x-WORD_LEFT_MARGIN)/SQUARE_OUTER_SIZE);

            if (LETTER_Y_STARTS[word] <= y &&
                y <= LETTER_Y_STARTS[word]+SQUARE_OUTER_SIZE) {
               return {word:word, placed:false, index:index};
            } else if (SLOT_Y_STARTS[word] <= y &&
                       y <= SLOT_Y_STARTS[word]+SQUARE_OUTER_SIZE) {
               return {word:word, placed:true, index:index};
            }
         }
      }

      var lastinfo = getLastWordClick(x,y);
      if (lastinfo != null)
         return lastinfo;
   }

   return null;
}


function pickUp(e) {
   var info = getWordClick(e.pageX, e.pageY);
   if (info == null)
      return;

   var pu = (info.placed ? "placed" : "unplaced");

   if (info.word == 5) {
      // last word!
      if (LAST_WORD[pu][info.index] != null) {
         // not empty
         SELECTED_POSITION = info;
         SELECTED_LETTER = LAST_WORD[pu][info.index];
         SELECTED_LETTER.div.style.zIndex = SELECTED_ZINDEX;
         SELECTED_LETTER.div.style.backgroundColor = SELECTED_COLOR;
         LAST_WORD[pu][info.index] = null;
      }      
   } else if (WORDS[info.word][pu][info.index] != null &&
              !WORDS[info.word][pu][info.index].locked) {
      SELECTED_POSITION = info;
      SELECTED_LETTER = WORDS[info.word][pu][info.index];
      SELECTED_LETTER.div.style.zIndex = SELECTED_ZINDEX;
      SELECTED_LETTER.div.style.backgroundColor = SELECTED_COLOR;
      WORDS[info.word][pu][info.index] = null;
   }
}

// callback from loadPuzzle
function process_puzzle(doc) {
   try {
      PUZZLE = new Puzzle(doc);
      setupPuzzle(PUZZLE);
   } catch (e) {
      if (DEBUG) setStatus("error: " + e);
      error();
   }
}

function loadPuzzle() {
   var XHR = new XMLHttpRequest();
   XHR.open("GET", MIKE_URL_PREFIX + "/today.xml", true);
   XHR.onreadystatechange = function() {
      if (XHR.readyState == 4 && XHR.status == 200) {
         process_puzzle(XHR.responseXML);
      } else if (XHR.readyState == 4) {
         if (DEBUG) setStatus("XHR status code = " + XHR.status);
         error();
      }
   };
   XHR.send(null);
}

function loadArchivePuzzle(index) {
   var archivedate = new Date();
   archivedate.setDate(archivedate.getDate()-index);

   var year = (archivedate.getFullYear() + "").substring(2);
   var month = (archivedate.getMonth()+1) + "";
   if (month.length < 2) month = "0" + month;
   var day = archivedate.getDate() + "";
   if (day.length < 2) day = "0" + day;
   var prefix = "puzzle-" + year+month+day;;

   var XHR = new XMLHttpRequest();
   XHR.open("GET", MIKE_URL_PREFIX + "/archive/" + prefix + ".xml", true);
   XHR.onreadystatechange = function() {
      if (XHR.readyState == 4 && XHR.status == 200) {
         var image = document.getElementById("image");
         image.setAttribute("src", "archive/" + prefix + ".gif");
         process_puzzle(XHR.responseXML);
      } else if (XHR.readyState == 4) {
         if (DEBUG) setStatus("XHR status code = " + XHR.status);
         error();
      }
   };
   XHR.send(null);
}

function mockLoadPuzzle() {
   var root = document.createElement("jumble");

   var poop = function(name, attrs) {
      var elt = document.createElement(name);
      for (var i = 0; i < attrs.length; i++) {
         elt.setAttribute(attrs[i][0], attrs[i][1]);
      }
      root.appendChild(elt);
   };

   poop("c", [["j","KALOC"],["a","CLOAK"],["circle","3,4"]]);
   poop("c2", [["j","IGNAT"],["a","GIANT"],["circle","4,5"]]);
   poop("c3", [["j","TRYAGE"],["a","GYRATE"],["circle","1,3"]]);
   poop("c4", [["j","BOEDUL"],["a","DOUBLE"],["circle","1,2,3"]]);
   poop("v1", [["t","He never got around to marrying, but he"]]);
   poop("s1", [["layout","GOT{ }AROUND "],["a","GOTAROUND"]]);

   process_puzzle(doc);
}



function error() {
   STATE = ERROR_STATE;
   PUZZLE = null;
   SELECTED_POSITION = null;
   WORDS = null;
   LAST_WORD = null;

   var errorimg = document.getElementById("errorimg");
   errorimg.style.visibility = "visible";

   var image = document.getElementById("image");
   image.style.visibility = "hidden";
}


function setStatus(str) {
   var status = document.getElementById("status");
   clearChildren(status);
   status.appendChild(document.createTextNode(str));
}


// searches the cookie for the given attribute name
function checkCookie(attr) {
   var attrregex = new RegExp("(?:^|[^0-9a-zA-Z])" + attr + "\\s*=\\s*([^;]+)(?:;|$)");
   var result = attrregex.exec(document.cookie);
   if (result && result.length>1) {
      return result[1];
   } else
      return null;
}

function clearCookie() {
   setCookie("word1","nil",-2);
   setCookie("word2","nil",-2);
   setCookie("word3","nil",-2);
   setCookie("word4","nil",-2);
}

function sameLetters(str1,str2) {
   var counts1 = new Array(26);
   var counts2 = new Array(26);
   for (var i = 0; i < 26; i++)
      counts1[i] = counts2[i] = 0;
   for (var i = 0; i < str1.length; i++) {
      var ch = str1.charCodeAt(i);
      if (65 <= ch && ch <= 90)
         counts1[ch-65]++;
      else if (97 <= ch && ch <= 122)
         counts1[ch-97]++;
   }
   for (var i = 0; i < str2.length; i++) {
      var ch = str2.charCodeAt(i);
      if (65 <= ch && ch <= 90)
         counts2[ch-65]++;
      else if (97 <= ch && ch <= 122)
         counts2[ch-97]++;
   }

   for (var i = 0; i < 26; i++) {
      if (counts1[i] != counts2[i])
         return false;
   }
   return true;
}


// must be called in initial game state,
// where all tiles are unplaced
function loadCookie() {
   for (var word = 0; word < 4; word++) {
      var wordN = checkCookie("word" + (1+word));
      if (wordN) {
         if (!sameLetters(wordN, PUZZLE.words[word].clear)) {
            clearCookie();
            return;
         }
         wordN = trim(wordN);
         var colon = wordN.indexOf(":");
         var unplaced = wordN.substring(0,colon);
         var placed = wordN.substring(colon+1);

         var letters = [];
         for (var i = 0; i < WORDS[word].unplaced.length; i++) {
            letters.push(WORDS[word].unplaced[i]);
            WORDS[word].unplaced[i] = null;
         }

         for (var i = 0; i < placed.length; i++) {
            var ch = placed.charAt(i);
            if (ch=="_") continue;
            for (var j = 0; j < letters.length; j++) {
               if (letters[j]!=null && 
                   letters[j].letter == ch) {
                  dropTile(word, true, i, letters[j]);
                  letters[j] = null;
               }
            }
         }

         for (var i = 0; i < unplaced.length; i++) {
            var ch = unplaced.charAt(i);
            if (ch=="_") continue;
            for (var j = 0; j < letters.length; j++) {
               if (letters[j]!=null && 
                   letters[j].letter == ch) {
                  dropTile(word, false, i, letters[j]);
                  letters[j] = null;
               }
            }
         }

         if (checkWordUnjumbled(word))
            lockWord(word);
      }
   }
}

function setCookie(attr,value,daysToLive) {
   var str = attr + "=" + value + " ; " ;
   if (daysToLive) {
      var date = new Date();
      date.setTime(date.getTime() + daysToLive*24*60*60*1000);
      str += "expires=" + date.toGMTString() + " ; ";
   }
   document.cookie = str;
}


var FIRST_ARCHIVE_DATE = new Date("Fri Aug 14 00:00:01 PDT 2009");

function init() {
   var donothing = function(e) {e.preventDefault(); return false;};
   document.addEventListener("scroll", donothing, true);
   document.addEventListener("scroll", donothing, false);
   document.addEventListener("touchmove", donothing, true);
   document.addEventListener("touchmove", donothing, false);
   document.addEventListener("touchstart", donothing, false);
   document.addEventListener("touchstart", donothing, true);
   document.addEventListener("touchend", (DEBUG ? donothing : mytouchend), true);
   document.addEventListener("touchcancel", donothing, false);
   document.addEventListener("touchcancel", donothing, true);
   document.addEventListener("mouseup", donothing, true);
   document.addEventListener("mousemove", donothing, true);
   document.addEventListener("mousedrag", donothing, true);
   document.addEventListener("selectstart", donothing, true);

   // mousedown needed for select element
   document.addEventListener("mousedown", (DEBUG ? mymousedown : donothing), true);
   document.addEventListener("mousedown", donothing, false);

   var archivebutton = document.getElementById("archivebutton");
   var archiveselector = document.getElementById("archiveselector");

   archivebutton.onclick = function() {
      var index = archiveselector.selectedIndex;
      try {
         loadArchivePuzzle(index);
      } catch (e) {
         if (DEBUG) setStatus("error: " + e);
         error();
      }
      return false;
   };

   populateArchiveSelector(archiveselector);

   try {
      loadPuzzle();
   } catch (e) {
      if (DEBUG) setStatus("error: " + e);
      error();
   }
}

function populateArchiveSelector(selector) {
   clearChildren(selector);
   var current = new Date();
   while (current >= FIRST_ARCHIVE_DATE) {
      var datestring = (current.getMonth()+1) + "-" +
         current.getDate() + "-" +
         current.getFullYear();
      var option = document.createElement("OPTION");
      option.appendChild(document.createTextNode("Puzzle from " + datestring));      
      selector.appendChild(option);
      current.setDate(current.getDate()-1);
   }
}

window.onload = init;
