/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.system.NanoTimer;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Viet
 */
public class GuiManager {
    
    private AppSettings settings;
    private AssetManager assetManager;
    private Node guiNode;
    
    
    private BitmapText loadingText;
    
    private Node dialogueNode;
    private BitmapText dialogue;
    private Geometry dialogueBox;
    
    private float calledTime;
    private float dialogueDuration;
    private float endTime;
    private boolean inProgress = false;
    
    private NanoTimer timer;
    
    private float tpf;
    
    private Queue<Dialogue> diagQ;
    private Queue<Runnable> runQ;
            
    private class Dialogue {
        private String str;
        private float duration;

        public Dialogue(String str, float duration) {
            this.str = str;
            this.duration = duration;
        }

        public String getString() {
            return str;
        }

        public float getDuration() {
            return duration;
        }
    }
    
    public GuiManager(AppSettings settings, AssetManager assetManager, Node guiNode) {
        this.settings = settings;
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.timer = new NanoTimer();
        this.dialogueNode = new Node("Dialogue node");
        this.diagQ = new LinkedList<Dialogue>();
        
        //Default font
        //loadingText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"));
        loadingText = new BitmapText(assetManager.loadFont("Interface/Fonts/LucidaCalligraphy.fnt"));
        dialogue = new BitmapText( assetManager.loadFont("Interface/Fonts/LucidaCalligraphy.fnt"));

        // Set up dialogue box
        
    }
    
    public void addLoadingText(String loadText) {
        loadingText.setText(loadText);
        loadingText.setLocalTranslation(
            settings.getWidth()/2 - loadingText.getLineWidth()/2,
            settings.getHeight()/2 + loadingText.getLineHeight()/2,
            100
        );
        guiNode.attachChild(loadingText);
    }
    
    public void removeLoadingText() {
        guiNode.detachChild(loadingText);
    }
    
    public void showDialogue(String str, float duration) {
        Dialogue diag = new Dialogue(str, duration);
        showDialogue(diag);
    }
    
    public void showDialogue(Dialogue diag) {
        String str = diag.getString();
        float duration = diag.getDuration();
        System.out.println("Dialogue shown: " + str);
        
        inProgress = true;
        calledTime = timer.getTimeInSeconds();
        dialogueDuration = duration;
        endTime = calledTime + duration;
        
        dialogue.setText(str);
        dialogue.setSize(30);
        dialogue.setColor(ColorRGBA.White);
        dialogue.setLocalTranslation((settings.getWidth() - dialogue.getLineWidth()) / 2, settings.getHeight() / 2.5f, 0); // position
        
        dialogueBox = new Geometry("Quad", new Quad(dialogue.getLineWidth(), dialogue.getHeight()));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        dialogueBox.setMaterial(mat);
        dialogueBox.setLocalTranslation(dialogue.getLocalTranslation().add(0, -dialogue.getHeight(), 0));
    
        dialogueNode.attachChild(dialogueBox);
        dialogueNode.attachChild(dialogue);
        
        //Ensure text is printed over background
        dialogueBox.move(0,0,-1);
        dialogue.move(0,0,1);
        
        guiNode.attachChild(dialogueNode);
    }
    
    public void enqueueDialogue(String str, float duration) {
        try {
            Dialogue d = new Dialogue(str, duration);
            diagQ.add(d);
        } catch (Exception ex) {
            System.err.println("Something went wrong with queueing dialog" + ex.toString());
        }
    }
    
    public void dialogUpdater(float tpf) {
        if(!diagQ.isEmpty() && !inProgress) {
            Dialogue diag = diagQ.poll();
            // Empty string just indicates "blocking" (time spacing) between dialogue
            if(diag.getString().equals("")) {
                float duration = diag.getDuration();
                inProgress = true;
                calledTime = timer.getTimeInSeconds();
                dialogueDuration = duration;
                endTime = calledTime + duration;
            } else {
                showDialogue(diag);
            }
        }
            
        if(timer.getTimeInSeconds() > endTime && inProgress) {
            inProgress = false;
            dialogue.setText("");
            try {
                dialogueNode.removeFromParent();
            } catch (Exception ex) {}
            System.out.println("Dialogue finished, queue empty: " + diagQ.isEmpty() + " inProgress: " + inProgress);
        }
    }
    
    public void clearQueue() {
        diagQ.clear();
        inProgress = false;
        dialogue.setText("");
        try {
            dialogueNode.removeFromParent();
        } catch (Exception ex) {}
    }

}
