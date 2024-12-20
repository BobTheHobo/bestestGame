package com.mygame.viet_files;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import com.mygame.PhysicsHelper;
import com.mygame.PlayerInteractionManager;

/**
 * @author viet
 */
public class SceneCreatorOld extends AbstractAppState {

    private Node rootNode;
    private AssetManager assetManager;
    private SimpleApplication app;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;
    private GameShadows shadows;
    private PlayerInteractionManager playerInteractionManager;
    
    private final static Trigger TRIGGER_P= new KeyTrigger(KeyInput.KEY_P);
    private final static String MAPPING_SCENE = "Next Scene";
    private boolean nextScene = false;
    private Node room_node;
    private Node guiNode;
    private Quaternion rotateY90;

    private GameLighting lighting;
    private GameEnvironment environment;
    private GameParticles particles;

    private Table mainTable;
    
    public SceneCreatorOld(Node rootNode, AssetManager assetManager, ViewPort viewPort, BulletAppState bulletAppState, GameShadows shadows, GameParticles particles, PlayerInteractionManager playerInteractionManager) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
	this.viewPort = viewPort;
	this.bulletAppState = bulletAppState;
	this.shadows = shadows;
	this.particles = particles;
	this.playerInteractionManager = playerInteractionManager;
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
	Table table = new Table("Main Table", new Vector3f(0,0,0), false, assetManager, bulletAppState, shadows);
	this.mainTable = table;
	table.translate(new Vector3f(0,1.4f,0));
	room_node.attachChild(table.getNode());

	// Side table
	//Table table2 = new Table("Side Table", new Vector3f(3,0,0), false, assetManager, bulletAppState, shadows);
	//System.out.println("loc: " + table2.getLocation());
	//table2.translate(new Vector3f(0,1,0));
	//System.out.println("loc: " + table2.getLocation());
	//room_node.attachChild(table2);

	// Grandfather clock
	//Spatial clock = insertClock(new Vector3f(-3.8f, 0f, -7f));
	Clock clock = new Clock("Clock Node", new Vector3f(-3.8f, 0f, -7f), false, assetManager, bulletAppState, shadows);
	room_node.attachChild(clock.getNode());

	// Note when referring to blender positions, x is the same, y and z are swapped
	// Also, flip signs between the y coords
	
	//Spatial table_candle = insertCandle(new Vector3f(0.6f, 2.1f, -1f));
	Spatial table_candle = insertCandle(new Vector3f(0.6f, 5f, -1f));
	playerInteractionManager.setCandle(table_candle);
	System.out.println(table_candle.getName());
	room_node.attachChild(table_candle);

	Spatial chest = insertChest(new Vector3f(-5f, 1f, 0.2f));
	room_node.attachChild(chest);

	Note note = new Note("NoteRectangle", new Vector3f(-4.5f, 0f, 8f), new Vector3f(0.4f, 0.01f, 0.2f), ColorRGBA.White, assetManager, rootNode, bulletAppState);
	Geometry notegeo = note.getGeometry();
	notegeo.setUserData("puzzle", true);
	rootNode.attachChild(note.getGeometry());
	playerInteractionManager.setNote(note.getGeometry());


	// block to test shadows
	//Geometry block = Util.insertBlock(assetManager, new Vector3f(0,2,0), 1);
	//shadows.attachShadowCastAndReceive(block);
	//room_node.attachChild(block);

	
	// Just sets room node to the origin
        Vector3f origin = new Vector3f(0f, 0f, 0f);
        room_node.setLocalTranslation(origin);


	rootNode.attachChild(room_node);

