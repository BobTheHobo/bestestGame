package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * @author viet
 */
public class GameLighting {
    private final Node rootNode;
    private final AssetManager assetManager;
    private final GameShadows shadows;

    public GameLighting(Node rootNode, AssetManager assetManager, GameShadows gameShadows) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
	this.shadows = gameShadows;
    }

    public void setupLighting() {
	// Directional light (basically sun) points in one direction from infinitely far away
	DirectionalLight sun = new DirectionalLight();
	sun.setDirection((new Vector3f(-0.5f, -0.3f, -1.5f)));
	sun.setColor(ColorRGBA.White.mult(0.0f));
	rootNode.addLight(sun);
	shadows.attachDirectionalLight(sun);

	// Small pointlight just arbitrarily to light up scene
	PointLight pl = new PointLight();
	pl.setColor(ColorRGBA.White.mult(0.4f)); //Adjust mult value to increase/decrease brightness
	pl.setRadius(18f);
	Vector3f pl_vec = new Vector3f(0.0f, 7f, 0f);
	pl.setPosition(pl_vec);
	rootNode.addLight(pl);
	shadows.attachPointLight(pl);

	rootNode.attachChild(Util.insertBlock(this.assetManager, pl_vec));

	// AmbientLight doesn't work because we don't have any materials yet
	// AmbientLight al = new AmbientLight();
	// al.setColor(ColorRGBA.White.mult(1.3f));
	// rootNode.addLight(al);
    }
}
