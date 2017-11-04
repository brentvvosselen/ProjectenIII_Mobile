package com.brentvanvosselen.oogappl.RestClient;

import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.models.SetupValues;
import com.brentvanvosselen.oogappl.RestClient.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    /*
    * Definieer de verschillende mogelijke calls
    *
    * @Get - @POST - ...
    *
    * Wanneer post:
    * Mogelijkheid tot meegeven @Body
    * */

    @GET("/api/parents?")
    Call<List<Parent>> doGetParents();

    @POST("/api/parents")
    Call<Parent> createParent(@Body Parent p);

    @POST("/api/signup")
    Call<User> createUser(@Body User u);

    @POST("/api/login")
    Call<Parent> loginUser(@Body User u);

    @GET("/api/parents/{email}")
    Call<Parent> getParentByEmail(@Path("email") String email);

    @POST("/api/parents/edit")
    Call<Parent> saveProfile(@Body Parent p);

    @POST("/api/children/update")
    Call<Child> saveChild(@Body Child c);

    @POST("/api/setup")
    Call<Parent> completeSetup(@Body SetupValues s);

    @POST("/api/child/{id}")
    Call<Child> addChild(@Path("id") String id, @Body Child child);

    @POST("/api/children/update")
    Call<Child> updateChild(@Body Child child);

    @GET("/api/calendar/getall/{email}")
    Call<List<Event>> getEvents(@Path("email") String email);

    @GET("/api/calendar/event/next/{email}")
    Call<Event> getNextEvent(@Path("email") String email);

    @GET("/api/calendar/event/{id}")
    Call<Event> getEvent(@Path("id") String id);


    /*
    Voorbeeld van API call


    APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);

    Call call = apiInterface.doGetParents();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("API event", response.message());
                List<Parent> parents = (List<Parent>) response.body();
                for(Parent p : parents) {
                    Log.i("API call", p.id + "  " + p.name + "  " + p.hobby);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                call.cancel();
            }
        });
     */

        /*
        Call call = apiInterface.doGetParents();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("API event", response.message());
                List<Parent> parents = (List<Parent>) response.body();
                for(Parent p : parents) {
                    Log.i("API call", p.getId() + "  " + p.getName() + "  " + p.getHobby());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                call.cancel();
            }
        });

        Parent p = new Parent("Parent");

        call = apiInterface.createParent(p);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("API event", response.message());
                Parent p = (Parent) response.body();
                Log.i("API call", p.getId() + "  " + p.getName() + "  " + p.getHobby());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i("API event", t.getMessage());
                call.cancel();
            }
        });
        */
}
