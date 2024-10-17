package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.CompareMode;
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

    // Whether or not shadows are on
    private boolean shadowsOn = true;

    // If filtering is used for shadow simulation (rendering will be used else)
    // Rendering is more precise but more intensive, filtering is less accurate but faster
    private boolean useFilter = false;

    // Size of rendered shadow maps, in pixels per side (512, 1024, 2048, etc)
    private int shadowmap_size = 2048;

    // The number of shadow maps rendered(more shadow maps = better quality, but slower).
    // Only affects shadow renderers, not filters
    private int shadowmaps = 3;

    // Shadow intensity (1 = black, 0 = invis)
    private float shadowIntensity = 0.6f;

    // Defines how shadows are filtered
    private EdgeFilteringMode edgeFiltering = EdgeFilteringMode.PCFPOISSON;

    // Shadow edges thickness. default is 10, setting it to lower values can help to reduce the jagged effect of the shadow edges (units in tenths of a pixel)
    private int shadowEdgeThickness = 10;

    // Specifies whether software or hardware shadows are used
    private boolean useHWShadows = true; 

    // Specifies whether or not screen space ambient occlusion is on
    private boolean ssaoOn = true;

    FilterPostProcessor fpp;

    DirectionalLightShadowRenderer dlsr;
    DirectionalLightShadowFilter dlsf; 

    PointLightShadowRenderer plsr;
    PointLightShadowFilter plsf; 
	
    SpotLightShadowRenderer slsr;
    SpotLightShadowFilter slsf;

    SSAOFilter ssaof;

    public GameShadows(Node rootNode, AssetManager assetManager, ViewPort viewPort) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
	this.viewPort = viewPort;

	rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
    }

    public void setupShadowHandlers() {
	// Keep this on top because other handlers rely on it
	setupFilterPostProcessor();

	setupDirectionalLightHandlers();
	setupPointLightHandlers();
	setupSpotLightHandlers();

	setupSSAO();
	
        toggleShadows(shadowsOn);
	toggleSSAO(ssaoOn);
    }

    // Handlers for directional light shadows
    private void setupDirectionalLightHandlers() {
	dlsr = new DirectionalLightShadowRenderer(assetManager, shadowmap_size, shadowmaps);
        dlsr.setEdgeFilteringMode(edgeFiltering);
        dlsr.setShadowIntensity(shadowIntensity);

	dlsf = new DirectionalLightShadowFilter(assetManager, shadowmap_size, shadowmaps);
        dlsf.setEdgeFilteringMode(edgeFiltering);
        dlsf.setShadowIntensity(shadowIntensity);
    	dlsf.setEnabled(useFilter);
	fpp.addFilter(dlsf);
    }

    // Handlers for point light shadows
    private void setupPointLightHandlers() {
	plsr = new PointLightShadowRenderer(assetManager, shadowmap_size);
        plsr.setEdgeFilteringMode(edgeFiltering);
        plsr.setShadowIntensity(shadowIntensity);

	plsf = new PointLightShadowFilter(assetManager, shadowmap_size);
        plsf.setEdgeFilteringMode(edgeFiltering);
        plsf.setShadowIntensity(shadowIntensity);
    	plsf.setEnabled(useFilter);
	fpp.addFilter(plsf);
    }

    // Handlers for spot light shadows
    private void setupSpotLightHandlers() {
        slsr = new SpotLightShadowRenderer(assetManager, shadowmap_size);
        slsr.setEdgeFilteringMode(edgeFiltering);
        slsr.setShadowIntensity(shadowIntensity);

        slsf = new SpotLightShadowFilter(assetManager, shadowmap_size);
        slsf.setEdgeFilteringMode(edgeFiltering);
        slsf.setShadowIntensity(shadowIntensity);
    	slsf.setEnabled(useFilter);
	fpp.addFilter(slsf);
    }

    // Ambient occlusion
    private void setupSSAO() {
	ssaof = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
	ssaof.setEnabled(ssaoOn);
	fpp.addFilter(ssaof);
    }

    private void setupFilterPostProcessor() {
	this.fpp = new FilterPostProcessor(assetManager);
    }

    private void setShadowFilterEnabled(boolean enabled) {
	dlsf.setEnabled(enabled);
	slsf.setEnabled(enabled);
	plsf.setEnabled(enabled);
    }

    // Toggles between filtering and rendering
    public void switchShadowSimMethod() {
	switchShadowSimMethod(!useFilter);
    }

    // Overloaded method to allow you to specify whether or not filtering is used
    public void switchShadowSimMethod(boolean useFiltering) {
	useFilter = useFiltering;

	// Handle turning on and off render processors
	if (!useFilter) {
		addRenderProcessors();
	} else {
		removeRenderProcessors();
	}

	// Handle turning on/off filter processors
	setShadowFilterEnabled(useFilter);
	
	System.out.println("Processors: " + viewPort.getProcessors());
    }

    // Toggles shadows
    public void toggleShadows() {
	toggleShadows(!shadowsOn);
    }

    // Overloaded method to specify shadows on or off
    public void toggleShadows(boolean on) {
	shadowsOn = on;

	if (shadowsOn) {
		// Use whatever shadow sim method is enabled
		switchShadowSimMethod(useFilter);
	} else {
		// Remove render processors and turn off shadow filters
		removeRenderProcessors();
		setShadowFilterEnabled(false);
	}
    }

    // Toggles ambient occlusion
    public void toggleSSAO() {
	toggleSSAO(!ssaoOn);
    }

    // Overloaded method to specify ambient occlusion on or off
    public void toggleSSAO(boolean on) {
	ssaoOn = on;
	if (ssaoOn) {
		ssaof.setEnabled(ssaoOn);
	}
    }

    public float getShadowIntensity() {
	    return this.shadowIntensity;
    }

    public void setShadowIntensity(float intensity) {
	this.shadowIntensity = intensity;
        dlsr.setShadowIntensity(shadowIntensity);
        dlsf.setShadowIntensity(shadowIntensity);
        plsr.setShadowIntensity(shadowIntensity);
        plsf.setShadowIntensity(shadowIntensity);
        slsr.setShadowIntensity(shadowIntensity);
        slsf.setShadowIntensity(shadowIntensity);
    }

    public int getShadowEdgeThickness() {
        return this.shadowEdgeThickness;
    }

    public void setShadowEdgeThickness(int thickness) {
	this.shadowEdgeThickness = thickness;
	dlsr.setEdgesThickness(this.shadowEdgeThickness);
	dlsf.setEdgesThickness(this.shadowEdgeThickness);
	slsr.setEdgesThickness(this.shadowEdgeThickness);
	slsf.setEdgesThickness(this.shadowEdgeThickness);
	plsr.setEdgesThickness(this.shadowEdgeThickness);
	plsf.setEdgesThickness(this.shadowEdgeThickness);
    }

    public void toggleShadowCompareMode() {
	    setShadowCompareMode(!this.useHWShadows);
    }

    public void setShadowCompareMode(boolean useHW) {
	this.useHWShadows = useHW;	
	CompareMode cm = this.useHWShadows ? CompareMode.Hardware : CompareMode.Software;
	dlsr.setShadowCompareMode(cm);
	dlsf.setShadowCompareMode(cm);
	slsr.setShadowCompareMode(cm);
	slsf.setShadowCompareMode(cm);
	plsr.setShadowCompareMode(cm);
	plsf.setShadowCompareMode(cm);
    }

    private void addRenderProcessors() {
	viewPort.addProcessor(slsr);
	viewPort.addProcessor(plsr);
	viewPort.addProcessor(dlsr);
    }

    private void removeRenderProcessors() {
	viewPort.removeProcessor(slsr);
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

    public EdgeFilteringMode getEdgeFilteringMode() {
	return this.edgeFiltering;
    }

    public void setEdgeFilteringMode(int index) {
	this.edgeFiltering = EdgeFilteringMode.values()[index];
	dlsr.setEdgeFilteringMode(this.edgeFiltering);
	dlsf.setEdgeFilteringMode(this.edgeFiltering);
	slsr.setEdgeFilteringMode(this.edgeFiltering);
	slsf.setEdgeFilteringMode(this.edgeFiltering);
	plsr.setEdgeFilteringMode(this.edgeFiltering);
	plsf.setEdgeFilteringMode(this.edgeFiltering);
    }

    public void nextEdgeFilteringMode() {
        int filteringIndex = this.edgeFiltering.ordinal();
        filteringIndex = (filteringIndex + 1) % EdgeFilteringMode.values().length;
	setEdgeFilteringMode(filteringIndex);
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

    public DirectionalLightShadowRenderer getDLSR() {
	return this.dlsr;
    }

    public DirectionalLightShadowFilter getDLSF() {
	return this.dlsf;
    }

    public boolean getUseHWShadows() {
	return this.useHWShadows;
    }

    public boolean getUseFilter() {
	return this.useFilter;
    }
}
