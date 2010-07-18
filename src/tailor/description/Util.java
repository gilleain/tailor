package tailor.description;

public class Util {
    
    public static void printHierarchy(Description description) {
        System.out.println(description.getLevel() + " " + description.getID());
        for (Description subDescription : description.getSubDescriptions()) {
            printHierarchy(subDescription);
        }
    }
    
    public static void labelTree(Description description) {
        labelTree(description, 0);
    }
    
    private static int labelTree(Description description, int label) {
        description.setID(label);
        int currentLabel = label;
        for (Description subDescription : description.getSubDescriptions()) {
            currentLabel++;
            currentLabel = labelTree(subDescription, currentLabel);
        }
        return currentLabel;
    }

}
