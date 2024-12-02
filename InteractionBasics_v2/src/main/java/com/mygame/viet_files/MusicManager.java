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
import com.jme3.audio.AudioRenderer;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioSource;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MusicManager {
    private final Map<String, AudioNode> musicTracks = new HashMap<>();
    private static final Logger logger = Logger.getLogger(MusicManager.class.getName());
    private AudioNode currentlyPlaying = null;
    private final AssetManager assetManager;

    public MusicManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Load a music track into the manager.
     * 
     * @param name   The name of the track (used as an identifier).
     * @param path   The file path of the music asset.
     */
    public void loadTrack(String name, String path) {
        AudioNode audioNode = new AudioNode(assetManager, path, AudioData.DataType.Stream);
        audioNode.setLooping(false); // Set individual tracks non-looping by default
        audioNode.setPositional(false); // Music tracks are non-positional
        musicTracks.put(name, audioNode);
    }

    /**
     * Play a music track by name.
     * 
     * @param trackName The name of the track to play.
     */
    public void playTrack(String trackName) {
        if (!musicTracks.containsKey(trackName)) {
            logger.warning("Track not found: " + trackName);
            return;
        }

        if (currentlyPlaying != null) {
            currentlyPlaying.stop();
        }

        AudioNode track = musicTracks.get(trackName);
        track.setLooping(true); // Ensure looping is enabled
        track.play();
        currentlyPlaying = track;
    }

    /**
     * Stop a playing music track by name.
     * 
     * @param trackName The name of the track to stop.
     */
    public void stopTrack(String trackName) {
        if (!musicTracks.containsKey(trackName)) {
            logger.warning("Track not found: " + trackName);
            return;
        }

        AudioNode track = musicTracks.get(trackName);
    if (track.getStatus() == AudioSource.Status.Playing) {
        track.stop();
    }

    }

    /**
     * Stop any currently playing music.
     */
    public void stopCurrent() {
    if (currentlyPlaying != null && currentlyPlaying.getStatus() == AudioSource.Status.Playing) {
        currentlyPlaying.stop();
        currentlyPlaying = null;
    }
    }
}

