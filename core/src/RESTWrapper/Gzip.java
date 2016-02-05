package RESTWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;

public class Gzip {

	public static byte[] compress(String data) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data.getBytes(StandardCharsets.UTF_8));
		gzip.close();
		byte[] compressed = bos.toByteArray();
		bos.close();
		return compressed;
	}
	
	public static String decompress(byte[] compressed) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(bis);
		BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		gis.close();
		bis.close();
		return sb.toString();
	}
	
	public static String decompress(String zipText) throws IOException {
	    byte[] compressed = Base64.decodeBase64(zipText);
	    if (compressed.length > 4)
	    {
	        GZIPInputStream gzipInputStream = new GZIPInputStream(
	                new ByteArrayInputStream(compressed, 4,
	                        compressed.length - 4));

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        for (int value = 0; value != -1;) {
	            value = gzipInputStream.read();
	            if (value != -1) {
	                baos.write(value);
	            }
	        }
	        gzipInputStream.close();
	        baos.close();
	        String sReturn = new String(baos.toByteArray(), "UTF-8");
	        return sReturn;
	    }
	    else
	    {
	        return "";
	    }
	}
}