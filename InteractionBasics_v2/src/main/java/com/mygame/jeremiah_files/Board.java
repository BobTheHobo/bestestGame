/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame.jeremiah_files;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
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
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author jerem
 */
public class Board {
    private static Box mesh = new Box(.375f, .001f, .6f);
    private Node table; //Table node where we place our board
    private Node selfNode; //The node representing this board
    private ArrayList<Galley> galleys = new ArrayList<>(); //Array of all Galleys on board
    private ArrayList<Galley> enemyGalleys = new ArrayList<>(); //Array of enemy Galleys on board
    private ArrayList<Galley> playerGalleys = new ArrayList<>(); //Array of friendly Galleys on board
    private ArrayList<Card> playerDeck = new ArrayList<>(); //Array representing player deck
    private ArrayList<Card> playerHand = new ArrayList<>(); //Array representing player hand
    private ArrayList<Card> enemyDeck = new ArrayList<>(); //Array representing enemy deck
    private ArrayList<Card> enemyHand = new ArrayList<>(); //Array representing enemy hand
    private ArrayList<Card> playerDiscard = new ArrayList<>(); //Array representing player discard pile
    private ArrayList<Card> enemyDiscard = new ArrayList<>(); //Array representing enemy discard pile
    private AssetManager assetManager; //Asset manager
    private Spatial self;
    private Random rand = new Random();
    private AppStateManager stateManager;
    private AudioNode drawAudio;
    private AudioNode sinkAudio;
    private AudioNode playAudio;
    private int opponent = 1;
    private int jollyState = 0; //Board state. 0 = No card selected, 1 = Card from hand selected
    private boolean hasKraken = false;
    private boolean kraken = false;
    private boolean lost = false;
    private boolean played = false;
    
    int cards = 0;// So each cards has a unique name
    
    public Board(Node node, AssetManager assetManager, AppStateManager stateManager) {
        this.assetManager = assetManager;
        this.stateManager = stateManager;
        table = node;
        selfNode = new Node();
        
        playAudio = new AudioNode(assetManager, "Audio/DEAL1.wav");
        drawAudio = new AudioNode(assetManager, "Audio/SHUFFLE.wav");
        sinkAudio = new AudioNode(assetManager, "Audio/uwater2.wav");
        sinkAudio.setPositional(false);

        Geometry self = new Geometry("PlayMat", mesh);
        Material tileMat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        tileMat.setColor("Color", ColorRGBA.Blue);
        tileMat.setTexture("ColorMap", assetManager.loadTexture("Textures/cloth.jpg"));
        self.setMaterial(tileMat);
        self.center();
        self.move(0, 0.425f, 0.45f);
        
        selfNode.attachChild(self);
        makeGalleys();
        makeDecks();
        Spatial coin = assetManager.loadModel("Models/3D Models/Card-Game-Related/Skull Coin.glb");
        coin.scale(0.1f);
        selfNode.attachChild(coin);
        coin.center();
        coin.setLocalTranslation(0f, .45f, 0.50f);
        Quaternion angle = new Quaternion();
        angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(1, 0,0));
        coin.setLocalRotation(angle);
        coin.rotate(0, 0, FastMath.PI);

