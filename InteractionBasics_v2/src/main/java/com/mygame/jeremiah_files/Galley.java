/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.jeremiah_files;

import com.jme3.asset.AssetManager;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author jerem
 */
public class Galley {
    private static Box mesh = new Box(.35f, .001f, .8f);
    private ArrayList<Slot> slots = new ArrayList<>(); //The six playable slots on each galley
    private Board parent; //The board that contains the galleys
    private int index; //Which of the 6 galleys this one is (0-5) 
    private Spatial self; //The spatial object this class represents
    private String name; //The Galley's name
    private Node selfNode; //The Galley's node
    private AssetManager assetManager; //Asset manager
    private Random rand = new Random();
    private boolean sunk = false;
    
    public Galley(Board parent, int index) {
        this.parent = parent;
        this.index = index;
        assetManager = parent.getAssetManager();
        name = String.format("Galley%d", index);
        
        selfNode = new Node(name);
        
        String tileName = String.format("GalleyMat%d", index);
        Geometry self = new Geometry(tileName, mesh);
        Material tileMat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        tileMat.setColor("Color", ColorRGBA.Brown);
        tileMat.setTexture("ColorMap", assetManager.loadTexture("Textures/Wood/AT_Wood_01_BUMP.jpg"));
        self.setMaterial(tileMat);
        self.center();
        selfNode.attachChild(self);
        
        makeSlots();
        
        selfNode.center();
        selfNode.move((-.2f + ((index % 3)) * .2f), .43f, (.2f + ((index / 3) * .6f))); 
        selfNode.scale(.25f);
        parent.getSelfNode().attachChild(selfNode);
    }
    
    //Initializes the slots array and the slots in the 3d scene
    private void makeSlots(){
        for (int i = 0; i < 6; i++) {
            slots.add(new Slot(this, i));
        }
    }
    
    //Plays the card on a random available slot
    public void playRandom(Card card) {
        ArrayList<Slot> open = new ArrayList<>();//To collect all slots that are valid
        for (int i = 0; i < 6; i++) {
            if(!slots.get(i).getFilled()) {//Slot does not have a card
                open.add(slots.get(i));
            }
        }
        
        
        int pull = rand.nextInt(open.size());//Choose randomly from available slots    
        open.get(pull).setCard(card);//Put card in slot
        
    }
    
    //Changes a singular random card on the galley, negative power for weakening
    public void buffRandom(int power) {
        ArrayList<Slot> open = new ArrayList<>();//To collect all slots that are filled
        for (int i = 0; i < slots.size(); i++) {
            if(slots.get(i).getFilled()) {//Slot has a card
                open.add(slots.get(i));
            }
        }
        
        if (!open.isEmpty()) {
            int pull = rand.nextInt(open.size());//Choose randomly from available slots
            open.get(pull).getCard().changePower(power);//Affect the card
        }
    }
    
    //Changes all cards on galley by 'power', negative power for weakening
    public void buffAll(int power) {
        for (int i = 0; i < slots.size(); i++) {
            if(slots.get(i).getFilled()) {//Slot has a card
                slots.get(i).getCard().changePower(power);
            }
        }
    }
    
    public Galley opposite() {
        return parent.getOppositeGalley(this);
    }
    
    public Slot getSlot(int slot) {
        return slots.get(slot);
    }
    
    public int getIndex() {
        return index;
    }
    
    public Board getParent() {
        return parent;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public Node getSelfNode() {
        return selfNode;
    }
    
    //If there is a playable slot on the galley
    public boolean open() {
        ArrayList<Slot> open = new ArrayList<>();//To collect all slots that are valid
        for (int i = 0; i < 3; i++) {
            if(!slots.get(i).getFilled()) {//Slot does not have a card
                open.add(slots.get(i));
            }
        }
        return !open.isEmpty(); //No open slots on this galley
    }
    
    //Sums power of each slot
    public int getPower() {
        int toReturn = 0;
        for (int i = 0; i < 6; i++) {
            if(slots.get(i).getFilled()) {//Slot has a card
                toReturn += slots.get(i).getCard().getPower();
            }
        }
        return toReturn;
    }
    
    //Removes all currently played cards from thex galley
    public void clear() {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getFilled()) {// There is a card
                parent.discard((index < 3), slots.get(i).getCard());
            }
            //Empty the slots
            slots.get(i).setFilled(false);
        }
    }
    
    public boolean getSunk() {
        return sunk;
    }
    
    public void setSunk(boolean sunk) {
            this.sunk = sunk;
    }
    
    public void reset() {
        selfNode.center();
        selfNode.move((-.2f + ((index % 3)) * .2f), .43f, (.2f + ((index / 3) * .6f))); 
        selfNode.scale(.25f);
        parent.getSelfNode().attachChild(selfNode);
    }
    
}
