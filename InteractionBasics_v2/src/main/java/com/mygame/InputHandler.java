package com.mygame;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.system.NanoTimer;

/*
* author: shawn
*/
public class InputHandler {

    private InputManager inputManager;
    private CrosshairManager crosshairManager;
    private PlayerInteractionManager interactionManager;
    private NanoTimer timer = new NanoTimer();
    private float time;

    // Movement flags
    private boolean left = false, right = false, forward = false, backward = false;

    public InputHandler(Application app, CrosshairManager crosshairManager, PlayerInteractionManager interactionManager) {
        this.inputManager = app.getInputManager();
        this.crosshairManager = crosshairManager;
        this.interactionManager = interactionManager;
        time = timer.getTimeInSeconds();

        setupKeys();
    }

    private void setupKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));

        // Add a key mapping to toggle the crosshair (e.g., "C" key)
        inputManager.addMapping("ToggleCrosshair", new KeyTrigger(KeyInput.KEY_C));

        // Add a mouse mapping for interaction (left mouse button)
        inputManager.addMapping("Interact", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Interact", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addListener(actionListener, "Left", "Right", "Forward", "Backward", "ToggleCrosshair", "Interact");
    }

    private final ActionListener actionListener = new ActionListener() {
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
                case "ToggleCrosshair":
                    if (isPressed) { // On key press
                        if (crosshairManager.isCrosshairVisible()) {
                            crosshairManager.hideCrosshair();
                        } else {
                            crosshairManager.showCrosshair();
                        }
                    }
                    break;
                case "Interact":
                    if (isPressed) {
                        // Delegate interaction to the PlayerInteractionManager
                        
                        interactionManager.handleInteraction();
                        time = timer.getTimeInSeconds();
                        
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
        inputManager.clearMappings();
    }
}
