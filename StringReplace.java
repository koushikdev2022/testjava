import java.util.Arrays;
import java.util.List;

public class StringReplace {

    StringReplace(){

    }
    public static String hideMessage(String message) {
        List<String> smallWords = Arrays.asList("the", "of", "is", "to", "a", "an", "in", "it", "and");
        String[] words = message.split(" ");
        StringBuilder result = new StringBuilder();
        for(int i = 0;i<words.length;i++){
            String original = words[i];
            String clean = original.replaceAll("[^a-zA-Z]", "");
            String processed = original;
            if ((i + 1) % 3 == 0) {
                processed = "***";
            } 
            else if (smallWords.contains(clean.toLowerCase())) {
                processed = original;
            } 
            else if (clean.length() % 2 == 0 && clean.length() > 0) {
                char first = clean.charAt(0);
                char last = clean.charAt(clean.length() - 1);
                String middle = clean.substring(1, clean.length() - 1);
                String swapped = last + middle + first;
                String punctuation = original.substring(clean.length());
                processed = swapped + punctuation;
            }
            result.append(processed).append(" ");
        }
        return result.toString().trim();
    }
    public static void main(String Args[]){
        String input = "The purpose of this exercise is to tesd logical thinking";
        System.out.println(hideMessage(input));
    }
}
