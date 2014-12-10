import solver.Solver;
import solver.constraints.ICF;
import solver.constraints.LCF;
import solver.search.strategy.ISF;
import solver.search.strategy.strategy.AbstractStrategy;
import solver.variables.IntVar;
import solver.variables.VF;

public class FJSP_FeaChoco3 {

	/**
	 * @param args
	 */
	static int n;
	// le nombre de l'agent qu'on a definit
	static int m = 35;
	static int[] ddt, dft;

	public static void main(String[] args) {

		LecteurFichierFJSP MonLecteurDeFichier = new LecteurFichierFJSP(
				"../DonneesFJSP/Donnees Fixed Job 6.txt");
		MonLecteurDeFichier.convert();

		n = MonLecteurDeFichier.GetNbJobs();
		ddt = MonLecteurDeFichier.GetDateDeDebutJobs();
		dft = MonLecteurDeFichier.GetDateDeFinJobs();

		System.out.println("------------------------------------------------");
		System.out.println("n= " + n);
		for (int j = 0; j < n; j++)
			System.out.println("dddt[" + j + "]= " + ddt[j] + " " + "ddf[" + j
					+ "]= " + dft[j]);
		System.out.println("m= " + m);
		System.out.println("------------------------------------------------");

		System.out.println("FJSP:");
		FJSP();
		System.out.println("------------------------------------------------");
		System.out.println("FJSPwSC:");
		FJSPwSC();
		System.out.println("------------------------------------------------");
		System.out.println("FJSPwSCaP:");
		FJSPwSCaP();

	}

	/**
	 * Fixed Jobs Scheduling Problem (FJSP)
	 */
	public static void FJSP() {
		// D�claration du Solver
		Solver FJSPSolv = new Solver("FJSP");

		// D�claration des variables
		// A[i]: num�ro de l�agent auquel est affect�e la t�che i. 
		IntVar[] A = VF.enumeratedArray("T�che", n, 0, m - 1, FJSPSolv);

		IntVar[] tddt = new IntVar[n];
		IntVar[] tdft = new IntVar[n];
		for (int i = 0; i < n; i++) {
			tddt[i] = VF.fixed(ddt[i], FJSPSolv);
			tdft[i] = VF.fixed(dft[i], FJSPSolv);
		}
		
		// D�claration des contraintes
		for (int i = 0; i < n; i++) {
			// pour diff�rentes t�ches sur un m�me agent 
			for (int j = 0; j < n; j++) {
				if (i != j) {
					FJSPSolv.post(LCF.ifThen(
							ICF.arithm(A[i], "=", A[j]),
							LCF.or(ICF.arithm(tdft[j], "<=", tddt[i]),		// soit dft[j] <= ddt[i]
									ICF.arithm(tdft[i], "<=", tddt[j]))));  // soit dft[i] <= ddt[j]
				}
			}
		}

		// Strat�gie
		AbstractStrategy[] as = { ISF.lexico_LB(A) };
		FJSPSolv.set(as);

		// R�solution
		int cpt = 0;
		if (FJSPSolv.findSolution()) {

			System.out.println("Solution " + cpt++ + ", m = " + m);
			System.out.println("----------------");
			for (int i = 0; i < n; i++) {
				System.out.println(A[i].getName() + " " + A[i].getValue());
			}
			System.out.println(FJSPSolv.getMeasures().getSolutionCount()
					+ " solution(s) trouv�e(s) en "
					+ FJSPSolv.getMeasures().getTimeCount() + " secondes");
		} else {
			System.out.println("Pas de solution");
		}

	}

	/**
	 * Fixed Jobs Scheduling Problem with Spead Constraint (FJSPwSC)
	 */
	public static void FJSPwSC() {
		// D�claration du Solver
		Solver FJSPwSCSolv = new Solver("FJSPwSC");

		// D�claration des variables
		// A[i]: num�ro de l�agent auquel est affect�e la t�che i.
		IntVar[] A = VF.enumeratedArray("T�che", n, 0, m - 1, FJSPwSCSolv);

		IntVar[] tddt = new IntVar[n];
		IntVar[] tdft = new IntVar[n];
		for (int i = 0; i < n; i++) {
			tddt[i] = VF.fixed(ddt[i], FJSPwSCSolv);
			tdft[i] = VF.fixed(dft[i], FJSPwSCSolv);
		}
		// dta[k]: date de d�but de travail sur l�agent k
		IntVar[] dta = VF.enumeratedArray("d�but", m, 360, 1320, FJSPwSCSolv);
		
		// D�claration des contraintes
		for (int i = 0; i < n; i++) {
			// pour diff�rentes t�ches sur un m�me agent 
			for (int j = 0; j < n; j++) {
				if (i != j) {
					FJSPwSCSolv.post(LCF.ifThen(
							ICF.arithm(A[i], "=", A[j]),
							LCF.or(ICF.arithm(tdft[j], "<=", tddt[i]),       // soit dft[j] <= ddt[i]
									ICF.arithm(tdft[i], "<=", tddt[j]))));   // soit dft[i] <= ddt[j]
				}
			}
			for (int k = 0; k < m; k++) {
				// un agent travaille au maximum 8 heures
				FJSPwSCSolv.post(LCF.ifThen(
						ICF.arithm(A[i], "=", k),
						LCF.and(ICF.arithm(dta[k], "<=", tddt[i]),              // dta[k] <= ddt de toute les t�ches
								ICF.arithm(dta[k], ">=", tdft[i], "-", 480)))); // dta[k] + 480 >= dft de toute les t�ches
			}
		}

		// Strat�gie
		AbstractStrategy[] as = { ISF.lexico_LB(A) };
		FJSPwSCSolv.set(as);

		// R�solution
		int cpt = 0;
		if (FJSPwSCSolv.findSolution()) {

			System.out.println("Solution " + cpt++ + ", m = " + m);
			System.out.println("----------------");
			for (int i = 0; i < n; i++) {
				System.out.println(A[i].getName() + " " + A[i].getValue());
			}
			System.out.println(FJSPwSCSolv.getMeasures().getSolutionCount()
					+ " solution(s) trouv�e(s) en "
					+ FJSPwSCSolv.getMeasures().getTimeCount() + " secondes");
		} else {
			System.out.println("Pas de solution");
		}
	}

