/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.jeremiah_files;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author jerem
 */
public class Slot {
    private static Box mesh = new Box(.14f, .001f, .24f);
    private boolean filled;
    private Card card;
    private Galley parent;
    private int index;
    private String name;
    private Spatial self;
    private Node selfNode;
    private AssetManager assetManager; //Asset manager
 
    public Slot(Galley parent, int index) {
        filled = false;
        this.index = index;
        this.parent = parent;
        assetManager = parent.getAssetManager();
        
        name = String.format("Slot%d.%d", parent.getIndex(), index);
        
        selfNode = new Node(name);
        
        self = new Geometry(name, mesh);
        Material mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Wood/AT_Wood_01_BUMP.jpg"));
        self.setMaterial(mat);
        self.center();
        self.move(-.15f + ((index % 2) * .3f), .001f, (-.5f + ((index / 2) * .5f)));
        
        selfNode.attachChild(self);
        
        parent.getSelfNode().attachChild(selfNode);
    }
    
    //Plays a card on this slot and triggers game logic
    public void setCard(Card card) {
        this.card = card;
        filled = true;
        card.setParent(this);
        
        card.getSelf().getParent().setLocalRotation(self.getParent().getParent().getLocalRotation());

        Vector3f destination = self.getParent().getParent().getLocalTranslation();
        //System.out.print(destination.toString());

        if (index % 2 == 0) { //Moves played card to correct column
            destination = destination.add(-0.15f / 4, 0, 0);
        } else {
            destination = destination.add(0.15f / 4, 0, 0);
        }
        
        if (index / 2 == 0) { //Moves played card to correct row
            destination = destination.add(0.0f, 0, -0.5f / 4);
        } else if (index / 2 == 2) {
            destination = destination.add(0.0f, 0, 0.5f / 4);
        } 
        destination = destination.add(0, 0.012f, 0);
        
        MotionPath path =  new MotionPath();
        path.addWayPoint(card.getSelf().getParent().getLocalTranslation());
        path.addWayPoint(destination);
        path.setPathSplineType(Spline.SplineType.CatmullRom);
  
        MotionEvent motion = new MotionEvent(card.getSelf().getParent(), path);
        motion.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motion.setSpeed(8f);
        motion.play();
        
    
        //Trigger any card effects
        String cardName = card.getName();
        switch (cardName.substring(4, cardName.indexOf("-"))) {
            case "Cook":
                parent.buffAll(1);
                break;
            case "Gunner":
                parent.opposite().buffRandom(-3);
                break;
            case "Cannoneer":
                parent.opposite().buffAll(-1);
                break;
            case "Lookout":
                parent.getParent().draw(1, parent.getIndex() >= 3);
                break;
            case "Anchorman":
                ArrayList<Card> hand = parent.getIndex() >= 3 ? parent.getParent().getPlayerHand() : parent.getParent().getEnemyHand();
                break;
        }
    }
        
    
    public int getIndex() {
        return index;
    }
    
    public Galley getParent() {
        return parent;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public boolean getFilled() {
        return filled;
    }
    
    public Card getCard() {
        return card;
    }
    
    public void setFilled(boolean filled) {
        this.filled = filled;
    }
}
