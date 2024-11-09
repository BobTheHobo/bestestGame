package com.mygame.viet_files;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Contains general utility functions 
 * @author viet
 */
public class Util {

    // Creates a simple blue box for testing and whatnot
	// params: assetManager; local translation vector where you want block placed
	// returns: Small blue cube geometry
    public static Geometry insertBlock(AssetManager assetManager, Vector3f vec) {
	Box b = new Box(0.1f,0.1f,0.1f); // create cube shape
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        geom.setMaterial(mat);  // set the cube's material
	geom.setLocalTranslation(vec);
	return geom;
    }

    // Overloaded method to allow specification of size
	// params: assetManager; local translation vector where you want block placed;
   	//	size of cube
	// returns: Small blue cube geometry
    public static Geometry insertBlock(AssetManager assetManager, Vector3f vec, float size) {
	Box b = new Box(size, size, size); // create cube shape
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        geom.setMaterial(mat);  // set the cube's material
	geom.setLocalTranslation(vec);
	return geom;
    }

    public static void printAnimationNames(AnimComposer control) {
	    System.out.println("Animations for " + control.getSpatial().getName() + ":");
	    for (String anim : control.getAnimClipsNames()) {
		    System.out.println("   " + anim);
	    }
    }

    // Prints all children of a spatial
    public static void printChildren(Spatial s) {
	    System.out.println("Children of:");
	    recurseChildren(s);
    }

    private static void recurseChildren(Spatial s) {
	    System.out.println("  " + s.getName());
	    // If geometry (which has no children) or node w/ no children
	    if (s instanceof Geometry || ((Node)s).getChildren().isEmpty()) {
		return;
	    }

	    for (Spatial child : ((Node)s).getChildren()) {
		    System.out.print("   Child of " + s.getName() + ":");
		    recurseChildren(child);
	    }
    }
}