package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;  // Physics system
    private CharacterControl player;        // Player control
    private Node playerNode;                // Player node

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);  // Attach physics to the state manager

        flyCam.setEnabled(false);  // Disable flyCam for first-person control

        setupPlayer();  // Set up player character
        setupScene();   // Set up the environment
    }

    // Initialize player with movement and collision
    private void setupPlayer() {
        // Create a capsule collision shape for the player
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);  // Add physics control

        // Initialize the player node and attach the player control
        playerNode = new Node("Player");
        playerNode.addControl(player);

        // Set initial position of the player
        player.setPhysicsLocation(new Vector3f(0, 1, 0));

        // Add player control to the physics space
        bulletAppState.getPhysicsSpace().add(player);
        rootNode.attachChild(playerNode);
    }

    // Set up the scene with the floor, walls, obstacles, and pickable items
    private void setupScene() {
        // Create the floor (a large box)
        createBox("Floor", new Vector3f(0, -1, 0), new Vector3f(10, 1, 10), ColorRGBA.Orange, false);
        
        // Create walls (four boxes placed around the floor)
        createBox("Wall1", new Vector3f(0, 2, -10), new Vector3f(10, 2, 1), ColorRGBA.Brown, false); // Back wall
        createBox("Wall2", new Vector3f(0, 2, 10), new Vector3f(10, 2, 1), ColorRGBA.Brown, false);  // Front wall
        createBox("Wall3", new Vector3f(-10, 2, 0), new Vector3f(1, 2, 10), ColorRGBA.Brown, false); // Left wall
        createBox("Wall4", new Vector3f(10, 2, 0), new Vector3f(1, 2, 10), ColorRGBA.Brown, false);  // Right wall
        
        // Create obstacles (non-interactable colored boxes)
        createBox("Obstacle1", new Vector3f(5, 1, 5), new Vector3f(1, 1, 1), ColorRGBA.Gray, false);  // Obstacle
        createBox("Obstacle2", new Vector3f(-5, 1, -5), new Vector3f(1, 1, 1), ColorRGBA.Gray, false);  // Obstacle
        
        // Create pickable items (small boxes, same color, all pickupable)
        createBox("Item1", new Vector3f(3, 0.3f, 3), new Vector3f(0.3f, 0.3f, 0.3f), ColorRGBA.Green, true);  // Pickable item
        createBox("Item2", new Vector3f(-3, 0.3f, -3), new Vector3f(0.3f, 0.3f, 0.3f), ColorRGBA.Green, true); // Pickable item
    }

    // Utility method to create and add a box with a specific position, size, and color, and optional pickup flag
    private void createBox(String name, Vector3f position, Vector3f size, ColorRGBA color, boolean canBePickedUp) {
        Box box = new Box(size.x, size.y, size.z);
        Geometry geom = new Geometry(name, box);
        geom.setLocalTranslation(position);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);

        // Set a user-defined flag to indicate if the object can be picked up
        if (canBePickedUp) {
            geom.setUserData("canBePickedUp", true);
        }

        rootNode.attachChild(geom);
    }
}
