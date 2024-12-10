/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

/**
 *
 * @author Nakano on this thread, modified by Viet
 * https://hub.jmonkeyengine.org/t/how-to-make-loading-screen/46454/17
 */
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.post.filters.FadeFilter;

public final class FadeAppState extends BaseAppState {
	private final FadeFilter filter;
	
	private float listenerTarget;
	private Runnable listener;
	
	public FadeAppState(final FadeFilter filter) {
		if(filter == null) {
			throw new IllegalArgumentException("filter == null");
		}
		this.filter = filter;
	}
	
	@Override
	public void update(final float tpf) {
		if(listener != null) {
			if(filter.getValue() == listenerTarget) {
				final Runnable r = listener;
				listener = null;
				r.run();
			}
		}
	}
	
	public void fadeIn(final Runnable l) {
		if(l == null) {
			throw new IllegalArgumentException("l == null");
		}
		
		if(listener != null) {
			listener.run();
		}
		
		listenerTarget = 1.0f;
		listener = l;
		filter.fadeIn();
	}
	public void fadeOut(final Runnable l) {
		if(l == null) {
			throw new IllegalArgumentException("l == null");
		}
		
		if(listener != null) {
			listener.run();
		}
		
		listenerTarget = 0.0f;
		listener = l;
		filter.fadeOut();
	}
	public void setDuration(final float duration) {
		if(duration < 0) {
			// Reverses the fading. Could it makes sense to allow this?
			throw new IllegalArgumentException("duration < 0");
		}
		filter.setDuration(duration);
	}
	
	@Override
	protected void cleanup(final Application app) {
		
	}
	@Override
	protected void initialize(final Application app) {
		
	}
	@Override
	protected void onDisable() {
		
	}
	@Override
	protected void onEnable() {
		
	}
}