	//Util.printChildren(rootNode);
    }

    public Table getMainTable() {
	return this.mainTable;
    }

    private Spatial insertRoom() {
	Spatial room_model = assetManager.loadModel("Models/mdl_room_main_v4/mdl_room_main_v4.j3o");
	Spatial geo;

	for(Spatial s : ((Node)room_model).getChildren()) {
		System.out.println("Child is: " + s.getName());
		geo = s;
		int i = 0;
	}
	geo = ((Node)room_model).getChild(0);

	Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
	mat.setBoolean("UseMaterialColors", true);  // Set some parameters, e.g. blue.
	mat.setColor("Ambient", ColorRGBA.Brown);   // ... color of this object
	mat.setColor("Diffuse", ColorRGBA.White);   // ... color of light being reflected

	Texture diff = assetManager.loadTexture("Textures/Wood/AT_Wood_01_DIFF.jpg");
	diff.setWrap(Texture.WrapMode.Repeat);
	mat.setTexture("DiffuseMap", diff);

	//TangentBinormalGenerator.generate(geo);
	//Texture norm = assetManager.loadTexture("Textures/Wood/AT_Wood_01_NORM.jpg");
	//norm.setWrap(Texture.WrapMode.Repeat);
	///mat.setTexture("NormalMap", norm);

	geo.setMaterial(mat);
	//setTextureScale(geo, new Vector2f(0.8f, 0.8f));
	setTextureScale(geo, new Vector2f(30, 30));

	// Add collisions
	PhysicsHelper.addPhysics(room_model, false, false, bulletAppState);

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

	candle.setName("candle");
	candle_node.attachChild(candle);

	Node geo = (Node)candle_node.getChild("candle");

	Spatial flame;
	Spatial something;
	Spatial candleBody;
	Spatial holder;

	for(Spatial s : geo.getChildren()) {
		int i = 0;
		for(Spatial sp : ((Node)s).getChildren()) {
			if (i == 0) {
				candleBody = sp;
				Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
				TangentBinormalGenerator.generate(candleBody);
				mat.setTexture("NormalMap", assetManager.loadTexture("Textures/candle-wax-bump-map.jpg"));

			}
			if (i == 2) {
				holder = sp;
				Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
				mat.setFloat("Shininess", 10f);
				mat.setBoolean("UseMaterialColors", true);  // Set some parameters, e.g. blue.
				mat.setColor("Specular", ColorRGBA.Orange);   // ... color of this object
				mat.setColor("Diffuse", ColorRGBA.Orange);   // ... color of light being reflected
				holder.setMaterial(mat);
			}
			i++;
		}
	}

	//System.out.println(geo.getName());

	// Allow model to cast and receive shadows
	shadows.attachShadowCastAndReceive(candle);

	// Invisible flame node, specifies where light spawns from
	Node flame_node = new Node("Flame node for Candle");
	flame_node.move(0f, 0.8f, 0f); // Add a little offset to make light spawn from actual flame
	particles.addFireParticles(flame_node); // Add fire particles
	candle_node.attachChild(flame_node);
	
	// Used to find out where to set light origin
	// Geometry geom = Util.insertBlock();
        // flame_node.attachChild(geom);

	// Pointlight from candle
	PointLight candle_light = new PointLight();
	candle_light.setColor(ColorRGBA.Orange.mult(1.2f));
	candle_light.setRadius(8f);
	shadows.attachPointLight(candle_light); // Allow this pointlight to cast shadows
	room_node.addLight(candle_light);

	// Makes light source follow the flame node, Will be useful if candle is moved
	LightControl candleControl = new LightControl(candle_light);
	flame_node.addControl(candleControl);

	// Ambient light test, doesn't work for now
	// AmbientLight al = new AmbientLight();
	// al.setColor(ColorRGBA.Red.mult(1.3f));
	// flame_node.addLight(al);

        // Set the canBePickedUp flag as user data
        candle_node.setUserData("canBePickedUp", true);

	// Add physics
	PhysicsHelper.addPhysics(candle_node, true, true, bulletAppState);	

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
	PhysicsHelper.addPhysics(chest_node, false, false, bulletAppState);	

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
	PhysicsHelper.addPhysics(clock_node, false, false, bulletAppState);	

	return clock_node;
    }

	public void setTextureScale(Spatial spatial, Vector2f vector) {

		if (spatial instanceof Node) {

			Node findingnode = (Node) spatial;

			for (int i = 0; i < findingnode.getQuantity(); i++) {

				Spatial child = findingnode.getChild(i);

				setTextureScale(child, vector);

			}

		} else if (spatial instanceof Geometry) {

			((Geometry) spatial).getMesh().scaleTextureCoordinates(vector);

		}

	}

}
