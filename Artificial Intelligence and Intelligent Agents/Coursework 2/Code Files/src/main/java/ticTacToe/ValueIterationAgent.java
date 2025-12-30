package ticTacToe;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Value Iteration Agent, only very partially implemented. The methods to implement are: 
 * (1) {@link ValueIterationAgent#iterate}
 * (2) {@link ValueIterationAgent#extractPolicy}
 * 
 * You may also want/need to edit {@link ValueIterationAgent#train} - feel free to do this, but you probably won't need to.
 * @author ae187
 *
 */
public class ValueIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states
	 */
	Map<Game, Double> valueFunction=new HashMap<Game, Double>();
	
	/**
	 * the discount factor
	 */
	double discount=0.9;
	
	/**
	 * the MDP model
	 */
	TTTMDP mdp=new TTTMDP();
	
	/**
	 * the number of iterations to perform - feel free to change this/try out different numbers of iterations
	 */
	int k=10;
	
	
	/**
	 * This constructor trains the agent offline first and sets its policy
	 */
	public ValueIterationAgent()
	{
		super();
		mdp=new TTTMDP();
		this.discount=0.9;
		initValues();
		train();
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public ValueIterationAgent(Policy p) {
		super(p);
		
	}

	public ValueIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		mdp=new TTTMDP();
		initValues();
		train();
	}
	
	/**
	 * Initialises the {@link ValueIterationAgent#valueFunction} map, and sets the initial value of all states to 0 
	 * (V0 from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.valueFunction.put(g, 0.0);
		
		
		
	}
	
	
	
	public ValueIterationAgent(double discountFactor, double winReward, double loseReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		mdp=new TTTMDP(winReward, loseReward, livingReward, drawReward);
	}
	
	
	/*
	 * Performs {@link #k} value iteration steps. After running this method, the {@link ValueIterationAgent#valueFunction} map should contain
	 * the (current) values of each reachable state. You should use the {@link TTTMDP} provided to do this.
	 */
	
	public void iterate()
	{
		/* CODE BY (SYED ARIF ALI - H00474888) HERE
		 * 
		 */
		
		// Iterate k times to compute value estimates
	    for (int i = 0; i < k; i++) {
	    	// Create a new HashMap to store updated values to prevent in-iteration updates
	        HashMap<Game, Double> newValues = new HashMap<>(valueFunction);
	        for (Game state : valueFunction.keySet()) {
	        	
	        	// Check for terminal states - their value is always 0
	            if (state.isTerminal()) {
	            	// Reset terminal state value to 0
	                newValues.put(state, 0.0);
	                continue;
	            }
	            
	            // Find the maximum expected value across all possible moves
	            double maxVal = Double.NEGATIVE_INFINITY;
	            for (Move move : state.getPossibleMoves()) {
	                double expectedValue = 0.0;
	                
	                // Compute expected value using transition probabilities
	                for (TransitionProb transProb : mdp.generateTransitions(state, move)) {
	                	
	                	// Calculate expected value using Bellman equation
	                    // V(s) = max(R + γ * V(s'))
	                	
	                    double reward = transProb.outcome.localReward;
	                    double nextStateValue = valueFunction.getOrDefault(transProb.outcome.sPrime, 0.0);
	                    expectedValue += transProb.prob * (reward + discount * nextStateValue);
	                }
	             // Update maximum value for this state
	                maxVal = Math.max(maxVal, expectedValue);
	            }
	            // Update the value for the current state
	            newValues.put(state, maxVal);
	        }
	     // Update value function with new computed values
	        valueFunction = newValues;
	    }		
	}
	
	/**This method should be run AFTER the train method to extract a policy according to {@link ValueIterationAgent#valueFunction}
	 * You will need to do a single step of expectimax from each game (state) key in {@link ValueIterationAgent#valueFunction} 
	 * to extract a policy.
	 * 
	 * @return the policy according to {@link ValueIterationAgent#valueFunction}
	 */
	
	/**
	 * Extracts the optimal policy based on the computed value function
	 * Uses one-step look-ahead to determine the best move for each state
	 * @return Optimal policy derived from the value function
	 */
	public Policy extractPolicy()
	{
		/*
		 * CODE BY (SYED ARIF ALI - H00474888) HERE
		 */
		
		// HashMap to store the best move for each game state
	    HashMap<Game, Move> policyMap = new HashMap<>();
	    
	    // Iterate through all states in the value function
	    for (Game state : valueFunction.keySet()) {
	    
	    	// Skip terminal states
	        if (state.isTerminal()) continue;
	        
	        Move bestMove = null;
	        double maxVal = Double.NEGATIVE_INFINITY;
	        
	     // Find the best move for the current state
	        for (Move move : state.getPossibleMoves()) {
	            double expectedValue = 0.0;
	            
	         // Compute expected value for each possible transition
	            for (TransitionProb transProb : mdp.generateTransitions(state, move)) {
	                double reward = transProb.outcome.localReward;
	                double nextStateValue = valueFunction.getOrDefault(transProb.outcome.sPrime, 0.0);
	                expectedValue += transProb.prob * (reward + discount * nextStateValue);
	            }
	         // Update best move if current move has higher expected value
	            if (expectedValue > maxVal) {
	                maxVal = expectedValue;
	                bestMove = move;
	            }
	        }
	     // Add best move to policy map if found
	        if (bestMove != null) {
	            policyMap.put(state, bestMove);
	        }
	    }
	 // Create and return the policy based on the computed best moves
	    policy = new Policy(policyMap);		
	    return policy;
	}	
	/**
	 * This method solves the mdp using your implementation of {@link ValueIterationAgent#extractPolicy} and
	 * {@link ValueIterationAgent#iterate}. 
	 */
	public void train()
	{
		/**
		 * First run value iteration
		 */
		this.iterate();
		/**
		 * now extract policy from the values in {@link ValueIterationAgent#valueFunction} and set the agent's policy 
		 *  
		 */
		
		super.policy=extractPolicy();
		
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the iterate() & extractPolicy() methods");
			//System.exit(1);
		}	
	}
	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play the agent against a human agent.
		ValueIterationAgent agent=new ValueIterationAgent();
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();
	}
}
