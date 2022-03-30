# TwitterGuesser

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Guess who tweeted what.

### App Evaluation
- **Category:** Game
- **Mobile:** Android
- **Story:** See how well people actually know who they follow
- **Market:** Social media users
- **Habit:** Play to get the highest score on the public leaderboards
- **Scope:** Create a game that tasks players to guess anonomized tweets, save and compare their scores against other players, and view their personal stats in their personal profile

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* The user can Login/Sign Up.

* The user can play the game and earn a high score.
    * During the game the user will be served a random, anonymized tweet in which they have to try to guess the correct Tweet Author in the shortest amount of time.
    * The user's score will be uploaded to the leaderboard and match history automically.

* The user will be able to see their own profile which of their username, twitter profile picture and leaderboard position.

* The user will be able to view their match history.
    * The user will be able to click on a game to revist the Tweets. 

* The user will able to view the leaderboard consisting of top 100 players.

* The user will be able to view other players profiles through the leaderboard.



**Optional Nice-to-have Stories**

* The user will be able to change their profile picture.

* The user will able to fine tune preferences in setting screen
    * Senstive Filter, Language, etc...

* The user will be able to go to Twitter for to view their previous game's tweets.

* The user will be able to hear music and sound effects, making a more immersive experience.

* The user will receive ads through the Mobile Ads SDK.

### 2. Screen Archetypes

* Login
   * The user can Login/Sign Up.
* Register
   * The user can Login/Sign Up.
* Profile
    * The user will be able to see their own profile which of their username, twitter profile picture and leaderboard position.
    * The user will be able to view other players profiles through the leaderboard.

* Leaderboard
    * The user will able to view the leaderboard consisting of top 100 players.

* Play Screen

* Game Screen
    * The user can play the game and earn a high score.

* Game Detail Screen
    * The user will be able to click on a game to revist the Tweets. 

* Settings [Optional]
    * The user will able to fine tune preferences in setting screen


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Game Start
* Profile
* Leaderboard

**Flow Navigation** (Screen to Screen)

* Login
    * Register
    * Play Screen
* Register
    * Login
    * Play Screen
* Profile
    * Game Detail Screen
* Leaderboard
    * Profile
* Play Screen
    * Game Screen
* Game Screen
    * Play Screen
* Game Detail Screen
    * Profile

## Wireframes

## Low Fidelity Wireframe & Mockups

<img src="./low_fidelity.jpg" width = 600>

### Digital Wireframes & Mockups

#### Login
<img src="./login.png" width=600><br />

#### Main
<img src="./main.png" width=600><br />

#### Profile
<img src="./profile.png" width=600><br />

#### Game
<img src="./game_1.png" width=600><br />
<img src="./game_2.png" width=600><br />

### Interactive Prototype

<img src="./twitterGuesser_prototype.gif" width=303>


## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
