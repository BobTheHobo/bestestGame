package com.mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.RigidBodyControl;
//import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

/*
* author: shawn
*/
public class PhysicsHelper {

    // Adds physics to the given geometry based on whether it's dynamic or static
    public static RigidBodyControl addPhysics(Spatial spa, boolean isDynamic, boolean pickUpable, BulletAppState bulletAppState) {
        // Static objects (e.g., floor, walls) have mass = 0, dynamic objects (pickable items) have mass > 0
        float mass = isDynamic ? 1.0f : 0.0f;

        // Use RigidBodyControl to apply physics automatically (no custom shapes or margins)
        RigidBodyControl physicsControl = new RigidBodyControl(mass);
        spa.addControl(physicsControl);

	if (pickUpable) {
	    // Set item to collision group 3
	    physicsControl.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
	    // Allow item to collide with environment (group 1)
	    physicsControl.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
	    // Allow item to collide with character (group 2)
	    physicsControl.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
	}

        // Add to the physics space
        bulletAppState.getPhysicsSpace().add(physicsControl);

	return physicsControl;
    }
}
