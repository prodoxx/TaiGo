package lcom.reggieescobar.taigo.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.reggieescobar.taigo.Helpers.AppResultListener;
import com.reggieescobar.taigo.Helpers.Utilities;
import com.squareup.picasso.Picasso;

/**
 * Created by rescobar on 28/05/2016.
 */
public class ImageToByteConverter extends AsyncTask<String, Integer, Boolean> {

    private String _pathToImage;
    private Context _appContext;
    private byte [] data;
    private AppResultListener _listener;
    private Exception _error;

    public ImageToByteConverter(Context appContext, String pathToImage, AppResultListener listener){
        this._appContext = appContext;
        this._pathToImage = pathToImage;
        this._listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... strings) {
        Boolean success = false;

        //convert here;

        try {
            Bitmap image = Picasso.with(_appContext).load(_pathToImage).resize(500,500).centerCrop().get();
            data = Utilities.convertImageToByte(image);

            return true;
        }

        catch (Exception e){
            e.printStackTrace();
            success = false;
            _error = e;

        }


        return success;
    }

    @Override
    protected void onPostExecute(Boolean s) {

        if(s){
            _listener.onImageFetchSuccess(data);
        } else {
            _listener.onError(_error);
        }



        super.onPostExecute(s);
    }

}
