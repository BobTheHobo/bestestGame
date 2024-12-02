/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * Represents a Key object in the game for prototyping.
 * @author shawn
 */
public class Key {

    private Geometry keyGeom;

    // Constructor that takes all parameters needed to create and add a key to the world
    public Key(String name, Vector3f position, Vector3f size, AssetManager assetManager, Node rootNode, BulletAppState bulletAppState) {
        
        // Create the key geometry (a box)
        Box box = new Box(size.x, size.y, size.z);
        keyGeom = new Geometry(name, box);
        keyGeom.setLocalTranslation(position);

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

        // Attach the key to the root node
        rootNode.attachChild(keyGeom);
    }

    // Getter for the geometry
    public Geometry getGeometry() {
        return keyGeom;
    }
}
