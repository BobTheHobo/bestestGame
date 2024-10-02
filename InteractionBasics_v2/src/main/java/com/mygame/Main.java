package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;  // Physics system
    private PlayerManager playerManager;
    private SceneManager sceneManager;
    private boolean left = false, right = false, forward = false, backward = false;  // Movement flags

    public static void main(String[] args) {
        Main app = new Main();

        // Force fullscreen with AppSettings
        AppSettings settings = new AppSettings(true);
        settings.setResolution(640, 480);  // Set to your desired resolution
        settings.setFullscreen(false);        // Enable fullscreen mode
        settings.setTitle("InteractionBasics");

        app.setSettings(settings);
        app.setShowSettings(false);  // Skip the JMonkeyEngine settings dialog
        app.start();
    }
   
    @Override
    public void simpleInitApp() {
        // Initialize Bullet Physics System
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Enable FlyCam and customize it
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(0);          // Disable movement
        flyCam.setRotationSpeed(2.0f);   // Adjust rotation speed as needed
        flyCam.setDragToRotate(false);   // Disable drag to rotate, so the cursor is hidden and locked
        flyCam.setZoomSpeed(0);          // Disable zoom

        // Hide the cursor (FlyCam handles this)
        inputManager.setCursorVisible(false);  // This might be redundant, but ensures cursor is hidden

        // Initialize player and scene managers
        playerManager = new PlayerManager(bulletAppState, rootNode, cam, inputManager, settings);
        sceneManager = new SceneManager(bulletAppState, rootNode, assetManager);

        // Set up the player and the scene
        playerManager.setupPlayer();
        sceneManager.setupScene();

        // Set up key mappings for player movement
        setupKeys();
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Synchronize the camera position with the player
        float playerHeight = 0f;  // ADJUST
        cam.setLocation(playerManager.getPlayerPosition().add(0, playerHeight, 0));

        // Apply movement based on key presses
        playerManager.movePlayer(left, right, forward, backward);
    }


    // Set up the key mappings for movement
    private void setupKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));

        inputManager.addListener(actionListener, "Left", "Right", "Forward", "Backward");
    }
    
    

    // Create an ActionListener to handle movement inputs
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            switch (name) {
                case "Left":
                    left = isPressed;
                    break;
                case "Right":
                    right = isPressed;
                    break;
                case "Forward":
                    forward = isPressed;
                    break;
                case "Backward":
                    backward = isPressed;
                    break;
            }
        }
    };
}
