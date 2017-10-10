package com.brentvanvosselen.oogappl.RestClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("/api/parents?")
    Call<ParentList> doGetParents();
}
