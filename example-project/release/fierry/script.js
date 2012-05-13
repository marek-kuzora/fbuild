/**
 * The Fierry Framework.
 *
 * Copyright 2012, Marek Kuzora.
 */

// Forcing strict mode.
"use strict";



var F = (function() {
  
  // Local copy of the fierry variable.
  var F = {};



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * 
   * Modules management system. Enables client to encapsulate their
   * code into seperate modules that can require each other via
   * accessor functions.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  

  // Associative array storing modules definitions.
  F.modules_def_ = {};

  // Associative array storing 'static modules' public API.  
  F.static_cache_ = {};

  // Associative array storing 'other modules' public API.
  F.modules_cache_;

  // Regex for validating the reserved namespace prefix.
  F.reserved_ns_prefix_ = /^\?!/;

  // Global time counter. Used to determine if cached modules are 
  // up-to-date. Important events will increment the counter and 
  // mark all modules for reload.
  F.global_time_ = 0;


  /**
   * Registers module definition. Module name is required not to start
   * with "?!" as it is a reserved prefix for scope-dependent cache
   * mechanism.
   *
   * @param name    {String}    module name.
   * @param module  {function}  module definition.
   */
  F.register_module = function(name, module) {
    
    // Assert that module name doesn't start with '?!'.
    if(F.reserved_ns_prefix_.test(name)) {
      throw new Error('Module name is required not to start with "?!".');
    }

    // Register the module.
    F.modules_def_[name] = module;
  };


  // Initial time for module not yet loaded.
  F.module_unloaded_time_ = -1;


  /**
   * Return accessor for the statefull module. Accessor is a function
   * that, when invoked will execute return the module public API.
   * Module will be lazily executed when the accessor is first invoked.
   *
   * @param name  {String}    module name.
   * @return      {function}  module accessor.
   */
  F.require = function(name) {
    var module, time = F.module_unloaded_time_;

    return function() {
      
      // Check if module is not up-to-date.
      if(time < F.global_time_) {
        // Update copy of the global time counter.
        time = F.global_time_;

        // Reload the module from cache.
        module = F.hard_require_(F.modules_cache_, name);
      }
      return module;
    };
  };


  /**
   * Returns public API for the stateless module (e.g. class
   * definition, set of functions). The module will be executed 
   * if it has never been required.
   *
   * Note that this function maintains its own cache mechanism for
   * storing already executed modules. Therefore a single module
   * should only be required using one of the provided requiring
   * methods, never by both.
   *
   * @param name  {String}  module name.
   * @return      {*}       module public API.
   */
  F.srequire = function(name) {
    return F.hard_require_(F.static_cache_, name);
  };


  /**
   * Requires standard module via accessor & immediately accesses it.
   * Enables clients to run selected modules one time only for their
   * side effects on the application state.
   *
   * @param name  {String}  module name
   * @return      {*}
   */
  F.run = function(name) {
    return F.require(name)();
  };


  // Substitute for module being evaluated.
  F.evaluated_module_ = new Object();

  // Substitute for module with undefined public API.
  F.undefined_module_ = new Object();
  
  // Substitute for module with null public API.
  F.null_module_ = new Object();


  /**
   * Returns module public API from a provided cache. If no entry is
   * found, executed the module definition and caches its API for 
   * the later use.
   *
   * @param cache {Object}  modules cache.
   * @param name  {String}  module name.
   * @return      {*}
   */
  F.hard_require_ = function(cache, name) {
    
    // Local copy of module public API.
    var module = cache[name];

    // Check if module was executed & returns its public API.
    if(module !== undefined) {

      // Fail if required module is currently being evaluated.
      if(module === F.evaluated_module_) {
        throw new Error('Cyclic dependency when requiring: ' + name);
      }

      // Return undefined if substitute for undefined was found.
      if(module === F.undefined_module_) {
        return undefined;
      }

      // Return null if substitute for null was found.
      if(module === F.null_module_) {
        return null;
      }
      //console.log(name, module)    
      return module;
    }

    // Check if module function exists.
    if(!F.modules_def_[name]) {
      throw new Error('Module not found: ' + name);
    }
    
    // Mark that module is currently being evaluated.
    cache[name] = F.evaluated_module_;

    // Execute module & caches its public API.
    cache[name] = F.modules_def_[name]();

    // Replace undefined API with a temporary substitute. 
    if(cache[name] === undefined) {
      return cache[name] = F.undefined_module_;
    }

    // Replace null API with a temporary substitute. 
    if(cache[name] === null) {
      return cache[name] = F.null_module_;
    }

    return cache[name];
  };



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * Hierarchy Scopes.
   *
   * A mechanism that provides modules with a strong encapsulation
   * along with managed setup & teardown. Enables client to create or
   * discard stack-based scopes containg loaded & active modules. 
   *
   * This greatly simplifies any kind of testing, as clients have now
   * full control over the existing environment - different modules
   * can be loaded with different tests, no depedency whatsoever 
   * between two different tests.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



  // Hierarchy Scopes stack. Each scope represents an associative
  // array storing 'other modules' public API.
  F.hierarchy_scopes_ = [{}];

  // Update copy of the latest hierarchy scope.
  F.modules_cache_ = F.hierarchy_scopes_[0];


  /**
   * Adds new scope on top of the existing Hierarchy Scopes. Modules
   * loaded using the standard require method will become unaccessible
   * as well as the data they provide.
   *
   * This allows clients to run simultaneously multiple fierry 
   * applications - each one with its own state. The feature is used
   * mainly for testing - where each test runs as a separate fierry
   * application.
   */
  F.push_scope = function() {
    F.hierarchy_scopes_.push({});

    // Update copy of the latest hierarchy scope.
    F.modules_cache_ = F.hierarchy_scopes_[F.hierarchy_scopes_.length - 1];

    // Increment global time counter.
    F.global_time_++;
  };


  /**
   * Removes topmost scope from the existing Hierarchy Scopes. Each
   * module from that scope will be cleaned up using the __cleanup__
   * method if defined. All modules from the previously active scope
   * will become accessible. This method will throw an error if only
   * one scope remains.
   */
  F.pop_scope = function() {

    // Assert that there is more than one hierarchy scope.
    if(F.hierarchy_scopes_.length === 1) {
      throw new Error("Cannot discard the application modules cache.");
    }
  
    // Remove top scope from the stack.
    F.hierarchy_scopes_.pop();

    // Retrieve the global variables cache.
    var globals = F.get_hierarchy_cache(F.globals_cache_key_);

    // Restore global variables.
    for(var name in globals) {
      F.unset_global(name);
    }
    
    // Cleanup each removed module if able.
    for (var name in F.modules_cache_) {
      var module = F.modules_cache_[name];

      if(module && typeof module.__cleanup__ === 'function') {
        module.__cleanup__()
      }
    }

    //  Update copy of the latest hierarchy scope.
    F.modules_cache_ = F.hierarchy_scopes_[F.hierarchy_scopes_.length - 1];

    // Increment global time counter.
    F.global_time_++;    
  };


  /**
   * List modules that were invoked at least one time in the latest
   * hierarchy scope.
   *
   * @return {Array.<String>}
   */
  F.get_loaded_modules = function() {

    // Create an empty array for collecting the results.
    var arr = [];

    // Traverse through cache and push all modules into the array.
    for(var name in F.modules_cache_) {

      // Skip modules that are being currently processed.
      if(F.modules_cache_[name] !== -1) {
        arr.push(name);
      }
    }
    return arr;
  };


  /**
   * Replaces module public API with provided one. Returns old module
   * API or undefined if no API existed or module hasn't been invoked.
   * Module name is required not to start with "?!" as it is a
   * reserved prefix for scope-dependent cache mechanism.

   *
   * @param name  {String}  module name.
   * @param api   {*}       new module API.
   * @return      {*}       old module API.
   */
  F.replace_module = function(name, module) {
    
    // Assert that module name doesn't start with '?!'.
    if(F.reserved_ns_prefix_.test(name)) {
      throw new Error('Module name is required not to start with "?!".');
    }

    // Save module old API into a local variable.
    var old = F.modules_cache_[name];

    // Replace module with a new API.
    F.modules_cache_[name] = module;
    
    // Increment global time counter.
    F.global_time_++;

    return old;
  };


  /**
   * Returns an associative array for storing any kind of data client
   * requires. The cache is scope dependent: new hierarchy scope means 
   * new (empty) cache. The cache is created lazily - only for
   * namespaces that were requested. Namespace is required to start
   * with '?!' in order not to conflict with other internal data.   
   *
   * @param  ns  {String}  namespace.
   * @returns    {Object}  associative array.
   */
  F.get_hierarchy_cache = function(ns) {
    
    // Assert that ns does starts with '?!'
    if(!F.reserved_ns_prefix_.test(ns)) {
      throw new Error('Namespace is required to start with "?!".');
    }

    // Create cache if it doesn't exist.
    if(!(ns in F.modules_cache_)) {
      F.modules_cache_[ns] = {};
    }

    return F.modules_cache_[ns];
  };



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * Global scope no-conflict mechanism. Enables client to save
   * variables into the global scope and be able to undo the changes
   * anytime (automatically on cleanup). Also introduces no-conflict
   * behavior for F global variable.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



  // Copy existing F in case of overwrite.
  var F_ = window.F;


  /**
   * Restores the global F variable to its previous value. Relinquish
   * fierry.js control of the variable - will do nothing if the
   * current F variable does not reference fierry.js F variable.
   *
   * @return  {Object}  fierry.js F variable.
   */
  F.no_conflict = function() {

    // Check if global F stands for fierry.js.
    if ( window.F === F ) {

      // Replace global F with previous version of F.
      window.F = F_;
    }

    // Return fierry.js F reference to a client.
    return F;
  };


  // Key for retrieving global variables cache.
  F.globals_cache_key_ = '?!globals';

  /**
   * Saves variable into the global scope under the given name. Copies
   * previous version of that variable from the global scope in order
   * to enable client to undo the changes at any time.
   *
   * Will throw an error if the given name isn't unique across all
   * other global variables cached within the active hierarchy scope.
   *
   * @param name      {String}  variable name.
   * @param variable  {*}       variable value.
   */
  F.set_global = function(name, variable) {

    // Retrieve the global variables cache.
    var globals = F.get_hierarchy_cache(F.globals_cache_key_);

    // Assert that variable is not set within current hierarchy scope.
    if(name in globals) {
      throw new Error('Cannot set_global under duplicated name: ' + name);
    }

    // Copy existing global variable into the cache.
    globals[name] = window[name];

    // Set the variable into global scope.
    return window[name] = variable;
  };


  /**
   * Restores the global variable to its previous state. Discards
   * changes made by 'F.set_global' to a variable with the given name.
   *
   * Will throw an error if the previous state of the variable is not
   * found - that is if the variable was not set as global using 
   * 'F.set_global' in the active hierarchy scope.
   *
   * @param name  {String}  variable name.
   */
  F.unset_global = function(name) {

    // Retrieve the global variables cache.
    var globals = F.get_hierarchy_cache(F.globals_cache_key_);
    
    // Assert that variable is set within current hierarchy scope.
    if(!(name in globals)) {
      throw new Error('Cannot unset_global from unexisting name: ' + name);
    }

    // Replace global variable with previous version of that variable.
    window[name] = globals[name];

    // Remove previous version of the variable from the cache.
    delete globals[name];
  };



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * Scope-independent caching mechanism. Enables client to require 
   * a cache that will not be erased when a hierarchy scope is created
   * or discarded.
   *
   * This allows clients to create massive data generators that won't
   * be needed to recreate for each running test - it will create some
   * dependency between the tests (data will be the same), but also
   * will save much time spent on generating that data.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



  // Associative array storing scope-independent simple caches.
  F.global_cache_ = {};


  /**
   * Returns an associative array for storing any kind of data client
   * requires. The cache is scope independent and created lazily -
   * only for namespaces that were requested.
   *
   * @param  ns  {String}  namespace.
   * @return     {Object}  associative array.
   */
  F.get_global_cache = function(ns) {

    // Create cache if it doesn't exist.
    if(!(ns in F.storage_cache_)) {
      F.storage_cache_[ns] = {};
    }
    return F.storage_cache_[ns];
  };



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * Object identification mechanism. Enables client to stamp objects
   * witemh unique ID.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



  // Uid global sequence. Starts with 1 as 0 evaluates to false when
  // forced to act as a Boolean.
  F.uid_sequence_ = 1;


  /**
   * Stamps an object with an unique int identificator.
   *
   * @param o  {Object}
   */
  F.uid = function(o) {
    if(!o.__uid__) { o.__uid__ = ++F.uid_sequence_; }
    return o.__uid__;
  };



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * Object tagging mechanism. Enables client to attach any number 
   * of labels to any kind of object (e.g: hash, function, array) 
   * via tag() method, and to distinguish objects by their labels 
   * using is() method.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



  /**
   * Tags the given object with any number of labels. This function
   * accepts variable argument list where all arguments are {String}
   * labels except last {Object} object to tag. It returns the 
   * provided tagged object.
   *
   * @param label   {String...}
   * @param object  {Object}
   * @return        {Object}
   */
  F.tag = function() {
    var last   = arguments.length - 1,
        object = arguments[last];

    // Set labels directly when object was not tagged yet.
    if(!object.__tag__) {
      
      // Optimization when there is only one label to tag.
      if(last === 1) {
        object.__tag__ = [arguments[0]];

      // TODO slice or concat would be faster? Or mayby direct push??
      } else {
        object.__tag__ = Array.prototype.slice.call(arguments, 0, last);
      }

    // Push additional labels into the object's tag property.
    } else {
      for(var i = 0, l = arguments.length - 1; i < l; i++) {
        object.__tag__.push(arguments[i]);
      }
    }

    // Return the given object to the client.
    return object;
  };


  /**
   * Returns true if the object contains the given label as its tag.
   *
   * @param label   {String}
   * @param object  {Object}
   * @return        {Boolean}
   */
  F.is = function(label, object) {
    return object.__tag__ ? object.__tag__.indexOf(label) !== -1 : false;
  };



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * Type discovery mechanism. Enables client to check if argument is
   * boolean, number, string, function, array, date, regexp or any
   * unspecified other object.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



  /**
   * Returns true if argument is a boolean.
   *
   * @param o  {*}
   * @return   {Boolean}
   */
  F.is_boolean = function(o) {
    return typeof o === 'boolean' || (o && o.toString() === '[object Boolean]');
  };


  /**
   * Returns true if argument is a number.
   *
   * @param o  {*}
   * @return   {Boolean} 
   */
  F.is_number = function(o) {
    return typeof o === 'number' || (o && o.toString() === '[object Number]');
  };


  /**
   * Returns true if argument is a string.
   *
   * @param o  {*}
   * @return   {Boolean}
   */
  F.is_string = function(o) {
    return typeof o === 'string' || (o && o.toString() === '[object String]');  
  };


  /**
   * Returns true if argument is a function.
   *
   * @param o  {*}
   * @return   {Boolean}
   */
  F.is_function = function(o) {
    return typeof o === 'function' || (o && o.toString() === '[object Function]');
  };


  /**
   * Returns true if argument is an array.
   *
   * @param o  {*}
   * @return   {Boolean}
   */
  F.is_array = function(o) {
    return Array.isArray(o);
  };


  /**
   * Returns true if argument is an unspecified object. Please note
   * that this method is rather slow - use it only if no other
   * typechecking can be done.
   *
   * @param o  {*}
   * @return   {Boolean}
   */
  F.is_object = function(o) {
    return typeof o === 'number' || (o && o.toString() === '[object Object]');
  };


  /**
   * Returns true if argument is a date object.
   *
   * @param o  {*}
   * @return   {Boolean}
   */
  F.is_date = function(o) {
    return o instanceof Date;
  };


  /**
   * Returns true if argument is a regexp object.
   *
   * @param o  {*}
   * @return   {Boolean}
   */
  F.is_regexp = function(o) {
    return o instanceof RegExp;
  };



  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * Utilities. Whatever.
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



  /**
   * Returns randomly generated number. If the max argument is
   * specified, returns integer from <0, max>. Otherwise it returns
   * float from <0,1> - behaving exactly as Math.random().
   *
   * This method is implemented to be extremely fast - therefore the
   * distribution of generated integers is broken. Maximum value is
   * very unlikely to be generated.
   *
   * @param max  {Number}  positive integer.
   * @return     {Number}
   */
  F.random = function(max) {
    return max ? Math.random() * max << 0 : Math.random();
  };


  return F;

})();



