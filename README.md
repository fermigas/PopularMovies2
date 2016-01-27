#Popular Movies, Stage 2

Popular Movies is an Android app built for the Udacity course

   [Developing Android Apps: Android Fundamentals](https://www.udacity.com/course/ud853).

It shows data on popular movies from themoviedb.org.   

##Features New to Popular Movies, Stage 2

- Scroll through a grid of over 200,000 movie posters!
- Read reviews of movies
- Easily build a list of your favorite movies and browse it offline
- Play Trailers
- Mark and sort by your favorite movies
- Share trailers with friends on many social media outlets!
- Now supports Android Tablets - *Classy master/detail view in portrait and landscape!*
- Now follows Android's beautiful new Material Design guidelines

###Several features have been added which aren't part of the Udacity Course

**Sort movies by:**

- Latest Releases
- Highest Gross  (not supported in cache)
- Primary Release Dates (Newest and Oldest)
- Alphabetical

**Filter movies by**

- Number of ratings
- Date Released
- Genres

**Other Novelties!**

- Good behavior generally when offline, movies cached in a database
- Reset settings to defaults

###The following concepts, technologies and libraries were used in Popular Movies, Stage 2:

- **Android Studio 2.0** preview versions 3-7 with *Amazing* instant changes.
- SQL Lite Database
- Content Providers
- OkHttp
- GSON
- AppCompat
- Loopj
- httpcore
- cursors/cursor adapters
- cursor loaders
- Android Test
- junit
- ShareActionProvider

##Features in Popular Movies, Stage 1

- Scroll through a grid of 20 movie posters for themoviedb.org
- Sort movies by popularity or rating
- See details about movies:  Release Date, Average Rating, Overview

###The following Android concepts, technologies and libraries were used in Popular Movies, Stage 1:

- Custom ArrayAdapters
- Percelables and SaveInstanceState()
- HTTP Networking / AsyncTask
- JSON Parsing
- Shared Preference
- Portrait/Landscape Layouts
- GridView
- Picasso  (Graphics downloading Library)
- Gradle
- Android Studio 2.0 (preview) and emulators
- Preferences
- logging/debugging

##Building and Running

In order to get this project  to build and run, you'll need an API key from themoviedb.org.   

First, sign up to themoviedb.org
https://www.themoviedb.org/account/signup

Once signed  in, you'll get to the main page
https://www.themoviedb.org/account/[your account name]

Chose API from the menu on the left.  Drill down to create an API key.   

Next, add  your API key to the code by adding the following to your ~/.gradle/gradle.properties file:
  
MyTheMovieDbApiKey="[Your API key]"

In your build.gradle  (Module: app) these lines will reference that key:

buildTypes.each{
     it.buildConfigField 'String', 'THE_MOVIE_DB_API_KEY', MyTheMovieDbApiKey
}






