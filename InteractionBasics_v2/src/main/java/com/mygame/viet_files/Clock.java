/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mygame.PhysicsHelper;

/**
 *
 * @author viet
 */
public class Clock {

    	private Node clock_node;
    	private RigidBodyControl control;

	private AnimComposer animComposer;
	private static final String ANI_MOVE_FACE = "move face";

    	// Constructor that takes all parameters needed to create and add a clock to the world
	public Clock(String name, Vector3f position, boolean canBePickedUp, AssetManager assetManager, BulletAppState bulletAppState, GameShadows shadows) {
		
		// Invisible clock node everything is attached to
		clock_node = new Node();
		
		// Load actual model and attach it to candle node
		//Spatial clock = assetManager.loadModel("Models/mdl_grandfatherclock_main_v2_fixorigin/mdl_grandfatherclock_main_v2_fixorigin.j3o");
		//Spatial clock = assetManager.loadModel("Models/test/test.j3o");
		Spatial clock = assetManager.loadModel("Models/Clock-With-Anim/Clock-With-Anim.j3o");
		clock_node.attachChild(clock);
		
		// Move
		clock_node.setLocalTranslation(position);

		// Set up animations
		Util.printChildren(clock);
		//this.animComposer = clock.getControl(AnimComposer.class);
		//System.out.println("pp: " + animComposer.toString());
		
		// Print out available animations
		//Util.printAnimationNames(animComposer);

		// Add animation
		//animComposer.setCurrentAction("move face");
		
		// Add shadows
		shadows.attachShadowReceive(clock);
		
		// Add collisions
		PhysicsHelper.addPhysics(clock_node, false, false, bulletAppState);
	}

	public Node getNode() {
		return clock_node;
	}
	
}
