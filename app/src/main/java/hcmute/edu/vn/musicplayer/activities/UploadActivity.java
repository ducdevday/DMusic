package hcmute.edu.vn.musicplayer.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import hcmute.edu.vn.musicplayer.MyDialog;
import hcmute.edu.vn.musicplayer.NetworkChangeReceiver;
import hcmute.edu.vn.musicplayer.R;
import hcmute.edu.vn.musicplayer.events.NetworkChangeListener;
import hcmute.edu.vn.musicplayer.models.Song;

public class UploadActivity extends AppCompatActivity implements NetworkChangeListener {
    ImageView imgArtistCover;
    Button btnMusicFileUpload, btnMusicImgUpload, btnUpload;
    TextView txtMusicFileUpload, txtMusicImgUpload;
    EditText edtTxtSongTitleUpload, edtTxtArtistNameUpload;
    ImageButton btnBack;

    MyDialog myDialog;


    MediaMetadataRetriever metadataRetriever;
    DatabaseReference mDatabaseReference ;
    StorageReference mStorageReference;

    Uri audioUri, imgUri ;

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initView();
        handleEvent();

        networkChangeReceiver =new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        networkChangeReceiver.addListener(this);
    }

    private void initView() {
        imgArtistCover = findViewById(R.id.imgArtistCover);
        btnMusicFileUpload = findViewById(R.id.btnMusicFileUpload);
        btnMusicImgUpload = findViewById(R.id.btnMusicImgUpload);
        btnUpload = findViewById(R.id.btnUpload);
        txtMusicFileUpload = findViewById(R.id.txtMusicFileUpload);
        txtMusicImgUpload = findViewById(R.id.txtMusicImgUpload);
        edtTxtSongTitleUpload = findViewById(R.id.edtTxtSongTitleUpload);
        edtTxtArtistNameUpload = findViewById(R.id.edtTxtArtistNameUpload);
        btnBack = findViewById(R.id.btnBack);

        myDialog = new MyDialog(this);

        mDatabaseReference = FirebaseDatabase.getInstance("https://musicplayer-b04ab-default-rtdb.firebaseio.com/").getReference("discovery");
        mStorageReference = FirebaseStorage.getInstance("gs://musicplayer-b04ab.appspot.com").getReference("discovery_song");
        metadataRetriever = new MediaMetadataRetriever();

    }


    private void handleEvent() {
        btnMusicFileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusicFile(v);
            }
        });
        btnMusicImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusicImg(v);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMusic(v);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openMusicFile(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i,1);
    }

    private void openMusicImg(View v) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, 2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode  == RESULT_OK && data.getData() != null){
            audioUri = data.getData();
            metadataRetriever.setDataSource(this,audioUri);
            String fileNames = getFileName(audioUri);
            txtMusicFileUpload.setText(fileNames);

            edtTxtArtistNameUpload.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            edtTxtSongTitleUpload.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        }

        if(requestCode == 2 && resultCode  == RESULT_OK && data.getData() != null ){
            imgUri = data.getData();
            String fileNames = getFileName(imgUri);
            txtMusicImgUpload.setText(fileNames);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                imgArtistCover.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  String getFileName(Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(index);

                }
            }
            finally {
                cursor.close();
            }
        }

        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut +1);

            }
        }
        return  result;
    }
    private void uploadMusic(View v) {
        if(txtMusicFileUpload.getText().toString().trim().equals("") || txtMusicImgUpload.getText().toString().trim().equals("No file selected") || edtTxtSongTitleUpload.getText().toString().trim().equals("") || edtTxtArtistNameUpload.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please fill all form", Toast.LENGTH_LONG).show();
            return;
        }
        if(audioUri != null){
            myDialog.show();
            final  StorageReference storageReference = mStorageReference.child(System.currentTimeMillis()+"."+getfileextension(audioUri));
            storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uriSong) {
                            final  StorageReference nextStorageReference = mStorageReference.child(System.currentTimeMillis()+"."+getfileextension(imgUri));
                            nextStorageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    nextStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uriImage) {
                                            Song song = new Song(edtTxtSongTitleUpload.getText().toString(), edtTxtArtistNameUpload.getText().toString().trim(),uriImage.toString(), uriSong.toString());
                                            String uploadId = mDatabaseReference.push().getKey();
                                            mDatabaseReference.child(uploadId).setValue(song);
                                        }
                                    });
                                }
                            });
                        }
                    });
                    myDialog.cancel();
                    Toast.makeText(getApplicationContext(), "Upload finished", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });}

    }

    private  String getfileextension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if(!isConnected)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}