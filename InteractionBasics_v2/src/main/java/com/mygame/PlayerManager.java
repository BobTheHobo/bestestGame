package com.mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.mygame.viet_files.InputHandlers.InputHandler;
import com.mygame.viet_files.SFXManager;

public class PlayerManager {

    private CharacterControl player;     // Player control
    private Node playerNode;             // Player node
    private BulletAppState bulletAppState;
    private Camera cam;                  // Camera for look direction
    private CameraManager cameraManager;
    private InputHandler inputHandler;   // For handling input
    private PlayerInteractionManager interactionManager; // Handle interactions
    private AssetManager assetManager;
    private float playerMoveSpeed = 0.1f;
    private boolean walkingEnabled = false;

    private SFXManager sfxManager;       // SFX Manager for playing sounds
    private float footstepInterval = 5f; // Interval between footsteps in seconds
    private float footstepTimer = 0f;     // Timer to track footstep intervals

    private Vector3f spawnPos = new Vector3f(0, 2f, 0);
    
    public PlayerManager(BulletAppState bulletAppState, Node rootNode, Camera cam, CameraManager cameraManager, InputHandler inputHandler, 
            PlayerInteractionManager interactionManager, AppSettings settings, AssetManager assetManager) {
        this.bulletAppState = bulletAppState;
        this.cam = cam;
        this.cameraManager = cameraManager;
        this.inputHandler = inputHandler;
        this.interactionManager = interactionManager;
        this.assetManager = assetManager;

        this.walkingEnabled = false;
    }

    // Initialize the player with movement and collision
    public void setupPlayer() {
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.25f, 2f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);  // Add physics control

        player.getCollisionShape().setMargin(0.01f);  // Reduce collision margin for more precise collision detection

        // Set player to collision group 2
        player.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        // Allow player to collide with environment (default is collision group 1)
        player.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
        System.out.println("Player collision group: " + player.getCollisionGroup());

        playerNode = new Node("Player");
        playerNode.addControl(player);

        player.setPhysicsLocation(spawnPos);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);

        bulletAppState.getPhysicsSpace().add(player);

        // Initialize the SFXManager
        sfxManager = new SFXManager(assetManager);
        sfxManager.loadSFX("footstep", "Sounds/SFX/Footstep-16bit.wav");
    }

    public void setWalkingEnabled(boolean enabled) {
        walkingEnabled = enabled;
        if (walkingEnabled) {
            movePlayerToCamera();
            cameraManager.setupCamera();
        }
    }

    // Return the player's current position (for camera)
    public Vector3f getPlayerPosition() {
        return player.getPhysicsLocation();
    }

    public void movePlayerToCamera() {
        player.setPhysicsLocation(cam.getLocation());
    }
    
    public void resetPlayerPosition() {
        cam.setLocation(spawnPos);
        movePlayerToCamera();
    }

    // Handle player movement based on key inputs
    public void movePlayer(boolean left, boolean right, boolean forward, boolean backward) {
        Vector3f walkDirection = new Vector3f();

        // Use camera direction for movement
        Vector3f camDir = cam.getDirection().clone().setY(0).normalizeLocal();
        Vector3f camLeft = cam.getLeft().clone().setY(0).normalizeLocal();

        if (forward) {
            walkDirection.addLocal(camDir.mult(playerMoveSpeed));
        }
        if (backward) {
            walkDirection.addLocal(camDir.negate().multLocal(playerMoveSpeed));
        }
        if (left) {
            walkDirection.addLocal(camLeft.mult(playerMoveSpeed));
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate().multLocal(playerMoveSpeed));
        }

        player.setWalkDirection(walkDirection);
        player.setViewDirection(camDir);  // Orient the player to face the camera direction

        // Update footstep timer and play sound if moving
        if (!walkDirection.equals(Vector3f.ZERO)) {
            footstepTimer += playerMoveSpeed;
            if (footstepTimer >= footstepInterval) {
                // Play footstep sound at player's position
                sfxManager.playSFX("footstep", player.getPhysicsLocation());
                footstepTimer = 0f; // Reset the timer
            }
        } else {
            // Reset timer if player stops moving
            footstepTimer = 0f;
        }
    }

    public void updatePlayer(float tpf) {
        if (walkingEnabled) {
            // Apply movement based on input handler's movement flags
            movePlayer(
                inputHandler.gih.isLeft(),
                inputHandler.gih.isRight(),
                inputHandler.gih.isForward(),
                inputHandler.gih.isBackward()
            );

            // Update camera
            cameraManager.playerMovementCameraUpdate(tpf, getPlayerPosition());

            // Update the interaction manager
            interactionManager.update(tpf);
        }
    }
    
    public void cleanup() {
        playerNode.removeControl(player);
    }
}
