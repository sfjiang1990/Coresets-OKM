package coreset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import okm.OKMSolver;
import okm.Point;
import okm.Segment;
import okm.WeightedDouble;
import okm.WeightedPoint;

public class Coreset
{
	ArrayList<WeightedPoint> instance;
	public Coreset(ArrayList<WeightedPoint> instance)
	{
		this.instance = instance;
	}
	
	private ArrayList<Point> getCenters(double eps, int k, ArrayList<Integer> p)
	{
		ArrayList<Point> res = new ArrayList<Point>();
		OKMSolver solver = new OKMSolver(this.instance);
		int npoint = WeightedPoint.totalWeight(this.instance);
		for (Integer t : p)
		{
			res.addAll(solver.sampling(WeightedDouble.getWeightVector(t, npoint), k));
		}
		return res;
	}
	
	private ArrayList<ArrayList<WeightedPoint>> getClusters(ArrayList<Point> centers)
	{
		int k = centers.size();
		ArrayList<ArrayList<WeightedPoint>> res = new ArrayList<ArrayList<WeightedPoint>>();
		for (int i = 0; i < centers.size(); i++)
		{
			res.add(new ArrayList<WeightedPoint>());
		}
		for (int i = 0; i < this.instance.size(); i++)
		{
			Point p = this.instance.get(i).data;
			double min = -1;
			int minid = -1;
			for (int j = 0; j < k; j++)
			{
				double d = Point.dist(centers.get(j), p);
				if (min == -1 || min > d)
				{
					min = d;
					minid = j;
				}
			}
			res.get(minid).add(this.instance.get(i));
		}
		return res;
	}
	
	private ArrayList<ArrayList<Coreset1D>> to1D(ArrayList<Point> centers, ArrayList<ArrayList<WeightedPoint>> clusters, double eps)
	{
		ArrayList<ArrayList<Coreset1D>> res = new ArrayList<ArrayList<Coreset1D>>();
		for (int i = 0; i < centers.size(); i++)
		{
			res.add(to1D(centers.get(i), clusters.get(i), eps));
		}
		return res;
	}
	
	private ArrayList<Coreset1D> to1D(Point center, ArrayList<WeightedPoint> cluster, double eps)
	{
		Collections.sort(cluster, new Comparator<WeightedPoint>(){
			@Override
			public int compare(WeightedPoint o1, WeightedPoint o2)
			{
				return Double.compare(o1.data.atan2(center), o2.data.atan2(center));
			}
			
		});

		ArrayList<Coreset1D> res = new ArrayList<Coreset1D>();
		int len = (int)(1.0 / eps * 2); // constant?
		double unit = 2 * Math.PI / len;
		for (int i = 0, j = 0; i < len && j < cluster.size(); i++)
		{
			double rad = -Math.PI + unit * i;
			int start = j;
			for (;j < cluster.size() && (cluster.get(j).data.atan2(center) <= rad + unit || i == len - 1); j++)
			{
			}
			int end = j;
			ArrayList<WeightedPoint> tmp = new ArrayList<WeightedPoint>();
			for (int k = start; k < end; k++)
			{
				tmp.add(cluster.get(k));
			}
			if (tmp.size() == 0)
				continue;
			res.add(new Coreset1D(tmp, center, new Point(Math.cos(rad), Math.sin(rad))));
		}
		return res;
	}
	
	private ArrayList<WeightedPoint> projectedPoints(ArrayList<ArrayList<Coreset1D>> oned)
	{
		ArrayList<WeightedPoint> res = new ArrayList<WeightedPoint>();
		for (ArrayList<Coreset1D> i : oned)
		{
			for (Coreset1D j : i)
			{
				res.addAll(j.getSegment().toPoints());
			}
		}
		return res;
	}
	
	private HashSet<WeightedPoint> getLightSet(ArrayList<WeightedPoint> pointSet, ArrayList<Point> center, int p)
	{
		HashSet<WeightedPoint> S = new HashSet<WeightedPoint>();
		WeightedPoint.sortByDist(pointSet, center);
		System.out.println("sorted");
		for (int i = 0; i < pointSet.size() - p; i++)
		{
			S.add(pointSet.get(i));
		}
		/*for (int i = 0; i < p; i++)
		{
			S.add(pointSet.get(pointSet.size() - 1 - i));
		}*/
		return S;
	}
	
	public ArrayList<WeightedPoint> getCoreset(double eps, int k, int p)
	{
		ArrayList<WeightedPoint> res = new ArrayList<WeightedPoint>();

		ArrayList<Integer> pList = new ArrayList<Integer>();
		pList.add(p);
		ArrayList<Point> C = getCenters(eps, k, pList);
		ArrayList<ArrayList<WeightedPoint>> clusters = this.getClusters(C);
		
		ArrayList<ArrayList<Coreset1D>> onedList = this.to1D(C, clusters, eps);
		ArrayList<WeightedPoint> projected = this.projectedPoints(onedList);

		int npoint = WeightedPoint.totalWeight(projected);
		System.out.println(npoint);
		System.out.println(WeightedPoint.totalWeight(instance));
		double opt = WeightedPoint.cost(projected, C, WeightedDouble.getWeightVector(p, npoint));

		HashSet<WeightedPoint> S = getLightSet(projected, C, p);
		
		for (ArrayList<Coreset1D> tmp : onedList)
		{
			for (Coreset1D oned : tmp)
			{
				res.addAll(Segment.meanPoints(oned.getCoreset(eps, k, p, S, opt, C)));
			}
		}
		
		return res;
	}
}
