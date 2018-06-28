package com.lensim.fingerchat.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.BitmapUtil;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.compress.ImageInterface;
import com.lensim.fingerchat.data.ApiEnum.ERequestType;
import com.lensim.fingerchat.data.hexmeet.LockUser;
import com.lensim.fingerchat.data.hexmeet.RoomInfo;
import com.lensim.fingerchat.data.hexmeet.VideoMeeting;
import com.lensim.fingerchat.data.hrcs.HRCS;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.data.request.MomentsRequest;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.data.response.ret.RetArrayResponse;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.data.response.ret.RetResponse;
import com.lensim.fingerchat.data.work_center.OAToken;
import com.lensim.fingerchat.data.work_center.SignInJson;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.sign.SPListResponse;
import com.lensim.fingerchat.data.work_center.sign.SignInPicture;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 * date on 2017/12/26
 * author ll147996
 * describe
 */

public class Http {

    private static HttpChannel httpChannel = HttpChannel.getInstance();
    private static HttpChannel mgsonHttpChannel = HttpChannel.getInstance(ERequestType.MGSON);
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    /**
     * 获取SSOtoken
     * @param userid 用户ID
     * @param password 密码
     * @param clientType 令牌(*string, clientType=webpage/android/ios/windows/macos)
     * @param appid 业务系统ID
     */
//    public static Observable<ResponseObject<SSOToken>> SSOLogin(String userid, String password, String clientType, String appid){
//        Map<String, String> map = new HashMap<>();
//        map.put("userid", userid);
//        map.put("password", password);
//        map.put("clientType", clientType);
//        map.put("appid", appid);
//
//        return httpChannel.getRetrofitService()
//                .SSOLogin(getRequestBody(map));
//    }


    /**
     * 获取OAtoken
     * @param userid 用户ID
     * @param token 令牌
     * @param clientType 令牌(*string, clientType=webpage/android/ios/windows/macos)
     * @param appid 业务系统ID
     */
    public static Observable<ResponseObject<OAToken>> getOAToken(String userid, String token, String clientType, String appid){
        Map<String, String> map = new HashMap<>();
        map.put("userid", userid);
        map.put("token", token);
        map.put("clientType", clientType);
        map.put("appid", appid);

        return httpChannel.getRetrofitService().getOAToken(getRequestBody(map));
    }


    /**
     * 工作中心获取子项目
     */
    public static Observable<ResponseBody> getFunctions(String empno, String token) {
        return httpChannel.getRetrofitService().getFunctions(empno, token);
    }

    /**
     * 申请视频会议帐号
     */
    public static Observable<LockUser> applyForCameras(int num) {
        return httpChannel.getRetrofitService().applyForCameras(num);
    }


    /**
     * 获取当前admin的token
     * 用于结束会议等操作时所需的admin权限
     */
    public static Observable<String> getTempToken() {
        return httpChannel.getRetrofitService().getTempToken();
    }

    /**
     * 获取会议列表
     */
    public static Observable<RetArrayResponse<VideoMeeting>> getHexMeetingList(String token, String type, String user, String pageSize, String pageNum) {
        return mgsonHttpChannel.getRetrofitService().getHexMeetingList(token, type, user, pageSize, pageNum);

    }


    /**
     * 会议信息录入
     */
    public static Observable<RetObjectResponse<String>> postHexMeeting(VideoMeeting videoMeeting) {
        return mgsonHttpChannel.getRetrofitService().postHexMeeting(getRequestBody(videoMeeting));

    }


    /**
     * 删除会议
     */
    public static Observable<RetObjectResponse<String>> deleteHexMeeting(String id, String userid, String token) {
        return mgsonHttpChannel.getRetrofitService().deleteHexMeeting(id, userid, token);

    }


    /**
     * 给已存在的会议添加联系人
     */
    public static Observable<RetObjectResponse<String>> JoinToExistMeeting(VideoMeeting videoMeeting) {
        return mgsonHttpChannel.getRetrofitService().JoinToExistMeeting(getRequestBody(videoMeeting));

    }


    /**
     * 获取聊天室所有群成员
     */
    public static Observable<List<RoomInfo>> getMucMember(String fun, String userid, String teamserno) {
        return httpChannel.getRetrofitService().getMucMember(fun, userid, teamserno);

    }


    /**
     * 签到
     */
    public static Observable<RetObjectResponse<String>> signIn(SignInJson signInJson) {
        return mgsonHttpChannel.getRetrofitService().signIn(getRequestBody(signInJson));

    }

