/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

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
        
                                        
        //Moves played card to center of played galley
        card.getSelf().getParent().center();
        card.getSelf().getParent().setLocalTranslation(self.getParent().getParent().getLocalTranslation());
        card.getSelf().getParent().setLocalRotation(self.getParent().getParent().getLocalRotation());
        card.getSelf().getParent().move(0, 0.012f, 0);
        
        if (index % 2 == 0) { //Moves played card to correct column
        card.getSelf().getParent().move(-0.15f / 4, 0, 0);
        } else {
            card.getSelf().getParent().move(0.15f / 4, 0, 0);
        }
        
        if (index / 2 == 0) { //Moves played card to correct row
            card.getSelf().getParent().move(0.0f, 0, -0.5f / 4);
        } else if (index / 2 == 2) {
            card.getSelf().getParent().move(0.0f, 0, 0.5f / 4);
        }
        
    
        //Trigger any card effects
        String cardName = card.getName();
        switch (cardName.substring(4, cardName.indexOf("-"))) {
            case "Cook":
                parent.buffAll(1);
                break;
            case "Gunner":
                parent.opposite().buffRandom(-3);
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
