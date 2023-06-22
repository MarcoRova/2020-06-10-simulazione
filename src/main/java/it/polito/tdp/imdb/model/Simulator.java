package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulator {

	// INPUT
	private int days;
	
	// OUTPUT
	private int pauses;
	
	private Map<Integer, Actor> attoriIntervistati;
	
	// MODEL
	private Graph<Actor, DefaultWeightedEdge>grafo;
	private List<Actor> attoriDisponibili;   // per facilitare la scelta casuale
	
	
	public Simulator(int days, Graph<Actor, DefaultWeightedEdge> grafo) {
		super();
		this.days = days;
		this.grafo = grafo;
	}
	
	
	// Inizializzazione 
	public void init() {
		attoriIntervistati = new HashMap<Integer,Actor>();
		this.pauses = 0;
		this.attoriDisponibili = new ArrayList<Actor>(this.grafo.vertexSet());
	}
	
	
	public void run() {
		
		for(int i = 1; i<=days; i++) {
			
			Random rand = new Random();
			
			// primo giorno o giorno dopo una pausa allora scelgo casualmente
			if(i == 1 || !attoriIntervistati.containsKey(i-1)) {
				
				Actor a = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
				
				attoriIntervistati.put(i, a);
				attoriDisponibili.remove(a);
				
				System.out.println("[GIORNO " + i + "] - selezionato autore casualmente (" + a.toString() + ")");
				continue ;
				
			}
			//per due giorni di fila il produttore ha intervistato attori dello stesso genere
			// 90% di probabilità pausa
			if(i>=3 && attoriIntervistati.containsKey(i-1) || attoriIntervistati.containsKey(i-2) 
					&& attoriIntervistati.get(i-1).getGender().equals(attoriIntervistati.get(i-2).getGender())) {
				
				if(rand.nextFloat() <= 0.9) {
					this.pauses ++;
					System.out.println("[GIORNO " + i + "] - pausa!");
					continue ;
				}
			}
			
			// produttore può farsi consigliare dall'ultimo intervistato
			
			// 60% scelgo ancora casualemente
			if(rand.nextFloat() <= 0.6) {
				Actor actor = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
				attoriIntervistati.put(i, actor);
				attoriDisponibili.remove(actor);		
				System.out.println("[GIORNO " + i + "] - selezionato autore casualmente (" + actor.toString() + ")");
				continue ;
			}
			else { // mi faccio consigliare da ultimo intervistato
				Actor lastInterviewed = attoriIntervistati.get(i-1);
				Actor recommended = this.getRecommended(lastInterviewed);
				if(recommended == null || !attoriDisponibili.contains(recommended)) {
					//se l'attore non fornisce consigli, o se l'attore consigliato è già stato intervistato -> scelgo casualmente
					Actor actor = attoriDisponibili.get(rand.nextInt(attoriDisponibili.size()));
					attoriIntervistati.put(i, actor);
					attoriDisponibili.remove(actor);		
					System.out.println("[GIORNO " + i + "] - selezionato autore casualmente (" + actor.toString() + ")");
					continue ;
				} else {
					attoriIntervistati.put(i, recommended);
					attoriDisponibili.remove(recommended);	
					System.out.println("[GIORNO " + i + "] - selezionato autore consigliato (" + recommended.toString() + ")");
					continue ;
				}
			}
		}
	}

	private Actor getRecommended(Actor lastInterviewed) {
		Actor recommended = null;
		int weight = 0;
		
		for(Actor neighbor : Graphs.neighborListOf(this.grafo, lastInterviewed)) {
			if(this.grafo.getEdgeWeight(this.grafo.getEdge(lastInterviewed, neighbor)) > weight) {
				recommended = neighbor;
				weight = (int) this.grafo.getEdgeWeight(this.grafo.getEdge(lastInterviewed, neighbor));
			}
		}
		
		return recommended;
	}
	
	public int getPauses() {
		return this.pauses;
	}
	
	public Collection<Actor> getInterviewedActors(){
		return this.attoriIntervistati.values();
	}
				
	
}
