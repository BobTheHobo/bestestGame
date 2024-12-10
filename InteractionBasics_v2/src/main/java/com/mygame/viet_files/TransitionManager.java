/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

/**
 *
 * @author Nakano on this thread, modified by Viet
 * https://hub.jmonkeyengine.org/t/how-to-make-loading-screen/46454/17
 */
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.post.filters.FadeFilter;
import com.jme3.scene.Node;
import com.jme3.system.NanoTimer;
import com.mygame.CrosshairManager;
import java.util.LinkedList;
import java.util.Queue;

public final class TransitionManager {
    private final GameShadows shadows;
    private final AssetManager assetManager;
    private final GuiManager gui;
    private final CrosshairManager crosshairManager;
    private final SFXManager sfxManager;
    private FadeFilter fadef;
    private Node rootNode;
    private Queue<Actionable> runQ;
    
    private boolean inProgress = false;
    private float calledTime;
    private float actionDuration;
    private float endTime;
    
    private NanoTimer timer;
    
    private AudioNode matchstrike;

    
    public TransitionManager(AssetManager assetManager, GameShadows shadows, GameLighting lighting, GuiManager gui, CrosshairManager crosshairManager, SFXManager sfxManager, Node rootNode) {
        this.shadows = shadows;
        this.assetManager = assetManager;
        this.crosshairManager = crosshairManager;
        this.sfxManager = sfxManager; 
        this.gui = gui;
        this.rootNode = rootNode;
        this.inProgress = false;
        this.timer = new NanoTimer();
        this.runQ = new LinkedList<Actionable>();
        
        //sfxManager.loadSFX("matchstrike", "Sounds/SFX/matchstrike.wav");
        
        matchstrike = new AudioNode(assetManager, "Sounds/SFX/matchstrike.wav");
        
        lighting.setLightingDoneAction(() -> {
            transitionIntoGame();
        });
    }
    
    public void setupFadeFilter() {
        fadef = this.shadows.getFadeFilter();
        
        //Remove crosshair
        crosshairManager.hideCrosshair();
        
        //Black everything out on game start
        fadef.setDuration(0);
        fadef.fadeOut();
        
        //Add loading text
        gui.addLoadingText("Loading...");
    }
    
    public void transitionIntoGame() {
        
        //Wait until pbr results are in and everything else finishes
        System.out.println("Lighting done, fade into game");
        
        //removeLoading text
        gui.removeLoadingText();
        
        enqueueRunnable(1);
        
        matchstrike.setVolume(4);
        
        // light match
        enqueueRunnable(()->{
            matchstrike.play();
        }, 0.28f);
        
        // Enable candle partles to show through
        enqueueRunnable(()->{
            shadows.setParticlesLastFilter();
        }, 4);

        // dialogue
        enqueueRunnable(()->{
                    
            // Queue dialogue
            gui.enqueueDialogue("Finally awake?", 3);

            gui.enqueueDialogue("", 2);

            gui.enqueueDialogue("About time", 5);

            gui.enqueueDialogue("", 2);

            gui.enqueueDialogue("Let's play a game", 5);
            
        }, 7);
        
        // fade in
        enqueueRunnable(()->{
            fadef.setDuration(5);
            fadef.fadeIn();
        }, 5);
    }
   
    public void transitionIntoCardState() {
        
    }
    
    public void transitionIntoWalking() {
        // Enable crosshair again
        crosshairManager.showCrosshair();
    }
    
    private class Actionable {
        private Runnable action;
        private float duration;
        private boolean pause;

        public Actionable(Runnable action, float duration, boolean pause) {
            this.action = action;
            this.duration = duration;
        }

        public Runnable getAction() {
            return action;
        }

        public float getDuration() {
            return duration;
        }
        
        public boolean isPause() {
            return pause;
        }
    }
    
    public void enqueueRunnable(Runnable action, float duration) {
        try {
            Actionable act = new Actionable(action, duration, false);
            runQ.add(act);
        } catch (Exception ex) {
            System.err.println("Something went wrong with queueing a actionable" + ex.toString());
        }
    }
    
    public void enqueueRunnable(float pause) {
        try {
            Actionable act = new Actionable(()->{}, pause, true);
            runQ.add(act);
        } catch (Exception ex) {
            System.err.println("Something went wrong with queueing a actionable" + ex.toString());
        }
    }
    
        
    public void runnableUpdater(float tpf) {
        if(!runQ.isEmpty() && !inProgress) {
            Actionable action = runQ.poll();
            
            float duration = action.getDuration();
            inProgress = true;
            calledTime = timer.getTimeInSeconds();
            actionDuration = duration;
            endTime = calledTime + duration;
                
            if(!action.isPause()) {
                action.getAction().run();
            }
        }
            
        if(timer.getTimeInSeconds() > endTime && inProgress) {
            inProgress = false;
            System.out.println("Action finished, queue empty: " + runQ.isEmpty() + " inProgress: " + inProgress);
        }
    }
}
