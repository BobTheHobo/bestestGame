package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;

public class CameraManager {

    private FlyByCamera flyCam;
    private InputManager inputManager;

    public CameraManager(SimpleApplication app) {
        this.flyCam = app.getFlyByCamera();
        this.inputManager = app.getInputManager();

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
}
