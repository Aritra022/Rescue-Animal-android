import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/api/rescueField/add") // Change this path to match your backend route
    Call<ResponseBody> uploadRescuePet(
            @Part("petType") RequestBody petType,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part image
    );
}
