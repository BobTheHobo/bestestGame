package com.mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;

public class PhysicsHelper {

    // Adds physics to the given geometry based on whether it's dynamic or static
    public static void addPhysics(Geometry geom, boolean isDynamic, BulletAppState bulletAppState) {
        // Static objects (e.g., floor, walls) have mass = 0, dynamic objects (pickable items) have mass > 0
        float mass = isDynamic ? 1.0f : 0.0f;

        // Use RigidBodyControl to apply physics automatically (no custom shapes or margins)
        RigidBodyControl physicsControl = new RigidBodyControl(mass);
        geom.addControl(physicsControl);

        // Add to the physics space
        bulletAppState.getPhysicsSpace().add(physicsControl);
    }
}
