package com.mygame.viet_files;

import com.jme3.anim.AnimComposer;
import com.mygame.viet_files.InputHandlers.InputHandler;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.NanoTimer;
import com.jme3.ui.Picture;
import com.mygame.CameraManager;
import com.mygame.CrosshairManager;
import com.mygame.PlayerInteractionManager;
import com.mygame.PlayerManager;

/**
 * @author viet
 */
public class SceneAppState extends AbstractAppState {

    private Node rootNode;
    private AssetManager assetManager;
    private SimpleApplication app;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;
    
    private final static Trigger TRIGGER_P= new KeyTrigger(KeyInput.KEY_P);
    private final static String MAPPING_SCENE = "Next Scene";
    private boolean nextScene = false;
    private Node room_node;
    private Node guiNode;
    private KillPlane killPlane;

    private PlayerInteractionManager interactionManager; // Interaction manager
    private CrosshairManager crosshairManager; // Crosshair manager
    private GameLighting lighting;
    private GameShadows shadows;
    private GameEnvironment environment;
    private GameParticles particles;
    private BoardEnvironment boardEnvironment;
    private CameraManager cameraManager; // Camera manager
    private PlayerManager playerManager;
    private InputHandler inputHandler;
    private CardGameState state;
    private AppStateManager stateManager;
    private TransitionManager transitions;
    private GuiManager guiManager;
    
    private static final Trigger TRIGGER_R = new KeyTrigger(KeyInput.KEY_R); // 'R' key trigger for testing
    private static final Trigger TRIGGER_ESC = new KeyTrigger(KeyInput.KEY_ESCAPE); // 'ESC' key trigger to exit game
    private static final String MAPPING_WIN = "Win Game";
    private static final String MAPPING_EXIT = "Exit Game";

    private float shakeTime = 0f; // Timer for shake duration
    private boolean isShaking = false;
    private float shakeIntensity = 0f; // Shake intensity
    private boolean winTriggered = false; // Tracks if the win sequence is in progress
    private MusicManager musicManager; 
    private SFXManager sfxManager;
    private AnimComposer animComposer;
    
    private float calledTime;
    private int delay = -1;
    private boolean wonCalled = false;
    private boolean lostCalled = false;
    NanoTimer timer = new NanoTimer();
    
    private Picture endScreen;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //Initialize fields
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
	this.viewPort = this.app.getViewPort();
        
        this.musicManager = new MusicManager(assetManager);
        this.sfxManager = new SFXManager(assetManager);
        
        this.stateManager = stateManager;
        
        musicManager.loadTrack("Ambience-Rumble", "Sounds/MusicPlus/Ambience-Rumble-16bit.wav");
        musicManager.loadTrack("Ambience-Waves", "Sounds/Music/Ambience-Waves-16bit.wav");
        sfxManager.loadSFX("Ship-Sink", "Sounds/SFXPlus/Ship-Sink-16bit.wav");
        
        musicManager.playTrack("Ambience-Rumble");
        musicManager.playTrack("Ambience-Waves");
        
        InputManager inputManager = this.app.getInputManager();
        inputManager.addMapping(MAPPING_SCENE, TRIGGER_P);
        inputManager.addMapping(MAPPING_WIN, TRIGGER_R);
        inputManager.addListener(actionListener, MAPPING_SCENE);
        inputManager.addListener(actionListenerWin, MAPPING_WIN);

	bulletAppState = new BulletAppState();
        //bulletAppState.setDebugEnabled(true); // ENABLE FOR COLLISION WIREFRAMES
	stateManager.attach(bulletAppState);

        crosshairManager = new CrosshairManager(
            this.app.getAssetManager(),
            this.app.getContext().getSettings(),
            this.app.getGuiNode()
        );
	//crosshairManager.hideCrosshair();
		

        // Initialize the interaction manager
        interactionManager = new PlayerInteractionManager(
            this.app,
            bulletAppState.getPhysicsSpace(),
            sfxManager
        );

        guiManager = new GuiManager(
            this.app.getContext().getSettings(),
            assetManager,
            this.app.getGuiNode()
        );

