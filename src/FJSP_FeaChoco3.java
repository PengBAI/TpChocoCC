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
		// Déclaration du Solver
		Solver FJSPSolv = new Solver("FJSP");

		// Déclaration des variables
		// A[i]: numéro de l'agent auquel est affectée lâche i. 
		IntVar[] A = VF.enumeratedArray("Tâche", n, 0, m - 1, FJSPSolv);

		IntVar[] tddt = new IntVar[n];
		IntVar[] tdft = new IntVar[n];
		for (int i = 0; i < n; i++) {
			tddt[i] = VF.fixed(ddt[i], FJSPSolv);
			tdft[i] = VF.fixed(dft[i], FJSPSolv);
		}
		
		// Déclaration des contraintes
		for (int i = 0; i < n; i++) {
			// pour différentes tâches sur un même agent 
			for (int j = 0; j < n; j++) {
				if (i != j) {
					FJSPSolv.post(LCF.ifThen(
							ICF.arithm(A[i], "=", A[j]),
							LCF.or(ICF.arithm(tdft[j], "<=", tddt[i]),		// soit dft[j] <= ddt[i]
									ICF.arithm(tdft[i], "<=", tddt[j]))));  // soit dft[i] <= ddt[j]
				}
			}
		}

		// Stratégie
		AbstractStrategy[] as = { ISF.lexico_LB(A) };
		FJSPSolv.set(as);

		// Résolution
		int cpt = 0;
		if (FJSPSolv.findSolution()) {

			System.out.println("Solution " + cpt++ + ", m = " + m);
			System.out.println("----------------");
			for (int i = 0; i < n; i++) {
				System.out.println(A[i].getName() + " " + A[i].getValue());
			}
			System.out.println(FJSPSolv.getMeasures().getSolutionCount()
					+ " solution(s) trouvée(s) en "
					+ FJSPSolv.getMeasures().getTimeCount() + " secondes");
		} else {
			System.out.println("Pas de solution");
		}

	}

	/**
	 * Fixed Jobs Scheduling Problem with Spead Constraint (FJSPwSC)
	 */
	public static void FJSPwSC() {
		// Déclaration du Solver
		Solver FJSPwSCSolv = new Solver("FJSPwSC");

		// Déclaration des variables
		// A[i]: numéro de l'agent auquel est affectée la tâche i.
		IntVar[] A = VF.enumeratedArray("Tâche", n, 0, m - 1, FJSPwSCSolv);

		IntVar[] tddt = new IntVar[n];
		IntVar[] tdft = new IntVar[n];
		for (int i = 0; i < n; i++) {
			tddt[i] = VF.fixed(ddt[i], FJSPwSCSolv);
			tdft[i] = VF.fixed(dft[i], FJSPwSCSolv);
		}
		// dta[k]: date de début de travail sur l'agent k
		IntVar[] dta = VF.enumeratedArray("début", m, 360, 1320, FJSPwSCSolv);
		
		// Déclaration des contraintes
		for (int i = 0; i < n; i++) {
			// pour différentes tâches sur un m�me agent 
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

		// Stratégie
		AbstractStrategy[] as = { ISF.lexico_LB(A) };
		FJSPwSCSolv.set(as);

		// Résolution
		int cpt = 0;
		if (FJSPwSCSolv.findSolution()) {

			System.out.println("Solution " + cpt++ + ", m = " + m);
			System.out.println("----------------");
			for (int i = 0; i < n; i++) {
				System.out.println(A[i].getName() + " " + A[i].getValue());
			}
			System.out.println(FJSPwSCSolv.getMeasures().getSolutionCount()
					+ " solution(s) trouvée(s) en "
					+ FJSPwSCSolv.getMeasures().getTimeCount() + " secondes");
		} else {
			System.out.println("Pas de solution");
		}
	}

	/**
	 * Prise en compte de pause (FJSPwSCaP)
	 * */
	public static void FJSPwSCaP() {
		// Déclaration du Solver
		Solver FJSPwSCaPSolv = new Solver("FJSPwSCaP");

		// Déclaration des variables
		// A[i]: numéro de l'agent auquel est affectée lâche i.
		IntVar[] A = VF.enumeratedArray("Tâche", n, 0, m - 1, FJSPwSCaPSolv);

		IntVar[] tddt = new IntVar[n];
		IntVar[] tdft = new IntVar[n];
		for (int i = 0; i < n; i++) {
			tddt[i] = VF.fixed(ddt[i], FJSPwSCaPSolv);
			tdft[i] = VF.fixed(dft[i], FJSPwSCaPSolv);
		}
		// dta[k]: date de début de travail de l'agent k
		IntVar[] dta = VF.enumeratedArray("début", m, 360, 1320, FJSPwSCaPSolv);
		// dpa[k]: date de début de pause de l'agent k
		IntVar[] dpa = VF.enumeratedArray("pause", m, 360, 1320, FJSPwSCaPSolv);

		// Déclaration des contraintes
		for (int i = 0; i < n; i++) {
			// pour différentes tâches sur un même agent 
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
						LCF.and(ICF.arithm(dta[k], "<=", tddt[i]),              // dta[k] <= ddt de toute les tâches
								ICF.arithm(dta[k], ">=", tdft[i], "-", 480)))); // dta[k] + 480 >= dft de toute les tâches
				
				// la pause ne doit pas dans la durée de tâche
				FJSPwSCaPSolv.post(LCF.ifThen(
						ICF.arithm(A[i], "=", k),
						LCF.or(ICF.arithm(dpa[k], "<=", tddt[i], "-", 30), // soit dpa + 30 <= ddt
								ICF.arithm(dpa[k], ">=", tdft[i]))));      // soit dpa >= dft
			}
		}
		
		// sur le même agent k, les contraintes entre début de travail agent(dta) et début de pause agent(dpa)
		for (int k = 0; k < m; k++) {
			FJSPwSCaPSolv.post(LCF.and(
					ICF.arithm(dpa[k], "-", dta[k], "<=", 360), // dpa - dta <= 360
					ICF.arithm(dpa[k],"-" , dta[k],">=", 90))); // dta + 480 - (dpa + 30) <= 360 
		}

		// Stratégie
		AbstractStrategy[] as = { ISF.lexico_LB(A) };
		FJSPwSCaPSolv.set(as);

		// Résolution
		int cpt = 0;
		if (FJSPwSCaPSolv.findSolution()) {

			System.out.println("Solution " + cpt++ + ", m = " + m);
			System.out.println("----------------");
			for (int i = 0; i < n; i++) {
				System.out.println(A[i].getName() + " " + A[i].getValue());
			}
			System.out.println(FJSPwSCaPSolv.getMeasures().getSolutionCount()
					+ " solution(s) trouvée(s) en "
					+ FJSPwSCaPSolv.getMeasures().getTimeCount() + " secondes");
		} else {
			System.out.println("Pas de solution");
		}
	}
}
