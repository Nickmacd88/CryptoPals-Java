package Set1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Challenge 8:
   In this file are a bunch of hex-encoded ciphertexts.

	One of them has been encrypted with ECB.
	
	Detect it.
	
	Remember that the problem with ECB is that it is stateless and deterministic; the 
	same 16 byte plaintext block will always produce the same 16 byte ciphertext.
 */
public class Set8 {
	/*
	 * This challenge had two ways to solve it in my mind. I could either count how many
	 * duplicate bytes of 16 there are in each string and return the one with the largest
	 * duplicate or I could measure the edit-distance between all of the hex bytes.
	 * I chose to modify my code from Challenge 5 and 6 to count the edit-distance of
	 * all of the bytes in each array reasoning that the one with the smallest edit distance
	 * would be the one encrypted by AES ECB.
	 */
	public static TreeMap<Float, String> detectAES(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		String msg ="";
		Byte[] finalArr = null;
		Byte[] tempArr = null;
		byte[] testArr = null;
		byte[] a = null;
		int keySize = 16, keyCount = 0, count = 0; //keySize is set to 16 as that is the expected keysize for AES ECB
        float testHamCount = 0, hamCount = 0;
        TreeMap<Float, String> keySet = new TreeMap<>();
		while(scanner.hasNext()) {
			msg = scanner.next();
			a = Set1.hexToByte(msg);
			for(int j = 0; j < ((a.length)-(((a.length)%keySize))); j+= keySize) {
				testArr = new byte[keySize];
				for(int k = 0; k < keySize; k++) {//creates the testkey array, for testing.
	                testArr[k] = a[j+k];
	                }
	            //This for-loop will test they key against all keysize bytes left in the array until we are 
	            //out of keysize bites.
				for(int l = j+keySize; l < ((a.length)-(((a.length)%keySize))); l++) {
	                testHamCount += Set1.hammingDistOfByte(testArr[keyCount], a[l]);
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

    public static void main(final String[] args) throws FileNotFoundException{
		File file = new File("D:\\Coding\\Eclipse Workspace\\Crypto Pals\\src\\Set1\\Chal 8");
        TreeMap<Float, String> a = new TreeMap<>(detectAES(file));
        for(int i = 0 ; i< a.size(); i++) {
        	System.out.println(i+ ". Edit Dist: " + a.firstKey() + " the String: " +a.get(a.firstKey()));
        	a.remove(a.firstKey());
        }
        
    } 
    
}
