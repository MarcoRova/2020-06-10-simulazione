package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private ImdbDAO dao;
	private Graph<Actor, DefaultWeightedEdge> grafo;
	private Map<Integer, Actor> actorsIDMap;
	private Simulator sim;

	public Model() {
		super();
		this.dao = new ImdbDAO();
	}
	
	
	public void creaGrafo(String genre) {
		
		this.grafo = new SimpleWeightedGraph<Actor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.actorsIDMap = new HashMap<>();
		loadActorsMap();
		
		Graphs.addAllVertices(this.grafo, getVertici(genre));
		
		
		for(Arco a : this.dao.getArchi(genre)) {
			Graphs.addEdgeWithVertices(this.grafo, this.actorsIDMap.get(a.getId1()),  this.actorsIDMap.get(a.getId2()), a.getPeso());
		}
	}
	
	public void loadActorsMap() {
		for(Actor a : this.dao.listAllActors())
			this.actorsIDMap.put(a.getId(), a);
	}
	
	public List<Actor> getVertici(String genre) {
		
		List<Actor> vertici = new LinkedList<>();
		
		List<Integer> idA = this.dao.getIdActorsByGenre(genre);
		
		for(Integer i : idA) {
			vertici.add(this.actorsIDMap.get(i));
		}
		return vertici;
	}
	
	public List<Actor> getConnectedActors(Actor a){
		
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<>(grafo);
		
		List<Actor> actors = new ArrayList<>(ci.connectedSetOf(a));
		actors.remove(a);
		Collections.sort(actors);
		
		return actors;
	}
	
	
	public List<String> getAllGenres(){
		return this.dao.getAllGenres();
	}
	
	public String infoGrafo() {
		return "Numero vertici: " + this.grafo.vertexSet().size()+" Numero archi: " + this.grafo.edgeSet().size();
	}
	
	public Graph<Actor, DefaultWeightedEdge> getGrafo(){
		return this.grafo;
	}
	
	
	public void simulate(int n) {

		sim = new Simulator(n, grafo);
		
		sim.init();
		sim.run();
		
	}
	
	public Collection<Actor> getInterviewedActors(){
		if(sim == null){
			return null;
		}
		return sim.getInterviewedActors();
	}
	
	public Integer getPauses(){
		if(sim == null){
			return null;
		}
		return sim.getPauses();
	}
}
