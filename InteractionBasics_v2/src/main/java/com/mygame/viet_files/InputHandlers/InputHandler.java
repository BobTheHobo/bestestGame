/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/AppState.java to edit this template
 */
package com.mygame.viet_files.InputHandlers;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.mygame.viet_files.GameShadows;
import com.mygame.viet_files.UIManager;

/**
 *
 * @author jerem, viet
 */
public class InputHandler {
    private SimpleApplication app;
    private AssetManager assetManager;
    private InputManager inputManager;

    private GameShadows shadows;
    private UIManager ui;
    
    public InputHandler(Application app, GameShadows shadows, UIManager ui) {
        this.app = (SimpleApplication) app;
	this.assetManager = this.app.getAssetManager();
	this.inputManager = this.app.getInputManager();

	this.shadows = shadows;
        this.ui = ui;

	GameInputHandler gih = new GameInputHandler(app, inputManager);
	ShadowUIInputHandler sih = new ShadowUIInputHandler(app, inputManager, shadows, ui);
    }
}
