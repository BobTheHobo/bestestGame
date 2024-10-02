/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/AppState.java to edit this template
 */
package com.mygame.jeremiah_files;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.mygame.PhysicsHelper;
import java.util.Random;

import com.jme3.bullet.BulletAppState;

/**
 *
 * @author jerem
 */
public class CardGameState extends AbstractAppState {
    
    private SimpleApplication app;
    private  Camera cam;
    private  Node rootNode;
    private Node tableNode;
    private AssetManager assetManager;
    private Ray ray = new Ray();
    private static Box card = new Box(1f, 1f, 1f);
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private final static Trigger TRIGGER_W = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_A = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_S = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_D = new KeyTrigger(KeyInput.KEY_D);
    private final static Trigger TRIGGER_Q = new KeyTrigger(KeyInput.KEY_Q);
    private final static Trigger TRIGGER_LEFT_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_FORWARD = "Move Forward";
    private final static String MAPPING_BACK = "Move Backwards";
    private final static String MAPPING_LEFT = "Look Left";
    private final static String MAPPING_RIGHT = "Look Right";
    private final static String MAPPING_LEFT_CLICK = "Selected";
    private final static String MAPPING_RESET = "Reset";
    private int position = 0;
    private int look = 0;
    private int enemy = 0;
    private int friendly = 0;
    private Random random = new Random();
    private Board board;
    private final Vector3f seatedPos = new Vector3f(0.02f, 2.5f, 5.5f);
    private final Quaternion seatedAng = new Quaternion(-2.6305395E-4f, 0.997723f, -0.06733209f, -0.0038974239f);
    private final Vector3f boardPos = new Vector3f(0.02f, 5f, 2.8f);
    private final Quaternion boardAng = new Quaternion(-5.3E-4f, 0.8f, -.6f, -0.0038974239f);
    private Spatial selected = null;
    
    // ADDITIONAL
    private BulletAppState bulletAppState;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        
        super.initialize(stateManager, app); //Initialize fields
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        
        
        
        //Initialize inputs
        inputManager = this.app.getInputManager();
        inputManager.addMapping(MAPPING_FORWARD, TRIGGER_W);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A);
        inputManager.addMapping(MAPPING_BACK, TRIGGER_S);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D);
        inputManager.addMapping(MAPPING_LEFT_CLICK, TRIGGER_LEFT_CLICK);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_Q);
        
        inputManager.addListener(actionListener, MAPPING_FORWARD);
        inputManager.addListener(actionListener, MAPPING_BACK);
        inputManager.addListener(actionListener, MAPPING_LEFT);
        inputManager.addListener(actionListener, MAPPING_RIGHT);
        inputManager.addListener(actionListener, MAPPING_LEFT_CLICK);
        inputManager.addListener(actionListener, MAPPING_RESET);
        
        //We don't give free move while sitting
        flyCam = this.app.getFlyByCamera();     
        flyCam.setEnabled(false);
        
        //Initialize basic scene w/ lighting and moving table
        setUpLight();
        
        tableNode = new Node("table");
        
        Spatial table = assetManager.loadModel("Models/3D Models/Non-Interactable Environmental/Tables/mdl_longSideTable_main_v1.glb");
        Quaternion rotate90 = new Quaternion();
        rotate90.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0));
        table.rotate(rotate90);
                // Set the canBePickedUp flag as user data
        table.setUserData("canBePickedUp", false);

        // Add physics to the geometry
