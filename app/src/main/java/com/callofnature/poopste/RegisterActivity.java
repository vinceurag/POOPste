package com.callofnature.poopste;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.callofnature.poopste.adapters.CollegesAdapter;
import com.callofnature.poopste.helpers.PoopsteApi;
import com.callofnature.poopste.model.Model;
import com.callofnature.poopste.model.Nearby;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    ImageButton select;
    private Uri imageUri;
    String picturePath;
    String imageName;
    Long tsLong;
    String ts;
    String fileName = "";

    File fileToUpload;
    AmazonS3 s3;
    TransferUtility transferUtility;

    ProgressDialog dialog;
    EditText editText_FirstName;
    EditText editText_LastName;

    UserSessionManager session;
    String googleId;
    ArrayList<CollegesAdapter> colleges = new ArrayList<CollegesAdapter>();

    int myCollege = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");

        Intent regCaller = getIntent();
        googleId = regCaller.getStringExtra("googleId");

        editText_FirstName = (EditText) findViewById(R.id.editText_firstName);
        editText_LastName = (EditText) findViewById(R.id.editText_lastName);
        final List<String> spinnerArray =  new ArrayList<String>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);
        PoopsteApi.get("register/colleges", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONArray jsonColleges = new JSONArray(response);

                    for (int i = 0; i < jsonColleges.length(); i++) {
                        try {
                            JSONObject objCollege = jsonColleges.getJSONObject(i);
                            CollegesAdapter ca = new CollegesAdapter();
                            Log.e("ID", objCollege.getInt("id") + "");
                            ca.setCollegeId(objCollege.getInt("id"));
                            ca.setCollegeName(objCollege.getString("college"));
                            Log.e("ID", ca.getCollegeId() + "");
                            Log.e("name", ca.getCollegeName() + "");

                            colleges.add(ca);
                            spinnerArray.add(objCollege.getString("college"));
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myCollege = colleges.get(position).getCollegeId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                myCollege = 1;
            }
        });

        // callback method to call credentialsProvider method.
        credentialsProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();

        select = (ImageButton) findViewById(R.id.imageButton);

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

    public void register(View view) {

        tsLong = System.currentTimeMillis()/1000;
        ts = tsLong.toString();

        fileName = "profile_pictures/" + ts + "-" + imageName;

        TransferObserver transferObserver = transferUtility.upload(
                "poopste-images-bucket",     /* The bucket to upload to */
                fileName,    /* The key for the uploaded object */
                fileToUpload,        /* The file where the data to upload exists */
                CannedAccessControlList.PublicRead
        );




        Log.d("TAG", "FROM UPLOAD: " + picturePath);
        Log.d("URL", transferObserver.getAbsoluteFilePath());
        transferObserverListener(transferObserver);
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
                    dialog = new ProgressDialog(RegisterActivity.this);
                    dialog.setMessage("Registering...");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.show();
                    Log.d("PROGRESS", "In Progress");
                } else if(state == TransferState.COMPLETED) {
                    try {
                        JSONObject jsonParams = new JSONObject();
                        jsonParams.put("google_id", googleId);
                        jsonParams.put("profile_pic", "https://s3-ap-southeast-1.amazonaws.com/poopste-images-bucket/" + fileName);
                        jsonParams.put("first_name", editText_FirstName.getText().toString());
                        jsonParams.put("last_name", editText_LastName.getText().toString());
                        jsonParams.put("college_id", myCollege);
                        StringEntity entity = new StringEntity(jsonParams.toString());
                        PoopsteApi.post("register", entity, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String response = new String(responseBody, "UTF-8");
                                    JSONObject obj = new JSONObject(response);
                                    JSONObject meta = obj.getJSONObject("meta");
                                    JSONObject data = obj.getJSONObject("data");

                                    int id = Integer.parseInt(obj.getString("id"));
                                    String googleId = data.getString("google_id");
                                    String fullName = data.getString("fullname");
                                    String profilePic = data.getString("profile_pic");
                                    int collegeId = Integer.parseInt(data.getString("college_id"));
                                    String token = meta.getString("token");


                                    Model.setUserId(id);
                                    Model.setFullName(fullName);
                                    Model.setGoogleId(googleId);
                                    Model.setProfilePic(profilePic);
                                    Model.setToken(token);
                                    Model.setCollegeId(collegeId);

                                    Toast.makeText(getApplicationContext(), googleId + " - " + fullName, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.putExtra("userRegistered", true);
                                    RegisterActivity.this.startActivity(intent);
                                    finish();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                try {
                                    String response = new String(responseBody, "UTF-8");
                                    Log.d("JSON Response", response + "error - " + statusCode);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(), "ERROR: Unable to register", Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                int percentage = (int) ((float) bytesCurrent/bytesTotal * 100);
                Log.e("currentBytes", bytesCurrent + "");
                Log.e("totalBytes", bytesTotal + "");
                Log.e("percentage", percentage +"");
                Log.e("intPerc", percentage + "");
                dialog.setProgress(percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
    }
}
