package com.project.loco;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;


import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class AutoCompleteAdapter extends FragmentActivity {

    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private String mQuery;
    private Context mContext;
    private PlacesClient places;
    private TextView mText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public AutoCompleteAdapter(Context context, String query, TextView t){
        mQuery = query;
        mContext = context;
        places = Places.createClient(mContext);
        mText = t;
    }

    public void findAutocompletePredictions() {
        if(mQuery.length() > 0) {
            FindAutocompletePredictionsRequest.Builder requestBuilder =
                    FindAutocompletePredictionsRequest.builder()
                            .setQuery(mQuery);
            requestBuilder.setSessionToken(AutocompleteSessionToken.newInstance());


            Task<FindAutocompletePredictionsResponse> task =
                    places.findAutocompletePredictions(requestBuilder.build());

            task.addOnSuccessListener(
                    (response) ->
                    {
                        for (AutocompletePrediction a : response.getAutocompletePredictions()) {
                            Log.d("Prediction ", "" + a.getPrimaryText(STYLE_BOLD));
                            Log.d("Prediction ", " your queary " + mQuery);
                            int i = 0;

                            while (i < mQuery.length()) {
                                i++;
                            }
                            if (i <= a.getPrimaryText(STYLE_BOLD).length()) {
                                SpannableString spannedText = new SpannableString(a.getPrimaryText(STYLE_BOLD));
                                int start = 0, end = i;
                                spannedText.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mText.setHint(spannedText);
                            }
                            break;
                        }
                    });


            task.addOnFailureListener(
                    (exception) -> {
                        exception.printStackTrace();
                    });
        }
        else{
            mText.setHint("");
        }

    }

}
