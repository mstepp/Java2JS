function URLClassLoader() {
   this.document = null;
}
URLClassLoader.prototype.setDocument = function(document) {
   this.document = document;
};
URLClassLoader.prototype.getClassURL = function(classname) {
   throw "This is an abstract method that must be overloaded by subclasses";
};
URLClassLoader.prototype.loadClass = function(classname, callback) {
   if (this.document == null)
      throw "No document specified";

   var fullURL = this.getClassURL(classname);
   var head = this.document.getElementsByTagName("head")[0];
   var script = this.document.createElement("SCRIPT");
   script.type = "text/javascript";
   script.src = fullURL;
   if (callback)
      script.onload = callback;
   head.appendChild(script);
};
