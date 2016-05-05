package jul.lib.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import jul.lib.test.R;

/**
 * Created by owner on 2016. 5. 4..
 */
public class MaterialTransitionAdapter extends RecyclerView.Adapter<MaterialTransitionAdapter.ViewHolder>{

    private List<Integer> mImageResIds;

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mIvThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material_sample, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mIvThumb.setImageResource(mImageResIds.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageResIds.size();
    }


    public void setImageResIds(List<Integer> list){
        mImageResIds = list;
        notifyDataSetChanged();
    }

}