//        PhysicsHelper.addPhysics(table, false, bulletAppState);
        tableNode.attachChild(table);
               
        tableNode.scale(3f);
        tableNode.center();
        
        board = new Board(tableNode, assetManager); //Populates table with game mat
        rootNode.attachChild(tableNode); 
        
        /* Will use later
        Spatial galley1 = rootNode.getChild("Galley1");
        Spatial galley4 = rootNode.getChild("Galley4");
        Vector3f location1 = galley1.getWorldTranslation();
        Vector3f location2 = galley4.getWorldTranslation();
        
        Geometry button = new Geometry("JollyRoger", card);
        Material buttonMat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        buttonMat.setColor("Color", ColorRGBA.Black);
        button.setMaterial(buttonMat);
        button.center();
        button.move(0f, .43f, .55f);
        button.scale(.035f, .001f, .035f);
        tableNode.attachChild(button);
        */
        
        
        
        //Sets our camera to the position at the chair
        cam.setLocation(seatedPos);
        cam.setRotation(seatedAng);
        
            
    }
    
    //All of our button mappings
    private ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf)
            {
                if (!isPressed) {
                    switch (name) {
                        case MAPPING_FORWARD:
                            if (position == 0) {//Sitting in chair
                                
                                cam.setLocation(boardPos);
                                cam.setRotation(boardAng);
                                
                                position = 1;
                                look = 0;
                            }
                            break;
                        case MAPPING_BACK:
                            if (position == 0) {//Sitting in chair                               
                                flyCam.setEnabled(true);
                                position = -1;
                                look = 0;
                                board.hidePlayerHand();
                                selected = null;
                            } else if (position == 1) {// Looking at board
                                cam.setLocation(seatedPos);
                                cam.setRotation(seatedAng);
             
                                position = 0;
                                look = 0;             

                            }
                            break;
                        case MAPPING_LEFT:
                            if (position == 0 && look > -1){//Look left if seated
                                Quaternion left = new Quaternion();
                                left.fromAngleAxis(FastMath.QUARTER_PI / 2, new Vector3f(0,1,0));
                                cam.setRotation(cam.getRotation().mult(left));
                                look -= 1;
                            }
                            break;
                        case MAPPING_RIGHT:
                            if (position == 0 && look < 1) {//Look right if seated
                                Quaternion right = new Quaternion();
                                right.fromAngleAxis(-FastMath.QUARTER_PI / 2, new Vector3f(0,1,0));
                                cam.setRotation(cam.getRotation().mult(right));
                                look += 1;
                            }
                            break;
                        case MAPPING_LEFT_CLICK:
                            //Get what we clicked
                            CollisionResults results = new CollisionResults();
                            Vector2f click2d = inputManager.getCursorPosition();
                            Vector3f click3d = cam.getWorldCoordinates(
                                new Vector2f(click2d.getX(), click2d.getY()), 0f);
                            Vector3f dir = cam.getWorldCoordinates(
                                new Vector2f(click2d.getX(), click2d.getY()), 1f).
                                subtractLocal(click3d);
                            ray = new Ray(click3d, dir);
                            rootNode.collideWith(ray, results);
                                
                            if (position == 1) {// Looking at game state
                                if (results.size() > 0) { //We clicked something
                                    Spatial clicked = results.getClosestCollision().getGeometry();
                                    if ((clicked.toString().contains("Slot3") || //Clicked a friendly slot
                                        clicked.toString().contains("Slot4") ||
                                        clicked.toString().contains("Slot5")) &&
                                        selected != null) {// We have a card selected from our hand

                                        board.play(selected, clicked);
                                        selected = null;

                                    } else if (clicked.toString().contains("Roger")) { //Currently nonfunctional
                                        
                                    }
                                }
                            } else if (position == 0) {// Looking at hand
                                 if (results.size() > 0) { //We clicked something
                                    Spatial clicked = results.getClosestCollision().getGeometry();
                                    if (clicked.toString().contains("Card")) { //Raise clicked :)
                                        if (selected != null) {//Lower previously selected card
                                            selected.getParent().move(0, -0.05f, 0);                                           
                                        }
                                        if (clicked == selected) {//We are unselecting
                                            selected = null;
                                        } else {//Selecting new card
                                            selected = clicked;
                                            selected.getParent().move(0, 0.05f, 0);
                                        }
                                        
                                        
                                    }
                                }
                            }
                            break;
                        case MAPPING_RESET:
                            if (position == -1) {// Sits back in chair, currently mapped to 'q'
                                cam.setLocation(seatedPos);
                                cam.setRotation(seatedAng);
                                flyCam.setEnabled(false);
                                position = 0;
                                look = 0;
                                board.showPlayerHand();
                            } else if (position == 1) {
                                board.nextRound();
                            }
                            break;
                    }
                }
            }
       };
    
    public int getPositiion() {
        return position;
    }

    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    
    @Override
    public void cleanup() {
        super.cleanup();
        tableNode.removeFromParent();
    }
    
    private void setUpLight() {
           // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
}
