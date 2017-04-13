package com.randy.randyclient.base;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 通用ApiService
 * Created by RandyZhang on 2017/4/11.
 */

@SuppressWarnings("ALL")
public interface BaseApiService {

    /**
     * @param url
     * @param maps
     * @param <T>
     * @return
     */
    @FormUrlEncoded
    @POST()
    <T> Observable<ResponseBody> executePost(
            @Url() String url,
            @FieldMap Map<String, Object> maps);

    /**
     * @param url
     * @param body
     * @param <T>
     * @return
     */
    @POST("{url}")
    <T> Observable<ResponseBody> executePostBody(
            @Path("url") String url,
            @Body RequestBody body);

    /**
     * @param url
     * @param maps
     * @param <T>
     * @return
     */
    @GET()
    <T> Observable<ResponseBody> executeGet(
            @Url String url,
            @QueryMap Map<String, Object> maps);

    /**
     * @param url
     * @param maps
     * @param <T>
     * @return
     */
    @DELETE()
    <T> Observable<ResponseBody> executeDelete(
            @Url String url,
            @QueryMap Map<String, Object> maps);

    /**
     * @param url
     * @param maps
     * @param <T>
     * @return
     */
    @PUT()
    <T> Observable<ResponseBody> executePut(
            @Url String url,
            @FieldMap Map<String, Object> maps);

    /**
     * @param url
     * @param requestBody
     * @return
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadImage(
            @Url() String url,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);

    /**
     * @param fileUrl
     * @param description
     * @param file
     * @return
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFlie(
            @Url String fileUrl,
            @Part("description") RequestBody description,
            @Part("files") MultipartBody.Part file);


    /**
     * @param url
     * @param maps
     * @return
     */
    @POST()
    Observable<ResponseBody> uploadFiles(
            @Url() String url,
            @Body Map<String, RequestBody> maps);

    /**
     * @param url
     * @param file
     * @return
     */
    @POST()
    Observable<ResponseBody> uploadFile(
            @Url() String url,
            @Body RequestBody file);

    /**
     * @param url
     * @param partMap
     * @param file
     * @return
     */
    @Multipart
    @POST
    Observable<ResponseBody> uploadFileWithPartMap(
            @Url() String url,
            @PartMap() Map<String, RequestBody> partMap,
            @Part("file") MultipartBody.Part file);

    /**
     * @param fileUrl
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);


    /**
     * @param fileUrl
     * @return
     */
    @GET
    Observable<ResponseBody> downloadSmallFile(@Url String fileUrl);


    /**
     * @param fileUrl
     * @param maps
     * @param <T>
     * @return
     */
    @GET
    <T> Observable<ResponseBody> getTest(@Url String fileUrl,
                                         @QueryMap Map<String, Object> maps);

    /**
     * @param url
     * @param maps
     * @param <T>
     * @return
     */
    @FormUrlEncoded
    @POST()
    <T> Observable<ResponseBody> postForm(
            @Url() String url,
            @FieldMap Map<String, Object> maps);


    /**
     * @param url
     * @param Body
     * @return
     */
    @POST()
    Observable<ResponseBody> postRequestBody(
            @Url() String url,
            @Body RequestBody Body);
}
