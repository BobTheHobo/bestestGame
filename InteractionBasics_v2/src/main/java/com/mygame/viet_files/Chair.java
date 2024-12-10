/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mygame.PhysicsHelper;

/**
 *
 * @author viet
 */
public class Chair {

    	private Node chair_node;
        private Node rootNode;
        private String name; 
        private Vector3f position;
        private Quaternion rot;
        private AssetManager assetManager;
        private BulletAppState bulletAppState;
        private GameShadows shadows;
    	private RigidBodyControl control;

    	// Constructor that takes all parameters needed to create and add a clock to the world
	public Chair(String name, Vector3f position, Quaternion rot, Node rootNode, AssetManager assetManager, BulletAppState bulletAppState, GameShadows shadows) {
            this.name = name;
            this.position = position;
            this.rot = rot;
            this.rootNode = rootNode;
            this.assetManager = assetManager;
            this.bulletAppState = bulletAppState;
            this.shadows = shadows;
            
            insertChair();
	}
        
        // Inserts a chair
        private void insertChair() {
            // Invisible clock node everything is attached to
            chair_node = new Node(name);
            chair_node.setLocalTranslation(position);
            chair_node.setLocalRotation(rot);

            // Load actual model and attach it to candle node
            Spatial chair = assetManager.loadModel("Models/Chair/Chair.j3o");
            chair_node.attachChild(chair);

            // Add shadows
            shadows.attachShadowCastAndReceive(chair);

            // Add collisions
            PhysicsHelper.addPhysics(chair_node, false, false, bulletAppState);	
        }

	public Node getNode() {
            return chair_node;
	}
        
        public void removeChair() {
            rootNode.detachChild(chair_node);
        }
        
        public void showChair() {
            rootNode.attachChild(chair_node);
        }
}
