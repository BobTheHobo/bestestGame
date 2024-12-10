/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.mygame.PhysicsHelper;

/**
 * Represents a Key object in the game for prototyping.
 * @author shawn
 */
public class Key {

    private Node key_node;
    private Geometry keyGeom;
    private AssetManager assetManager;
    private Vector3f position;
    private Vector3f size;
    private Node rootNode;
    private BulletAppState bulletAppState;
    private GameShadows shadows;
    private String name;
    private boolean keyRemoved;

    // Constructor that takes all parameters needed to create and add a key to the world
    public Key(String name, Vector3f position, Vector3f size, AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, GameShadows shadows) {
        this.assetManager = assetManager;
        this.position = position;
        this.size = size;
        this.rootNode = rootNode;
        this.bulletAppState = bulletAppState;
        this.shadows = shadows;
        
        this.keyRemoved = false;
        
        //key itself isn't always attached to key_node
        key_node = new Node("Key node");
        createKey();
    }

    // Getter for the geometry
    public Spatial getGeometry() {
        return keyGeom;
    }
    
    // Getter for the key node
    public Spatial getKeyNode() {
        return key_node;
    }
    
    public void removeKey() {
        keyRemoved = true;
        
        try { 
            Spatial key = rootNode.getChild("key");
            
            // just blanket remove everything
            rootNode.detachChild(key);
            ((Node)rootNode.getChild("HandNode")).detachChild(keyGeom.getParent());
            
            // rename the key because name of node is how it keeps on getting reattached in drop item handler
            key.setName("keyremoved");
        } catch (Exception ex) {}
    }
    
    // Create key
    private void createKey() {
	// Invisible candle node everything is attached to
	key_node.setLocalTranslation(position);
	    
	// Load actual model and attach it to candle node
	Spatial key = assetManager.loadModel("Models/mdl_key_main_v1/mdl_key_main_v1.j3o");
        keyGeom = (Geometry)((Node)key).getChild(0);
        
	key.setName("key");
	key_node.attachChild(key);

	shadows.attachShadowCastAndReceive(key_node);

        // Set the canBePickedUp flag as user data
        key.setUserData("canBePickedUp", true);
        key.setUserData("puzzle", true);
        
        // Create a custom cube-shaped collision shape
        Vector3f halfExtents = new Vector3f(0.2f, 0.1f, 0.44f); // Cube dimensions: 1x1x1
        BoxCollisionShape cubeShape = new BoxCollisionShape(halfExtents);
      
	// Add physics
	PhysicsHelper.addPhysics(key, true, true, bulletAppState, cubeShape);
    }
    
    // Create box placeholder
    private void createKeyBox() {
        
        // Create the key geometry (a box)
        Box box = new Box(size.x, size.y, size.z);
        keyGeom = new Geometry(name, box);
        keyGeom.setLocalTranslation(position);
        key_node.attachChild(keyGeom);

        // Create material and set color (gray)
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        keyGeom.setMaterial(mat);

        // Set the "canBePickedUp" flag in user data
        keyGeom.setUserData("canBePickedUp", true);
        keyGeom.setUserData("puzzle", true);

        // Add physics with a mass of 1.0f for dynamic objects
        RigidBodyControl physicsControl = new RigidBodyControl(1.0f); // 1.0f mass makes it pickable
        keyGeom.addControl(physicsControl);
        bulletAppState.getPhysicsSpace().add(physicsControl);
    }
}
