package me.lim_bo56.lnpp.utils;

import org.bukkit.Bukkit;

public class UtilMethods {

    private static UtilMethods instance = new UtilMethods();
    
    public static UtilMethods getInstance() {
    	return instance;
    }
    
    public boolean Is1_9() {
    	String version;

        try {

            version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
        
		if(version.equals("v1_9_R1")) {
			return true;
		} else {
			return false;
		}
    }
}
