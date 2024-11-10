/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.water.SimpleWaterProcessor;
import com.mygame.jeremiah_files.Board;
import com.mygame.jeremiah_files.Galley;

/**
 *
 * @author viet
 */
public class BoardEnvironment {
	private Node rootNode;
	private AssetManager assetManager;
	private ViewPort viewPort;

	private Node boardEnvNode;
	private Node boardModels;
	private Board board;

	private SimpleWaterProcessor waterProcessor;
	
	public BoardEnvironment(Node rootNode, AssetManager assetManager, ViewPort viewPort) {
		this.assetManager = assetManager;
		this.viewPort = viewPort;
		this.rootNode = rootNode;

		boardEnvNode = new Node("Board Env Node");	

		// Board Models
		boardModels = new Node("Board Env Models");
		boardEnvNode.attachChild(boardModels);

		// Attach to rootnode
		rootNode.attachChild(boardEnvNode);
	}	

	public void attachOceanToBoard(Board board) {
		this.board = board;
		Geometry playMat = (Geometry) rootNode.getChild("PlayMat");

		// Make ocean and attach it to the mesh
		this.makeBoardOcean(playMat);
	}

	public Node getNode() {
		return boardEnvNode;
	}

	public void attachGalleys(Board board) {
		for(Galley g : board.getEnemyGalleys()) {
			g.getSelfNode().attachChild(makeEnemyGalley());
		}
		for(Galley g : board.getPlayerGalleys()) {
			g.getSelfNode().attachChild(makePlayerGalley());
		}
	}

	private Spatial makeGalley() {
		Spatial galley = assetManager.loadModel("Models/Ship/Ship.j3o");
		Util.addAmbient(galley, assetManager);
		galley.scale(0.1f);
		return galley;
	}

	private Spatial makeEnemyGalley() {
		Spatial enemyGalley = makeGalley();

		Quaternion rotate = new Quaternion();
		rotate.fromAngleAxis(FastMath.PI/2, new Vector3f(0, 1, 0));
		enemyGalley.setLocalRotation(rotate);

		return enemyGalley;
	}

	private Spatial makePlayerGalley() {
		Spatial playerGalley = makeGalley();

		Quaternion rotate = new Quaternion();
		rotate.fromAngleAxis(-FastMath.PI/2, new Vector3f(0, 1, 0));
		playerGalley.setLocalRotation(rotate);

		return playerGalley;
	}

	private void makeBoardOcean(Geometry playMat) {

		//create water plane
		Geometry waterPlane = new Geometry();
		waterPlane.setMesh(playMat.getMesh());

		// Attach so waterPlane inherits the board node's location
		board.getSelfNode().attachChild(waterPlane);

		// Set same transform as board
		waterPlane.setLocalTransform(playMat.getLocalTransform());
		waterPlane.move(new Vector3f(0,0.0005f,0));

		Vector3f waterLocation = waterPlane.getWorldTranslation();

		//create processor
		waterProcessor = new SimpleWaterProcessor(assetManager);
		waterProcessor.setDebug(true);

		// Set reflection scene and plane
		waterProcessor.setReflectionScene(rootNode.getChild("Room setup node"));
		waterProcessor.setPlane(waterPlane.getWorldTranslation(), Vector3f.UNIT_Y);

		// Settings
		waterProcessor.setWaterColor(ColorRGBA.Blue); // color
		waterProcessor.setWaterTransparency(1000);
		waterProcessor.setWaterDepth(0.01f);   	  // adjust this if color not showing thru      
		waterProcessor.setDistortionScale(0.03f); // strength of waves
		waterProcessor.setWaveSpeed(0.05f);       // speed of waves
		waterProcessor.setTexScale(60f);		// Scale of water texture
		waterProcessor.setReflectionClippingOffset(0); // DO NOT CHANGE
		waterProcessor.setRefractionClippingOffset(0); // DO NOT CHANGE
		waterProcessor.setDistortionMix(0.9f);


		waterPlane.setMaterial(waterProcessor.getMaterial());

		viewPort.addProcessor(waterProcessor);
	}
}
