package com.goku1.bionatura;

import com.goku1.bionatura.models.AsistenciaPendiente;
import com.goku1.bionatura.models.PasswordUpdateRequest;
import com.goku1.bionatura.models.RegistroRequest;
import com.goku1.bionatura.models.UserAsistencia;
import com.goku1.bionatura.models.UserDate;
import com.goku1.bionatura.models.UserProfile;
import com.goku1.bionatura.models.UserUpdateProfileRequest;


import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;


public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("profile")
    Call<UserProfile> getProfile(@Header("Authorization") String token);

    @GET("date")
    Call<List<UserDate>> getRegistros(@Header("Authorization") String token);

    @POST("material/registrar")
    Call<ResponseBody> registrarMaterial(@Body RegistroRequest request);

    @POST("asistencia/registrar")
    Call<ResponseBody> registrarAsistencia(@Body UserAsistencia request);

    @GET("asistencia/pendiente")
    Call<AsistenciaPendiente> verificarPendiente(@Header("Authorization") String token);

    @PUT("asistencia/salida")
    Call<ResponseBody> registrarSalida(@Header("Authorization") String token, @Body Map<String, String> body);


    @PUT("profile/update")
    Call<ResponseBody> actualizarPerfil(@Header("Authorization") String token, @Body UserUpdateProfileRequest request);

    @PUT("profile/password")
    Call<ResponseBody> actualizarPassword(@Header("Authorization") String token, @Body PasswordUpdateRequest request);
}