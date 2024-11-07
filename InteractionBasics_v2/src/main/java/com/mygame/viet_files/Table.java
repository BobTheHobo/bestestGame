package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mygame.PhysicsHelper;

/**
* @author shawn
*/
public class Table {

    private Node table_node;

    // Constructor that takes all parameters needed to create and add a candle to the world
    public Table(String name, Vector3f position, boolean canBePickedUp,
                  AssetManager assetManager, BulletAppState bulletAppState, GameShadows shadows) {
        
     // Invisible table node everything is attached to
	    table_node = new Node(name);
	    table_node.setLocalTranslation(position);

	    // Load actual model and attach it to table node
	    //Spatial table = assetManager.loadModel("Models/mdl_table_main_v2/mdl_table_main_v2.j3o");
	    Spatial table = assetManager.loadModel("Models/3D Models/Non-Interactable Environmental/Tables/mdl_longSideTable_main_v1.glb");
	    table_node.attachChild(table);

	    // Rotate 90
	    Quaternion rotate90 = new Quaternion();
	    rotate90.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0, 1, 0));
	    table.rotate(rotate90);

	    // Scale
	    table_node.scale(2f);

	    // Set to origin
	    table_node.center();

	    // Add shadows
	    shadows.attachShadowCastAndReceive(table);

	    // Add collisions
	    PhysicsHelper.addPhysics(table_node, false, bulletAppState);
    }

    public Node getNode() {
	return table_node;
    }
}
