/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

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
        self.setMaterial(tileMat);
        self.center();
        selfNode.attachChild(self);
        
        makeSlots();
        
        selfNode.center();
        selfNode.move((-.25f + ((index % 3)) * .25f), .43f, (.3f + ((index / 3) * .5f))); 
        selfNode.scale(.25f);
        parent.getSelfNode().attachChild(selfNode);
    }
    
    private void makeSlots(){
        for (int i = 0; i < 6; i++) {
            slots.add(new Slot(this, i));
        }
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
    
}
