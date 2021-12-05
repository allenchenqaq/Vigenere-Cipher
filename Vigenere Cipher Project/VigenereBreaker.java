/**
 * @author (Allen Chen) 
 * @version (2021/11/30)
 */
import java.util.*;
import edu.duke.*;
import java.io.File;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        StringBuilder sb = new StringBuilder();
        for (int i = whichSlice; i < message.length(); i += totalSlices){
            sb.append(message.charAt(i));
        }
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker cc = new CaesarCracker();
        for (int i = 0; i < klength; i ++){
            key[i] = cc.getKey(sliceString(encrypted, i, klength));
        }
        return key;
    }

    public void breakVigenere () {
        FileResource fr = new FileResource();
        String str = fr.asString();
        int[] findKey = tryKeyLength(str, 4, 'e');
        VigenereCipher vc = new VigenereCipher(findKey);
        System.out.println(vc.decrypt(str));
    }  
    
    public HashSet<String> readDictionary(FileResource fr){
        HashSet<String> hash = new HashSet<String>();
        for (String read: fr.lines()){
            hash.add(read.toLowerCase());
        }
        return hash;
    }
    
    public int countWords(String message, HashSet<String> dictionary){
        int count = 0;
        String[] msg = message.split("\\W+");
        for (String str: msg){
            String lowerStr = str.toLowerCase();
            if (dictionary.contains(lowerStr)){
                count ++;
            }
        }
        return count;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary){
        /*int max = 0;
        for (int i = 1; i <= 100; i ++){
            int[] key = tryKeyLength(encrypted, i, mostCommonCharIn(dictionary));
            VigenereCipher vc = new VigenereCipher(key);
            int curr = countWords(vc.decrypt(encrypted),dictionary);
            if (curr > max){
                max = curr;
            }
        }
        for (int i = 1; i <= 100; i ++){
            int[] key = tryKeyLength(encrypted, i, mostCommonCharIn(dictionary));
            VigenereCipher vc = new VigenereCipher(key);
            int curr = countWords(vc.decrypt(encrypted),dictionary);
            if (curr == max){
                String result = vc.decrypt(encrypted);
                return result ;
            }
        }
        return null;*/
        int maxCount = 0;
        int keyLength = 0;
        char mostCommonChar = mostCommonCharIn(dictionary);
        for (int k = 1; k <= 100; k++){    
            int[] key = tryKeyLength(encrypted, k, mostCommonChar);
            VigenereCipher vc = new VigenereCipher(key);
            String message = vc.decrypt(encrypted);
            int currCount = countWords(message, dictionary);
            int totalCount = message.split("\\W+").length;
            if (maxCount < currCount){
                maxCount = currCount; 
            }
        }
        
        for (int k = 1; k <= 100; k++){
            int[] key = tryKeyLength(encrypted, k, mostCommonChar);
            VigenereCipher vc = new VigenereCipher(key);
            String message = vc.decrypt(encrypted);
            int currCount = countWords(message, dictionary);
            if (maxCount == currCount){
               return message;
            }
        }
        return null;
    }
    
    public void breakVigenere2(){
        FileResource fr = new FileResource();
        String str = fr.asString();
        FileResource dr = new FileResource("./dictionaries/English");
        HashSet<String> dict = readDictionary(dr);
        String msg = breakForLanguage(str, dict);
        System.out.println(msg);
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary){
        int max = 0;
        HashMap<Character, Integer> freq = new HashMap<Character, Integer>();
        for (String word: dictionary){
            String lowerWord = word.toLowerCase();
            for (char ch: lowerWord.toCharArray()){
                if (freq.containsKey(ch)){
                    freq.put(ch, freq.get(ch) + 1);
                }
                else{
                    freq.put(ch, 1);
                }
            }
        }
        for (char ch: freq.keySet()){
            int value = freq.get(ch);
            if (value > max){
                max = value;
            }
        }
        for (char ch: freq.keySet()){
            if (freq.get(ch) == max){
                return ch;
            }
        }
        return 'n';   
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages){
        int max = 0;
        for(String langs : languages.keySet()) {
            HashSet<String> dictionary = new HashSet<String>(languages.get(langs));
            String testDecryption = breakForLanguage(encrypted, dictionary);
            int value = countWords(testDecryption, dictionary);
            if(value > max) {
                max = countWords(testDecryption, dictionary);
            }
        }
        for (String langs: languages.keySet()){           
            String msg = breakForLanguage(encrypted, languages.get(langs));
            int count = countWords(msg, languages.get(langs));
            if (count == max){        
                System.out.println("The decrypted message is: " + msg);
                System.out.println("The language chosen is: " + langs);
            }
        }
    }
    
    public void breakVigenere3() {
        FileResource fr = new FileResource();
        String str = fr.asString();
        String[] langs = {"Danish","Dutch","English","French","German","Italian","Portuguese","Spanish"};
        HashMap<String, HashSet<String>> dictionary = new HashMap<String, HashSet<String>>();
        for(int i = 0; i < langs.length; i++){
            FileResource file = new FileResource("./dictionaries/" + langs[i]);
            HashSet<String> languageDictionary = readDictionary(file);
            dictionary.put(langs[i], languageDictionary);
        }
        breakForAllLangs(str, dictionary);
    }
}