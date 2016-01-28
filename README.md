#Popular Movies, Stage 2

Popular Movies is an Android app built for the Udacity course

   [Developing Android Apps: Android Fundamentals](https://www.udacity.com/course/ud853).

It shows data on popular movies from themoviedb.org.   

##Features New to Popular Movies, Stage 2

- Scroll through a grid of over 200,000 movie posters!
- Read reviews of movies
- Easily build a list of your favorite movies; browse and sort it offline
- Play Trailers
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
- Callbacks

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


##This app is incomplete.  Here's what it still needs:

- A way to delete data in the cache
- Attribution to themoviedb.com in an About page
- A sane way of keeping track of how movies got into the cache so that they can be filtered and
sorted more effectively.  Currently, a lot of highest rated movies show up in the most popular
list, which makes no sense.  I need to find out what sorting criteria themoviedb uses or mark
the origin in the database as movies are fetched from the network.
- A lot of productionizing; strings, google play, google properties integration
- Better use of material design


##Some self-criticisms

###I tried to do too much
- Having an offline cache for all movies instead of just favorites added a lot of complexity
- Offering so many kinds of sorting and filtering also lead to a lot of code bloat

###Test coverage is low, leading to a lot time lost quenching weird bugs
- I need to learn Dagger and how to automate UI testing, on real devices in particular
- I didn't use tdd, so a lot of cruft built up.  I probably could have gotten away with it
if I hadn't tried to support so many features and technologies.  Nah, who'm I kidding.
- I'm dying for something like NCrunch to keep me honest

###Many design choices are less than optimal
- I had to forego cursor loaders because of various conflicts with jloop I couldn't resolve.
In retrospect, while this cut down on code and some classes, it added a lot of code to handle
varous concurrency issues.
- I used several different async libraries to test them out; in practice, I should use the one
that's most appropriate to my needs.  
- GSON/GSON Format are powerful, but I need to find or build a cleaner way to combine them with
data providers and parcelables and database fields that aren't in the original JSON
- The concurrency model used is clunky overall.  This is a result of a combination of my
unfamiliarity with Android when I started and using different async libs.
- The MoviesFragment class shold be factored out into several classes, one for managing network
mode, another for handling cache mode, and a third for managing interaction.  There's too much
coupling between the data fetching, database code, drawing the UI and handling interaction.
Most of this would never have materialized had I been using tdd.
- Use of meterial design is poor

###I made poor use of git/github
- Checkins and pushes were seldom logical units
- I didn't make effective use of branching, especially near the beginning

###Android Studio 2.0 preview
- Great for-round tripping UI tweeks and small logic changes
- Painful for Gradle changes, emulator weirdness and test environment breaks