/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Extending RegExp object.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



/**
 * Quotes the given string. Escapes all special characters with proper
 * backslashes for string to match literally the given string. 
 *
 * @param str  {String}
 * @return     {String}
 */
RegExp.quote = function(str) {
  return str.replace(/([.?*+^$[\]\\(){}-])/g, '\\$1');
};



/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Extending Array prototype.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



/**
 * Erases and returns first instance of the given item from the
 * array. Compares items via '===' - references only.
 *
 * @param item  {*}
 * @return      {*}
 */
Array.prototype.erase = function(item) {
  var i = this.indexOf(item);
  return i !== -1 ? this.splice(i, 1)[0] : undefined;
};


/**
 * Erases all instances of the given item from the array.
 *
 * Compares items via '===' - references only. Please note that 
 * this method iterates backwards, from the last element to the first.
 *
 * @param item  {*}
 */
Array.prototype.erase_all = function(item) {
  var l = this.length;

  while(l--) {
    if(this[l] === item) {
      this.splice(l, 1);
    }
  }
};


/**
 * Removes additional dimention from the given array, if the array is
 * multidimensional. Having a String[][] array this utility would
 * produce String[] that contains all its array elements concatenated
 * together.
 *
 * @return  {Array}
 */
Array.prototype.flatten = function() {
  var result = [];
  return result.concat(result, array);
};


