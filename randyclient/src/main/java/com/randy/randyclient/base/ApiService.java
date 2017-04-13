package com.randy.randyclient.base;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by RandyZhang on 2017/4/11.
 */

public interface ApiService {

//      各注解解释:

//      http://blog.csdn.net/qiang_xi/article/details/53959437
//
//      @GET 请求方式，表示请求方式为Get；
//      @POST 请求方式，表示请求方式为Post
//      @Path 修饰请求参数的，用来组装请求路径来使用，采用@Path时，@GET或@POST注解中肯定包含{value}，
//                   此时value为@Path后面对应参数的值
//      @Query 修饰请求参数的，一个@Query表示一个Get参数
//      @Field 修饰请求参数的，一个@Field表示一个Post参数
//      @Part 修饰请求参数的， 一个@Part表示一个文件属性（可能时文件主体，也可能时文件描述）
//      @QueryMap 修饰请求参数的，Get请求方式参数不确定时使用
//      @FieldMap 修饰请求参数的，Post请求方式参数不确定时使用
//      @PartMap 修饰请求参数的 上传文件时文件个数不确定时使用
//      @Url 修饰请求参数的，表示请求地址（此时retrofit配置的baseUrl将失效）
//      @Body 修饰请求参数的，表示采用RequestBody形式传递参数
//
//      @FormUrlEncoded 修饰请求方式@POST的，表示Post参数以FormURLEncoded形式提交
//      @Multipart 修饰请求方式@POST的，表示上传文件
    /**
     * retrofit的base url
     */
    String baseUrl = "http://local.rainmonth.com";

    /**
     * 通用get请求
     *
     * @param url      绝对地址
     * @param paramMap 请求参数
     * @return Observable对象
     */
    @GET
    Observable<ResponseBody> get(
            @Url String url,
            @QueryMap Map<String, Object> paramMap);


    /**
     * @param pathUrl  相对地址
     * @param paramMap 请求参数
     * @return Observable对象
     */
    @GET("{pathUrl}")
    Observable<ResponseBody> getByPath(
            @Path("pathUrl") String pathUrl,
            @QueryMap Map<String, Object> paramMap);


    /**
     * 通用post请求
     *
     * @param pathUrl  相对url地址
     * @param paramMap 请求参数
     * @return Observable对象
     */
    @POST()
    Observable<ResponseBody> post(
            @Url String pathUrl,
            @FieldMap Map<String, Object> paramMap);


    /**
     * 通用post请求
     *
     * @param pathUrl  相对url地址
     * @param paramMap 请求参数
     * @return Observable对象
     */
    @POST("{pathUrl}")
    Observable<ResponseBody> postByPath(
            @Path("pathUrl") String pathUrl,
            @FieldMap Map<String, Object> paramMap);


    /**
     * Form（表单）形式通用get请求
     *
     * @param url      绝对地址
     * @param paramMap 请求参数
     * @return Observable对象
     */
    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> postForm(@Url String url, @FieldMap Map<String, Object> paramMap);

    /**
     * Form（表单）形式通用get请求
     *
     * @param pathUrl  相对地址
     * @param paramMap 请求参数
     * @return Observable对象
     */
    @FormUrlEncoded
    @POST("{pathUrl}")
    Observable<ResponseBody> postFormByPath(
            @Path("pathUrl") String pathUrl,
            @FieldMap Map<String, Object> paramMap);

    /**
     * 上传单张图片
     *
     * @param url       绝对地址
     * @param imageFile 要上传的图片文件
     * @return Observable对象
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadImage(
            @Url String url,
            @Part("image\";filename=\"avatar.phg") RequestBody imageFile);

    /**
     * 上传图片
     *
     * @param pathUrl   相对地址
     * @param imageFile 要上传的图片文件
     * @return Observable对象
     */
    @Multipart
    @POST("{pathUrl}")
    Observable<ResponseBody> uploadImageByPath(
            @Path("pathUrl") String pathUrl,
            @Part("image\";filename=\"avatar.phg") RequestBody imageFile);


    /**
     * 上传文件
     *
     * @param url  相对地址
     * @param file 要上传的图片文件
     * @return Observable对象
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFile(
            @Url String url,
            @Part("file\";filename=\"filename") RequestBody file);

    /**
     * 上传文件
     *
     * @param urlPath 相对地址
     * @param file    要上传的图片文件
     * @return Observable对象
     */
    @Multipart
    @POST("{urlPath}")
    Observable<ResponseBody> uploadFileByPath(
            @Path("urlPath") String urlPath,
            @Part("file\";filename=\"filename") RequestBody file);

    /**
     * 上传多张图片
     *
     * @param url     url地址（绝对地址）
     * @param fileMap 要上传的图片文件
     * @return Observable对象
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(
            @Url String url,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> fileMap);

    /**
     * 上传多张图片
     *
     * @param urlPath 相对地址
     * @param fileMap 要上传的图片文件
     * @return Observable对象
     */
    @Multipart
    @POST("{urlPath}")
    Observable<ResponseBody> uploadFilesBypPath(
            @Path("urlPath") String urlPath,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> fileMap);


    /**
     * 图文混传
     *
     * @param url       绝对地址
     * @param paramMap  请求参数
     * @param imageFile 上传的文件
     * @return Observable对象
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadMixContents(
            @Url String url,
            @FieldMap Map<String, Object> paramMap,
            @Part("image\";filename=\"avatar.png") RequestBody imageFile);


    /**
     * 图文混传
     * <p>
     * 构建body
     * RequestBody requestBody
     * = new MultipartBody.Builder().setType(MultipartBody.FORM)
     * .addFormDataPart("name", name)
     * .addFormDataPart("name", psd)
     * .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"),
     * file))
     * .build();
     * </p>
     *
     * @param url  绝对地址
     * @param body 图文混合对象
     * @return Observable 对象
     */
    @POST()
    Observable<ResponseBody> uploadMixContentBody(
            @Url() String url,
            @Body RequestBody body);

}
