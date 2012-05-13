#
# This file loads performance tests for creating primitives, arrays,
# hashes, etc.
#



# Register main group.
# Register main group.
performance '/create'
  before: ->
    @user = world()



# Load tests.
F.run 'fierry-qa/performance/create/primitives'
F.run 'fierry-qa/performance/create/array'
#F.run 'fierry-qa/performance/create/hash'
#F.run 'fierry-qa/performance/create/class'
#F.run 'fierry-qa/performance/create/function'
#F.run 'fierry-qa/performance/create/function_prototype' # ???
