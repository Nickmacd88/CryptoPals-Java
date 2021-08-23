package Set1;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/*
 * The methods in this set are all static methods designed to be used on a byte[] object
 * with a few exceptions. Noted above each method is the challenge that it is trying to solve
 * as well as a description of how it works.
 * TO-DO: Increase the amount of exception handling and testing for edge-cases and proper 
 * formatting for each method.
 * TO-DO: The detectSingleCharXOR method needs to be 'cleaned up'.
 */
public class Set1 {
/*
 * The main method is currently set to solve Set 1 Challenge 6.
 */
    public static void main(final String[] args) throws FileNotFoundException{
        File file = new File("Chall6Data.txt");
        Scanner scanner = new Scanner(file);
        StringBuilder test = new StringBuilder();
        int testCount = 0;
        while(scanner.hasNext()){
            test.append(scanner.next());
            testCount++;
        }
        breakRepeatingXOR(Base64.getDecoder().decode(test.toString()));
        scanner.close();

        
    }   
    /*
    *hexToByte takes a hex-encoded String object and converts it to a byte[]
    *@parameter s: The hex-encoded string to be converted to a byte[].
    */
    public static byte[] hexToByte(String s){
        final byte[] arr = new byte[s.length()/2];
        for(int i = 0; i< s.length(); i+=2){
            arr[i/2] = (byte) ((byte)(Character.digit(s.charAt(i), 16)<<4) + Character.digit(s.charAt(i+1), 16));
        }
        return arr;
    }
    
    /*
     * encodeHexString takes a byte[] and returns a hex-encoded String
     * that is able to be printed
     * @parameter b: The byte[] to be converted into a hex-encoded String.
     */
    public static String encodeHexString(byte[] b) {
    	StringBuilder str = new StringBuilder();
    	for(byte x : b) {
    		str.append(byteToHex(x));
    	}
    	return str.toString();
    }
    /*
     * Helper method for encodeHexString that works on an individual byte level.
     * This was necessary because a byte can have a max value of 127. This method takes the
     * two hex-digits, converts the higher-order digit and shifts the bits right by 4 to 
     * create space for the second set of bits.
     */
    private static String byteToHex(byte num) {
    	char[] hexDigits = new char[2];
    	hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
    	hexDigits[1] = Character.forDigit((num) & 0xF, 16);
    	return new String(hexDigits);
    }
    
    /*
     * hammingDistance finds the 'Hamming Distance' or number of differing
     * bytes between two byte-arrays and returns that number as an integer
     * @parameter a: first byte array you want to compare
     * @parameter b: second byte array to be compared.
     */
    public static int hammingDistance(byte[] a, byte[] b) {
    	byte c;
    	int hammingDist = 0;
    	for(int i = 0; i <a.length; i++) {
    		c = (byte) (a[i]^b[i]);
    		while(c>0) {
    			if(c%2 == 1) hammingDist++;
    			c >>= 1;
    		}
    	}
    	return hammingDist;
    }
    
    /*
     * hammingDistOfByte returns the hamming distance between two individual
     * bytes.
     */
    public static int hammingDistOfByte(byte a, byte b) {
    	byte c = (byte) (a^b);
    	int hammingDist = 0;
    	while(c>0) {
    		if(c%2 == 1) hammingDist++;
    		c >>= 1;
    	}
    	return hammingDist;
    }
    
