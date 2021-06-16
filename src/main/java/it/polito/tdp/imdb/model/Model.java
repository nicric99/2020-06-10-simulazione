package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	private Integer giorni;
	private Actor sorgente;
	private Set<Actor> copia;
	private List<Actor> cammino;
	private Graph<Actor,DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map<Integer,Actor> idMap;
	
	public Model(){
		this.dao= new ImdbDAO();
		this.idMap= new HashMap<Integer,Actor>();
		this.cammino= new ArrayList<>();
	}
	
	public void creaGrafo(String genere) {
		dao.listAllActors(idMap);
		this.grafo=  new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		for(Actor a: dao.getVertici(idMap,genere)) {
			this.grafo.addVertex(a);
		}
		for(Adiacenza a:dao.getArchi(genere, idMap)) {
			Graphs.addEdge(this.grafo, a.getA1(),a.getA2(), a.getPeso());
		}
	}
	public Integer getNVertici() {
		return this.grafo.vertexSet().size();
	}
	public List<String> getGeneri(){
		return dao.getGeneri();
	}
	public Integer getNArchi() {
		return this.grafo.edgeSet().size();
	}
	public List<Actor> getAttori(){
		List<Actor> result= new ArrayList<>();
		for (Actor a:this.grafo.vertexSet()) {
			result.add(a);
		}
		return result;
	}

	public Set<Actor> getSimili(Actor a) {
		Set<Actor> result;
		List<Actor> resultl=new ArrayList<>();
		ConnectivityInspector<Actor, DefaultWeightedEdge> conn= new ConnectivityInspector<Actor, DefaultWeightedEdge>(this.grafo);
		result=new HashSet<>(conn.connectedSetOf(a));
		for(Actor as:result) {
			resultl.add(as);
		}
		Collections.sort(resultl);
		Collections.reverse(resultl);
		return result;
		
	}
	public void initializeSim(Integer giorni) {
		this.giorni=giorni;
		this.copia= new HashSet<>(this.grafo.vertexSet());
		this.sorgente=new Actor(null,null,null,null);
		int size= this.grafo.vertexSet().size();
		int posizione=  (int) (Math.random() * size) +1;
		int i=0;
		for(Actor a: this.grafo.vertexSet()) {
			if(i==posizione) {
				this.sorgente= new Actor(a.getId(),a.getFirstName(),a.getLastName(),a.getGender());
				break;
			}
			i++;
		}
		
		
	}
	public void run() {
		int numero= (int) (Math.random() * 100) +1;
		while(this.cammino.size()<=this.giorni) {
			if(numero<=40){
				
				cammino.add(this.sorgente);
				copia.remove(this.sorgente);
				this.sorgente=this.getNextAttore(sorgente);
				
			}else{
				cammino.add(this.sorgente);
				copia.remove(this.sorgente);
				randomizzaAttore();
			}
		}
		
	}
	
	public void randomizzaAttore() {
		int size= this.copia.size();
		int posizione=  (int) (Math.random() * size) +1;
		int i=0;
		for(Actor a: this.copia) {
			if(i==posizione) {
				this.sorgente= new Actor(a.getId(),a.getFirstName(),a.getLastName(),a.getGender());
				cammino.add(a);
				copia.remove(a);
				break;
			}
		}
	}
	public Actor getNextAttore(Actor a) {
		List<Actor> list= new ArrayList<Actor>(Graphs.neighborListOf(this.grafo, a));
		int pesoBest=0;
		Actor migliore=a;
		for(Actor s: list) {
			DefaultWeightedEdge e=this.grafo.getEdge(a, s);
			int tmp=(int) this.grafo.getEdgeWeight(e);
			if(tmp>pesoBest && !this.cammino.contains(s)) {
				pesoBest=tmp;
				migliore=new Actor(s.getId(),s.getFirstName(),s.getLastName(),s.getGender());
			}
		}
		return migliore;
	}
	public List<Actor> getCammino(){
		return this.cammino;
	}

}
