package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.collision.CollisionResults;
import com.jme3.renderer.Camera;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionGroupListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.collision.CollisionResult;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.ui.Picture;
import com.mygame.viet_files.Key;
import com.jme3.bullet.BulletAppState;
import com.mygame.viet_files.SFXManager;
import com.mygame.viet_files.Util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/*
* author: shawn
*/
public class PlayerInteractionManager {

    private final InputManager inputManager;
    private final Camera cam;
    private final Node rootNode;
    private final PhysicsSpace physicsSpace;
    private final Node handNode; // Node representing the player's hand
    private final float pickupRange = 5f; // Adjust as needed
    private Spatial heldItem = null;
    private RigidBodyControl heldItemControl = null;
    
    private Spatial note;
    private Spatial candle;
    private static final float NOTE_INTERACTION_RANGE = 5f;
    private static final float CANDLE_NOTE_RANGE = 4f;
    
    private BitmapText rangeText;
    private BitmapText closeText;
    private float xOffsetAboveCrosshair = -70f;
    private float yOffsetAboveCrosshair = 30f;
    private final SimpleApplication app;

    private float collisionMargin = 0f; // Specifies default margin between geometry and collision shape
    
    private Picture noteImage;
    private boolean isReadingNote = false;
    
     //fields for the grandfather clock system
    private static final Vector3f CLOCK_LOCATION = new Vector3f(-3.8f, 0, -7f);
    private static final float CLOCK_INTERACTION_RANGE = 5f;
    private BitmapText clockText; // To display the current time
    private BitmapText clockProximityText; // To display "Press 'F' to turn clock"
    private boolean isClockUIActive = false;
    private LocalTime clockTime = LocalTime.of(3, 30); // Starting time
    private float correctTimeHeldDuration = 0f; // Tracks time held at the correct value
    private static final LocalTime CORRECT_TIME = LocalTime.of(2, 15); // Correct puzzle time
    private boolean isClockInteractable = true;
    private boolean clockPuzzleComplete = false;
    private BitmapText zPromptText;
    private BitmapText xPromptText;
    
    private static final Vector3f KEY_SPAWN_LOCATION = new Vector3f(-2f, 2f, -5f);
    private Key spawnedKey;
    private final BulletAppState bulletAppState;
    private Runnable onPuzzleComplete;
    private Runnable onCardGot;
    
    private Spatial key; // The key spatial for proximity checks
    private static final Vector3f CHEST_LOCATION = new Vector3f(-5f, 1f, 0.2f); // Treasure chest location
    private static final float CHEST_INTERACTION_RANGE = 4f;
    private static final float KEY_CHEST_RANGE = 4f;
    private BitmapText chestText;
    private boolean chestUnlocked = false;
    
    private BitmapText rewardText;
    private float rewardTextTimer = 0f; // Timer for showing the reward text
    private boolean showRewardText = false; // Flag to control visibility

    private SFXManager sfxManager;
    
    private Spatial keyNode; // Stores node of key

    public PlayerInteractionManager(SimpleApplication app, PhysicsSpace physicsSpace, SFXManager sfxManager) {
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.rootNode = app.getRootNode(); // Access rootNode directly
        this.physicsSpace = physicsSpace;
        
        this.app = app;
        
        bulletAppState = new BulletAppState();
        this.sfxManager = sfxManager;
        
        sfxManager.loadSFX("Chest-Open", "Sounds/SFX/Chest-Open.wav");
        sfxManager.loadSFX("Clock-Turn", "Sounds/SFX/Clock-Turn-16bit.wav");
        sfxManager.loadSFX("Key-Drop", "Sounds/SFX/Key-Drop-16bit.wav");
        sfxManager.loadSFX("Note-Read", "Sounds/SFX/Note-Read-16bit.wav");

	CollisionShape.setDefaultMargin(collisionMargin);

        // Create the hand node and attach it to the root node
        handNode = new Node("HandNode");
        app.getRootNode().attachChild(handNode);

	// 
        
        setupInput();
        setupText();
        setupCloseText();
        setupNoteImage();
        setupClockSystem();
        setupClockInput();
        setupChestText();
        setupRewardText(); 
   }
    
