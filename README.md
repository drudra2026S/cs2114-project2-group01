# Project 2: Every Second Counts — Prototype + Tests

## Project Description

Every Second Counts is a space exploration game where the player travels to different planets to collect materials needed to create a cure for a dangerous disease on Earth. The player must manage the time passing on Earth while completing missions, dealing with random events, and eventually returning with the necessary materials.

The game uses a generic `LinkedChain` to store and retrieve different types of objects. This allows the same data structure to be reused for multiple parts of the game instead of creating separate data structures for each type.

## How to Compile and Run

The project files currently being developed include:

- `Main.java`
- `Mission.java`
- `Event.java`
- `Planet.java`
- `Spaceship.java`
- `WorldBuild.java`
- `LinkedChain.java`
- `PlanetTest.java`
- `SpaceshipTest.java`
- `WorldBuildTest.java`
- `MainTest.java`
- `MissionTest.java`
- `EventTest.java`

To compile the program, place the required source files in the appropriate project directory and compile them together

To run the game, execute the `Main` class.

To run the tests execute the corresponding test and download the library used in the test.

## Reflection

### What Changed from the Specification?

- Time and Mission Rules: The game starts with 100 hours of Earth time in the regular difficulties. Mission time costs depend on difficulty and the number of resources required.
- Time Dilation: Planets use different time-dilation factors to represent differences between local time and Earth time.
- Win Conditions: The player must complete every mission and return to Earth before running out of time.
- Difficulty Levels: Easy, Normal, Hard, and Demo modes change gameplay. Demo mode provides additional starting time and reduces the effects of certain random events.
- Code Responsibilities: Mission manages mission information and completion, Event handles random events and their effects, WorldBuild generates the planets and missions, and Main manages menus and gameplay.
### What Changed in LinkedChain?

The project was given a `LinkedChain.java` implementation, so the goal is to reuse the provided generic data structure instead of building a new one.

The application is intended to store and retrieve multiple element types through the same generic `LinkedChain` class. For example, we used it for different game objects, such as planets and events.

The application classes should interact with `LinkedChain` only through its public methods. Game rules and application behavior should remain in classes such as `Mission`, `Event`, `WorldBuild`, and `Main`.

### What Happened to Our Unknowns?

We resolved all the unknowns listed in our spec. `Mission` handles mission information and completion, while `Event` handles random event information and effects. `WorldBuild` creates and store the game's planets and events.

We initially had simple logic for our time dilation but we used AI to assist us in making it a bit more interactive without it being to complicated

### What Doesn't Work Yet?

The full application has not yet been verified as a complete, working game. The following areas still need to be implemented, connected, or tested:

- Verifying that all JUnit tests execute and pass.
- Testing invalid inputs and ensuring the application handles them without crashing.
- Confirming that the complete program compiles and runs along the minimum viable product (MVP) path.

## Next Steps

We plan to complete the prototype and then extend it toward the final version of the game.

### Remaining MVP Work

1. **Implement time management.** Track the remaining Earth time and apply the planet's time-dilation factor to mission and travel time.
2. **Implement the win/loss conditions.** Decide the required materials, implement the return-to-Earth requirement if we keeps it, and determine what happens when time runs out or the spaceship can no longer continue.
3. **Test and debug.** Run all tests, fix failures, and verify the complete MVP path.

### Stretch Goals

we needs to confirm which stretch goals from the original scope it will attempt.

- **Committed:** Additional planets with more unique missions, Difficulty levels that can change travel-event probabilities, time costs, and rewards
- **Dropped:** More varied random events with different consequences and recovery options, Randomized mission events, so each playerthough is different and not repetitive 
- **Undecided:** Multiple routes through solar system, so players can choose between a safe path or a higher difficult path with greater rewards

### Additional Tests Needed

For the stretch goals these are the additional test we need
Planned test coverage includes:

• Difficulty Level Variations: One normal case to confirm easy/hard modifiers correctly scale time costs and reward parameters, and one bad-input case handling invalid difficulty levels.
• Expanded Planet Generation: Tests validating that WorldBuild accurately maps and links the additional unique planets and their respective missions without throwing null pointer exceptions.

### Team Responsibilities

For the stretch goals this is what is needed from each individual 

- Ami: Implementing additional planets, mapping the expanded network connections, and writing the new planet generation validation tests.
- Luke: Creating the core difficulty level selection parameters, adjusting the event probability logic, and writing the difficulty modifier unit tests.
- Rudra: Updating the Mission and Event classes to handle scaled reward or damage values across different difficulties.
- Zachary: Updating the menu selection interface in Main to allow players to set difficulty and view expanded planet pathways.

## Attribution

The table below records the current understanding of who worked on each file. we should review it and correct or complete the entries before submission.

| File | Contributor(s) | Contribution |
|---|---|---|
| `Main.java` | Zachary | Wrote the initial menu and program structure |
| `MainTest.java` | Zachary | Wrote the test class for Main |
| `Mission.java` | Rudra | Wrote the Mission Class |
| `MissionTest.java` | Rudra | Created the test class for Mission |
| `Event.java` | Rudra | Wrote the Random event class for the project |
| `EventTest.java` | Rudra | Created the testing class for Event |
| `Planet.java` | Ami | Created the Planet class |
| `PlanetTest.java` | Ami | Wrote the planet test class |
| `Spaceship.java` | Luke | Created spaceship class and program structure |
| `SpaceshipTest.java` | Luke | Wrote spaceship test class |
| `WorldBuild.java` | Luke | Created the WorldBuild class |
| `WorldBuild.java` | Luke | Wrote the WorldBuild test class |
| `LinkedChain.java` | Course-provided/Ami | Provided implementation, and modified accordingly to our project|
| `README.md` | Rudra | Created the README and provided the necessary information|

### GenAI Note

GenAI was used to help define logic that all the classes share. GenAI helped to assist what the win condition should be, including the amount of time Earth should have and how time dilation should work. GenAI was also used to find inconsistencies between classes which caused error. GenAI was used for generating planet names, mission types, and resources collected.
