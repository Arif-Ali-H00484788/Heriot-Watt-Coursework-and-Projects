package ticTacToe;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * A Q-Learning agent with a Q-Table, i.e. a table of Q-Values. This table is implemented in the {@link QTable} class.
 * 
 *  The methods to implement are: 
 * (1) {@link QLearningAgent#train}
 * (2) {@link QLearningAgent#extractPolicy}
 * 
 * Your agent acts in a {@link TTTEnvironment} which provides the method {@link TTTEnvironment#executeMove} which returns an {@link Outcome} object, in other words
 * an [s,a,r,s']: source state, action taken, reward received, and the target state after the opponent has played their move. You may want/need to edit
 * {@link TTTEnvironment} - but you probably won't need to. 
 * @author ae187
 */

public class QLearningAgent extends Agent {
	
	/**
	 * The learning rate, between 0 and 1.
	 */
	double alpha=0.1;
	
	/**
	 * The number of episodes to train for
	 */
	int numEpisodes=50000;
	
	/**
	 * The discount factor (gamma)
	 */
	double discount=0.99;
	
	
	/**
	 * The epsilon in the epsilon greedy policy used during training.
	 */
	double epsilon=0.5;
	
	/**
	 * This is the Q-Table. To get an value for an (s,a) pair, i.e. a (game, move) pair.
	 * 
	 */
	
	QTable qTable=new QTable();
	
	
	/**
	 * This is the Reinforcement Learning environment that this agent will interact with when it is training.
	 * By default, the opponent is the random agent which should make your q learning agent learn the same policy 
	 * as your value iteration and policy iteration agents.
	 */
	TTTEnvironment env=new TTTEnvironment();
	
	
	/**
	 * Construct a Q-Learning agent that learns from interactions with {@code opponent}.
	 * @param opponent the opponent agent that this Q-Learning agent will interact with to learn.
	 * @param learningRate This is the rate at which the agent learns. Alpha from your lectures.
	 * @param numEpisodes The number of episodes (games) to train for
	 */
	public QLearningAgent(Agent opponent, double learningRate, int numEpisodes, double discount)
	{
		env=new TTTEnvironment(opponent);
		this.alpha=learningRate;
		this.numEpisodes=numEpisodes;
		this.discount=discount;
		initQTable();
		train();
	}
	
	/**
	 * Initialises all valid q-values -- Q(g,m) -- to 0.
	 *  
	 */
	
	protected void initQTable()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
		{
			List<Move> moves=g.getPossibleMoves();
			for(Move m: moves)
			{
				this.qTable.addQValue(g, m, 0.0);
				//System.out.println("initing q value. Game:"+g);
				//System.out.println("Move:"+m);
			}
			
		}
		
	}
	
	/**
	 * Uses default parameters for the opponent (a RandomAgent) and the learning rate (0.2). Use other constructor to set these manually.
	 */
	public QLearningAgent()
	{
		this(new RandomAgent(), 0.01, 50000, 0.95);
		
	}
	
	
	/**
	 *  Implement this method. It should play {@code this.numEpisodes} episodes of Tic-Tac-Toe with the TTTEnvironment, updating q-values according 
	 *  to the Q-Learning algorithm as required. The agent should play according to an epsilon-greedy policy where with the probability {@code epsilon} the
	 *  agent explores, and with probability {@code 1-epsilon}, it exploits. 
	 *  
	 *  At the end of this method you should always call the {@code extractPolicy()} method to extract the policy from the learned q-values. This is currently
	 *  done for you on the last line of the method.
	 */
	

	/**
	 * Train the Q-Learning agent through multiple episodes of Tic-Tac-Toe
	 * Implements an epsilon-greedy exploration strategy with dynamic epsilon decay
	 */
	
	public void train()
	{
		/* 
		 * CODE BY (SYED ARIF ALI - H00474888) HERE
		 */
	    Random rand = new Random();
	    double startingEpsilon = epsilon;

	    // Train for specified number of episodes
	    // Q-table initialization for learning
	    for (int episode = 0; episode < numEpisodes; episode++) {
	    	
	        // Decay epsilon over time
	    	// Epsilon decay strategy to balance exploration and exploitation
	        epsilon = startingEpsilon * (1 - ((double)episode / numEpisodes));
	        env.reset();

	        // Continue playing until the game reaches a terminal state
	        while (!env.getCurrentGameState().isTerminal()) {
	            Game currentGame = env.getCurrentGameState();
	            
	            // Skip if game is already terminal
	            if (currentGame.isTerminal()) {
	                break;
	            }

	            // Move selection strategy: Exploration vs Exploitation
	            Move chosenMove;
	            if (rand.nextDouble() < epsilon) {
	                List<Move> possibleMoves = currentGame.getPossibleMoves();
	                List<Move> defensiveMoves = findDefensiveMoves(currentGame);
	                
	                // Exploration in detail
	                if (!defensiveMoves.isEmpty()) {
	                   
	                    chosenMove = defensiveMoves.get(rand.nextInt(defensiveMoves.size()));
	                } else {
	                    chosenMove = possibleMoves.get(rand.nextInt(possibleMoves.size()));
	                }
	            } else {
	            	// Exploitation phase: Choose best move based on current Q-values
	                chosenMove = pickBestMove(currentGame);
	            }

	            // Execute move and process outcome
	            try {
	                Outcome outcome = env.executeMove(chosenMove);
	                
	                // Reward shaping for different game outcomes	                
	                double reward = outcome.localReward;
	                if (outcome.sPrime.isTerminal()) {
	                    switch(outcome.sPrime.evaluateGameState()) {
	                        case Game.O_WON:
	                            reward = -100;  // Strong penalty for losing
	                            break;
	                        case Game.X_WON:
	                            reward = 100;   // Strong reward for winning
	                            break;
	                        case Game.DRAW:
	                            reward = 10;    // Small positive reward for draw (encourages defensive play)
	                            break;
	                    }
	                } else {
	                    // Add a small defensive bonus for blocking potential threats
	                    if (wouldBlockTwoInARow(outcome.sPrime, outcome.move)) {
	                        reward += 5;
	                    }
	                }

	                // Q-value update using Q-Learning update rule
	                double currentQValue = qTable.getQValue(outcome.s, outcome.move);
	                double maxNextStateQValue = maxQvalue(outcome.sPrime);

	                double newQValue = (1 - alpha) * currentQValue +
	                    alpha * (reward + discount * maxNextStateQValue);
	                
	                // Update Q-table with new Q-value
	                qTable.addQValue(outcome.s, outcome.move, newQValue);

	            } catch (IllegalMoveException e) {
	                System.err.println("Illegal move during training: " + e.getMessage());
	                break;
	            }
	        }
	    }
		//--------------------------------------------------------
		//you shouldn't need to delete the following lines of code.
	    // Extract final policy from learned Q-values
		this.policy=extractPolicy();
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the train() & extractPolicy methods");
			//System.exit(1);
		}
	}
	
	/*
	 * CODE BY (SYED ARIF ALI - H00474888) HERE
	 */
	// Helper method to find defensive moves
	
	private List<Move> findDefensiveMoves(Game game) {
	    List<Move> defensiveMoves = new ArrayList<>();
	    List<Move> possibleMoves = game.getPossibleMoves();
	    for (Move move : possibleMoves) {
	        Game simulatedGame = new Game(game);
	        try {
	            simulatedGame.simulateMove(move);
	            if (wouldBlockTwoInARow(simulatedGame, move) || 
	                wouldBlockForkOpportunity(simulatedGame, move) || 
	                wouldBlockOpponentWin(simulatedGame, move)) {
	                defensiveMoves.add(move);
	            }
	        } catch (IllegalMoveException e) {
	            continue;
	        }
	    }
	    return defensiveMoves;
	}
	
	
	 /*
	 * CODE BY (SYED ARIF ALI - H00474888) HERE
	 */
	//  Helper method to find blocking moves
	private boolean wouldBlockOpponentWin(Game simulatedGame, Move move ) {
	    // Directly use Game's state evaluation method
	    switch(simulatedGame.evaluateGameState()) {
	        case Game.O_WON:  // If O would win in simulated move
	            return true;
	        default:
	            return false;
	    }
	}
	
	/* 
	 * CODE BY (SYED ARIF ALI - H00474888) HERE
	 */  
	// Helper method to block 2 moves done together
	private boolean wouldBlockTwoInARow(Game simulatedGame, Move move) {
	    // More sophisticated blocking logic
	    char[][] board = simulatedGame.getBoard();
	    char opponentSymbol = (move.who.getName() == 'X') ? 'O' : 'X';

	    // Check rows, columns, and diagonals for potential threats
	    int[][] directions = {
	        {0,1,2}, {3,4,5}, {6,7,8},  // Rows
	        {0,3,6}, {1,4,7}, {2,5,8},  // Columns
	        {0,4,8}, {2,4,6}            // Diagonals
	    };

	    for (int[] line : directions) {
	        int opponentCount = 0;
	        int emptyCount = 0;
	        for (int index : line) {
	            int row = index / 3;
	            int col = index % 3;
	            if (board[row][col] == opponentSymbol) opponentCount++;
	            if (board[row][col] == ' ') emptyCount++;
	        }
	        
	        // More aggressive blocking criteria
	        if (opponentCount >= 2 && emptyCount > 0) return true;
	    }
	    return false;
	}
	
	/* 
	 * CODE BY (SYED ARIF ALI - H00474888) HERE
	 */
	// Helper method to find blocking defensive moves to prevent forks
	
	private boolean wouldBlockForkOpportunity(Game simulatedGame, Move move) {
	    // Validate the move
	    if (!simulatedGame.isLegal(move)) {
	        return false;
	    }

	    try {
	        // Simulate the move
	        Game tempGame = simulatedGame.simulateMove(move);
	        
	        // Determine opponent's symbol
	        char opponentSymbol = (move.who.getName() == 'X') ? 'O' : 'X';
	        
	        // Analyze potential fork opportunities
	        int forkOpportunities = 0;
	        
	        // Check all possible lines
	        int[][] potentialLines = {
	            {0,1,2}, {3,4,5}, {6,7,8},  // Rows
	            {0,3,6}, {1,4,7}, {2,5,8},  // Columns
	            {0,4,8}, {2,4,6}            // Diagonals
	        };
	        
	        // Track lines with potential fork opportunities
	        List<Integer> forkLines = new ArrayList<>();
	        
	        for (int lineIndex = 0; lineIndex < potentialLines.length; lineIndex++) {
	            int[] line = potentialLines[lineIndex];
	            
	            // Convert line indices to board coordinates
	            int[] rows = {line[0]/3, line[1]/3, line[2]/3};
	            int[] cols = {line[0]%3, line[1]%3, line[2]%3};
	            
	            // Analyze the line
	            int opponentCount = 0;
	            int emptyCount = 0;
	            
	            for (int i = 0; i < 3; i++) {
	                if (tempGame.getBoard()[rows[i]][cols[i]] == opponentSymbol) {
	                    opponentCount++;
	                } else if (tempGame.getBoard()[rows[i]][cols[i]] == ' ') {
	                    emptyCount++;
	                }
	            }
	            
	            // Potential fork line: one opponent symbol, two empty spaces
	            if (opponentCount == 1 && emptyCount == 2) {
	                forkLines.add(lineIndex);
	            }
	        }
	        
	        // Count intersecting fork opportunities
	        for (int i = 0; i < forkLines.size(); i++) {
	            for (int j = i + 1; j < forkLines.size(); j++) {
	                // Check if lines intersect
	                if (hasIntersection(potentialLines[forkLines.get(i)], potentialLines[forkLines.get(j)])) {
	                    forkOpportunities++;
	                }
	            }
	        }
	        
	        // Block if more than one fork opportunity exists
	        return forkOpportunities > 0;
	    } catch (IllegalMoveException e) {
	        // Handle potential illegal move exception
	        return false;
	    }
	}

	/* 
	 * CODE BY (SYED ARIF ALI - H00474888) HERE
	 */	
	
	// Helper method to check if two lines intersect
	private boolean hasIntersection(int[] line1, int[] line2) {
	    // Convert to sets for easy intersection check
	    Set<Integer> set1 = new HashSet<>(Arrays.stream(line1).boxed().collect(Collectors.toList()));
	    Set<Integer> set2 = new HashSet<>(Arrays.stream(line2).boxed().collect(Collectors.toList()));
	    
	    // Remove intersection
	    set1.retainAll(set2);
	    
	    // If intersection is not empty, lines intersect
	    return !set1.isEmpty();
	}
	
	/* 
	 * CODE BY (SYED ARIF ALI - H00474888) HERE
	 */
	
	/**
	 * Select best move for a game state using Q-values and strategic considerations
	 * Incorporates multiple strategic elements:
	 * 1. Q-value based selection
	 * 2. Strategic position scoring
	 * 3. Immediate win detection
	 * 4. Blocking critical moves
	 * 
	 * @param game Current game state
	 * @return Best move according to learned strategy
	 */
	
	private Move pickBestMove(Game game) {
	    List<Move> possibleMoves = game.getPossibleMoves();
	    if (possibleMoves.isEmpty()) return null;

	    // Prioritize strategic positions
	    List<Integer> strategicPositions = Arrays.asList(4, 0, 2, 6, 8, 1, 3, 5, 7);
	    
	    Move bestMove = null;
	    double maxQValue = Double.NEGATIVE_INFINITY;

	    for (Move move : possibleMoves) {
	        double currentQValue = qTable.getQValue(game, move);
	        
	        // Position-based strategic scoring
	        int positionScore = strategicPositions.indexOf(move.x * 3 + move.y);
	        currentQValue += (strategicPositions.size() - positionScore) * 0.1;

	        // Bonus for winning moves
	        Game simulatedGame = game.clone();
	        try {
	            simulatedGame.simulateMove(move);
	            if (simulatedGame.getState() == Game.X_WON) {
	                currentQValue += 50; // Huge bonus for winning move
	            }
	        } catch (IllegalMoveException e) {
	            continue;
	        }

	        //  Bonus for blocking critical moves
	        if (wouldBlockTwoInARow(simulatedGame, move)) {
	            currentQValue += 20; // Bonus for blocking
	        }
	        
	        //Update best move
	        if (currentQValue > maxQValue) {
	            maxQValue = currentQValue;
	            bestMove = move;
	        }
	    }

	    return bestMove;
	}
	
	/* 
	 * CODE BY (SYED ARIF ALI - H00474888) HERE
	 */
	// Helper method to get the maximum Q-value for a given game state
	private Double maxQvalue(Game gamePrime) {
	    // Return 0 for terminal states
	    if (gamePrime.isTerminal()) {
	        return 0.0;
	    }
	    
	    // Find the maximum Q-value among possible moves
	    List<Move> possibleMoves = gamePrime.getPossibleMoves();
	    double maxQValue = -Double.MAX_VALUE;
	    
	    for (Move move : possibleMoves) {
	        double currentQValue = qTable.getQValue(gamePrime, move);
	        maxQValue = Math.max(maxQValue, currentQValue);
	    }
	    
	    return maxQValue;
	}

	/** Implement this method. It should use the q-values in the {@code qTable} to extract a policy and return it.
	 *
	 * @return the policy currently inherent in the QTable
	 */
	
	/**
	 * Extract optimal policy from learned Q-values
	 * Selects the best move for each game state based on Q-table
	 * 
	 * @return Optimal policy derived from Q-learning
	 */
	
	public Policy extractPolicy()
	{
		/* 
		 * CODE BY (SYED ARIF ALI - H00474888) HERE
		 */
	    Policy derivedPolicy = new Policy();
	    
	    // Iterate through all game states in the Q-table
	    for (Game game : qTable.keySet()) {
	        // Skip terminal states
	        if (game.isTerminal()) continue;
	        
	        // Find the best move for each non-terminal game state
	        Move bestMove = pickBestMove(game);
	        if (bestMove != null) {
	            derivedPolicy.policy.put(game, bestMove);
	        }
	    }
	    
	    return derivedPolicy;
	}
	
	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play your agent against a human agent (yourself).
		QLearningAgent agent=new QLearningAgent();
		
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();
	}	
}