    private void setupCloseText() {
        closeText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        closeText.setText("Press 'Q' to close");
        closeText.setColor(ColorRGBA.White);

        closeText.setLocalTranslation(
            app.getCamera().getWidth() * 0.10f, // Position near the top-right
            app.getCamera().getHeight() * 0.8f,
            0
        );

        closeText.setCullHint(Spatial.CullHint.Always); // Start hidden
        app.getGuiNode().attachChild(closeText);
    }
    
    private void setupNoteImage() {
        noteImage = new Picture("Note Image");
        try {
            noteImage.setImage(app.getAssetManager(), "Textures/NotePlaceholder.png", true);
            System.out.println("Image loaded successfully."); // Debug: confirms image is loaded
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            return;
        }

        // Scale and position the image to cover most of the screen
        noteImage.setWidth(app.getCamera().getWidth() * 0.8f);
        noteImage.setHeight(app.getCamera().getHeight() * 0.12f);
        noteImage.setPosition(
            app.getCamera().getWidth() * 0.1f, // Offset from the left
            app.getCamera().getHeight() * 0.8f  // Offset from the bottom
        );

        noteImage.setCullHint(Spatial.CullHint.Always); // Start hidden
        app.getGuiNode().attachChild(noteImage);
        System.out.println("Note image setup complete."); // Debug: confirm setup completion
    }

    private void setupText() {
        rangeText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        rangeText.setColor(ColorRGBA.White);

        rangeText.setLocalTranslation(
            xOffsetAboveCrosshair + app.getCamera().getWidth() / 2 - rangeText.getLineWidth() / 2,
            yOffsetAboveCrosshair + app.getCamera().getHeight() / 2 + rangeText.getLineHeight() / 2,
            0
        );

        rangeText.setCullHint(Spatial.CullHint.Always); // Hide initially
        app.getGuiNode().attachChild(rangeText);
    }



    public void setNote(Spatial note) {
        this.note = note;
    }
    
    public void setCandle(Spatial candle) {
        this.candle = candle;
    }
    
