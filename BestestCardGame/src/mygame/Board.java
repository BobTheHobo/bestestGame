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
    private ArrayList<Card> playerDeck = new ArrayList<>(); //Array representing player deck
    private ArrayList<Card> playerHand = new ArrayList<>(); //Array representing player hand
    private ArrayList<Card> enemyDeck = new ArrayList<>(); //Array representing enemy deck
    private ArrayList<Card> enemyHand = new ArrayList<>(); //Array representing enemy hand
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
        draw(7, true);
        draw(7, false);
        showPlayerHand();
    }
    
    //Initializes our array and galleys in the scene
    private void makeGalleys() {
        for (int i = 0; i < 6; i++) {
            galleys.add(new Galley(this, i));
        }
    }
    
    //Draws 'count' cards to a hand; player's hand if 'player' is true, enemy if else 
    private void draw(int count, boolean player) {
        ArrayList<Card> hand = player ? playerHand : enemyHand;
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
    
    //Attatches the cards in our hand to the 3d scene
    public void showPlayerHand() {
        int count = playerHand.size();
        Vector3f middle = new Vector3f(0, 0.5f, 1.4f);//Where the center card is
        Quaternion angle = new Quaternion();//So we can read the card sitting down
        angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
        for (int i = 0; i < count; i++) {//Get each card
            Node card = playerHand.get(i).getSelfNode();
            card.center();
            card.move(middle);//Move it to the central card
            int direction = i % 2;
            int step = (i + 1) / 2;
            if (direction == 0) {//Move card to right
                card.move(.07f * step, 0, -.01f * step);
            } else {// Move card to left
                card.move(-.07f * step, 0, -.01f * step);
            }
            card.setLocalRotation(angle);
            table.attachChild(card);
        }
    }
    
    //Will be added later to attatch enemy cards to 3d scene so you know how many they have
    public void showEnemyHand() {
        
    }
    
    //Removes the cards from the 3d scene
    public void hidePlayerHand() {
        for (int i = 0; i < playerHand.size(); i++) {
            Node card = playerHand.get(i).getSelfNode();
            card.removeFromParent();
        }
    }
    
    
    //Puts the 'card' in the 'slot' applying game logic
    public void play(Spatial card, Spatial slot) {
        Card cardObj = getCard(card, playerHand);  //Get our card and slot
        Slot slotObj = getSlot(slot);
        
        slotObj.setCard(cardObj); //Put card in slot
        
        playerHand.remove(cardObj); //Take card from hand
       
        showPlayerHand(); //Update hand spatials
        enemyMove();
        
        if (playerHand.isEmpty()) {
            nextRound();
        }
    }
    
    //All galleys are paired, gets the opposite one
    public Galley getOppositeGalley(int i) {
        if (i < 3) {//Enemy galley
            return galleys.get(i + 3);
        } else {//Friendly galley
            return galleys.get(i - 3);
        }
    }
    
    //Advances game state to next round
    public void nextRound() {
        for (int i = 0; i < galleys.size(); i++) {//Remove galleys that are weaker than opposites            
            if (galleys.get(i).getPower() < galleys.get(i).opposite().getPower()) {
                galleys.get(i).setSunk(true);
                galleys.get(i).getSelfNode().removeFromParent();
            }  
        }
        
        for (int i = 0; i < galleys.size(); i++) {//Remove galleys that are weaker than opposites
            galleys.get(i).clear();
        }
        
        draw(2, true);//Each player draws 2
        draw(2, false);
        showPlayerHand();
    }
    
    //Currently very basic AI for how the enemy acts
    private void enemyMove() {
        int pull = rand.nextInt(enemyHand.size()); //Get a random index hand
        Card card = enemyHand.get(pull); //Get a the card corresponding to index
        enemyHand.remove(card); //Remove the card from the enemy hand
        
        ArrayList<Galley> open = new ArrayList<>();// To collect all galleys that are valid
        for (int i = 0; i < 3; i++) {
            if(!galleys.get(i).getSunk() && galleys.get(i).open()) {// Galley has an open slot and is not sunk
                open.add(galleys.get(i));
            }
        }
        
        pull = rand.nextInt(open.size());//Get a random valid galley
        open.get(pull).playRandom(card);//Play the card on the galley
        
        table.attachChild(card.getSelfNode());//Show the card in space
        
    }
    
    //Gets the Card object that corresponds to the 'card' spatial
    private Card getCard(Spatial card, ArrayList<Card> hand) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getSelf() == card) {
                return hand.get(i);
            }
        }
        return null;
    }
    
    private Slot getSlot(Spatial slot) {
        int galley = Integer.parseInt(slot.toString().substring(4,5));
        int space = Integer.parseInt(slot.toString().substring(6,7));
        
        return galleys.get(galley).getSlot(space);
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
