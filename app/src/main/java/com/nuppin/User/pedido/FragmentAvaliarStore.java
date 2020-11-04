package com.nuppin.User.pedido;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.model.ErrorCode;
import com.nuppin.model.Scheduling;
import com.nuppin.model.Order;
import com.nuppin.Util.Util;
import com.nuppin.connection.ConnectApi;
import com.nuppin.R;


public class FragmentAvaliarStore extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private LoaderManager loaderManager;
    private RatingBar ratingBar;
    private EditText edtInput;
    private MaterialButton btn;
    private Order order;
    private TextView nomeLoja;
    private SimpleDraweeView fotoLoja;
    private int rating;
    private static String ORDERS = "ORDERS";
    private static String AGENDAMENTOS = "AGENDAMENTOS";
    private String TAG;
    private Scheduling scheduling;
    private CardView progress;

    public FragmentAvaliarStore() {
    }

    public static FragmentAvaliarStore newInstance(Order order) {
        FragmentAvaliarStore fragment = new FragmentAvaliarStore();
        Bundle args = new Bundle();
        args.putParcelable(ORDERS, order);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentAvaliarStore newInstance(Scheduling scheduling) {
        FragmentAvaliarStore fragment = new FragmentAvaliarStore();
        Bundle args = new Bundle();
        args.putParcelable(AGENDAMENTOS, scheduling);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ORDERS)) {
            order = getArguments().getParcelable(ORDERS);
            TAG = ORDERS;
        }
        if (getArguments() != null && getArguments().containsKey(AGENDAMENTOS)) {
            scheduling = getArguments().getParcelable(AGENDAMENTOS);
            TAG = AGENDAMENTOS;
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_company_rating, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        progress = view.findViewById(R.id.progress);
        ratingBar = view.findViewById(R.id.ratingBar);
        edtInput = view.findViewById(R.id.ratingObs);
        btn = view.findViewById(R.id.btn);
        nomeLoja = view.findViewById(R.id.nomeLoja);
        fotoLoja = view.findViewById(R.id.fotoLoja);

        if (TAG.equals(ORDERS)) {
            Util.hasPhoto(order,fotoLoja);
            nomeLoja.setText(order.getCompany_name());
        } else {
            Util.hasPhoto(scheduling,fotoLoja);
            nomeLoja.setText(scheduling.getCompany_name());
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = ratingBar.getProgress();

                if (rating < 1) {
                    Toast.makeText(getContext(), R.string.warning_before_rating, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TAG.equals(ORDERS)) {
                    order.setRating(rating);
                    order.setRating_note(edtInput.getText().toString());
                } else {
                    scheduling.setRating(rating);
                    scheduling.setRating_note(edtInput.getText().toString());
                }
                progress.setVisibility(View.VISIBLE);
                btn.setClickable(false);
                loaderManager.restartLoader(1, null, FragmentAvaliarStore.this);
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        if (TAG.equals(ORDERS)) {
            return new StoreRatingLoader(requireContext(), order);
        } else {
            return new AgendamentoRating(requireContext(), scheduling);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(1);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof Boolean && (Boolean) data) {
                Toast.makeText(getActivity(), R.string.avaliado, Toast.LENGTH_SHORT).show();
                Util.backFragmentFunction(this);
            }else if(data instanceof ErrorCode){
                Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
                Util.backFragmentFunction(this);
            }else {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class StoreRatingLoader extends AsyncTaskLoader<Object> {

        Order order;

        private StoreRatingLoader(@NonNull Context context, Order order) {
            super(context);
            this.order = order;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            String json = ConnectApi.PATCH(order, ConnectApi.STORES_RATING, getContext());
            try {
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(),Boolean.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }            }
        }
    }

    private static class AgendamentoRating extends AsyncTaskLoader<Object> {

        Scheduling scheduling;

        private AgendamentoRating(@NonNull Context context, Scheduling scheduling) {
            super(context);
            this.scheduling = scheduling;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                String json = ConnectApi.PATCH(scheduling, ConnectApi.STORES_RATING_AGENDAMENTO, getContext());
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(),Boolean.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

}
