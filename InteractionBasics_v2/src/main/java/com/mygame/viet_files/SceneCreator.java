package com.mygame.viet_files;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.mygame.PhysicsHelper;

/**
 * @author viet
 */
public class SceneCreator extends AbstractAppState {

    private Node rootNode;
    private AssetManager assetManager;
    private SimpleApplication app;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;
    private GameShadows shadows;
    
    private final static Trigger TRIGGER_P= new KeyTrigger(KeyInput.KEY_P);
    private final static String MAPPING_SCENE = "Next Scene";
    private boolean nextScene = false;
    private Node room_node;
    private Node guiNode;
    private Quaternion rotateY90;

    private GameLighting lighting;
    private GameEnvironment environment;
    
    public SceneCreator(Node rootNode, AssetManager assetManager, ViewPort viewPort, BulletAppState bulletAppState, GameShadows shadows) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
	this.viewPort = viewPort;
	this.bulletAppState = bulletAppState;
	this.shadows = shadows;
    }
            
    // Spawns models in and places them
    public void setupScene() {
	// Main node for the room
	room_node = new Node("Room setup node");

	// Rotation quats along Y axis
	rotateY90 = new Quaternion();
	rotateY90.fromAngleAxis(90f *FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
	
	// Ship hull (without roof)
	// Spatial room_model = assetManager.loadModel("Models/mdl_room_main_v1_fixedmesh/mdl_room_main_v1_fixedmesh.j3o");
	// room_node.attachChild(room_model);
	
	// Ship hull (with roof)
	Spatial room_model = insertRoom();
	room_node.attachChild(room_model);

	// Main game table
	Spatial table = insertTable(new Vector3f(0f,0f,0f));
	room_node.attachChild(table);

	// Grandfather clock
	Spatial clock = insertClock(new Vector3f(-3.8f, 0f, -7f));
	room_node.attachChild(clock);

	// Note when referring to blender positions, x is the same, y and z are swapped
	// Also, flip signs between the y coords
	
	Spatial table_candle = insertCandle(new Vector3f(0.6f, 2.1f, -1f));
	//Spatial table_candle = insertCandle(new Vector3f(0.6f, 3f, -1f));
	System.out.println(table_candle.getName());
	room_node.attachChild(table_candle);

	Spatial chest = insertChest(new Vector3f(-5f, 1f, 0.2f));
	room_node.attachChild(chest);

	// block to test shadows
	//Geometry block = Util.insertBlock(assetManager, new Vector3f(0,2,0), 1);
	//shadows.attachShadowCastAndReceive(block);
	//room_node.attachChild(block);

	
	// Just sets room node to the origin
        Vector3f origin = new Vector3f(0f, 0f, 0f);
        room_node.setLocalTranslation(origin);


	rootNode.attachChild(room_node);
    }

    private Spatial insertRoom() {
	Spatial room_model = assetManager.loadModel("Models/mdl_room_main_v4/mdl_room_main_v4.j3o");

	// Add collisions
	PhysicsHelper.addPhysics(room_model, false, bulletAppState);

	// Add shadows
	shadows.attachShadowCastAndReceive(room_model);

	return room_model;
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
	Geometry geo = (Geometry) candle_node.getChild(candle_node.getChildIndex(candle));

	// Allow model to cast and receive shadows
	shadows.attachShadowCastAndReceive(candle);

	// Invisible flame node, specifies where light spawns from
	Node flame_node = new Node("Flame node for Candle");
	flame_node.move(0f, 0.8f, 0f); // Add a little offset to make light spawn from actual flame
	candle_node.attachChild(flame_node);
	
	// Used to find out where to set light origin
	// Geometry geom = Util.insertBlock();
        // flame_node.attachChild(geom);

	// Pointlight from candle
	PointLight candle_light = new PointLight();
	candle_light.setColor(ColorRGBA.Orange);
	candle_light.setRadius(8f);
	shadows.attachPointLight(candle_light); // Allow this pointlight to cast shadows
	rootNode.addLight(candle_light);

	// Makes light source follow the flame node, Will be useful if candle is moved
	LightControl candleControl = new LightControl(candle_light);
	flame_node.addControl(candleControl);

	// Ambient light test, doesn't work for now
	// AmbientLight al = new AmbientLight();
	// al.setColor(ColorRGBA.Red.mult(1.3f));
	// flame_node.addLight(al);

        // Set the canBePickedUp flag as user data
        geo.setUserData("canBePickedUp", true);

	// Add physics
	PhysicsHelper.addPhysics(geo, true, bulletAppState);	

        return candle_node;
    }

    // Inserts chest
    private Spatial insertChest(Vector3f loc) {
	// Invisible chest node everything is attached to
	Node chest_node = new Node("Chest node");
	chest_node.setLocalTranslation(loc);

	// Load actual model and attach it to the chest node
	Spatial chest = assetManager.loadModel("Models/mdl_chest_closed_v2/mdl_chest_closed_v2.j3o");
	chest_node.attachChild(chest);
	chest_node.setLocalRotation(rotateY90);

	// Add shadows
	shadows.attachShadowReceive(chest);

	// Add collisions
	PhysicsHelper.addPhysics(chest_node, false, bulletAppState);	

	return chest_node;
    }

    // Inserts grandfather clock
    private Spatial insertClock(Vector3f loc) {
	// Invisible clock node everything is attached to
	Node clock_node = new Node("Clock node");
	clock_node.setLocalTranslation(loc);

	// Load actual model and attach it to candle node
	Spatial clock = assetManager.loadModel("Models/mdl_grandfatherclock_main_v2_fixorigin/mdl_grandfatherclock_main_v2_fixorigin.j3o");
	clock_node.attachChild(clock);

	// Add shadows
	shadows.attachShadowReceive(clock);

	// Add collisions
	PhysicsHelper.addPhysics(clock_node, false, bulletAppState);	

	return clock_node;
    }

    private Spatial insertTable(Vector3f loc) {
	// Invisible table node everything is attached to
	Node table_node = new Node("Table node");
	table_node.setLocalTranslation(loc);

	// Load actual model and attach it to table node
	//Spatial table = assetManager.loadModel("Models/mdl_table_main_v2/mdl_table_main_v2.j3o");
        Spatial table = assetManager.loadModel("Models/3D Models/Non-Interactable Environmental/Tables/mdl_longSideTable_main_v1.glb");
	table_node.attachChild(table);

	// Rotate 90
        Quaternion rotate90 = new Quaternion();
        rotate90.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0));
        table.rotate(rotate90);

	// Scale
        table_node.scale(2f);

	// Add shadows
	shadows.attachShadowCastAndReceive(table);
	
	// Add collisions
	PhysicsHelper.addPhysics(table_node, false, bulletAppState);	

	return table_node;
    }
}