    /**
     * 签到查询
     */
    public static Observable<RetObjectResponse<String>> getSignIn(String userId, String fromDate, String toDate, String token) {
        return mgsonHttpChannel.getRetrofitService().getSignIn(userId, fromDate, toDate, token);

    }

    /**
     * 认证
     */
    public static Observable<ResponseBody> certification(String baseUrl, String employeeId, String id, String userName) {
        String url = String.format(baseUrl, employeeId, id, userName);
        return httpChannel.getRetrofitService().certification(url);

    }

    /**
     * 获取用户个人信息
     */
    public static Observable<List<UserIdentify>> getUserInfoByAsync(String fun, String userid) {
        return httpChannel.getRetrofitService().getUserInfoByAsync(fun, userid);
    }

    /**
     * 获取客服
     */
    public static Observable<RetArrayResponse<HRCS>> getHrNumber(String url) {
        return mgsonHttpChannel.getRetrofitService().getHrNumber(url);
    }

    /**
     * 签到——上传图片
     * @param path 图片路径
     * @return Observable<SPListResponse<SignInPicture>>
     */
    public static Observable<SPListResponse<SignInPicture>> signInPostPicture(String path) {
        File file = new File(path);
        RequestBody body = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("photoContent",file.getName(),body);
        return httpChannel.getRetrofitService().signInPostPicture(part);
    }


