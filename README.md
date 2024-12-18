

# The Card Game

<div align="center"><img src="https://media1.tenor.com/m/8roYGyMXjrgAAAAd/cyno-genshin-impact.gif" width="500px"></div>

## Personal Overview

A complex project that teaches the principles of OOP the hard-way, because of the need to implement multiple functionalities, while maintaining a logical game flow.

This project took multiple days to finish, as well as countless changes to the project structure itself.

## Project Concepts

The project involves implementing a simplified version of a strategic card game similar to Gwent.

The game flow involves players engaging in turn-based rounds, where each player strategically uses cards from their hand to achieve specific objectives. 

Players manage resources, deploy cards, and execute actions based on predefined rules. Cards may have unique effects, interact with other cards, or impact the game state. 

The game progresses through multiple phases, culminating in determining a winner based on criteria like health.

## Project Structure

* src/
  * cards/ - contains the classes used to represent the Minion Cards and the Hero Cards
  * fileio/ - contains classes used to read data from the json files
  * game/
    * contains the Game Manager that distributes the play session into multiple games
    * contains the Rounds played within one game -> this  is where the actions are handled
    * contains the Commands used to perfom each action requested
    * contains the Error Handler that stores different Exceptions that could appear during the game
  * player/ - contains the class Player, which is to say the entity who handles the cards
  * table/ - contains the Game Table where all the cards will be put down after being chosen

## Game Flow

When running the program, the first method called is that of the GameManager. Inside, all manner of classes and methods are called in order to develop the wanted output.

The flow is as follows:
1. Extract the list of games from the input;
2. For each game, assign two players and start the game, by calling the .start() method from the Rounds class;
3. From the input, assign the order in which the players are to play;
4. Give a go to the many rounds, that only end when the Actions List is left empty of elements;
5. Depending on the Action, different methods from Commands are called. Most of the time, for important commands such as CardUsesAttack(), all available classes are used in order to properly update everything;

## Class Commands

The Commands class is actually the heart of this entire project. While the Rounds.start() method is the entry-point into this class, the most important method is that of Rounds.actOnCommand().

This is the method that parses the Actions list and assigns each action a command (proper method that handles it).

Because this project requires many features, the Commands class ended up being huge. It was espescially hard to implement all those functionalities so that they all worked well together.
In order to reduce the class length, I also added the ErrorHandler class and used Exceptions. This minimised the use of duplicate code.

My usual approach when solving an assignment is to look at the tests in the checker and implement only the functionalities tested. This time, it did not work well at all, because
some edge-cases would be tested later on, or the way I solved one test would not suffice for the next. This led to my project facing many refactorings.

## Key Takeaways

1. I gained a better understanding on the principles of OOP and realised that I could have done a better work at using the inheritance.
2. The sheer scale of this project forced me to structure my ideas well, which proved to be quite the endeavour.
3. Since finishing this project, I have learned of new ways of structuring code, such as design patterns and exception handling that I wish to use in my next project.
4. All in all, I liked this project as it had an easy-to-grasp concept. Had I not played card games before or been able to actually imagine playing, coming up with the game flow would have been much harder.

