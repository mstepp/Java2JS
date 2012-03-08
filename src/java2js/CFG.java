package java2js;

import org.apache.bcel.generic.*;
import java.util.*;

public class CFG {
   private Block startBlock;
   private final InstructionList instructionList;
   private final List<Block> blocks = new ArrayList<Block>();
   private final Map<InstructionHandle,Block> start2block = 
      new HashMap<InstructionHandle,Block>();

   public static class ExceptionTarget {
      public final ObjectType type;
      public final Block target;
      public ExceptionTarget(ObjectType _type, Block _target) {
         this.type = _type;
         this.target = _target;
      }
   }
   
   public class Block {
      private final List<Block> successors = new ArrayList<Block>();
      private final InstructionHandle start, end;
      private final List<ExceptionTarget> exceptions = new ArrayList<ExceptionTarget>();
      private final boolean hasTerminator;
      
      Block(InstructionHandle _start, InstructionHandle _end) {
         this.start = _start;
         this.end = _end;
         this.hasTerminator = isTerminator(end.getInstruction());
      }
      public boolean hasTerminator() {return this.hasTerminator;}
      public InstructionHandle getStart() {return this.start;}
      public InstructionHandle getEnd() {return this.end;}
      public List<ExceptionTarget> exceptions() {return this.exceptions;}
   }

   public CFG(InstructionList list, CodeExceptionGen[] exceptions) {
      this.instructionList = list;
      this.build(exceptions);
   }
   
   public Block getStart() {
      return this.startBlock;
   }

   public List<Block> getBlocks() {
      return this.blocks;
   }

   public Block getBlockFromStart(InstructionHandle start) {
      return this.start2block.get(start);
   }

   private boolean isTerminator(Instruction inst) {
      return inst instanceof BranchInstruction ||
         inst instanceof ReturnInstruction ||
         inst instanceof ATHROW ||
         inst instanceof RET;
   }

   private void build(CodeExceptionGen[] exceptions) {
      // find the start and end of the blocks
      Set<InstructionHandle> starts = new HashSet<InstructionHandle>();

      starts.add(this.instructionList.getStart());

      // add handlers
      for (CodeExceptionGen ceg : exceptions) {
         starts.add(ceg.getHandlerPC());
         starts.add(ceg.getStartPC());
         starts.add(ceg.getEndPC().getNext());
      }

      Set<CodeExceptionGen> inside = new HashSet<CodeExceptionGen>();
      for (InstructionHandle curr = this.instructionList.getStart(); curr != null; curr = curr.getNext()) {
         Instruction inst = curr.getInstruction();
         if (inst instanceof GotoInstruction) {
            starts.add(curr.getNext());
            starts.add(((GotoInstruction)inst).getTarget());
         } else if (inst instanceof IfInstruction) {
            starts.add(curr.getNext());
            starts.add(((IfInstruction)inst).getTarget());
         } else if (inst instanceof JsrInstruction) {
            starts.add(curr.getNext());
            starts.add(((JsrInstruction)inst).getTarget());
         } else if (inst instanceof Select) {
            Select select = (Select)inst;
            starts.add(select.getTarget());
            for (InstructionHandle target : select.getTargets()) 
               starts.add(target);
         } else if (inst instanceof ReturnInstruction) {
            starts.add(curr.getNext());
         } else if (inst instanceof ATHROW) {
            starts.add(curr.getNext());
         } else if (inst instanceof RET) {
            starts.add(curr.getNext());
         }
      }

      // now build blocks (not successors)
      InstructionHandle start = null;
      for (InstructionHandle curr = this.instructionList.getStart(); curr != null; curr = curr.getNext()) {
         if (starts.contains(curr)) {
            if (start != null) {
               Block block = new Block(start, curr.getPrev());
               this.blocks.add(block);
               this.start2block.put(start, block);
            }
            start = curr;
         }
      }
	
      // fill in final block
      if (start != null) {
         Block block = new Block(start, this.instructionList.getEnd());
         this.blocks.add(block);
         this.start2block.put(start, block);
      }

      // set start block
      this.startBlock = this.blocks.get(0);

      // add exceptions to blocks
      for (CodeExceptionGen ceg : exceptions) {
         InstructionHandle curr = ceg.getStartPC();
         while (true) {
            if (start2block.containsKey(curr)) {
               Block handlerBlock = start2block.get(ceg.getHandlerPC());
               if (handlerBlock == null)
                  throw new RuntimeException("Handler does not start block");
               start2block.get(curr).exceptions.add(new ExceptionTarget(ceg.getCatchType(), handlerBlock));
            }
            if (curr == ceg.getEndPC())
               break;
            curr = curr.getNext();
         }
      }
   }
}
