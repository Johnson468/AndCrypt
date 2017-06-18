package com.example.turtl.andcrypt;
/*
    todo:
    Find a way to encrypt and decrypt images

 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class selectImages extends AppCompatActivity {
    //Path to get to the image
    private String imagePath;
    //Key for the encryption
    private String encryptionKey;
    //The view of the image on the page
    ImageView iv;
    static File encryptionKeyFile;
    Uri selectedImageUri;
    //Not sure
    private static final int SELECT_PICTURE = 1;
    //The key that isn't really used here
    private static String key;
    //The file representation of the image
    private File image;
    Key Key;
    byte[] fileContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_images);
        iv = (ImageView) findViewById(R.id.imageView);
        loadImages();
        setUpKeyFile();
        getKey();
        byte[] contents;
        try {
            Key = keyGen(encryptionKey);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Call to reload the images
    public void reloadImages(View view) {
        loadImages();
    }
    //Load images from the files
    private void loadImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
    }
    //When an image is selected
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                imagePath = getPath(selectedImageUri);
                //Set up the image object using the image path of the selected image
                setupFile(imagePath);
                //Put the image into the image view
                iv.setImageURI(selectedImageUri);
                fileContents = getFile();

            }
        }
    }
    //Get the string representation of a filepath to the selected image
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            finish();
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            //cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    //Return the contents of the file as a byte array
    public byte[] getFile() {
        if (selectedImageUri==null) {
            showAlert("Error","An error has occured");
            return null;
        }
        File f = new File(selectedImageUri.toString());
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        byte[] content = null;
        try {
            content = new byte[is.read()];
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            is.read(content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }

    //Load the hashed string from the key file
    public void getKey() {
        Scanner myScan = null;
        try {
            myScan = new Scanner(encryptionKeyFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (myScan.hasNext()) {
            encryptionKey = myScan.nextLine();
        }
    }



    //Sets up the file object, passing it the file location as a string
    private void setupFile(String path) {
        image = new File(getRealPathFromURI_API19(this.getApplicationContext(),selectedImageUri));
    }
    //Get the file path from the Uri
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
    //Encrypt an image
    public void encrypt(View view) throws Exception {
        if (selectedImageUri ==null) {
            showAlert("Error", "An error has occured");
            return;
        }
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,  Key);
            encrypted = cipher.doFinal(fileContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveFile(encrypted);
        showAlert("Success","Image encrypted successfully");
    }


    //Decrypt an image
    public void decrypt(View view) throws Exception {
        if (selectedImageUri==null) {
            showAlert("Error", "An error has occured");
            return;
        }
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, Key);
            decrypted = cipher.doFinal(fileContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveFile(decrypted);
        showAlert("Success","Image encrypted successfully");
    }


    public void saveFile(byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(imagePath);
        fos.write(bytes);
        fos.close();
    }



    //Generate a key object from the hashed password string
    public static Key keyGen(String k) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(k.toCharArray(), k.getBytes(), 1000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    //Logout and return to the password screen
    public void logOut(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void showAlert(String title, String message) {
        AlertDialog attemptWarning = new AlertDialog.Builder(this).create();
        attemptWarning.setTitle(title);
        attemptWarning.setMessage(message);
        attemptWarning.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        attemptWarning.show();
    }
    public void setUpKeyFile() {
        encryptionKeyFile = new File(selectImages.this.getApplicationContext().getFilesDir(),"pubKey.txt");
    }

}
