package org.softeg.slartus.forpdaplus.devdb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.softeg.slartus.forpdaplus.R;
import org.softeg.slartus.forpdaplus.devdb.adapters.base.BaseRecyclerViewHolder;
import org.softeg.slartus.forpdaplus.devdb.helpers.DevDbUtils;
import org.softeg.slartus.forpdaplus.devdb.model.ReviewsModel;

import java.util.List;

import butterknife.BindView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private static final int LAYOUT = R.layout.dev_db_reviews_item;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<ReviewsModel> mModels;
    private ImageLoader imageLoader;

    public ReviewsAdapter(Context context, List<ReviewsModel> models, ImageLoader imageLoader) {
        super();
        this.mContext = context;
        this.mModels = models;
        this.imageLoader = imageLoader;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(LAYOUT, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ReviewsModel obj = mModels.get(position);
        holder.textBody.setText(obj.getReviewTitle());
        holder.date.setText(obj.getReviewDate());

        holder.textBody.setOnClickListener(v -> DevDbUtils.showUrl(mContext, obj.getReviewLink()));

        //Picasso.with(mContext).load(obj.getReviewImgLink()).into(holder.image);
        imageLoader.displayImage(obj.getReviewImgLink(), holder.image);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public static class ViewHolder extends BaseRecyclerViewHolder {

        @BindView(R.id.devDbReviewsText)
        TextView textBody;
        @BindView(R.id.devDbReviewsDate)
        TextView date;
        @BindView(R.id.devDbReviewsIV)
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
