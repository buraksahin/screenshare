package network;

public class Utils {
	public static byte[] long2bytes(long l) {
		byte[] bytes = new byte[8];
		// Apply right shift
		bytes[0] = (byte) (l >> 56);
		bytes[1] = (byte) (l >> 48);
		bytes[2] = (byte) (l >> 40);
		bytes[3] = (byte) (l >> 32);
		bytes[4] = (byte) (l >> 24);
		bytes[5] = (byte) (l >> 16);
		bytes[6] = (byte) (l >> 8);
		bytes[7] = (byte) (l >> 0);
		return bytes;
	}

	public static long bytes2long(byte[] bytes) {
		// Apply left shift
		long l0 = ((long) bytes[0] & 0xff) << 56;
		long l1 = ((long) bytes[1] & 0xff) << 48;
		long l2 = ((long) bytes[2] & 0xff) << 40;
		long l3 = ((long) bytes[3] & 0xff) << 32;
		long l4 = ((long) bytes[4] & 0xff) << 24;
		long l5 = ((long) bytes[5] & 0xff) << 16;
		long l6 = ((long) bytes[6] & 0xff) << 8;
		long l7 = ((long) bytes[7] & 0xff) << 0;

		return l0 | l1 | l2 | l3 | l4 | l5 | l6 | l7;
	}
}