    /*
    *This method takes two Hexadecimal strings, converts the to raw byte arrays
    *using the hexToByte method,XOR's them against each other, and then returns the XOR'd raw byte array
    This method is solves Challenge 3
    @param str1 the first hex string to XOR
    @param str2 the second hex string to XOR
    */
    public static byte[] xorOfHex(String str1, String str2){
        byte[] arr1 = hexToByte(str1);
        byte[] arr2 = hexToByte(str2);
        byte[] arr3 = new byte[arr1.length];
        for(int i =0;i<arr1.length;i++){
            arr3[i]= (byte) ((byte)arr1[i]^(byte)arr2[i]);
        }
        return arr3;
    }
    /*
    This method will take a Hex encoded string that has been XOR'd against a single character
    and return the most likely character that it has been XOR'd against. This is accomplished by 
    testing all ASCII characters from 0-127, scoring them using a frequency table, and then 
    returning the decrypted text that was found.
    This is the answer for Challenge 4.
    @param str - the string that has been encrypted using a single character.
    */
    public static void singleCharDecrypt(String str){
        int score = 0;
        int topScore = 0;
        int cipher = -1;
        byte[] arr1 = hexToByte(str);
        byte[] fin = new byte[arr1.length];
        byte[] fin2 = new byte[arr1.length];

        for(int i = 0; i <=127; i++){
            score = 0;
            
            for(int j = 0; j < arr1.length; j++){
                fin[j] = (byte) (arr1[j] ^ i);
                score += scoreByte(fin[j]);
            }

            if(score > topScore){
                topScore = score;
                fin2 = fin.clone();
                cipher = i;

            }
        }
        System.out.println("The decoded string is: " + new String(fin2));
        System.out.println("The cipher character is: " + (char) cipher);
        System.out.println("The score was " + topScore);

    }

    /*
     * This method detects a single character XOR from a .txt file of encoded Hex Stings.
     * It will take the filepath, and for each line XOR the line against every ASCII symbol 
     * from 0-127, record the 'highest-scoring' string using the scoreByte method and return
     * the highest scoring decoded string as well as the key used to encrypt the string
     * @param filepath: the file path of the .txt file of hex-encoded strings you want to decrypt
     * @throws FileNotFoundException in case you do not have a legit filepath name.
     */
public static Byte[] detectSingleCharXOR(Byte[] arr1) throws FileNotFoundException{
        int score = 0;
        int topScore = 0;
        int finalScore =0;
        int cipher = -1;
        byte[] fin = null;
        byte[] fin2 = null;
        Byte[] fin3 = null;

    
	    fin = new byte[arr1.length];
	    fin2 = new byte[arr1.length];
	        for(int i = 0; i <=127; i++){
	            score = 0;
	            for(int j = 0; j < arr1.length; j++){
	                fin[j] = (byte) (arr1[j] ^ i);
	                score += scoreByte(fin[j]);
	            }
	            // System.out.println("The decode string is: " + new String(fin) );
	            // System.out.println("Cipher " + i);
	            if(score > topScore){
	                topScore = score;
	                fin2 = fin.clone();
	            }
	        }
	    if(topScore > finalScore) {
            finalScore = topScore;
            fin3 = new Byte[fin2.length];
            for(int k = 0; k < fin2.length; k++){
                fin3[k] = fin2[k];
            }
	    }
    
        topScore = 0;
        fin2 = null;
    
        return fin3;
    }
	/*
	*Takes two unencoded strings, turns them into byte arrays and then encodes the first string against 
	*the key using a repeating key XOR. This means that the first byte of str1 is encoded against the 
	*first byte of the key until all bytes of str1 are encoded. This algorithm will loop back to the front 
	*of the key when the end is reached.
	*This method is the answer to Challenge 5.
	*@param str1 The message you want to encode.
	*@parm key The encoding key you want to use for repeating key XOR.
	*/
	public static byte[] repeatingKeyXOR(String str1, String key){
	    byte[] arr1 = str1.getBytes();
	    byte[] keyArr = key.getBytes();
	    byte[] arr3 = new byte[arr1.length];
	    int bitCount = 0;
	    for(int i =0;i<arr1.length;i++){
	        arr3[i]= (byte) ((byte)arr1[i]^(byte)keyArr[bitCount]);
	        bitCount++;
	        if(bitCount == keyArr.length) bitCount = 0;
	    }
	    return arr3;
	}

