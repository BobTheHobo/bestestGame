package com.mygame;

import com.mygame.viet_files.InputHandlers.InputHandler;
import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;

/*
* author: shawn
*/

public class CameraManager {

    private SimpleApplication app;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private InputHandler inputHandler;
    private PlayerManager playerManager;
    private PlayerInteractionManager interactionManager;

    public CameraManager(SimpleApplication app, PlayerManager playerManager, PlayerInteractionManager interactionManager, InputHandler inputHandler) {
	this.app = app;
        this.flyCam = app.getFlyByCamera();
        this.inputManager = app.getInputManager();

	this.playerManager = playerManager;
	this.interactionManager = interactionManager;
	this.inputHandler = inputHandler;

        setupCamera();
    }

    private void setupCamera() {
        // Enable FlyCam and customize it
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(0);          // Disable movement
        flyCam.setRotationSpeed(2.0f);   // Adjust rotation speed as needed
        flyCam.setDragToRotate(false);   // Ensure mouse movement rotates the camera
        flyCam.setZoomSpeed(0);          // Disable zoom

        // Hide the cursor
        inputManager.setCursorVisible(false);
    }

    public void playerMovementCameraUpdate(float tpf, Vector3f position) {
		// Synchronize the camera position with the player
		float playerHeight = 1f;  // Adjust based on your player's height
		Vector3f playerPosition = position;

		if (playerPosition != null) {
		    app.getCamera().setLocation(playerPosition.add(0, playerHeight, 0));
		} else {
		    System.out.println("Player position is null");
		}
    }
}
