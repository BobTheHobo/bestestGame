package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
	app.setShowSettings(false); // Turn off settings options at launch
        app.start();
    }

    @Override
    public void simpleInitApp() {
	    setupScene();
	    setupCamera();
	    setupLighting();
    }

    // Spawns models in and places them
    private void setupScene() {
	// Main node for the room
	Node room_node = new Node("Room setup node");

	// Rotation quats along Y axis
	Quaternion rotateY90 = new Quaternion();
	rotateY90.fromAngleAxis(90f *FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
	
	// Ship hull
	Spatial room_model = assetManager.loadModel("Models/mdl_room_main_v1/mdl_room_main_v1.j3o");
	room_node.attachChild(room_model);

	Spatial main_table = assetManager.loadModel("Models/mdl_table_main_v2.j3o");
	room_node.attachChild(main_table);

	// Note when referring to blender positions, x is the same, y and z are swapped
	
	Spatial table_candle = insertCandle(new Vector3f(-0.9f, 2.2f, -0.01f));
	room_node.attachChild(table_candle);

	Spatial chest = insertChest(new Vector3f(-5f, 1f, 0.2f));
	chest.setLocalRotation(rotateY90);
	room_node.attachChild(chest);

	// Just sets room node to the origin
        Vector3f origin = new Vector3f(0f, 0f, 0f);
        room_node.setLocalTranslation(origin);


	rootNode.attachChild(room_node);
    }

    private void setupCamera() {
	// Set camera position to something more natural to scene
	cam.setLocation(new Vector3f(0f, 4f, 7f));

    }

    private void setupLighting() {
	// Directional light (basically sun) points in one direction from infinitely far away
	// DirectionalLight sun = new DirectionalLight();
	// sun.setDirection((new Vector3f(-0.5f, -0.3f, -1.5f)));
	// sun.setColor(ColorRGBA.White.mult(0.5f));
	// rootNode.addLight(sun);

	// Small pointlight just arbitrarily to light up scene
	PointLight pl = new PointLight();
	pl.setColor(ColorRGBA.White.mult(0.4f)); //Adjust mult value to increase/decrease brightness
	pl.setRadius(18f);
	Vector3f pl_vec = new Vector3f(0.0f, 7f, 0f);
	pl.setPosition(pl_vec);
	rootNode.addLight(pl);

	// AmbientLight doesn't work because we don't have any materials yet
	// AmbientLight al = new AmbientLight();
	// al.setColor(ColorRGBA.White.mult(1.3f));
	// rootNode.addLight(al);

    }

    // Creates a simple blue box for testing and whatnot
    private Geometry insertBlock() {
	Box b = new Box(0.1f, 0.1f, 0.1f); // create cube shape
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        geom.setMaterial(mat);  // set the cube's material
	return geom;
    }

    // Inserts a candle and returns the spatial
    private Spatial insertCandle() {
        Vector3f origin = new Vector3f(0f, 0f, 0f);
	return insertCandle(origin);
    }

    // Overloaded to be able to specify a location for the candle
    private Spatial insertCandle(Vector3f loc) {
	// Invisible candle node everything is attached to
	Node candle_node = new Node("Candle node");
	candle_node.setLocalTranslation(loc);
	    
	// Load actual model and attach it to candle node
	Spatial candle = assetManager.loadModel("Models/mdl_candle_main_v2/mdl_candle_main_v2.j3o");
	candle_node.attachChild(candle);

	// Invisible flame node, specifies where light spawns from
	Node flame_node = new Node("Flame node for Candle");
	flame_node.move(0f, 0.8f, 0f); // Add a little offset to make light spawn from actual flame
	candle_node.attachChild(flame_node);
	
	// Used to find out where to set light origin
	// Geometry geom = insertBlock();
        // flame_node.attachChild(geom);

	// Pointlight from candle
	PointLight candle_light = new PointLight();
	candle_light.setColor(ColorRGBA.Orange);
	candle_light.setRadius(8f);
	rootNode.addLight(candle_light);

	// Makes light source follow the flame node, Will be useful if candle is moved
	LightControl candleControl = new LightControl(candle_light);
	flame_node.addControl(candleControl);

	// Ambient light test, doesn't work for now
	// AmbientLight al = new AmbientLight();
	// al.setColor(ColorRGBA.Red.mult(1.3f));
	// flame_node.addLight(al);

        return candle_node;
    }

    // Inserts chest
    private Spatial insertChest(Vector3f loc) {
	// Invisible chest node everything is attached to
	Node chest_node = new Node("Chest node");
	chest_node.setLocalTranslation(loc);

	// Load actual model and attach it to candle node
	Spatial chest = assetManager.loadModel("Models/mdl_chest_closed_v2/mdl_chest_closed_v2.j3o");
	chest_node.attachChild(chest);

	return chest_node;
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

