package com.brentvanvosselen.oogappl.RestClient;

import com.brentvanvosselen.oogappl.RestClient.models.Category;
import com.brentvanvosselen.oogappl.RestClient.models.Child;
import com.brentvanvosselen.oogappl.RestClient.models.Cost;
import com.brentvanvosselen.oogappl.RestClient.models.CostCategory;
import com.brentvanvosselen.oogappl.RestClient.models.Costbill;
import com.brentvanvosselen.oogappl.RestClient.models.Event;
import com.brentvanvosselen.oogappl.RestClient.models.Group;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerBoek;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerDag;
import com.brentvanvosselen.oogappl.RestClient.models.HeenEnWeerItem;
import com.brentvanvosselen.oogappl.RestClient.models.Image;
import com.brentvanvosselen.oogappl.RestClient.models.Parent;
import com.brentvanvosselen.oogappl.RestClient.models.SetupValues;
import com.brentvanvosselen.oogappl.RestClient.models.User;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    Call<Parent> createParent(@Header("authorization") String token, @Body Parent p);

    @POST("/api/signup")
    Call<User> createUser(@Body User u);

    @POST("/api/login")
    Call<Parent> loginUser(@Body User u);

    @GET("/api/parents/{email}")
    Call<Parent> getParentByEmail(@Header("authorization") String token, @Path("email") String email);

    @POST("/api/parents/edit")
    Call<Parent> saveProfile(@Header("authorization") String token, @Body Parent p);

    @POST("/api/children/update")
    Call<Child> saveChild(@Header("authorization") String token, @Body Child c);

    @POST("/api/setup")
    Call<String> completeSetup(@Header("authorization") String token, @Body SetupValues s);

    @POST("/api/child/{id}")
    Call<Child> addChild(@Header("authorization") String token, @Path("id") String id, @Body Child child);

    @POST("/api/children/update")
    Call<Child> updateChild(@Header("authorization") String token, @Body Child child);

    @GET("/api/calendar/getall/{email}")
    Call<List<Event>> getEvents(@Header("authorization") String token, @Path("email") String email);

    @GET("/api/calendar/event/next/{email}")
    Call<Event> getNextEvent(@Header("authorization") String token, @Path("email") String email);

    @GET("/api/calendar/event/{id}")
    Call<Event> getEvent(@Header("authorization") String token, @Path("id") String id);

    @GET("/api/calendar/event/date/{email}/{date}")
    Call<List<Event>> getEventsFromDate(@Header("authorization") String token, @Path("email")String email, @Path("date")Date date);

    @GET("/api/calendar/heenenweer/day/{email}/{date}")
    Call<List<HeenEnWeerDag>> getChildrenFromBookFromDate(@Header("authorization") String token, @Path("email") String email, @Path("date")Date date);

    @GET("/api/category/{email}")
    Call<List<Category>> getCategoriesFromUser(@Header("authorization") String token, @Path("email")String email);

    @POST("/api/category/add/{email}")
    Call<Category> addCategory(@Header("authorization") String token, @Path("email") String email, @Body Category category);

    @POST("/api/calendar/event/add/{email}")
    Call<String> addEvent(@Header("authorization") String token, @Path("email") String email, @Body Event event);

    @PUT("/api/calendar/event/edit/{id}")
    Call<String> editEvent(@Header("authorization") String token, @Path("id")String id, @Body Event event);

    @POST("/api/finance")
    Call<Group> addFinanceInfo(@Header("authorization") String token, @Body Group group);

    @POST("/api/finance/accept")
    Call<String> acceptFinanceInfo(@Header("authorization") String token, @Body Parent parent);

    @GET("/api/costs/{email}")
    Call<List<Cost>> getAllCosts(@Header("authorization") String token, @Path("email") String email);

    @GET("api/costs/month/{email}")
    Call<List<Cost>> getAllCostsMonth(@Header("authorization") String token, @Path("email") String email);

    @GET("api/costs/bill/{email}")
    Call<Costbill> getCostbill(@Header("authorization") String token, @Path("email") String email);

    @GET("/api/costs/categories/{email}")
    Call<List<CostCategory>> getAllCostCategories(@Header("authorization") String token, @Path("email") String email);

    @POST("/api/costs/addCost/{email}")
    Call<Cost> addCost(@Header("authorization") String token, @Path("email") String email, @Body Cost cost);

    @POST("/api/costs/addCategory/{email}")
    Call<CostCategory> addCategory(@Header("authorization") String token, @Path("email") String email, @Body CostCategory category);

    @GET("/api/costs/bill/small/{email}")
    Call<Costbill> GetSmallBill(@Header("authorization") String token, @Path("email") String email);

    @DELETE("/api/event/delete/{email}/{id}")
    Call<String> deleteEvent(@Header("authorization") String token, @Path("email")String email, @Path("id")String id);

    @GET("/api/heenenweer/getAll/{email}")
    Call<HeenEnWeerBoek[]> getAllBooks(@Header("authorization") String token, @Path("email") String email);

    @GET("/api/heenenweer/day/{id}")
    Call<HeenEnWeerDag> getHeenEnWeerDay(@Header("authorization") String token, @Path("id")String id);

    @PUT("/api/heenenweer/item/edit/{id}")
    Call<String> editHeenEnWeerItem(@Header("authorization") String token, @Path("id")String id, @Body HeenEnWeerItem item);

    @POST("/api/heenenweer/item/add/{dayid}")
    Call<String> addHeenEnWeerItem(@Header("authorization") String token, @Path("dayid")String id, @Body HeenEnWeerItem item);

    @POST("/api/heenenweer/day/add")
    Call<String> addHeenEnWeerDay(@Header("authorization") String token, @Body HeenEnWeerDag item);

    @POST("/api/parents/picture/{email}")
    Call<String> changeProfilePicture(@Header("authorization")String token, @Path("email")String email, @Body Image image);

    @POST("/api/children/picture/{id}")
    Call<Child> changeChildPicture(@Header("authorization")String token, @Path("id") String id, @Body Image image);


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
