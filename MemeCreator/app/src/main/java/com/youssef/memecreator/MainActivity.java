package com.youssef.memecreator;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView memeImageView;
    private TextView topText;
    private TextView bottomText;
    private EditText editTop;
    private EditText editbottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memeImageView = (ImageView) findViewById(R.id.memeImage);
        topText = (TextView) findViewById(R.id.memeTopText);
        bottomText = (TextView) findViewById(R.id.memeBottomText);
        editTop = (EditText) findViewById(R.id.editTop);
        editbottom = (EditText) findViewById(R.id.editBottom);
    }

    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,null,null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            memeImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    public void createMeme(View view) {
        topText.setText(editTop.getText().toString());
        bottomText.setText(editbottom.getText().toString());
        hideKeyboard();
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private Bitmap getScreenshot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bm;
    }

    private void store(Bitmap bm, String fileName){
        String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }

        File file = new File(dirPath, fileName);
        try{
            FileOutputStream fis = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fis);
            fis.flush();
            fis.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMeme(View view) {
        View content = findViewById(R.id.lay);
        Bitmap bm = getScreenshot(content);
        String fileName = "meme" + System.currentTimeMillis() + ".png";
        store(bm, fileName);
    }
}
