package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.mygame.viet_files.InputHandlers.InputHandler;
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

    private boolean walkingEnabled;
    private float currentPitch = 0f;  // Current vertical pitch of the camera
    private final float maxPitch = 89f; // Maximum pitch (in degrees)
    private final float minPitch = -89f; // Minimum pitch (in degrees)

    public CameraManager(SimpleApplication app, PlayerManager playerManager, PlayerInteractionManager interactionManager, InputHandler inputHandler) {
        this.app = app;
        this.flyCam = app.getFlyByCamera();
        this.inputManager = app.getInputManager();

        this.playerManager = playerManager;
        this.interactionManager = interactionManager;
        this.inputHandler = inputHandler;

        this.walkingEnabled = false;

        setupCamera();
    }

    public void setupCamera() {
        // Enable FlyCam and customize it
        toggleCameraWalking(true);
        flyCam.setMoveSpeed(0);          // Disable movement
        flyCam.setRotationSpeed(2.0f);   // Adjust rotation speed as needed
        flyCam.setDragToRotate(false);   // Ensure mouse movement rotates the camera
        flyCam.setZoomSpeed(0);          // Disable zoom

        // Hide the cursor
        inputManager.setCursorVisible(false);
    }

    public void playerMovementCameraUpdate(float tpf, Vector3f position) {
        if (walkingEnabled) {
            // Synchronize the camera position with the player
            float playerHeight = 1f;  // Adjust based on your player's height
            Vector3f playerPosition = position;

            if (playerPosition != null) {
                app.getCamera().setLocation(playerPosition.add(0, playerHeight, 0));
            } else {
                System.out.println("Player position is null");
            }

            // Clamp the camera's vertical rotation (pitch)
            clampCameraPitch();
        }
    }

    public void toggleCameraWalking() {
        walkingEnabled = !walkingEnabled;
        toggleCameraWalking(walkingEnabled);
    }

    // Overloaded method allowing you to specify flycam on or off
    public void toggleCameraWalking(boolean enabled) {
        walkingEnabled = enabled;
        flyCam.setEnabled(true); // Requires flycam on to function

        if (walkingEnabled) {
            System.out.println("Walking on");
            flyCam.setMoveSpeed(0);          // Disable movement
            flyCam.setDragToRotate(false);   // Ensure mouse movement rotates the camera
            flyCam.setZoomSpeed(0);          // Disable zoom
        } else {
            System.out.println("Walking off");
            flyCam.setMoveSpeed(2);          
        }
    }

    private void clampCameraPitch() {
        // Get the camera's current rotation
        Quaternion rotation = app.getCamera().getRotation();

        // Extract pitch (vertical rotation)
        float[] angles = new float[3];
        rotation.toAngles(angles);
        currentPitch = angles[0] * FastMath.RAD_TO_DEG; // Convert pitch to degrees

        // Clamp the pitch between minPitch and maxPitch
        currentPitch = FastMath.clamp(currentPitch, minPitch, maxPitch);

        // Reapply the clamped pitch
        Quaternion clampedRotation = new Quaternion();
        clampedRotation.fromAngles(currentPitch * FastMath.DEG_TO_RAD, angles[1], angles[2]); // Keep other axes as-is
        app.getCamera().setRotation(clampedRotation);
    }
}
