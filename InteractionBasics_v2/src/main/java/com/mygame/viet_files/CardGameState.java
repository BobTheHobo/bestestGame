/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/AppState.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
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
import com.jme3.scene.shape.Quad;
import com.jme3.system.NanoTimer;
import com.mygame.PlayerManager;
import com.mygame.jeremiah_files.Board;
import java.util.Random;

/**
 *
 * @author jerem
 */
public class CardGameState extends AbstractAppState {
    
    private SimpleApplication app;
    private  Camera cam;
    private  Node rootNode;
    private AssetManager assetManager;
    private Ray ray = new Ray();
    private static Box card = new Box(1f, 1f, 1f);
    private FlyByCamera flyCam;
    private InputManager inputManager;
    private AppStateManager stateManager;
    private final static Trigger TRIGGER_W = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_A = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_S = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_D = new KeyTrigger(KeyInput.KEY_D);
    private final static Trigger TRIGGER_E = new KeyTrigger(KeyInput.KEY_E);
    private final static Trigger TRIGGER_LEFT_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static Trigger TRIGGER_RIGHT_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
    private final static String MAPPING_FORWARD = "Move Forward";
    private final static String MAPPING_BACK = "Move Backwards";
    private final static String MAPPING_LEFT = "Look Left";
    private final static String MAPPING_RIGHT = "Look Right";
    private final static String MAPPING_LEFT_CLICK = "Selected";
    private final static String MAPPING_RIGHT_CLICK = "Alt Selected";
    private final static String MAPPING_RESET = "Reset";
    private int position = 0;
    private int look = 0;
    private int enemy = 0;
    private int friendly = 0;
    private Random random = new Random();
    private Board board;
    private Vector3f seatedPos;
    private Vector3f enemyPos = new Vector3f(0, 3f,-3f);
    private final Quaternion seatedAng = new Quaternion(-2.6305395E-4f, 0.997723f, -0.06733209f, -0.0038974239f);
    // private final Vector3f boardPos = new Vector3f(0.02f, 5f, 2.8f);
    private Vector3f boardPos;
    private final Quaternion boardAng = new Quaternion(-5.3E-4f, 0.8f, -.6f, -0.0038974239f);
    private Spatial selected = null;
    private PlayerManager playerManager;
    private Table table;
    private BoardEnvironment boardEnv;
    private float calledTime;
    private float dialogueDuration = -1;
    private BitmapText dialogue;
    private Geometry dialogueBox = new Geometry();
    NanoTimer timer = new NanoTimer();
    private int opponentCheck = 0;
    private boolean won = false;
    private boolean lost = false;
    private AudioNode an;

    public CardGameState(PlayerManager playerManager, Table table, BoardEnvironment boardEnv) {
	super();
	this.playerManager = playerManager;
	this.table = table;
	this.boardEnv = boardEnv;
    }
   
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //Initialize fields
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
	this.stateManager = stateManager;
        
        //We don't give free move while sitting
        flyCam = this.app.getFlyByCamera();     
        flyCam.setEnabled(false);
        
