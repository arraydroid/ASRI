package asri_tools.devalkhatech.com.asri.interfaces;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
/**
 * Created by Array-PC on 19-May-16.
 */
public interface SalesOrderAPI {
    @FormUrlEncoded
    @POST("/aang/mysqlsqlitesync/insert_sales_order.php")
    //@POST("/php_scripts/insert_sales_order.php")

    public void
    insertSOrder(
            @Field("code") String code,
            @Field("customer_code") String customer_name,
            @Field("remark") String remark,
            @Field("date") String date,
            @Field("salesman") String salesman,

            Callback<Response> callback);
}
