package algorithms;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class DefaultTeam {
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		//REMOVE >>>>>
		ArrayList<Point> result = (ArrayList<Point>)points.clone();
		//for (int i=0;i<points.size()/3;i++) result.remove(0);
		// if (false) result = readFromFile("output0.points");
		// else saveToFile("output",result);
		//<<<<< REMOVE


		//System.out.println("Size : "+points.size());
		//ArrayList<Point> domSet = domSetGloutonMain(points);
		//System.out.println("Size : "+points.size());
		//return domSetLocalSearchingNaifMain(points, domSet);

		//ArrayList<Point> domSet = domSetGloutonMain(result);
		//return domSetLocalSearchingNaifMain(result, domSet);

		//Solution otptimisee
		ArrayList<Point> domSet =testDomSet(result);

		return domSet;
	}

	/**
	 * Methode qui trouve tous les points isoles (sans aucun voisin) dans une liste de points
	 * @param points2
	 * @return iles (points isoles)
	 */
	public ArrayList<Point> iles(ArrayList<Point> points2){
		ArrayList<Point> points = (ArrayList<Point>) points2.clone();
		ArrayList<Point> iles = new ArrayList<Point>();

		Evaluation eval = new Evaluation();
		for(int i = 0; i < points.size(); i++){
			Point v = points.get(i);
			if(eval.neighbor(v, points, 80).size()==0){
				iles.add(v);
			} 
		}
		return iles;
	}
	/**
	 * Methode qui trouve tous les points qui ont un seul voisin
	 * de degree un dans une liste de points
	 * @param points2
	 * @return 
	 */
	public ArrayList<Point> degreeUn(ArrayList<Point> points2){
		ArrayList<Point> points = (ArrayList<Point>) points2.clone();
		ArrayList<Point> degreeUn = new ArrayList<Point>();

		Evaluation eval = new Evaluation();
		for(int i = 0; i < points.size(); i++){
			Point v = points.get(i);
			if(eval.neighbor(v, points, 80).size()==1){
				degreeUn.add(v);
			}	
		}
		return degreeUn;
	}
	/**
	 * Pour un set de points appartenant au graphe (points2)
	 * retourne les voisins de ce set
	 * @param points2
	 * @param set
	 * @return voisins
	 */
	public ArrayList<Point> voisins(ArrayList<Point> points2, ArrayList<Point> set2 ){
		ArrayList<Point> points = (ArrayList<Point>) points2.clone();
		ArrayList<Point> set = (ArrayList<Point>) set2.clone();
		ArrayList<Point> voisins = new ArrayList<Point>();

		Evaluation eval = new Evaluation();
		for(int i = 0; i < set.size(); i++){
			Point v = set.get(i);
			ArrayList<Point> voisinage =  eval.neighbor(v, points, 80);
			voisins.addAll(voisinage);
		}
		return voisins;
	}
	/**
	 * Pour un set de points appartenant au graphe (points2)
	 * retourne les voisins de ce set
	 * @param points2
	 * @param set
	 * @return voisins
	 */
	public ArrayList<Point> voisinsParDegree(ArrayList<Point> points2, Point v, int d){
		ArrayList<Point> points = (ArrayList<Point>) points2.clone();
		
		ArrayList<Point> voisins = new ArrayList<Point>();
		int degree = d;
		
		Evaluation eval = new Evaluation();
		for(int i = 0; i < d; i++){
			ArrayList<Point> voisinage = new ArrayList<Point>();
			for(Point p: voisins){
				voisinage =  eval.neighbor(p, points, 80);
			}
			
			voisins.addAll(voisinage);
		}
		return voisins;
	}
	/**
	 * test domSet with additional methods
	 * @param graphe
	 * @param domSet2
	 * @return
	 */
	public ArrayList<Point> testDomSet(ArrayList<Point> graphe){
		ArrayList<Point> points = (ArrayList<Point>) graphe.clone();
		ArrayList<Point> domSet = new ArrayList<Point>();
		Evaluation eval = new Evaluation();
		//trouver les points isolee
		ArrayList<Point> iles =iles(points);
		points.removeAll(iles);
		//trouver tout les sommets de degree 1
		ArrayList<Point> degreeUn =degreeUn(points);
		points.removeAll(degreeUn);
		//en supprimant les degreeUn, si on a une composante de deux sommets, elle s'efface
		//donc il faut une methode pour ne pas les supprimés



		//trouver les voisins de l'ensemble degreeUn
		ArrayList<Point> voisinsDegreeUn = voisins(points, degreeUn);
		points.removeAll(voisinsDegreeUn);
		//sur points sans iles, sans degreeUn et sans voisinsDegreeUn
		//on appelle algo glouton
		domSet = domSetGloutonMain(points);
		domSetLocalSearchingNaifMain(points, domSet);

		domSet.addAll(voisinsDegreeUn);
		domSet.addAll(iles);

		return domSet;
	}
	/**
	 * Polynomial-time approximation scheme (PTAS) for the Minimum Dominating Set problem
	 * in Unit Disk Graphs
	 * by Tim Nieberg and Johann Hurink
	 * 
	 * 
	 * @param graphe
	 * @param domSet2
	 * @return
	 */
	public ArrayList<Point> ptasDomSet(ArrayList<Point> points2){
		ArrayList<Point> V = (ArrayList<Point>) points2.clone(); //copie de la liste initial des points
		ArrayList<Point> W = new ArrayList<Point>(); // sous-graphe de V
		ArrayList<Point> domSet = new ArrayList<Point>();
		Evaluation eval = new Evaluation();

		//collection of neighborhoods Ni: domSet(G) = domSet(Ni) forall i
		//Nr(v) := voisinage de dergree r de v; recursivement N(N r-1 (v)) = N(v)
		//N1(v) := N(v) //  méthode voisinsParDegree(points, v, r) retourne les voisins par degree de v dans points
		ArrayList<Point> voisinageR = new ArrayList<Point>();
		ArrayList<Point> voisinageR2 = new ArrayList<Point>();

		//trouver les points isolee et enlever de la liste des points à traiter
		ArrayList<Point> iles =iles(V);
		V.removeAll(iles);

		int r = 0;
		int P = 1; //approximation ratio
		/*"Lemma 5. There exists a constant c = c(p) such that ^r1<=c, that is, 
		 *the largest neighborhood to be considered during the iteration of the algorithm 
		 * is bounded by a constant" p.10
		 */
		int c = 0;



		do {
			for(int i = 0; i < r; i++) {
				ArrayList<Point> tmp = new ArrayList<Point>();
				for(Point q : voisinageR) {
					tmp.addAll(eval.neighbor(q, V, 55));
				}
				voisinageR.addAll(tmp);
			}

			voisinageR2 = (ArrayList<Point>) voisinageR.clone();
			for(int i = 0; i < 2; i++) {
				ArrayList<Point> tmp = new ArrayList<Point>();
				for(Point q : voisinageR2) {
					tmp.addAll(eval.neighbor(q, V, 55));
				}
				voisinageR2.addAll(tmp);
			}

			r++;
		} while((domSet.size()*P) < V.size());




		return domSet;
	}

	public ArrayList<Point> optiDomSet(ArrayList<Point> points2, ArrayList<Point> domSet2) {
		ArrayList<Point> points = (ArrayList<Point>) points2.clone();
		ArrayList<Point> domSet = (ArrayList<Point>) domSet2.clone();


		for(Point p : domSet2) {
			for(Point q : domSet2) {
				if(p.equals(q)) continue;
				if(p.distance(q) > 55*3) continue;

				for(Point z : points) {

					domSet.remove(p);
					domSet.remove(q);
					domSet.add(z);

					//				  points.add(p);
					//				  points.add(q);
					//				  points.remove(z);

					if(isValid(points2, domSet)) {
						return domSet;
					}
					else {
						domSet.add(p);
						domSet.add(q);
						domSet.remove(z);

						//					  points.remove(p);
						//					  points.remove(q);
						//					  points.add(z);
					}
					domSet2 = (ArrayList<Point>)domSet.clone();
				}
			}
			//		  points2 = (ArrayList<Point>) points.clone();
		}

		return domSet;
	}


	public ArrayList<Point> PTAS(ArrayList<Point> points2) {
		ArrayList<Point> points = (ArrayList<Point>) points2.clone();


		Point v = points.get(0);
		ArrayList<Point> domSet = new ArrayList<Point>();
		domSet.add(v);
		ArrayList<Point> domSet2 = new ArrayList<Point>();

		int r = 0;
		int P = 1;

		ArrayList<Point> voisinageR = new ArrayList<Point>();
		ArrayList<Point> voisinageR2 = new ArrayList<Point>();

		Evaluation eval = new Evaluation();
		do {
			for(int i = 0; i < r; i++) {
				ArrayList<Point> tmp = new ArrayList<Point>();
				for(Point q : voisinageR) {
					tmp.addAll(eval.neighbor(q, points, 55));
				}
				voisinageR.addAll(tmp);
			}

			voisinageR2 = (ArrayList<Point>) voisinageR.clone();
			for(int i = 0; i < 2; i++) {
				ArrayList<Point> tmp = new ArrayList<Point>();
				for(Point q : voisinageR2) {
					tmp.addAll(eval.neighbor(q, points, 55));
				}
				voisinageR2.addAll(tmp);
			}

			r++;
		} while((domSet.size()*P) < domSet2.size());



		return new ArrayList<Point>();
	}



	public ArrayList<Point> domSetLocalSearchingNaifMain(ArrayList<Point> points2, ArrayList<Point> domSet2) {
		ArrayList<Point> points = (ArrayList<Point>)points2.clone();
		ArrayList<Point> domSet = (ArrayList<Point>)domSet2.clone();
		int oldScore = domSet.size();
		System.out.println("1 - Domset : "+domSet.size()+" |Â Points : "+points.size());
		domSet = optiDomSet(points, domSet);
		System.out.println("Avant : Score : "+oldScore+"Â |Â New score : "+domSet.size());
		while(oldScore > domSet.size()) {
			oldScore = domSet.size();
			domSet = optiDomSet(points, domSet);
			System.out.println("Score : "+oldScore+"Â |Â New score : "+domSet.size());
		}
		System.out.println("AprÃ¨s : Score : "+oldScore+"Â |Â New score : "+domSet.size());
		return domSet;
	}


	public ArrayList<Point> domSetLocalSearchingNaif(ArrayList<Point> points2, ArrayList<Point> domSet2) {	  
		int cpt_if = 0, cpt_else = 0;
		ArrayList<Point> points = (ArrayList<Point>)points2.clone();
		ArrayList<Point> domSet = (ArrayList<Point>)domSet2.clone();
		for(int i = 0; i < domSet.size(); i++) {
			Point a = domSet.get(i);
			for(int j = 0; j < domSet.size(); j++) {
				Point b = domSet.get(j);
				if(a.equals(b)) continue;
				if(a.distance(b) >= 55*3) continue;
				for(int k = 0; k < points.size(); k++) {
					//System.out.println("Points size : "+points.size()+" CPT : "+cpt++);
					Point c = points.get(k);
					domSet.remove(a);
					domSet.remove(b);
					points.remove(c);
					domSet.add(c);
					points.add(a);
					points.add(b);
					System.out.println("1 - Domset : "+domSet.size()+" |Â Points : "+points.size());

					if(isValid(points, domSet)) {
						cpt_if++;
						return domSet;
					}
					else {
						cpt_else++;
						domSet.add(a);
						domSet.add(b);
						points.add(c);
						domSet.remove(c);
						points.remove(a);
						points.remove(b);
					}

				}
			}

			//System.out.println("IF : "+cpt_if+" | ELSE : "+cpt_else);
		}

		return domSet;
	}



	/**
	 * Method de vériification si le set retourné est bien un Dominant Set
	 * Un sous ensemble domSet pour lequel un point P dans points est soit dans domSet, 
	 * soit un voisin d'un sommet dans domSet
	 * @param points2
	 * @param domSet
	 * @return
	 */
	public boolean isValid(ArrayList<Point> points2, ArrayList<Point> domSet) {
		ArrayList<Point> points = (ArrayList<Point>) points2.clone();
		Evaluation eval = new Evaluation();
		for(int i = 0; i < domSet.size(); i++) {
			if(points.size() == 0) break;
			Point p = domSet.get(i);
			ArrayList<Point> voisins = eval.neighbor(domSet.get(i), points, 55);
			points.removeAll(voisins); 
			points.remove(p);
		}
		return points.size() == 0;
	}
	/**
	 * domSetGloutonMain retourne le domSet à partir d'ajout du sommet de max connectivité 
	 * tant que la solution n'est pas valide
	 * @param points2
	 * @return
	 */
	public ArrayList<Point> domSetGloutonMain(ArrayList<Point> points2){ 
		ArrayList<Point> points = (ArrayList<Point>)points2.clone();
		ArrayList<Point> domSet = new ArrayList<>();
		int i = 0;
		while(!isValid(points, domSet)) {
			domSetGlouton(points, domSet);
		}
		System.out.println("Size : "+points2.size());
		return domSet;
	}

	/**
	 * domSetGlouton trouve le sommet le plus connecté dans points et l'ajoute au domSet
	 * @param points
	 * @param domSet
	 */
	public void domSetGlouton(ArrayList<Point> points, ArrayList<Point> domSet) {
		int max = 0;
		Point p_max = points.get(0);
		for(int i = 0; i < points.size(); i++) {
			int tmp = 0;
			for(int j = 0; j < points.size(); j++) {
				if(points.get(i).equals(points.get(j))) continue;
				if(points.get(i).distance(points.get(j)) < 55) {
					tmp++;
				}
			}
			if(tmp > max) {
				max = tmp;
				p_max = points.get(i);
			}
		}
		domSet.add(p_max);
		points.remove(p_max);
		Evaluation eval = new Evaluation();
		points.removeAll(eval.neighbor(p_max, points, 55));
	}



	//FILE PRINTER
	private void saveToFile(String filename,ArrayList<Point> result){
		int index=0;
		try {
			while(true){
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			printToFile(filename+Integer.toString(index)+".points",result);
		}
	}
	private void printToFile(String filename,ArrayList<Point> points){
		try {
			PrintStream output = new PrintStream(new FileOutputStream(filename));
			int x,y;
			for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("I/O exception: unable to create "+filename);
		}
	}

	//FILE LOADER
	private ArrayList<Point> readFromFile(String filename) {
		String line;
		String[] coordinates;
		ArrayList<Point> points=new ArrayList<Point>();
		try {
			BufferedReader input = new BufferedReader(
					new InputStreamReader(new FileInputStream(filename))
					);
			try {
				while ((line=input.readLine())!=null) {
					coordinates=line.split("\\s+");
					points.add(new Point(Integer.parseInt(coordinates[0]),
							Integer.parseInt(coordinates[1])));
				}
			} catch (IOException e) {
				System.err.println("Exception: interrupted I/O.");
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close "+filename);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found.");
		}
		return points;
	}
}
