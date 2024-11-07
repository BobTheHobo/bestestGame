package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.collision.CollisionResults;
import com.jme3.renderer.Camera;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.collision.CollisionResult;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.ui.Picture;
import com.jme3.texture.Texture;

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
    
    private Picture noteImage;
    private boolean isReadingNote = false;

    public PlayerInteractionManager(SimpleApplication app, PhysicsSpace physicsSpace) {
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.rootNode = app.getRootNode(); // Access rootNode directly
        this.physicsSpace = physicsSpace;
        
        this.app = app;

        // Create the hand node and attach it to the root node
        handNode = new Node("HandNode");
        app.getRootNode().attachChild(handNode);
        
        setupInput();
        setupText();
        setupCloseText();
        setupNoteImage();
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
        inputManager.addListener(actionListener, "InteractWithNote", "ExitReadingNote");
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("InteractWithNote") && isPressed) {
                interactWithNote();
            } else if (name.equals("ExitReadingNote") && isPressed && isReadingNote) {
                exitReadingNote();
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
        System.out.println("Showing note image."); // Debug
    }

    private void exitReadingNote() {
        isReadingNote = false;
        noteImage.setCullHint(Spatial.CullHint.Always); // Hide the note image
        closeText.setCullHint(Spatial.CullHint.Always); // Hide the close text
        updateRangeText(); // Restore range text if within interaction range
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
		} else {
			target = res.getParent().getParent().getParent();
		}
                float distance = results.getCollision(i).getDistance();

                // Check if the item is within pickup range
                if (distance <= pickupRange) {
                    // Retrieve the canBePickedUp flag from the item's user data
                    Boolean canBePickedUp = (Boolean) target.getUserData("canBePickedUp");
                    if (Boolean.TRUE.equals(canBePickedUp)) {
                        // Pick up the item
                        heldItem = target;
                        heldItemControl = heldItem.getControl(RigidBodyControl.class);

                        if (heldItemControl != null) {
                            // Set the physics control to kinematic mode
                            heldItemControl.setKinematic(true);
                            // Optional: Set the gravity to zero, IDK IF I SHOULD DO THIS
//                            heldItemControl.setGravity(Vector3f.ZERO);
                        }

                        // Detach from previous parent and attach to hand node
                        if (heldItem.getParent() != null) {
                            heldItem.getParent().detachChild(heldItem);
                        }
                        heldItem.setLocalTranslation(Vector3f.ZERO);
                        handNode.attachChild(heldItem);

                        break; // Item picked up, exit loop
                    }
                }
            }
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

            if (results.size() > 0) {
                // Drop at the collision point, slightly offset to prevent overlap
                CollisionResult closest = results.getClosestCollision();
                dropPosition = closest.getContactPoint().subtract(direction.mult(0.1f));
                System.out.println("Dropping at collision point: " + dropPosition);
            } else {
                // No collision within range; drop at maximum pickup range
                dropPosition = origin.add(direction.mult(pickupRange));
                System.out.println("Dropping at max range: " + dropPosition);
            }

            // Update the spatial's local translation to the drop position
            heldItem.setLocalTranslation(dropPosition);

            // Set the physics control back to dynamic mode
            heldItemControl.setKinematic(false);

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



    public void update(float tpf) {
        // Update the hand node's position and rotation to match the camera
        handNode.setLocalTranslation(cam.getLocation().add(cam.getDirection().mult(2f)));
        handNode.setLocalRotation(cam.getRotation());
        
        if (!isReadingNote) {
           updateRangeText(); 
        }  
    }
} 