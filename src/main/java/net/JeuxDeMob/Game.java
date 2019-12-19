package net.JeuxDeMob;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Game {
	
	private TheDeck deck;
	private ArrayList<Player> playerList;
	private int nbPlayerTotal;
	int playerTurn;
	static Player played;
	
	
	

	public Game(String style, int nbPlayer) throws SQLException {
		nbPlayerTotal = nbPlayer+1;
		this.setDeck(new TheDeck(style));
		this.setPlayerList(new ArrayList<Player>());
		playerList.add(new Player(true));
		for(int i = 0; i<nbPlayer; i++) {
			playerList.add(new Player(false));
		}
		for(int i = 0; i<this.nbPlayerTotal; i++) {
			while(toPioche(playerList.get(i)));;
			
		}
		this.playerTurn = new Random().nextInt(this.getNbPlayerTotal());
		
		
	}
	
	public void runTurn() throws SQLException {
			played = this.getPlayerList().get(getPlayerTurn());

			if(partyIsFinish()) {
					int posPlayerWin = whoWin();
					boolean userWin = false;
					if(whoWin()==0)userWin = true;
					
				System.out.println("--------Partie terminer -----------");
					
					DataBase.getInstance().addEndGame(userWin, (this.getPlayerList().get(posPlayerWin).getHandFigurine().size()-1));
					return;
				}

			
			if(played.isUser()) {
				System.out.println("------------------------------------\nplayer = "+played.getPseudo());
				System.out.println("-----main-----");
				for(Card c : played.getHandCards()) {
					System.out.println(c.getName());
				}
				if(!iHaveNoPossibilities()) {
					System.out.println("ok play !");
				TableGameController.phase1();
				return;
				}
				System.out.println("ok next player");
				nextPlayer();
			
			}
			else {
				System.out.println("------------------------------------\nplayer = "+played.getPseudo());
				System.out.println("-----main-----");
				for(Card c : played.getHandCards()) {
					System.out.println(c.getName());
				}

				if(!iHaveNoPossibilities()) {
					System.out.println("ok play !");
					int rand = new Random().nextInt(played.getHandCards().size());
					Card c = played.getHandCards().get(rand);
					while(inHandFigurine(c.getName(),played)) {
						rand = new Random().nextInt(played.getHandCards().size());
						c = played.getHandCards().get(rand);
					}

					if(toPlayCard(c.toString())) {
						
						nextPlayer();
					}
					
				}
				else {
					System.out.println("ok next player");
					nextPlayer();
				}

			}
		}
	public boolean toPlayCard(String card) {
		int posPlayer = howIsFiguring(card);
		Player focus = null;
		if(posPlayer<30) focus = this.getPlayerList().get(posPlayer);
		
		if(posPlayer <50) {
			if(whatNumberCardInHand(card, focus )<30){
				System.out.println("ok "+played.getPseudo()+" joue la carte  = "+card+" contre  "+focus.getPseudo());
				if(focus.isUser()) {
					System.out.println(focus.getPseudo()+" veut tu contré? ");
					TableGameController.phaseContre(played.getHandCards().get(whatNumberCardInHand(card, played)), played.getPseudo());
					played.getHandCards().remove(whatNumberCardInHand(card, played));
					return false;
				}
				else {
					System.out.println(focus.getPseudo()+" contre ");
					contre(focus,card);
					if( whatNumberCardInHand(card, played)<30) {
						if(played.isUser()) {
							System.out.println(focus.getPseudo()+"veut tu  double contré");
							TableGameController.phaseContreContre(played.getHandCards().get(whatNumberCardInHand(card, played)), played.getPseudo(), true);
							return false;
						}
						System.out.println("double contre");
						played.addFigurine(this.getPlayerList().get(posPlayer).getHandFigurine().get(card));
						this.getPlayerList().get(posPlayer).getHandFigurine().remove(card);
						played.getHandCards().remove(whatNumberCardInHand(card, played));
						TableGameController.refreshFigPlayer(this.playerTurn, posPlayer, card);
						toPioche(played);
					}
					System.out.println("le joueur "+focus.getPseudo()+" contre");
					return true;
				}
			}
			played.addFigurine(this.getPlayerList().get(posPlayer).getHandFigurine().get(card));
			this.getPlayerList().get(posPlayer).getHandFigurine().remove(card);
			}
		else {
			played.addFigurine(this.deck.getDeckFigurine().get(card));
			this.deck.getDeckFigurine().remove(card);

		}
		System.out.println("carte  = "+card);
		System.out.println("carte "+card+"   remove n° = "+whatNumberCardInHand(card, played));
		TableGameController.refreshFigPlayer(this.playerTurn, posPlayer, card);
		
		played.getHandCards().remove(whatNumberCardInHand(card, played));
		toPioche(played);
		return true;
	}
	public void contre(Player focus, String card){
		played.getHandCards().remove(whatNumberCardInHand(card, played));
		toPioche(played);
		focus.getHandCards().remove(whatNumberCardInHand(card, focus));
		toPioche(focus);
	}
	//
	public int  whoWin() {
		int posPlayer=0;
		int nbFig = 0;
		for(int i = 0 ; i<this.getPlayerList().size(); i++) {
			if(nbFig< this.getPlayerList().get(i).getHandFigurine().size()) {
				nbFig = this.getPlayerList().get(i).getHandFigurine().size();
				posPlayer = i;
			}

		}
		return posPlayer;
	}
	// to add card for player P
	public boolean toPioche(Player player) {
		if(this.getDeck().getDeck().size()==0)return false;
		if(player.addCard(this.getDeck().getDeck().get(0))) {
			this.getDeck().getDeck().remove(0);
			this.getDeck().setCountCard(this.getDeck().getCountCard()-1);
			return true;
		}
		return false;	
	}
	//to allows to switch next players
	public void nextPlayer() throws SQLException {
		
		this.getPlayerList().get(this.playerTurn).setYouTurn(false);
		this.playerTurn++;
		if(this.playerTurn==this.getNbPlayerTotal())this.playerTurn=0;
		this.getPlayerList().get(this.playerTurn).setYouTurn(true);
		viewFiguring();
		
		runTurn();
	}
	// give key card in index X
	public int whatNumberCardInHand(String name, Player p) {
		int pos = 0;
		for(int i = 0; i<p.getHandCards().size();i++) {
			String c = p.getHandCards().get(i).getName();
			if(name == c) {
				return pos;
			}
		pos++;
		}
		return 50;
	}
	//view figiguring hand 
	public void viewFiguring() {
		System.out.println("------Figurine--------");
		for (Map.Entry mapentry : played.getHandFigurine().entrySet()) {
			System.out.println( mapentry.getKey().toString());
			}
	}
	// to verified how is figuring
	public int howIsFiguring(String nameCard) {
		int count;
		for(int i = 0; i<nbPlayerTotal;i++) {
			
			Player p = this.playerList.get(i);
			String[] HandCards = new String[5];
			count =0;
			for (Map.Entry mapentry : p.getHandFigurine().entrySet()) {
				HandCards[count] = mapentry.getKey().toString(); 
				if(nameCard == mapentry.getKey().toString()) {
					if(this.playerList.get(i)==played)break;
					count++;
					break;
				}
			}
			if(count ==1) return i;	
			}
			
		return 99;
		}
	// to verifie is this player have possibilitie to play 
	public boolean iHaveNoPossibilities() {
		int count = 0; 
		String[] listHand= new String[11];
		for (Map.Entry mapentry : played.getHandFigurine().entrySet()) {
			listHand[count]= mapentry.getKey().toString();
			count++;
		}
		count=0;
		for(int j = 0; j<listHand.length; j++) {
			for(int i =0; i<played.getHandCards().size();i++) {
				if(played.getHandCards().get(i).getName().equals(listHand[j]) ) {
				count++;
			}
		}
		}
		System.out.println(" le count est de "+count+" sur "+played.getHandCards().size()+"carte en main");
		if(count ==played.getHandCards().size()) {
			played.setNoPossibilities(true);
			System.out.println("peut pas jouer? = "+played.isNoPossibilities());
			return true;
		}
		played.setNoPossibilities(false);
		return false;

	}
	// to verified  if i have figuring in my hand
	public boolean inHandFigurine(String card, Player p) {
		int count = 0;
		String[] listHand= new String[5];
		for (Map.Entry mapentry : played.getHandFigurine().entrySet()) {
			if(card == mapentry.getKey().toString()) {
				return true ;
			}
		}
		return false;
	}

	// to verifie is all player havent possibilitie of game
	public boolean partyIsFinish(){
		int count = 0;
		for(int i = 0; i<this.getNbPlayerTotal(); i++) {
			if(this.getPlayerList().get(i).isNoPossibilities()) {
				count++;
			}
		}
		return count==this.getNbPlayerTotal();
		
	}
	public ArrayList<Player> getPlayerList() {
		return playerList;
	}
	public void setPlayerList(ArrayList<Player> playerList) {
		this.playerList = playerList;
	}
	
	public TheDeck getDeck() {
		return deck;
	}
	public void setDeck(TheDeck deck) {
		this.deck = deck;
	}
	
	public int getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}
	
	public int getNbPlayerTotal() {
		return nbPlayerTotal;
	}

}
