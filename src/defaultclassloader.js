function DefaultURLClassLoader(prefix) {
   this.prefix = prefix;
}
DefaultURLClassLoader.prototype = new URLClassLoader();
DefaultURLClassLoader.prototype.getClassURL = function(classname) {
   return this.prefix + "?class=" + classname;
};
