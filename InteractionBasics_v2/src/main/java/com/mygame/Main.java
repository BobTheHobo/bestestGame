package com.mygame;

import com.jme3.app.SimpleApplication;
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
        // Attach the InteractiveDemoAppState
        //cardAppState = new CardGameState();
        //stateManager.attach(cardAppState);
        
        sceneState = new SceneAppState();
        stateManager.attach(sceneState);
        
        
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //if (scene == 0 && cardAppState.getPositiion() == -1) {
        //    System.out.println("Next scene");
        //    stateManager.detach(cardAppState);
        //    interactiveDemoAppState = new InteractiveDemoAppState();
        //    stateManager.attach(interactiveDemoAppState);
        //    scene = 1;
        //}
        //if (scene == 1 && interactiveDemoAppState.getNextScene()) {
        //    System.out.println("Next scene");
        //    stateManager.detach(interactiveDemoAppState);
        //    sceneState = new SceneAppState();
        //    stateManager.attach(sceneState);
        //    scene = 2;
        //}
    }
}