    public void setupInput() {
        inputManager.addMapping("InteractWithNote", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("ExitReadingNote", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addListener(actionListenerNote, "InteractWithNote", "ExitReadingNote");
        inputManager.addMapping("UnlockChest", new com.jme3.input.controls.KeyTrigger(com.jme3.input.KeyInput.KEY_F));
        inputManager.addListener(actionListenerChest, "UnlockChest");
    }
    
    private final ActionListener actionListenerNote = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("InteractWithNote") && isPressed) {
                interactWithNote();
            } else if (name.equals("ExitReadingNote") && isPressed && isReadingNote) {
                exitReadingNote();
            }
        }
    };
    
    private final ActionListener actionListenerChest = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("UnlockChest") && isPressed) {
                unlockChest();
            }
        }
    };
    
    
    
    private void interactWithNote() {
        if (note != null && candle != null) {
            float distanceToNote = cam.getLocation().distance(note.getWorldTranslation());
            float candleToNoteDistance = candle.getWorldTranslation().distance(note.getWorldTranslation());

            if (distanceToNote <= NOTE_INTERACTION_RANGE && candleToNoteDistance <= CANDLE_NOTE_RANGE) {
                showNoteImage();
            }
        }
    }
    
    private void showNoteImage() {
        isReadingNote = true;
        noteImage.setCullHint(Spatial.CullHint.Never); // Show the note image
        rangeText.setCullHint(Spatial.CullHint.Always); // Hide the range text while reading
        closeText.setCullHint(Spatial.CullHint.Never); // Show the close text
        sfxManager.playSFX("Note-Read", this.note.getWorldTranslation());
        System.out.println("Showing note image."); // Debug
    }

    private void exitReadingNote() {
        isReadingNote = false;
        noteImage.setCullHint(Spatial.CullHint.Always); // Hide the note image
        closeText.setCullHint(Spatial.CullHint.Always); // Hide the close text
        updateRangeText(); // Restore range text if within interaction range
        sfxManager.playSFX("Note-Read", this.note.getWorldTranslation());
        System.out.println("Hiding note image."); // Debug
    }
    
    private void updateRangeText() {
        if (note == null || candle == null) return;

        // Check distance between the note and the player
        float playerToNoteDistance = cam.getLocation().distance(note.getWorldTranslation());
        
        // Check distance between the candle and the note
        float candleToNoteDistance = candle.getWorldTranslation().distance(note.getWorldTranslation());

        // Update rangeText based on distances
        if (playerToNoteDistance <= NOTE_INTERACTION_RANGE) {
            if (candleToNoteDistance <= CANDLE_NOTE_RANGE) {
                rangeText.setText("Press 'F' to read note");
            } else {
                rangeText.setText("Too dark to read");
            }
            rangeText.setCullHint(Spatial.CullHint.Never); // Show text
        } else {
            rangeText.setCullHint(Spatial.CullHint.Always); // Hide text
        }
    }

    private void pickUpItem() {
        try {
            // Cast a ray from the camera to detect items under the crosshair
            CollisionResults results = new CollisionResults();
            Vector3f origin = cam.getLocation();
            Vector3f direction = cam.getDirection();

            Ray ray = new Ray(origin, direction);
            rootNode.collideWith(ray, results);

            if (results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
                    Spatial target = null;
                    Spatial res = results.getCollision(i).getGeometry();

                    if (res.getUserData("puzzle") != null) {
                        target = res;	
                    } else if (res.getName().equals("Key4.001"))  {
                        target = res.getParent();
                        keyNode = target;
                    } else {
                        target = res.getParent().getParent().getParent();
                    } 
                    float distance = results.getCollision(i).getDistance();

                    // Check if the item is within pickup range
                    if (distance <= pickupRange && target != null) {
                        // Retrieve the canBePickedUp flag from the item's user data
                        Boolean canBePickedUp = (Boolean) target.getUserData("canBePickedUp");
                        System.out.println(target.getName() + " : " + canBePickedUp);
                        if (Boolean.TRUE.equals(canBePickedUp)) {
                            // Pick up the item
                            heldItem = target;
                            heldItemControl = heldItem.getControl(RigidBodyControl.class);

                            if (heldItemControl != null) {
                                // Remove collision with player (group 2)
                                heldItemControl.removeCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_02);

                                // Set the physics control to kinematic mode
                                heldItemControl.setKinematic(true);

                                // Turn off collision shape
                                heldItemControl.setEnabled(false);

                                // Optional: Set the gravity to zero, IDK IF I SHOULD DO THIS
    //                            heldItemControl.setGravity(Vector3f.ZERO);
                            }

                            // Detach from previous parent and attach to hand node
                            if (heldItem.getParent() != null) {
                                heldItem.getParent().detachChild(heldItem);
                            }
                            heldItem.setLocalTranslation(Vector3f.ZERO);
                            handNode.attachChild(heldItem);
                            try {
                                System.out.println("node " + heldItemControl.getSpatial().getName());
                            } catch (Exception e) {
                                // throw away
                            }

                            break; // Item picked up, exit loop
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // I will legit kill someone if i get another error from this
            System.out.println("pickup exception " + ex.toString());
        }
    }


    private void dropItem() {
        if (heldItem != null) {
            // Detach the item from the hand node
            handNode.detachChild(heldItem);

            // Reattach the item to the root node
            rootNode.attachChild(heldItem);

            // Calculate the drop position
            Vector3f dropPosition;

            // Cast a ray to determine where to drop the item
            CollisionResults results = new CollisionResults();
            Vector3f origin = cam.getLocation();
            Vector3f direction = cam.getDirection().clone();

            Ray ray = new Ray(origin, direction);
            ray.setLimit(pickupRange); // Limit the ray to the pickup range

            // Temporarily set the physics control to kinematic to avoid self-collision
            heldItemControl.setKinematic(true);

            // Perform collision detection with the scene
            rootNode.collideWith(ray, results);

	    // Get closest collision, if any
            CollisionResult closest = results.getClosestCollision();

            if (closest != null && closest.getDistance() <= pickupRange) {
		// Drop at the collision point, slightly offset to prevent overlap
		dropPosition = closest.getContactPoint().subtract(direction.mult(0.1f));
		System.out.println("Dropping at collision point: " + dropPosition);
            } else {
                // No collision within range; drop at maximum pickup range
                dropPosition = origin.add(direction.mult(pickupRange));
                System.out.println("Dropping at max range: " + dropPosition);
            }
            
            Node moveableNode = (Node)rootNode.getChild("Move object node");
            // Add object back to the moveable object node (which killplane depends on)
            // unless removed
            if (heldItem.getName().equals("keyremoved")) {
                // Clear held item references
                heldItem = null;
                heldItemControl = null;
                return;
            } else if (heldItem.getName().equals("key")) {
                ((Node)rootNode.getChild("Key node")).attachChild(heldItem);
                System.out.println("putting key back");
            } else {
                moveableNode.attachChild(heldItem);
            }

            // Update the spatial's local translation to the drop position
            heldItem.setLocalTranslation(dropPosition);

            // Set the physics control back to dynamic mode
            heldItemControl.setKinematic(false);

	    // Turn on physics control again
	    heldItemControl.setEnabled(true);

	    // Re-add collision with player (group 2)
	    heldItemControl.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_02);

            // Update the physics control's location to match the spatial
            heldItemControl.setPhysicsLocation(dropPosition);

            // Optional: Apply an impulse to simulate throwing
            // heldItemControl.setLinearVelocity(cam.getDirection().mult(5));

            // Clear held item references
            heldItem = null;
            heldItemControl = null;
        }
    }


    public void handleInteraction() {
        if (heldItem == null) {
            pickUpItem();
        } else {
            dropItem();
        }
    }
    
    private void setupClockSystem() {
        // Proximity text
        clockProximityText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        clockProximityText.setText("Press 'F' to turn clock");
        clockProximityText.setColor(ColorRGBA.White);
        clockProximityText.setLocalTranslation(
            app.getCamera().getWidth() / 2f - 100,
            app.getCamera().getHeight() / 2f + 50,
            0
        );
        clockProximityText.setCullHint(Spatial.CullHint.Always);
        app.getGuiNode().attachChild(clockProximityText);

        // Clock text
        clockText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        clockText.setText(formatClockTime(clockTime));
        clockText.setColor(ColorRGBA.White);
        clockText.setLocalTranslation(
            app.getCamera().getWidth() / 2f - 35,
            app.getCamera().getHeight() / 2f - 10,
            0
        );
        clockText.setCullHint(Spatial.CullHint.Always);
        app.getGuiNode().attachChild(clockText);

        // Z prompt text
        zPromptText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        zPromptText.setText("'Z'");
        zPromptText.setColor(ColorRGBA.White);
        zPromptText.setLocalTranslation(
            clockText.getLocalTranslation().x - 40, // Offset to the bottom-left of the clock text
            clockText.getLocalTranslation().y - 20, // Slightly below the clock text
            0
        );
        zPromptText.setCullHint(Spatial.CullHint.Always); // Start hidden
        app.getGuiNode().attachChild(zPromptText);

        // X prompt text
        xPromptText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        xPromptText.setText("'X'");
        xPromptText.setColor(ColorRGBA.White);
        xPromptText.setLocalTranslation(
            clockText.getLocalTranslation().x + 100, // Offset to the bottom-right of the clock text
            clockText.getLocalTranslation().y - 20, // Slightly below the clock text
            0
        );
        xPromptText.setCullHint(Spatial.CullHint.Always); // Start hidden
        app.getGuiNode().attachChild(xPromptText);
    }
    
    private void showClockUI() {
        isClockUIActive = true;
        clockText.setCullHint(Spatial.CullHint.Never); // Show the clock time
        zPromptText.setCullHint(Spatial.CullHint.Never); // Show 'Z' prompt
        xPromptText.setCullHint(Spatial.CullHint.Never); // Show 'X' prompt
        clockProximityText.setCullHint(Spatial.CullHint.Always); // Hide proximity text
    }

    private void hideClockUI() {
        isClockUIActive = false;
        clockText.setCullHint(Spatial.CullHint.Always); // Hide the clock time
        zPromptText.setCullHint(Spatial.CullHint.Always); // Hide 'Z' prompt
        xPromptText.setCullHint(Spatial.CullHint.Always); // Hide 'X' prompt
        clockProximityText.setCullHint(Spatial.CullHint.Never); // Show proximity text if still near
    }

    
        // Format clock time as HH:MM (AM/PM)
    private String formatClockTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    
    // Check proximity to the grandfather clock
    private boolean isNearClock() {
        return cam.getLocation().distance(CLOCK_LOCATION) <= CLOCK_INTERACTION_RANGE;
    }
    
    // Handle update logic for proximity and UI
    private void updateClockProximityText() {
        if (!isClockInteractable) {
            clockProximityText.setCullHint(Spatial.CullHint.Always); // Hide text if clock is not interactable
            return;
        }

        if (isNearClock() && !isClockUIActive) {
            clockProximityText.setCullHint(Spatial.CullHint.Never); // Show proximity text
        } else {
            clockProximityText.setCullHint(Spatial.CullHint.Always); // Hide proximity text
        }

        // Automatically close clock UI if player leaves interaction range
        if (isClockUIActive && !isNearClock()) {
            exitClockInteraction();
        }
    }

    private void interactWithClock() {
        if (!isClockInteractable) return; // Skip if the clock is no longer interactable

        if (isNearClock()) {
            showClockUI(); // Show the clock UI when the player interacts
            System.out.println("Clock interaction started."); // Debug message
        }
    }

    
    // Exit clock interaction logic
    private void exitClockInteraction() {
        isClockUIActive = false;
        hideClockUI(); // Consolidate UI hiding logic
        System.out.println("Exiting clock interaction."); // Debug
    }

    // Update the displayed time
    private void updateClockTime(int minutes) {
        if (!isClockUIActive) return;

        clockTime = clockTime.plusMinutes(minutes);
        clockText.setText(formatClockTime(clockTime));
    }

    // Check if the puzzle is solved
    private void checkClockPuzzle(float tpf) {
        if (!isClockInteractable || !isClockUIActive) return;

        if (clockTime.equals(CORRECT_TIME)) {
            correctTimeHeldDuration += tpf;

            if (correctTimeHeldDuration >= 3f) {
                System.out.println("Puzzle Complete!"); // Puzzle completion feedback
                completeClockPuzzle();

                // Mark the puzzle as complete and disable interaction
                isClockInteractable = false;
                exitClockInteraction(); // Close the clock UI
                clockProximityText.setCullHint(Spatial.CullHint.Always); // Hide proximity text
                onPuzzleComplete.run();
            }
        } else {
            correctTimeHeldDuration = 0f; // Reset timer if not holding correct time
        }
    }
    
    private void completeClockPuzzle() {
        sfxManager.playSFX("Key-Drop", CLOCK_LOCATION);
        clockPuzzleComplete = true;
    }
    
    public boolean getIsClockPuzzleComplete() {
        return clockPuzzleComplete;
    }
    
    // Add key mappings and actions for clock interaction and time adjustment
    private void setupClockInput() {
        inputManager.addMapping("InteractWithClock", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("TurnClockBackward", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("TurnClockForward", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addListener(clockActionListener, "InteractWithClock", "TurnClockBackward", "TurnClockForward");
    }

    private final ActionListener clockActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("InteractWithClock") && isPressed) {
                interactWithClock();
            } else if (name.equals("TurnClockBackward") && isPressed) {
                updateClockTime(-5);
                sfxManager.playSFX("Clock-Turn", CLOCK_LOCATION);
            } else if (name.equals("TurnClockForward") && isPressed) {
                updateClockTime(5);
                sfxManager.playSFX("Clock-Turn", CLOCK_LOCATION);
            }
        }
    };
    
        // Method to set the listener
    public void setOnPuzzleCompleteListener(Runnable listener) {
        this.onPuzzleComplete = listener;
    }
    
        // Method to set the card get listener
    public void setOnCardGotten(Runnable listener) {
        this.onCardGot = listener;
    }
   
    
    private void setupChestText() {
        chestText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        chestText.setColor(ColorRGBA.White);

        chestText.setLocalTranslation(
            app.getCamera().getWidth() / 2 - chestText.getLineWidth() / 2 - 35,
            app.getCamera().getHeight() / 2 + chestText.getLineHeight() + 20,
            0
        );

        chestText.setCullHint(BitmapText.CullHint.Always); // Start hidden
        app.getGuiNode().attachChild(chestText);
    }
    
    public void setKey(Spatial key) {
        this.key = key;
    }   
    
    private void unlockChest() {
        if (chestUnlocked || key == null) return;

        float playerToChestDistance = app.getCamera().getLocation().distance(CHEST_LOCATION);
        float keyToChestDistance = key.getWorldTranslation().distance(CHEST_LOCATION);

        if (playerToChestDistance <= CHEST_INTERACTION_RANGE && keyToChestDistance <= KEY_CHEST_RANGE) {
            chestUnlocked = true;
            onCardGot.run();
            chestText.setCullHint(BitmapText.CullHint.Always); // Hide the chest prompt
            System.out.println("chest opened");

            // Show the reward text
            showRewardText = true;
            rewardText.setCullHint(BitmapText.CullHint.Never); // Show the reward text
            rewardTextTimer = 3f; // Start the timer for 3 seconds
	    sfxManager.playSFX("Chest-Open", CHEST_LOCATION);
        }
    }
    
    private void updateChestText() {
        if (chestUnlocked) return; // Skip if chest is already unlocked

        float playerToChestDistance = app.getCamera().getLocation().distance(CHEST_LOCATION);

        if (playerToChestDistance <= CHEST_INTERACTION_RANGE) {
            if (key != null) { // Only check key distance if the key exists
                float keyToChestDistance = key.getWorldTranslation().distance(CHEST_LOCATION);
                if (keyToChestDistance <= KEY_CHEST_RANGE) {
                    chestText.setText("Press 'F' to unlock");
                } else {
                    chestText.setText("Locked");
                }
            } else {
                chestText.setText("Locked"); // Show "Locked" if key isn't present
            }
            chestText.setCullHint(BitmapText.CullHint.Never); // Show the text
        } else {
            chestText.setCullHint(BitmapText.CullHint.Always); // Hide the text
        }
    }

    private void setupRewardText() {
        rewardText = new BitmapText(app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"), false);
        rewardText.setText("You collected a card: \"The Kraken\""); // The reward text
        rewardText.setColor(ColorRGBA.White);

        // Position near the top-center of the screen
        rewardText.setLocalTranslation(
            app.getCamera().getWidth() * 0.5f - rewardText.getLineWidth() * 0.5f,
            app.getCamera().getHeight() * 0.8f,
            0
        );

        rewardText.setCullHint(BitmapText.CullHint.Always); // Start hidden
        app.getGuiNode().attachChild(rewardText);
    }



    public void update(float tpf) {
        // Update the hand node's position and rotation to match the camera
        handNode.setLocalTranslation(cam.getLocation().add(cam.getDirection().mult(2f)));
        handNode.setLocalRotation(cam.getRotation());
        
        if (!isReadingNote) {
           updateRangeText(); 
        }
        updateClockProximityText();
        checkClockPuzzle(tpf);
        updateChestText();
        
        // Handle reward text visibility
        if (showRewardText) {
            rewardTextTimer -= tpf;
            if (rewardTextTimer <= 0) {
                rewardText.setCullHint(BitmapText.CullHint.Always); // Hide the reward text
                showRewardText = false;
            }
        }
    }
    
    public boolean getChestUnlocked() {
        return chestUnlocked;
    }
} 