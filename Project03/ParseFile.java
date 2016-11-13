package Project03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.List;
import java.util.Arrays;

import com.sun.glass.ui.TouchInputSupport;

public class ParseFile {
	static boolean validLine;
	
	static String delimiter = "[ ]";
	static String[] lineTokens = null;
	static String icounter = "";
	static String fcounter = "";
	static String IFcache = null; 
	static String BDcache = null;
	static String DIMcache = null;
	
	static LinkedHashMap<String, Individual> indiMap = new LinkedHashMap<String, Individual>();
	static LinkedHashMap<String, Family> famMap = new LinkedHashMap<String, Family>();
	
	private static String US22ErrorMsg = "";
	static List<String> monNames = Arrays.asList(new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"});
	static String US41ErrorMsg = "";
	static String US42ErrorMsg = "";
	
	public static void main(String[] args) {
		String fileName ="test.ged";
		String line = null;
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			
			while((line = br.readLine())!= null){
				lineTokens = line.split(delimiter);
				storeData(lineTokens);
			}
			br.close();
			printData();
            printErrorsAnomalies();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//To print Individual and Family Data
	public static void printData(){
		for (Entry<String, Individual> i :indiMap.entrySet()){
			System.out.println(i.getValue().toString());
			NErrorChecker nErrorCheck = new NErrorChecker(indiMap, famMap);
			nErrorCheck.calculateAge(i.getValue());
		}
		for (Entry<String, Family> f :famMap.entrySet()){
			Family fam = f.getValue();
			
			System.out.println("\nFamily id : "+fam.getId());
			if(fam.getHusband() != null){
				if(indiMap.containsKey(fam.getHusband())){
					Individual husb = indiMap.get(fam.getHusband());
					System.out.println("Husband Name : "+husb.getName());
				}
			}
			if(fam.getWife() != null){
				if(indiMap.containsKey(fam.getWife())){
					Individual wife = indiMap.get(fam.getWife());
					System.out.println("Wife Name : "+wife.getName());
				}
			}
			System.out.println("Marriage Date : "+fam.getMarriageDate());
			if(fam.getDivorceDate() != null){
				System.out.println("Divorce Date : "+fam.getDivorceDate());
			}
			if(fam.getChild() != null){
				System.out.println("Child : "+fam.getChild());
			}
		}
	}
	
	// To store individual data in Individual List and family data in Family list
	public static void storeData(String []lineTokens){
		Individual i;
		Family f;
		switch(lineTokens[0]){
			case "0":
				if(lineTokens.length>2){

					checkIfIdExist(lineTokens[1]);
					
					switch(lineTokens[2]){
						case "INDI":
							i = new Individual();
							icounter = lineTokens[1];
							i.setId(lineTokens[1]);
							indiMap.put(lineTokens[1], i);
							IFcache = "I";
							break;
						case "FAM":
							f = new Family();
							fcounter = lineTokens[1];
							f.setId(lineTokens[1]);
							famMap.put(lineTokens[1], f);
							IFcache ="F";
							break;
						default:break;
						}
			
				}
				break;
			case "1":
				switch(IFcache){
					case "I":i = indiMap.get(icounter);
							switch(lineTokens[1]){
								case "NAME":i.setName(lineTokens[2]+" "+lineTokens[3].replaceAll("/",""));break;
								case "SEX":i.setGender(lineTokens[2]);break;
								case "BIRT":BDcache = "B";break;
								case "DEAT":BDcache = "D";break;
								case "FAMS":i.setFspouseId(lineTokens[2]); break;
								case "FAMC":i.setFchildId(lineTokens[2]); break;
								default:break;
								
							}
						break;
						
					case "F":f = famMap.get(fcounter);
							switch(lineTokens[1]){
								case "HUSB":f.setHusband(lineTokens[2]);break;
								case "WIFE":f.setWife(lineTokens[2]);break;
								case "MARR":DIMcache = "M";break;
								case "DIV":DIMcache = "DI";break;
								case "CHIL": f.setChild(lineTokens[2]); break;
								default:break;
							}
						break;
					default:break;
				}
				break;
			case "2":
				//Level 2 mostly contains date only, birth or death
				if(!lineTokens[1].equals("DATE"))
					break;
				String day = "01";
				String month = "JAN";
				String year = lineTokens[2];
				String us41ErrMesg = String.format("US41: %1s(%2s)'s date is partial.\n", indiMap.get(icounter).name, indiMap.get(icounter).id);
				
				if(monNames.contains(lineTokens[2])){
					month = lineTokens[2];
					year = lineTokens[3];
					US41ErrorMsg += us41ErrMesg;
				}
				else if(tryParseInt(lineTokens[2]) && 0 < Integer.valueOf(lineTokens[2]) && Integer.valueOf(lineTokens[2]) < 32){
					day = lineTokens[2];
					month = lineTokens[3];
					year = lineTokens[4];
				}
				else {
					US41ErrorMsg += us41ErrMesg;
				}
				Date date = getDate(day+month+year);
				if(date == null){
					US42ErrorMsg += String.format("US42: %1s(%2s)'s dates is illegitimate.\n", indiMap.get(icounter).name, indiMap.get(icounter).id);
					date = getDate("01JAN1900");
				}
				
				switch(IFcache){
					case "I":
						i = indiMap.get(icounter);
						switch(BDcache){
							case "B":i.setBirthDate(date);break;
							case "D":i.setDeathDate(date);break;
							default:break;
						}
						break;
					case "F": 
						f = famMap.get(fcounter);
						switch(DIMcache){
							case "M":f.setMarriageDate(date);break;
							case "DI":f.setDivorceDate(date);break;
							default:break;
						}
						break;
					default:
						break;
				}
			break;
			default:break;
		}
		
	}
	
	// To convert String date to Date Object
	public static Date getDate(String date){
		DateFormat format = new SimpleDateFormat("ddMMMyyyy", Locale.ENGLISH);
		Date dt = null;
		try {
			dt = format.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return dt;
	}


    private static void printErrorsAnomalies(){
		BErrorChecker bErrorChecker = new BErrorChecker(indiMap, famMap);
		bErrorChecker.check();
		System.out.print(US22ErrorMsg);
		System.out.print(US41ErrorMsg);
		System.out.print(US42ErrorMsg);
		AErrorChecker aErrorCheck = new AErrorChecker(indiMap, famMap);
		aErrorCheck.runLoop();
		NErrorChecker nErrorCheck = new NErrorChecker(indiMap,famMap);
		nErrorCheck.mar_div_BeforeDeath();
		nErrorCheck.parentsNotOld();
		nErrorCheck.uniqueNameBirthDate();
		nErrorCheck.spouseAgeDiff();
		nErrorCheck.recentSurvivors();
		nErrorCheck.upcomingBirthdays();
		RErrorChecker check = new RErrorChecker(indiMap, famMap);
		check.user_stories();
    }
	
	
    private static void checkIfIdExist(String id){
    	boolean isIndividualExist = indiMap.containsKey(id);
    	boolean isFamilyExist = famMap.containsKey(id);

    	if(isIndividualExist || isFamilyExist){
    		US22ErrorMsg = US22ErrorMsg.concat(String.format("Error US22: This unique id(%1s) already exist\n", id));
    	}
    }
	
	
	static boolean tryParseInt(String value) {  
		try {  
			Integer.parseInt(value);  
			return true;  
		} catch (NumberFormatException e) {  
			return false;  
		}  
	}

}
