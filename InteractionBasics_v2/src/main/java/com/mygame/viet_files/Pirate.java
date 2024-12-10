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
public class Pirate {

    	private Node pirate_node;
    	private RigidBodyControl control;

	private AnimComposer animComposer;
	private static final String idle_anim = "idle";

    	// Constructor that takes all parameters needed to create and add a clock to the world
	public Pirate(String name, Vector3f position, AssetManager assetManager, BulletAppState bulletAppState, GameShadows shadows) {
		
		// Invisible node everything is attached to
		pirate_node = new Node();
		
		// Load actual model and attach it to node
		Spatial pirate = assetManager.loadModel("Models/pirate/pirate.j3o");
                pirate.setCullHint(Spatial.CullHint.Never);
                
		pirate_node.attachChild(pirate);
		
		// Move
		pirate_node.setLocalTranslation(position);

		// Set up animation on correct geometry
		this.animComposer = ((Node)pirate).getChild(0).getControl(AnimComposer.class);
		
		// Add shadows
		shadows.attachShadowCastAndReceive(pirate);
		
		// Add collisions
		PhysicsHelper.addPhysics(pirate_node, false, false, bulletAppState);
                
                playAnimation();
	}

	public Node getNode() {
            return pirate_node;
	}
        
        public void playAnimation() {
            // loop idle animation
            animComposer.setCurrentAction(idle_anim, AnimComposer.DEFAULT_LAYER, true);
        }
        
        public AnimComposer getAnimComposer() {
            return this.animComposer;
        }
}