	/*This method will crack a Vignere or repeating key XOR cipher by finding the 
	 * best-guess of the key length, creating an ArrayList of ArrayLists of each 
	 * set of encrypted letters.  The method will then use the detectSingleCharXOR
	 * method to decrypt each ArrayList of letters. Finally, the method will re-
	 * assemble all of the letters and print out the decrypted message.
	 * This is the answer to Challenge 6.
	 * @parameter a: The array of bytes that is to be decrypted.
	 */
	public static void breakRepeatingXOR(byte[] a) throws FileNotFoundException {
        System.out.println("The number of bytes is: " + a.length);
        Byte[] b = null;
		TreeMap<Float, Integer> keySet = new TreeMap<>();
        ArrayList<ArrayList<Byte>> list = new ArrayList<>();
        StringBuilder fin= new StringBuilder();
        keySet = findKeySize(a);
        int keyLength = keySet.get(keySet.firstKey());
        for(int i = 0; i <+ keyLength; i++){ //initialize all of the byte array lists
            list.add(new ArrayList<Byte>());
        }
        int count = 0;
        for (int j =0; j<a.length; j++){ //separate all of the characters
            list.get(count).add(new Byte(a[j]));
            count++;
            if(count == keyLength) count = 0;
        }
        for (int k = 0; k < list.size(); k++){ //run each list of single characters through the single char decryption
            b = new Byte[list.get(k).size()];
            b = list.get(k).toArray(b); //creates a Byte[] for the detectSingleChar method
            b = detectSingleCharXOR(b); //runs the encrypted Byte[] through the decryption method
            list.get(k).clear(); //clears out the encrypted slot in the arraylist
            for(Byte c : b){
                list.get(k).add(c); //adds the new, decrypted bytes
            }
        }
        int listCount = 0;
        for(int l = 0; l< a.length; l++){
            fin.append((char)list.get(listCount).remove(0).byteValue());
            listCount++;
            if(listCount == keyLength){listCount=0;}
        }
        System.out.println(fin.toString());	
	}
	/*
	 * This is the second and was the hardest part of challenge 6 for me.
	 * This method will find the key size of a list of bytes that have been
	 * encrypted using a repeating key XOR method. It will iterate
	 * through all possible key-sizes from 2 to 40 or 1/2 the length of the array,
	 * whichever is smaller. Then, for each potential keySize, the method will
	 * find the edit distance between corresponding 'chunks' of that keySize.
	 * This method is designed to start at the left and find the edit distance between 
	 * each chunk until you run out of keySize size chunks. For each chunk, it will normalize
	 * the edit distance using the keySize and then add it to a total count. Once all 
	 * combinations have been exhausted, the method will calculate the average edit distance
	 * and store it in a sorted TreeMap for later retrieval. This method is an effective
	 * way to find the keySize, but it is not very efficient.
	 * This method is the helper method for Challenge 6.
	 * @parameter a: The byte[] of encrypted data one wishes to find the key size for.
	 */
	public static TreeMap<Float, Integer> findKeySize(byte[] a) {
        int keyCount = 0, count = 0;
        float testHamCount = 0, hamCount = 0;
		byte[] testArr;
		TreeMap<Float, Integer> keySet = new TreeMap<>();
		int maxKeyLength = Math.min(a.length/2, 40);
		for(int keySize = 2 ; keySize <= maxKeyLength; keySize++) { //for loop to iterate through all possible keysizes
            //this loop will iterate through all possible keys starting with the left most key and working right until we are out of space.
            for(int j = 0; j < ((a.length)-(((a.length)%keySize))); j+= keySize) {
				testArr = new byte[keySize];
				for(int k = 0; k < keySize; k++) {//creates the testkey array, for testing.
                    testArr[k] = a[j+k];
                    }
                //This for-loop will test they key against all keysize bytes left in the array until we are 
                //out of keysize bites.
				for(int l = j+keySize; l < ((a.length)-(((a.length)%keySize))); l++) {
                    testHamCount += hammingDistOfByte(testArr[keyCount], a[l]);
                    keyCount++;
					if(keyCount == testArr.length){
                        keyCount = 0;
                        testHamCount = (testHamCount/keySize);                
                        hamCount += testHamCount;
                        testHamCount = 0;
                        count++;
                    }
                }
			}
            hamCount = (hamCount / count);
            keySet.put(hamCount,keySize);
            keyCount = 0;
            count = 0;
            hamCount = 0;
        }
        return keySet;
	}

