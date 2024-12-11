# - Mutiny -

# Game Summary:
A dark, atmospheric escape room game where you must defeat enemies in a card based board game to receive items to escape.  The setting is that you are a mutineer who is being held below deck after your mutiny failed.

# Genre:
- Escape-room
- Card game 
- Inscryption-esque

# Inspiration:

## Inscryption
Main inspiration for the game.  Atmosphere, table, and escape room elements all heavily inspired by Inscryption’s mechanics, but in a pirate setting instead of a fantasy cabin.  The relative simplicity of the card game in Inscryption compared to other deck-builders on the market to instead focus on an engaging setting is something we wish to emulate so we can put more focus on the graphical aspects of our game.

<img src="https://umlconnector.com/wp-content/uploads/inscryption-1.webp" alt="Inscryption Inspiration Image" width="500"/>
<a href="https://store.steampowered.com/app/1092790/Inscryption/" alt="Inscryption Steam Page"/>Inscryption Steam Page</a>

## Buckshot Roulette
Another game inspired by Inscryption’s perspective with the table and interactable items.  Closer to the fidelity of what we expect with our assets.  The grittiness and sparseness of Buckshot Roulette’s environment compared to Inscryption’s more involved effects provide a good example of how to create the atmosphere we want while keeping our workload reasonable.

<img src="https://static.wikia.nocookie.net/buckshot-roulette/images/3/32/GameRoom.png/revision/latest/scale-to-width-down/1000?cb=20240112231309" alt="Buckshot Roulette Inspiration Image" width="500"/>
<a href="https://store.steampowered.com/app/2835570/Buckshot_Roulette/" alt="Buckshot Roulette Steam Page"/>Buckshot Roulette Steam Page</a>

## Gwent (Witcher 3 not standalone)
A minigame in a larger RPG that is intentionally simple, but effective to not distract from the other aspects of the game.  The 3 lanes with cards that have clear, large effects instead of the more gradual progression of larger card games (like Magic the Gathering, Hearthstone, etc.) allows for both easier implementation/design as well as putting more emphasis on other aspects, atmosphere in our case.  Gwent’s styling after a skirmish in war with footmen, archers, artillery with cards representing weather events causing “environmental effects” is a good model of how we would like to style our card game after a naval battle.

<img src="https://witcher3.mmorpg-life.com/wp-content/uploads/2015/05/witcher-3-gwent-1024x576-660x371.jpg" alt="Gwent Inspiration Image" width="500"/>
<a href="https://store.steampowered.com/app/292030/The_Witcher_3_Wild_Hunt/" alt="Witcher 3 Steam Page"/>Witcher 3 Steam Page</a>


# Gameplay:
- Player sits at table to play the card game against other members of the crew
- Player can stand up once unchained to walk around the room and complete puzzles.
- Players can hold items that are used to interact with objects around the room.
- The card game is relatively simple with 3 lanes where you can put cards represented as 3 galleys in a naval battle.  Cards increase the power of their represented galley and/or cause other effects and players alternate playing single cards until one chooses to stop, with the opponent then allowed to play any number of cards until they choose to stop.  The first player to stop will receive some incentive still to be decided.  Once both players stop playing for the round, any galley with less power than its opposite is removed and cards in play are discarded.  Galleys which sink their opposite are matched if there is an opposing open galley and are simply ignored otherwise.  One side loses by having no galleys.  (Tentative rules that might need balancing.  Something like only 2 lanes with a backup galley could work to discourage just dumping hand early, etc.)
- Specific cards have various effects such as strengthening other cards, drawing cards, weakening enemy cards, etc.  The player gets choices on adding cards to their deck as the game progresses.
- There is possibly a third deck in addition to yours and the opponents that can cause events that change the board state themed after maritime events (tides, winds, etc.).  These can be represented by changing the appearance of the play mat on the table.
- Some items that the player collects can be placed at the table and used during the game to give advantages.  Such as a bottle of rum causing you to “see double” and double the power of your next played card.
- After you defeat a member of the crew and do a puzzle, another member will enter until you are able to play the captain and escape the hold.
- After you escape the hold, you walk on deck, alone, staring into the vast emptiness of the ocean for eternity (very artful).

Graphics:
- First-person perspective with low-poly graphics
- High contrast lighting with particle systems for card playing and surrounding terrain

# Development

## First Deliverable 
Our work for this deliverable consists of three main components:
- Basic Card Game Function (card playing, basic GUI, game board, turns)
- First Person Movement and World Interaction (free look and walk, collisions, crosshair, item pick up/drop)
- Set Modeling (model imports, level design, basic atmosphere)

