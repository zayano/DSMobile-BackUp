package id.sentuh.digitalsignage.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.helper.OnDialogListener;

import static id.sentuh.digitalsignage.app.EndPoints.TAG;


public class ImageDialogFragment extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    OnDialogListener mListener;
    private int DELAY_TIME = 15000;
    Handler mHandler;
    Context mContext;
    public ImageDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void dismiss() {
        mListener.OnDialogClose(true);
        super.dismiss();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ImageFragment.
     */
    public static ImageDialogFragment newInstance(String param1) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler.postDelayed(mRunnable,DELAY_TIME);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
        mHandler = new Handler();
        mContext = getActivity();
        mListener = (OnDialogListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        //Picasso.with(getContext()).load(mParam1).into(imageView);
        String filepath = mParam1;
        Log.d(TAG,"image : "+filepath);
        File file = new File(filepath);
        Uri imageUri = Uri.fromFile(file);
        if(file.exists()){
            Picasso.with(mContext)
                    .load(imageUri).error(R.drawable.pagenotfound)
                    .into(imageView);
        }

        return view;
    }
    Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {

        dismiss();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mRunnable);
    }

}
