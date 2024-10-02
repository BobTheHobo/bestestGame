package com.mygame;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * @author viet
 */
public class GameLighting {
    private final Node rootNode;
    private final AssetManager assetManager;

    public GameLighting(Node rootNode, AssetManager assetManager) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
    }

    public void setupLighting() {
	//Directional light (basically sun) points in one direction from infinitely far away
	// DirectionalLight sun = new DirectionalLight();
	// sun.setDirection((new Vector3f(-0.5f, -0.3f, -1.5f)));
	// sun.setColor(ColorRGBA.White.mult(0.5f));
	// rootNode.addLight(sun);

	//Small pointlight just arbitrarily to light up scene
	PointLight pl = new PointLight();
	pl.setColor(ColorRGBA.White.mult(0.4f)); //Adjust mult value to increase/decrease brightness
	pl.setRadius(18f);
	Vector3f pl_vec = new Vector3f(0.0f, 7f, 0f);
	pl.setPosition(pl_vec);
	rootNode.addLight(pl);

	rootNode.attachChild(insertBlock(pl_vec));

	// AmbientLight doesn't work because we don't have any materials yet
	// AmbientLight al = new AmbientLight();
	// al.setColor(ColorRGBA.White.mult(1.3f));
	// rootNode.addLight(al);
    }

    // Creates a simple blue box for testing and whatnot
    private Geometry insertBlock(Vector3f vec) {
	Box b = new Box(0.1f,0.1f,0.1f); // create cube shape
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        geom.setMaterial(mat);  // set the cube's material
	geom.setLocalTranslation(vec);
	return geom;
    }

}
