/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/AppState.java to edit this template
 */
package com.mygame.viet_files.InputHandlers;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.NanoTimer;
import com.mygame.CameraManager;
import com.mygame.CrosshairManager;
import com.mygame.PlayerInteractionManager;

/**
 *
 * @author jerem, viet
 */
public class GameInputHandler {
    
    private SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private ViewPort viewPort;
    private CrosshairManager crosshairManager;
    private PlayerInteractionManager interactionManager;
    private CameraManager cameraManager;

    private final static Trigger TRIGGER_W = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_A = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_S = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_D = new KeyTrigger(KeyInput.KEY_D);
    private final static Trigger TRIGGER_Q = new KeyTrigger(KeyInput.KEY_Q);
    private final static Trigger TRIGGER_C = new KeyTrigger(KeyInput.KEY_C);
    private final static Trigger TRIGGER_O = new KeyTrigger(KeyInput.KEY_O);
    private final static Trigger TRIGGER_V = new KeyTrigger(KeyInput.KEY_V);
    private final static Trigger TRIGGER_LEFT_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);

    private final static String MAPPING_FORWARD = "Move Forward";
    private final static String MAPPING_BACK = "Move Backwards";
    private final static String MAPPING_LEFT = "Move Left";
    private final static String MAPPING_RIGHT = "Move Right";
    private final static String MAPPING_LEFT_CLICK = "Interact";
    private final static String MAPPING_RESET = "Reset";
    private final static String MAPPING_CROSSHAIR = "Toggle Crosshair";
    private final static String MAPPING_TOGGLE_WALKING = "Toggle Walking";
    
    private NanoTimer timer = new NanoTimer();
    private float time = timer.getTimeInSeconds();

    // Movement flags
    private boolean left = false, right = false, forward = false, backward = false;

    public GameInputHandler(Application app, CrosshairManager crosshairManager, PlayerInteractionManager interactionManager, InputManager inputManager, CameraManager cameraManager) {
        this.app = (SimpleApplication) app;
	this.inputManager = inputManager;
	this.crosshairManager = crosshairManager;
	this.interactionManager = interactionManager;
	this.cameraManager = cameraManager;

        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
	this.viewPort = this.app.getViewPort();
	
	initInputs();
    }

    private void addMappings() {
	String[] mappings = {MAPPING_FORWARD, MAPPING_LEFT, MAPPING_BACK, MAPPING_RIGHT, MAPPING_LEFT_CLICK, MAPPING_RESET, MAPPING_CROSSHAIR, MAPPING_TOGGLE_WALKING};

	// Game inputs
        inputManager.addMapping(MAPPING_FORWARD, TRIGGER_W);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A);
        inputManager.addMapping(MAPPING_BACK, TRIGGER_S);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D);
        //inputManager.addMapping(MAPPING_RESET, TRIGGER_Q);

        // Add a key mapping to toggle the crosshair (e.g., "C" key)
        inputManager.addMapping(MAPPING_CROSSHAIR, TRIGGER_C);

        // Add a mouse mapping for interaction (left mouse button)
        inputManager.addMapping(MAPPING_LEFT_CLICK, TRIGGER_LEFT_CLICK);

	// Toggle Flycam
        inputManager.addMapping(MAPPING_TOGGLE_WALKING, TRIGGER_V);


	inputManager.addListener(walkingActionListener, mappings);
	inputManager.addListener(cameraActionListener, mappings);
    }

    private void initInputs() {
        //Initialize inputs
	addMappings();
    }

    private final ActionListener cameraActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            switch (name) {
                case MAPPING_TOGGLE_WALKING:
                    if (isPressed) {
                        // Delegate interaction to the PlayerInteractionManager
			cameraManager.toggleCameraWalking();
                    }
                    break;
            }
        }
    };

    private final ActionListener walkingActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            switch (name) {
                case MAPPING_LEFT:
                    left = isPressed;
                    break;
                case MAPPING_RIGHT:
                    right = isPressed;
                    break;
                case MAPPING_FORWARD:
                    forward = isPressed;
                    break;
                case MAPPING_BACK:
                    backward = isPressed;
                    break;
                case MAPPING_CROSSHAIR:
                    if (isPressed) { // On key press
                        if (crosshairManager.isCrosshairVisible()) {
                            crosshairManager.hideCrosshair();
                        } else {
                            crosshairManager.showCrosshair();
                        }
                    }
                    break;
                case MAPPING_LEFT_CLICK:
                    //if(MouseInput.get().isButtonDown(0)) return;
                    
                    //MouseInput.

                    if (isPressed  && (timer.getTimeInSeconds() > (time + 0.5f))) {
                        // Delegate interaction to the PlayerInteractionManager
                        time = timer.getTimeInSeconds();                                                                                    
                        interactionManager.handleInteraction();
                        //System.out.println("Input manager: ");
                        System.out.println("Input manager: " + inputManager.toString());
                    }
                    break;
            }
        }
    };

    // Getter methods for movement flags
    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isForward() {
        return forward;
    }

    public boolean isBackward() {
        return backward;
    }
    
    public void reset() {
        inputManager.removeListener(walkingActionListener);
        inputManager.removeListener(cameraActionListener);
    }
}
