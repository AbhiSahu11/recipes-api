package nl.abnamro.api.recipes.util;

 public class Util {

    public static boolean checkValidInput(String type){
        return type!=null && !type.isEmpty() && !type.isBlank();
    }


}
