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
modules["source/math"] = function(require) {
  exports.add = function(a, b) {
    return a + b;
  };
}
modules["example/app"] = function(require) {
  var math;
  
  math = require('/source/math');
  
  console.log('Working example project 12');
  
  console.log(math.add(Math.random(), 10));
}
modules["source/main_area:example"] = function(require) {
  var node    = require('/source/view/action');
  var execute = require('/source/view/registry').execute;
  var math = require('/source/Math');
  var app = require('/example/App');
  
  var n = function() {
    arr = []
    arr.push(new action("div", 'aa', (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new action("p", 'aa', (function() {
        return 'Hello World!';
      }), (function() {
        arr = [];
        return arr;
      }), this));
      return arr;
    }), this));
    return arr;
  }
  return execute('body', n);
}
modules["source/main_area:main_area"] = function(require) {
  var node    = require('/source/view/action');
  var execute = require('/source/view/registry').execute;
  var math = require('/source/Math');
  var app = require('/example/App');
  
  var n = function() {
    arr = []
    var __hasProp = Object.prototype.hasOwnProperty, __indexOf = Array.prototype.indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (__hasProp.call(this, i) && this[i] === item) return i; } return -1; };
    
    arr.push(new action("div", 'aa', (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new action("header", 'aa', (function() {}), (function() {
        arr = [];
        arr.push(new action("p", 'aa', (function() {
          return 'Hello world';
        }), (function() {
          arr = [];
          return arr;
        }), this));
        arr.push(new action("p", 'ab', (function() {
          return 'Here is some additional info';
        }), (function() {
          arr = [];
          return arr;
        }), this));
        arr.push(new action("date", 'ac', (function() {
          return '29.08.2011';
        }), (function() {
          arr = [];
          return arr;
        }), this));
        arr.push(new action("script", 'ad', (function() {
          var hash;
          return hash = {
            fafa: 'gaga',
            haha: 'papa'
          };
        }), (function() {
          arr = [];
          return arr;
        }), this));
        arr.push(new action("if", 'ae', (function() {
          return user.happy != null;
        }), (function() {
          arr = [];
          arr.push(new action("p", 'aa', (function() {
            "Hello world";
            "that: I'm anything";        return "- I have to come see u!";
          }), (function() {
            arr = [];
            return arr;
          }), this));
          return arr;
        }), this));
        arr.push(new action("for", 'af', (function() {
          return __indexOf.call(user.fleets, fleet) >= 0;
        }), (function() {
          arr = [];
          arr.push(new action("p", 'aa', (function() {
            return fleet.name;
          }), (function() {
            arr = [];
            return arr;
          }), this));
          arr.push(new action("p", 'ab', (function() {
            return fleet.get_description();
          }), (function() {
            arr = [];
            return arr;
          }), this));
          arr.push(new action("", 'ac', (function() {
            return 'dada';
          }), (function() {
            arr = [];
            return arr;
          }), this));
          arr.push(new action("", 'ad', (function() {
            return 'fafa';
          }), (function() {
            arr = [];
            return arr;
          }), this));
          arr.push(new action("", 'ae', (function() {
            return 'gaga';
          }), (function() {
            arr = [];
            return arr;
          }), this));
          return arr;
        }), this));
        return arr;
      }), this));
      return arr;
    }), this));
    
    arr.push(new action("footer", 'ab', (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new action("args", 'aa', (function() {}), (function() {
        arr = [];
        arr.push(new action("logged", 'aa', (function() {
          return as(user.logged);
        }), (function() {
          arr = [];
          return arr;
        }), this));
        return arr;
      }), this));
      arr.push(new action("p", 'ab', (function() {
        if (logged > 0) return 'Logged';
        return 'Unlogged';
      }), (function() {
        arr = [];
        return arr;
      }), this));
      arr.push(new action("p", 'ac', (function() {
        return user.name;
      }), (function() {
        arr = [];
        return arr;
      }), this));
      arr.push(new action("if", 'ad', (function() {
        return user.logging_of;
      }), (function() {
        arr = [];
        arr.push(new action("p", 'aa', (function() {
          return 'goodbye';
        }), (function() {
          arr = [];
          return arr;
        }), this));
        return arr;
      }), this));
      return arr;
    }), this));
    return arr;
  }
  return function() { return registry.create('pfc-body', n); };
}
require('/example/app');