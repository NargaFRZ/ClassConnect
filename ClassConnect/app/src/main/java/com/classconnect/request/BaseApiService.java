package com.classconnect.request;

import com.classconnect.model.*;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Field;

import java.util.List;

public interface BaseApiService {

    @GET("account/{id}")
    Call<Account> getAccountById(@Path("id") int id);

    @POST("account/register")
    Call<BaseResponse<Account>> register(@Body Account account);

    @POST("account/login")
    Call<BaseResponse<Account>> login(@Body LoginRequest loginRequest);

    @PUT("admin/approve")
    Call<BaseResponse<Account>> approveUser(@Body UserIdRequest userId);

    @DELETE("admin/delete")
    Call<BaseResponse<Account>> deleteUser(@Query("user_id") String userId);

    @GET("admin/unapproved")
    Call<BaseResponse<List<Account>>> getUnapprovedUsers();

    @POST("teacher/createClass")
    Call<BaseResponse<Classes>> createClass(@Body ClassRequest classRequest);

    @GET("class/getAllClasses")
    Call<BaseResponse<List<Classes>>> getAllClasses();

    @GET("class/getClassById/{id}")
    Call<BaseResponse<Classes>> getClassById(@Path("id") String classId);

    @POST("/student/enrollStudent")
    Call<BaseResponse<Void>> enrollStudent(@Body EnrollRequest enrollRequest);

    @GET("/class/getClassMembers/{classId}")
    Call<BaseResponse<List<Member>>> getClassMembers(@Path("classId") String classId);

    @POST("messages/sendMessage")
    Call<BaseResponse<Message>> sendMessage(@Body MessageRequest messageRequest);

    @GET("messages/getMessages/{sender_id}/{receiver_id}")
    Call<BaseResponse<List<Message>>> getMessages(@Path("sender_id") String senderId, @Path("receiver_id") String receiverId);

    @GET("messages/getMessageSenders/{receiver_id}")
    Call<BaseResponse<List<Account>>> getMessageSenders(@Path("receiver_id") String receiverId);

    @GET("student/getStudentClasses/{studentId}")
    Call<BaseResponse<List<Classes>>> getStudentClasses(@Path("studentId") String studentId);

    @POST("teacher/registerAsClassTeacher")
    Call<BaseResponse<TeacherClass>> registerAsClassTeacher(@Body TeacherRequest teacherRequest);

    @Multipart
    @POST("teacher/createAssignment")
    Call<BaseResponse<Void>> createAssignment(
            @Part("class_id") RequestBody classId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("due_date") RequestBody dueDate,
            @Part MultipartBody.Part file
    );

    @GET("student/getUnsubmittedAssignments/{studentId}")
    Call<BaseResponse<List<Assignment>>> getUnsubmittedAssignments(@Path("studentId") String studentId);

    @GET("student/getSubmittedAssignments/{studentId}")
    Call<BaseResponse<List<Assignment>>> getSubmittedAssignments(@Path("studentId") String studentId);

    @Multipart
    @POST("assignment/createSubmission")
    Call<BaseResponse<Void>> createSubmission(@Part("assignment_id") RequestBody assignmentId,
                                              @Part("student_id") RequestBody studentId,
                                              @Part MultipartBody.Part file);

    @Multipart
    @PUT("assignment/updateSubmission/{submission_id}")
    Call<BaseResponse<Void>> updateSubmission(
            @Path("submission_id") int submissionId,
            @Part("assignment_id") RequestBody assignmentId,
            @Part("student_id") RequestBody studentId,
            @Part MultipartBody.Part file
    );


    @DELETE("assignment/deleteSubmission/{submission_id}")
    Call<BaseResponse<Void>> deleteSubmission(@Path("submission_id") int submissionId);

    @DELETE("assignment/deleteAssignment/{assignment_id}")
    Call<BaseResponse<Void>> deleteAssignment(@Path("assignment_id") int assignmentId);

    @GET("assignment/getAssignmentById/{assignment_id}")
    Call<BaseResponse<List<Assignment>>> getAssignmentById(@Path("assignment_id") int assignmentId);

    @GET("assignment/getSubmission")
    Call<BaseResponse<Submission>> getSubmissionByAssignmentAndStudent(@Query("assignment_id") int assignmentId, @Query("student_id") String studentId);

    @GET("assignment/getAssignmentByClass")
    Call<BaseResponse<List<Assignment>>> getAssignmentByClass(@Query("class_id") String classId);

    @GET("assignment/getSubmissionByClass")
    Call<BaseResponse<List<Submission>>> getSubmissionsByClass(@Query("class_id") String classId);

    @POST("teacher/gradeAssignment")
    Call<BaseResponse<Grades>> gradeAssignment(@Body GradeRequest gradeRequest);

    @GET("assignment/getSubmissionById/{submissionId}")
    Call<BaseResponse<Submission>> getSubmissionById(@Path("submissionId") int submissionId);

    @GET("student/getRecentGrades")
    Call<BaseResponse<List<Grades>>> getRecentGrades(@Query("student_id") String studentId);

    @GET("getTeachedClasses/{teacherId}")
    Call<BaseResponse<List<Classes>>> getTeachedClasses(@Path("teacherId") String teacherId);
}
