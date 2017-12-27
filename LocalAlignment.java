import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

public class LocalAlignment {
	
	public static ArrayList<Character> readInput(File file) {
		ArrayList<Character> result = new ArrayList<>(); // list that store result
		BufferedReader br = null;
		int value;	// variable that store one character in the file
		char c;	// 'value' will be changed to 'c' 
		
		// for convenience in calculating index at the DPTable, insert meaningless value at index 0
		result.add(' ');
		
		try {
            br = new BufferedReader(new FileReader(file));
            
            br.readLine();	// ignore first line
            
            while ((value = br.read()) != -1) {
            	// read one character from file
            	c = (char) value;
            	
            	// if not whitespace, add to the result list
            	if(!Character.isWhitespace(c)) 
            		result.add(c);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null) try {br.close(); } catch (IOException e) {}
        }
		
		return result;
	}
	
	
	public static void writeResult(DPTable dpTable, String dbFileName, String queryFileName, String absoluteOutputPath) {
		 BufferedWriter bw = null;
		 String outFileName;
		 File outFile;;
		 
		 if(absoluteOutputPath.lastIndexOf(File.separator) != absoluteOutputPath.length() - 1)
			 // if outputpath doesn't have file separator at last, insert slash
			 absoluteOutputPath += File.separator;
		  
		 // concatenate path and file name
		 outFileName = absoluteOutputPath + dbFileName + "_" + queryFileName + ".txt";
		 outFile = new File(outFileName);
		 
		 try {
			 bw = new BufferedWriter(new FileWriter(outFile));
	         
			 // write DB Name
			 bw.write("DB : " + dbFileName);
			 bw.newLine();
			 
			 // write Query Name
			 bw.write("Query : " + queryFileName);
			 bw.newLine();
			 bw.newLine();
			 
			 bw.write("Alignment");
			 bw.newLine();
			 
			 // write alignment result of DB
			 bw.write(dpTable.resultDBString);
			 bw.newLine();
			 bw.newLine();
			 
			 // write alignment result of Query
			 bw.write(dpTable.resultQueryString);
			 
			 bw.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally {
	            if(bw != null) try {bw.close(); } catch (IOException e) {}
	        }
	}

	public static String getFileName(String absolutePath) {
		String fileName;
		int fileSeparatorIndex, periodIndex;
		
		fileSeparatorIndex = absolutePath.lastIndexOf(File.separator);
		periodIndex = absolutePath.lastIndexOf(".");
		
		// get file name between file separator and "."
		fileName = absolutePath.substring(fileSeparatorIndex + 1, periodIndex);
		System.out.println(fileName);
		return fileName;
	}
	
	public static void main(String args[]) {
		File dbFile, queryFile;
		ArrayList<Character> db = new ArrayList<>();
		ArrayList<Character> query = new ArrayList<>();
		String absoluteDBPath, absoluteQueryPath, absoluteOutputPath, dbFileName, queryFileName;
		DPTable dpTable;
		
		absoluteDBPath = args[0];
		absoluteQueryPath = args[1];
		absoluteOutputPath = args[2];
		
		dbFile = new File(absoluteDBPath);
		queryFile = new File(absoluteQueryPath);
		
		db = readInput(dbFile);	// read value from the DB file
		query = readInput(queryFile);	// read value from the query file
		
		dpTable = new DPTable(db, query); // create DPTable Object
		dpTable.makeTable(db, query);	// make array and fill in the array
		dpTable.backTrace(); // calculate optimal path
		
		dbFileName = getFileName(absoluteDBPath); // return file name of the DB file
		queryFileName = getFileName(absoluteQueryPath); //return file name of the Query file
		
		writeResult(dpTable, dbFileName, queryFileName, absoluteOutputPath);
	}
}