/**
 * Returns but does not remove the last element from the array.
 * @return  {*}
 */
Array.prototype.last = function() {
  return this[this.length - 1];
};

F.register_module('fierry/class', function() {
  
  var Xxx;
  
  return Xxx = (function() {
  
    Xxx.name = 'Xxx';
  
    function Xxx() {}
  
    Xxx.prototype.hello = function() {
      return 'hello';
    };
  
    return Xxx;
  
  })();
});
F.register_module('fierry/app', function() {
  var math = F.srequire('fierry/math');
  console.log(math().add(1, 12));
});
F.register_module('fierry/primitives', function() {
  
  performance('/create.primitives', {
    'string': function(i) {
      while (i--) {
        'string';
  
      }
    },
    'boolean': function(i) {
      while (i--) {
        true;
  
      }
    },
    'integer': function(i) {
      while (i--) {
        101;
  
      }
    },
    'float': function(i) {
      while (i--) {
        1.01;
  
      }
    },
    'regexp': function(i) {
      while (i--) {
        /regexp/;
  
      }
    }
  });
  
  performance('/create.wrappers', {
    'string': function(i) {
      while (i--) {
        new String('string');
      }
    },
    'boolean': function(i) {
      while (i--) {
        new Boolean(true);
      }
    },
    'integer': function(i) {
      while (i--) {
        new Number(101);
      }
    },
    'float': function(i) {
      while (i--) {
        new Number(1.01);
      }
    },
    'regexp': function(i) {
      while (i--) {
        new RegExp('regexp');
      }
    }
  });
});
F.register_module('fierry/math', function() {
  
  var math;
  
  console.log("Loading math package");
  
  return math = {
    add: function(a, b) {
      return a + b;
    }
  };
});
F.register_module('fierry/create', function() {
  
  performance('/create', {
    before: function() {
      return this.user = world();
    }
  });
  
  F.run('fierry-qa/performance/create/primitives');
  
  F.run('fierry-qa/performance/create/array');
});
F.register_module('fierry/main_area:example', function() {

  var roots  = F.require('/fierry/view/roots');
  var Action = F.srequire('/fierry/view/action');
  var math = F.require('/source/Math');var app = F.require('/example/App');var _require_2 = F.require('/fierry/dom/p');var _require_1 = F.require('/fierry/dom/div');var _require_0 = F.require('/fierry/dom/body');
  
  
  var n0 = function() { return []; };
  var n = function() {
    arr = []
    arr.push(new Action("div", 'aa', this, _require_1(), (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new Action("p", 'aa', this, _require_2(), (function() {
        return 'Hello World!';
      }), (function() {
        arr = [];
        return arr;
      })));
      return arr;
    })));
    return arr;
  }
  return roots().execute('body', _require_0(), n);
});
F.register_module('fierry/main_area:main_area', function() {

  var roots  = F.require('/fierry/view/roots');
  var Action = F.srequire('/fierry/view/action');
  var math = F.require('/source/Math');var app = F.require('/example/App');var _require_2 = F.require('/fierry/dom/p');var _require_1 = F.require('/fierry/dom/div');var _require_3 = F.require('/fierry/dom/tag');var _require_0 = F.require('/pfc-fierry/dom/body');
  
  
  var n0 = function() { return []; };
  var n = function() {
    arr = []
    arr.push(new Action("div", 'aa', this, _require_1(), (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new Action("tag", 'ab', this, _require_3(), (function() {
        return 'main-area unique';
      }), (function() {
        arr = [];
        return arr;
      })));
      arr.push(new Action("div", 'aa', this, _require_1(), (function() {}), (function() {
        var fleet, _i, _ref;
        arr = [];
        arr.push(new Action("p", 'aa', this, _require_2(), (function() {
          return 'Hello world';
        }), (function() {
          arr = [];
          arr.push(new Action("tag", 'aa', this, _require_3(), (function() {
            return 'title';
          }), (function() {
            arr = [];
            return arr;
          })));
          return arr;
        })));
        arr.push(new Action("p", 'ab', this, _require_2(), (function() {
          return 'Here is some additional info';
        }), (function() {
          arr = [];
          arr.push(new Action("tag", 'aa', this, _require_3(), (function() {
            return 'desc';
          }), (function() {
            arr = [];
            return arr;
          })));
          return arr;
        })));
        arr.push(new Action("p", 'ac', this, _require_2(), (function() {
          return '29.08.2011';
        }), (function() {
          arr = [];
          return arr;
        })));
        if (user.happy != null) {
          arr.push(new Action("p", 'ad', this, _require_2(), (function() {
            return "Hello world" + "that: I'm anything" + "- I have to come see u!";
          }), (function() {
            arr = [];
            return arr;
          })));
        }
        for (fleet = _i = 0, _ref = user.fleets.length; 0 <= _ref ? _i <= _ref : _i >= _ref; fleet = 0 <= _ref ? ++_i : --_i) {
          arr.push(new Action("p", 'ae' + math.uid(fleet) + 'aa', this, _require_2(), (function() {
            return fleet.name;
          }), (function() {
            arr = [];
            arr.push(new Action("tag", 'aa', this, _require_3(), (function() {
              return 'name';
            }), (function() {
              arr = [];
              return arr;
            })));
            return arr;
          })));
          arr.push(new Action("p", 'ae' + math.uid(fleet) + 'ab', this, _require_2(), (function() {
            return fleet.get_description();
          }), (function() {
            arr = [];
            arr.push(new Action("tag", 'aa', this, _require_3(), (function() {
              return 'desc';
            }), (function() {
              arr = [];
              return arr;
            })));
            return arr;
          })));
        }
        return arr;
      })));
      return arr;
    })));
    
    arr.push(new Action("div", 'ab', this, _require_1(), (function() {}), (function() {
      var arr;
      arr = [];
      arr.push(new Action("p", 'aa', this, _require_2(), (function() {
        if (logged > 0) return 'Logged';
        return 'Unlogged';
      }), (function() {
        arr = [];
        return arr;
      })));
      arr.push(new Action("p", 'ab', this, _require_2(), (function() {
        return user.name;
      }), (function() {
        arr = [];
        return arr;
      })));
      if (user.logging_of) {
        arr.push(new Action("p", 'ac', this, _require_2(), (function() {
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
  return function() { return roots().execute_raw('pfc-body', _require_0(), n); };
});
F.run('fierry/app');