package jmetal;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

public class Pareto extends Problem {

    public Pareto(String solutionType, int nombres) {
        super.numberOfObjectives_ = 2;
        super.numberOfConstraints_ = 0;
        super.numberOfVariables_ = nombres;
        super.problemName_ = "NSGA-II";

        if (solutionType.compareTo("Real") == 0) {
            this.solutionType_ = new RealSolutionType(this);
        } else {
            System.out.println("Error");
            System.exit(-1);
        }

        super.lowerLimit_ = new double[numberOfVariables_];
        super.upperLimit_ = new double[numberOfVariables_];

        for (int i = 0 ; i < numberOfVariables_ ; i++) {
            super.lowerLimit_[i] = 0.0;
            super.upperLimit_[i] = 1.0;
        }

    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        Variable[] decisionVariables = solution.getDecisionVariables();

        double [] x = new double[numberOfVariables_];

        for (int i = 1 ; i < numberOfVariables_ ; i++) {
            x[i] = decisionVariables[i].getValue();
        }

        double f1 = x[0];
        double f2 = 0.0;
        double g = 0;

        for (int i = 1 ; i < numberOfVariables_ ; i++) {
            g += x[i];
        }

        g = 1 + (9 * g / (numberOfVariables_ - 1));
        f2 = g * (1 - Math.sqrt(f1/g));

        solution.setObjective(0, f1);
        solution.setObjective(1, f2);
    }
}
