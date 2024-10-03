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
TBD

### Set Modeling
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
<blockquote class="imgur-embed-pub" lang="en" data-id="a/pAj2WXc"  ><a href="//imgur.com/a/pAj2WXc">Movement/Free Look</a></blockquote><script async src="//s.imgur.com/min/embed.js" charset="utf-8"></script>

Collisions and physics are handled through the jBullet plugin. The player and all objects in the scene are affected by collisions. If an object has the `canBePickedUp` tag (green object in demo), the player can "pick up" the object if they are in a defined range and are looking at the object. The object will float at the center of the screen until it is dropped.
<blockquote class="imgur-embed-pub" lang="en" data-id="a/a38lkK5"  ><a href="//imgur.com/a/a38lkK5">Picking Up</a></blockquote><script async src="//s.imgur.com/min/embed.js" charset="utf-8"></script>


### Set Modeling
The ship hull that acts as the main room for the game was created in Blender. It was modeled by hand using a cylinder base mesh and gradually adding the walls and ceiling to achieve the current look. It has a grate at the top that will be used to let in light, and features portholes (circular windows) as well as larger, rectangular windows that will allow the player to look out and observe the ocean/world in a future update. Inside the room is a table, chest, candle, and grandfather clock, all sourced from Poly Pizza and credits linked below. The table is used to play the card game. The chest and grandfather clock will have puzzle functionality attached to them, but is not present at this time. All of the items were attached to nodes, and then the nodes attached to the rootnode using helper methods for each object. 

Right now, the candle acts as the sole lighting for the scene, but sets the atmosphere as a dark, dimly lit hold that a prisoner would be held in. The candle doesn't light up the entirety of the scene which is by design, as in the future we want the player to use the candle and/or a lantern to illuminate certain objects for the puzzles, and as mentioned previously makes the game feel more ominous and dark. The candle uses a point light with an orange color, which is attached to a node above the candle model. The reason for this choice is to allow the light to illuminate the candle itself, and cast light below itself more effectively. Setting the point light right at the flame part of the model results in the base of the candle and the flame itself to not be lit. We plan to add more lights through the form of lanterns, the sun/moon using directional light, and perhaps other items/cards.

![Perspective sitting at table while playing](https://github.com/user-attachments/assets/eb9129af-0cfe-47dc-bdde-68bcadcdf11f)
![Looking left at table](https://github.com/user-attachments/assets/52b5908c-a057-4f26-b9f9-24da4ff997b5)
![Looking right at table](https://github.com/user-attachments/assets/a1ece375-5be6-461f-9ad0-747bb012d6e4)
![Looking into room from windows](https://github.com/user-attachments/assets/d8b1f5ff-d4a2-4066-b69c-47627d2a6cb6)
![Looking inside from doorway](https://github.com/user-attachments/assets/59e83ea6-5923-46ae-8470-bcc263761bee)
![Hull Model in Blender](https://github.com/user-attachments/assets/3d45f8ee-b7f3-41f5-94e1-bf20a0ac569d)

#### Model Attributions:
- Candle: Candle by Nick Slough [CC-BY](https://creativecommons.org/licenses/by/3.0/) via Poly Pizza (https://poly.pizza/m/HFpLq6iqKu)
- Table: Table by Darwin Yamamoto [CC-BY](https://creativecommons.org/licenses/by/3.0/) via Poly Pizza (https://poly.pizza/m/2UW71XCeyGh)
- Grandfather Clock: Grandfathers Clock by CreativeTrio (https://poly.pizza/m/09YKIkFZnA)
- Chest: Chest by Quaternius (https://poly.pizza/m/O72u4Drp8k)

### Running Instructions
Transition from the Card Game "scene" to the interaction demo "scene" using 'S' while sitting.
Transition from the interaction demo "scene" to the model demo "scene" using 'P'.


  
