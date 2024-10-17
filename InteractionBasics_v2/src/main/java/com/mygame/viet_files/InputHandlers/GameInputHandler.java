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

/**
 *
 * @author jerem, viet
 */
public class GameInputHandler implements ActionListener {
    
    private SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private ViewPort viewPort;

    private final static Trigger TRIGGER_W = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_A = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_S = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_D = new KeyTrigger(KeyInput.KEY_D);
    private final static Trigger TRIGGER_Q = new KeyTrigger(KeyInput.KEY_Q);
    private final static Trigger TRIGGER_LEFT_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);

    private final static String MAPPING_FORWARD = "Move Forward";
    private final static String MAPPING_BACK = "Move Backwards";
    private final static String MAPPING_LEFT = "Look Left";
    private final static String MAPPING_RIGHT = "Look Right";
    private final static String MAPPING_LEFT_CLICK = "Selected";
    private final static String MAPPING_RESET = "Reset";

    public GameInputHandler(Application app, InputManager inputManager) {
        this.app = (SimpleApplication) app;
	this.inputManager = inputManager;

        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
	this.viewPort = this.app.getViewPort();

	initInputs();
    }

    private void addMappings() {
	String[] mappings = {MAPPING_FORWARD, MAPPING_LEFT, MAPPING_BACK, MAPPING_RIGHT, MAPPING_LEFT_CLICK, MAPPING_RESET};

	// Game inputs
        inputManager.addMapping(MAPPING_FORWARD, TRIGGER_W);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A);
        inputManager.addMapping(MAPPING_BACK, TRIGGER_S);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D);
        inputManager.addMapping(MAPPING_LEFT_CLICK, TRIGGER_LEFT_CLICK);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_Q);

	inputManager.addListener(this, mappings);
    }

    private void initInputs() {
        //Initialize inputs
	addMappings();
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
    }
}
