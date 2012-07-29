performance 'items'

  'references':
    before: -> @a = []; @b = []; @c = []; @d = []; @e = []
    run:    -> a: @a, b: @b, c: @c, d: @d, e: @e
    
    
  ' 4 actions: 1x3':
    before: ->
      @v = F.srequire('fierry-qa/performance/view/create:1x3')
      console.log @v(), @v()
    run: ->
      @v()
    