var require = (function() {

	// Cache to store executed modules public API.
	var cache = {};
	
	// Function that resolves absolute & relative modules paths.
	var resolve = function(module, path) {
		if(path[0] === "/") {
			return path.substr(1);
		}
		
		var regexp = /[0-9a-zA-Z_]+\/\.\.\//;
		var name = module + '/' + path;
		
		// Recursive path's normalization.
		while(name.indexOf('../') > -1) {
			name = name.replace(regexp, '');
		}
		return name;
	};
	
	// Function that requires modules specified by absolute or relative path.
	var require = function(module, path) {
		var name = resolve(module, path);
		
		// Check if module was execute & returns its public API.
		if(cache.hasOwnProperty(name)) {
		
			// Fail if required module is currently being evaluated.
			if(cache[name] === -1) {
				throw new Error('Cyclic dependency found when requiring: ' + name);
			}
			return cache[name];
		}
		
		// Check if module function exists.
		if(modules[name] == null) {
			throw new Error('Module not found: ' + name);
		}
		
		cache[name] = -1;
		
		// Executes module & caches its public API.
		var base = name.substr(0, name.lastIndexOf('/'));
		return cache[name] = modules[name](function(path) {
			return require(base, path);
		});
	};
	return function(path) {
		return require('', path);
	};
})();

var modules = {};
modules["simple/app"] = function(require) {

}
require('/simple/app');