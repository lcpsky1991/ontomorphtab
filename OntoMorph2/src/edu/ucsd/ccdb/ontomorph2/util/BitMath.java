package edu.ucsd.ccdb.ontomorph2.util;

public class BitMath {

	public static final int sizeOf8BitUnsignedInt = 1;
	public static final int sizeOfUnsignedInt = 4;
	public static final int sizeOfFloat = 4;
	public static final int sizeOfUnsignedShort = 2;
	
	/* from (http://darksleep.com/player/JavaAndUnsignedTypes.html)
	 *  What is going on there is that we are promoting a (signed) byte to int, 
	 *  and then doing a bitwise AND operation on it to wipe out everything but 
	 *  the first 8 bits. Because Java treats the byte as signed, if its unsigned 
	 *  value is above > 127, the sign bit will be set, and it will appear to java 
	 *  to be negative. When it gets promoted to int, bits 0 through 7 will be the 
	 *  same as the byte, and bits 8 through 31 will be set to 1. So the 
	 *  bitwise AND with 0x000000FF clears out all of those bits. Note that this could 
	 *  have been written more compactly as;
     *
	 * 0xFF & buf[index]
     * 
     * Java assumes the leading zeros for 0xFF, and the bitwise & operator automatically
     *  promotes the byte to int. But I wanted to be a tad more explicit about it.
     * 
     * The next thing you'll see a lot of is the <<, or bitwise shift left operator. 
     * It's shifting the bit patterns of the left int operand left by as many bits 
     * as you specify in the right operand So, if you have some int foo = 0x000000FF, 
     * then (foo << 8) == 0x0000FF00, and (foo << 16) == 0x00FF0000.
     * 
     * The last piece of the puzzle is |, the bitwise OR operator. Assume you've loaded 
     * both bytes of an unsigned short into separate integers, so you have 0x00000012 and 
     * 0x00000034. Now you shift one of the bytes by 8 bits to the left, so you have 
     * 0x00001200 and 0x00000034, but you still need to stick them together. So you 
     * bitwise OR them, and you have 0x00001200 | 0x00000034 = 0x00001234. This is 
     * then stored into Java's 'char' type.
     * 
     * That's basically it, except that in the case of the unsigned int, you have to 
     * now store it into the long, and you're back up against that sign extension 
     * problem we started with. No problem, just cast your int to long, then do the
     *  bitwise AND with 0xFFFFFFFFL. (Note the trailing L to tell Java this is a 
     *  literal of type 'long' integer.) 
     *  
     *  Modified for little endianness (http://en.wikipedia.org/wiki/Little_endian#Little-endian)
	 */
	public static int convertByteArrayToInt(byte[] buf) {
		int firstByte = 0;
		int secondByte = 0;
		int thirdByte = 0;
		int fourthByte = 0;
		
		int out = 0;
		if (buf.length == sizeOf8BitUnsignedInt) {
			out = (int)buf[0];
		} else if (buf.length == sizeOfUnsignedInt) {
			long anUnsignedInt = 0;
			
			firstByte = (0x000000FF & ((int)buf[3]));
			secondByte =(0x000000FF & ((int)buf[2]));
			thirdByte = (0x000000FF & ((int)buf[1]));
			fourthByte =(0x000000FF & ((int)buf[0]));
			
			anUnsignedInt = ((long) (firstByte << 24 | secondByte << 16 |
									 thirdByte <<8 | fourthByte))
									 & 0xFFFFFFFL;
			
			out = (int)anUnsignedInt;
		} else if (buf.length == sizeOfUnsignedShort) {
			char anUnsignedShort = 0;
			
			firstByte = (0x000000FF & ((int)buf[1]));
			secondByte = (0x000000FF & ((int)buf[0]));
			
			anUnsignedShort = (char) (firstByte << 8 | secondByte);
			out = (int)anUnsignedShort;
		}
		return out;
	}
	
	//from http://www.captain.at/howto-java-convert-binary-data.php
	public static float convertByteArrayToFloat (byte[] arr, int start) {
		int i = 0;
		int len = sizeOfFloat;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);
	}
}
