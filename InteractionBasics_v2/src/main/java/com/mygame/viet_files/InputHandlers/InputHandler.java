/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/AppState.java to edit this template
 */
package com.mygame.viet_files.InputHandlers;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.mygame.jeremiah_files.Board;
import com.mygame.viet_files.GameShadows;
import com.mygame.viet_files.TestUIManager;
import java.util.Random;

/**
 *
 * @author jerem, viet
 */
public class InputHandler implements ActionListener {
    
    private SimpleApplication app;
    private  Camera cam;
    private  Node rootNode;
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
    private Vector3f seatedPos;
    private final Quaternion seatedAng = new Quaternion(-2.6305395E-4f, 0.997723f, -0.06733209f, -0.0038974239f);
    // private final Vector3f boardPos = new Vector3f(0.02f, 5f, 2.8f);
    private Vector3f boardPos;
    private final Quaternion boardAng = new Quaternion(-5.3E-4f, 0.8f, -.6f, -0.0038974239f);
    private Spatial selected = null;
    private BitmapText shadowStabilizationText;
    private BitmapText shadowZfarText;

    private GameShadows shadows;
    private DirectionalLightShadowRenderer dlsr;
    private DirectionalLightShadowFilter dlsf;

    private Node guiNode;
    
    private ViewPort viewPort;

    private BitmapFont guiFont;

    private TestUIManager ui;

   
    
    public InputHandler(Application app, InputManager inputManager, GameShadows shadows) {
	this.inputManager = inputManager;
	this.shadows = shadows;
	this.dlsr = this.shadows.getDLSR();
	this.dlsf = this.shadows.getDLSF();

        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
	this.guiNode = this.app.getGuiNode();
	this.viewPort = this.app.getViewPort();

        //We don't give free move while sitting
        flyCam = this.app.getFlyByCamera();     
        flyCam.setEnabled(false);

        guiFont = assetManager.loadFont("Interface/Fonts/LucidaCalligraphy.fnt");

        ui = new TestUIManager(assetManager, shadows, guiNode, inputManager, viewPort);

	initInputs();
    }

    private void addMappings() {
	// Game inputs
        inputManager.addMapping(MAPPING_FORWARD, TRIGGER_W);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A);
        inputManager.addMapping(MAPPING_BACK, TRIGGER_S);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D);
        inputManager.addMapping(MAPPING_LEFT_CLICK, TRIGGER_LEFT_CLICK);
        inputManager.addMapping(MAPPING_RESET, TRIGGER_Q);
        

	// Shadow testing inputs
        inputManager.addMapping("lambdaUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("lambdaDown", new KeyTrigger(KeyInput.KEY_J));

        inputManager.addMapping("debug", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("stabilize", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("distance", new KeyTrigger(KeyInput.KEY_N));

        inputManager.addMapping("backShadows", new KeyTrigger(KeyInput.KEY_K));
    }

    private void addUIText() {

	// Display shadow stabilization instructions
        shadowStabilizationText = new BitmapText(guiFont);
        shadowStabilizationText.setSize(guiFont.getCharSet().getRenderedSize() * 0.75f);
        shadowStabilizationText.setText("(b:on/off) Shadow stabilization : " + dlsr.isEnabledStabilization());
        shadowStabilizationText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 100, 0);
        guiNode.attachChild(shadowStabilizationText);


	// Display shadow extend + fading instructions
        shadowZfarText = new BitmapText(guiFont);
        shadowZfarText.setSize(guiFont.getCharSet().getRenderedSize() * 0.75f);
        shadowZfarText.setText("(n:on/off) Shadow extend to 500 and fade to 50 : " + (dlsr.getShadowZExtend() > 0));
        shadowZfarText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 120, 0);
        guiNode.attachChild(shadowZfarText);
    }

    private void initInputs() {
        //Initialize inputs

	addMappings();

	addUIText();

        inputManager.addListener(this, 
		"lambdaUp", 
		"lambdaDown",
                "debug",
		"stabilize",
		"distance",
		"ShadowUp",
		"ShadowDown",
		"backShadows"
	);
    }
    

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {

        if (name.equals("lambdaUp") && keyPressed) {
            dlsr.setLambda(dlsr.getLambda() + 0.01f);
            dlsf.setLambda(dlsr.getLambda() + 0.01f);
            System.out.println("Lambda : " + dlsr.getLambda());
        } else if (name.equals("lambdaDown") && keyPressed) {
            dlsr.setLambda(dlsr.getLambda() - 0.01f);
            dlsf.setLambda(dlsr.getLambda() - 0.01f);
            System.out.println("Lambda : " + dlsr.getLambda());
        }


        if (name.equals("ShadowUp") && keyPressed) {
	    shadows.setShadowIntensity(shadows.getShadowIntensity() + 0.05f);
	    ui.updateUI();
        }
        if (name.equals("ShadowDown") && keyPressed) {
	    shadows.setShadowIntensity(shadows.getShadowIntensity() - 0.05f);
	    ui.updateUI();
	}

        if (name.equals("debug") && keyPressed) {
            dlsr.displayFrustum();
        }

        if (name.equals("backShadows") && keyPressed) {
            dlsr.setRenderBackFacesShadows(!dlsr.isRenderBackFacesShadows());
            dlsf.setRenderBackFacesShadows(!dlsf.isRenderBackFacesShadows());
        }

        if (name.equals("stabilize") && keyPressed) {
            dlsr.setEnabledStabilization(!dlsr.isEnabledStabilization());
            dlsf.setEnabledStabilization(!dlsf.isEnabledStabilization());
            shadowStabilizationText.setText("(b:on/off) Shadow stabilization : " + dlsr.isEnabledStabilization());
        }
        if (name.equals("distance") && keyPressed) {
            if (dlsr.getShadowZExtend() > 0) {
                dlsr.setShadowZExtend(0);
                dlsr.setShadowZFadeLength(0);
                dlsf.setShadowZExtend(0);
                dlsf.setShadowZFadeLength(0);

            } else {
                dlsr.setShadowZExtend(500);
                dlsr.setShadowZFadeLength(50);
                dlsf.setShadowZExtend(500);
                dlsf.setShadowZFadeLength(50);
            }
            shadowZfarText.setText("(n:on/off) Shadow extend to 500 and fade to 50 : " + (dlsr.getShadowZExtend() > 0));

        }
    }
}