### Basic Card Game Function
The card game scene is implemented in an appState that creates and moves a table spatial as well as many placeholder boxes to create a playmat, grouped card slots(galleys), individual card slots, and cards.  The card spatials are translated through space to indicate selection and place the selected card on a card slots.  This is handled using triggers, mappings, and an ActionListener.  The ActionListener is also used to implement alternate means of looking around and at the game board as the mouse cursor is used to select and play cards while seated.  Game logic including drawing cards, cards affecting the board state, the opponent drawing and playing cards, progression of turns is handled in a set of java files(Board, Galley, Slot, Card).  These files modify the 3d space by moving cards or changing the text on cards to represent effects while maintaining an internal representation of the game state.

<a href="https://imgur.com/n3Il7YC"><img src="https://i.imgur.com/n3Il7YC.png" title="source: imgur.com" width="250" /></a>
<a href="https://imgur.com/NjMkFVE"><img src="https://i.imgur.com/NjMkFVE.png" title="source: imgur.com" width="250" /></a>
<a href="https://imgur.com/rRM0AeK"><img src="https://i.imgur.com/rRM0AeK.png" title="source: imgur.com" width="250" /></a>
<a href="https://imgur.com/Veo45n2"><img src="https://i.imgur.com/Veo45n2.png" title="source: imgur.com" width="250" /></a>
<a href="https://imgur.com/304klXU"><img src="https://i.imgur.com/304klXU.png" title="source: imgur.com" width="250" /></a>
<a href="https://imgur.com/TuBu68c"><img src="https://i.imgur.com/TuBu68c.png" title="source: imgur.com" width="250" /></a>
<a href="https://imgur.com/PSoMY0Y"><img src="https://i.imgur.com/PSoMY0Y.png" title="source: imgur.com" width="250" /></a>

### First Person Movement and World Interaction
Movement and free look are controlled through WASD and cursor tracking, respectively, and are implemented through a modified version of JME's built in flyCam. There is also a small crosshair that is on the GUINode.
<blockquote class="imgur-embed-pub" lang="en" data-id="a/pAj2WXc"  ><a href="//imgur.com/a/pAj2WXc">Movement/Free Look</a></blockquote>

Collisions and physics are handled through the jBullet plugin. The player and all objects in the scene are affected by collisions. If an object has the `canBePickedUp` tag (green object in demo), the player can "pick up" the object if they are in a defined range and are looking at the object. The object will float at the center of the screen until it is dropped. Physics on the object are disabled after pickup. Only ONE object can be picked up at a time. The bind for picking up an object is currently set to left mouse click.
<blockquote class="imgur-embed-pub" lang="en" data-id="a/a38lkK5"  ><a href="//imgur.com/a/a38lkK5">Picking Up</a></blockquote>

By left clicking again, the held object is dropped at the vector originating from the camera position and going to where the player is looking, but dropped at the same range (magnitude) as the pickup range. Physics are re-enabled on the object after dropping.
<blockquote class="imgur-embed-pub" lang="en" data-id="a/N0xybuo"  ><a href="//imgur.com/a/N0xybuo">Dropping</a></blockquote>


### Set Modeling
The ship hull that acts as the main room for the game was created in Blender. It was modeled by hand using a cylinder base mesh and gradually adding the walls and ceiling to achieve the current look. It has a grate at the top that will be used to let in light, and features portholes (circular windows) as well as larger, rectangular windows that will allow the player to look out and observe the ocean/world in a future update. Inside the room is a table, chest, candle, and grandfather clock, all sourced from Poly Pizza and credits linked below. The table is used to play the card game. The chest and grandfather clock will have puzzle functionality attached to them, but is not present at this time. All of the items were attached to nodes, and then the nodes attached to the rootnode using helper methods for each object. 

Right now, the candle acts as the sole lighting for the scene, but sets the atmosphere as a dark, dimly lit hold that a prisoner would be held in. The candle doesn't light up the entirety of the scene which is by design, as in the future we want the player to use the candle and/or a lantern to illuminate certain objects for the puzzles, and as mentioned previously makes the game feel more ominous and dark. The candle uses a point light with an orange color, which is attached to a node above the candle model. The reason for this choice is to allow the light to illuminate the candle itself, and cast light below itself more effectively. Setting the point light right at the flame part of the model results in the base of the candle and the flame itself to not be lit. We plan to add more lights through the form of lanterns, the sun/moon using directional light, and perhaps other items/cards.

