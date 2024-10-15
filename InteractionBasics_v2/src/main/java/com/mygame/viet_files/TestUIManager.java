package com.mygame.viet_files;

/*
 * Copyright (c) 2009-2021 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.AbstractShadowFilter;
import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.EdgeFilteringMode;

/**
 *
 * @author Nehon, Viet
 */
public class TestUIManager implements ActionListener {

    public BitmapText shadowTypeText;
    public BitmapText shadowCompareText;
    public BitmapText shadowFilterText;
    public BitmapText shadowIntensityText;
    private final static String TYPE_TEXT = "(Space) Shadow type : ";
    private final static String COMPARE_TEXT = "(enter) Shadow compare ";
    private final static String FILTERING_TEXT = "(f) Edge filtering : ";
    private final static String INTENSITY_TEXT = "(t:up, g:down) Shadow intensity : ";
    private boolean hardwareShadows = true;
    final private ViewPort viewPort;
    private int filteringIndex = 0;
    private int renderModeIndex = 0;
    GameShadows shadows;
    private final InputManager inputManager;
    private final AssetManager assetManager;
    private final Node guiNode;
    

    public TestUIManager(AssetManager assetManager, GameShadows shadows, 
            Node guiNode, InputManager inputManager, ViewPort viewPort) {
	this.assetManager = assetManager;
	this.shadows = shadows;
	this.guiNode = guiNode;
	this.inputManager = inputManager;
        this.viewPort = viewPort;

	initUI();

	addMappings();
    }
    
    private void addMappings() {
        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("changeFiltering", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("ShadowUp", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("ShadowDown", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("ThicknessUp", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("ThicknessDown", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("toggleHW", new KeyTrigger(KeyInput.KEY_RETURN));

        inputManager.addListener(this, "toggleHW", "toggle", "ShadowUp", "ShadowDown", "ThicknessUp", "ThicknessDown", "changeFiltering");
    }

    public void updateUI() {
        shadowTypeText.setText(TYPE_TEXT + "Processor");
        shadowCompareText.setText(COMPARE_TEXT + (hardwareShadows ? "Hardware" : "Software"));
        shadowFilterText.setText(FILTERING_TEXT + shadows.getEdgeFilteringMode());
        shadowIntensityText.setText(INTENSITY_TEXT + shadows.getShadowIntensity());
    }

    private void initUI() {
	// set up font
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/LucidaCalligraphy.fnt");

        shadowTypeText = createText(guiFont);
        shadowCompareText = createText(guiFont);
        shadowFilterText = createText(guiFont);
        shadowIntensityText = createText(guiFont);

        shadowTypeText.setText(TYPE_TEXT + "Processor");
        shadowCompareText.setText(COMPARE_TEXT + (hardwareShadows ? "Hardware" : "Software"));
        shadowFilterText.setText(FILTERING_TEXT + shadows.getEdgeFilteringMode());
        shadowIntensityText.setText(INTENSITY_TEXT + shadows.getShadowIntensity());

        shadowTypeText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 20, 0);
        shadowCompareText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 40, 0);
        shadowFilterText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 60, 0);
        shadowIntensityText.setLocalTranslation(10, viewPort.getCamera().getHeight() - 80, 0);

        guiNode.attachChild(shadowTypeText);
        guiNode.attachChild(shadowCompareText);
        guiNode.attachChild(shadowFilterText);
        guiNode.attachChild(shadowIntensityText);
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (name.equals("toggle") && keyPressed) {
            renderModeIndex += 1;
            renderModeIndex %= 3;

            switch (renderModeIndex) {
                case 0:
		    shadows.toggleShadows(true);
                    shadowTypeText.setText(TYPE_TEXT + "Renderer");
		    shadows.switchShadowSimMethod(false);
                    break;
                case 1:
		    shadows.toggleShadows(true);
                    shadowTypeText.setText(TYPE_TEXT + "Filter");
		    shadows.switchShadowSimMethod(true);
                    break;
                case 2:
                    shadowTypeText.setText(TYPE_TEXT + "None");
		    shadows.toggleShadows(false);
                    break;
            }

        } else if (name.equals("toggleHW") && keyPressed) {
            hardwareShadows = !hardwareShadows;
	    shadows.setShadowCompareMode(hardwareShadows);
            shadowCompareText.setText(COMPARE_TEXT + (hardwareShadows ? "Hardware" : "Software"));
        }


        if (name.equals("changeFiltering") && keyPressed) {
	    shadows.nextEdgeFilteringMode();
            shadowFilterText.setText(FILTERING_TEXT + shadows.getEdgeFilteringMode().toString());
        }


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

    }

    private BitmapText createText(BitmapFont guiFont) {
        BitmapText t = new BitmapText(guiFont);
        t.setSize(guiFont.getCharSet().getRenderedSize() * 0.75f);
        return t;
    }
}
