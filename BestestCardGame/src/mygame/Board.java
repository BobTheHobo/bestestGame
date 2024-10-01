/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author jerem
 */
public class Board {
    private static Box mesh = new Box(.375f, .001f, .6f);
    private Node table; //Table node where we place our board
    private Node selfNode; //The node representing this board
    private ArrayList<Galley> galleys = new ArrayList<>(); //Array of Galleys on board
    private ArrayList<Card> deck = new ArrayList<>(); //Array representing player deck
    private ArrayList<Card> hand = new ArrayList<>(); //Array representing player hand
    private AssetManager assetManager; //Asset manager
    private Spatial self;
    private Random rand = new Random();
    
    private int state = 0; //Board state. 0 = No card selected, 1 = Card from hand selected
    private boolean jolly = false; //Weather or not the jolly roger is being raised
    
    int cards = 0;// So each cards has a unique name
    
    public Board(Node node, AssetManager assetManager) {
        this.assetManager = assetManager;
        table = node;
        selfNode = new Node();

        Geometry self = new Geometry("PlayMat", mesh);
        Material tileMat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        tileMat.setColor("Color", ColorRGBA.Blue);
        self.setMaterial(tileMat);
        self.center();
        self.move(0, 0.425f, 0.45f);
        
        selfNode.attachChild(self);
        makeGalleys();
        table.attachChild(selfNode);
        selfNode.move(0, 0.01f, 0.0f);
        draw(7);
        showHand();
    }
    
    private void makeGalleys() {
        for (int i = 0; i < 6; i++) {
            galleys.add(new Galley(this, i));
        }
    }
    
    private void draw(int count) {
        for (int i = 0; i < count; i++) {
            int pull = rand.nextInt(101);
            if (pull <= 49) {
                hand.add(new Card(assetManager, "Swashbuckler", cards++));
            } else if (pull <= 74) {
                hand.add(new Card(assetManager, "Cook", cards++));
            } else {
                hand.add(new Card(assetManager, "Gunner", cards++));
            }
        }
    }
    
    public void showHand() {
        int count = hand.size();
        Vector3f middle = new Vector3f(0, 0.5f, 1.4f);
        Quaternion angle = new Quaternion();
        angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
        for (int i = 0; i < count; i++) {
            Node card = hand.get(i).getSelfNode();
            card.center();
            card.move(middle);
            int direction = i % 2;
            int step = (i + 1) / 2;
            if (direction == 0) {//Move card to right
                card.move(.07f * step, 0, -.01f * step);
            } else {// Move card to left
                card.move(-.07f * step, 0, -.01f * step);
            }
            card.setLocalRotation(angle);
            //card.rotate(angle);
            table.attachChild(card);
        }
    }
    
    public void hideHand() {
        for (int i = 0; i < hand.size(); i++) {
            Node card = hand.get(i).getSelfNode();
            card.removeFromParent();
        }
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    
    public Node getSelfNode() {
        return selfNode;
    }
    
    public Node getTable() {
        return table;
    }
}
