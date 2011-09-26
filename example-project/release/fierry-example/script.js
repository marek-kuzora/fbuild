var require = (function() {

	var cache = {};
	var require = function(name) {
		if(cache.hasOwnProperty(name)) {
			if(cache[name] === -1) {
				throw new Error('Cyclic dependency found when requiring: ' + name);
			}
			return cache[name];
		}
		cache[name] = -1;
	
		if(modules[name] == null) {
			throw new Error('Module not found: ' + name);
		}
		
		var exports = {};
		var results = modules[name](require, exports);	// what if modules[name] is undefined?

		return cache[name] = results != null ? results : exports;
	};
	return require;
})();

var run = function(name) {
    require(name); // doesn't return exports
};

var modules = {};
modules['source/Math'] = function(require, exports) {
exports.add = function(a, b) {
  return a + b;
};

}
var math;
math = require('source/Math');
console.log('Working example project 12');
console.log(math.add(Math.random(), 10));

