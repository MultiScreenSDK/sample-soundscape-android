/**
 * ****************************************************************************
 * Copyright (c) 2015 Samsung Electronics
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * *****************************************************************************
 */


package com.samsung.soundscape.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.samsung.soundscape.App;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Util {
    //The debug tag.
    private static final String TAG = "SamsungSoundScape";

    //The flag to output debug message or not.
    public static final boolean DEBUG = true;


    /**
     * Generate the Log.d log message.
     * @param message The message to be output.
     */
    public static void d(String message) {
        if (DEBUG) Log.d(TAG, message);
    }

    /**
     * Generate the Log.e log message.
     * @param message The message to be output.
     */
    public static void e(String message) {
        if (DEBUG) Log.e(TAG, message);
    }


    public static String getWifiName() {
        String ssid = "none";
        if (isWiFiConnected()) {
            WifiManager wifiManager = (WifiManager) App.getInstance().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ssid = wifiInfo.getSSID();
        }

        return ssid;
    }

    public static boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

//    /**
//     * Get custom font.
//     * @param context the context object.
//     * @return the Typeface object.
//     */
//    public static Typeface customFont(Context context) {
//        return setFont(context, Constants.FONT_FOLDER + Constants.FONT_ROBOTO_LIGHT);
//    }
//
//    /**
//     * Get custom font with italic style.
//     * @param context the context object.
//     * @return the Typeface object.
//     */
//    public static Typeface italicFont(Context context) {
//        return setFont(context, Constants.FONT_FOLDER + Constants.FONT_ROBOTO_LIGHT_ITALIC);
//    }
//
//    private static Typeface setFont(Context context, String font) {
//        return Typeface.createFromAsset(context.getAssets(), font);
//    }
//
//    /**
//     * Return the screen size
//     *
//     * @return The point object contains screen width and height.
//     */
//    public static Point getDisplaySize() {
//        WindowManager wm = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        return size;
//    }
//
//
//    /**
//     * Calculate the DP by give pixels.
//     * @param context the context object.
//     * @param pixels the pixels to be calculated.
//     * @return the dps.
//     */
//    public static int getDipsFromPixel(Context context, float pixels) {
//        // Get the screen's density scale
//        final float scale = context.getResources().getDisplayMetrics().density;
//        // Convert the dps to pixels, based on density scale
//        return (int) (pixels * scale + 0.5f);
//    }
//
//
//    /**
//     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
//     * disk filename.
//     */
//    public static String hashKeyForDisk(String key) {
//        String cacheKey;
//        try {
//            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
//            mDigest.update(key.getBytes());
//            cacheKey = bytesToHexString(mDigest.digest());
//        } catch (NoSuchAlgorithmException e) {
//            cacheKey = String.valueOf(key.hashCode());
//        }
//        return cacheKey;
//    }
//
//    private static String bytesToHexString(byte[] bytes) {
//        // http://stackoverflow.com/questions/332079
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < bytes.length; i++) {
//            String hex = Integer.toHexString(0xFF & bytes[i]);
//            if (hex.length() == 1) {
//                sb.append('0');
//            }
//            sb.append(hex);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * Generate the key of image which is to be sent to TV.
//     *
//     * @param path The file path of the image.
//     * @return the key of the image in disk cache, or null if file does not exist.
//     */
//    public static String getTVImageKey(String path) {
//        return generateDiskCacheKey("tv", path);
//    }
//
//    /**
//     * Generate the key of image which is to be displayed in photo slider screen.
//     *
//     * @param path The file path of the image.
//     * @return the key of the image in disk cache, or null if file does not exist.
//     */
//    public static String getSliderImageKey(String path) {
//        return generateDiskCacheKey("slider", path);
//    }
//
//    /**
//     * Generate the key of image which is to be displayed in photo slider screen.
//     *
//     * @param path The file path of the image.
//     * @return the key of the image in disk cache, or null if file does not exist.
//     */
//    public static String getThumbnailImageKey(String path) {
//        return generateDiskCacheKey("thumbnail", path);
//    }
//
//    /**
//     * Generate the disk cache key by given image type and path.
//     * @param type the image type.
//     * @param path the image path.
//     * @return the disk cache key.
//     */
//    private static String generateDiskCacheKey(String type, String path) {
//        if (path == null) {
//            return null;
//        }
//
//        File file = new File(path);
//        if (!file.exists()) {
//            return null;
//        }
//
//        long size = file.length();
//        long lastModification = file.lastModified();
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(type);
//        sb.append("-");
//        sb.append(path);
//        sb.append("-");
//        sb.append(size);
//        sb.append("-");
//        sb.append(lastModification);
//
//        return sb.toString();
//    }
//
//    @SuppressLint("NewApi")
//    public static long getAvailableSpaceSize() {
//        StatFs sfs = new StatFs(App.getInstance().getCacheDir().getPath());
//
//        //For the older Android OS version.
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            return sfs.getAvailableBlocks() * sfs.getBlockSize();
//        }
//
//        //For the version equals or great than JELLY_BEAN_MR2
//        return sfs.getAvailableBytes();
//    }
//
//
//    /**
//     * Get photo's orientation.
//     * @param filepath the photo path.
//     * @return the orientation value which is defined in android.media.ExifInterface.
//     */
//    public static int getExifOrientation(String filepath) {
//        int degree = 0;
//        ExifInterface exif = null;
//
//        //Try to get ExifInterface.
//        try {
//            exif = new ExifInterface(filepath);
//        } catch (IOException ex) {
//        }
//
//        //Read the orientation value if ExifInterface is not null.
//        if (exif != null) {
//            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//        }
//        return degree;
//    }
//
//    /**
//     * Rotate the bitmap according to the orientation value.
//     * @param orientation the orientation of image.
//     * @param originalBitmap the original bitmap.
//     * @return the new bitmap.
//     */
//    public static Bitmap rotateBitmap(int orientation, Bitmap originalBitmap){
//
//        if(originalBitmap == null) return null;
//        Bitmap result = originalBitmap;
//
//        //No change for ExifInterface.ORIENTATION_NORMAL
//        if(orientation > ExifInterface.ORIENTATION_NORMAL){
//
//            //Get the rotation matrix.
//            Matrix matrix = getRotateMatrix(orientation);
//
//            //Rotate the bitmap.
//            result = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(),
//                    originalBitmap.getHeight(), matrix, true);
//
//
//            //Recycle the original bitmap if the new bitmap is created.
//            if(originalBitmap != result){
//                originalBitmap.recycle();
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * Return the rotation matrix according to orientation.
//     * @param orientation the orientation of image.
//     * @return the matrix object.
//     */
//    private static Matrix getRotateMatrix(int orientation){
//        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                matrix.setScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                matrix.setRotate(180);
//                break;
//            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                matrix.setRotate(180);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_TRANSPOSE:
//                matrix.setRotate(90);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                matrix.setRotate(90);
//                break;
//            case ExifInterface.ORIENTATION_TRANSVERSE:
//                matrix.setRotate(-90);
//                matrix.postScale(-1, 1);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                matrix.setRotate(-90);
//                break;
//        }
//
//        return matrix;
//
//    }
//
//
//    /**
//     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object when decoding
//     * bitmaps using the decode* methods from {@link android.graphics.BitmapFactory}. This implementation calculates
//     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
//     * having a width and height equal to or larger than the requested width and height.
//     *
//     * @param options An options object with out* params already populated (run through a decode*
//     *            method with inJustDecodeBounds==true
//     * @param reqWidth The requested width of the resulting bitmap
//     * @param reqHeight The requested height of the resulting bitmap
//     * @param aggressiveScaleDown do aggressively scale down if it is true.
//     * @return The value to be used for inSampleSize
//     */
//    public static int calculateInSampleSize(BitmapFactory.Options options,
//                                            int reqWidth, int reqHeight, boolean aggressiveScaleDown) {
//        // BEGIN_INCLUDE (calculate_sample_size)
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//
//            // This offers some additional logic in case the image has a strange
//            // aspect ratio. For example, a panorama may have a much larger
//            // width than height. In these cases the total pixels might still
//            // end up being too large to fit comfortably in memory, so we should
//            // be more aggressive with sample down the image (=larger inSampleSize).
//
//            if (aggressiveScaleDown) {
//                long totalPixels = width * height / (inSampleSize * inSampleSize);
//
//                // Anything more than 2x the requested pixels we'll sample down further
//                final long totalReqPixelsCap = reqWidth * reqHeight * 2;
//
//                while (totalPixels > totalReqPixelsCap) {
//                    inSampleSize *= 2;
//                    totalPixels /= 2;
//                }
//            }
//        }
//        return inSampleSize;
//        // END_INCLUDE (calculate_sample_size)
//    }
//
//    /**
//     * Decode and sample down a bitmap from a file to the requested width and height.
//     *
//     * @param filename The full path of the file to decode
//     * @param reqWidth The requested width of the resulting bitmap
//     * @param reqHeight The requested height of the resulting bitmap
//     * @param aggressiveScaleDown Perform aggressively scale down if true.
//     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
//     *         that are equal to or greater than the requested width and height
//     */
//    public static Bitmap decodeSampledBitmapFromFile(String filename,
//                                                     int reqWidth, int reqHeight,
//                                                     boolean aggressiveScaleDown) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        options.inPreferQualityOverSpeed = false;
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filename, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, aggressiveScaleDown);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(filename, options);
//        return bitmap;
//    }
//
//    /**
//     * Decode and sample down a bitmap from a file to the requested width and height.
//     * Rotate the image if necessary.
//     *
//     * @param filename The full path of the file to decode
//     * @param reqWidth The requested width of the resulting bitmap
//     * @param reqHeight The requested height of the resulting bitmap
//     * @param aggressiveScaleDown Aggressively scale down if true.
//     * @return The byte array of bitmap sampled down from the original with the same aspect ratio and dimensions
//     *         that are equal to or greater than the requested width and height
//     */
//    public static byte[] decodeSampledBitmapByteArrayFromFile(String filename,
//                                                     int reqWidth, int reqHeight, boolean aggressiveScaleDown) {
//        byte[] bytes = null;
//        Bitmap bm = null;
//
//        //Try to load bitmap with target width and height.
//        try {
//            Bitmap bitmap = decodeSampledBitmapFromFile(filename, reqWidth, reqHeight, aggressiveScaleDown);
//            bm = Util.rotateBitmap(Util.getExifOrientation(filename), bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //Compress the bitmap to JPEG formant with 65% compression rate.
//        if (bm != null) {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            try {
//                bm.compress(Bitmap.CompressFormat.JPEG, 65, bos);
//                bytes = bos.toByteArray();
//            } finally {
//                try {
//                    bos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (bm != null && !bm.isRecycled()) {
//                    bm.recycle();
//                }
//            }
//        }
//        return bytes;
//    }
//
//    /**
//     * Decode and sample down a bitmap from a file.
//     *
//     * @param filename The full path of the file to decode
//     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
//     *         that are equal to or greater than the requested width and height
//     */
//    public static Bitmap decodeSampledBitmapFromFile(String filename) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        options.inPreferQualityOverSpeed = false;
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeFile(filename, options);
//    }


    /**
     * Return whether or not WiFi is connected.
     *
     * @return true if WiFi is connected, otherwise false.
     */
    public static boolean isWiFiConnected() {
        //Get connectivity manager.
        ConnectivityManager connManager = (ConnectivityManager) App.getInstance().
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //get network info object.
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }


    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static String getFriendlyTvName(String name) {
        if (name == null) {
            return null;
        }

        if (name.startsWith("[TV]")) {
            return name.substring(4).trim();
        }

        return name;
    }
}
