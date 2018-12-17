package htbus.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressUtil {
	/**2018年6月24日下午10:15:53
	 *
	 * @author Jokki
	 *  
	 */
	
	private static final Inflater infl = new Inflater();

	 

    private static final Deflater defl = new Deflater();

 

    private CompressUtil(){

 

    }

 

    public static byte[] uncompress(byte[] inputByte) throws IOException {

        int len = 0;

        infl.setInput(inputByte);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] outByte = new byte[1024];

        try {

            while (!infl.finished()) {

                len = infl.inflate(outByte);

                if (len == 0) {

                    break;

                }

                bos.write(outByte, 0, len);

            }

            infl.end();

        } catch (Exception e) {

        } finally {

            bos.close();

        }

        return bos.toByteArray();

    }

 

    public static byte[] compress(byte[] inputByte) throws IOException {

        int len = 0;

        defl.setInput(inputByte);

        defl.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] outputByte = new byte[1024];

        try {

            while (!defl.finished()) {

                len = defl.deflate(outputByte);

                bos.write(outputByte, 0, len);

            }

            defl.end();

        } finally {

            bos.close();

        }

        return bos.toByteArray();

    }
}
