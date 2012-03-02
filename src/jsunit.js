function TestCase() {}
TestCase.prototype.run = function() {
    this.setUp();
    for (var func in this) {
	if (this[func] instanceof Function &&
	    func.length >= 4 && func.substring(0,4) == "test") {
	    try {
		this[func]();
		alert("Test " + func + " passed!");
	    } catch (e) {
		alert("Test " + func + " failed: " + e);
	    }
	}
    }
    this.tearDown();
};
TestCase.prototype.setUp = function() {
    // do nothing
};
TestCase.prototype.tearDown = function() {
    // do nothing
};
TestCase.prototype.assertEquals = function(lhs, rhs, msg) {
    if (lhs != rhs) {
	throw "Assertion failed: " + lhs + " == " + rhs + (msg ? "; " + msg : "");
    }
};
TestCase.prototype.assertTrue = function(test, msg) {
    if (!test) {
	throw "Assertion failed" + (msg ? ": " + msg : "");
    }
};
TestCase.prototype.assertFalse = function(test, msg) {
    if (test) {
	throw "Assertion failed" + (msg ? ": " + msg : "");
    }
};