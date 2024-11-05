/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/AppState.java to edit this template
 */
package com.mygame.viet_files.InputHandlers;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.mygame.CrosshairManager;
import com.mygame.PlayerInteractionManager;
import com.mygame.viet_files.GameShadows;
import com.mygame.viet_files.UIManager;

/**
 *
 * @author viet, shawn
 */
public class InputHandler {
    private SimpleApplication app;
    private AssetManager assetManager;
    private InputManager inputManager;
    private CrosshairManager crosshairManager;
    private PlayerInteractionManager interactionManager;

    private GameShadows shadows;
    private UIManager ui;

    public GameInputHandler gih;
    
    public InputHandler(Application app, CrosshairManager crosshairManager, PlayerInteractionManager interactionManager, GameShadows shadows, UIManager ui) {
        this.app = (SimpleApplication) app;
	this.assetManager = this.app.getAssetManager();
	this.inputManager = this.app.getInputManager();
        this.crosshairManager = crosshairManager;
        this.interactionManager = interactionManager;

	this.shadows = shadows;
        this.ui = ui;

	gih = new GameInputHandler(app, crosshairManager, interactionManager, inputManager);
	ShadowUIInputHandler sih = new ShadowUIInputHandler(app, inputManager, shadows, ui);
    }
}
