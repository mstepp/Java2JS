function Integer(bits) {
    this.bits = bits | 0;
}
Integer.ONE = new Integer(1);
Integer.ZERO = new Integer(0);
Integer.NEGATIVE_ONE = new Integer(0xFFFFFFFF);
Integer.MAX_VALUE = new Integer(0x7FFFFFFF);
Integer.MIN_VALUE = new Integer(0x80000000);
Integer.prototype.toHex = 
    (function() {
	var HEX = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'];
	return function() {
	    var result = "";
	    for (var i = 0; i < 8; i++) {
		var tempbits = (this.bits >>> (i*4)) & 0xF;
		result = HEX[tempbits] + result;
	    }
	    return result;
	};
    })();
Integer.prototype.toJS = function() {
    return this.bits | 0;
};
Integer.prototype.isTrue = function() {
    return this.bits == 1;
};
Integer.prototype.isFalse = function() {
    return this.bits == 0;
};
Integer.prototype.i2l = function() {
    if (this.isNegative()) {
	return new Long(this.bits, 0xFFFFFFFF);
    } else {
	return new Long(this.bits, 0);
    }
};
Integer.prototype.i2c = function() {
    return new Integer(this.bits & 0x0000FFFF);
};
Integer.prototype.i2d = function() {
    return this.toJS();
};
Integer.prototype.i2b = function() {
    if (this.isNegative()) {
	return new Integer(0xFFFFFF00 | (this.bits & 0xFF));
    } else {
	return new Integer(this.bits & 0xFF);
    }
};
Integer.prototype.i2s = function() {
    if (this.isNegative()) {
	return new Integer(0xFFFF000 | (this.bits & 0xFFFF));
    } else {
	return new Integer(this.bits & 0xFFFF);
    }
};
Integer.prototype.equals = function(other) {
    return this.bits == other.bits;
};
Integer.prototype.isNegative = function() {
    return (this.bits & 0x80000000) != 0;
};
Integer.prototype.isZero = function() {
    return this.bits == 0;
};
Integer.prototype.isOdd = function() {
    return (this.bits & 1) != 0;
};
Integer.prototype.lt = function(other) {
    return this.compareTo(other) < 0;
};
Integer.prototype.lte = function(other) {
    return this.compareTo(other) <= 0;
};
Integer.prototype.gt = function(other) {
    return this.compareTo(other) > 0;
};
Integer.prototype.gte = function(other) {
    return this.compareTo(other) >= 0;
};
Integer.prototype.notEquals = function(other) {
    return this.compareTo(other) != 0;
};
Integer.prototype.compareTo = function(other) {
    // can use direct numeric comparison because of bits|0
    if (this.bits == other.bits)
	return 0;
    else if (this.bits > other.bits) 
	return 1;
    else
	return -1;
};
Integer.prototype.not = function() {
    return new Integer(this.bits ^ 0xFFFFFFFF);
};
Integer.prototype.inc = function() {
    if (this.bits == (0xFFFFFFFF | 0))
	return Integer.ZERO;
    else
	return new Integer(this.bits + 1);
};
Integer.prototype.negate = function() {
    return this.not().inc();
};
Integer.prototype.add = function(other) {
    var a16 = (this.bits >>> 16) & 0xFFFF;
    var a00 = this.bits & 0xFFFF;
    var b16 = (other.bits >>> 16) & 0xFFFF;
    var b00 = other.bits & 0xFFFF;

    var c16 = 0, c00 = 0;
    c00 += a00 + b00;
    c16 += (c00 >>> 16) & 0xFFFF;
    c00 &= 0xFFFF;
    c16 += a16 + b16;
    c16 &= 0xFFFF;

    return new Integer(c00 | (c16<<16));
};
Integer.prototype.subtract = function(other) {
    return this.add(other.negate());
};
Integer.prototype.and = function(other) {
    return new Integer(this.bits & other.bits);
};
Integer.prototype.or = function(other) {
    return new Integer(this.bits | other.bits);
};
Integer.prototype.xor = function(other) {
    return new Integer(this.bits ^ other.bits);
};
Integer.prototype.shl = function(amt) {
    // amt is an Integer
    amt = amt.bits & 31;
    return new Integer(this.bits << amt);
};
Integer.prototype.ushr = function(amt) {
    // amt is an Integer
    amt = amt.bits & 31;
    return new Integer(this.bits >>> amt);
};
Integer.prototype.shr = function(amt) {
    amt = amt.bits & 31;
    return new Integer(this.bits >> amt);
};
Integer.prototype.multiply = function(other) {
    // shift-and-add
    var result = Integer.ZERO;
    var addend = this;
    for (var i = 0; i < 32; i++) {
	if ((other.bits & (1<<i)) != 0) {
	    result = result.add(addend);
	}
	addend = addend.shl(Integer.ONE);
    }
    return result;
};
Integer.prototype.divmod = function(other) {
    Util.assertWithException(!other.equals(Integer.ZERO), Util.resolveClass("java.lang.ArithmeticException"));

    if (other.equals(Integer.MIN_VALUE)) {
	if (this.equals(other)) {
	    return {div:Integer.ONE, mod:Integer.ZERO};
	} else {
	    return {div:Integer.ZERO, mod:this};
	}
    }

    if (this.isNegative()) {
	if (other.lt(this) ||
	    (!other.isNegative() && other.negate().lt(this))) {
	    return {div:Integer.ZERO, mod:this};
	}
    } else {
	if (other.gt(this) ||
	    (other.isNegative() && other.negate().gt(this))) {
	    return {div:Integer.ZERO, mod:this};
	}
    }

    var div;
    var prod;
    function iterate(toofar) {
	var newdiv = div.shl(Integer.ONE);
	var newprod = prod.shl(Integer.ONE);
	if (toofar(newprod))
	    return false;
	prod = newprod;
	div = newdiv;
	return true;
    }

    var toofar;
    var that = this;

    if (this.isNegative()) {
	toofar = function(prod) {
	    return !prod.isNegative() || prod.lt(that);
	};
	if (other.isNegative()) {
	    div = Integer.ONE;
	} else {
	    div = Integer.NEGATIVE_ONE;
	}
    } else {
	toofar = function(prod) {
	    return prod.isNegative() || prod.gt(that);
	};
	if (other.isNegative()) {
	    div = Integer.NEGATIVE_ONE;
	} else {
	    div = Integer.ONE;
	}
    }
    prod = div.multiply(other);

    while (iterate(toofar)) {}

    var recurse = this.subtract(prod).divmod(other);
    return {div:div.add(recurse.div), mod:recurse.mod};
};
Integer.prototype.toString = function() {
    return "" + (this.bits | 0);
};
