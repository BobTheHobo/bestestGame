package com.mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;

public class InteractiveDemoAppState extends AbstractAppState {

    private SimpleApplication app;
    private BulletAppState bulletAppState;  // Physics system
    private PlayerManager playerManager;
    private SceneManager sceneManager;
    private CrosshairManager crosshairManager; // Crosshair manager
    private PlayerInteractionManager interactionManager; // Interaction manager
    private CameraManager cameraManager; // Camera manager
    private InputHandler inputHandler; // Input handler

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;

        // Initialize Bullet Physics System
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Initialize the camera manager
        cameraManager = new CameraManager(this.app);

        // Initialize player and scene managers
        playerManager = new PlayerManager(
            bulletAppState,
            this.app.getRootNode(),
            this.app.getCamera(),
            this.app.getInputManager(),
            this.app.getContext().getSettings()
        );

        sceneManager = new SceneManager(
            bulletAppState,
            this.app.getRootNode(),
            this.app.getAssetManager()
        );

        // Set up the player and the scene
        playerManager.setupPlayer();
        sceneManager.setupScene();

        // Initialize the interaction manager
        interactionManager = new PlayerInteractionManager(
            this.app,
            bulletAppState.getPhysicsSpace()
        );

        // Initialize the crosshair manager
        crosshairManager = new CrosshairManager(
            this.app.getAssetManager(),
            this.app.getContext().getSettings(),
            this.app.getGuiNode()
        );

        // Initialize the input handler
        inputHandler = new InputHandler(
            this.app,
            crosshairManager,
            interactionManager
        );
    }

    @Override
    public void update(float tpf) {
        // Synchronize the camera position with the player
        float playerHeight = 0f;  // Adjust based on your player's height
        Vector3f playerPosition = playerManager.getPlayerPosition();
        if (playerPosition != null) {
            app.getCamera().setLocation(playerPosition.add(0, playerHeight, 0));
        } else {
            System.out.println("Player position is null");
        }

        // Apply movement based on input handler's movement flags
        playerManager.movePlayer(
                inputHandler.isLeft(),
                inputHandler.isRight(),
                inputHandler.isForward(),
                inputHandler.isBackward()
        );

        // Update the interaction manager
        interactionManager.update(tpf);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        // Clean up resources and detach states if necessary
        app.getStateManager().detach(bulletAppState);

        // Perform any additional cleanup if required
    }

    // Optional: Implement stateAttached and stateDetached if needed
    // @Override
    // public void stateAttached(AppStateManager stateManager) {
    //     super.stateAttached(stateManager);
    //     // Called when the state is attached to the state manager
    // }

    // @Override
    // public void stateDetached(AppStateManager stateManager) {
    //     super.stateDetached(stateManager);
    //     // Called when the state is detached from the state manager
    // }
}
