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
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
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
    private MusicManager musicManager;
    private SFXManager sfxManager;
    private AnimComposer animComposer;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //Initialize fields
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
	this.viewPort = this.app.getViewPort();
        
        InputManager inputManager = this.app.getInputManager();
        inputManager.addMapping(MAPPING_SCENE, TRIGGER_P);
        inputManager.addListener(actionListener, MAPPING_SCENE);

	bulletAppState = new BulletAppState();
        //bulletAppState.setDebugEnabled(true); // ENABLE FOR COLLISION WIREFRAMES
	stateManager.attach(bulletAppState);
        
        sfxManager = new SFXManager(assetManager);

        crosshairManager = new CrosshairManager(
            this.app.getAssetManager(),
            this.app.getContext().getSettings(),
            this.app.getGuiNode()
        );

        // Initialize the interaction manager
        interactionManager = new PlayerInteractionManager(
            this.app,
            bulletAppState.getPhysicsSpace(),
            sfxManager
        );


	shadows = new GameShadows(rootNode, assetManager, viewPort);
	lighting = new GameLighting(rootNode, assetManager, shadows);
	environment = new GameEnvironment(rootNode, assetManager, viewPort, shadows);
	particles = new GameParticles(rootNode, assetManager);
	boardEnvironment = new BoardEnvironment(rootNode, assetManager, viewPort);
        
        musicManager = new MusicManager(assetManager);
	musicManager.loadTrack("Ambience", "Sounds/Music/Ambience-Waves-16bit.wav");
	musicManager.playTrack("Ambience");

	shadows.setupShadowHandlers();
	lighting.setupLighting();
	lighting.insertLightProbe(stateManager.getApplication(), stateManager);
	environment.setupSkybox();
	environment.addFogEffect();
	environment.addOcean();

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
		this.assetManager
        );
	playerManager.setupPlayer();

	// Tiny rotation to (hopefully) fix shadow artifacts
	//rootNode.rotate(0, 0.01f, 0);
        
        killPlane = new KillPlane(this.rootNode, this.assetManager, this.playerManager, this.sfxManager);

        CardGameState state = new CardGameState(playerManager, sceneCreator.getMainTable(), boardEnvironment);
        stateManager.attach(state);

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
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //room_node.removeFromParent();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
}
