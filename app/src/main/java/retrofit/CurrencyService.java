package retrofit;


import io.reactivex.Observable;
import model.Rates;
import model.RootObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyService {
  @GET("latest?")
  Observable<RootObject> getData(@Query("base") String base);

  @GET("latest")
  Observable<RootObject> getData();
}