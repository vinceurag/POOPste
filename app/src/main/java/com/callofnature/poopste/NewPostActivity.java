package com.callofnature.poopste;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.Model;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NewPostActivity extends AppCompatActivity {

    AnimatedCircleLoadingView cv;
    Boolean disableButtons = false;
    TextView name;
    ImageView profilePic;
    LinearLayout linLay;
    TextView post_timestamp;
    EditText status;
    String msg_status;

    //For image upload
    private static final int FILE_SELECT_CODE = 1;
    ImageButton select;
    private Uri imageUri;
    String picturePath = "";
    String imageName;
    Long tsLong;
    String ts;
    String fileName;
    String awsUrl = "";

    File fileToUpload;
    AmazonS3 s3;
    TransferUtility transferUtility;
    SimpleDateFormat sdfDate;
    Date now;
    String strDate;

    boolean postIsSuccessful = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        getSupportActionBar().setTitle("New Post");

        name = (TextView) findViewById(R.id.name);
        profilePic = (ImageView) findViewById(R.id.profilePic);
        cv = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        linLay = (LinearLayout) findViewById(R.id.all_layout);
        post_timestamp = (TextView) findViewById(R.id.timestamp);
        status = (EditText) findViewById(R.id.status);

        cv.setAnimationListener(new AnimatedCircleLoadingView.AnimationListener() {
            @Override
            public void onAnimationEnd(boolean success) {
                if(postIsSuccessful) {
                    Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                    intent.putExtra("postCreated", "successful");
                    NewPostActivity.this.startActivity(intent);
                    finish();
                } else {
                    Log.d("END", "Animation End");
                    Intent intent = new Intent(NewPostActivity.this, MainActivity.class);
                    intent.putExtra("postCreated", "failed");
                    NewPostActivity.this.startActivity(intent);
                    finish();
                }
            }
        });

        sdfDate = new SimpleDateFormat("MMM. dd, yyyy hh:mm aaa");//dd/MM/yyyy
        now = new Date();
        strDate = sdfDate.format(now);
        Log.d("DATE", strDate);

        name.setText(Model.getFullName());

        post_timestamp.setText(strDate);
        Picasso.with(this)
                .load(Model.getProfilePic())
                .into(profilePic);

        // callback method to call credentialsProvider method.
        credentialsProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();

        select = (ImageButton) findViewById(R.id.uploadImage);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start file chooser
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(
                        Intent.createChooser(intent, "Select a file to upload"),
                        FILE_SELECT_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picturePath = cursor.getString(columnIndex);
            fileToUpload = new File(picturePath);
            Log.d("path", picturePath);

            imageName = fileToUpload.getName();

            Picasso.with(this)
                    .load(imageUri)
                    .fit()
                    .centerCrop()
                    .into(select);


        }
    }

    public void credentialsProvider(){

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:7a6ef930-d537-45df-b7d2-2ef57beef813", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){

        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));

    }

    public void setTransferUtility(){

        transferUtility = new TransferUtility(s3, getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // POST button has an id of 777
        final MenuItem menuItem = menu.add(Menu.NONE, 777, Menu.NONE, R.string.post);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.findItem(777).setEnabled(!disableButtons);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if(id == 777) {
            postStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    public void postStatus() {


        disableButtons = true;
        invalidateOptionsMenu();
        linLay.setVisibility(View.GONE);

        msg_status = status.getText().toString();

        cv.setVisibility(View.VISIBLE);

        cv.startIndeterminate();
        if(!picturePath.equals("")) {
            tsLong = System.currentTimeMillis()/1000;
            ts = tsLong.toString();

            fileName = "feed_images/" + ts + "-" + imageName;

            TransferObserver transferObserver = transferUtility.upload(
                    "poopste-images-bucket",     /* The bucket to upload to */
                    fileName,    /* The key for the uploaded object */
                    fileToUpload,        /* The file where the data to upload exists */
                    CannedAccessControlList.PublicRead
            );


            Log.d("TAG", fileToUpload.toString());

            Log.d("TAG", "FROM UPLOAD: " + picturePath);
            Log.d("URL", transferObserver.getAbsoluteFilePath());
            transferObserverListener(transferObserver);

        } else {
            Log.e("NO IMAGE", "You did not select any image");
            sendDataToApi();
        }

    }

    public void sendDataToApi() {
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("status", msg_status);
            jsonParams.put("photo", awsUrl);
            StringEntity entity = new StringEntity(jsonParams.toString());
            PoopsteApi.postWithHeader("posts", entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    cv.stopOk();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    cv.stopFailure();
                    postIsSuccessful = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Discard Post?")
                .content("You won't be able to retrieve discarded posts.")
                .autoDismiss(false)
                .positiveText("Stay")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .positiveColorRes(R.color.colorPrimary)
                .negativeText("Discard Post")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .canceledOnTouchOutside(false)
                .show();
    }

    /**
     * This is listener method of the TransferObserver
     * Within this listener method, we got status of uploading and downloading file,
     * to diaplay percentage of the part of file to be uploaded or downloaded to S3
     * It display error, when there is problem to upload and download file to S3.
     * @param transferObserver
     */

    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, final TransferState state) {
                if(state == TransferState.IN_PROGRESS) {
                    Log.d("PROGRESS", "In Progress");
                } else if(state == TransferState.COMPLETED) {
                    awsUrl = "https://s3-ap-southeast-1.amazonaws.com/poopste-images-bucket/" + fileName;
                    sendDataToApi();
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                int percentage = (int) ((float) bytesCurrent/bytesTotal * 100);
                Log.e("currentBytes", bytesCurrent + "");
                Log.e("totalBytes", bytesTotal + "");
                Log.e("percentage", percentage +"");
                Log.e("intPerc", percentage + "");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
    }
}
