package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.system.AppSettings;
import com.mygame.jeremiah_files.CardGameState;
import com.mygame.viet_files.SceneAppState;

/*
* author: shawn
*/
public class Main extends SimpleApplication {

    CardGameState cardAppState;
    InteractiveDemoAppState interactiveDemoAppState;
    SceneAppState sceneState;

    int scene = 0;
    boolean lockout = false;
    private final static Trigger TRIGGER_P= new KeyTrigger(KeyInput.KEY_P);
    private final static String MAPPING_SCENE = "Next Scene";
    private static AppSettings settings;
    private static Main app;

    public static void main(String[] args) {
        app = new Main();

        // Application settings
        settings = new AppSettings(true);
        settings.setResolution(1280, 960);
        settings.setFullscreen(false);
        settings.setTitle("InteractionBasics");

        app.setSettings(settings);
        app.setShowSettings(false);  // Skip the JMonkeyEngine settings dialog
        app.start();
    }

    @Override
    public void simpleInitApp() {
       	enableSceneSwitching(false); 
        inputManager.addMapping(MAPPING_SCENE, TRIGGER_P);
        
        inputManager.addListener(actionListener, MAPPING_SCENE);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if (sceneState.lost()) {
            app.enqueue(() -> {
                app.getRootNode().detachAllChildren();
                            app.getGuiNode().detachAllChildren();
			    stateManager.detach(sceneState);
                            inputManager.clearRawInputListeners();
			    sceneState = new SceneAppState();
			    stateManager.attach(sceneState);
            
            });
             
        }
    }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        { 
                if (!isPressed && name.equals(MAPPING_SCENE) && !lockout) {
			    System.out.println("Next scene 1");
                            app.getRootNode().detachAllChildren();
                            app.getGuiNode().detachAllChildren();
			    stateManager.detach(sceneState);
                            inputManager.clearRawInputListeners();
			    sceneState = new SceneAppState();
			    stateManager.attach(sceneState);
                            
		}
    	}
    };

    private void enableSceneSwitching(boolean enabled) {
	    if (enabled) {
		    inputManager.addMapping(MAPPING_SCENE, TRIGGER_P);
		    inputManager.addListener(actionListener, MAPPING_SCENE);
		    
		    // Attach the InteractiveDemoAppState
		    cardAppState = new CardGameState();
		    stateManager.attach(cardAppState);
		    
		    // Attach the merged scene
		    sceneState = new SceneAppState();
		    stateManager.attach(sceneState);
	    } else {
		    // Attach just the merged scene
		    sceneState = new SceneAppState();
		    stateManager.attach(sceneState);
	    }
    }
}
