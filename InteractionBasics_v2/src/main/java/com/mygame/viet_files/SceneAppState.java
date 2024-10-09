package com.mygame.viet_files;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;

/**
 * @author viet
 */
public class SceneAppState extends AbstractAppState {

    private Node rootNode;
    private AssetManager assetManager;
    private SimpleApplication app;
    
    private final static Trigger TRIGGER_P= new KeyTrigger(KeyInput.KEY_P);
    private final static String MAPPING_SCENE = "Next Scene";
    private boolean nextScene = false;
    private Node room_node;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //Initialize fields
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        
        
        InputManager inputManager = this.app.getInputManager();
        inputManager.addMapping(MAPPING_SCENE, TRIGGER_P);
        inputManager.addListener(actionListener, MAPPING_SCENE);

	setupScene();

	GameLighting lighting = new GameLighting(rootNode, assetManager);
	lighting.setupLighting();

        CardGameState state = new CardGameState();
        stateManager.attach(state);
    }
    
    private ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf)
            {
                if (!isPressed) {
                    if (name.equals(MAPPING_SCENE)) {
                        app.getFlyByCamera().setEnabled(true);
                    }
                    }
                }
    };
    
    public boolean getNextScene() {
        return nextScene;
    }
            
            

    // Spawns models in and places them
    public void setupScene() {
	// Main node for the room
	room_node = new Node("Room setup node");

	// Rotation quats along Y axis
	Quaternion rotateY90 = new Quaternion();
	rotateY90.fromAngleAxis(90f *FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
	
	// Ship hull (without roof)
	// Spatial room_model = assetManager.loadModel("Models/mdl_room_main_v1_fixedmesh/mdl_room_main_v1_fixedmesh.j3o");
	// room_node.attachChild(room_model);
	
	// Ship hull (with roof)
	Spatial room_model = assetManager.loadModel("Models/mdl_room_main_v3/mdl_room_main_v3.j3o");
	room_node.attachChild(room_model);

	// Grandfather clock
	Spatial clock = insertClock(new Vector3f(-3.8f, 0f, -7f));
	room_node.attachChild(clock);

	// Note when referring to blender positions, x is the same, y and z are swapped
	// Also, flip signs between the y coords
	
	Spatial table_candle = insertCandle(new Vector3f(0.6f, 2.1f, -1f));
	room_node.attachChild(table_candle);

	Spatial chest = insertChest(new Vector3f(-5f, 1f, 0.2f));
	chest.setLocalRotation(rotateY90);
	room_node.attachChild(chest);

	// Just sets room node to the origin
        Vector3f origin = new Vector3f(0f, 0f, 0f);
        room_node.setLocalTranslation(origin);


	rootNode.attachChild(room_node);
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

    // Inserts grandfather clock
    private Spatial insertClock(Vector3f loc) {
	// Invisible clock node everything is attached to
	Node clock_node = new Node("Clock node");
	clock_node.setLocalTranslation(loc);

	// Load actual model and attach it to candle node
	Spatial clock = assetManager.loadModel("Models/mdl_grandfatherclock_main_v2_fixorigin/mdl_grandfatherclock_main_v2_fixorigin.j3o");
	clock_node.attachChild(clock);

	return clock_node;
    }

    private Spatial insertTable(Vector3f loc) {
	// Invisible table node everything is attached to
	Node table_node = new Node("Table node");
	table_node.setLocalTranslation(loc);

	// Load actual model and attach it to table node
	Spatial table = assetManager.loadModel("Models/mdl_table_main_v2/mdl_table_main_v2.j3o");
	table_node.attachChild(table);

	return table_node;
    }

    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        room_node.removeFromParent();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    
    
    

}
