Football Team Tracker
===================================

In 2019, in my 3rd 6-a-side season I decided I wanted to track my team's fixtures and our individual stats. After searching on the play store the only options I found were either poorly designed or the developers had abandoned the apps. For this reason, I decided to make my own.


Building
---------------

To build this project, use "Import Project" in Android Studio.

Features
---------------

The app has three main components: tracking the fixture schedule, individual player stats, and team stats. For fixtures, teams can record the date, time name of opponent, and the goalscorers and assists. This leads into the individual player tracking which includes goals and assists. Finally, the teams record and form is recorded including the number of wins, losses, and draws. As well as a summary of the goals for and against.

<div>
  <img src="/screenshots/hub_tab.png" alt="Hub tab screenshot" width="20%"/>
  <img src="/screenshots/fixtures_tab.png" alt="Fuxtures tab screenshot" width="20%"/>
  <img src="/screenshots/goals_tab.png" alt="Stats tab screenshot" width="20%"/>
  <img src="/screenshots/team_tab.png" alt="Team tab screenshot" width="20%"/>
  <img src="/screenshots/fixture_details.png" alt="Fixture details screenshot" width="20%"/>
</div>

Architecture
---------------

The architecture is almost completely built around Android Architecture components.

This has been a long process as I originally built the app without following Android's app architecture guides. Therefore, I have been slowly migrating the app across. This includes using the Navigation component to reduce the number of activities, in favour of fragments, and generally making the code more idiomatic. I have also been transitioning to using ViewModel's to help seperate logic and UI related code. The app also uses view binding to eliminate the risk of null pointers and also remove the need for explicitly coding types.

Firebase
---------------

I chose to use Firebase because of its quick setup time and great integration with the Android platform. The whole of the app's backend is in Firebase:
* Cloud Firestore is used to store all user data (teams, players). It allowed seamless syncing capabalities through the use of FirebaseUI.
* Authentication allowed me to easily authenticate users and store their information. Once again I used FirebaseUI as it provided a drop-in UI flow.

Kotlin
---------------

In the summer of 2020 I decided to rewrite the entire app in Kotlin. There were numerous reasons for this including, but not limited to, more idiomatic code, null safety, type inference, smarter classes and less boilerplate code.

