package com.david.nytimessearch.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.david.nytimessearch.R;
import com.david.nytimessearch.databinding.ItemStaggeredArticleBinding;
import com.david.nytimessearch.databinding.ItemStaggeredImageBinding;
import com.david.nytimessearch.databinding.ItemStaggeredTextBinding;
import com.david.nytimessearch.models.Article;
import com.david.nytimessearch.util.DynamicHeightImageView;

import java.util.List;

/**
 * Created by David on 3/19/2017.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    ItemStaggeredArticleBinding genericBinding;
    ItemStaggeredImageBinding imageBinding;
    ItemStaggeredTextBinding textBinding;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void onPrepareLoad(Drawable placeHolderDrawable) {
            //do nothing?
        }

        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("DEBUG", "Bitmap failed to load.");
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Article article = articles.get(position);
                //create intent to display article
//                Intent i = new Intent(context, ArticleActivity.class);
//                //pass in article into intent
//                i.putExtra("article", article);
//                //launch activity
//                context.startActivity(i);

                // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
                String url = article.getWebUrl();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                // set toolbar color and/or setting custom actions before invoking build()
                builder.setToolbarColor(ContextCompat.getColor(context, R.color.bgColor));

                //add share icon
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_share);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                int requestCode = 100;
                PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // Map the bitmap, text, and pending intent to this icon
                // Set tint to be true so it matches the toolbar color
                builder.setActionButton(bitmap, context.getResources().getString(R.string.share_link), pendingIntent, true);

                // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                CustomTabsIntent customTabsIntent = builder.build();
                // and launch the desired Url with CustomTabsIntent.launchUrl()
                customTabsIntent.launchUrl(context, Uri.parse(url));
            }
        }
    }

    public class ViewHolderGeneric extends ViewHolder {
        public DynamicHeightImageView ivImage;
        public TextView tvTitle;

        public ViewHolderGeneric(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            genericBinding = ItemStaggeredArticleBinding.bind(itemView);

            ivImage = genericBinding.ivImage;
            tvTitle = genericBinding.tvTitle;

            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        public SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
//                Log.d("DEBUG", "setting height ratio: " + ratio);
                // Set the ratio for the image
                ivImage.setHeightRatio(ratio);
                // Load the image into the view
                ivImage.setImageBitmap(bitmap);
            }
        };

//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            // Calculate the image ratio of the loaded bitmap
//            float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
////            Log.d("DEBUG", "setting height ratio: " + ratio);
//            // Set the ratio for the image
//            ivImage.setHeightRatio(ratio);
//            // Load the image into the view
//            ivImage.setImageBitmap(bitmap);
//        }
    }

    public class ViewHolderImage extends ViewHolder {
        public DynamicHeightImageView ivImage;

        public ViewHolderImage(View itemView) {
            super(itemView);

            imageBinding = ItemStaggeredImageBinding.bind(itemView);

            ivImage = imageBinding.ivImage;

            itemView.setOnClickListener(this);
        }

        public SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
//                Log.d("DEBUG", "setting height ratio: " + ratio);
                // Set the ratio for the image
                ivImage.setHeightRatio(ratio);
                // Load the image into the view
                ivImage.setImageBitmap(bitmap);
            }
        };

//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            // Calculate the image ratio of the loaded bitmap
//            float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
////            Log.d("DEBUG", "setting height ratio: " + ratio);
//            // Set the ratio for the image
//            ivImage.setHeightRatio(ratio);
//            // Load the image into the view
//            ivImage.setImageBitmap(bitmap);
//        }
    }

    public class ViewHolderText extends ViewHolder {
        public TextView tvTitle;

        public ViewHolderText(View itemView) {
            super(itemView);

            textBinding = ItemStaggeredTextBinding.bind(itemView);

            tvTitle = textBinding.tvTitle;

            itemView.setOnClickListener(this);
        }
    }

    // Store a member variable for the articles
    private List<Article> articles;
    // Store the context for easy access
    private Context context;

    private final int GENERIC = 0, IMAGE = 1, TEXT = 2;

    // Pass in the article array into the constructor
    public ArticlesAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return this.context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;

        switch (viewType) {
            case GENERIC:
                View view1 = inflater.inflate(R.layout.item_staggered_article, parent, false);
                viewHolder = new ViewHolderGeneric(view1);
                break;
            case IMAGE:
                View view2 = inflater.inflate(R.layout.item_staggered_image, parent, false);
                viewHolder = new ViewHolderImage(view2);
                break;
            case TEXT:
            default:
                View view3 = inflater.inflate(R.layout.item_staggered_text, parent, false);
                viewHolder = new ViewHolderText(view3);
                break;
        }

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = this.articles.get(position);

        switch (viewHolder.getItemViewType()) {
            case GENERIC:
                ViewHolderGeneric viewHolderGeneric = (ViewHolderGeneric) viewHolder;
                configureViewHolderGeneric(viewHolderGeneric, article);
                break;
            case IMAGE:
                ViewHolderImage viewHolderImage = (ViewHolderImage) viewHolder;
                configureViewHolderImage(viewHolderImage, article);
                break;
            case TEXT:
            default:
                ViewHolderText viewHolderText = (ViewHolderText) viewHolder;
                configureViewHolderText(viewHolderText, article);
                break;
        }
    }

    private void configureViewHolderGeneric(ViewHolderGeneric viewHolder, Article article) {
//        TextView textView = viewHolder.tvTitle;
//        textView.setText(article.getHeadLine());
        genericBinding.setArticle(article);
        genericBinding.executePendingBindings();

        DynamicHeightImageView imageView = viewHolder.ivImage;
        imageView.setImageResource(0);

        String thumbnail = article.getThumbNail();
//        Log.d("DEBUG", "thumbnail url: " + thumbnail);
        Glide.with(context).load(thumbnail)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.target);
    }

    private void configureViewHolderImage(ViewHolderImage viewHolder, Article article) {
        DynamicHeightImageView imageView = viewHolder.ivImage;
        imageView.setImageResource(0);

        String thumbnail = article.getThumbNail();
//        Log.d("DEBUG", "thumbnail url: " + thumbnail);
        Glide.with(context).load(thumbnail)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.target);
    }

    private void configureViewHolderText(ViewHolderText viewHolder, Article article) {
//        TextView textView = viewHolder.tvTitle;
//        textView.setText(article.getHeadLine());
        textBinding.setArticle(article);
        textBinding.executePendingBindings();
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = this.articles.get(position);
        if (!TextUtils.isEmpty(article.getHeadLine()) && !TextUtils.isEmpty(article.getThumbNail())) {
            return GENERIC;
        } else if (!TextUtils.isEmpty(article.getThumbNail())) {
            return IMAGE;
        } else {
            return TEXT;
        }
    }
}
