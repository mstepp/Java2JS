function IntTestCase() {}
IntTestCase.prototype = new TestCase();
IntTestCase.prototype.assertIntEq = function(lhs, rhs, msg) {
    this.assertTrue(lhs.equals(rhs), msg);
};

IntTestCase.prototype.testAdd = function() {
    var a = new Integer(10);
    var b = new Integer(20);
    var c = new Integer(30);
    
    this.assertIntEq(c, a.add(b), "30=10+20");
    this.assertIntEq(b, a.add(a), "20=10+10");
    this.assertIntEq(c, a.add(a).add(a), "30=10+10+10");

    a = new Integer(0xFFFFFFFF);
    b = new Integer(10);
    c = new Integer(9);
    this.assertIntEq(c, a.add(b), "9=-1+10");
};

IntTestCase.prototype.testNot = function() {
    var a = new Integer(0x55555555);
    var b = new Integer(0xAAAAAAAA);
    var c = new Integer(0);
    var d = new Integer(0xFFFFFFFF);
    
    this.assertIntEq(a.not(), b, "~01010101... = 1010101010...");
    this.assertIntEq(a, b.not(), "01010101... = ~1010101010...");
    this.assertIntEq(a.not(), b, "~0 = -1");
    this.assertIntEq(a, b.not(), "0 = ~-1");
};

IntTestCase.prototype.testNegate = function() {
    var a = new Integer(0);
    var b = new Integer(-3);
    var c = new Integer(3);
    var d = new Integer(0x80000000);
    
    this.assertIntEq(a, a.negate(), "0=negate(0)");
    this.assertIntEq(b, c.negate(), "-3=negate(3)");
    this.assertIntEq(b.negate(), c, "negate(-3)=3");
    this.assertIntEq(d, d.negate(), "-2^32 = negate(-2^32)");
};

new IntTestCase().run();