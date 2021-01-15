package com.e.arena;

import com.e.arena.Model.DemoClassrequestModel;
import com.e.arena.Model.FeePlanModel;
import com.e.arena.Model.TransactionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("mPayCreditsToGym")
    Call<TransactionResponse> getTransaction(@Query("userId") String userId,
                                             @Query("gymId") String gymId,
                                             @Query("credits") long credits);


    @GET("addMoneyTowallet")
    Call<TransactionResponse> addCreditsToAccount(@Query("userId") String userId,
                                                  @Query("credits") long credits,
                                                  @Query("payment_method") String payment_method);

    @GET("FeePlanForYear")
    Call<FeePlanModel> feePlan(@Query("tutorId") String tutorId);


    @GET("requestForDemoClass")
    Call<DemoClassrequestModel> setRequestForDemoClass(@Query( "userId" )String userId,
                                                       @Query("tutorId") String tutorId);

}
