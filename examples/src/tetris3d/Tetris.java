package tetris3d;

import java.util.*;

public class Tetris {
   private static class BlockType {
      public final int[][] diffs;
      public final Color color;
      BlockType(int[][] diffs, Color color) {
         this.diffs = diffs;
         this.color = color;
      }
   }

   private static final BlockType[] blocktypes = {
      // plusblock
      new BlockType(new int[][]{{0,0,0},{-1,0,0},{0,1,0},{1,0,0}}, new Color(179, 230, 26)),
      // squareblock
      new BlockType(new int[][]{{0,0,0},{0,1,0},{1,1,0},{1,0,0}}, new Color(179, 230, 26)),
      // stickblock
      new BlockType(new int[][]{{0,0,0},{0,-1,0},{0,-2,0},{0,1,0}}, new Color(179, 230, 26)),
      // s-block
      new BlockType(new int[][]{{0,0,0},{0,-1,0},{1,0,0},{1,1,0}}, new Color(179, 230, 26)),
      // l-block
      new BlockType(new int[][]{{0,0,0},{1,0,0},{0,1,0},{0,2,0}}, new Color(179, 230, 26)),
      // newblock1
      new BlockType(new int[][]{{0,0,0},{0,0,-1},{0,1,-1},{1,1,-1}}, new Color(179, 230, 26)),
      // newblock2
      new BlockType(new int[][]{{0,0,0},{0,0,-1},{0,1,-1},{-1,1,-1}}, new Color(179, 230, 26)),
      // newblock3
      new BlockType(new int[][]{{0,0,0},{0,0,-1},{1,0,-1},{0,1,-1}}, new Color(179, 230, 26))
   };

   double droptimeout;
   long lastdroptime;
   int score;
   Function updateScore;
   Element gobutton;
   final int PLAYING_STATE = 0;
   final int LOADING_STATE = 1;
   final int DEAD_STATE = 2;
   final int FLASHING_STATE = 3;
   int state = LOADING_STATE;
   Camera camera;
   Shape wallshape;
   Shape floorshape;
   double diffuse = 0.05;
   double[][] lights;
   int width = 8;
   int height = 16;
   final double squaresize = 3.0;
   double zstart = 2;
   final Color wallcolor = new Color(3, 3, 230);
   Screen myscreen;
   Box[][][] boxes;
   Block currentBlock;
   int[][] shadowgrid;
   Shape axisshape;
   double[][] axistransformed;
   double[] axistranscenter = {0.8, 0.8, -0.1};
   
   double camera_angle = (225/360)*2.0*Math.PI;
   double camera_radius = Math.sqrt((squaresize*width/2+40)*(squaresize*width/2+40)*2);
   double camera_nup_radius = -1/Math.cos(camera_angle);
   double[] camera_center = {squaresize*width/2, squaresize*width/2, squaresize*height+30};
   
   void updateCamera() {
      double[] point = {camera_radius*Math.cos(camera_angle), camera_radius*Math.sin(camera_angle), 0};
      Util.add(camera_center, point, point);
      camera.setPoint(point);
      lights = new double[][]{point};
      
      double[] n = {0.1*camera_nup_radius*Math.cos(camera_angle), 0.1*camera_nup_radius*Math.sin(camera_angle), 0.105};
      double[] up = {-1*camera_nup_radius*Math.cos(camera_angle), -1*camera_nup_radius*Math.sin(camera_angle), 0};
      camera.setAxes(n,up);
      
      // retransform the walls and floor
      for (int i = 0; i < wallshape.faces.size(); i++) {
         camera.transformFace(wallshape.faces.get(i));
      }
      for (int i = 0; i < floorshape.faces.size(); i++) {
         camera.transformFace(floorshape.faces.get(i));
      }
      
      // retransform axis points
      camera.transformWorld2Cube(axisshape.points, axistransformed);
      double[] temp = {0,0,0};
      Util.copy(temp, axistransformed[0]);
      for (int i = 0; i < axistransformed.length; i++) {
         Util.subtract(axistransformed[i], temp, axistransformed[i]);
      }
      for (int i = 0; i < axistransformed.length; i++) {
         Util.add(axistransformed[i], axistranscenter, axistransformed[i]);
      }
      
      redraw();
   }
                   
   void buildAxisShape() {
      double radius = 5;
      axisshape = new Shape(new double[][]{{0,0,0,1},
                                           {radius,0,0,1},  // x+
                                           {-radius,0,0,1}, // x-
                                           {0,radius,0,1},  // y+
                                           {0,-radius,0,1}}); // y-
      
      axistransformed = new double[axisshape.points.length][];
      for (int i = 0; i < axisshape.points.length; i++) 
         axistransformed[i] = new double[]{0,0,0,0};
   }
  
