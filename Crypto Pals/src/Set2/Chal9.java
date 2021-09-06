package Set2;
/*
 * Implement PKCS#7 padding
A block cipher transforms a fixed-sized block (usually 8 or 16 bytes) of plaintext into ciphertext. But we almost never want to transform a single block; we encrypt irregularly-sized messages.

One way we account for irregularly-sized messages is by padding, creating a plaintext that is an even multiple of the blocksize. The most popular padding scheme is called PKCS#7.

So: pad any block to a specific block length, by appending the number of bytes of padding to the end of the block. For instance,

"YELLOW SUBMARINE"
... padded to 20 bytes would be:

"YELLOW SUBMARINE\x04\x04\x04\x04"
 */

public class Chal9{

	public static void main(String[] args) {
		byte[] test = new byte[32];
		for(int i = 0; i< test.length;i++) {test[i] = (byte)77;}
		test = padArr(test);
		
	}

    public static byte[] padArr(byte[] a){
        int padLength = 16 - a.length%16;
        if(padLength != 16){
            byte[] paddedBlock= new byte[a.length + padLength];

            for(int i = 0; i < a.length; i++){
                paddedBlock[i] = a[i];
            }
            for(int j = a.length;  j < paddedBlock.length; j++){
                paddedBlock[j] = ((byte) padLength);
            }
            return paddedBlock;
        }
        else{
            byte[] paddedBlock= new byte[a.length + 16];
            for(int i = 0; i < a.length; i++){
                paddedBlock[i] = a[i];
            }
            for(int j = a.length;  j < paddedBlock.length; j++){
                paddedBlock[j] = ((byte) 0);
            }
            return paddedBlock;
        	}    
        }
    
    public static byte[] unPadArr(byte[] a) {
    	int padLength = a[a.length-1];
    	if (padLength > 0 && padLength < 16) {
    		for(int i = padLength; i >1; i--) {
    			if(a[a.length-i] != padLength) return a;
    		}
    		return java.util.Arrays.copyOfRange(a, 0, a.length-padLength);
    		//create a new byte array and return that array
    	}
    	else if(padLength == 0) {
    		for(int i = 16; i>0; i--) {
    			if(a[i] != 0) return a;
    		}
    		return java.util.Arrays.copyOfRange(a,  0,  a.length-16);
    	}
    	return a;
    }
}