        Quaternion rotate90 = new Quaternion();
        rotate90.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0,1,0));
        //tableNode.rotate(rotate90);
               
	Node tableNode = table.getNode();

        board = new Board(tableNode, assetManager, stateManager); //Populates table with game mat
	boardEnv.attachOceanToBoard(board);
	//boardEnv.attachGalleys(board);

        // Set seated and board camera position with respect to table
        //seatedPos = tableNode.getLocalTranslation().add(new Vector3f(0,1.73f,3.8f));
        seatedPos = tableNode.getLocalTranslation().add(new Vector3f(0,1.47f,3.88f));
        boardPos = tableNode.getLocalTranslation().add(new Vector3f(0,3.4f,1.9f));

        //Sets our camera to the position at the chair
        cam.setLocation(seatedPos);
        cam.setRotation(seatedAng);
        
	this.inputManager = app.getInputManager();
        
        inputManager.addMapping(MAPPING_FORWARD, TRIGGER_W);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A);
        inputManager.addMapping(MAPPING_BACK, TRIGGER_S);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D);
        inputManager.addMapping(MAPPING_LEFT_CLICK, TRIGGER_LEFT_CLICK);
        inputManager.addMapping(MAPPING_RIGHT_CLICK, TRIGGER_RIGHT_CLICK);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_E);
        
        inputManager.addListener(actionListener, MAPPING_FORWARD);
        inputManager.addListener(actionListener, MAPPING_BACK);
        inputManager.addListener(actionListener, MAPPING_LEFT);
        inputManager.addListener(actionListener, MAPPING_RIGHT);
        inputManager.addListener(actionListener, MAPPING_LEFT_CLICK);
        inputManager.addListener(actionListener, MAPPING_RIGHT_CLICK);
        inputManager.addListener(actionListener, MAPPING_RESET);
        
        dialogue = new BitmapText( assetManager.loadFont("Interface/Fonts/LucidaCalligraphy.fnt"));
        
       
    }

    public Board getBoard() {
	    return board;
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
				playerManager.setWalkingEnabled(true);
                                position = -1;
                                look = 0;
                                board.hidePlayerHand();
                                board.hideEnemyHand();
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
                                        

                                    } else if (clicked.toString().contains("Skull") && board.getPlayed()) { //Currently nonfunctional
                                        //System.out.println("HAHAHAHA");
                                        //board.jolly(true);s
                                        board.nextRound();
                                        AudioNode an = new AudioNode(assetManager, "Audio/sword.wav");
                                        an.setPositional(false);    
                                        an.play(); 
                                        if (board.isLost()) {
                                            speak(2);
                                        }
                                    }
                                }
                            } else if (position == 0) {// Looking at hand
                                 if (results.size() > 0) { //We cli cked something
                                    Spatial clicked = results.getClosestCollision().getGeometry();
                                    
                                    
                                    if (clicked.toString().contains("Card") && (board.getCard(clicked, board.getPlayerHand()) != null)) { //Raise clicked :)
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
                        case MAPPING_RIGHT_CLICK:
                            //Get what we clicked
                            results = new CollisionResults();
                            click2d = inputManager.getCursorPosition();
                            click3d = cam.getWorldCoordinates(
                                new Vector2f(click2d.getX(), click2d.getY()), 0f);
                            dir = cam.getWorldCoordinates(
                                new Vector2f(click2d.getX(), click2d.getY()), 1f).
                                subtractLocal(click3d);
                            ray = new Ray(click3d, dir);
                            rootNode.collideWith(ray, results);
                                   
                            if (position == 1 || position == 0) {
                                 if (results.size() > 0) { //We clicked something
                                    Spatial clicked = results.getClosestCollision().getGeometry();
                                    if (clicked.toString().contains("Card") && board.getCard(clicked, board.getEnemyHand()) == null && board.getCard(clicked, board.getEnemyDeck()) == null && board.getCard(clicked, board.getPlayerDeck()) == null) { //Print card effects on screen
                                       explain(clicked.toString().substring(4, clicked.toString().indexOf("-")));
                                    }
                                }
                            }
                            break;
                        case MAPPING_RESET:
                            
                            if (position == -1) {// Sits back in chair, currently mapped to 'q'
                                cam.setLocation(seatedPos);
                                cam.setRotation(seatedAng);
                                flyCam.setEnabled(false);
				playerManager.setWalkingEnabled(false);
                                position = 0;
                                look = 0;
                                board.showPlayerHand();
                                board.showEnemyHand();
                            } else if (position == 1) {
                                //board.nextRound();
                            }
                            
                            
                            //speak(1);
                            break;
                    }
                }
            }
       };
    @Override
    public void update(float tpf) {
        if (board.kraken()) {
            won = true;
        }
        
        if (position == 0 || position == 1) {
            board.showEnemyHand();
            board.showPlayerHand();
        }
        if (selected != null) {
            selected.getParent().move(0, 0.05f, 0);                                           
        }
        if (dialogueDuration != -1 && (timer.getTimeInSeconds() >= calledTime + dialogueDuration)) {
            dialogue.removeFromParent();
            dialogueBox.removeFromParent();
            //flyCam.setEnabled(true);
            //playerManager.setWalkingEnabled(true);
            dialogueDuration = -1;
            if (position == 0) {
                cam.setLocation(seatedPos);
                cam.setRotation(seatedAng);
            }
            if (board.isLost()) {
                //System.exit(0);
                lost = true;
                an.stop();
            }
        }
        
        if (opponentCheck == 0  && board.getOpponent() == 2) {
            speak(1);
            opponentCheck = 1;
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
    // Points the camera at the seated opponent and makes them speak
    private void speak(int scene) {
        calledTime = timer.getTimeInSeconds();
        dialogueDuration = getDuration(scene);
        
        if (position == 1) {
            cam.setLocation(seatedPos);
            cam.setRotation(seatedAng);
            position = 0;
        }
          
        cam.lookAt(enemyPos, new Vector3f(0, 1, 0));
        //flyCam.setEnabled(false);
        //playerManager.setWalkingEnabled(false);
        
        dialogue.setText(getSceneText(scene));
        dialogue.setSize(30);
        dialogue.setColor(ColorRGBA.White);
        dialogue.setLocalTranslation((app.getCamera().getWidth() - dialogue.getLineWidth()) / 2, app.getCamera().getHeight() / 4, 0); // position
        
        dialogueBox = new Geometry("Quad", new Quad(dialogue.getLineWidth(), dialogue.getHeight()));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        dialogueBox.setMaterial(mat);
        dialogueBox.setLocalTranslation(dialogue.getLocalTranslation().add(0, -dialogue.getHeight(), 0));
        app.getGuiNode().attachChild(dialogueBox);
        app.getGuiNode().attachChild(dialogue);


        
        an = new AudioNode(assetManager, getSceneAudio(scene));
        an.setPositional(false);    
        an.play();      
    }
    
    private void explain(String card) {
        calledTime = timer.getTimeInSeconds();
        dialogueDuration = 3;
        
        dialogue.removeFromParent();
        dialogueBox.removeFromParent();
        
        switch(card) {
            case "Swashbuckler":
                dialogue.setText("Swashbuckler:\n Basic pirate");
                break;
            case "Cook":
                dialogue.setText("Cook:\n When played gives +1 power to all pirates on this galley");
                break;
            case "Gunner":
                dialogue.setText("Gunner:\n When played gives -3 power to one pirate on opposite galley");
                break;
            case "Cannoneer":
                dialogue.setText("Cannoneer:\n When played gives -1 power to all pirates on opposite galley");
                break;
            case "Lookout":
                dialogue.setText("Lookout:\n When played draw one card");
                break;
            case "Guilt":
                dialogue.setText("Guilt:\n It's unbearable");
                break;
        }
        
        
        
        dialogue.setSize(30);
        dialogue.setColor(ColorRGBA.White);
        dialogue.setLocalTranslation((app.getCamera().getWidth() - dialogue.getLineWidth()) / 2, app.getCamera().getHeight() / 2, 0); // position
        
        
        
        dialogueBox = new Geometry("Quad", new Quad(dialogue.getLineWidth(), dialogue.getHeight()));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Black);
        dialogueBox.setMaterial(mat);
        dialogueBox.setLocalTranslation(dialogue.getLocalTranslation().add(0, -dialogue.getHeight(), 0));
        app.getGuiNode().attachChild(dialogueBox);
        app.getGuiNode().attachChild(dialogue);
        
    }
    
    private String getSceneText(int scene) {
        switch (scene) {
            case 1:
                return "You only delay the inevitable";
            case 2:
                return "A dissapointment.";
            default:
                return "";
                
        }
    }
    
    private String getSceneAudio(int scene) {
        switch (scene) {
            case 1:
            case 2:
                return "Audio/FOGHORN.wav";
            default:
                return "";
                
        }
    }
    
    private float getDuration(int scene) {
        switch (scene) {
            case 1:
                return 8;
            case 2:
                return 5;
            default:
                return 0;
                
        }
    }
    
    public boolean getWon() {
        return won;
    }
    
    public boolean getLost() {
        return lost;
    }
}
