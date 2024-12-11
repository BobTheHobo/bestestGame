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
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.post.filters.FadeFilter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.NanoTimer;
import com.mygame.CrosshairManager;
import com.mygame.PlayerInteractionManager;
import java.util.LinkedList;
import java.util.Queue;

public final class TransitionManager {
    private final GameShadows shadows;
    private final AssetManager assetManager;
    private final GuiManager gui;
    private final CrosshairManager crosshairManager;
    private final SFXManager sfxManager;
    private final PlayerInteractionManager playerInteractionManager;
    private FadeFilter fadef;
    private Node rootNode;
    private Queue<Actionable> runQ;
    
    private boolean inProgress = false;
    private float calledTime;
    private float actionDuration;
    private float endTime;
    
    private NanoTimer timer;
    
    private AudioNode matchstrike;
    private AudioNode blowout;
    
    private Node pirate;
    private Node candle;
    private boolean transitionInProgress;
    
        
    public enum Transition {
        TOWALKING,
        TOCARD
    }
    
    public TransitionManager(AssetManager assetManager, GameShadows shadows, GameLighting lighting, GuiManager gui, CrosshairManager crosshairManager, SFXManager sfxManager, Node rootNode, PlayerInteractionManager playerInteractionManager) {
        this.shadows = shadows;
        this.assetManager = assetManager;
        this.crosshairManager = crosshairManager;
        this.sfxManager = sfxManager; 
        this.playerInteractionManager = playerInteractionManager;
        this.gui = gui;
        this.rootNode = rootNode;
        this.inProgress = false;
        this.timer = new NanoTimer();
        this.runQ = new LinkedList<Actionable>();
        this.transitionInProgress = true;
        
        //sfxManager.loadSFX("matchstrike", "Sounds/SFX/matchstrike.wav");
        
        matchstrike = new AudioNode(assetManager, "Sounds/SFX/matchstrike.wav");
        blowout = new AudioNode(assetManager, "Sounds/SFX/blowoutcandle.wav");
        
        lighting.setLightingDoneAction(() -> {
            transitionIntoGame();
            try {
                //removeLoading text
                gui.removeLoadingText();
            }catch(Exception ex) {}
            
//            enqueueRunnable(1);
            
//            enqueueRunnable(()->{
//                System.out.println("into game");
//               transitionIntoGame(); 
//            }, 0);
//            enqueueRunnable(()->{
//                System.out.println("into walking");
//                transitionIntoCardState();
//            }, 0);
//            enqueueRunnable(()->{
//                System.out.println("intowalking");
//                transitionIntoWalking();
//            }, 0);
            
            //fadef.setDuration(0);
            //fadef.fadeIn();
        });
    }
    
    public void setupFadeFilter() {
        fadef = this.shadows.getFadeFilter();
        
        //Remove crosshair
        //crosshairManager.hideCrosshair();
        
        //Black everything out on game start
        fadef.setDuration(0);
        fadef.fadeOut();
        
        //Add loading text
        gui.addLoadingText("Loading...");
    }
    
    private void transitionIntoGame() {
        transitionInProgress = true;
                
        clearQueues();
        
        pirate = (Node)rootNode.getChild("Pirate");
        candle = (Node)rootNode.getChild("Candle");
        
        //Wait until pbr results are in and everything else finishes
        System.out.println("Lighting done, fade into game");
        
        //Black everything out on game start
        fadef.setDuration(0);
        fadef.fadeOut();
        
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

            gui.enqueueDialogue("Let's play a game", 4);
            
        }, 7);
        
        // fade in
        enqueueRunnable(()->{
            fadef.setDuration(5);
            fadef.fadeIn();
        }, 5);
        
        // set filter order back
        enqueueRunnable(()->{
            shadows.fixFilterOrdering();
            shadows.setFadeLastFilter();
            transitionInProgress = false;
        }, 0);
    }
   
    private void transitionIntoCardState() {
        resetCandle();
        transitionInProgress = true;
        clearQueues();
                
        //crosshairManager.hideCrosshair();
        
        blowout.setVolume(2);
        // blow out candle
        enqueueRunnable(()->{
            //Black everything out
            fadef.setDuration(0);
            fadef.fadeOut();
            blowout.play();
        }, 1f);
        
        enqueueRunnable(0.5f);
        
        // dialogue
        enqueueRunnable(()->{
            try {
                rootNode.attachChild(pirate);
            } catch(Exception ex) {}
                    
            // Queue dialogue
            gui.enqueueDialogue("Another round?", 3.5f);
            
        }, 2);
        
        // light match
        enqueueRunnable(()->{
            matchstrike.play();
        }, 0.28f);
        
        // Enable candle partles to show through
        enqueueRunnable(()->{
            resetCandle();
            shadows.setParticlesLastFilter();
        }, 0);

        // fade in
        enqueueRunnable(()->{
            fadef.setDuration(2);
            fadef.fadeIn();
        }, 2);
        
        // set filter order back
        enqueueRunnable(()->{
            shadows.fixFilterOrdering();
            shadows.setFadeLastFilter();
            transitionInProgress = false;
        }, 0);
    }
    
    private void transitionIntoWalking() {
        transitionInProgress = true;
        clearQueues();
        
        // set filter order back
        enqueueRunnable(()->{
            shadows.fixFilterOrdering();
            shadows.setFadeLastFilter();
        }, 0);
        
        // dialogue
        enqueueRunnable(()->{
            //Black everything out
            fadef.setDuration(2);
            fadef.fadeOut();
        }, 1);
        
        enqueueRunnable(()->{
            // Queue dialogue
            gui.enqueueDialogue("Until next time then", 2f);
        }, 2f);
        
        enqueueRunnable(()->{
            try {
                rootNode.getChild("Pirate").removeFromParent();
            } catch(Exception ex) {}

            fadef.setDuration(2);
            fadef.fadeIn();
        }, 1f);
        
        enqueueRunnable(()->{
            //crosshairManager.showCrosshair();
            transitionInProgress = false;
        }, 0);
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
    
    private void clearQueues() {
        gui.clearQueue();
        this.clearQueue();
        
        // Reset incase a fade was in progress
        fadef.setDuration(0);
        fadef.fadeIn();
        
        // Also reset filter order
        shadows.fixFilterOrdering();
        shadows.setFadeLastFilter();
    }
    
    private void clearQueue() {
        runQ.clear();
        inProgress = false;
    }

    
    public void transitionTo(Transition tran) {
        if (!transitionInProgress) {
            if(tran == Transition.TOCARD) {
                transitionIntoCardState();
            } else if (tran == Transition.TOWALKING) {
                transitionIntoWalking();
            }
        }
    }
    
    public boolean canTransition() {
        return !transitionInProgress;
    }
    
    private void resetCandle() { 
        try {
            Node candleNode = (Node)rootNode.getChild("Candle node");
            if(((Node)rootNode.getChild("HandNode")).hasChild(candleNode)) {
                playerInteractionManager.handleInteraction(); // drop candle
                
                //Node candleNode = ((Node)rootNode.getChild("HandNode")).getChild("Candle node");
                //for(Spatial spa : ((Node)rootNode.getChild("HandNode")).getChildren()) {
                //    System.out.println(spa.getName());
                //}
            }
            
            Vector3f spawnPos = new Vector3f(0.6f, 2.2f, -1f);
            candleNode.setLocalTranslation(spawnPos);
            candleNode.getControl(RigidBodyControl.class).setPhysicsLocation(spawnPos); //Also update collision
            System.out.println("Transition reset candle");
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }
}