    /**
     * 上传头像 ——3处
     * 1、用户修改头像
     * 2、朋友圏背景图
     * 3、注册上传头像
     */
    public static Flowable<String> setAvatar(@android.support.annotation.NonNull  File file, String username) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("userid", username);
        builder.addFormDataPart("photoContent", file.getName(), requestBody);
        MultipartBody body = builder.build();//调用即可
        return httpChannel.getRetrofitService().setAvatar(body);
    }


    /**
     * 更新朋友圈信息
     */
    public static Flowable<List<FriendCircleEntity>> UpdateFriendCircle(String fun, String userid) {
        return httpChannel.getRetrofitService().UpdateFriendCircle(fun, userid);
    }


    /**
     * 朋友圈——上拉加载更多
     */
    public static Flowable<List<FriendCircleEntity>> loadMoreCircleData(int num, String userName) {
        Map<String, String> params = new HashMap<>();
        params.put("fun", "getfriendphotopg");
        params.put("userid", userName);
        params.put("pagesize", "5");
        params.put("pagenum", num + "");
        return httpChannel.getRetrofitService().loadMoreCircleData(params);
    }


    /**
     * 获取最新评论的数量
     */
    public static Flowable<RetObjectResponse<String>> getNewCommentCount(String pho_serno, String username) {
        return mgsonHttpChannel.getRetrofitService().getNewCommentCount(pho_serno, username);
    }


    /**
     * 分页加载评论列表
     */
    public static Flowable<RetObjectResponse<String>> getCommentsByPage(String userName,
        String pageNum, String pagesize, String timeStamp) {
        return mgsonHttpChannel.getRetrofitService().getCommentsByPage(userName, pageNum, pagesize,timeStamp);
    }


    /**
     * 新的评论列表
     */
    public static Flowable<List<NewComment>> getNewComment(String fun, String userid) {
        return httpChannel.getRetrofitService().getNewComment(fun, userid);
    }


    /**
     * 更新查看评论列表时间
     */
    public static Flowable<RetObjectResponse<String>> addSeeCommentTime(String fun, String userid) {
        return mgsonHttpChannel.getRetrofitService().addSeeCommentTime(fun, userid);
    }



    /**
     * 删除——朋友圈
     */
    public static Flowable<RetObjectResponse<String>> deleteCircle(String cricleId, String userName) {
        return mgsonHttpChannel.getRetrofitService().deleteCircle(cricleId, userName);
    }


    /**
     * 获取单条分享的所有评论
     */
    public static Flowable<RetObjectResponse<String>> getPhotoItemById(String pho_serno, String username) {
        return  mgsonHttpChannel.getRetrofitService().getPhotoItemById(pho_serno, username);
    }

    /**
     * 朋友圈——点赞
     */
    public static Flowable<ResponseBody> likeCircleFriends(CircleItem item){
        Map<String, String> params = new HashMap<>();
        params.put("fun", "zambia");
        params.put("CommentUserid", UserInfoRepository.getUserName().toLowerCase());
        String username = CyptoUtils.encrypt(
            StringUtils.isEmpty
                (UserInfoRepository.getUsernick()) ?
                UserInfoRepository.getUserName().toLowerCase() : UserInfoRepository.getUsernick());
        if (username.contains("+")) {
            username = username.replace("+", "%2B");
        }
        params.put("CommentUsername", username);
        params.put("photoserno", item.id);
        params.put("CreateUserid", item.userid);
        params.put("CreateUsername",
            StringUtils.isEmpty(item.username) ? item.userid : item.username);
        return httpChannel.getRetrofitService().like(params);
    }

    /**
     * 浏览相册——点赞
     * @param commentUserid 点赞的人
     * @param createUserid 被点赞的人
     */
    public static Flowable<ResponseBody> likePhotos(String commentUserid,
        String commentUsername, String photoserno, String createUserid, String createUsername){

        Map<String, String> params = new HashMap<>();
        params.put("fun", "zambia");
        params.put("CommentUserid", commentUserid);
        params.put("CommentUsername", CyptoUtils.encrypt(commentUsername));
        params.put("photoserno", photoserno);
        params.put("CreateUserid", createUserid);
        params.put(
            "CreateUsername", StringUtils.isEmpty(createUsername) ? createUserid : createUsername);
        return httpChannel.getRetrofitService().like(params);
    }


    /**
     * 删除——赞
     */
    public static Flowable<ResponseBody> cancelLike(String commentUserid, String photoserno,
        String createUserid) {

        return httpChannel.getRetrofitService().cancelLike
            ("delzambia", commentUserid, photoserno, createUserid);

    }


    public static Flowable<RetObjectResponse<String>> comment(String commentUserid, String commentUsername, String photoserno,
        String createUserid, String createUsername, String content) {
        MomentsRequest moments = new MomentsRequest();
        moments.setFunc("comment");
        moments.setCreateUserid(createUserid);
        moments.setCreateUsername(createUsername);
        moments.setCommentUserid(commentUserid);
        moments.setCommentUsername(CyptoUtils.encrypt(commentUsername));
        moments.setPhotoserno(photoserno);
        moments.setContent(CyptoUtils.encrypt(content));
        return mgsonHttpChannel.getRetrofitService().addComment(getRequestBody(moments));
    }


    /**
     * 回复
     * @param secondid 被回复人id
     */
    public static Flowable<RetObjectResponse<String>> reComment(String photoserno, String createUserid, String createUsername,
        String content, String secondid, String secondname) {

        MomentsRequest moments = new MomentsRequest();
        moments.setFunc("recomment");
        moments.setCreateUserid(createUserid);
        moments.setCreateUsername(createUsername);
        moments.setCommentUserid(UserInfoRepository.getUserName().toLowerCase());
        moments.setCommentUsername(CyptoUtils.encrypt(UserInfoRepository.getUsernick()));
        moments.setPhotoserno(photoserno);
        moments.setContent(CyptoUtils.encrypt(content));
        moments.setSecondid(secondid);
        moments.setSecondname(CyptoUtils.encrypt(secondname));

        return mgsonHttpChannel.getRetrofitService().addComment(getRequestBody(moments));
    }


    /**
     * 删除——评论
     * 要么是/1/cba5d713e1/ll032197  , 1代表了 createuser, 即朋友圈的创建者
     要么是/2/cba5d713e1/ll117394 , 2代表的是 commentuser, 即 发表评论的人
     */
    public static Flowable<RetObjectResponse<String>> deleteComment(String phc_serno, String commentUser) {
        return mgsonHttpChannel.getRetrofitService().deleteComment(phc_serno, commentUser);
    }


    public static Flowable<ResponseBody> downloadVideoFile(String fileUrl) {
        return httpChannel.getRetrofitService().downloadVideoFile(fileUrl);
    }


    /**
     * 相册——更新
     */
    public static Flowable<List<FriendCircleEntity>> getPhotos(String fun, String userid) {
        return httpChannel.getRetrofitService().getPhotos(fun, userid);
    }


    public static Observable<ResponseBody> downloadFileWithDynamicUrlAsync(String fileUrl) {
        return httpChannel.getRetrofitService().downloadFileWithDynamicUrlAsync(fileUrl);
    }

    /**
     * 收藏列表——获取
     */
    public static Flowable<RetObjectResponse<String>> getFavList(String username, String pagenum, String pagesize) {
        return mgsonHttpChannel.getRetrofitService().getFavList(username, pagenum, pagesize);
    }

    /**
     * 收藏——删除
     */
    public static Flowable<RetResponse> removeFavItem(String msgId, String username) {
        return mgsonHttpChannel.getRetrofitService().removeFavItem(msgId, username);
    }

    /**
     * 上传日志
     */
    public static Flowable<ResponseBody> uploadLog(String json){
        return httpChannel.getRetrofitService()
            .uploadLog(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json));
    }

    public static Observable<ResponseBody> uploadLogger(Map<String, String> params) {
        return httpChannel.getRetrofitService()
            .uploadlogger(getRequestBody(params));
    }

    /**
     * 收藏列表——新建
     */
    public static Flowable<ResponseBody> createFavList(FavJson favJson) {
        return  httpChannel.getRetrofitService().createFavList(getRequestBody(favJson));
    }

    public static Flowable<ResponseBody> sendVideoAndText(String userName, String content, File file) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("type", "1");
        builder.addFormDataPart("userid", userName);
        builder.addFormDataPart("content", content);
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("photoContent", file.getName(), requestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MultipartBody body = builder.build();//调用即可
        return httpChannel.getRetrofitService().sendVideoAndText(body);
    }

    /**
     * 认证
     */
    public static void sendIdcard(final String userName, List<String> mImagePathes,
        final String id, final String employid, final @NonNull Listener listener) {

        final List<File> files = new ArrayList<>();
        Observable.fromIterable(mImagePathes)
            .map(new Function<String, File>() {
                @Override
                public File apply(@NonNull String s) throws Exception {
                    //压缩图片，并将压缩后的图片
                    return BitmapUtil.compress(s);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    L.e("压缩图片出错");
                }
            })
            .onErrorResumeNext(Observable.<File>empty())
            .filter(new Predicate<File>() {
                @Override
                public boolean test(@NonNull File file) throws Exception {
                    return file != null;
                }
            })
            .subscribe(new Consumer<File>() {
                @Override
                public void accept(@NonNull File file) throws Exception {
                    files.add(file);
                    if (files.size() == 3 && listener != null) {
                        listener.success(sendIdcard(files, userName,id, employid));
                    }
                }
            });

    }


    /**
     * 压缩完成后发表状态
     */
    public static void sendPhotoAndText(String userName, String content, List<ImageInterface> images,
        Callback callback) {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("userid", userName);
        builder.addFormDataPart("content", CyptoUtils.encrypt(content));
        builder.addFormDataPart("type", "1");
        if (null != images) {
            Collections.sort(images);
            for (int i = 0, len = images.size(); i < len; i++) {
                ImageInterface image = images.get(i);
                String tail = image.getPath().substring(image.getPath().lastIndexOf("."));
                File f = new File(image.getThumb());
                builder.addFormDataPart("photoContent" + i, f.getName() + tail,
                    RequestBody.create(MEDIA_TYPE_PNG, f));

                if (len == 1) {
                    int[] img = getImageWidthHeight(image.getThumb());
                    builder.addFormDataPart("width", img[0] + "");
                    builder.addFormDataPart("height", img[1] + "");
                    builder.addFormDataPart("size", img[2] + "");
                }
            }
        }

        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder()
            .url(Route.SendCircleUrl)
            .post(requestBody)
            .build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    /***
     * @return
     * width
     * height
     * size(KB)_int
     * */
    private static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null

        String sizeKB = FileUtil.formatFileSize(FileUtil.getFileSize(path));
        int index = sizeKB.indexOf(".");
        int size = Integer.parseInt(sizeKB.substring(0, index));
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight, size};
    }

    public interface Listener {
        void success(Observable<RetObjectResponse<String>> observable);
    }

    private static Observable<RetObjectResponse<String>> sendIdcard(List<File> compressedFiles, String userName, String id, String employid) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("func", "sendcer");
        builder.addFormDataPart("userid", userName);
        builder.addFormDataPart("empno", employid);
        builder.addFormDataPart("idcard", id);

        try {
            RequestBody requestBody0 = RequestBody.create(MediaType.parse("multipart/form-data"), compressedFiles.get(0));
            RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), compressedFiles.get(1));
            RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), compressedFiles.get(2));

            builder.addFormDataPart("photoContent1", compressedFiles.get(0).getName(), requestBody0);
            builder.addFormDataPart("photoContent2", compressedFiles.get(1).getName(), requestBody1);
            builder.addFormDataPart("photoContent3", compressedFiles.get(2).getName(), requestBody2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MultipartBody body = builder.build();//调用即可
        return HttpChannel.getInstance().getRetrofitService().sendIdcard(body);
    }



    private static RequestBody getRequestBody(Object object) {
        Gson gson = new Gson();
        String route= gson.toJson(object);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), route);
    }


    private static RequestBody getRequestBody(Map<String, String> map) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mapToJSON(map));
    }

    /**
     * 将Map转化为Json
     */
    private static String mapToJSON(Map<String, String> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }

}
