package Project03;
package proj04;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class RErrorChecker {
	private LinkedHashMap<String, Individual> individual_Map;
	private LinkedHashMap<String, Family> family_Map;
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	HashMap<String, Date> m = new HashMap<String, Date>();
	HashMap<String, String> m2 = new HashMap<String, String>();
	Set<String> m3 = new HashSet<String>();
	
	public RErrorChecker(LinkedHashMap<String, Individual> individual_Map, LinkedHashMap<String, Family> family_Map){
		this.individual_Map = individual_Map;
		this.family_Map = family_Map;
	
	}
	public void user_stories(){
		for(Entry<String, Individual> individual : individual_Map.entrySet()) {
			Date currentDate = new Date();
			if(currentDate.before(individual.getValue().getBirthDate())){
				String name = individual.getValue().getName();
				System.out.println("Anamoly US01: "+name+ "("+individual.getValue().getId()+")birth is before current date");
				
			}
		}
		for(Entry<String, Family> family : family_Map.entrySet()) {
			Family family1 = family.getValue();
			String husband = individual_Map.get(family1.getHusband()).getName();
			String wife = individual_Map.get(family1.getWife()).getName();
			Date birth_hushband = individual_Map.get(family1.getHusband()).getBirthDate();
			Date birth_wife = individual_Map.get(family1.getWife()).getBirthDate();
			if(birth_hushband != null){
				if(family1.getMarriageDate().before(birth_hushband)){
					if(!m.containsKey(husband))
					{
					System.out.println("Anamoly US02: "+husband+"("+family1.getHusband()+") birth is before marriage");	
					m.put(husband, birth_hushband);
					}	
				}
				if(birth_wife != null){
					if(family1.getMarriageDate().before(birth_wife))
						if(!m.containsKey(wife))
						{
							System.out.println("Anomaly US02 : "+wife+"("+family1.getWife()+") birth is  before marriage");
							m.put(wife, birth_wife);
						}			
			}
		}
	}
		for(Entry<String, Family> family : family_Map.entrySet()){
			Family family1 = family.getValue();
			String hus_id = individual_Map.get(family1.getHusband()).getId();
			String wife_id = individual_Map.get(family1.getWife()).getId();
			
			ArrayList<String> children = family1.getChild();
			if(!children.isEmpty()){
				for(int i=0;i<children.size();i++){
					Individual child = individual_Map.get(children.get(i));
					if(child.getFspouseId() != null){
						String SpouseID = child.getFspouseId();
						Family fam = family_Map.get(SpouseID);
						String compareID = "";
						if(fam.getHusband().equals(child.getId())){
							if(fam.getWife().equals(wife_id)){
								System.out.println("Anomaly US17: Family member  " +child.getName() + " " +  child.getId() +" married to descendent "+hus_id);
							}
						}
						else if(fam.getWife().equals(child.getId())){
							if(fam.getHusband().equals(hus_id)){
								System.out.println("Anomaly US17: Familymember  " +child.getName() + " " + child.getId()+" married to descendent "+wife_id);
							}
						}
					}
				}
				
			}
		}
		
		for (Entry<String, Individual> individual1 : individual_Map.entrySet()){
			Individual first = individual1.getValue();
			for (Entry<String, Individual> individual2 : individual_Map.entrySet()){
				Individual second = individual2.getValue();
				if (first.getName().equals(second.getName())){
						if(m3.add(individual1.getKey())){
							if(m3.add(individual2.getKey()))
								System.out.println("Anomaly US25 : Individuals "+individual1.getKey()+" and "+individual2.getKey()+" have same first name" + second.getName());
						}
						else if(m3.add(individual2.getKey())){
							System.out.println("Anomaly US25 :Individuals "+individual2.getKey()+"and " + individual1.getKey()+" have same first name "+first.getName());
						}
					
				}
			}
		}
		for(Entry<String, Individual> individual1 : individual_Map.entrySet()){
			if(individual1.getValue().getDeathDate()!=null){
				System.out.println("Anomaly US29: Individual: " + individual1.getValue().getId() + " " +individual1.getValue().getName() + "is deceased on: " + individual1.getValue().getDeathDate());
			}
		}
		for(Entry<String, Family> family : family_Map.entrySet()){
			Family family1 = family.getValue();
			String hus = individual_Map.get(family1.getHusband()).getName();
			String wife = individual_Map.get(family1.getWife()).getName();
			Date death_hus = individual_Map.get(family1.getHusband()).getDeathDate();
			Date death_wife = individual_Map.get(family1.getWife()).getDeathDate();
			if(death_wife != null){
				System.out.println("Anomaly US30: Individual: " + family1.getWife() +" "+ wife + " is married");
			}
			if(death_hus != null){
				System.out.println("Anomaly US30: Individual: " + family1.getHusband() +" "+ hus + " is married");
			}
			
		}
		for(Entry<String, Family> family : family_Map.entrySet()){
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			Date nowTime = new Date();
			int diffDay = 0;
			int diffMonth = 0;
			
				Family fam = family.getValue();
				cal1.setTime(fam.getMarriageDate());
				cal2.setTime(nowTime);
				diffDay = Math.abs(cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH));
				diffMonth = Math.abs(cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH));
				// Compare the dates
				if (diffMonth == 0 && diffDay < 60) {
					System.out.println("Anomaly US39: Individuals: " + fam.getId() + " : " + fam.getHusband() + " & "+ fam.getWife() + " has an anniversery less than 60 days from today");
				}
			

		}
	}
	
}
	
