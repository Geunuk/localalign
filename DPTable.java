import java.util.ArrayList;

public class DPTable {

	final static int MATCH_VALUE = 5, MISMATCH_VALUE = -4, GAP_VALUE = 10; // score and penalty constants
	ArrayList<Character> db, query;	// list of values in the DB and Query file
	int[][] dpTable;	// 2-dimensional array that store subproblem's optimal solution
	int dbSize, querySize;	// number of item in the DB and Query list
	int maxValue = 0, maxRow = 0, maxCol = 0;	// track maximum value at the table
	String resultDBString, resultQueryString;	// optimal solution
	
	public DPTable(ArrayList<Character> db, ArrayList<Character> query) {
		this.db = db;
		this.query = query;
		
		this.dbSize = db.size();
		this.querySize = query.size();
	}
	
	public int[][] makeTable(ArrayList<Character> db, ArrayList<Character> query) {
		int i, j, val;
		
		/*
		 *  for convenience, insert meaningless value at the 0 index of the list
		 *  so max index is dbSize, not dbSize + 1
		 *  also querySize, not QuerySize + 1
		 */

		dpTable = new int[dbSize][querySize];
		
		for(i = 0; i < dbSize; i ++)
			// initialize first column to 0
			dpTable[i][0] = 0;
		
		for(j = 0; j < querySize; j++)
			// initialize first row to 0
			dpTable[0][j] = 0;
		
		
		for(i = 1; i < dbSize; i++) {
			for(j = 1; j < querySize; j++) {
				// calculate optimal solution of subproblem using score method
				val = score(i, j);
				
				if(val > maxValue) {
					// track maximum value at the table and row and column of value
					maxValue = val;
					maxRow = i;
					maxCol = j;
				}
				
				// insert value to the table
				dpTable[i][j] = val;
			}
		}
		
		System.out.printf("maxValue = %d maxRow = %d maxCol = %d\n", maxValue, maxRow, maxCol);
		return dpTable;
	}
	
	public int score(int i, int j) {
		int result;
		
		/*
		 *  result = max(dpTable[i - 1][j - 1] + compare(i, j),
		 *  			 dpTable[i - 1][j] - GAP_VALUE,
		 *  			 dpTable[i][j - 1] - GAP_VALUE,
		 *  			 0)
		 */	   	
		
		// 'compare' method return value between MATCH_VALUE and MISMATCH_VALUE
		result = dpTable[i - 1][j - 1] + compare(i, j);	
		
		// when DB acquire GAP
		if (result < dpTable[i][j - 1] - GAP_VALUE)
			result = dpTable[i][j - 1] - GAP_VALUE;
		
		// when Query acquire GAP
		if (result < dpTable[i - 1][j] - GAP_VALUE)
			result = dpTable[i - 1][j] - GAP_VALUE;
		
		// when result is smaller than 0
		if (result < 0)
			result = 0;
			
		return result;
	}
	
	public int compare(int i, int j) {
		
		// if DB value and Query value is same, match
		if(db.get(i) == query.get(j))
			return MATCH_VALUE;
		
		// if not, mismatch
		else 
			return MISMATCH_VALUE;
	}
	
	public void backTrace() {
	
		StringBuffer reverseDBBuffer, reverseQueryBuffer; // buffer that store path in reverse order
		int i = maxRow, j = maxCol; // initialize start tracking point to max index
		
		reverseDBBuffer= new StringBuffer();
		reverseQueryBuffer = new StringBuffer();
		
		while(true) {	
			if(dpTable[i][j] == 0)
				// meet 0, escape while 
				break;	
				
			else if(dpTable[i - 1][j - 1] + compare(i, j) == dpTable[i][j]) {
				// when value come from dpTable[i-1][j-1]
				reverseDBBuffer.append(db.get(i));
				reverseQueryBuffer.append(query.get(j));
				
				// move index
				i = i - 1;
				j = j - 1;
			}
			
			else if(dpTable[i][j - 1] - GAP_VALUE == dpTable[i][j]) {
				// when value came from dpTable[i][j-1] and row is GAP
				reverseDBBuffer.append('-');
				reverseQueryBuffer.append(query.get(j));
				
				// move index
				j = j - 1;
			}
			
			else if(dpTable[i - 1][j] - GAP_VALUE == dpTable[i][j]) {
				// when value came from dpTable[i-1][j] and column is GAP
				reverseDBBuffer.append(db.get(i));
				reverseQueryBuffer.append('-');
				
				// move index
				i = i - 1;
			}
		}
	
		// change reverse order to ordinary order and store final result
		resultDBString = reverseDBBuffer.reverse().toString();
		resultQueryString = reverseQueryBuffer.reverse().toString();
		
		System.out.println("DB    : " + resultDBString);
		System.out.println("Query : " + resultQueryString);	
	}
	
	public void dbAnswerTest(String answer) {
		int error = 0;
		
		for(int k = 0; k < answer.length(); k++) {
			if(!answer.substring(k, k+1).equals(resultDBString.substring(k,  k+1))) {
				// when answer and result is different, increase error
				System.out.printf("error %d: %s / %s\n",
								   k , answer.substring(k, k+ 1), resultDBString.substring(k, k+1));
				error++;
			}
		}
		
		System.out.println("total error : " + error);
	}
	
	public void queryAnswerTest(String answer) {
		int error = 0;
		
		for(int k = 0; k < answer.length(); k++) {
			if(!answer.substring(k, k+1).equals(resultQueryString.substring(k,  k+1))) {
				// when answer and result is different, increase error
				System.out.printf("error %d: %s / %s\n", 
								   k , answer.substring(k, k+ 1), resultQueryString.substring(k, k+1));
				error++;
			}
		}
		
		System.out.println("total error : " + error);
	}
}