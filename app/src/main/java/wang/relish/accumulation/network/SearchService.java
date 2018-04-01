package wang.relish.accumulation.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Relish Wang
 * @since 2018/04/01
 */
public interface SearchService {
    @GET("search")
    Call<List<String>> listRepos(
            @Query("key") String key
    );
}
