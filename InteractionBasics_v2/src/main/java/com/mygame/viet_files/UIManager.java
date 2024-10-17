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
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 *
 * @author Nehon, Viet
 */
public class UIManager {

    public BitmapText shadowTypeText;
    public BitmapText shadowCompareText;
    public BitmapText ssaoText;
    public BitmapText shadowFilterText;
    public BitmapText shadowIntensityText;
    public BitmapText shadowStabilizationText;
    public BitmapText shadowZfarText;

    private final static String TYPE_TEXT = "(Space) Shadow type : ";
    private final static String COMPARE_TEXT = "(enter) Shadow compare ";
    private final static String SSAO_TEXT = "(o) Toggle Ambient Occlusion : ";
    private final static String FILTERING_TEXT = "(f) Edge filtering : ";
    private final static String INTENSITY_TEXT = "(t:up, g:down) Shadow intensity : ";
    private final static String STABILIZATION_TEXT = "(b:on/off) Shadow stabilization : ";
    private final static String EXTEND_TEXT = "(n:on/off) Shadow extend to 500 and fade to 50 : ";

    private final AssetManager assetManager;
    private final GameShadows shadows;
    private final Node guiNode;
    final private ViewPort viewPort;

    public UIManager(AssetManager assetManager, GameShadows shadows, 
            Node guiNode, ViewPort viewPort) {
	this.assetManager = assetManager;
	this.shadows = shadows;
	this.guiNode = guiNode;
        this.viewPort = viewPort;

	initUI();
    }

    public void updateUI() {
        shadowCompareText.setText(COMPARE_TEXT + 
		(shadows.getUseHWShadows() ? "Hardware" : "Software"));
        shadowFilterText.setText(FILTERING_TEXT + 
		shadows.getEdgeFilteringMode());
	if (!shadows.getUseFilter()) {
		ssaoText.setText(SSAO_TEXT + "Cannot use SSAO with render processing");
	} else {
		ssaoText.setText(SSAO_TEXT + shadows.getSSAO());
	}
        shadowIntensityText.setText(INTENSITY_TEXT + 
		shadows.getShadowIntensity());
	shadowStabilizationText.setText(STABILIZATION_TEXT +
		shadows.getShadowStabilization());
	shadowZfarText.setText(EXTEND_TEXT +
		(shadows.getDLSR().getShadowZExtend() > 0));
    }

    public void updateTypeText(String mode) {
	    shadowTypeText.setText(TYPE_TEXT + mode);
    }

    private void initUI() {
	// set up font
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/LucidaCalligraphy.fnt");

        shadowTypeText = createText(guiFont);
        shadowCompareText = createText(guiFont);
	ssaoText = createText(guiFont);
        shadowFilterText = createText(guiFont);
        shadowIntensityText = createText(guiFont);
	shadowStabilizationText = createText(guiFont);
	shadowZfarText = createText(guiFont);

	BitmapText[] texts = {shadowTypeText, shadowCompareText, ssaoText, shadowFilterText, shadowIntensityText, shadowStabilizationText, shadowZfarText};

	updateUI();
	updateTypeText("rendering");

	// Layout text vertically
	layoutTexts(texts);
    }

    private void layoutTexts(BitmapText[] texts) {
	int offset = 20;
	int cameraHeight = viewPort.getCamera().getHeight();
	// Shift each next text by 20 down
	for (BitmapText text : texts) {
	    text.setLocalTranslation(10, cameraHeight - offset, 0);
	    guiNode.attachChild(text);
	    offset += 20;
    	}
    }

    private BitmapText createText(BitmapFont guiFont) {
        BitmapText t = new BitmapText(guiFont);
        t.setSize(guiFont.getCharSet().getRenderedSize() * 0.75f);
        return t;
    }
}
