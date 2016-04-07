package jul.lib.test.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import jul.lab.library.log.Log;
import jul.lib.test.R;

public class ImageShowFragment extends Fragment {

    public static ImageShowFragment newInstance(String url){
        Bundle args = new Bundle();
        args.putString("url", url);

        ImageShowFragment fragment = new ImageShowFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_img_show, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivImg = (ImageView) view.findViewById(R.id.iv_img);
        Picasso.with(ivImg.getContext())
                .load(getArguments().getString("url"))
                .placeholder(new ColorDrawable(0xffa3a3a3))
                .resize(getActivity().getWindowManager().getDefaultDisplay().getWidth(), getActivity().getWindowManager().getDefaultDisplay().getWidth())
                .error(new ColorDrawable(0xffff0000))
                .into(ivImg);
    }
}