	/**
	 * Prise en compte de pause (FJSPwSCaP)
	 * */
	public static void FJSPwSCaP() {
		// D�claration du Solver
		Solver FJSPwSCaPSolv = new Solver("FJSPwSCaP");

		// D�claration des variables
		// A[i]: num�ro de l�agent auquel est affect�e la t�che i.
		IntVar[] A = VF.enumeratedArray("T�che", n, 0, m - 1, FJSPwSCaPSolv);

		IntVar[] tddt = new IntVar[n];
		IntVar[] tdft = new IntVar[n];
		for (int i = 0; i < n; i++) {
			tddt[i] = VF.fixed(ddt[i], FJSPwSCaPSolv);
			tdft[i] = VF.fixed(dft[i], FJSPwSCaPSolv);
		}
		// dta[k]: date de d�but de travail de l�agent k
		IntVar[] dta = VF.enumeratedArray("d�but", m, 360, 1320, FJSPwSCaPSolv);
		// dpa[k]: date de d�but de pause de l'agent k
		IntVar[] dpa = VF.enumeratedArray("pause", m, 360, 1320, FJSPwSCaPSolv);

		// D�claration des contraintes
		for (int i = 0; i < n; i++) {
			// pour diff�rentes t�ches sur un m�me agent 
			for (int j = 0; j < n; j++) {
				if (i != j) {
					FJSPwSCaPSolv.post(LCF.ifThen(
							ICF.arithm(A[i], "=", A[j]),
							LCF.or(ICF.arithm(tdft[j], "<=", tddt[i]),       // soit dft[j] <= ddt[i]
									ICF.arithm(tdft[i], "<=", tddt[j]))));   // soit dft[i] <= ddt[j]
				}
			}
			
			for (int k = 0; k < m; k++) {
				// un agent travaille au maximum 8 heures
				FJSPwSCaPSolv.post(LCF.ifThen(
						ICF.arithm(A[i], "=", k),
						LCF.and(ICF.arithm(dta[k], "<=", tddt[i]),              // dta[k] <= ddt de toute les t�ches
								ICF.arithm(dta[k], ">=", tdft[i], "-", 480)))); // dta[k] + 480 >= dft de toute les t�ches
				
				// la pause ne doit pas dans la dur�e de t�che
				FJSPwSCaPSolv.post(LCF.ifThen(
						ICF.arithm(A[i], "=", k),
						LCF.or(ICF.arithm(dpa[k], "<=", tddt[i], "-", 30), // soit dpa + 30 <= ddt
								ICF.arithm(dpa[k], ">=", tdft[i]))));      // soit dpa >= dft
			}
		}
		
		// sur le m�me agent k, les contraintes entre d�but de travail agent(dta) et d�but de pause agent(dpa)
		for (int k = 0; k < m; k++) {
			FJSPwSCaPSolv.post(LCF.and(
					ICF.arithm(dpa[k], "-", dta[k], "<=", 360), // dpa - dta <= 360
					ICF.arithm(dpa[k],"-" , dta[k],">=", 90))); // dta + 480 - (dpa + 30) <= 360 
		}

		// Strat�gie
		AbstractStrategy[] as = { ISF.lexico_LB(A) };
		FJSPwSCaPSolv.set(as);

		// R�solution
		int cpt = 0;
		if (FJSPwSCaPSolv.findSolution()) {

			System.out.println("Solution " + cpt++ + ", m = " + m);
			System.out.println("----------------");
			for (int i = 0; i < n; i++) {
				System.out.println(A[i].getName() + " " + A[i].getValue());
			}
			System.out.println(FJSPwSCaPSolv.getMeasures().getSolutionCount()
					+ " solution(s) trouv�e(s) en "
					+ FJSPwSCaPSolv.getMeasures().getTimeCount() + " secondes");
		} else {
			System.out.println("Pas de solution");
		}
	}
}
