
import java.io.PrintStream;
import java.util.Scanner;

/**
 * The QuestionsGame class constructs an object that represents
 * a game of 20 Questions. Clients can either begin to create a
 * file of questions and answers to play with or can use a pre-
 * existing file using the Question Tree "Standard Format."
 * <p>
 * QuestionsGame contains a private static QuestionNode class
 * that is used to represent and connect the questions and
 * answers.
 *
 * @author Brandon Said
 * @section EGR227A
 * @date April 30, 2021
 */

public class QuestionsGame {
    private QuestionNode root; // Question Tree Root

    /**
     * Constructs a QuestionsGame object with just one answer.
     *
     * @param object used as the initial answer in the game
     */
    public QuestionsGame(String object) {
        this.root = new QuestionNode(object);
    }

    /**
     * Constructs a QuestionsGame object with a questions and answers.
     *
     * @param input Scanner reading from a tree of questions in Standard Format
     */
    public QuestionsGame(Scanner input) {
        // Intialize root recursively
        this.root = formTree(input);
    }

    /**
     * A recursive helper for creating the question tree from the root.
     * Used exclusively with one of the constructors.
     * Pre: Assumed that the Scanner is connected to non-null file
     * in standard format.
     * Post: A subtree of the main question tree is returned
     *
     * @param input the Scanner from which the question and answer data is taken
     * @return a question or answer added to the binary tree
     */
    private QuestionNode formTree(Scanner input) {

        // if (input.hasNextLine()) { // should be unnecessary
        String qOrA = input.nextLine();
        // Base Case: An Answer
        if (qOrA.equals("A:")) { //
            return new QuestionNode(input.nextLine()); // removed trim
        } else { //if (qOrA.equals("Q:")){ //SHOULDN'T NEED THIS
            // Recursive Case: A Question
            return new QuestionNode(input.nextLine(), // removed trim
                    formTree(input), formTree(input));
        }
    }
    // return null // Added this. Should not need this
    //}

    /**
     * Saves the current game's questions and answers
     * through the provided PrintStream.
     *
     * @param output a PrintStream to print questions and answers to,
     *               if null, throws IllegalArgumentException
     */
    public void saveQuestions(PrintStream output) {
        // Check for null PrintStream
        if (output == null) {
            throw new IllegalArgumentException("PrintStream passed to " +
                    "saveQuestions is null.");
        }

        //recursively print in Standard Format
        saveQuestions(output, this.root);

    }

    /**
     * Recursive helper for saveQuestions. Prints questions to given
     * output source in pre-order traversal (Standard Format).
     *
     * @param output PrintStream used to print the tree
     * @param node   current node checked to print
     */
    private void saveQuestions(PrintStream output, QuestionNode node) {
        // do nothing if null
        if (node != null) {
            // if answer
            if (node.left == null && node.right == null) {
                output.println("A:");
            } else { // if question
                output.println("Q:");
            }
            //Need to save in pre-order traversal
            // Print data
            // print(left) <- exploring children
            // print(right) <- exploring children

            // Print data regardless
            output.println(node.data);

            //Explore children. Does nothing if null in case of answer.
            saveQuestions(output, node.left);
            saveQuestions(output, node.right);
        }
    }

    /**
     * Starts a game of 20 Questions based on this object's
     * questions and answers.
     *
     * @param input Scanner used to collect user input
     */
    public void play(Scanner input) {
        // Narrow down computer guess
         QuestionNode guessNode = determineGuess(input, this.root);

        //Ask user about guess
        System.out.println("I guess that your object is " + guessNode.data
                + "!");
        System.out.print("Am I right? (y/n)? ");
        // Actions based on winning or losing

        //if guess is correct
        if (isYes(input.nextLine())) {
            //print winner message
            System.out.println("Awesome! I win!");
        } else { // if guess is incorrect
            // 1. Get information

            // a. ask for object
            System.out.println("Boo! I Lose. Please help me get better!");
            System.out.print("What is your object? ");
            String object = input.nextLine().trim();

            // b. ask for Y/N question
            System.out.println("Please give me a yes/no question that " +
                    "distinguishes between " + object + " and "
                    + guessNode.data + ".");
            String question = input.nextLine().trim();


            // c. ask if object is the Y or the N
            System.out.print("Is the answer \"yes\" for " + object +
                    "? (y/n)? ");

            // 2. Adjust tree
            if (isYes(input.nextLine())) {
                // make new answer left-child
                // QuestionNode parentNode = findParentNode(guessNode.data);
                guessNode = new QuestionNode(
                        question,                            // data
                        new QuestionNode(object),            // left-child (correct object)
                        guessNode                            // right-child (incorrect guess)
                );
            } else {
                // make new answer right-child
                guessNode = new QuestionNode(
                        question,                 // data (question)
                        guessNode,                // left-child (incorrect guess)
                        new QuestionNode(object) // right-child (correct object)
                );
            }
        }
    }


    /*
    /**
     * For use in play method to determine if node is the root node
     *
     * @return true if the node is the root node

    private boolean onlyRoot() {
        return root.left == null && root.right == null;
    }
    */



    /**
     * Helper for play method that figures out a guess from player answers
     *
     * @param input Scanner taking in user responses
     * @param node  QuestionNode subtree root containing remaining
     *              questions and answers
     * @return node of the guess narrowed down to as a String
     */
    private QuestionNode determineGuess(Scanner input, QuestionNode node) {
        //BASE CASE: ANSWER NODE
        if (node.left == null && node.right == null) {
            return node;
        } else {
            //RECURSIVE CASE: QUESTION NODE
            //Ask question
            System.out.print(node.data + " (y/n)? ");
            // use response to determine next node
            QuestionNode nextNode = isYes(input.nextLine()) ? node.left
                    : node.right;
            // traverse accordingly by returning determineGuess of child
            return determineGuess(input, nextNode);
        }
    }

    /**
     * Evaluates response to yes-or-no question. Used with play method.
     *
     * @param response String answer to yes-or-no question taken from user
     * @return true if answer is considered yes, false if no
     */
    private boolean isYes(String response) {
        return response.trim().toLowerCase().startsWith("y");
    }

    /**
     * QuestionNodes are used to organize and connect the questions and answers
     * as a binary tree.
     */
    private static class QuestionNode {
        public final String data;
        public QuestionNode left;
        public QuestionNode right;

        /**
         * Constructs an instance of QuestionNode.
         *
         * @param data  String representing question or answer
         * @param left  the node accessed when answering yes if question.
         *              Null if answer.
         * @param right the node accessed when answering no if question.
         *              Null if answer.
         */
        public QuestionNode(String data, QuestionNode left, QuestionNode right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }


        /**
         * Constructs an instance of QuestionNode given just a String.
         * Should only be used when given an answer and no question.
         *
         * @param answer String representing an answer
         */
        public QuestionNode(String answer) {
            this(answer, null, null);
        }
    }
}
