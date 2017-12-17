package com.brentvanvosselen.oogappl.fragments.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.BitmapCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brentvanvosselen.oogappl.RestClient.models.Image;
import com.brentvanvosselen.oogappl.RestClient.models.User;
import com.brentvanvosselen.oogappl.activities.MainActivity;
import com.brentvanvosselen.oogappl.R;
import com.brentvanvosselen.oogappl.RestClient.APIInterface;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.RetrofitClient;
import com.brentvanvosselen.oogappl.util.ObjectSerializer;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by brentvanvosselen on 04/10/2017.
 */

public class ProfileFragment extends Fragment {

    //declaration fo the api-interface
    private APIInterface apiInterface;
    private SharedPreferences sharedPreferences;
    private int PICK_IMAGE_REQUEST = 1;

    TextView vTextViewEmail, vTextViewFirstname, vTextViewAddress, vTextViewTelephone, vTextViewWork, vTextViewType;
    CircularImageView vImageViewProfile;

    public interface OnNavigationChange{
        public void profilePictureChanged(Bitmap b);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiInterface = RetrofitClient.getClient(getContext()).create(APIInterface.class);
        sharedPreferences = getActivity().getSharedPreferences("com.brentvanvosselen.oogappl.fragments", Context.MODE_PRIVATE);

        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText(R.string.profile);

        //getting the textviews
        final View content = getView();
        vTextViewEmail = content.findViewById(R.id.textview_profile_email);
        vTextViewFirstname = content.findViewById(R.id.textview_profile_firstname);
        vTextViewAddress = content.findViewById(R.id.textview_profile_address);
        vTextViewTelephone = content.findViewById(R.id.textview_profile_telephone);
        vTextViewWork = content.findViewById(R.id.textview_profile_work);
        vTextViewType = content.findViewById(R.id.textview_profile_type);

        vImageViewProfile = content.findViewById(R.id.profile_imageview);

        vImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getView(), R.string.new_picture_press, Snackbar.LENGTH_SHORT).show();
            }
        });

        vImageViewProfile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(takePictureIntent,PICK_IMAGE_REQUEST);
                }
                return true;
            }
        });

        //get the user from the api-server
        Call call = apiInterface.getParentByEmail("bearer "+ sharedPreferences.getString("token",null),((MainActivity) getActivity()).getUserEmail());
        //progress
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.getting_data));
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Parent parent = (Parent) response.body();
                    Log.i("parent",parent.toString());

                    if(parent.getPicture() != null){
                        byte[] decodedString = Base64.decode(parent.getPicture().getValue(),Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                        vImageViewProfile.setImageBitmap(decodedByte);
                    }else{
                        Bitmap image = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.no_picture);
                        vImageViewProfile.setImageBitmap(image);
                    }


                    vTextViewEmail.setText((parent.getEmail() == null)?"":parent.getEmail());
                    vTextViewFirstname.setText(parent.getFirstname() + " " + parent.getLastname());
                    if((parent.getAddressStreet() == null  || parent.getAddressStreet().isEmpty()) && (parent.getAddressNumber() == null || parent.getAddressNumber().isEmpty()) && (parent.getAddressPostalcode() == null || parent.getAddressPostalcode().isEmpty()) && (parent.getAddressCity() == null || parent.getAddressCity().isEmpty())){
                        vTextViewAddress.setVisibility(View.GONE);
                    }
                    vTextViewAddress.setText(parent.getAddressStreet() + " " + parent.getAddressNumber() + "\n" + parent.getAddressPostalcode() + " " + parent.getAddressCity());
                    if(parent.getTelephoneNumber() == null || parent.getTelephoneNumber().isEmpty()){
                        vTextViewTelephone.setVisibility(View.GONE);
                    }else{

                        vTextViewTelephone.setText(R.string.telephonenumber + ": " + parent.getTelephoneNumber());
                    }
                    if((parent.getWorkName() == null || parent.getWorkName().isEmpty()) && (parent.getWorkNumber() == null || parent.getWorkNumber().isEmpty())){
                        vTextViewWork.setVisibility(View.GONE);
                    }
                    vTextViewWork.setText(R.string.work_data + ": " + parent.getWorkName() + "\n" + parent.getWorkNumber());
                    if(parent.getType() != null){
                        switch (parent.getType()){
                            case "M": vTextViewType.setText(R.string.mother);
                                break;
                            case "F": vTextViewType.setText(R.string.father);
                                break;
                            default:
                                vTextViewType.setText("");
                        }
                    }else{
                        vTextViewType.setText("");
                    }

                }else{
                    Snackbar.make(getView(), R.string.get_profileinfo_neg, Snackbar.LENGTH_SHORT).show();
                    Log.i("USER","FAIL: "+ response.message());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(getView(), R.string.geen_verbinding, Snackbar.LENGTH_SHORT).show();
                Log.i("API EVENT", t.getMessage());
                call.cancel();
                progressDialog.dismiss();
            }
         });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_edit){
            Fragment editFragment = new ProfileEditFragment();
            if (editFragment != null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main,editFragment);
                ft.commit();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                // Log.d(TAG, String.valueOf(bitmap));
                Log.i("before compress", String.valueOf(BitmapCompat.getAllocationByteCount(bitmap)));

                //compressing image
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG,50,out);
                Bitmap smaller = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                Log.i("after compress", String.valueOf(BitmapCompat.getAllocationByteCount(smaller)));

                byte[] byteArray = out.toByteArray();
                String value = Base64.encodeToString(byteArray,Base64.DEFAULT);
                String name = String.valueOf(new Date().getTime());
                String type = "image/jpeg";

                Image image = new Image(name,type,value);

                User currentUser = ObjectSerializer.deserialize2(sharedPreferences.getString("currentUser",null));

                Call changeProfilePictureCall = apiInterface.changeProfilePicture("bearer " + sharedPreferences.getString("token",null),currentUser.getEmail(),image);
                changeProfilePictureCall.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if(response.isSuccessful()){
                            Snackbar.make(getView(), R.string.change_picture_pos, Snackbar.LENGTH_SHORT).show();

                        }else{
                            Snackbar.make(getView(), R.string.change_picture_neg, Snackbar.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Snackbar.make(getView(), R.string.geen_verbinding, Snackbar.LENGTH_SHORT).show();

                    }
                });

            OnNavigationChange mCallback = (OnNavigationChange)getActivity();
            mCallback.profilePictureChanged(smaller);
            vImageViewProfile.setImageBitmap(smaller);


        }
    }
}
