/require source/view/sidebar:left as left_sidebar
/require /example/App  as app
/require /source/Math  as math

/run example: body
  /div
    /p 'Hello World!'

/export main_area: pfc-body

  # Comment. Should be stripped.
  /div -main-area -unique

    /div
      /p -title 'Hello world'
      /p -desc  'Here is some additional info'
      /p        '29.08.2011'

      # Special action here! And multiline expression :-O
      #/script
      #  hash =
      #    fafa: 'gaga'
      #    haha: 'papa'

      /if user.happy?
        /p
          "Hello world" +
          "that: I'm anything" +
          "- I have to come see u!"

      /for fleet in user.fleets
        /p -name fleet.name
        /p -desc fleet.get_description() # same as: ..user.fleets.$i.get_description()

  /div
    #/args
    #  /logged as user.logged

    /p
      return 'Logged' if logged > 0
      return 'Unlogged'
    
    /p user.name  
    /p 'goodbye' /if user.logging_of