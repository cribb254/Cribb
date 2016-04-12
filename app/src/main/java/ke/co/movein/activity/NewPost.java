package ke.co.movein.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.DividerItemDecoration;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.io.File;
import java.util.ArrayList;

import ke.co.movein.R;
import ke.co.movein.adapter.ImageListAdapter;
import ke.co.movein.adapter.MyPostsAdapter;
import ke.co.movein.utility.DbAdapter;
import ke.co.movein.utility.Functions;
import ke.co.movein.utility.RequestHandler;

public class NewPost extends AppCompatActivity {

    private final int PICK_IMAGE_MULTIPLE = 19;
    RecyclerView horizontalList;
    EditText etDesc;
    EditText etLoc;
    EditText etUnit;
    EditText etPrice;
    Spinner spPtyType;

    Functions fx;
    RequestHandler rh = new RequestHandler();
    ArrayList<String> imageList = null;

    SwipeRefreshLayout refresh;
    LinearLayout llHolder;
    CardView cvCurImages;
    RecyclerView rvEditCurImages;
    CardView cvCategory;

    DbAdapter db;

    boolean isEdit;
    int editPostID;
    public static ArrayList<String> oldImagesArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        oldImagesArr = new ArrayList<>();

        fx = new Functions(this);
        db = new DbAdapter(this);
        db.open();
        Cursor crsPtyType = db.browseTable("tbl_pty_type");
        db.close();

        try {
            isEdit = getIntent().getBooleanExtra("isEdit", false);
        }catch (Exception ex){
            isEdit = false;
        }

