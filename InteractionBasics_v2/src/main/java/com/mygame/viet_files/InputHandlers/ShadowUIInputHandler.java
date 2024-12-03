/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/AppState.java to edit this template
 */
package com.mygame.viet_files.InputHandlers;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.mygame.viet_files.GameShadows;
import com.mygame.viet_files.UIManager;

/**
 *
 * @author viet
 */
public class ShadowUIInputHandler implements ActionListener {
    
    private SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;

    private FlyByCamera flyCam;
    private InputManager inputManager;

    private GameShadows shadows;
    //private DirectionalLightShadowFilter dlsf;

    private UIManager ui;

    private int renderModeIndex = 0; 

    
    public ShadowUIInputHandler(Application app, InputManager inputManager, GameShadows shadows, UIManager ui) {
	this.inputManager = inputManager;
	this.shadows = shadows;
	//this.dlsf = this.shadows.getDLSF();
 	this.ui = ui; 

        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();

        //We don't give free move while sitting
        flyCam = this.app.getFlyByCamera();     
        flyCam.setEnabled(false);

	initInputs();
    }

    private void addMappings() {
	// Displayed by UIManager
        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addMapping("toggleHW", new KeyTrigger(KeyInput.KEY_RETURN));

	inputManager.addMapping("toggleSSAO", new KeyTrigger(KeyInput.KEY_O));

        inputManager.addMapping("changeFiltering", new KeyTrigger(KeyInput.KEY_F));

        inputManager.addMapping("ShadowUp", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("ShadowDown", new KeyTrigger(KeyInput.KEY_G));

        inputManager.addMapping("stabilize", new KeyTrigger(KeyInput.KEY_B));

        inputManager.addMapping("distance", new KeyTrigger(KeyInput.KEY_N));

	// Not displayed
        //inputManager.addMapping("lambdaUp", new KeyTrigger(KeyInput.KEY_U));
        //inputManager.addMapping("lambdaDown", new KeyTrigger(KeyInput.KEY_J));

        //inputManager.addMapping("debug", new KeyTrigger(KeyInput.KEY_X));

        //inputManager.addMapping("backShadows", new KeyTrigger(KeyInput.KEY_K));

        //inputManager.addMapping("ThicknessUp", new KeyTrigger(KeyInput.KEY_Y));
        //inputManager.addMapping("ThicknessDown", new KeyTrigger(KeyInput.KEY_H));
    }

    private void initInputs() {
        //Initialize inputs
	addMappings();

        inputManager.addListener(this, 
		"lambdaUp", 
		"lambdaDown",
                "debug",
		"stabilize",
		"distance",
		"ShadowUp",
		"ShadowDown",
		"backShadows",
		"toggleHW", 
		"toggle",
		"ShadowUp",
		"ShadowDown",
		"ThicknessUp",
		"ThicknessDown",
		"changeFiltering",
		"toggleSSAO"
	);
    }
    

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
	
	// update ui on action
	ui.updateUI();

	if (name.equals("toggleSSAO") && keyPressed) {
		shadows.toggleSSAO();
	}

        //if (name.equals("lambdaUp") && keyPressed) {
        //    dlsf.setLambda(dlsf.getLambda() + 0.01f);
        //    System.out.println("Lambda : " + dlsf.getLambda());
        //} else if (name.equals("lambdaDown") && keyPressed) {
        //    dlsf.setLambda(dlsf.getLambda() - 0.01f);
        //    System.out.println("Lambda : " + dlsf.getLambda());
        //}

        if (name.equals("ShadowUp") && keyPressed) {
	    shadows.setShadowIntensity(shadows.getShadowIntensity() + 0.05f);
        }
        if (name.equals("ShadowDown") && keyPressed) {
	    shadows.setShadowIntensity(shadows.getShadowIntensity() - 0.05f);
	}

        if (name.equals("debug") && keyPressed) {
		System.out.println("Renderer not used anymore, debug not available");
        }

        //if (name.equals("backShadows") && keyPressed) {
        //    dlsf.setRenderBackFacesShadows(!dlsf.isRenderBackFacesShadows());
        //}

 //       if (name.equals("stabilize") && keyPressed) {
        //    dlsf.setEnabledStabilization(!dlsf.isEnabledStabilization());
  //      }

 //       if (name.equals("distance") && keyPressed) {
        //    if (dlsf.getShadowZExtend() > 0) {
        //        dlsf.setShadowZExtend(0);
        //        dlsf.setShadowZFadeLength(0);
//
        //    } else {
        //        dlsf.setShadowZExtend(500);
        //        dlsf.setShadowZFadeLength(50);
 //           }
 //       }

        if (name.equals("ThicknessUp") && keyPressed) {
	    int thickness = shadows.getShadowEdgeThickness();
	    shadows.setShadowEdgeThickness(thickness + 1);
            System.out.println("Shadow thickness : " + shadows.getShadowEdgeThickness());
        }
        if (name.equals("ThicknessDown") && keyPressed) {
	    int thickness = shadows.getShadowEdgeThickness();
	    shadows.setShadowEdgeThickness(thickness - 1);
            System.out.println("Shadow thickness : " + shadows.getShadowEdgeThickness());
        }


        if (name.equals("changeFiltering") && keyPressed) {
	    shadows.nextEdgeFilteringMode();
        }

        if (name.equals("toggle") && keyPressed) {
            renderModeIndex += 1;
            renderModeIndex %= 3;

            switch (renderModeIndex) {
                case 0:
			System.out.println("case 0");
		    shadows.toggleShadows(true);
		    shadows.switchShadowSimMethod(false);
		    ui.updateTypeText("Render");
                    break;
                case 1:
			System.out.println("case 1");
		    shadows.toggleShadows(true);
		    shadows.switchShadowSimMethod(true);
		    ui.updateTypeText("Filter");
                    break;
                case 2:
			System.out.println("case 2");
		    shadows.toggleShadows(false);
		    ui.updateTypeText("None");
                    break;
            }
        } else if (name.equals("toggleHW") && keyPressed) {
	    shadows.toggleShadowCompareMode();
        }
    }
}