	shadows = new GameShadows(rootNode, assetManager, viewPort);
	lighting = new GameLighting(rootNode, assetManager, shadows);
	environment = new GameEnvironment(rootNode, assetManager, viewPort, shadows);
	particles = new GameParticles(rootNode, assetManager);
	boardEnvironment = new BoardEnvironment(rootNode, assetManager, viewPort);
        transitions = new TransitionManager(assetManager, shadows, lighting, guiManager, crosshairManager, sfxManager, rootNode, interactionManager);

	shadows.setupShadowHandlers();
	lighting.setupLighting();
	lighting.insertLightProbe(stateManager.getApplication(), stateManager);
	environment.setupSkybox();
	environment.addFogEffect();
	environment.addOcean();
        transitions.setupFadeFilter();      

	SceneCreator sceneCreator = new SceneCreator(rootNode, assetManager, viewPort, bulletAppState, shadows, particles, interactionManager);
	sceneCreator.setupScene();

	UIManager ui = new UIManager(this.assetManager, this.shadows, this.app.getGuiNode(), this.viewPort);

        // Initialize the camera manager
        cameraManager = new CameraManager(this.app, playerManager, interactionManager, inputHandler);

	inputHandler = new InputHandler(this.app, crosshairManager, interactionManager, shadows, ui, cameraManager);

        // Initialize player and scene managers
        playerManager = new PlayerManager(
            bulletAppState,
            this.app.getRootNode(),
            this.app.getCamera(),
	    this.cameraManager,
	    this.inputHandler,
	    this.interactionManager,
            this.app.getContext().getSettings(),
            this.assetManager,
            this.sfxManager
        );
	playerManager.setupPlayer();

	// Tiny rotation to (hopefully) fix shadow artifacts
	//rootNode.rotate(0, 0.01f, 0);
        
        killPlane = new KillPlane(this.rootNode, this.assetManager, this.playerManager, this.sfxManager);

	// Tiny rotation to (hopefully) fix shadow artifacts
	//rootNode.rotate(0, 0.01f, 0);
        
        killPlane = new KillPlane(this.rootNode, this.assetManager, this.playerManager, this.sfxManager);