   void buildWallAndFloor() {
      double max = width*squaresize;
      wallshape = new Shape(new double[][]{// neartopright: 0
            {max, max, squaresize*height, 1},
            // nearbottomright: 1
            {max, 0, squaresize*height, 1},
            // nearbottomleft: 2
            {0, 0, squaresize*height, 1},
            // neartopleft: 3
            {0, max, squaresize*height, 1},
            
            // fartopright: 4
            {max, max, 0, 1},
            // farbottomright: 5
            {max, 0, 0, 1},
            // farbottomleft: 6
            {0, 0, 0, 1},
            // fartopleft: 7
            {0, max, 0, 1}});
      
      // make wall faces
      wallshape.addFace(new int[]{3,0,4,7},wallcolor); // top face
      wallshape.addFace(new int[]{0,1,5,4},wallcolor); // right face
      wallshape.addFace(new int[]{2,3,7,6},wallcolor); // left face
      wallshape.addFace(new int[]{1,2,6,5},wallcolor); // bottom face
      
      //////////////////////////////////////////////
      
      // make floor points
      java.util.List<double[]> points = new ArrayList<double[]>();
      for (int x = 0; x <= width; x++) {
         for (int y = 0; y <= width; y++) {
            points.add(new double[]{x*squaresize, y*squaresize, 0, 1});
         }
      }
      floorshape = new Shape(points.toArray(new double[0][]));

      // make floor faces
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < width; y++) {
            floorshape.addFace(new int[]{x*(width+1)+y,  x*(width+1)+y+1, (x+1)*(width+1)+y+1, (x+1)*(width+1)+y}, wallcolor);
         }
      }
   }
   
   
   // should only be run once, ever
   public void init(Element button, Function scoreCallback, JSObject context) {
      myscreen = new Screen(400, 400, new Color(255,255,255), context);
      gobutton = button;
      updateScore = scoreCallback;
      camera = new Camera(new double[]{0,0,0}, new double[]{0,0,1}, new double[]{0,1,0});
      double dim = squaresize*width/2;
      camera.setBounds(-dim,dim,-dim,dim,-60);
      
      shadowgrid = new int[width][width];
      for (int i = 0; i < width; i++) {
         int[] row = new int[width];
         for (int j = 0; j < width; j++)
            row[j] = -1;
         shadowgrid[i] = row;
      }
      
      // build walls
      buildWallAndFloor();
      buildAxisShape();

      state = LOADING_STATE;

      updateCamera();
      
      myscreen.clear();
      
      Element.DOCUMENT.addEventHandler("onkeydown", keydown);
      button.addEventHandler("onclick", onclick);
   }

   EventCallback onclick = new EventCallback() {
         public boolean onevent(Element source, JSObject e) {
            if (state == LOADING_STATE || state == DEAD_STATE) {
               setup();
               gobutton.invokeMethod("blur");
            }
            return false;
         }
      };
	
   // run for e<very reset
   void setup() {
      lights = new double[][]{camera.CAMERA_POINT};
      boxes = new Box[width][width][height];
      for (int i = 0; i < width; i++) {
         for (int j = 0; j < width; j++) {
            for (int k = 0; k < height; k++) {
               boxes[i][j][k] = null;
            }
         }
      }
      
      currentBlock = makeBlock();
      score = 0;
      updateScore.apply(null, JSObject.fromNumber(score));
      droptimeout = 1000;
      showPanel("diepanel", false);
      showPanel("loadpanel", false);
      
      state = PLAYING_STATE;
      
      redraw();
      lastdroptime = currentTimeMillis();
      JSObject.setTimeout(timer, 50);
   }

   private native long currentTimeMillis();
   
   Function timer = new Function() {
         public JSObject apply(JSObject target, JSObject... args) {
            if (state == PLAYING_STATE) {
               long now = currentTimeMillis();
               long delta = (now - lastdroptime);
               if (delta > droptimeout) {
                  if (!tryMove(new int[]{0,0,-1})) {
                     dropBlock();
                  }
                  redraw();
                  lastdroptime = now;
               }
               JSObject.setTimeout(timer, 50);
            } else if (state == FLASHING_STATE) {
               // use lastdroptime as a timer
               long delta = (currentTimeMillis() - lastdroptime);
               if (delta >= 1000) {
                  // done, go back to playing
                  clearLines();
                  currentBlock = makeBlock();
                  currentBlock.setCenters();
                  for (int i = 0; i < currentBlock.boxes.length; i++) {
                     if (!fits(currentBlock.boxes[i].center)) {
                        // dead!
                        die();
                        return null;
                     }
                  }
                  
                  state = PLAYING_STATE;
                  lastdroptime = currentTimeMillis();
                  redraw();
                  JSObject.setTimeout(timer, 50);
               } 
               else {
                  redraw();
                  JSObject.setTimeout(timer, 50);
               }
            }
            return null;
         }
      };
         
   void clearLines() {
      for (int z = 0; z < height; z++) {
         boolean removedany = false;
         for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
               if (boxes[x][y][z]!=null && boxes[x][y][z].flashing) {
                  for (int k = z; k < height-1; k++) {
                     boxes[x][y][k] = boxes[x][y][k+1];
                     if (boxes[x][y][k]!=null) {
                        boxes[x][y][k].center[2]--;
                        boxes[x][y][k].setPosition(new double[]{0,0,0}, squaresize);
                     }
                  }
                  boxes[x][y][height-1] = null;
                  removedany = true;
               }
            }
         }
         if (removedany) z--;
      }
   }

   boolean fits(int[] point) {
      if (!inBounds(point))
         return false;
      return boxes[point[0]][point[1]][point[2]] == null;
   }

   boolean inBounds(int[] point) {
      if (point[0]<0 || point[0]>=width)
         return false;
      if (point[1]<0 || point[1]>=width)
         return false;
      if (point[2]<0 || point[2]>=height)
         return false;
      return true;
   }
   
   boolean tryMove(int[] delta) {
      boolean canMove = true;
      int[] newcenter = {0,0,0};
      for (int i = 0; i < currentBlock.boxes.length; i++) {
         newcenter[0] = delta[0] + currentBlock.boxes[i].center[0];
         newcenter[1] = delta[1] + currentBlock.boxes[i].center[1];
         newcenter[2] = delta[2] + currentBlock.boxes[i].center[2];
         if (!fits(newcenter)) {
            canMove = false;
            break;
         }
      }
      if (canMove) {
         currentBlock.center[0] += delta[0];
         currentBlock.center[1] += delta[1];
         currentBlock.center[2] += delta[2];
      }
      return canMove;
   }
   
   boolean tryRotateXY(boolean which) {
      currentBlock.rotateXY(which);
      currentBlock.setCenters();
      boolean allin = true;
      for (int i = 0; i < currentBlock.boxes.length; i++) {
         if (!fits(currentBlock.boxes[i].center)) {
            allin = false;
            break;
         }
      }
      
      if (!allin)
         currentBlock.rotateXY(!which);
      return allin;
   }


   boolean tryRotateYZ(boolean which) {
      currentBlock.rotateYZ(which);
      currentBlock.setCenters();
      boolean allin = true;
      for (int i = 0; i < currentBlock.boxes.length; i++) {
         if (!fits(currentBlock.boxes[i].center)) {
            allin = false;
            break;
         }
      }
      
      if (!allin)
         currentBlock.rotateYZ(!which);
      return allin;
   }

   void dropBlock() {
      // the current block is now in place, see if it causes lines to clear
      placeBlock(currentBlock);
      for (int i = 0; i < currentBlock.boxes.length; i++) {
         currentBlock.boxes[i].block = null;
      }
      currentBlock = null;

      int xlines = 0;
      int ylines = 0;
      for (int z = 0; z < height; z++) {
         for (int x = 0; x < width; x++) {
            // check whole y line
            boolean gotall = true;
            for (int y = 0; y < width; y++) {
               gotall &= (boxes[x][y][z]!=null);
            }
            if (gotall) {
               ylines++;
               for (int y = 0; y < width; y++) {
                  boxes[x][y][z].flashing = true;
               }
            }
         }

         for (int y = 0; y < width; y++) {
            // check whole x line
            boolean gotall = true;
            for (int x = 0; x < width; x++) {
               gotall &= (boxes[x][y][z]!=null);
            }
            if (gotall) {
               xlines++;
               for (int x = 0; x < width; x++) {
                  boxes[x][y][z].flashing = true;
               }
            }
         }
      }

      if (xlines>0 || ylines>0) {
         // cleared some lines
         score += (xlines+1)*(ylines+1);
         updateScore.apply(null, JSObject.fromNumber(score));
            
         // make bricks fall faster
         droptimeout = 1000-100*(score/100);
         if (droptimeout < 200) droptimeout = 200;

         state = FLASHING_STATE;
         lastdroptime = currentTimeMillis();
         return;
      } else {
         // no cleared lines, make new block
         currentBlock = makeBlock();
         currentBlock.setCenters();
         for (int i = 0; i < currentBlock.boxes.length; i++) {
            if (!fits(currentBlock.boxes[i].center)) {
               // dead!
               die();
               return;
            }
         }
      }
   }

   void die() {
      state = DEAD_STATE;
      redraw();
   }


   EventCallback keydown = new EventCallback() {
         boolean stop(JSObject e) {
            e.invokeMethod("stopPropagation");
            e.invokeMethod("preventDefault");
            return false;
         }

         public boolean onevent(Element source, JSObject e) {
            if (state == PLAYING_STATE) {
               int keynum = e.getProperty("keyCode").asNumber().intValue();
               if (keynum == 37) { // left
                  tryMove(new int[]{-1,0,0});
                  redraw();
                  return stop(e);
               } else if (keynum == 38) { // up
                  tryMove(new int[]{0,1,0});
                  redraw();
                  return stop(e);
               } else if (keynum == 39) { // right
                  tryMove(new int[]{1,0,0});
                  redraw();
                  return stop(e);
               } else if (keynum == 40) { // down
                  tryMove(new int[]{0,-1,0});
                  redraw();
                  return stop(e);
               } else if (keynum == 32) { // inwards
                  lastdroptime = currentTimeMillis();
                  if (!tryMove(new int[]{0,0,-1})) {
                     dropBlock();
                  }
                  redraw();
                  return stop(e);
               } else if (keynum == 90) { // z, rotate
                  tryRotateXY(true);
                  redraw();
                  return stop(e);
               } else if (keynum == 88) { // x, rotate
                  tryRotateYZ(true);
                  redraw();
                  return stop(e);
               } else if (keynum == 65) { // a, rotate camera
                  camera_angle += Math.PI/30;
                  updateCamera();
                  return stop(e);
               } else if (keynum == 83) { // s, rotate camera other way
                  camera_angle -= Math.PI/30;
                  updateCamera();
                  return stop(e);
               }
            }
            return true;
         }
      };
         
   private native double random();

   Block makeBlock() {
      int index = (int)(random()*blocktypes.length);
      return new Block(new int[]{width/2,width/2,height-1}, 
                       blocktypes[index].diffs, 
                       blocktypes[index].color);
   }

   boolean facingCamera(Face face) {
      double[] v1 = Util.newVector();
      double[] v2 = Util.newVector();
      Util.subtract(face.transformed[0],face.transformed[1],v1);
      Util.subtract(face.transformed[2],face.transformed[1],v2);
      Util.cross(v1,v2,v1);
      return Util.dot(v1,new double[]{0,0,1}) >= 0;
   }

   void redrawPlaying() {
      if (currentBlock != null) {
         placeBlock(currentBlock);
         for (int i = 0; i < currentBlock.boxes.length; i++) {
            Box box = currentBlock.boxes[i];
            shadowgrid[box.center[0]][box.center[1]] = box.center[2];
         }
      }
      drawWalls();
      drawAxis();
      drawLaidBoxes(null);
      if (currentBlock != null) {
         unplaceBlock(currentBlock);
         for (int i = 0; i < currentBlock.boxes.length; i++) {
            Box box = currentBlock.boxes[i];
            shadowgrid[box.center[0]][box.center[1]] = -1;
         }
      }
   }
   
   void redrawFlashing() {
      long delta = (currentTimeMillis() - lastdroptime);
      boolean which = ((delta%200) < 100);
      myscreen.clear();
      drawWalls();
      drawAxis();
      if (which) {
         drawLaidBoxes(new Color(230,230,230));
      } else {
         drawLaidBoxes(new Color(230,26,26));
      }
   }

   void redrawLoading() {
      myscreen.clear();
   }

   void redrawDead() {
      showPanel("diepanel", true);
   }

   void showPanel(String id, boolean show) {
      Element panel = Element.getElementById(id);
      panel.getProperty("style").setProperty("visibility", JSObject.fromString(show ? "visible" : "hidden"));
   }
      
   void redraw() {
      if (state == PLAYING_STATE) {
         redrawPlaying();
      }
      else if (state == LOADING_STATE) {
         redrawLoading();
      }
      else if (state == FLASHING_STATE) {
         redrawFlashing();
      }
      else if (state == DEAD_STATE) {
         redrawDead();
      }
   }
   
   void placeBlock(Block block) {
      block.setPosition(new double[]{0,0,0},squaresize);
      for (int i = 0; i < block.boxes.length; i++) {
         int[] c = block.boxes[i].center;
         boxes[c[0]][c[1]][c[2]] = block.boxes[i];
      }
   }
   void unplaceBlock(Block block) {
      for (int i = 0; i < block.boxes.length; i++) {
         int[] c = block.boxes[i].center;
         boxes[c[0]][c[1]][c[2]] = null;
      }
   }

   Comparator<Box> boxComparator = new Comparator<Box>() {
      public int compare(Box box1, Box box2) {
         double[] average1 = {0,0,0};
         double[] average2 = {0,0,0};
         for (int i = 0; i < 8; i++) {
            Util.ax_plus_by(1, average1, 1.0/8, box1.shape.points[i], average1);
            Util.ax_plus_by(1, average2, 1.0/8, box2.shape.points[i], average2);
         }
         double dist1 = Util.distance(average1, camera.CAMERA_POINT);
         double dist2 = Util.distance(average2, camera.CAMERA_POINT);
         if (dist1 > dist2)
            return -1;
         else if (dist1 < dist2)
            return 1;
         else
            return 0;
         /*
         // unreachable
         if (box1.center[2]!=box2.center[2])
            return box1.center[2]-box2.center[2];
         else if (box1.center[0]!=box2.center[0])
            return box2.center[0]-box1.center[0];
         else
            return box2.center[1]-box1.center[1];
         */
      }
   };
   
   // assume all boxes have positions set
   void drawLaidBoxes(Color subcolor) {
      int wid2 = width/2;
      java.util.List<Box> allboxes = new ArrayList<Box>();
      for (int z = 0; z < height; z++) {
         for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
               if (boxes[x][y][z] != null)
                  allboxes.add(boxes[x][y][z]);
            }
         }
      }
      
      Collections.sort(allboxes, boxComparator);
      for (Box box : allboxes) {
         drawBox(box, shadowgrid[box.center[0]][box.center[1]], subcolor);
      }
   }
   
   // does not set position
   void drawBox(Box box, double shadow, Color subcolor) {
      Shape myshape = box.shape;
      for (int j = 0; j < myshape.faces.size(); j++) {
         Face face = myshape.faces.get(j);
         camera.transformFace(face);
         if (facingCamera(face)) {
            double newdiff = (shadow>=0 && j==0 && box.block==null && box.center[2]<shadow) ? 0.01 : diffuse;
            double olddiff = diffuse;
            diffuse = newdiff;
            
            if (subcolor!=null && box.flashing) {
               Color oldcolor = face.color;
               face.color = subcolor;
               myscreen.fillFace(face, doLighting(face));
               face.color = oldcolor;
            } else {
               myscreen.fillFace(face, doLighting(face));
            }
            
            diffuse = olddiff;
            myscreen.drawFace(face, "rgb(0,0,0)");
         }
      }
   }
   
   // assume that the point has already been transformed and translated
   void drawAxis() {
      myscreen.drawLine(axistransformed[1], axistransformed[2], "rgb(0,0,0)");
      myscreen.drawLine(axistransformed[3], axistransformed[4], "rgb(0,0,0)");
      myscreen.drawString(axistransformed[1], "R", "rgb(0,0,0)");
      myscreen.drawString(axistransformed[2], "L", "rgb(0,0,0)");
      myscreen.drawString(axistransformed[3], "U", "rgb(0,0,0)");
      myscreen.drawString(axistransformed[4], "D", "rgb(0,0,0)");
   }
   
   // assume faces already transformed
   void drawWalls() {
      myscreen.clear();
      for (int i = 0; i < wallshape.faces.size(); i++) {
         if (facingCamera(wallshape.faces.get(i))) {
            myscreen.fillFace(wallshape.faces.get(i), doLighting(wallshape.faces.get(i)));
            myscreen.drawFace(wallshape.faces.get(i), "rgb(0,0,0)");
         }
      }
      
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < width; y++) {
            int index = x*width+y;
            double newdiff = (shadowgrid[x][y]>=0 ? 0.01 : diffuse);
            double olddiff = diffuse;
            diffuse = newdiff;
            myscreen.fillFace(floorshape.faces.get(index), doLighting(floorshape.faces.get(index)));
            diffuse = olddiff;
            myscreen.drawFace(floorshape.faces.get(index), "rgb(0,0,0)");
         }
      }
   }
	
   // assume the face has its normal computed
   String doLighting(Face face) {
      double I = 0;
      double[] L = Util.newVector();
      for (int i = 0; i < lights.length; i++) {
         Util.subtract(lights[i], face.parent.points[face.points[0]], L);
         double LN = Util.dot(face.normal, L);
         I += LN*diffuse;
      }
      int red = (int)(face.color.getRed() * I);
      int green = (int)(face.color.getGreen() * I);
      int blue = (int)(face.color.getBlue() * I);
      return "rgb(" + red + "," + green + "," + blue + ")";
   }
}