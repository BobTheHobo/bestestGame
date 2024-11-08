package com.mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/*
* author: shawn
*/
public class SceneManager {

    private BulletAppState bulletAppState;
    private Node rootNode;
    private AssetManager assetManager;
    private Node geomsNode;

    public SceneManager(BulletAppState bulletAppState, Node rootNode, AssetManager assetManager) {
        this.bulletAppState = bulletAppState;
        this.rootNode = rootNode;
        this.assetManager = assetManager;
    }

    // Set up the scene with floor, walls, obstacles, and pickable items
    public void setupScene() {
        geomsNode = new Node();
        rootNode.attachChild(geomsNode);
        createBox("Floor", new Vector3f(0, -1, 0), new Vector3f(10, 1, 10), ColorRGBA.Orange, false);
        createBox("Wall1", new Vector3f(0, 2, -10), new Vector3f(10, 2, 1), ColorRGBA.Brown, false);
        createBox("Wall2", new Vector3f(0, 2, 10), new Vector3f(10, 2, 1), ColorRGBA.Brown, false);
        createBox("Wall3", new Vector3f(-10, 2, 0), new Vector3f(1, 2, 10), ColorRGBA.Brown, false); // Left wall
        createBox("Wall4", new Vector3f(10, 2, 0), new Vector3f(1, 2, 10), ColorRGBA.Brown, false); 
        createBox("Obstacle1", new Vector3f(5, 1, 5), new Vector3f(1, 1, 1), ColorRGBA.Gray, false);
        createBox("Item1", new Vector3f(3, 0.3f, 3), new Vector3f(0.3f, 0.3f, 0.3f), ColorRGBA.Green, true);
    }

    private void createBox(String name, Vector3f position, Vector3f size, ColorRGBA color, boolean canBePickedUp) {
        // Create the box geometry
        Box box = new Box(size.x, size.y, size.z);
        Geometry geom = new Geometry(name, box);
        geom.setLocalTranslation(position);

        // Assign a material to the geometry
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);  // Ensure the material is set

        // Set the canBePickedUp flag as user data
        geom.setUserData("canBePickedUp", canBePickedUp);

        // Add physics to the geometry
        PhysicsHelper.addPhysics(geom, canBePickedUp, canBePickedUp, bulletAppState);

        // Attach geometry to the scene graph
        geomsNode.attachChild(geom);
    }



}
