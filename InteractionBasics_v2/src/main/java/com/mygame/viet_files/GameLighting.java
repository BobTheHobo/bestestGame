package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import static com.jme3.math.ColorRGBA.fromRGBA255;
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
	//insertDL(new Vector3f(-0.5f, -0.3f, -1.5f), 1.0f);
	//insertPL(new Vestor3f(0.0f, 7f, 0f));
	insertMoonlight();
	//insertAL(0.1f);
    }

    public void insertAL (float mult) {
	AmbientLight ambient = new AmbientLight();

	// Dark bluish ambient light
	ambient.setColor(fromRGBA255(6,13,35,255).mult(mult));  
	rootNode.addLight(ambient);
    }

    public void insertMoonlight() {
	DirectionalLight moonlight = new DirectionalLight();
	// Soft bluish color for moonlight
	moonlight.setColor(fromRGBA255(6,13,35,255).mult(0.2f));  

	// Direction for long shadows
	moonlight.setDirection(new Vector3f(-0.5f, -1.0f, -0.5f).normalizeLocal());  

	shadows.attachDirectionalLight(moonlight);

	rootNode.addLight(moonlight);
    }

    // Directional light (basically sun) points in one direction from infinitely far away
    public void insertDL(Vector3f dir, float mult) {
	DirectionalLight sun = new DirectionalLight();
	sun.setDirection(dir);
	sun.setColor(ColorRGBA.White.mult(mult));
	shadows.attachDirectionalLight(sun);

	rootNode.addLight(sun);
    }

    // Small pointlight just arbitrarily to light up scene
    public void insertPL(Vector3f vec) {
	PointLight pl = new PointLight();
	pl.setColor(ColorRGBA.White.mult(0.4f)); //Adjust mult value to increase/decrease brightness
	pl.setRadius(18f);
	pl.setPosition(vec);
	rootNode.addLight(pl);
	shadows.attachPointLight(pl);

	rootNode.attachChild(Util.insertBlock(this.assetManager, vec));
    }
}
