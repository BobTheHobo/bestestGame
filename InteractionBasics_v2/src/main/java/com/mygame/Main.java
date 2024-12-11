package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.system.AppSettings;
import com.mygame.viet_files.CardGameState;
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
        // Attach just the merged scene
        sceneState = new SceneAppState();
        stateManager.attach(sceneState);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
    }
}
