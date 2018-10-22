/**
 *  Genetic Algorithm solution for a knapsack problem
 *  @version 1.0
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GA {

    Scanner scanner = new Scanner(System.in);

    private final int    population_size          = 1000;
    private final int    maximum_generations      = 500;
    private final double probability_of_crossover = 0.7;
    private final double probability_of_mutation  = 0.001;

    private int    number_of_items   = 0;
    private double knapsack_capacity = 0;

    private List<Double> value_of_items       = new ArrayList<>();
    private List<Double> weight_of_items      = new ArrayList<>();

    private List<String> population           = new ArrayList<>();
    private List<Double> fitness              = new ArrayList<>();
    private List<Double> cumulative_Fitnesses = new ArrayList<>();

    public static void main(String[] args) { new GA(); }

    private GA() {

        getUserInput();
        init();
        initPopulation();
        evaluatePopulationFitness();
        generations();
    }

    private void getUserInput() {

        System.out.println("Enter the number of items: ");
        number_of_items = scanner.nextInt();

        System.out.println("Enter the knapsack capacity: ");
        knapsack_capacity = scanner.nextInt();

        System.out.println("Enter the wight value ");
        for (int i = 0; i < number_of_items; i++) {

            weight_of_items.add(scanner.nextDouble());
            value_of_items.add(scanner.nextDouble());
        }
    }

    private void init(){

        for(int i=0; i< population_size; ++i){
            fitness.add(0.0);
        }
    }

    private void generations() {

        for (int i = 0; i < maximum_generations; ++i) {

            nextGeneration();
            evaluatePopulationFitness();
        }

        System.out.println("Best generation: "                + population.get(getBestSolution()));
        System.out.println("Fitness of the best generation: " + evaluateGene(population.get(getBestSolution())));
    }

    // -----------------------------------------------------------

    // Initialize Population
    private void initPopulation() {

        for(int i = 0; i < population_size; i++) population.add(generateGene());
    }

    // Generates a gene "random String of 1s and 0s"
    private String generateGene() {

        StringBuilder gene = new StringBuilder();
        char chromosome;

        for(int i = 0; i < number_of_items; ++i) {

            chromosome = '0';
            double random = Math.random();
            if(random > 0.5) { chromosome = '1'; }
            gene.append(chromosome);
        }

        return gene.toString();
    }

    // ----------------------------------------------------------

    // Evaluate Population – Fitness Function
    private void evaluatePopulationFitness() {

        double temp_fitness;

        for(int i = 0; i < population_size; i++) {

            temp_fitness = evaluateGene(population.get(i));

            fitness.set(i, temp_fitness);
        }
    }

    /**
     * Evaluates fitness of an individual
     * if chromosome is  '1', add its wight to the total wight
     * if gene's total weight < knapsack capacity (so it's acceptable)
     * otherwise fitness = 0 (Not acceptable)
     */

    private double evaluateGene(String gene) {

        double total_weight = 0, total_value = 0, fitness = 0;
        char chromosome;

        for(int j = 0; j < number_of_items; j ++) {

            chromosome = gene.charAt(j);

            if(chromosome == '1') {

                total_weight += weight_of_items.get(j);
                total_value  += value_of_items.get(j);
            }
        }

        if(knapsack_capacity >= total_weight) { fitness = total_value; }

        return fitness;
    }

    // ----------------------------------------------------------

    // Selection
    private double calculate_Cumulative_Fitnesses(){

        double totalFitness = 0;

        for(int i=0; i<population_size; ++i){

            totalFitness += fitness.get(i);
            cumulative_Fitnesses.add(totalFitness);
        }
        return totalFitness;
    }

    private int rouletteWheelSelection(){

        double totalFitness = calculate_Cumulative_Fitnesses();
        double randomNum = Math.random() * (totalFitness + 1);
        double lowerLimit = 0;

        for(int i = 0 ; i < cumulative_Fitnesses.size() ; i++){

            if(randomNum >= lowerLimit && randomNum < cumulative_Fitnesses.get(i))
                return i;

            lowerLimit = cumulative_Fitnesses.get(i);
        }
        return 0;
    }

    // ----------------------------------------------------------

    // create a new generation's population then call the cross over
    private void nextGeneration() {

        int gene_1 = rouletteWheelSelection();
        int gene_2 = rouletteWheelSelection();

        while(gene_1 == gene_2){ gene_2 = rouletteWheelSelection(); }

        // Crossover
        crossoverGenes(gene_1, gene_2);
    }

    //-------------------------------------------------------------

    /**
     * Crossover
     * Mutation
     * Replacement
     * */

    private void crossoverGenes(int parent1, int parent2) {

        String gene_1;
        String gene_2;

        double random_crossover = Math.random();
        int cross_point;

        if(random_crossover <= probability_of_crossover) { // r < PC

            cross_point = new Random().nextInt(number_of_items);

            gene_1 = population.get(parent1).substring(0, cross_point) + population.get(parent2).substring(cross_point);
            gene_2 = population.get(parent2).substring(0, cross_point) + population.get(parent1).substring(cross_point);

            // Check mutation
            mutateGene(gene_1);
            mutateGene(gene_2);

            // Add new genes to population (replace his parent)
            population.set(parent1, gene_1);
            population.set(parent2, gene_2);
        }
        else {

            // Check mutation
            mutateGene(population.get(parent1));
            mutateGene(population.get(parent2));

            // Add the same parents after mutation to population
            population.set(parent1, population.get(parent1));
            population.set(parent2, population.get(parent2));
        }
    }

    // ---------------------------------

    // do mutation, if necessary
    private void mutateGene(String gene) {

        double random_mutation;

        for (int i = 0; i < gene.length(); ++i) {

            random_mutation = Math.random();

            if (random_mutation <= probability_of_mutation) { // r < PM

                gene =  gene.substring(0, i) + flip(gene.charAt(i)) + gene.substring(i+1);
            }
        }
    }

    private char flip(char i){

        if(i == '0') return '1';
        else         return '0';
    }

    // -----------------------------------

    private int getBestSolution() {

        int    bestPosition = 0;
        double fitness;
        double bestFitness  = 0;

        for (int i=0; i < population_size; ++i) {

            fitness = evaluateGene(population.get(i));

            if(fitness > bestFitness) {

                bestFitness = fitness;
                bestPosition = i;
            }
        }
        return bestPosition;
    }
}