        table.attachChild(selfNode);
        selfNode.move(0, 0.01f, 0.0f);
        draw(7, true);
        draw(7, false);
        drawAudio.play();
        //showPlayerHand();
        //showEnemyHand();
    }
    
    //Initializes our array and galleys in the scene
    private void makeGalleys() {
        for (int i = 0; i < 3; i++) {
            Galley galley = new Galley(this, i);
            enemyGalleys.add(galley);
            //galleys.add(galley);
        }
        for (int i = 3; i < 6; i++) {
            Galley galley = new Galley(this, i);
            playerGalleys.add(galley);
            //galleys.add(galley);
        }
    }
    
    //Default player deck is 8 swashbucklers, 3 each of rest; Enemy is 4 of each
    private void makeDecks() {
        for (int i = 0; i < 8; i++) {
            playerDeck.add(new Card(assetManager, "Swashbuckler", cards++));
            if (i % 2 == 0) {
                enemyDeck.add(new Card(assetManager, "Swashbuckler", cards++));            
                enemyDeck.add(new Card(assetManager, "Cook", cards++));
                enemyDeck.add(new Card(assetManager, "Gunner", cards++));
                enemyDeck.add(new Card(assetManager, "Cannoneer", cards++));
                enemyDeck.add(new Card(assetManager, "Lookout", cards++));          
            }
        }
        
        for (int i = 0; i < 3; i++) {
            playerDeck.add(new Card(assetManager, "Cook", cards++));
            playerDeck.add(new Card(assetManager, "Gunner", cards++));
            playerDeck.add(new Card(assetManager, "Cannoneer", cards++));
            playerDeck.add(new Card(assetManager, "Lookout", cards++));
        }
        
        Collections.shuffle(playerDeck);
        Collections.shuffle(enemyDeck);
        
        Quaternion angle = new Quaternion();
        angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0, 1,0));
        
        
        for (int i = 0; i < playerDeck.size(); i++) {
            Spatial card = playerDeck.get(playerDeck.size() - 1 - i).getSelfNode();
            card.center();
            Vector3f spot = new Vector3f(-.3f, .45f + (i * 0.002f), 0.55f);
            card.move(spot);
            card.setLocalRotation(angle);
            card.rotate(0, 0, FastMath.PI);
            table.attachChild(card);
        }
        
        for (int i = 0; i < enemyDeck.size(); i++) {
            Spatial card = enemyDeck.get(enemyDeck.size() - 1 - i).getSelfNode();
            card.center();
            Vector3f spot = new Vector3f(-.3f, .45f + (i * 0.002f), 0.45f);
            card.move(spot);
            card.setLocalRotation(angle);
            card.rotate(0, 0, FastMath.PI);
            table.attachChild(card);
        }
    }
    
    //Draws 'count' cards to a hand; player's hand if 'player' is true, enemy if else 
    public void draw(int count, boolean player) {
        ArrayList<Card> hand = player ? playerHand : enemyHand;
        ArrayList<Card> deck = player ? playerDeck : enemyDeck;
        ArrayList<Card> discard = player ? playerDiscard : enemyDiscard;
        
        if (hasKraken && opponent == 2 && player) {
            Card card = new Card(assetManager, "Kraken", cards++);
            Vector3f middle = player ? new Vector3f(0, 0.5f, 1.4f) : new Vector3f(0, 0.5f, -0.8f);//Where the center card is
            Quaternion angle = new Quaternion();//So we can read the card sitting down
            if (player) {
                angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
            } else {
                angle.fromAngleAxis(-FastMath.HALF_PI, new Vector3f(1,0,0));
            }

            
            card.getSelfNode().center();
            card.getSelfNode().move(middle);
            int direction = (hand.size() - 1) % 2;
            int step = ((hand.size() - 1) + 1) / 2;
            if (direction == 0) {//Move card to right
                card.getSelfNode().move(.07f * step, 0, -.01f * step);
            } else {// Move card to left
                card.getSelfNode().move(-.07f * step, 0, -.01f * step);
            }
            card.getSelfNode().setLocalRotation(angle);
            hand.add(card);

            table.attachChild(card.getSelfNode());
            
            return;
        }
        
        
        for (int i = 0; i < count; i++) {
            if (deck.isEmpty() && discard.isEmpty()) {
                return;
            }
            playAudio.play();
            
            if (deck.isEmpty()) {// Deck is empty, shuffle
                shuffle(player);
            }
            Card card = deck.get(0);
            
            hand.add(card);
            deck.remove(card);
            
            Vector3f middle = player ? new Vector3f(0, 0.5f, 1.4f) : new Vector3f(0, 0.5f, -1.4f);//Where the center card is
            Quaternion angle = new Quaternion();//So we can read the card sitting down
            if (player) {
                angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(1,0,0));
            } else {
                angle.fromAngleAxis(-FastMath.HALF_PI, new Vector3f(1,0,0));
            }

            
            card.getSelfNode().center();
            card.getSelfNode().move(middle);
            int direction = (hand.size() - 1) % 2;
            int step = ((hand.size() - 1) + 1) / 2;
            if (direction == 0) {//Move card to right
                card.getSelfNode().move(.07f * step, 0, -.01f * step);
            } else {// Move card to left
                card.getSelfNode().move(-.07f * step, 0, -.01f * step);
            }
            card.getSelfNode().setLocalRotation(angle);
        }
        
        //showPlayerHand();
        //showEnemyHand();
    }
    
    //Shuffles discard pile into draw deck
    private void shuffle(boolean player) {
        drawAudio.play();
        //System.out.println("Shuffle!");
        Quaternion angle = new Quaternion();
        angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0, 1,0));
        
        if (player) {
            for (int i = 0; i < playerDiscard.size(); i++) {
                playerDeck.add(playerDiscard.get(i));
            }
            //playerDeck = playerDiscard;
            playerDiscard.clear();
            
            for (int i = 0; i < playerDeck.size(); i++) {
                Spatial card = playerDeck.get(playerDeck.size() - 1 - i).getSelfNode();
                card.center();
                Vector3f spot = new Vector3f(-.3f, .45f + (i * 0.002f), 0.55f);
                card.move(spot);
                card.setLocalRotation(angle);
                card.rotate(0, 0, FastMath.PI);
                table.attachChild(card);
            }
        } else {
            for (int i = 0; i < enemyDiscard.size(); i++) {
                enemyDeck.add(enemyDiscard.get(i));
            }
            //enemyDeck = enemyDiscard;
            enemyDiscard.clear();
            
            for (int i = 0; i < enemyDeck.size(); i++) {
                Spatial card = enemyDeck.get(enemyDeck.size() - 1 - i).getSelfNode();
                card.center();
                Vector3f spot = new Vector3f(-.3f, .45f + (i * 0.002f), 0.45f);
                card.move(spot);
                card.setLocalRotation(angle);
                card.rotate(0, 0, FastMath.PI);
                table.attachChild(card);
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
        Vector3f middle = new Vector3f(0, 0.5f, -0.8f);//Where the center card is
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
    
    public void hideEnemyHand() {
        for (int i = 0; i < enemyHand.size(); i++) {
            Node card = enemyHand.get(i).getSelfNode();
            card.removeFromParent();
        }
    }
    
    
    //Puts the 'card' in the 'slot' applying game logic
    public void play(Spatial card, Spatial slot) {
        Card cardObj = getCard(card, playerHand);  //Get our card and slot
        Slot slotObj = getSlot(slot);
        
        if (jollyState == 1) {
            cardObj.changePower(cardObj.getPower());
        }
        
        slotObj.setCard(cardObj); //Put card in slot
        
        playerHand.remove(cardObj); //Take card from hand
        
        playAudio.play();
        played = true;
        
       
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
            destination = destination.add(-.2f * toMove, 0f, 0f);
                    
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
            destination = destination.add(-.2f * toMove, 0f, 0f);
                    
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
            //toSink.get(0).getSelfNode().removeFromParent();
            Spatial node = toSink.get(0).getSelfNode();
            Vector3f destination = node.getLocalTranslation();
            destination = destination.add(0f, -.05f, 0f);
                    
            MotionPath path =  new MotionPath();
            path.addWayPoint(node.getLocalTranslation());
            path.addWayPoint(destination);
            path.setPathSplineType(Spline.SplineType.CatmullRom);

            MotionEvent motion = new MotionEvent(node, path);
            motion.setDirectionType(MotionEvent.Direction.PathAndRotation);
            motion.setSpeed(1.5f);
                    
            motion.play();
            toSink.remove(0);
            sinkAudio.play();
        }
        
        
        
        draw(2, true);//Each player draws 2
        draw(2, false);
        showPlayerHand();
        showEnemyHand();
        played = false;
        jollyReset();
        
        if (enemyGalleys.isEmpty()) {
            //System.out.println("You Win!");
            nextOpponent();
        } else if (playerGalleys.isEmpty()) {
            //System.out.println("You Lose!");
            lost = true;
        } else {
            System.out.println("Next Round!");
        }
        
    }
    
    //Currently very basic AI for how the enemy acts
    private void enemyMove() {
        if (enemyHand.isEmpty()) {
            return;
        }
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
            playAudio.play();
        }
        showEnemyHand();
        
        if (jollyState == 1) {
            nextRound();
        }
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
    
    public void discard(boolean player, Card card) {
        card.resetPower();
        
        ArrayList<Card> discard = player ? playerDiscard : enemyDiscard;
        discard.add(card);
        
        Quaternion angle = new Quaternion();
        angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0, 1,0));
        
        Spatial cardSpatial = card.getSelfNode();
        //cardSpatial.center();
        Vector3f spot = !player ? new Vector3f(.3f, .45f + ((discard.size() - 1) * 0.002f), 0.55f) : new Vector3f(.3f, .45f + ((discard.size() - 1) * 0.002f), 0.45f);
        //cardSpatial.move(spot);
        
        MotionPath path =  new MotionPath();
        path.addWayPoint(cardSpatial.getLocalTranslation());
        path.addWayPoint(spot);
        path.setPathSplineType(Spline.SplineType.CatmullRom);
  
        MotionEvent motion = new MotionEvent(cardSpatial, path);
        motion.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motion.setSpeed(8f);
        motion.play();
        
        cardSpatial.setLocalRotation(angle);
        cardSpatial.rotate(0, 0, FastMath.PI);
        table.attachChild(cardSpatial);
    }
    
    public void nextOpponent() {
        opponent = 2;

        while (!playerGalleys.isEmpty()) {
            playerGalleys.get(0).getSelfNode().removeFromParent();
            playerGalleys.remove(0);
        }
        makeGalleys();
        
        enemyDeck.clear();
        
        for (int i = 0; i < enemyHand.size(); i++) {
            enemyHand.get(i).getSelfNode().removeFromParent();
        }
        enemyHand.clear();
        
        for (int i = 0; i < enemyDiscard.size(); i++) {
            enemyDiscard.get(i).getSelfNode().removeFromParent();
        }
        enemyDiscard.clear();
        
        while(!playerHand.isEmpty()) {
            playerDeck.add(playerHand.get(0));
            playerHand.remove(0);
        }
        while (!playerDiscard.isEmpty()) {
            playerDeck.add(playerDiscard.get(0));
            playerDiscard.remove(0);
        }
        
        playerDiscard.clear();
        
        
        for (int i = 0; i < 20; i++) {
            enemyDeck.add(new Card(assetManager, "Guilt", cards++));
        }
        
        Collections.shuffle(playerDeck);
        Collections.shuffle(enemyDeck);
        
        showDeck(true);  
        showDeck(false);
        showDiscard(true);
        showDiscard(false);
        
        draw(7, true);
        draw(7, false);
        showPlayerHand();
        showEnemyHand();
        drawAudio.play();
    }
    
    private void showDeck(boolean player) {
        Quaternion angle = new Quaternion();
        angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0, 1,0));
        
        if (player) {    
            for (int i = 0; i < playerDeck.size(); i++) {
                Spatial card = playerDeck.get(playerDeck.size() - 1 - i).getSelfNode();
                card.center();
                Vector3f spot = new Vector3f(-.3f, .45f + (i * 0.002f), 0.55f);
                card.move(spot);
                card.setLocalRotation(angle);
                card.rotate(0, 0, FastMath.PI);
                table.attachChild(card);
            }
        } else {
            for (int i = 0; i < enemyDeck.size(); i++) {
                Spatial card = enemyDeck.get(enemyDeck.size() - 1 - i).getSelfNode();
                card.center();
                Vector3f spot = new Vector3f(-.3f, .45f + (i * 0.002f), 0.45f);
                card.move(spot);
                card.setLocalRotation(angle);
                card.rotate(0, 0, FastMath.PI);
                table.attachChild(card);
            }
        }
    }
    
    private void showDiscard(boolean player) {
        if (player) {
            for (int i = 0; i < playerDiscard.size(); i++) {
                Quaternion angle = new Quaternion();
                angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0, 1,0));

                Spatial cardSpatial = playerDiscard.get(i).getSelfNode();
                cardSpatial.center();
                Vector3f spot = new Vector3f(.3f, .45f + ((i) * 0.002f), 0.55f);
                cardSpatial.move(spot);
                cardSpatial.setLocalRotation(angle);
                cardSpatial.rotate(0, 0, FastMath.PI);
                table.attachChild(cardSpatial);
            }
        } else {
            for (int i = 0; i < enemyDiscard.size(); i++) {
                Quaternion angle = new Quaternion();
                angle.fromAngleAxis(FastMath.HALF_PI, new Vector3f(0, 1,0));

                Spatial cardSpatial = enemyDiscard.get(i).getSelfNode();
                cardSpatial.center();
                Vector3f spot = new Vector3f(.3f, .45f + ((i) * 0.002f), 0.45f);
                cardSpatial.move(spot);
                cardSpatial.setLocalRotation(angle);
                cardSpatial.rotate(0, 0, FastMath.PI);
                table.attachChild(cardSpatial);
            }
        }
    }

    public boolean kraken() {
        return kraken;
    }
    
    public void setKraken() {
        kraken = true;
    }
    
    public void jolly(boolean player) {
        if (player) {
            if (jollyState == 0) {
                jollyState = 1;
            }
        } else {
            if (jollyState == 0) {
                jollyState = -1;
            }
        }
    }
    
    private void jollyReset() {
        jollyState = 0;
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

    public ArrayList<Galley> getEnemyGalleys() {
	return enemyGalleys;
    }
    
    public ArrayList<Galley> getPlayerGalleys() {
	return playerGalleys;
    }
    
    public ArrayList<Card> getPlayerDiscard() {
        return playerDiscard;
    }
    
    public ArrayList<Card> getEnemyDiscard() {
        return enemyDiscard;
    }
    
    public ArrayList<Card> getPlayerDeck() {
        return playerDeck;
    }
    
    public ArrayList<Card> getEnemyDeck() {
        return enemyDeck;
    }
    
    public int getOpponent() {
        return opponent;
    }
    
    public boolean isLost() {
        return lost;
    }
    
    public boolean getPlayed() {
        return played;
    }
    
    public void addKraken() {
        
        if (!hasKraken && opponent == 2) {
            hasKraken = true;
            draw(1, true);
            hidePlayerHand();
        }
        
        hasKraken = true;
    }
}