	/*
	 * The scoreByte method is the secret behind being able to detect any sort of single-character XOR
	 * The scores here are weighted for the most common down to the least common letters used in the
	 * English alphabet. The input is just a single integer which represents at byte and the output is 
	 * the corresponding point value for that byte.
	 * 
	 */
	public static int scoreByte(int b){
        int score = 0;
        switch(b){
        	case 32: //A space
        		score +=100;
        		break;
            case 65: //capitol A
                score += 43;
                break;
            case 66: //capitol B
                score+= 10;
                break;
            case 67: //capitol C
                score += 23;
                break;
            case 68: //capitol D
                score += 17;
                break;
            case 69: //Capitol E
                score += 56;
                break;
            case 70: //Capitol F
                score += 9;
                break;
            case 71: //Capitol G
                score += 12;
                break;
            case 72: //Capitol H
                score += 15;
                break;
            case 73: //Capitol I
                score += 38;
                break;
            case 74: //Capitol J
                score += 1;
                break;
            case 75: //capitol K
                score += 5;
                break;
            case 76: //capitol L
                score += 28;
                break;
            case 77: //capitol M
                score += 15;
                break;
            case 78: //capitol N
                score += 34;
                break;
            case 79: //capitol O
                score += 36;
                break;
            case 80: //capitol P
                score += 16;
                break;
            case 81: //capitol Q
                score += 1;
                break;
            case 82: //capitol R
                score += 38;
                break;
            case 83: //capitol S
                score += 29;
                break;
            case 84: //capitol T
                score += 35;
                break;
            case 85: //capitol U
                score += 18;
                break;
            case 86: //capitol V
                score += 5;
                break;
            case 87: //capitol W
                score += 7;
                break;
            case 88: //capitol X
                score += 2;
                break;
            case 89: //capitol Y
                score += 9;
                break;
            case 90: //capitol Z
                score += 1;
                break;
            case 97: //lowercase a
                score += 8;
                break;
            case 98: //lowercase b
                score+= 10;
                break;
            case 99: //lowercase c
                score += 4;
                break;
            case 100: //lowercase d
                score += 17;
                break;
            case 101: //lowercase e
                score += 56;
                break;
            case 102: //lowercase f
                score += 9;
                break;
            case 103: //lowercase g
                score += 12;
                break;
            case 104: //lowercase h
                score += 15;
                break;
            case 105: //lowercase i
                score += 38;
                break;
            case 106: //lowercase j
                score += 1;
                break;
            case 107: //lowercase k
                score += 5;
                break;
            case 108: //lowercase l
                score += 28;
                break;
            case 109: //lowercase m
                score += 15;
                break;
            case 110: //lowercase n
                score += 34;
                break;
            case 111: //lowercase o
                score += 36;
                break;
            case 112: //lowercase p
                score += 16;
                break;
            case 113: //lowercase q
                score += 1;
                break;
            case 114: //lowercase r
                score += 38;
                break;
            case 115: //lowercase s
                score += 29;
                break;
            case 116: //lowercase t
                score += 35;
                break;
            case 117: //lowercase u
                score += 18;
                break;
            case 118: //lowercase v
                score += 5;
                break;
            case 119: //lowercase w
                score += 6;
                break;
            case 120: //lowercase x
                score += 1;
                break;
            case 121: //lowercase y
                score += 9;
                break;
            case 122://lowercase z
                score += 1;
                break;        
            }
        return score;
    }

}
