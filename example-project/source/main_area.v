/require source/view/sidebar:left as left_sidebar
/require /example/App  as app
/require /source/Math  as math

/define dom/div: body
  /div
    /p 'Hello World!'

/define dom/pfc-body: pfc-body

  /use main_area: world

  # Comment. Should be stripped.
  /div -main-area -unique
  
    /div
      /p -title 'Hello world'
      /p -desc  'Here is some additional info'
      /p        '29.08.2011'

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