        state = new CardGameState(playerManager, sceneCreator.getMainTable(), boardEnvironment, transitions);
        stateManager.attach(state);
    }
    
    private final ActionListener actionListenerWin = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_WIN) && isPressed) {
                //Win();
                wonCalled = true;
                calledTime = timer.getTimeInSeconds();
                delay = 1;
            } else if (name.equals(MAPPING_EXIT) && isPressed) {
                app.stop();
            }
        }
    };
    
    public void Win() {
        if (winTriggered) return; // Ensure the win sequence only runs once
        winTriggered = true;

        System.out.println("Win function triggered!");

        // Start shaking
        isShaking = true;
        shakeTime = 5f; // Total duration for shaking
        shakeIntensity = 0.2f; // Initial shake intensity
        sfxManager.playSFX("Ship-Sink", new Vector3f(0, 0, 0));
    }
    
    private void showEndScreen() {
        if (endScreen == null) { // Create the end screen only once
            endScreen = new Picture("EndScreen");
            endScreen.setImage(assetManager, "Textures/Mutiny-End-Screen-v2.png", true);
            endScreen.setWidth(app.getCamera().getWidth());
            endScreen.setHeight(app.getCamera().getHeight());
            endScreen.setPosition(0, 0);
        }

        app.getGuiNode().attachChild(endScreen);
        System.out.println("End screen displayed.");

        musicManager.stopTrack("Ambience-Rumble");
        musicManager.stopTrack("Ambience-Waves"); 

        enableRestart();
    }

    private void disableInput() {
        // Do not disable camera mappings or input controls
//        app.getInputManager().clearMappings(); // Clear non-essential mappings
//        app.getInputManager().clearRawInputListeners();
        System.out.println("Input disabled but camera mappings preserved.");
    }

    
    private void enableRestart() {
        InputManager inputManager = app.getInputManager();

        // Clear only mappings related to gameplay, not core controls like ESC
        inputManager.deleteMapping(MAPPING_SCENE); 
        inputManager.deleteMapping(MAPPING_WIN); 

        // Rebind 'P' for restarting the game
        inputManager.addMapping(MAPPING_SCENE, TRIGGER_P);
        inputManager.addListener(actionListenerRestart, MAPPING_SCENE);

        // Rebind 'ESC' for exiting
        inputManager.addMapping(MAPPING_EXIT, TRIGGER_ESC);
        inputManager.addListener(actionListenerWin, MAPPING_EXIT);

        System.out.println("'P' key enabled for restarting and 'ESC' key enabled for exiting.");
    }
    
    private ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf)
            {
                if (!isPressed) {
                    if (name.equals(MAPPING_SCENE)) {
                        app.getFlyByCamera().setEnabled(true);
                    }
	    	}
	    }
    };

    private final ActionListener actionListenerRestart = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed && name.equals(MAPPING_SCENE) && winTriggered) { // Check if triggered from the end screen
                System.out.println("Restarting game from end screen...");

                // Properly reset the scene
                resetScene();
            }
        }
    };

    private void resetScene() {
        System.out.println("Resetting scene...");

	stateManager.detach(state);

        // Detach the current app state
        stateManager.detach(this);

        // Detach all children from rootNode and guiNode
        app.getRootNode().detachAllChildren();
        app.getGuiNode().detachAllChildren();

        // Clear input mappings but preserve critical mappings like ESC
        app.getInputManager().clearRawInputListeners();
        app.getInputManager().deleteMapping(MAPPING_SCENE);
        app.getInputManager().deleteMapping(MAPPING_WIN);

        // Re-add essential input mappings
        app.getInputManager().addMapping(MAPPING_EXIT, TRIGGER_ESC);
        app.getInputManager().addListener(actionListenerWin, MAPPING_EXIT);

        // Re-enable the FlyByCamera
        app.getFlyByCamera().setEnabled(true);
        app.getFlyByCamera().setDragToRotate(false); // Ensure camera rotates with mouse movement

        // Reinitialize the camera using the CameraManager
        if (cameraManager != null) {
            cameraManager.resetCamera(); // Ensure camera is fully reinitialized
        }

        // Create a new instance of SceneAppState
        SceneAppState newScene = new SceneAppState();

        // Attach the new app state
        stateManager.attach(newScene);

        System.out.println("Game reset complete.");
    }




    
    public boolean getNextScene() {
        return nextScene;
    }

    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
	    super.update(tpf);
	    playerManager.updatePlayer(tpf);
        killPlane.checkForResets(tpf);
	    environment.addWaves(tpf);
        guiManager.dialogUpdater(tpf);
        transitions.runnableUpdater(tpf);
        
        if (!wonCalled && state.getWon()) {
            wonCalled = true;
            calledTime = timer.getTimeInSeconds();
            delay = 1;
        }
        
        if (!lostCalled && state.getLost()) {
            lostCalled = true;
            app.enqueue(() -> {
                resetScene();
            });
        }
        
       

        if (delay != -1 && timer.getTimeInSeconds() > (calledTime + delay) && state.getWon()) {
            Win();
        }

        if (interactionManager.getChestUnlocked()) {
            state.getBoard().addKraken();
        }

        if (isShaking) {
            shakeTime -= tpf;

            // Apply screen shake logic
            float shakeX = (FastMath.rand.nextFloat() - 0.5f) * 2f * shakeIntensity;
            float shakeY = (FastMath.rand.nextFloat() - 0.5f) * 2f * shakeIntensity;
            app.getCamera().setLocation(app.getCamera().getLocation().add(new Vector3f(shakeX, shakeY, 0)));

            // Gradually increase shake intensity
            shakeIntensity += tpf * 0.1f;

            if (shakeTime <= 0) {
                isShaking = false;
                app.getCamera().setLocation(new Vector3f(0, 0, 0)); // Reset camera position
                disableInput(); // Disable gameplay inputs
                showEndScreen(); // Show the end screen
                enableRestart(); // Allow restarting the game
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();

        // Preserve input mappings for ESC and restart
        app.getInputManager().deleteMapping(MAPPING_WIN);
        environment.reset();
        shadows.reset();
        stateManager.detach(state);

        musicManager.stopTrack("Ambience-Rumble");
        musicManager.stopTrack("Ambience-Waves");
        playerManager.cleanup();
        inputHandler.reset();

        System.out.println("Scene cleanup complete.");
    }
    
    public boolean lost() {
         if (!lostCalled && state.getLost()) {
            return true;
        }
        return false;
    }
    }
