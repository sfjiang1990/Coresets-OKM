import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import coreset.Coreset;
import okm.OKMSolver;
import okm.Point;
import okm.Rand;
import okm.WeightedDouble;
import okm.WeightedPoint;

public class Main
{
	private ArrayList<WeightedPoint> downSample(ArrayList<WeightedPoint> l, double prob)
	{
		ArrayList<WeightedPoint> res = new ArrayList<WeightedPoint>();
		for (WeightedPoint p : l)
		{
			if (Rand.getRand().nextDouble() <= prob)
			{
				res.add(p);
			}
		}
		return res;
	}
	public ArrayList<WeightedPoint> input(String filename) throws Exception
	{
		GZIPInputStream instream = new GZIPInputStream(new FileInputStream(new File("data/" + filename)));
		Scanner scan = new Scanner(instream);
		ArrayList<WeightedPoint> okm = new ArrayList<WeightedPoint>();
		while (scan.hasNext())
		{
			String line = scan.nextLine();
			String[] l = line.split(",");
			okm.add(new WeightedPoint(new Point(Double.parseDouble(l[0]), Double.parseDouble(l[1])), 1));
		}
		scan.close();
		return okm;
	}
	
	private static void evaluateError(ArrayList<WeightedPoint> instance,
			int k, int cases) throws Exception
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("data/evaluate_accuracy_k2_p01n.csv"))));
		out.println("eps,error,size,Tobj,Tcor");

		int npoint = WeightedPoint.totalWeight(instance);
		int p = npoint / 10;
		double[] epslist = new double[] { 0.5, 0.3, 0.2, 0.1, 0.05 };
		Coreset coreset = new Coreset(instance);
		WeightedDouble[] w = WeightedDouble.getWeightVector(p, npoint);
		for (double eps : epslist)
		{
			ArrayList<WeightedPoint> cs = coreset.getCoreset(eps, k, p);
			Object[] tmp = evaluateError(instance, cs, w, k, cases);
			long sumTobj = (Long)tmp[0];
			long sumTcor = (Long)tmp[1];
			double error = (Double)tmp[2];
			out.printf("%.6f,%.6f,%d,%d,%d\n", eps, error, cs.size(), sumTobj, sumTcor);
		}

		out.close();
	}
	
	private static Object[] evaluateError(ArrayList<WeightedPoint> instance,
			ArrayList<WeightedPoint> cs, WeightedDouble[] w, int k, int cases)
	{
		System.out.println(cs.size());
		double x1, y1, x2, y2;
		x1 = x2 = instance.get(0).data.x;
		y1 = y2 = instance.get(0).data.y;
		for (WeightedPoint wp : instance)
		{
			Point p = wp.data;
			x1 = Math.min(x1, p.x);
			y1 = Math.min(y1, p.y);
			x2 = Math.max(x2, p.x);
			y2 = Math.max(y2, p.y);
		}
		double error = -1;
		long sumTobj = 0;
		long sumTcor = 0;
		ArrayList<ArrayList<Point>> clist = new ArrayList<ArrayList<Point>>();
		for (int t = 0; t < cases; t++)
		{
			ArrayList<Point> center = new ArrayList<Point>();
			for (int i = 0; i < k; i++)
			{
				double x = x1 + Rand.getRand().nextDouble() * (x2 - x1);
				double y = y1 + Rand.getRand().nextDouble() * (y2 - y1);
				center.add(new Point(x, y));
			}
			clist.add(center);
		}
		
		long t0 = System.currentTimeMillis();
		ArrayList<Double> objlist = new ArrayList<Double>();
		for (ArrayList<Point> center : clist)
		{
			objlist.add(WeightedPoint.cost(instance, center, w));
		}
		sumTobj = System.currentTimeMillis() - t0;
		
		t0 = System.currentTimeMillis();
		ArrayList<Double> corlist = new ArrayList<Double>();
		for (ArrayList<Point> center : clist)
		{
			corlist.add(WeightedPoint.cost(cs, center, w));
		}
		sumTcor = System.currentTimeMillis() - t0;
		
		for (int i = 0; i < cases; i++)
		{
			double ratio = corlist.get(i) / objlist.get(i);
			error = Math.max(error, Math.abs(ratio - 1));
		}
		
		return new Object[] {sumTobj, sumTcor, error};
	}
	
	private static void evaluateSolver(String file, ArrayList<WeightedPoint> instance, WeightedDouble[] w, int k) throws Exception
	{
		OKMSolver solver = new OKMSolver(instance);
		ArrayList<Double> res = solver.samplingForEvaluation(w, k, 50);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("data/evaluate_solver_" + file + ".csv"))));
		for (int i = 0; i < res.size(); i++)
		{
			out.println((i+1) + "," + res.get(i));
		}
		out.close();
	}
	
	private static void evaluateP(ArrayList<WeightedPoint> instance, int cases) throws Exception
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("data/evaluate_p.csv"))));

		int npoint = WeightedPoint.totalWeight(instance);
		int[] plist = new int[] {npoint / 100, npoint / 20, npoint / 10 * 2, npoint / 10 * 3, npoint /10 * 4, npoint / 2, npoint};
		String[] name = new String[] {"0.01n", "0.05n", "0.2n", "0.3n", "0.4n", "0.5n", "n"};
		double eps = 0.1;
		int k = 2;
		Coreset coreset = new Coreset(instance);
		WeightedDouble[] w = WeightedDouble.getWeightVector(npoint / 10, npoint);
		ArrayList<WeightedPoint> cs = coreset.getCoreset(eps, k, npoint / 10);
		for (int i = 0; i < plist.length; i++)
		{
			int p = plist[i];
			w = WeightedDouble.getWeightVector(p, npoint);
			double err = (Double)evaluateError(instance, cs, w, k, cases)[2];
			out.printf("%s,%.6f\n", name[i], err);
		}

		out.close();
	}
	
	private static void evaluateWeighted(ArrayList<WeightedPoint> instance, int cases) throws Exception
	{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("data/evaluate_weighted.csv"))));
		
		int npoint = WeightedPoint.totalWeight(instance);
		double[] alphalist = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.2, 1.5};
		double eps = 0.1;
		int k = 2;
		int p = npoint / 10;
		Coreset coreset = new Coreset(instance);
		ArrayList<WeightedPoint> cs = coreset.getCoreset(eps, k, p);
		for (Double alpha : alphalist)
		{
			WeightedDouble[] w = WeightedDouble.getPowerLawVector(alpha, npoint);
			double err = (Double)evaluateError(instance, cs, w, k, cases)[2];
			out.printf("%f,%.6f\n", alpha, err);
		}
		
		out.close();
	}
	
	public void run() throws Exception
	{
		ArrayList<WeightedPoint> instance = downSample(input("data.csv.gz"), 1.0);
		int k = 2;
		int npoint = WeightedPoint.totalWeight(instance);

		evaluateSolver("10p", instance, WeightedDouble.getWeightVector(npoint / 10, npoint), k);
		evaluateSolver("100p", instance, WeightedDouble.getWeightVector(npoint, npoint), k);

		evaluateError(instance, k, 100);
		evaluateP(instance, 100);
		evaluateWeighted(instance, 100);
	}

	public static void main(String[] args) throws Exception
	{
		new Main().run();
	}
}
