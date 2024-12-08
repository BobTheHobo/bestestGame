/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.viet_files;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.mygame.PlayerManager;
import java.util.List;

/**
 *
 * @author Viet
 */
public class KillPlane {
    private Node rootNode;
    private AssetManager assetManager;
    private PlayerManager playerManager;
    private SFXManager sfxManager;
     
    private float killPlaneY = -0.7f; // y-coordinate of the kill plane
    private float playerKillY = -120f;
    private float objRespawnY = 0.5f;
    private Vector3f objRespawnPt = new Vector3f(0.6f, 0.5f, -4f);
    
    public KillPlane(Node rootNode, AssetManager assetManager, PlayerManager playerManager, SFXManager sfxManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.playerManager = playerManager;
        this.sfxManager = sfxManager;
        
        sfxManager.loadSFX("Key-Drop", "Sounds/SFX/Key-Drop-16bit.wav");
        
        // Add physical plane to visualize
        //addVis();
    }

    public void checkForResets(float tpf) {
        
        if (playerManager.getPlayerPosition().y < playerKillY) {
            playerManager.resetPlayerPosition();
        }
        
        List<Spatial> movObjs = ((Node)rootNode.getChild("Move object node")).getChildren();
        
        for(Spatial movObj : movObjs) {
            
            Geometry geo = null;
            try {
                if (movObj.getName().equals("Key node")) {
                    // Just get the first geo
                    geo = (Geometry)((Node)((Node)movObj).getChild(0)).getChild(0);
                }
                if (movObj.getName().equals("Candle node")) {
                    // Just get the first geo
                    
                    Node nd = (Node)((Node)((Node)movObj).getChild("candle")).getChild(0);
                    geo = (Geometry) nd.getChild(0);
                }

                if (geo != null) {
                    //System.out.println(
                    //    "geo " + geo.getName() + 
                    //    " at " + geo.getWorldTranslation().toString());
                    if(geo.getWorldTranslation().getY() < killPlaneY) {
                        System.out.println("reseting " + geo.getName());
                        resetObject(geo);
                    }
                }
            } catch (Exception ex) {
                
            }
            
        }
    }

    private void resetObject(Spatial object) {
        Spatial parent;
        boolean isCandle = false;
        boolean isKey = false;
                
        if (object.getName().equals("Candle.001_0")) {
            parent = object.getParent().getParent().getParent();
            isCandle = true;
        } else {
            parent = object.getParent();
            isKey = true;
        }
        
        Vector3f newPos;
        Vector3f oldPos = parent.getLocalTranslation();
        
        // If inside bounds of ship just reset y position
        if (
            oldPos.getX() < 2.6f && oldPos.getX() > -2.6 &&
            oldPos.getZ() < 3.9f && oldPos.getZ() > -4.25   
        ) {
            System.out.println("Inbounds");
            newPos = parent.getLocalTranslation().setY(objRespawnY);
        } else {
            System.out.println("Out of bounds");
            newPos = objRespawnPt;
        }
        
        System.out.println(
                "reseting " + parent.getName() + 
                " from " + oldPos + 
                " to " + newPos );
        parent.setLocalTranslation(newPos); // Reset position
        parent.getControl(RigidBodyControl.class).setPhysicsLocation(newPos); //Also update collision
    
        playResetAudio(isKey, newPos);
    }
    
    private void addVis() {
        Geometry killPlane = new Geometry("KillPlane", new Box(50, 0.1f, 50));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        killPlane.setMaterial(mat);
        killPlane.setLocalTranslation(0, killPlaneY, 0);
        rootNode.attachChild(killPlane);
    }
    
    private void playResetAudio(boolean isKey, Vector3f loc) {
        if (isKey) {
            sfxManager.playSFX("Key-Drop", loc);
        } else {
            // Play sfx for candle reset
        }
    }
}
