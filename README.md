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
- Basic card game function
- First Person Movement and world interaction
- Set Modeling

  
