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
    
    
    public int getIndex() {
        return index;
    }
    
    public Galley getParent() {
        return parent;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
}
