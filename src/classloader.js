function URLClassLoader() {
   this.document = null;
   this.logger = null;
}
URLClassLoader.prototype.setDocument = function(document) {
   this.document = document;
};
URLClassLoader.prototype.setLogger = function(logger) {
   this.logger = logger;
};
URLClassLoader.prototype.getClassURL = function(classname) {
   throw "This is an abstract method that must be overloaded by subclasses";
};
URLClassLoader.prototype.loadClass = function(classname) {
   if (this.document == null)
      return false;

   var fullURL = this.getClassURL(classname);
   fullURL = fullURL.replace(/[$]/gi, "\\$");
   var xhr = new XMLHttpRequest();
   xhr.open("GET", fullURL, false);
   xhr.send(null);
   if (xhr.readyState != 4 || xhr.status != 200) {
      return false;
   }
   var code = xhr.responseText;
   var head = this.document.getElementsByTagName("head")[0];
   var script = this.document.createElement("SCRIPT");
   script.type = "text/javascript";
   script.appendChild(this.document.createTextNode(code));
   head.appendChild(script);
   if (this.logger)
      this.logger("Loaded " + classname);
   return true;
};
URLClassLoader.prototype.loadClassCPS = function(classname, cont) {
   if (this.document == null)
      throw "No document specified";

   var fullURL = this.getClassURL(classname);
   var head = this.document.getElementsByTagName("head")[0];
   var script = this.document.createElement("SCRIPT");
   script.type = "text/javascript";
   script.src = fullURL;
   script.onload = function() {
      // this callback resumes the entire JVM!
      var runner = new Runner(cont);
      runner.runToCompletion();
   };

   head.appendChild(script);
   return null;
   // cut point! final continuation in this sequence
};

////////////////////////////////////////

function DefaultURLClassLoader(prefix) {
   this.prefix = prefix;
}
DefaultURLClassLoader.prototype = new URLClassLoader();
DefaultURLClassLoader.prototype.getClassURL = function(classname) {
   return this.prefix + "?class=" + classname;
};
