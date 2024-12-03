package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

/**
 * @author viet
 */
public class GameParticles {
    private final Node rootNode;
    private final AssetManager assetManager;

    public GameParticles(Node rootNode, AssetManager assetManager) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
    }
    
    public void addFireParticles(Node targetNode) {
	    ParticleEmitter fire = new ParticleEmitter("Emitter", Type.Triangle, 30);
	    Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
	    mat_red.setTexture("Texture", assetManager.loadTexture("Effects/flame.png"));

	    fire.setMaterial(mat_red);
	    fire.setImagesX(2);
	    fire.setImagesY(2); // 2x2 texture animation
	    fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
	    fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
	    fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0.5f, 0));
	    fire.setParticlesPerSec(100);
	    fire.setStartSize(0.1f);
	    fire.setEndSize(0.05f);
	    fire.setGravity(0, 0, 0);
	    fire.setLowLife(0.2f);
	    fire.setHighLife(0.4f);
	    fire.getParticleInfluencer().setVelocityVariation(0.2f);

	    targetNode.attachChild(fire);

	    fire.setQueueBucket(RenderQueue.Bucket.Translucent);
    }
}
