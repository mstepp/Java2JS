package lambda;

public class Expression {
   public enum Type {
      VARIABLE,
         APPLICATION,
         LAMBDA;
   }

   private Type type;
   ////////////////////
   private int index;
   ////////////////////
   private Expression lhs, rhs;
   ////////////////////
   private Expression child;

   public Expression(int _index) {
      this.type = Type.VARIABLE;
      this.index = _index;
   }
   public Expression(Expression _lhs, Expression _rhs) {
      this.type = Type.APPLICATION;
      this.lhs = _lhs;
      this.rhs = _rhs;
   }
   public Expression(Expression _child) {
      this.type = Type.LAMBDA;
      this.child = _child;
   }

   public Type type() {return this.type;}
   public int index() {
      if (!this.type.equals(Type.VARIABLE))
         throw new UnsupportedOperationException();
      return this.index;
   }
   public Expression child() {
      if (!this.type.equals(Type.LAMBDA))
         throw new UnsupportedOperationException();
      return this.child;
   }
   public Expression lhs() {
      if (!this.type.equals(Type.APPLICATION))
         throw new UnsupportedOperationException();
      return this.lhs;
   }
   public Expression rhs() {
      if (!this.type.equals(Type.APPLICATION))
         throw new UnsupportedOperationException();
      return this.rhs;
   }

   public boolean isNormal() {
      switch (this.type) {
      case APPLICATION:
         if (this.lhs.type.equals(Type.LAMBDA)) {
            return false;
         } else {
            return this.lhs.isNormal() && this.rhs.isNormal();
         }

      case LAMBDA:
         return this.child.isNormal();

      case VARIABLE:
         return true;

      default:
         throw new RuntimeException("Unexpected type: " + this.type);
      }
   }

   // only inc free variables
   private Expression inc(int offset, int inside) {
      if (offset == 0)
         return this;
      switch (this.type) {
      case VARIABLE:
         if (this.index <= inside) {
            return this;
         } else {
            return new Expression(this.index + offset);
         }
	    
      case APPLICATION: {
         Expression newlhs = this.lhs.inc(offset, inside);
         Expression newrhs = this.rhs.inc(offset, inside);
         if (newlhs == this.lhs && newrhs == this.rhs) {
            return this;
         } else {
            return new Expression(newlhs, newrhs);
         }
      }

      case LAMBDA: {
         Expression newchild = this.child.inc(offset, inside + 1);
         if (newchild == this.child)
            return this;
         else
            return new Expression(newchild);
      }

      default:
         throw new RuntimeException("Invalid type: " + this.type);
      }
   }

   private Expression subst(int depth, Expression replace) {
      switch (this.type) {
      case VARIABLE:
         if (this.index == depth + 1) {
            return replace.inc(depth, 0);
         } else if (this.index > depth+1) {
            // free variable
            return new Expression(this.index - 1);
         } else {
            return this;
         }
	    
      case LAMBDA:
         return new Expression(this.child.subst(depth + 1, replace));

      case APPLICATION:
         return new Expression(this.lhs.subst(depth, replace), this.rhs.subst(depth, replace));

      default:
         throw new RuntimeException("Invalid type: " + this.type);
      }
   }

   private void copy(Expression other) {
      this.type = other.type;
      this.index = other.index;
      this.lhs = other.lhs;
      this.rhs = other.rhs;
      this.child = other.child;
   }

   public boolean eval() {
      switch (this.type) {
      case APPLICATION:
         if (this.lhs.type.equals(Type.LAMBDA)) {
            // redex!
            Expression result = this.lhs.child.subst(0, this.rhs);
            this.copy(result);
            return true;
         } else {
            return this.lhs.eval() || this.rhs.eval();
         }

      case LAMBDA:
         return this.child.eval();

      case VARIABLE:
         return false;

      default:
         throw new RuntimeException("Invalid type: " + this.type);
      }
   }

   public void dump(Printer printer) {
      switch (this.type) {
      case APPLICATION:
         this.lhs.dump(printer);
         printer.printf(" ");
         if (this.rhs.type.equals(Type.APPLICATION)) {
            printer.printf("(");
            this.rhs.dump(printer);
            printer.printf(")");
         } else {
            this.rhs.dump(printer);
         }
         break;

      case LAMBDA:
         printer.printf("(\\ ");
         this.child.dump(printer);
         printer.printf(")");
         break;

      case VARIABLE:
         printer.printf("%s", this.index);
         break;

      default:
         throw new RuntimeException("Invalid type: " + this.type);
      }
   }
}
