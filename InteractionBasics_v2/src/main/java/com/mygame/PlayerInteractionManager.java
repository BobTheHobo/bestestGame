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
import com.jme3.scene.Spatial.CullHint;

public class PlayerInteractionManager {

    private final InputManager inputManager;
    private final Camera cam;
    private final Node rootNode;
    private final PhysicsSpace physicsSpace;
    private final Node handNode; // Node representing the player's hand
    private final float pickupRange = 5f; // Adjust as needed
    private Spatial heldItem = null;
    private RigidBodyControl heldItemControl = null;

    public PlayerInteractionManager(SimpleApplication app, PhysicsSpace physicsSpace) {
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.rootNode = app.getRootNode(); // Access rootNode directly
        this.physicsSpace = physicsSpace;

        // Create the hand node and attach it to the root node
        handNode = new Node("HandNode");
        app.getRootNode().attachChild(handNode);

//        setupInput();
    }

//    private void setupInput() {
//        inputManager.addMapping("Interact", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
//        inputManager.addListener(actionListener, "Interact");
//    }

//    private final ActionListener actionListener = new ActionListener() {
//        @Override
//        public void onAction(String name, boolean isPressed, float tpf) {
//            if (name.equals("Interact") && isPressed) {
//                if (heldItem == null) {
//                    pickUpItem();
//                } else {
//                    dropItem();
//                }
//            }
//        }
//    };

    private void pickUpItem() {
        // Cast a ray from the camera to detect items under the crosshair
        CollisionResults results = new CollisionResults();
        Vector3f origin = cam.getLocation();
        Vector3f direction = cam.getDirection();

        Ray ray = new Ray(origin, direction);
        rootNode.collideWith(ray, results);

        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Spatial target = results.getCollision(i).getGeometry();
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
    }
}
