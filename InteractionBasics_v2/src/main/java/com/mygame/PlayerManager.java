package com.mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.mygame.viet_files.InputHandlers.InputHandler;

/*
* author: shawn
*/
public class PlayerManager {

    private CharacterControl player;     // Player control
    private Node playerNode;             // Player node
    private BulletAppState bulletAppState;
    private Camera cam;                  // Camera for look direction
    private CameraManager cameraManager;
    private InputHandler inputHandler;   // For handling input
    private PlayerInteractionManager interactionManager; // Handle interactions
    private float playerMoveSpeed = 0.1f;
    private boolean walkingEnabled = false;

    public PlayerManager(BulletAppState bulletAppState, Node rootNode, Camera cam, CameraManager cameraManager, InputHandler inputHandler, PlayerInteractionManager interactionManager, AppSettings settings) {
        this.bulletAppState = bulletAppState;
        this.cam = cam;
	this.cameraManager = cameraManager;
        this.inputHandler = inputHandler;
	this.interactionManager = interactionManager;

	this.walkingEnabled = false;
    }

    // Initialize the player with movement and collision
    public void setupPlayer() {
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.25f, 2f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);  // Add physics control

        player.getCollisionShape().setMargin(0.01f);  // Reduce collision margin for more precise collision detection

        playerNode = new Node("Player");
        playerNode.addControl(player);

        player.setPhysicsLocation(new Vector3f(0, 2, 0));
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);

        bulletAppState.getPhysicsSpace().add(player);

        // No need to hide the mouse cursor here; it's handled by FlyCam
    }

    public void setWalkingEnabled(boolean enabled) {
	walkingEnabled = enabled;
	if (walkingEnabled) {
		movePlayerToCamera();
	} 
    }

    // Return the player's current position (for camera)
    public Vector3f getPlayerPosition() {
        return player.getPhysicsLocation();
    }

    public void movePlayerToCamera() {
	player.setPhysicsLocation(cam.getLocation());
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
}
