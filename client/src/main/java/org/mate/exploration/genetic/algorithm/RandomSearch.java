package org.mate.exploration.genetic.algorithm;

import static org.mate.MATE.getFormattedDate;

import org.mate.Properties;
import org.mate.commons.utils.MATELog;
import org.mate.exploration.genetic.chromosome.IChromosome;
import org.mate.exploration.genetic.chromosome_factory.IChromosomeFactory;
import org.mate.exploration.genetic.core.GeneticAlgorithm;
import org.mate.exploration.genetic.fitness.IFitnessFunction;
import org.mate.exploration.genetic.termination.ITerminationCondition;
import org.mate.utils.coverage.Coverage;
import org.mate.utils.coverage.CoverageDTO;
import org.mate.utils.coverage.CoverageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Provides an implementation of the random search algorithm. In random search, a random chromosome
 * is initially sampled. Then, in the evolution process, another random chromosome is sampled
 * and the one with the better fitness is kept while the other one is discarded.
 *
 * @param <T> The type of the chromosomes.
 */
public class RandomSearch<T> extends GeneticAlgorithm<T> {

    /**
     * The best combined fitness achieved during exploration.
     * This value is updated in the {@link RandomSearch#logCurrentFitness} method.
     */
    private double bestFitness;

    /**
     * A list of the historical individuals that improved fitness during exploration.
     */
    protected List<IChromosome<T>> finalPopulation = new ArrayList<>();

    /**
     * Initialises the random search algorithm with the necessary attributes.
     *
     * @param chromosomeFactory The used chromosome factory.
     * @param fitnessFunctions The used fitness functions. Only a single fitness function is used here.
     * @param terminationCondition The used termination condition.
     */
    public RandomSearch(IChromosomeFactory<T> chromosomeFactory,
                        List<IFitnessFunction<T>> fitnessFunctions,
                        ITerminationCondition terminationCondition) {
        super(chromosomeFactory,
                null,
                null,
                null,
                fitnessFunctions,
                terminationCondition,
                1,
                2,
                0,
                0);
        bestFitness = fitnessFunctions.get(0).isMaximizing() ? Double.NEGATIVE_INFINITY :
                Double.POSITIVE_INFINITY;
    }

    @Override
    public void createInitialPopulation() {
        super.createInitialPopulation();
        finalPopulation.addAll(population);
    }

    /**
     * In the evolve step of random search, a second random chromosome is created and the one
     * with the better fitness value is kept in the population.
     */
    @Override
    public void evolve() {
        MATELog.log_acc(String.format(Locale.US, "Creating population #%d (%s)",
                currentGenerationNumber + 1, getFormattedDate()));
        double previousBestFitness = bestFitness;

        // Add temporary a second random chromosome.
        population.add(chromosomeFactory.createChromosome());

        // Discard old chromosome if not better than new one.
        IFitnessFunction<T> fitnessFunction = fitnessFunctions.get(0);
        double compared = fitnessFunction.getNormalizedFitness(population.get(0))
                - fitnessFunction.getNormalizedFitness(population.get(1));

        logCurrentFitness();
        boolean fitnessImproved = false;

        if (fitnessFunction.isMaximizing()) {
            population.remove(compared > 0 ? 1 : 0);
            if (bestFitness > previousBestFitness) {
                fitnessImproved = true;
            }
        } else {
            population.remove(compared < 0 ? 1 : 0);
            if (bestFitness < previousBestFitness) {
                fitnessImproved = true;
            }
        }

        currentGenerationNumber++;

        if (fitnessImproved) {
            // The new individual in this generation improved the best fitness so far.
            // We add it to the final population
            MATELog.log_acc("RandomSearch best fitness improved from " +
                    previousBestFitness + " to " + bestFitness);
            finalPopulation.addAll(population);
        }
    }

    /**
     * Logs the fitness of the chromosomes in the current population.
     */
    @Override
    protected void logCurrentFitness() {

        for (int i = 0; i < Math.min(fitnessFunctions.size(), 5); i++) {
            MATELog.log_acc("Fitness function " + (i + 1) + ":");
            IFitnessFunction<T> fitnessFunction = fitnessFunctions.get(i);
            for (int j = 0; j < population.size(); j++) {
                IChromosome<T> chromosome = population.get(j);
                MATELog.log_acc("Chromosome " + (j + 1) + " Fitness: "
                        + fitnessFunction.getNormalizedFitness(chromosome));

                if (Properties.COVERAGE() != Coverage.NO_COVERAGE
                        && Properties.COVERAGE() != Coverage.ACTIVITY_COVERAGE) {
                    MATELog.log_acc("Chromosome " + (j + 1) + " Coverage: "
                            + CoverageUtils.getCoverage(Properties.COVERAGE(),
                            chromosome));
                }
            }
        }

        if (Properties.COVERAGE() != Coverage.NO_COVERAGE) {
            CoverageDTO combinedCoverageResult = CoverageUtils.getCombinedCoverage(Properties.COVERAGE());
            MATELog.log_acc("Accumulated Coverage: " + combinedCoverageResult);

            double combinedCoverage = combinedCoverageResult.getCoverage(Properties.COVERAGE());
            if (fitnessFunctions.get(0).isMaximizing()) {
                bestFitness = Math.max(bestFitness, combinedCoverage);
            } else {
                bestFitness = Math.min(bestFitness, combinedCoverage);
            }
        }
    }

    @Override
    public List<IChromosome<T>> getCurrentPopulation() {
        return finalPopulation;
    }
}
