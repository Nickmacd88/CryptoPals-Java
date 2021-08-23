package Set1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class Set8 {
	
	public static TreeMap<Float, String> detectAES(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		String msg ="";
		Byte[] finalArr = null;
		Byte[] tempArr = null;
		byte[] testArr = null;
		byte[] a = null;
		int keySize = 16, keyCount = 0, count = 0;
        float testHamCount = 0, hamCount = 0;
        TreeMap<Float, String> keySet = new TreeMap<>();
		while(scanner.hasNext()) {
			msg = scanner.next();
			a = hexToByte(msg);
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
	        keySet.put(hamCount,msg);
	        keyCount = 0;
	        count = 0;
	        hamCount = 0;
		}
        return keySet;
	}


/*
 * need to take the file
 * for each line
 * decrypt the hex string into a byte array
 * run the byte array through a modified keylength algorithm
 * 	not looking for the keylength itself, really just the smallest avg edit distance between 16-byte chunks
 * 
 */
//		}
		/*
		 * for each line of hex encoded bytes
		 * convert them to a byte[]
		 * put the byte[]
		 * Take an array of encodede bytes
		 * run it through the repeating key xor breaker, except no need to detect the keysize, it's 16 bytes
		 * score the results, keep the highest scoring result
		 */
	

    public static void main(final String[] args) throws FileNotFoundException{
		File file = new File("D:\\Coding\\Eclipse Workspace\\Crypto Pals\\src\\Set1\\Chal 8");
        TreeMap<Float, String> a = new TreeMap<>(detectAES(file));
        for(int i = 0 ; i< a.size(); i++) {
        	System.out.println(i+ ". Edit Dist: " + a.firstKey() + " the String: " +a.get(a.firstKey()));
        	a.remove(a.firstKey());
        }
        
    } 
    
    
    /*
    *This method takes two Hexadecimal strings, converts the to byte arrays
    *using the hexToByte method,XOR's them against each other, and then returns the XOR'd byte array
    */
    public static byte[] hexToByte(String s){
        final byte[] arr = new byte[s.length()/2];
        for(int i = 0; i< s.length(); i+=2){
            arr[i/2] = (byte) ((byte)(Character.digit(s.charAt(i), 16)<<4) + Character.digit(s.charAt(i+1), 16));
        }
        return arr;
    }
    
    public static int scoreByte(Byte[] b) {
    	int score =0;
    	for(Byte c : b) {
    		score += scoreByte(c.byteValue());
    	}
    	return score;
    }
    public static String encodeHexString(byte[] b) {
    	StringBuilder str = new StringBuilder();
    	for(byte x : b) {
    		str.append(byteToHex(x));
    	}
    	return str.toString();
    }
    public static String byteToHex(byte num) {
    	char[] hexDigits = new char[2];
    	hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
    	hexDigits[1] = Character.forDigit((num) & 0xF, 16);
    	return new String(hexDigits);
    }
    
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
    @param str1 the first hex string to XOR
    @param str2 the second hex string to XOR
    @param arr1 this is the converted byte array using the hexToByte() method
    @param arr2 this is the converted byte array using the hexToByte() method
    @arr3 this is a place holder array for the final XOR'd byte array
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
    @param str - the string that has been encrypted using a single character.
    */
    public static void singleCharDecrypt(String str){
        /*
        *have to take the string, xor it against the byte 0-255
        *for each xor, count the letter frequency and score it appropriately
        *If you have a new highschore, store the byte and the message
        */
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
        int topScore = -1;
        int finalScore =-1;
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
*Takes to unencoded strings, turns them into byte arrays and then encodes the first string against the key using a repeating key XOR.
*This means that the first byte of str1 is encoded against the first byte of the key until all bytes of str1 are encoded. This algorithm
*will loop back to the front of the key when the end is reached.
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

	public static String breakRepeatingXOR(byte[] a) throws FileNotFoundException {
//		find the length of the array
//		create a keysize from a.length, up to a keysize of 50
//		for each keysize, create an array of length a/keysize
//		starting at 0, find the hamming distance between all of the keysizes
//		store the three keysize with the lowest average in a sorted set
//		for each keysize
//			break the array into separate byte arrays, using linkedlists
//			for each byte array, use the detectsinglecharxor method to find the character
//			after that, decrypt the original byte array using the key
        Byte[] b = null;
		TreeMap<Float, Integer> keySet = new TreeMap<>();
        StringBuilder fin= new StringBuilder();
//        keySet = findKeySize(a);
//        int keyLength = keySet.get(keySet.firstKey());
        int keyLength = 16;
        ArrayList<ArrayList<Byte>> list = new ArrayList<>(keyLength);

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
        return fin.toString();
//        System.out.println(fin.toString());
        // for the keylength, create an array of Byte ListArry's
        // iterate through the byte array and add to the corresponding listarray
        // run each list array through the single key XOR solver
        // append the list back together and print the string
        
		
	}
	
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
            case 75: //capiotl k
                score += 5;
                break;
            case 76: //capitol l
                score += 28;
                break;
            case 77: //capitol m
                score += 15;
                break;
            case 78: //capitol n
                score += 34;
                break;
            case 79: //capitol o
                score += 36;
                break;
            case 80: //capitol p
                score += 16;
                break;
            case 81: //capitol q
                score += 1;
                break;
            case 82: //capitol r
                score += 38;
                break;
            case 83: //capitol s
                score += 29;
                break;
            case 84: //capitol t
                score += 35;
                break;
            case 85: //capitol u
                score += 18;
                break;
            case 86: //capitol v
                score += 5;
                break;
            case 87: //capitol w
                score += 7;
                break;
            case 88: //capitol x
                score += 2;
                break;
            case 89: //capitol y
                score += 9;
                break;
            case 90: //capitol z
                score += 1;
                break;
            case 97: //capitol A
                score += 8;
                break;
            case 98: //capitol B
                score+= 10;
                break;
            case 99: //capitol C
                score += 4;
                break;
            case 100: //capitol D
                score += 17;
                break;
            case 101: //Capitol E
                score += 56;
                break;
            case 102: //Capitol F
                score += 9;
                break;
            case 103: //Capitol G
                score += 12;
                break;
            case 104: //Capitol H
                score += 15;
                break;
            case 105: //Capitol I
                score += 38;
                break;
            case 106: //Capitol J
                score += 1;
                break;
            case 107: //capiotl k
                score += 5;
                break;
            case 108: //capitol l
                score += 28;
                break;
            case 109: //capitol m
                score += 15;
                break;
            case 110: //capitol n
                score += 34;
                break;
            case 111: //capitol o
                score += 36;
                break;
            case 112: //capitol p
                score += 16;
                break;
            case 113: //capitol q
                score += 1;
                break;
            case 114: //capitol r
                score += 38;
                break;
            case 115: //capitol s
                score += 29;
                break;
            case 116: //capitol t
                score += 35;
                break;
            case 117: //capitol u
                score += 18;
                break;
            case 118: //capitol v
                score += 5;
                break;
            case 119: //capitol w
                score += 6;
                break;
            case 120: //capitol x
                score += 1;
                break;
            case 121: //capitol y
                score += 9;
                break;
            case 122:
                score += 1;
                break;        
            }
        return score;
    }
}
