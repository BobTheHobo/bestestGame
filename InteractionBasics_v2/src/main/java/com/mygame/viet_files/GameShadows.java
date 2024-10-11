package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;

/**
 * Contains functions that help with assigning shadows to objects
 * @author viet
 */
public class GameShadows {
    private final Node rootNode;
    private final AssetManager assetManager;
    private final ViewPort viewPort;

    // If filtering is used for shadow simulation (rendering will be used else)
    // Rendering is more precise but more intensive, filtering is less accurate but faster
    boolean useFilter = false;

    // Size of rendered shadow maps, in pixels per side (512, 1024, 2048, etc)
    int shadowmap_size = 1024;

    // The number of shadow maps rendered(more shadow maps = better quality, but slower).
    // Only affects shadow renderers, not filters
    int shadowmaps = 3;

    // Defines how shadows are filtered
    EdgeFilteringMode edgeFiltering = EdgeFilteringMode.PCFPOISSON;

    FilterPostProcessor fpp;

    DirectionalLightShadowRenderer dlsr;
    DirectionalLightShadowFilter dlsf; 

    PointLightShadowRenderer plsr;
    PointLightShadowFilter plsf; 
	
    SpotLightShadowRenderer slsr;
    SpotLightShadowFilter slsf;

    public GameShadows(Node rootNode, AssetManager assetManager, ViewPort viewPort) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
	this.viewPort = viewPort;
    }

    public void setupShadowHandlers() {
	// Keep this on top because other handlers rely on it
	setupFilterPostProcessor();

	setupDirectionalLightHandlers();
	setupPointLightHandlers();
	//setupSpotLightHandlers();

	switchShadowSimMethod(useFilter);
    }


    // Handlers for directional light shadows
    private void setupDirectionalLightHandlers() {
	dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmap_size, shadowmaps);
        dlsr.setEdgeFilteringMode(edgeFiltering);

	dlsf = new DirectionalLightShadowFilter(assetManager, shadowmap_size, shadowmaps);
        dlsf.setEdgeFilteringMode(edgeFiltering);
    	dlsf.setEnabled(true); // Always true, handle filtering through adding/removing fpp
	fpp.addFilter(dlsf);
    }

    // Handlers for point light shadows
    private void setupPointLightHandlers() {
	plsr = new PointLightShadowRenderer(assetManager, shadowmap_size);
        plsr.setEdgeFilteringMode(edgeFiltering);

	plsf = new PointLightShadowFilter(assetManager, shadowmap_size);
        plsf.setEdgeFilteringMode(edgeFiltering);
    	plsf.setEnabled(true); // Always true, handle filtering through adding/removing fpp
	fpp.addFilter(plsf);
    }

    // Handlers for spot light shadows
    private void setupSpotLightHandlers() {
        slsr = new SpotLightShadowRenderer(assetManager, shadowmap_size);
        slsr.setEdgeFilteringMode(edgeFiltering);

        slsf = new SpotLightShadowFilter(assetManager, shadowmap_size);
        slsf.setEdgeFilteringMode(edgeFiltering);
    	slsf.setEnabled(true); // Always true, handle filtering through adding/removing fpp
	fpp.addFilter(slsf);
    }

    private void setupFilterPostProcessor() {
	this.fpp = new FilterPostProcessor(assetManager);
    }

    // Toggles between filtering and rendering
    public void switchShadowSimMethod() {
	if (useFilter) {
		useFilter = false;
		addRenderProcessors();
		viewPort.removeProcessor(fpp);
	} else {
		useFilter = true;
		removeRenderProcessors();
		viewPort.addProcessor(fpp);
	}
    }

    // Overloaded method to allow you to specify whether or not filtering is used
    public void switchShadowSimMethod(boolean useFiltering) {
	useFilter = useFiltering;
	if (!useFiltering) {
		addRenderProcessors();
		viewPort.removeProcessor(fpp);
	} else {
		removeRenderProcessors();
		viewPort.addProcessor(fpp);
	}
    }

    private void addRenderProcessors() {
	//viewPort.addProcessor(slsr);
	viewPort.addProcessor(plsr);
	viewPort.addProcessor(dlsr);
    }

    private void removeRenderProcessors() {
	//viewPort.removeProcessor(slsr);
	viewPort.removeProcessor(plsr);
	viewPort.removeProcessor(dlsr);
    }

    // Change shadowmap size
    public void changeShadowmapSize(int size) {
	shadowmap_size = size;
	//TODO: do i need to reupdate shadow renderers?	
    }

    // Change number of shadow maps rendered
    public void changeShadowmaps(int num) {
	shadowmaps = num;
	//TODO: do i need to reupdate shadow renderers?	
    }

    // Use when you want an object to both cast shadows and display shadows casted on it
    public void attachShadowCastAndReceive(Spatial obj) {
	obj.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    // Use when you want an object to only display shadows casted on it
    public void attachShadowReceive(Spatial obj) {
	obj.setShadowMode(RenderQueue.ShadowMode.Receive);
    }

    // Use when you want an object to only cast shadows
    public void attachShadowCast(Spatial obj) {
	obj.setShadowMode(RenderQueue.ShadowMode.Cast);
    }

    // Neither receive nor cast shadows
    public void attachShadowOff(Spatial obj) {
	obj.setShadowMode(RenderQueue.ShadowMode.Off);
    }

    // Add directional light
    public void attachDirectionalLight(DirectionalLight light) {
	    dlsr.setLight(light);
	    dlsf.setLight(light);
    }

    // Add point light
    public void attachPointLight(PointLight light) {
	    plsr.setLight(light);
	    plsf.setLight(light);
    }

    // Add spot light
    public void attachSpotLight(SpotLight light) {
	    slsr.setLight(light);
	    slsf.setLight(light);
    }
}
