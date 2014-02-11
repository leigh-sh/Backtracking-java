import java.io.File;
import java.util.Vector;
import java.util.Arrays;

/**
 * In the SmartKey class, a SmartKey object is created. The object's main
 * purpose is the suggestCompletions methods which return a list of suggested
 * completions when an array of pressed keys is the parameter.
 * 
 * @author lshaniv
 *
 */

public class SmartKeys {
	
    private static final int KEY0 = 0;
    private static final int KEY1 = 1;
    private static final int ALL_LETTERS_INDICATOR = 10;
    private static final char[][] KEYS_LETTERS = {
    	{},
    	{},
    	{'a','b','c'}, //letters of 2
        {'d','e','f'}, //letters of 3
        {'g','h','i'}, //letters of 4
        {'j','k','l'}, //letters of 5
        {'m','n','o'}, //letters of 6
        {'p','q','r','s'}, //letters of 7
        {'t','u','v'}, //letters of 8
        {'w','x','y','z'}, //letters of 9
        {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q',
         'r','s','t','u','v','w','x','y','z'} //letters of all ABC
    };
     
    private Vector<String> _dict; //The SmartKeys dictionary
    private int _maxLength; //The dictionary's longest word's length
	
    /**
     * Constructs a SmartKeys object based on the lexicon in the specified
     * path. The lexicon file has one word per line.
     * @param fn path to the lexicon
     */
    
    public SmartKeys(String fn){
        _maxLength = 0;
    	_dict = createDict(fn);
    }
    
    /**
     * Returns a list of suggested completions. The order of the returned
     * suggestions is undefined.
     * @param pressedKeys the keys pressed by the user.
     * @return array of completions
     */
    
    public String[] suggestCompletions(int[] pressedKeys){
    	return suggestCompletions(pressedKeys, _maxLength);
    }
    
    /**
     * Returns a list of suggested completions. The order of the returned
     * suggestions is undefined.
     * @param pressedKeys the keys pressed by the user
     * @param maxWordLength length of maximal completion (whole word length).
     * @return array of completions
     */
    
    public String[] suggestCompletions(int[] pressedKeys, int maxWordLength){
    	int minLength = minLength(pressedKeys, maxWordLength);
        Vector<String> temp = new Vector<String>();
        int[] relevantKeys = relevantKeys(pressedKeys, maxWordLength);
        possibleCombos(temp, relevantKeys,  "", minLength, 0);
        return toStringArray(temp);
    }
     
    /**
     * This method returns the number of minimal length of a word that can be
     * returned from suggestCompletions according to the pressedKeys and the 
     * maxLength
     * @param pressedKeys the given pressedKeys
     * @param maxLength the maximum length of a word which will be returned by
     * suggestCompletions
     * @return the number of minimal length of a word
     */
    
    private int minLength(int[] pressedKeys, int maxLength){
    	int min=0;
    	for(int i=0; i<pressedKeys.length; i++){
            if(hasLetters(pressedKeys[i]) && i<maxLength){
                min++;
            }
    	}
    	return min;
    }
    
    /**
     * This method returns a Vector representation of the lexicon contained in
     * the given path.
     * @param fn path to the lexicon
     * @return Vector representation of lexicon
     */
    
    private Vector<String> createDict(String fn){
    	LexScanner sc = new LexScanner(new File(fn));
    	Vector<String> temp = new Vector<String>();
    	int i=0;
    	while(sc.hasNext()){
            temp.addElement(sc.nextLine());
            //check that word is consisted only of small letters
            if(!temp.elementAt(i).matches("[a-z]*")){
                temp.remove(i);
            }
            else{
                if(_maxLength<temp.elementAt(i).length()){
                    _maxLength = temp.elementAt(i).length();
                    //finding length of maximal longest word in lexicon
                    if(temp.elementAt(i).matches("[a-z]*")){
        				
                    }
                }
                i++;
            }
    	}
    	return temp;
    }
    
    /**
     * This method returns a String array based on the given String Vector.
     * @param temp the given String Vector
     * @return a String array based on the given String Vector
     */
      
    private String[] toStringArray(Vector<String> temp){
    	String[] array = new String[temp.size()];
    	for(int i=0; i<array.length; i++){
            array[i] = temp.elementAt(i);
    	}
    	Arrays.sort(array);
    	return array;
    }
     
    /**
     * This method returns a new array. The new array will be identical to the
     * given array, only it will not contain 0s and 1s, and 
     * ALL_LETTERS_INDICATOR will be added in the beginning as many times as 
     * needed
     * @param pressedKeys the given array
     * @param maxLength the array will not be longer than maxLength
     * @return a new array based on the given one, without 0s and 1s
     */
    
    private int[] relevantKeys(int[] pressedKeys, int maxLength){
    	int[] relevantKeys = new int[maxLength];
    	int i=0;
    	int j=0;
    	while(i<relevantKeys.length){
            if(j<pressedKeys.length){
                if(hasLetters(pressedKeys[j])){
                    //adding a key to new array if not 0 or 1
                    relevantKeys[i] = pressedKeys[j];
                    i++;
                }
                j++;
            }
            else{
                relevantKeys[i] = ALL_LETTERS_INDICATOR;
                i++;
            }
    	}
    	return relevantKeys;
    }
        
    /**
     * This method returns true iff the given digit is not 0 or 1
     * @param digit the given digit
     * @return true iff digit is not 0 or 1
     */
    
    private boolean hasLetters(int digit){
        return(digit != KEY0 && digit != KEY1);
    }
    
    /**
     * This method adds the given Vector String all of the words which are
     * completions of the digits represented by letters in the given array.
     * @param combs the given Vector String
     * @param keys the given array
     * @param word changes with the recursion, empty word at the beginning
     * @param keyIndex indicates the depth of the recurtion - the index of
     * 'keys' array on which we are looking 
     */
    
    private void possibleCombos(Vector<String> combs, int[] keys, String word,
                                int minLength, int keyIndex){
    	if(word.length() >= minLength){
            //means the current word is a potential completion
            if(_dict.contains(word)){
                combs.add(word);
            }
    	}
    	if(keyIndex==keys.length){
            return;
    	}
    	for(int i=0; i<KEYS_LETTERS[keys[keyIndex]].length; i++){
            word+=KEYS_LETTERS[keys[keyIndex]][i];
            possibleCombos(combs, keys, word, minLength,keyIndex+1);
            word = word.substring(0,word.length()-1); //backtracking
    	}
    }
}