<img src="https://github.com/user-attachments/assets/eb9129af-0cfe-47dc-bdde-68bcadcdf11f" alt="Perspective sitting at table while playing" width="500"/>
<img src="https://github.com/user-attachments/assets/52b5908c-a057-4f26-b9f9-24da4ff997b5" alt="Looking left at table" width="500"/>
<img src="https://github.com/user-attachments/assets/a1ece375-5be6-461f-9ad0-747bb012d6e4" alt="Looking right at table" width="500"/>
<img src="https://github.com/user-attachments/assets/d8b1f5ff-d4a2-4066-b69c-47627d2a6cb6" alt="Looking into room from windows" width="500"/>
<img src="https://github.com/user-attachments/assets/59e83ea6-5923-46ae-8470-bcc263761bee" alt="Looking inside from doorway" width="500"/>
<img src="https://github.com/user-attachments/assets/3d45f8ee-b7f3-41f5-94e1-bf20a0ac569d" alt="Hull Model in Blender" width="500"/>

#### Model Attributions:
- Candle: Candle by Nick Slough [CC-BY](https://creativecommons.org/licenses/by/3.0/) via Poly Pizza (https://poly.pizza/m/HFpLq6iqKu)
- Table: Table by Darwin Yamamoto [CC-BY](https://creativecommons.org/licenses/by/3.0/) via Poly Pizza (https://poly.pizza/m/2UW71XCeyGh)
- Grandfather Clock: Grandfathers Clock by CreativeTrio (https://poly.pizza/m/09YKIkFZnA)
- Chest: Chest by Quaternius (https://poly.pizza/m/O72u4Drp8k)

### Running Instructions
Transition from the Card Game "scene" to the interaction demo "scene" using 'S' while sitting.
Transition from the interaction demo "scene" to the model demo "scene" using 'P'.

## Second Deliverable 
Our work for this deliverable consists of three main components:
- Additional Card Game Function 
- Basic Puzzle Functionality
- Merging of physics and collision with models

### Card Game Additions
The card game has two new card types, one that effects the enemy board and one that draws cards.  Enemy cards are now visible across the table in the opponent's "hand", and both player and enemy cards visually move to their slots instead of teleporting.  Additionally, whenever the player triggers a next turn event, after galleys "sink" each other, the galleys visibly slide down to allow further turns to be played out.  The game is able to tell when one side has won and prints a victory or defeat message to the system.

<a href="https://imgur.com/cieKkHJ"><img src="https://i.imgur.com/cieKkHJ.png" title="source: imgur.com" width="500" /></a>
<a href="https://imgur.com/OOvOHqN"><img src="https://i.imgur.com/OOvOHqN.png" title="source: imgur.com" width="500" /></a>
<a href="https://imgur.com/jKNl9lh"><img src="https://i.imgur.com/jKNl9lh.png" title="source: imgur.com" width="500" /></a>
<a href="https://imgur.com/fx8bFTf"><img src="https://i.imgur.com/fx8bFTf.png" title="source: imgur.com" width="500" /></a>
<a href="https://imgur.com/hkc6nKj"><img src="https://i.imgur.com/hkc6nKj.png" title="source: imgur.com" width="500" /></a>

### Puzzle Fuctionality
The ability to utilize objects within the scene to solve puzzles is implemented through a proximity system.  This system shows a message on the GUI node alerting the user that they are unable to progress with an aspect of the puzzle until the correct item is brought close enough to the puzzle aspect to allow interaction. 

<img src="https://github.com/user-attachments/assets/8ccf8220-ddc6-476e-be03-5bc4115e3df5" alt="Too dark to read note" width="500"/>

Currently our implementation incorporates a letter that is unreadable until you bring a candle close enough. The letter's contents are required to progress the game since it contains a secret code required to solve the next "puzzle". The note object measures distance to both the player and the candle every frame, so that 1. when the player is close but a candle isn't, the GUI text says the note is too dark to read and is uninteractable and 2. when the player and candle are both close, the GUI text says "Press 'F' to read". Right now 

<img src="https://github.com/user-attachments/assets/e2bf5d21-e38d-4236-9420-d2e08db61bd4" alt="Bringing our candle close to the note" width="500"/>

If 'F' is pressed when prompted, a GUI image of the note is opened and can be closed with another shown key bind on the screen.

<img src="https://github.com/user-attachments/assets/0522fb60-fbf6-4524-b3f4-a2cca15368f5" alt="Opening the note" width="500"/>

### Physics Merging and Misc.
The ability to move around and interact with objects with the player, scene, and objects having collision shown through placeholders in the previous submission has been merged with the scene containing relevant models. The player is now able to move around and collide with the various objects in the hold. Although the puzzle functionality is not implemented beyond what is mentioned in the previous section, we plan to make the clock interactable to reveal a compartment. However we have had a lot of difficulty importing animations so it currently doesn't function. Below is an image of holding and carrying the candle.

<img src="https://github.com/user-attachments/assets/3c0fd838-d49d-4e6e-9ec6-49c108a9cc29" alt="Picking up the candle" width="500"/>

### Textures, Lighting, Waves, and Shadows
Additionally, a wood texture has been applied to the hull. The textures are sourced from here: http://www.architwister.com/portfolio/wood-texture-01/

However, we are having issues scaling the textures right now. Waves have also been added that reflect the moonlight, as seen below:

<img src="https://github.com/user-attachments/assets/814ef6ff-6250-458d-b830-3eb00c008939" alt="Bringing our candle close to the note" width="500"/>

We have also begun experimentation with shadow settings using the shadow renderer, shadow filter, ambient occlusion, and other settings. We also made a testing UI that displays the current settings used and keybinds to change the settings:

No shadows:
<img src="https://github.com/user-attachments/assets/d23d11cb-59e6-49a2-a550-0110889cccde" alt="Shadows using the filtering method" width="300"/>
Shadows using filtering method:
<img src="https://github.com/user-attachments/assets/4c5a354e-c1ca-4c0b-8cec-8be114bf7e38" alt="Shadows using the filtering method" width="300"/>

Shadow Testing UI:

<img src="https://github.com/user-attachments/assets/efa437e6-ffd2-4582-9cfc-d54bdd64bfd8" alt="Shadows using the filtering method" width="300"/>


## Third Deliverable 
Our work for this deliverable consisted of:
- Fully functional card and puzzle aspects
- Sound
- Visual effects

### Card Game Additions
Sources:
  - Coin: Skull Coin by Quaternius (https://poly.pizza/m/lx1A0s4aoH)
  - Audio: https://www.findsounds.com/ISAPI/search.dll
  - Textures: https://texturelabs.org/

The appearances of the cards, playing mat, and galleys have been updated to utize cardstock, cloth, and wood.

<a href="https://imgur.com/scZzmIZ"><img src="https://i.imgur.com/scZzmIZ.png" title="source: imgur.com" width="500" /></a>

The rules have been updated, and a fully playable game loop is possible with the following rules:
  - You start seated looking across from your opponent and at your hand.
  - You may press W and S to look at the board or back at your hand, or if you press S while looking at your hand you stand from the table.  Pressing E while walking around will allow you to sit back down.  Additionally, pressing A or D while looking at your hand allows you to turn your head.
  - At the start of each game, you and your opponent each draw 7 cards.
  - While looking at your hand, you may left click a card to select it.
  - While a card is selected, clicking a slot on the near side of the table plays the card on that slot.  Each of these slots belongs to one of 3 near, galleys.  Your opponent then immediately plays a card from their hand into one of the far galleys.
  - Cards have power and some have effects that trigger on play.  You may right click a card in your hand, on the table, or the top of a discard pile to read its effects.
  - There is a skull coin in the middle of the board.  If you have played at least one card, you may left click the coin to advance the round.
  - When the round advances, each galley sums all of the power of the cards in its slots, and if it exceeds the opposite galley’s power, the opposite galley is sunk.  The board updates itself to allow further play.
  - If both players still have at least one galley, each player draws 2 cards and play continues.  If one player is out of galleys, then that player loses.

<a href="https://imgur.com/asMQ6pT"><img src="https://i.imgur.com/asMQ6pT.png" title="source: imgur.com" width="500" /></a>

<a href="https://imgur.com/pNc5Q9j"><img src="https://i.imgur.com/pNc5Q9j.png" title="source: imgur.com" width="500" /></a>

<a href="https://imgur.com/uroZvZh"><img src="https://i.imgur.com/uroZvZh.png" title="source: imgur.com" width="500" /></a>

Additionally, when you either win against the first opponent or lose to any apponent, a 'dialogue' system is implemented.

<a href="https://imgur.com/k6l0hCw"><img src="https://i.imgur.com/k6l0hCw.png" title="source: imgur.com" width="500" /></a>

The card game is also implemented with the puzzle aspects, allowing you to win the game once you play the kraken card obtained through the puzzles against the second opponent.

<a href="https://imgur.com/pT0VcBl"><img src="https://i.imgur.com/pT0VcBl.png" title="source: imgur.com" width="500" /></a>


### Puzzle Additions
The player can now interact with the clock at any time.

<a href="https://imgur.com/kVDZIV2"><img src="https://i.imgur.com/kVDZIV2.png" title="source: imgur.com" width="500" /></a>

After getting the secret code by using the candle to inspect the note, the player can go interact with the clock to initiate a prompt. By using prompted the 'Z' and 'X' keys, they can set the clock to the time specified on the aforementioned note. As the confirmation mechanism, staying on the correct time for 3 seconds will dispense the chest key.

<a href="https://imgur.com/6FL6Ujw"><img src="https://i.imgur.com/6FL6Ujw.png" title="source: imgur.com" width="500" /></a>

The chest is interactable in this deliverable but has the prompt "locked" when the player is close to it. If the player approaches with the chest key from the clock however, pressing 'F' would unlock the chest and grant the player the "Kraken" card. This card has 99 power and is crucial to winning against the otherwise-impossible second opponent, and eventually trigger the win condition.

<a href="https://imgur.com/QW8PBZ7"><img src="https://i.imgur.com/QW8PBZ7.png" title="source: imgur.com" width="500" /></a>

When the "Kraken" card is played against the second opponent, the screen (and player) will start shaking randomly with increasing intensity for 5 seconds, until the win screen appears with a prompt to exit the game.

<a href="https://imgur.com/eKJiuYr"><img src="https://i.imgur.com/eKJiuYr.png" title="source: imgur.com" width="500" /></a>

### Sound
For this submission, we created dedicated helper classes SFXManager and MusicManager to streamline audio-playing processes. This allows us to easily do both positional and non-positional audio. 

Some examples of non-positional audio we implemented:
- Ship creaking ambience. Fades out when the player wins.
- Ambient low drone. Fades out when the player wins.
- Ship sinking/rumble sounds when the player wins and the camera starts shaking. Fades out only after the win screen appears.
- Card noises (Playing, shuffling)
- Dialogue 'voice'
- Card game progression triggers (sinking of galleys, triggering of fight sequence)

Some examples of positional audio we implemented:
- A dynamic footstep system. Plays at parameterized intervals controlled in PlayerManager. Only plays when the player is walking.
- Treasure chest opening sound for when the key is used on the chest.
- UI sound for turning the clock hands in the clock puzzle.
- Key dropping on wood sound for when the key is dispensed after completing the clock puzzle.
- Note read sound for when the player uses the candle to read the note.

### Effects
Fully implemented within the game, we have water, shadows, ambient occlusion, screen shake.  Screen shake is not depicted below, but occurs during the sequence triggered by winning the game.
<a href="https://imgur.com/CdX5BIo"><img src="https://i.imgur.com/CdX5BIo.png" title="source: imgur.com" width="500" /></a>

Also mostly developed, but not integrated into the current submission, we have particle effects(fire) and a skybox.
<a href="https://imgur.com/UXDr1zO"><img src="https://i.imgur.com/UXDr1zO.png" title="source: imgur.com" width="500" /></a>

<a href="https://imgur.com/mWzWQqw"><img src="https://i.imgur.com/mWzWQqw.png" title="source: imgur.com" width="500" /></a>

These effects are not implemented and the shadow and ambient occlusion effects are in a toggleable testing state as unexpected difficulties prevented testing to resolve bugs and performance issues. These difficulties are also the reason why we do not currently have:
- a chest key model
- the opponent's model/animations
- clock opening animation trigger


# Member Contributions

## Shawn
- Puzzle logic
- Movement and look systems
- Object interaction and environment collision
- Puzzle and ambient environment sounds

## Jeremiah
- Puzzle logic
- Dialogue and game triggers
- Communication between puzzle and card game
- Card game and dialogue sounds

## Viet
- Environment modeling
- Model sourcing for puzzles
- Shadows and other vfx

# Demo
A recorded demo of our game.
[![Watch the video](https://raw.githubusercontent.com/BobTheHobo/bestestGame/merge/thumbnail.png)](https://raw.githubusercontent.com/BobTheHobo/bestestGame/merge/MutinyVideoDemo.mp4)

# Download
Download the game [here](https://livejohnshopkins-my.sharepoint.com/:f:/g/personal/sguo37_jh_edu/EiOjc5th0jdMutp0d8JDmYIBvaaLYh3GJI4BoElc2JoRKg?e=Tf6jgC).

# Future Work
This has only been a vertical slice of our vision for the game. Here are some planned features/changes we're looking to work on:
- Migration to a more modern game engine, like Unity or Godot, for better feature support
- Better puzzle replayability in the form of randomized puzzle solutions, so repeat players have a different experience for each playthrough
- More puzzle variety, leading to a longer quest for the "true" ending overall
- Have a main menu/title screen
- Add a tutorial to introduce players to the card game mechanics
- Add more stages in the form of more than one enemy that you face in the card game
- More cards and a better enemy AI to improve card game enjoyment
- Adding additional features through 'event deck' and game items
- More story aspects through dialogue



  
