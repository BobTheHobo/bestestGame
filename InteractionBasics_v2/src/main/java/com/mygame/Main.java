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

    public static void main(String[] args) {
        Main app = new Main();


        // Application settings
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 960);
        settings.setFullscreen(false);
        settings.setTitle("InteractionBasics");

        app.setSettings(settings);
        app.setShowSettings(false);  // Skip the JMonkeyEngine settings dialog
        app.start();
    }

    @Override
    public void simpleInitApp() {
        inputManager.addMapping(MAPPING_SCENE, TRIGGER_P);
        inputManager.addListener(actionListener, MAPPING_SCENE);

        // Attach the InteractiveDemoAppState
        cardAppState = new CardGameState();
        stateManager.attach(cardAppState);
        
        //sceneState = new SceneAppState();
        //stateManager.attach(sceneState);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
	    //if (scene == 0 && cardAppState.getPositiion() == -1) {
	   // 
	//	    lockout = true;
	//	    scene = 1;
	//	    System.out.println("Next scene 1");
	//	    stateManager.detach(cardAppState);
	//	    sceneState = new SceneAppState();
	//	    stateManager.attach(sceneState);
	 //   } 
    }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        { 
                if (!isPressed && name.equals(MAPPING_SCENE) && !lockout) {
			if (scene == 0) {
				lockout = true;
			    scene = 1;
			    System.out.println("Next scene 1");
			    stateManager.detach(cardAppState);
			    sceneState = new SceneAppState();
			    stateManager.attach(sceneState);
			}
		}
    	}
    };
}
