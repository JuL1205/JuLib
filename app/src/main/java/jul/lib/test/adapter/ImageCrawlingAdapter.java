package jul.lib.test.adapter;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import jul.lab.library.crawling.ImageUrlList;
import jul.lab.library.log.Log;
import jul.lib.test.R;
import jul.lib.test.activity.ImageCrawlingActivity;

/**
 * Created by owner on 2016. 4. 6..
 */
public class ImageCrawlingAdapter extends RecyclerView.Adapter<ImageCrawlingAdapter.ViewHolder> implements ImageUrlList.OnDataChangeListener {

    private ImageUrlList mImageUrlList;
    private int mAspectSize;

    public ImageCrawlingAdapter(int screenWidth, ImageUrlList urlList){
        mImageUrlList = urlList;
        mAspectSize = screenWidth / 4;
        mImageUrlList.setOnDataChangeListener(this);
    }

    @Override
    public void onAdd(int addCount) {
//        notifyItemRangeInserted(mImageUrlList.size()-addCount, addCount);
//        notifyItemInserted(mImageUrlList.size() - 1);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mIvThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIvThumb.getLayoutParams();
            params.width = params.height = mAspectSize;
            mIvThumb.setLayoutParams(params);
        }
    }

    @Override
    public ImageCrawlingAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_image_crawling, null));
    }

    @Override
    public void onBindViewHolder(ImageCrawlingAdapter.ViewHolder viewHolder, int i) {
//        viewHolder.mIvThumb.setImageDrawable(new ColorDrawable(0xffff0000));
        Picasso.with(viewHolder.mIvThumb.getContext())
                .load(ImageCrawlingActivity.DOMAIN+mImageUrlList.get(i))
                .resize(mAspectSize, mAspectSize)
                .placeholder(new ColorDrawable(0xffa3a3a3))
                .error(new ColorDrawable(0xffff0000))
                .centerCrop()
                .into(viewHolder.mIvThumb);
    }

    @Override
    public int getItemCount() {
        return mImageUrlList.size();
    }
}
