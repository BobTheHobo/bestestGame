package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FadeFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.SpotLightShadowFilter;

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
    private boolean useFilter = true;

    // Size of rendered shadow maps, in pixels per side (512, 1024, 2048, etc)
    private int shadowmap_size = 2048;

    // The number of shadow maps rendered(more shadow maps = better quality, but slower).
    // Only affects shadow renderers, not filters
    private int shadowmaps = 4;

    // Shadow intensity (1 = black, 0 = invis)
    private float shadowIntensity = 0.45f;

    // Defines how shadows are filtered
    private EdgeFilteringMode edgeFiltering = EdgeFilteringMode.PCFPOISSON;

    // Shadow edges thickness. default is 10, setting it to lower values can help to reduce the jagged effect of the shadow edges (units in tenths of a pixel)
    private int shadowEdgeThickness = 10;

    // Specifies whether software or hardware shadows are used
    private boolean useHWShadows = true; 

    // Specifies whether or not screen space ambient occlusion is on
    // Looks weird with render processing, so for now only enable with filter or none
    private boolean ssaoOn = false;

    // Only applies to directional light
    private boolean stabilizationOn = true;

    // Turns off backface shadow rendering
    private boolean backfaceShadows = false;

    FilterPostProcessor fpp;

    DirectionalLightShadowFilter dlsf; 
    PointLightShadowFilter plsf; 
    SpotLightShadowFilter slsf;

    SSAOFilter ssaof;
    
    FadeFilter fadef;

    public GameShadows(Node rootNode, AssetManager assetManager, ViewPort viewPort) {
	this.rootNode = rootNode;
	this.assetManager = assetManager;
	this.viewPort = viewPort;

	// By default objects added to rootnode should have shadows off
	rootNode.setShadowMode(RenderQueue.ShadowMode.Off);
    }

    public void setupShadowHandlers() {
	// Keep this on top because other handlers rely on it
	setupFilterPostProcessor();

	setupDirectionalLightHandlers();
	setupPointLightHandlers();
	setupSpotLightHandlers();
        

	
        toggleShadows(shadowsOn);

	setupSSAO();
	setSSAO(ssaoOn);

        // Add the TranslucentBucketFilter at the end of the stack
        TranslucentBucketFilter translucentBucketFilter = new TranslucentBucketFilter();
        this.fpp.addFilter(translucentBucketFilter);
        
        // Needed for transitions
        addFadeFilter();
    }
    
    //Add fade filter
    private void addFadeFilter() {
        fadef = new FadeFilter();
	fpp.addFilter(fadef);
        System.out.println("Added fade filter");
    }
    
    public FadeFilter getFadeFilter() {
        return fadef;
    }
    
    public void fixFilterOrdering() {
        //fog over water, 3rd to last
        setFadeLastFilter();
        
        //Ensures translucent bucket is rendered after water by adding as 2nd to last
        // Also partilces can't render over fade transition in this ordering
	setParticlesLastFilter();
        
        // Add fade filter as last filter so shows over everything
        fpp.removeFilter(fadef);
        fpp.addFilter(fadef);
    }
    
    public void setParticlesLastFilter() {
        //Enables particles and other translucent effects to show through everything
        //except solid objects and gui
	TranslucentBucketFilter translucentBucketFilter = fpp.getFilter(TranslucentBucketFilter.class);
	try {
		if (translucentBucketFilter == null) {
			fpp.removeFilter(translucentBucketFilter); // Remove then readd
			fpp.addFilter(translucentBucketFilter);
		}
	} catch(Exception ex) {}
    }
    
    public void setFadeLastFilter() {
        FadeFilter fadeFilter = getFadeFilter();
        fpp.removeFilter(fadeFilter);
        fpp.addFilter(fadeFilter);
    }

    // Handlers for directional light shadows
    private void setupDirectionalLightHandlers() {
	dlsf = new DirectionalLightShadowFilter(assetManager, shadowmap_size, shadowmaps);
        dlsf.setEdgeFilteringMode(edgeFiltering);
        dlsf.setShadowIntensity(shadowIntensity);
	dlsf.setRenderBackFacesShadows(backfaceShadows);
    	dlsf.setEnabled(true);
    }

    // Handlers for point light shadows
    private void setupPointLightHandlers() {
	plsf = new PointLightShadowFilter(assetManager, shadowmap_size);
        plsf.setEdgeFilteringMode(edgeFiltering);
        plsf.setShadowIntensity(shadowIntensity);
	plsf.setRenderBackFacesShadows(backfaceShadows);
	plsf.getPreShadowForcedRenderState().setPolyOffset(-2.0f, -2.0f);
	System.out.println("Hello " + plsf.getPreShadowForcedRenderState().getPolyOffsetFactor());
	System.out.println("Hello " + plsf.getPreShadowForcedRenderState().isPolyOffset());
    	plsf.setEnabled(true);
    }

    // Handlers for spot light shadows
    private void setupSpotLightHandlers() {
        slsf = new SpotLightShadowFilter(assetManager, shadowmap_size);
        slsf.setEdgeFilteringMode(edgeFiltering);
        slsf.setShadowIntensity(shadowIntensity);
	slsf.setRenderBackFacesShadows(backfaceShadows);
    	slsf.setEnabled(true);
    }

    // Ambient occlusion
    private void setupSSAO() {
	ssaof = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
	ssaof.setEnabled(true);
    }

    private void setupFilterPostProcessor() {
	this.fpp = new FilterPostProcessor(assetManager);

	viewPort.addProcessor(this.fpp);
    }

    private void addFilterProcessors() {
	// If processor hasn't been added yet then add it
	if(!fpp.getFilterList().contains(slsf))
		fpp.addFilter(slsf);
	if(!fpp.getFilterList().contains(plsf))
		fpp.addFilter(plsf);
	if(!fpp.getFilterList().contains(dlsf))
		fpp.addFilter(dlsf);
    }

    private void removeFilterProcessors() {
	// If processor has been added remove it
	if(fpp.getFilterList().contains(slsf))
		fpp.removeFilter(slsf);
	if(fpp.getFilterList().contains(plsf))
		fpp.removeFilter(plsf);
	if(fpp.getFilterList().contains(dlsf))
		fpp.removeFilter(dlsf);
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
		removeFilterProcessors();
	} else {
		addFilterProcessors();
	}
	
	System.out.println("Processors: " + viewPort.getProcessors());
	System.out.println("Filter enabled: " + fpp.getFilterList());
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
		removeFilterProcessors();
	}
    }

    // Toggles ambient occlusion
    public void toggleSSAO() {
	setSSAO(!ssaoOn);
    }

    // Method to specify ambient occlusion on or off
    public void setSSAO(boolean on) {
	if (!useFilter) {
		System.out.println("Cannot use SSAO with filtering");
		return;
	}

	ssaoOn = on;
	if(ssaoOn) {
		if(!fpp.getFilterList().contains(ssaof)) {
			fpp.addFilter(ssaof);
		}
	} else {
		if(fpp.getFilterList().contains(ssaof)) {
			fpp.removeFilter(ssaof);
		}
	}
	System.out.println("SSAO set to: " + fpp.getFilterList().contains(ssaof));
    }

    public boolean getSSAO() {
	return ssaoOn;
    }

    public float getShadowIntensity() {
	    return this.shadowIntensity;
    }

    public void setShadowIntensity(float intensity) {
	this.shadowIntensity = intensity;
        dlsf.setShadowIntensity(shadowIntensity);
        plsf.setShadowIntensity(shadowIntensity);
        slsf.setShadowIntensity(shadowIntensity);
    }

    public int getShadowEdgeThickness() {
        return this.shadowEdgeThickness;
    }

    public void setShadowEdgeThickness(int thickness) {
	this.shadowEdgeThickness = thickness;
	dlsf.setEdgesThickness(this.shadowEdgeThickness);
	slsf.setEdgesThickness(this.shadowEdgeThickness);
	plsf.setEdgesThickness(this.shadowEdgeThickness);
    }

    public void toggleShadowCompareMode() {
	    setShadowCompareMode(!this.useHWShadows);
    }

    public void setShadowCompareMode(boolean useHW) {
	this.useHWShadows = useHW;	
	CompareMode cm = this.useHWShadows ? CompareMode.Hardware : CompareMode.Software;
	dlsf.setShadowCompareMode(cm);
	slsf.setShadowCompareMode(cm);
	plsf.setShadowCompareMode(cm);
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
	dlsf.setEdgeFilteringMode(this.edgeFiltering);
	slsf.setEdgeFilteringMode(this.edgeFiltering);
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
	    dlsf.setLight(light);
    }

    // Add point light
    public void attachPointLight(PointLight light) {
	    plsf.setLight(light);
    }

    // Add spot light
    public void attachSpotLight(SpotLight light) {
	    slsf.setLight(light);
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

    public boolean getShadowStabilization() {
    	return stabilizationOn;
    }

    public void toggleShadowStabilization() {
	setShadowStabilization(!stabilizationOn);
    }
    
    public void setShadowStabilization(boolean on) {
	stabilizationOn = on;
	dlsf.setEnabledStabilization(stabilizationOn);
    }

    public void setBackfaceShadows(boolean on) {
	backfaceShadows = on;
	dlsf.setRenderBackFacesShadows(backfaceShadows);
	plsf.setRenderBackFacesShadows(backfaceShadows);
	slsf.setRenderBackFacesShadows(backfaceShadows);
    }

    public FilterPostProcessor getFPP() {
	    return fpp;
    }
    
    public void reset() {
        fpp.removeAllFilters();
    }
}
