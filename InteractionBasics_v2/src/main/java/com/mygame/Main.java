package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

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
        InteractiveDemoAppState demoAppState = new InteractiveDemoAppState();
        stateManager.attach(demoAppState);
    }
}
