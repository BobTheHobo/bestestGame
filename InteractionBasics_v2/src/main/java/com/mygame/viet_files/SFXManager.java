/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

/**
 *
 * @author encore
 */
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SFXManager {
    private final Map<String, AudioNode> sfxTracks = new HashMap<>();
    private static final Logger logger = Logger.getLogger(SFXManager.class.getName());
    private final AssetManager assetManager;

    public SFXManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        System.out.println("SFXManager created.");
    }

    /**
     * Load a sound effect (SFX) into the manager.
     *
     * @param name The name of the SFX (used as an identifier).
     * @param path The file path of the SFX asset.
     */
    public void loadSFX(String name, String path) {
        AudioNode audioNode = new AudioNode(assetManager, path, AudioData.DataType.Buffer);
        audioNode.setLooping(false); // SFX should not loop
        audioNode.setDirectional(false); // SFX are not directional by default
        audioNode.setPositional(true); // SFX are positional
        sfxTracks.put(name, audioNode);
        
    }

    /**
     * Play a sound effect at the given position.
     *
     * @param sfxName  The name of the SFX to play.
     * @param position The world position where the SFX should be played.
     */
    public void playSFX(String sfxName, Vector3f position) {
        if (!sfxTracks.containsKey(sfxName)) {
            logger.warning("SFX not found: " + sfxName);
            return;
        }

        // Clone the AudioNode to allow multiple SFX to play simultaneously
        AudioNode sfx = sfxTracks.get(sfxName).clone();
        sfx.setLocalTranslation(position); // Set the position of the SFX
        sfx.play();
    }
}

