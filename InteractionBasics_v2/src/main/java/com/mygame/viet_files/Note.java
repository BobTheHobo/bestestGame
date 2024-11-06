package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;

/**
* @author shawn
*/
public class Note {

    private Geometry noteGeom;
    private boolean isReadable = false;

    // Constructor that sets up the note in the world
    public Note(String name, Vector3f position, Vector3f size, ColorRGBA color, 
                AssetManager assetManager, Node rootNode, BulletAppState bulletAppState) {
        
        // Create the note geometry (a thin box)
        Box box = new Box(size.x, size.y, size.z);
        noteGeom = new Geometry(name, box);
        noteGeom.setLocalTranslation(position);

        // Create material and set color
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        noteGeom.setMaterial(mat);

        // Set the "isReadable" flag as user data for interaction purposes
        noteGeom.setUserData("isReadable", true);

        // Add physics to the note as a static object (mass = 0)
        RigidBodyControl physicsControl = new RigidBodyControl(0.0f);
        noteGeom.addControl(physicsControl);
        bulletAppState.getPhysicsSpace().add(physicsControl);

        // Attach note to root node
        rootNode.attachChild(noteGeom);
    }

    public Geometry getGeometry() {
        return noteGeom;
    }

    // Getter to check if the note is readable
    public boolean isReadable() {
        return isReadable;
    }

    // Setter to change readability status
    public void setReadable(boolean readable) {
        this.isReadable = readable;
    }
}
