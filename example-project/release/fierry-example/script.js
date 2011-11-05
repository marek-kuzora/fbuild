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
modules['fierry/app'] = function(require) {

}
modules['fierry-example/app'] = function(require) {
  var math;
  
  math = require('fierry/math');
  
  console.log('Working example project 12');
  
  console.log(math.add(Math.random(), 10));
}
modules['fierry/math'] = function(require) {
  var math;
  
  return math = {
    add: function(a, b) {
      return a + b;
    }
  };
}
modules['fierry/main_area:example'] = function(require) {
  var roots  = require('/fierry/view/roots');
  var action = require('/fierry/view/action');
  var math = require('/source/Math');var app = require('/example/App');var _require_2 = require('/fierry/dom/p');var _require_1 = require('/fierry/dom/div');var _require_0 = require('/fierry/dom/body');
  
  
  var n0 = function() { return []; };
  var n = function() {
    arr = []
    arr.push(new action("div", 'aa', this, _require_1, (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new action("p", 'aa', this, _require_2, (function() {
        return 'Hello World!';
      }), (function() {
        arr = [];
        return arr;
      })));
      return arr;
    })));
    return arr;
  }
  return roots.execute('body', _require_0, n);
}
modules['fierry/main_area:main_area'] = function(require) {
  var roots  = require('/fierry/view/roots');
  var action = require('/fierry/view/action');
  var math = require('/source/Math');var app = require('/example/App');var _require_2 = require('/fierry/dom/p');var _require_1 = require('/fierry/dom/div');var _require_0 = require('/pfc-fierry/dom/body');
  
  
  var n0 = function() { return []; };
  var n = function() {
    arr = []
    arr.push(new action("div", 'aa', this, _require_1, (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new action("div", 'aa', this, _require_1, (function() {}), (function() {
        var fleet, _ref;
        arr = [];
        arr.push(new action("p", 'aa', this, _require_2, (function() {
          return 'Hello world';
        }), (function() {
          arr = [];
          return arr;
        })));
        arr.push(new action("p", 'ab', this, _require_2, (function() {
          return 'Here is some additional info';
        }), (function() {
          arr = [];
          return arr;
        })));
        arr.push(new action("p", 'ac', this, _require_2, (function() {
          return '29.08.2011';
        }), (function() {
          arr = [];
          return arr;
        })));
        if (user.happy != null) {
          arr.push(new action("p", 'ad', this, _require_2, (function() {
            return "Hello world" + "that: I'm anything" + "- I have to come see u!";
          }), (function() {
            arr = [];
            return arr;
          })));
        }
        for (fleet = 0, _ref = user.fleets.length; 0 <= _ref ? fleet <= _ref : fleet >= _ref; 0 <= _ref ? fleet++ : fleet--) {
          arr.push(new action("p", 'ae' + math.uid(fleet) + 'aa', this, _require_2, (function() {
            return fleet.name;
          }), (function() {
            arr = [];
            return arr;
          })));
          arr.push(new action("p", 'ae' + math.uid(fleet) + 'ab', this, _require_2, (function() {
            return fleet.get_description();
          }), (function() {
            arr = [];
            return arr;
          })));
        }
        return arr;
      })));
      return arr;
    })));
    
    arr.push(new action("div", 'ab', this, _require_1, (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new action("p", 'aa', this, _require_2, (function() {
        if (logged > 0) return 'Logged';
        return 'Unlogged';
      }), (function() {
        arr = [];
        return arr;
      })));
      arr.push(new action("p", 'ab', this, _require_2, (function() {
        return user.name;
      }), (function() {
        arr = [];
        return arr;
      })));
      if (user.logging_of) {
        arr.push(new action("p", 'ac', this, _require_2, (function() {
          return 'goodbye';
        }), (function() {
          arr = [];
          return arr;
        })));
      }
      return arr;
    })));
    return arr;
  }
  return function() { return roots.execute_raw('pfc-body', _require_0, n); };
}
require('/fierry-example/app');