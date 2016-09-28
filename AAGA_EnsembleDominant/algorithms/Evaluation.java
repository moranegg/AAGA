package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class Evaluation {

	public ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold){
		ArrayList<Point> result = new ArrayList<Point>();

		for (Point point:vertices) if (point.distance(p)<edgeThreshold && !point.equals(p)) result.add(point);

		return result;
	}
}
