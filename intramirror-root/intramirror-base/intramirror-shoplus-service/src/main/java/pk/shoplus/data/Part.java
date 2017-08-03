
package pk.shoplus.data;


public class Part {


    private String[] parts;


    public Part(String str) {
        parts  = str.replaceAll("\\s*", "").split(",");
    }


    public boolean contains(String str) {
        for (String part: parts) {
            if (part.equals(str)) {
                return true;
            }
        }
        return false;
    }


}
