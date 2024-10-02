package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.mygame.jeremiah_files.CardGameState;

public class Main extends SimpleApplication {

    CardGameState cardAppState;
    InteractiveDemoAppState interactiveDemoAppState;
    public static void main(String[] args) {
        Main app = new Main();

        // Application settings
        AppSettings settings = new AppSettings(true);
        settings.setResolution(640, 480);
        settings.setFullscreen(false);
        settings.setTitle("InteractionBasics");

        app.setSettings(settings);
        app.setShowSettings(false);  // Skip the JMonkeyEngine settings dialog
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Attach the InteractiveDemoAppState
        cardAppState = new CardGameState();
        stateManager.attach(cardAppState);
        
        interactiveDemoAppState = new InteractiveDemoAppState();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if (cardAppState.getPositiion() == -1) {
            stateManager.detach(cardAppState);
            stateManager.attach(interactiveDemoAppState);
        }
    }
}
