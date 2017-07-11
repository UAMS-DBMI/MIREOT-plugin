package edu.uams.dbmi.protege.plugin.mireot.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

//Author: Cheng Chen, UALR
//The nameList is gained from OBO, EBI, BioPortal websites

public class AdditionalOntologyList {
	
	private Map<String, String> nameMap;
	private String[] keyStrings;
	Properties propFile = new Properties();

	public AdditionalOntologyList() {
		
		try {
			propFile.load(this.getClass().getResourceAsStream("/main/resources/ontologyURImappings.properties"));  //Fixed NUll pointer exception
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.nameMap = new HashMap<String, String>();
		
		//loading ontology mappings into memory
		for(Object keyObj : propFile.keySet()){
			String key = (String) keyObj;
			this.nameMap.put(key,  propFile.getProperty(key));
		}
		
		//loading into memory and sorting printable names
		Set<String> keys = this.nameMap.keySet();
		this.keyStrings = keys.toArray(new String[keys.size()]);
		
		java.util.Arrays.sort(this.keyStrings, java.text.Collator.getInstance());
		
	}

	public String[] getNames(){
		return this.keyStrings;
	}
	
	public String getLink(String nameString) {
		return this.nameMap.get(nameString); 
	}

}
