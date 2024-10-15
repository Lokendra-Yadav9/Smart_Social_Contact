package com.smart.helper;

public class UserSortName {

	  public String getInitials(String name) {
	        String[] words = name.split("\\s+");
	        
	        if (words.length == 0) {
	            return "";
	        } else if (words.length == 1) {
	            return words[0].substring(0, 1).toUpperCase();
	        } else {
	            String firstInitial = words[0].substring(0, 1).toUpperCase();
	            String lastInitial = words[words.length - 1].substring(0, 1).toUpperCase();
	            return firstInitial + lastInitial;
	        }
	    }
	  
	  
	  public String[] extractNameParts(String fullName) {
	        String[] names = fullName.split(" ");
	        String firstName = names.length > 0 ? names[0] : ".........";
	        String lastName = names.length > 1 ? names[1] : "................";

	        return new String[]{firstName, lastName};
	    }
	  public String extractROleParts(String Role) {
		  String[] names = Role.split("_");
		  String firstName = names.length > 0 ? names[0] : ".........";
		  String lastName = names.length > 1 ? names[1] : "................";
		  
		  return lastName;
	  }
}
