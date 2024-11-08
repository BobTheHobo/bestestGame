package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mygame.PhysicsHelper;

/**
* @author shawn, viet
*/
public class Table extends Node {

    	private Node table_node;
    	private RigidBodyControl control;

    	// Constructor that takes all parameters needed to create and add a candle to the world
    	public Table(String name, Vector3f position, boolean canBePickedUp, AssetManager assetManager, BulletAppState bulletAppState, GameShadows shadows) {
        
     		// Invisible table node everything is attached to
	    	table_node = new Node(name);

	    	// Load actual model and attach it to table node
	    	//Spatial table = assetManager.loadModel("Models/mdl_table_main_v2/mdl_table_main_v2.j3o");
	    	Spatial table = assetManager.loadModel("Models/3D Models/Non-Interactable Environmental/Tables/mdl_longSideTable_main_v1.glb");
	    	table_node.attachChild(table);

		// Translate
	    	table_node.setLocalTranslation(position);

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
		control = PhysicsHelper.addPhysics(table_node, false, false, bulletAppState);
        }

	public Node getNode() {
		return table_node;
	}

	// Translate to keep both object and physics in sync
	public void translate(Vector3f loc) {
		table_node.setLocalTranslation(loc);
		control.setPhysicsLocation(loc);
    	}
	
	public void rot(Quaternion quat) {
		table_node.setLocalRotation(quat);
		control.setPhysicsRotation(quat);
	}

	// Translate the location of a control
	public void translateControl(Vector3f loc) {
		control.setPhysicsLocation(loc);
	}
}
