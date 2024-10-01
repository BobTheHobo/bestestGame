/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author jerem
 */
public class Card {
    private static Box mesh = new Box(.14f, .001f, .24f);
    private Slot parent;
    private String name;
    private AssetManager assetManager; //Asset manager
    private Spatial self;
    private Node selfNode;
    private int power = 0;
    private String topString;
    private BitmapText topText;
    
    public Card(AssetManager assetManager, String name, int count) {
        this.assetManager = assetManager;
        parent = null; //Base case of a card before it is played
        this.name = String.format("Card%s-%d", name, count);
        self = new Geometry(this.name, mesh);
        selfNode = new Node();
        
        
        Material mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        switch (this.name.substring(4, this.name.indexOf("-"))) {// Just get the card type
            case "Swashbuckler":
                power = 4;
                mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Pink);
                break;
            case "Cook":
                power = 2;
                mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);
                break;
            case "Gunner":
                power = 2;
                mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
                break;
        }
        
        topString = String.format("%d-%s", power, this.name.substring(4, this.name.indexOf("-")));
        
        BitmapFont font = assetManager.loadFont("Interface/Fonts/LucidaCalligraphy.fnt");
        
        topText = new BitmapText(font);
        
        topText.setText(topString);
        
        self.setMaterial(mat);
        self.center();
        
        topText.center();
        topText.move(-.11f, .01f, -.13f);
        Quaternion back = new Quaternion();
        back.fromAngleAxis(-FastMath.HALF_PI, new Vector3f(1,0,0));
        topText.rotate(back);
        topText.scale(.002f);
       
        
        selfNode.attachChild(topText);
        
        selfNode.attachChild(self);
        selfNode.scale(.25f);
    }
    
    public Spatial getSelf() {
        return self;
    }
    
    public Node getSelfNode() {
        return selfNode;
    }
    
    public int changePower(int i) {
        power += i;
        topString = String.format("%d-%s", power, this.name.substring(4, this.name.indexOf("-")));
        topText.setText(topString);
        if (power <= 0) {//Card was killed, remove it.
           selfNode.removeFromParent();
           parent.setFilled(false);
           return 0;
        } else {//Card is not killed
           return 1;
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setParent(Slot slot) {
        parent = slot;
    }
    
    public int getPower() {
        return power;
    }
}
