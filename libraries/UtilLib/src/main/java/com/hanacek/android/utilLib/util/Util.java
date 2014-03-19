package com.hanacek.android.utilLib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Util {
	
    /**
	 * unique device uid
	 * 
	 * @return
	 */
	public static String getDeviceHardwareUid() {
		return
	        Build.BOARD.length()%10+ Build.BRAND.length()%10 +
	        Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
	        Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
	        "-" + Build.ID + "-" + Build.MANUFACTURER.length()%10 +
	        Build.MODEL + Build.PRODUCT +
	        Build.TAGS + Build.TYPE +
	        Build.USER ; //13 digits
	}
    
	public static String getMd5Hash(String input) {
	    try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
		    byte[] messageDigest = md.digest(input.getBytes());
		    BigInteger number = new BigInteger(1, messageDigest);
		    String md5 = number.toString(16);
		
		    while (md5.length() < 32) {
		    	md5 = "0" + md5;
		    }
	          
	        return md5;
	    } 
	    catch(NoSuchAlgorithmException e) {
	    	Log.error(e);
	    	return null;
	    }
	}
	
	public static byte[] stream2Bytes(InputStream is) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
		    byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
	}
	
	public static String formatDate(long ts) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
        return sdf.format(new Date(ts));
    }
    
    public static RectF rectFromString(String coordinates) {
        String c[] = coordinates.replaceAll("[{|}]", "").split(",");     
        float left = Float.parseFloat(c[0]);
        float top = Float.parseFloat(c[1]);
        float width = Float.parseFloat(c[2]);
        float height = Float.parseFloat(c[3]);
        return new RectF(left, top, left+width, top+height);
    }
    
    public static RectF rectFromJsonObject(JSONObject json) throws Exception {
        RectF result = new RectF();
        JSONObject leftTop = json.getJSONObject("left_top");
        JSONObject rightBottom = json.getJSONObject("right_bottom");
        
        result.left = (float)leftTop.getDouble("x");
        result.top = (float)leftTop.getDouble("y");
        result.right = (float)rightBottom.getDouble("x");
        result.bottom = (float)rightBottom.getDouble("y");
        
        return result;
    }
    
    public static RectF createRectFfromRect(Rect rect) {
        return new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }
    
    public static String readFile(File file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));  
            String line;   
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            } 
        }
        catch (IOException e) {
            Log.error(e);
            return null;
        }

        return text.toString();
    }
    
    public static View findViewByTag(Object tag, View[] views) {
        if (tag == null) {
            return null;
        }
        
        for (View view : views) {
            if (tag.equals(view.getTag())) {
                return view; 
            }
        }
        return null;
    }
    
    public static Bitmap pictureDrawable2Bitmap(PictureDrawable pictureDrawable){
        Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bitmap;
    }
    
    public static String serializePostParams(List<NameValuePair> params) {
        StringBuilder serialized = new StringBuilder();
        for (NameValuePair nameValuePair : params) {
            serialized.append("&")
                    .append(Uri.encode(nameValuePair.getName()))
                    .append("=")
                    .append(Uri.encode(nameValuePair.getValue()));
            Log.debug("serialized:" + serialized.toString());
        }
        return serialized.toString();
    }
    
    public static List<NameValuePair> deserializePostParams(String postParams) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (final String keyValuePair : postParams.split("&")) {
            if (TextUtils.isEmpty(keyValuePair)) {
                continue;
            }
            
            final String[] keyValueArray = keyValuePair.split("=");
            if (keyValueArray.length != 2) {
                Log.error("Erorr while deserializing postParams.");
                continue;
            }
            
            Log.debug("Utils - deserializePostParams(), key: " + keyValueArray[0] + ", value:" + keyValueArray[1]);
            
            NameValuePair nvp = new NameValuePair() {
                @Override
                public String getValue() {
                    return Uri.decode(keyValueArray[1]);
                }
                
                @Override
                public String getName() {
                    return Uri.decode(keyValueArray[0]);
                }
            };
            params.add(nvp);
        }
        
        return params;
    }
    
    /**
     * find out if the device has connection or not
     * 
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager conMgr =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        
        if (i == null) {
            return false;
        }
        if (!i.isConnectedOrConnecting()) {
            return false;
        }
        if (!i.isAvailable()) {
            return false;
        }
        return true;
    }
    
    public static String rectToString(Rect rect) {
        return new StringBuilder("l: ").append(rect.left).append(", r: ").append(rect.right).append(", t: ").
                append(rect.top).append(", b: ").append(rect.bottom).toString();
    }
    
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    
    public static String removeParagraphSpaces(String s){
        return s.replaceAll("(<p>)(\\s+)(\\S)", "$1$3");
    }

    public static String formatSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Longer lasting toast.
     *
     * @param context
     * @param message
     * @param lengthLongMultiplier
     */
    public static void toastMeLonger(Context context, String message, final int lengthLongMultiplier) {
        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        Thread t = new Thread() {
            public void run() {
                int count = 0;
                try {
                    while (count < lengthLongMultiplier) {
                        toast.show();
                        sleep(1850);
                        count++;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    public static String join(String[] arr, String expression) {
        List<String> clear = new ArrayList<String>();
        for (int i = 0; i < arr.length; i++) {
            if (!TextUtils.isEmpty(arr[i])) {
                clear.add(arr[i]);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clear.size(); i++) {
            sb.append(clear.get(i));
            if (i+1 != clear.size()) {
                sb.append(expression);
            }
        }
        return sb.toString();
    }
}
