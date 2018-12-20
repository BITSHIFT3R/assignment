package project.com.assignment.map.network;



import io.reactivex.Single;
import project.com.assignment.map.model.Response;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.POST;



public interface ApiService {

    @FormUrlEncoded
    @POST("api/add_child.php")
    Single<Response> send(@Field("name") String name,@Field("fname") String fname);


}
