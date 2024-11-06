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
* @author shawn
*/
public class Candle {

    private Geometry candleGeom;

    // Constructor that takes all parameters needed to create and add a candle to the world
    public Candle(String name, Vector3f position, Vector3f size, ColorRGBA color, boolean canBePickedUp,
                  AssetManager assetManager, Node rootNode, BulletAppState bulletAppState) {
        
        // Create the candle geometry (a box)
        Box box = new Box(size.x, size.y, size.z);
        candleGeom = new Geometry(name, box);
        candleGeom.setLocalTranslation(position);

        // Create material and set color
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        candleGeom.setMaterial(mat);

        // Set the "canBePickedUp" flag in user data
        candleGeom.setUserData("canBePickedUp", canBePickedUp);

        // Add physics if required
        RigidBodyControl physicsControl = new RigidBodyControl(canBePickedUp ? 1.0f : 0.0f); // 1.0f mass if pickable, 0 for static
        candleGeom.addControl(physicsControl);
        bulletAppState.getPhysicsSpace().add(physicsControl);

        // Attach candle to root node
        rootNode.attachChild(candleGeom);

    }

    public Geometry getGeometry() {
        return candleGeom;
    }
}
