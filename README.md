# movieDB
Android application that contains a list of movies taken from The Movie DB API

## Installation
In this section i will explain the installation of this project
1. Clone the project (https://github.com/iqbalShafiq/moviedb.git)
2. Open the project you have cloned from your android studio
3. Wait for gradle to finish syncing. For the purposes of this test, I did not save the api key in local.properties so that reviewers can run it directly without setting the api key first.
4. After the synchronization process is complete, you can build and run the project on Android Studio via an emulator or your android pyshical phone.

## The Features
The movieDB application that I developed contains several features and let's explore one by one:
1. The first feature you will see is the splash screen, which serves to show the branding of the MovieDB application that I developed.
2. On the dashboard, there will be a list of movies according to category. You can easily change the category as you want by pressing the category button.
There are 4 categories that can be scrolled horizontally.
3. In addition to the list of movies, on the dashboard there is also a search feature that can directly load a list of movies according to the query you provide.
4. Then for each film that is presented in the list there are 2 buttons, namely the bookmark and rating buttons. Bookmark will add the film into the watch list, and the rating button allows you to rate the movie
5. You can also refresh the dashboard screen so the data will be fetched from API again
6. The last but not least you can go to watch list and see list of movies you have bookmarked before.

## Implementation and Architecture
1. Built with MVVM architecture
2. The language used is kotlin
3. Using android jetpack libraries
4. Room DB for offline caching
5. Glide for load the images
6. Retrofit & RxJava for network & API call

## APK
if you want to run the application directly without cloning the project to your android studio, you can directly download app-debug.apk which is on the root folder.  I also upload it on the following link. 
https://drive.google.com/file/d/1kooYbgwjk86sIHyuQ_2DixBOGguzJyrg/view?usp=sharing

After downloading it, you can immediately move and install the file to your Android phone.

That's all the documentation of the movieDB application that I have developed. I hope you can enjoy the application :D
