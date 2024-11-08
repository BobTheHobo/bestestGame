/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.jeremiah_files;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
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
    private ArrayList<Galley> enemyGalleys = new ArrayList<>(); //Array of Galleys on board
    private ArrayList<Galley> playerGalleys = new ArrayList<>(); //Array of Galleys on board
    private ArrayList<Card> playerDeck = new ArrayList<>(); //Array representing player deck
    private ArrayList<Card> playerHand = new ArrayList<>(); //Array representing player hand
    private ArrayList<Card> enemyDeck = new ArrayList<>(); //Array representing enemy deck
    private ArrayList<Card> enemyHand = new ArrayList<>(); //Array representing enemy hand
    private AssetManager assetManager; //Asset manager
    private Spatial self;
    private Random rand = new Random();
    private AppStateManager stateManager;
    
    
    private int state = 0; //Board state. 0 = No card selected, 1 = Card from hand selected
    private boolean jolly = false; //Weather or not the jolly roger is being raised
    
    int cards = 0;// So each cards has a unique name
    
    public Board(Node node, AssetManager assetManager, AppStateManager stateManager) {
        this.assetManager = assetManager;
        this.stateManager = stateManager;
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
        showEnemyHand();
    }
    
    //Initializes our array and galleys in the scene
    private void makeGalleys() {
        for (int i = 0; i < 3; i++) {
            enemyGalleys.add(new Galley(this, i));
        }
        for (int i = 3; i < 6; i++) {
            playerGalleys.add(new Galley(this, i));
        }
    }
    
    //Draws 'count' cards to a hand; player's hand if 'player' is true, enemy if else 
    public void draw(int count, boolean player) {
        ArrayList<Card> hand = player ? playerHand : enemyHand;
        for (int i = 0; i < count; i++) {
            int pull = rand.nextInt(101);
            if (pull <= 19) {
                hand.add(new Card(assetManager, "Swashbuckler", cards++));
            } else if (pull <= 39) {
                hand.add(new Card(assetManager, "Cook", cards++));
            } else if (pull <= 59) {
                hand.add(new Card(assetManager, "Gunner", cards++));
            } else if (pull <= 79) {
                hand.add(new Card(assetManager, "Cannoneer", cards++));
            } else if (pull <= 99) {
                hand.add(new Card(assetManager, "Lookout", cards++));
            } else {
                hand.add(new Card(assetManager, "Lookout", cards++));
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
        int count = enemyHand.size();
        Vector3f middle = new Vector3f(0, 0.5f, -1.4f);//Where the center card is
        Quaternion angle = new Quaternion();//So we can read the card sitting down
        angle.fromAngleAxis(-FastMath.HALF_PI, new Vector3f(1,0,0));
        for (int i = 0; i < count; i++) {//Get each card
            Node card = enemyHand.get(i).getSelfNode();
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
        
        /*
        if (playerHand.isEmpty()) {
            nextRound();
        }
        */
    }
    
    //All galleys are paired, gets the opposite one
    public Galley getOppositeGalley(Galley galley) {
        if (enemyGalleys.contains(galley)) {//Enemy galley
            return playerGalleys.get(enemyGalleys.indexOf(galley));
        } else {//Friendly galley
            return enemyGalleys.get(playerGalleys.indexOf(galley));
        }
    }
    
    //Advances game state to next round
    public void nextRound() {
        ArrayList<Integer> sunkEnemies = new ArrayList<Integer>();
        ArrayList<Integer> sunkPlayers = new ArrayList<Integer>();
        
        for (int i = 0; i < enemyGalleys.size(); i++) {//Note which galleys lost
            if (i < playerGalleys.size()) {
                System.out.println("Galleys: " + i + " Enemy Power: " + enemyGalleys.get(i).getPower() + " Player Power: " + playerGalleys.get(i).getPower());
                if (enemyGalleys.get(i).getPower() < playerGalleys.get(i).getPower()) {
                    sunkEnemies.add(i);                  
                } else if (enemyGalleys.get(i).getPower() > playerGalleys.get(i).getPower()) {
                    sunkPlayers.add(i);
                }   
            }
              
        }
        
        for (int i = 0; i < enemyGalleys.size(); i++) {//Remove cards from galleys
            enemyGalleys.get(i).clear();
        }
        for (int i = 0; i < playerGalleys.size(); i++) {//Remove cards from galleys
            playerGalleys.get(i).clear();
        }
        
        for (int i = enemyGalleys.size() - 1; i >= 0; i--) {//Move all remaining enemy galleys to correct spot
            int toMove = 0;
                    
            for (int j = 0; j < sunkEnemies.size(); j++) {
                if (sunkEnemies.get(j) < i) {
                    toMove++;
                }
            }
            
            Spatial node = enemyGalleys.get(i).getSelfNode();
            Vector3f destination = node.getLocalTranslation();
            destination = destination.add(-.25f * toMove, 0f, 0f);
                    
            MotionPath path =  new MotionPath();
            path.addWayPoint(node.getLocalTranslation());
            path.addWayPoint(destination);
            path.setPathSplineType(Spline.SplineType.CatmullRom);

            MotionEvent motion = new MotionEvent(node, path);
            motion.setDirectionType(MotionEvent.Direction.PathAndRotation);
            motion.setSpeed(8f);
                    
            motion.play();
        }
            
        for (int i = playerGalleys.size() - 1; i >= 0; i--) {//Move all remaining player galleys to correct spot
            int toMove = 0;
                    
            for (int j = 0; j < sunkPlayers.size(); j++) {
                if (sunkPlayers.get(j) < i) {
                    toMove++;
                }
            }
            
            Spatial node = playerGalleys.get(i).getSelfNode();
            Vector3f destination = node.getLocalTranslation();
            destination = destination.add(-.25f * toMove, 0f, 0f);
                    
            MotionPath path =  new MotionPath();
            path.addWayPoint(node.getLocalTranslation());
            path.addWayPoint(destination);
            path.setPathSplineType(Spline.SplineType.CatmullRom);

            MotionEvent motion = new MotionEvent(node, path);
            motion.setDirectionType(MotionEvent.Direction.PathAndRotation);
            motion.setSpeed(8f);
                    
            motion.play();
        }
        
        ArrayList<Galley> toSink = new ArrayList<>();
        
        for (int i = 0; i < sunkEnemies.size(); i++) {//From index add galley objects to be sunk
            toSink.add(enemyGalleys.get(sunkEnemies.get(i)));
        }
        
        for (int i = 0; i < sunkPlayers.size(); i++) {//From index add galley objects to be sunk
            toSink.add(playerGalleys.get(sunkPlayers.get(i)));
        }
        
        while (!toSink.isEmpty()) {//Sink relevant galleys
            if (toSink.get(0).getIndex() < 3) {
                enemyGalleys.remove(toSink.get(0));
            } else {
                playerGalleys.remove(toSink.get(0));
            }
            toSink.get(0).getSelfNode().removeFromParent();
            toSink.remove(0);
        }
        
        
        
        draw(2, true);//Each player draws 2
        draw(2, false);
        showPlayerHand();
        showEnemyHand();
        
        if (enemyGalleys.isEmpty()) {
            System.out.println("You Win!");
        } else if (playerGalleys.isEmpty()) {
            System.out.println("You Lose!");
        } else {
            System.out.println("Next Round!");
        }
        
    }
    
    //Currently very basic AI for how the enemy acts
    private void enemyMove() {
        int pull = rand.nextInt(enemyHand.size()); //Get a random index hand
        Card card = enemyHand.get(pull); //Get a the card corresponding to index
        enemyHand.remove(card); //Remove the card from the enemy hand
        
        
        ArrayList<Galley> open = new ArrayList<>();// To collect all galleys that are valid
        for (int i = 0; i < enemyGalleys.size(); i++) {
            if(enemyGalleys.get(i).open() && (i < playerGalleys.size())) {// Galley has an open slot and is in play
                open.add(enemyGalleys.get(i));
            }
        }
        
        //ArrayList<Galley> open = enemyGalleys;
        
        if (!open.isEmpty()) {//Ensures there's a place to play
            pull = rand.nextInt(open.size());//Get a random valid galley
            
            
            open.get(pull).playRandom(card);//Play the card on the galley

            table.attachChild(card.getSelfNode());//Show the card in space
        }
        showEnemyHand();
    }
    
    //Gets the Card object that corresponds to the 'card' spatial
    public Card getCard(Spatial card, ArrayList<Card> hand) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getSelf() == card) {
                return hand.get(i);
            }
        }
        return null;
    }
    
    //Gets the Slot object that corresponds to the 'slot' spatial
    private Slot getSlot(Spatial slot) {
        int galley = Integer.parseInt(slot.toString().substring(4,5));
        int space = Integer.parseInt(slot.toString().substring(6,7));
        
        if (galley < 3) {
            for (int i = 0; i < enemyGalleys.size(); i++) {
                if (enemyGalleys.get(i).getIndex() == galley) {
                    return enemyGalleys.get(i).getSlot(space);
                }
            }
            //return enemyGalleys.get(galley).getSlot(space);
        } else {
            for (int i = 0; i < playerGalleys.size(); i++) {
                if (playerGalleys.get(i).getIndex() == galley) {
                    return playerGalleys.get(i).getSlot(space);
                }
            }
            //return playerGalleys.get(galley - 3).getSlot(space);
        }
        return null;
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
    
    public AppStateManager getStateManager() {
        return stateManager;
    }
    
    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }
    
    public ArrayList<Card> getEnemyHand() {
        return enemyHand;
    }
    
}
