package id.sentuh.digitalsignage.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Objects;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.models.Frames;
import id.sentuh.digitalsignage.models.Views;
import id.sentuh.digitalsignage.views.MultiPager;

import static id.sentuh.digitalsignage.R.id.imageView;
import static id.sentuh.digitalsignage.R.layout.fragment_image;
import static id.sentuh.digitalsignage.app.EndPoints.TAG;


public class ImageFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    public static View view;
    private String mParam1;
    private int mIndexPage;
    private int mPagerIndex;
    private int DELAY_TIME = 5000;
//    Handler mHandler;
    Context mContext;
    Dialog myDialog;
    View Currentview;
    List<Views> views=null;
    List<MultiPager> mPagers;
    List<Frames> frames=null;
    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ImageFragment.
     */
    public static ImageFragment newInstance(String param1,int index,int pagerIndex) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_INDEX, index);
        args.putInt(PAGER_INDEX, pagerIndex);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Currentview = view;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mIndexPage = getArguments().getInt(ARG_INDEX);
            mPagerIndex = getArguments().getInt(PAGER_INDEX);
        }

//        mHandler = new Handler();
        mContext = getContext();

    }
    @SuppressLint("Assert")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = this.getLayoutInflater();
        view = inflater.inflate(fragment_image, container, false);
        int parent_height = view.getHeight();
        int parent_width = view.getWidth();
//        final ImagePopup imagePopup = new ImagePopup(getLayoutInflater().getContext());
//        imagePopup.setWindowHeight(800);
//        imagePopup.setWindowWidth(800);
//        imagePopup.setBackgroundColor(Color.BLACK);
//        imagePopup.setFullScreen(true);
//        imagePopup.setHideCloseIcon(true);
//        imagePopup.setImageOnClickClose(true);


//        com.gigamole.library.ShadowLayout shadowLayout = view.findViewById(R.id.shadow);
        final RoundedImageView roundedImageView = view.findViewById(imageView);
        roundedImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        int MyVersion = Build.VERSION.SDK_INT;


        if (MyVersion>=Build.VERSION_CODES.JELLY_BEAN) {
            if (ScreenFragment.height >= ScreenFragment.screen_height &&
                    ScreenFragment.width >= ScreenFragment.screen_width) {
                roundedImageView.setCornerRadius(0);

            } else if (ScreenFragment.height >= ScreenFragment.maxHeight &&
                    ScreenFragment.width >= ScreenFragment.maxWidth){
                roundedImageView.setCornerRadius(0);

            }else {
                roundedImageView.setCornerRadius(20);
            }

        }


        final File file = new File(mParam1);
        final Uri uriFile = Uri.fromFile(file);
        int ivWidth = roundedImageView.getWidth();
        int ivHeight = roundedImageView.getHeight();
        if(file.exists()){

           try {
//
               Log.d(TAG,"image : "+mParam1);
//               Glide.with(this)
//                       .load(uriFile)
//                       .apply(RequestOptions
//                               .placeholderOf(R.drawable.pagenotfound)
//                       .override(ScreenFragment.width,ScreenFragment.height)
//                       .error(R.drawable.pagenotfound))
               Picasso.with(mContext)
                       .load(uriFile)
//                       .resize(4501,8000)
//                       .resize(ScreenFragment.width,ScreenFragment.height)
//                       .onlyScaleDown()
                       .fit()
                       .centerInside()
                       .error(R.drawable.pagenotfound)
                       .into(roundedImageView);
           } catch (Exception ex){
               Log.d(TAG,"image : "+mParam1+" error");
           }

        //Popup Image
//            final LayoutInflater finalInflater = inflater;
//            roundedImageView.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint({"ResourceType", "InflateParams"})
//                public void onClick(View view) {
//                    Log.d(TAG,"popup");
////
//                    final Dialog dialog = new Dialog(mContext);
//                    final View popupView = finalInflater.inflate(R.layout.popup, null);
//                    RoundedImageView roundedImageView1 = popupView.findViewById(R.id.popup_image);
//                    Glide.with(mContext)
//                            .load(uriFile)
////                            .resize(800,800)
//                            .apply(new RequestOptions()
//                                    .override(1000,1000)
//                                    .centerInside()
//                                    .error(R.drawable.pagenotfound))
////                            .centerInside()
////                            .error(R.drawable.blank_image)
//                            .into(roundedImageView1);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    dialog.setContentView(popupView);
//                    dialog.show();
//
//                    roundedImageView1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                }
//            });


            return view;
        }

        return null;
    }

    private void loadView(Views currentView){

    }

    Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mIndexPage++;
//            mListener.OnFrameFinish(mIndexPage,true);
//            mHandler.postDelayed(mRunnable,DELAY_TIME);
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mHandler.removeCallbacks(mRunnable);
    }
}