        TextView tvImage = (TextView) findViewById(R.id.tv_image);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        llHolder = (LinearLayout) findViewById(R.id.ll_holder);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });

        cvCategory = (CardView) findViewById(R.id.cv_category);

        etDesc = (EditText) findViewById(R.id.et_desc);
        etLoc = (EditText) findViewById(R.id.et_loc);
        etUnit= (EditText) findViewById(R.id.et_units);
        etPrice = (EditText) findViewById(R.id.et_price);

        spPtyType = (Spinner) findViewById(R.id.sp_pty_type);

        fx.populateSpinner(spPtyType, crsPtyType);

        horizontalList = (RecyclerView) findViewById(R.id.horizontal_rv);
        horizontalList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalList.addItemDecoration(new DividerItemDecoration(this, null));

        tvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPost.this, CustomPhotoGallery.class);
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
            }
        });

        if(isEdit)
            loadPostForEdit();

    }


    void loadPostForEdit(){
        try{
            JSONObject jObt = MyPostsAdapter.postDetails;
            String desc = jObt.getString("description");
            if(!desc.matches("null"))
                etDesc.setText(desc);
            etLoc.setText(jObt.getString("location"));
            etPrice.setText(jObt.getString("price"));
            etUnit.setText(String.valueOf(jObt.getInt("a_units")));
            spPtyType.setSelection(jObt.getInt("tbl_pty_type_id"));
            editPostID = jObt.getInt("id");
            cvCurImages = (CardView) findViewById(R.id.cv_current_images);
            rvEditCurImages = (RecyclerView) findViewById(R.id.rv);
            rvEditCurImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvEditCurImages.addItemDecoration(new DividerItemDecoration(this, null));
            loadImagesForPost();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tick, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_tick:
                attemptUpload();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_MULTIPLE){
            imageList = data.getStringArrayListExtra("data");
            horizontalList.setAdapter(new ImageListAdapter(imageList, false));
        }
    }

    boolean validateInput(){

        if(fx.isEditTextBlank(etPrice)){
            etPrice.setError(getString(R.string.required_field));
            return false;
        }

        if(fx.isEditTextBlank(etUnit)){
            etUnit.setError(getString(R.string.required_field));
            return false;
        }

        if(fx.isEditTextBlank(etLoc)){
            etLoc.setError(getString(R.string.required_field));
            return false;
        }

        return true;
    }

    boolean areImagesAttached(){
        return horizontalList.getAdapter() != null;
    }

    void attemptUpload(){

        if(spPtyType.getSelectedItemPosition()==0)
            Toast.makeText(this, "Please specify the type of property", Toast.LENGTH_SHORT).show();
        else
            if(validateInput())
                commenceUpload();
    }

    void commenceUpload(){
        AlertDialog.Builder aDialog = new AlertDialog.Builder(NewPost.this);
        aDialog.setTitle("Warning!");
        aDialog.setIcon(android.R.drawable.stat_sys_warning);
        aDialog.setMessage("No images attached! \nIt is recommended to attach at least one image to a post");
        aDialog.setNegativeButton("Proceed anyway", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                upload();
            }
        });
        aDialog.setPositiveButton("Attach image(s)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(NewPost.this, CustomPhotoGallery.class);
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
            }
        });

        if(isEdit || areImagesAttached())
            upload();
        else
            aDialog.show();
    }

    void upload(){
        if (fx.isConnectionAvailable()) {
            //Log.w("DATA", getParams().toString());
            new UploadPost().execute();
        } else
            Toast.makeText(NewPost.this, R.string.no_net, Toast.LENGTH_SHORT).show();
    }

    void loadImagesForPost(){
        if (fx.isConnectionAvailable()) {
            new LoadImagesForPost().execute();
        } else
            Toast.makeText(NewPost.this, R.string.no_net, Toast.LENGTH_SHORT).show();
    }

    private JSONObject getParams(){

        JSONObject uploadData = new JSONObject();
        /*
            structure
            [jsonObject] => post_details
            [jsonArray] => images
         */

        try {
            JSONObject postDetails = new JSONObject();
            JSONArray images = new JSONArray();

            postDetails.put("description", etDesc.getText().toString());
            postDetails.put("price", etPrice.getText().toString());
            postDetails.put("units", Integer.parseInt(etUnit.getText().toString()));
            postDetails.put("location", etLoc.getText().toString());
            postDetails.put("pty_type", spPtyType.getSelectedItemPosition());
            if(isEdit) 
                postDetails.put("post_id", editPostID);

            postDetails.put("user_id",
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("userId", 0));

            uploadData.put("post", postDetails);

            if(!isEdit) {
                if (horizontalList.getAdapter() != null) {
                    for (int i = 0; i < imageList.size(); i++)
                        images.put(fx.uploadStringImage(imageList.get(i)));
                    uploadData.put("images", images);
                } else
                    imageList = null;
            }
            else {
                //old images
                if(oldImagesArr.size() != 0)
                    uploadData.put("old_images", arrToJarr());
                //new images
                if (horizontalList.getAdapter() != null) {
                    for (int i = 0; i < imageList.size(); i++)
                        images.put(fx.uploadStringImage(imageList.get(i)));
                    uploadData.put("new_images", images);
                } else
                    imageList = null;
            }

        }catch (JSONException ex){
            ex.printStackTrace();
        }

        //Log.w("TO_UPLOAD", uploadData.toString());

        return uploadData;
    }

    JSONArray arrToJarr(){
        JSONArray jArr = new JSONArray();
        for(String str : oldImagesArr)
            jArr.put(str);

        return jArr;
    }

    class UploadPost extends AsyncTask<Void,Void,Boolean> {

        int success = 0;
        String message = "An error occurred! Please try again later";
        ProgressDialog pDialog = new ProgressDialog(NewPost.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Uploading post. \n\nPlease wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try{
                String response = rh.postJSON(
                        fx.endPoint(isEdit ? "update_post" : "add_post"), getParams());

                //Log.w("ADDPOST", response);
                try{
                    JSONObject jObt = new JSONObject(response);
                    success = jObt.getInt("success");
                    message = jObt.getString("message");

                    if(success == 1) {
                        fx.autoSync();
                    }

                }catch (JSONException ex){
                    ex.getMessage();
                }
            }catch (Exception ex){
                message = getString(R.string.net_timeout);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            pDialog.dismiss();

            if(imageList != null)
                cleanUp();

            Toast.makeText(NewPost.this, message, Toast.LENGTH_SHORT).show();

            if(success == 1){
                NavUtils.navigateUpTo(NewPost.this, new Intent(NewPost.this, MainActivity.class));
            }
        }

        void cleanUp(){
            for(String filePath: imageList) {
                try {
                    File file = new File(filePath.replace(".jpg", "_resized.jpg"));
                    file.delete();
                } catch (Exception ex) {
                }
            }
        }
    }

    class LoadImagesForPost extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = rh.postJSON(fx.endPoint("get_images"), new JSONObject()
                        .put("post_id", editPostID));
                //Log.w("ImagesOfPost", response);
                JSONArray jArr = new JSONObject(response).getJSONArray("images");
                for(int i=0; i<jArr.length();i++)
                    oldImagesArr.add(jArr.getString(i));
            }catch (Exception ex){
                ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(oldImagesArr.size() > 0){
                cvCurImages.setVisibility(View.VISIBLE);
                rvEditCurImages.setAdapter(new ImageListAdapter(oldImagesArr, true));
            }
        }
    }

}
