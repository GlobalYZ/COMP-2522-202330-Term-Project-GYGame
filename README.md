# COMP-2522-202330-Term-Project-GYGame

# EcoStack
> EcoStack is a puzzle game with an environmental twist. The player's goal is to manage waste by recycling and composting,
aligning it with ecological themes. It serves a social purpose by raising awareness about recycling and waste management.
> 
> Live demo [_here_](https://www.example.com).

## Table of Contents
* [General Info](#general-information)
* [Built With](#built-with)
* [Features](#features)
* [Screenshots](#screenshots)
* [Setup](#setup)
* [Usage](#usage)
* [Project Status](#project-status)
* [Room for Improvement](#room-for-improvement)
* [Acknowledgements](#acknowledgements)
* [Contact](#contact)


## General Information

### Core Mechanics:
Instead of colored blocks, you have different types of waste forming up the minos: plastics, glass, paper, organics, etc. 
Each waste type is presented as an image. The play area is a standard grid, simulating a garbage can or a landfill, 
similar to Tetris, where these items fall from the top of the screen.

### Gameplay:
The player must sort the waste into rows (or columns), which then gets recycled. Completing a row(or column) of three 
same type of waste clears it from the board and scores points.
If the player manages to stack several rows of the same type consecutively, they get a combo bonus, 
representing efficient recycling. Special items such as compost activators or recycling boosters occasionally appear 
and can be used to clear all the same type of waste on the board 
when being cleared with those type of waste.
The game gradually speeds up, increasing the difficulty as time goes on.


## Built With
- Java 20
- JavaFX 20
- Maven 20
- JavaFx media 20
- Jackson 20


## Features
List the ready features here:
- Awesome feature 1
- Awesome feature 2
- Awesome feature 3


## Screenshots
![Example screenshot](./img/screenshot.png)
<!-- If you have screenshots you'd like to share, include them here. -->


## Setting Up the Project

This section guides you through getting a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Before you begin, ensure you have the following installed:
- [Git](https://git-scm.com/downloads)
- [Java JDK](https://adoptopenjdk.net/) (version 20 or higher)
- [JavaFX](https://openjfx.io/openjfx-docs/) (version 20 or higher)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
- [Maven](https://maven.apache.org/download.cgi) (if not included in IntelliJ IDEA)

### Opening the Project in IntelliJ IDEA
1. Launch IntelliJ IDEA.
2. Select ```Open or Import``` on the welcome screen, or choose ```File > Open``` from the main menu if IntelliJ is already open.
Navigate to the directory where you cloned the project, select the root directory of the project, and click ```Open```.
3. IntelliJ IDEA will automatically detect the Maven project and load it accordingly.

### Configuring Maven
1. Once the project is open in IntelliJ, locate the ```pom.xml``` file in the project directory.
2. Right-click on ```pom.xml``` and select ```Add as Maven Project``` if IntelliJ hasn't already recognized it as a Maven project.
3. IntelliJ IDEA should now download all the required dependencies as specified in your ```pom.xml``` file.

### Configuring JavaFX
1. In IntelliJ IDEA, select ```File > Project Structure``` from the main menu.
2. Select ```SDKs``` under ```Platform Settings```.
3. Click the ```+``` button and select ```Java```.
4. Navigate to the directory where you installed JavaFX, select the ```lib``` folder, and click ```OK```.


## Usage
How does one go about using it?
Provide various use cases and code examples here.

`write-your-code-here`


## Project Status
Project is: _in progress_ / _complete_ / _no longer being worked on_. If you are no longer working on it, provide reasons why.


## Room for Improvement
Include areas you believe need improvement / could be improved. Also add TODOs for future development.

Room for improvement:
- Improvement to be done 1
- Improvement to be done 2

To do:
- Feature to be added 1
- Feature to be added 2


## Acknowledgements
Give credit here.
- This project was inspired by...
- This project was based on [this tutorial](https://www.example.com).
- Many thanks to...


## Contact
Created by [@flynerdpl](https://www.flynerd.pl/) - feel free to contact me!