/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

/**
 *
 * @author viet
 */
public class GameEnvironment {
    private final Node rootNode;
    private final AssetManager assetManager;
    private final ViewPort viewPort;
    private final GameShadows shadows;

    private FilterPostProcessor fpp;
    private WaterFilter water;
    private Vector3f lightDir = new Vector3f(-4.9f, -1.3f, 5.9f); // same as light source
    private float initialWaterHeight = -5f; // choose a value for your scene

    private float time = 0.0f;
    private float waterHeight = 0.0f;

    public GameEnvironment(Node rootNode, AssetManager assetManager, ViewPort viewPort, GameShadows shadows) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
	this.shadows = shadows;
	this.viewPort = viewPort;
    }

    public void setupSkybox() {
	rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/kloppenheim_02_puresky_4k.hdr", SkyFactory.EnvMapType.EquirectMap));
    }

    public void addOcean() {
	fpp = shadows.getFPP();
	Node empty = new Node("Empty Node");
	rootNode.attachChild(empty);
	// We pass an empty node to water because we don't need any reflections
	water = new WaterFilter(empty, lightDir);
	water.setWaterHeight(initialWaterHeight);
    	water.setUseRipples(false);
	fpp.addFilter(water);
	viewPort.addProcessor(fpp);
    }

    public void addWaves(float tpf) {
	time += tpf;
	waterHeight = (float) Math.cos(((time * 0.8f) % FastMath.TWO_PI)) * 3.5f;
	water.setWaterHeight(initialWaterHeight + waterHeight);
	//System.out.println("Wave height: " + waterHeight);
    }
}
