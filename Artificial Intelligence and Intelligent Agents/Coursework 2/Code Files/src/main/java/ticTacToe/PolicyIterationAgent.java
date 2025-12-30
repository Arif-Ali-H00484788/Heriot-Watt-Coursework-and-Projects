package ticTacToe;


import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * A policy iteration agent. You should implement the following methods:
 * (1) {@link PolicyIterationAgent#evaluatePolicy}: this is the policy evaluation step from your lectures
 * (2) {@link PolicyIterationAgent#improvePolicy}: this is the policy improvement step from your lectures
 * (3) {@link PolicyIterationAgent#train}: this is a method that should runs/alternate (1) and (2) until convergence. 
 * 
 * NOTE: there are two types of convergence involved in Policy Iteration: Convergence of the Values of the current policy, 
 * and Convergence of the current policy to the optimal policy.
 * The former happens when the values of the current policy no longer improve by much (i.e. the maximum improvement is less than 
 * some small delta). The latter happens when the policy improvement step no longer updates the policy, i.e. the current policy 
 * is already optimal. The algorithm should stop when this happens.
 * 
 * @author ae187
 *
 */
public class PolicyIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states according to the current policy (policy evaluation). 
	 */
	HashMap<Game, Double> policyValues=new HashMap<Game, Double>();
	
	/**
	 * This stores the current policy as a map from {@link Game}s to {@link Move}. 
	 */
	HashMap<Game, Move> curPolicy=new HashMap<Game, Move>();
	
	double discount=0.9;
	
	/**
	 * The mdp model used, see {@link TTTMDP}
	 */
	TTTMDP mdp;
	
	/**
	 * loads the policy from file if one exists. Policies should be stored in .pol files directly under the project folder.
	 */
	public PolicyIterationAgent() {
		super();
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();
		
		
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public PolicyIterationAgent(Policy p) {
		super(p);
		
	}

	/**
	 * Use this constructor to initialise a learning agent with default MDP paramters (rewards, transitions, etc) as specified in 
	 * {@link TTTMDP}
	 * @param discountFactor
	 */
	public PolicyIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Use this constructor to set the various parameters of the Tic-Tac-Toe MDP
	 * @param discountFactor
	 * @param winningReward
	 * @param losingReward
	 * @param livingReward
	 * @param drawReward
	 */
	public PolicyIterationAgent(double discountFactor, double winningReward, double losingReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		this.mdp=new TTTMDP(winningReward, losingReward, livingReward, drawReward);
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Initialises the {@link #policyValues} map, and sets the initial value of all states to 0 
	 * (V0 under some policy pi ({@link #curPolicy} from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.policyValues.put(g, 0.0);
		
	}
	
	/**
	 *  You should implement this method to initially generate a random policy, i.e. fill the {@link #curPolicy} for every state. Take care that the moves you choose
	 *  for each state ARE VALID. You can use the {@link Game#getPossibleMoves()} method to get a list of valid moves and choose 
	 *  randomly between them. 
	 */
	
	/**
	 * Initialize a random policy for all valid game states
	 * Ensures that each state has a valid move assigned randomly
	 */
	public void initRandomPolicy()
	{
		/*
		 * CODE BY (SYED ARIF ALI - H00474888) HERE
		 */
		// Iterate through all states in policy values
	    for (Game state : policyValues.keySet()) {
	    	
	    	// Get possible moves for the current state
	        List<Move> moves = state.getPossibleMoves();
	        
	     // Assign a random move if moves are available
	        if (!moves.isEmpty()) {
	        	
	        	// Select a random move from possible moves
	            Move randomMove = moves.get(new Random().nextInt(moves.size()));
	            curPolicy.put(state, randomMove);
	        }
	    }
	}
	
	
	/**
	 * Performs policy evaluation steps until the maximum change in values is less than {@code delta}, in other words
	 * until the values under the currrent policy converge. After running this method, 
	 * the {@link PolicyIterationAgent#policyValues} map should contain the values of each reachable state under the current policy. 
	 * You should use the {@link TTTMDP} {@link PolicyIterationAgent#mdp} provided to do this.
	 *
	 * @param delta
	 */
	
	/**
	 * Policy Evaluation Step: Compute state values under the current policy
	 * Iteratively updates state values until convergence
	 * 
	 * @param delta Convergence threshold for policy evaluation
	 */
	
	protected void evaluatePolicy(double delta)
	{
		/* CODE BY (SYED ARIF ALI - H00474888) HERE
		 */
		
		
	    boolean converged;
	    do {
	    	
	    	// Assume convergence until proven otherwise
	        converged = true;
	        
	        // Create a new map to store updated values
	        HashMap<Game, Double> newValues = new HashMap<>(policyValues);
	     
	        // Evaluate each state in the current policy
	        for (Game state : curPolicy.keySet()) {
	            
	        	// Skip terminal states
	        	if (state.isTerminal()) continue;
	        	
	        	// Get the move prescribed by current policy for this state
	            Move move = curPolicy.get(state);
	            
	            // Compute expected value using Bellman equation
	            double expectedValue = 0.0;
	            for (TransitionProb transProb : mdp.generateTransitions(state, move)) {
	                double reward = transProb.outcome.localReward;
	                double nextStateValue = policyValues.getOrDefault(transProb.outcome.sPrime, 0.0);
	                expectedValue += transProb.prob * (reward + discount * nextStateValue);
	            }
	            
	            // Check for convergence
	            if (Math.abs(policyValues.get(state) - expectedValue) > delta) converged = false;
	            
	            // Update values
	            newValues.put(state, expectedValue);
	        }
	        // Update policy values
	        policyValues = newValues;
	    } while (!converged);
	}
		
	
	
	/**This method should be run AFTER the {@link PolicyIterationAgent#evaluatePolicy} train method to improve the current policy according to 
	 * {@link PolicyIterationAgent#policyValues}. You will need to do a single step of expectimax from each game (state) key in {@link PolicyIterationAgent#curPolicy} 
	 * to look for a move/action that potentially improves the current policy. 
	 * 
	 * @return true if the policy improved. Returns false if there was no improvement, i.e. the policy already returned the optimal actions.
	 */
	
	/**
	 * Policy Improvement Step: Attempt to improve the current policy
	 * Looks for better moves that can increase state values
	 * 
	 * @return boolean indicating whether the policy was improved
	 */
	protected boolean improvePolicy()
	{
		/* CODE BY (SYED ARIF ALI - H00474888) HERE
		 */
		boolean improved = false;
		
		// Iterate through all states in current policy
	    for (Game state : curPolicy.keySet()) {
	    	
	    	// Skip terminal states
	        if (state.isTerminal()) continue;
	        
	     // Find the best move for the current state
	        Move bestMove = null;
	        double maxVal = Double.NEGATIVE_INFINITY;
	        
	        // Evaluate all possible moves
	        for (Move move : state.getPossibleMoves()) {
	            double expectedValue = 0.0;
	            
	            // Compute expected value for each possible transition
	            for (TransitionProb transProb : mdp.generateTransitions(state, move)) {
	                double reward = transProb.outcome.localReward;
	                double nextStateValue = policyValues.getOrDefault(transProb.outcome.sPrime, 0.0);
	                expectedValue += transProb.prob * (reward + discount * nextStateValue);
	            }
	            
	            // Update best move if current move has higher expected value
	            if (expectedValue > maxVal) {
	                maxVal = expectedValue;
	                bestMove = move;
	            }
	        }
	        
	        // Check if policy can be improved for this state
	        if (!bestMove.equals(curPolicy.get(state))) {
	            curPolicy.put(state, bestMove);
	            improved = true;
	        }
	    }
		
		return improved;
	}
	
	/**
	 * The (convergence) delta
	 */
	double delta=0.1;
	
	/**
	 * This method should perform policy evaluation and policy improvement steps until convergence (i.e. until the policy
	 * no longer changes), and so uses your 
	 * {@link PolicyIterationAgent#evaluatePolicy} and {@link PolicyIterationAgent#improvePolicy} methods.
	 */
	
	/**
	 * Training method for Policy Iteration
	 * Alternates between policy evaluation and policy improvement
	 * Continues until policy stabilizes (no further improvements)
	 */
	public void train()
	{
		/* CODE BY (SYED ARIF ALI - H00474888) HERE
		 */
	    boolean policyStable;
	    do {
	    	
	    	// Step 1: Evaluate current policy
	        evaluatePolicy(delta);
	        
	        // Step 2: Attempt to improve policy
	        // If no improvement is possible, policy has converged
	        policyStable = !improvePolicy();
	    } while (!policyStable);

	    // Ensure the policy field is set after training
	    // Convert current policy to Agent's policy
	    this.policy = new Policy(this.curPolicy);
	}
	
	public static void main(String[] args) throws IllegalMoveException
	{
		/**
		 * Test code to run the Policy Iteration Agent agains a Human Agent.
		 */
		PolicyIterationAgent pi=new PolicyIterationAgent();
		
		HumanAgent h=new HumanAgent();
		
		Game g=new Game(pi, h, h);
		
		g.playOut();	
	}
}
