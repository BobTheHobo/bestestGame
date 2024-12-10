package com.mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import com.jme3.scene.Node;

/*
* author: shawn
*/
public class CrosshairManager {

    private final Geometry crosshairGeometry;
    private final Node guiNode;
    private boolean isVisible = true;

    public CrosshairManager(AssetManager assetManager, AppSettings settings, Node guiNode) {
        this.guiNode = guiNode;

        // Create the circle mesh
        int samples = 32; // Number of segments for smoothness
        float radius = 5f; // Radius in pixels

        CircleMesh circleMesh = new CircleMesh(samples, radius);

        // Create geometry with the circle mesh
        crosshairGeometry = new Geometry("Crosshair", circleMesh);

        // Create an unshaded material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // Enable transparency
        crosshairGeometry.setMaterial(mat);

        // Position the crosshair at the center of the screen
        float x = settings.getWidth() / 2f;
        float y = settings.getHeight() / 2f;
        crosshairGeometry.setLocalTranslation(x, y, 0);

        // Set the crosshair to render in the GUI bucket
        crosshairGeometry.setQueueBucket(RenderQueue.Bucket.Gui);
        crosshairGeometry.setCullHint(CullHint.Never);

        // Attach to the GUI node
        guiNode.attachChild(crosshairGeometry);
    }

    public void showCrosshair() {
        if (!isVisible) {
            crosshairGeometry.setCullHint(CullHint.Never);
            isVisible = true;
            guiNode.attachChild(crosshairGeometry);
        }
    }

    public void hideCrosshair() {
        if (isVisible) {
            crosshairGeometry.setCullHint(CullHint.Always);
            isVisible = false;
            guiNode.detachChild(crosshairGeometry);
        }
    }

    public boolean isCrosshairVisible() {
        return isVisible;
    }
}
