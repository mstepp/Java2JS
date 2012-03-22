function Runner(cont) {
   this.current = cont;
}
Runner.prototype.step = function() {
   if (this.current) {
      this.current = this.current();
   }
};
Runner.prototype.runToCompletion = function(callback) {
   while (this.current) {
      this.current = this.current();
   }
   if (callback) 
      callback();
};
