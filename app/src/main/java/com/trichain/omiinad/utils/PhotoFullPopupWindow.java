package com.trichain.omiinad.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.trichain.omiinad.R;
import com.trichain.omiinad.entities.PhotoTable;

import java.io.File;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class PhotoFullPopupWindow extends PopupWindow {

    View view;
    Context mContext;
    ViewGroup parent;
    ImageView imageView;
    CarouselView carouselView;
    private static PhotoFullPopupWindow instance = null;


    public PhotoFullPopupWindow(int mPosition, List<PhotoTable> photoTable, Context ctx, String name, int layout, View v, String imageUrl, Bitmap bitmap) {
        super(((LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_photo_full, null), ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        if (Build.VERSION.SDK_INT >= 21) {
            setElevation(5.0f);
        }
        this.mContext = ctx;
        this.view = getContentView();
        ImageButton closeButton = (ImageButton) this.view.findViewById(R.id.ib_close);
        ImageButton sharePhoto = this.view.findViewById(R.id.imgGalleryShare);


        setOutsideTouchable(true);

        setFocusable(true);
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                dismiss();
            }
        });
        //---------Begin customising this popup--------------------

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                // imageView.setImageResource(sampleImages[position]);
                String filename = photoTable.get(position).getPhotoName();
                Glide.with(ctx)
                        .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + filename))
                        .fallback(R.drawable.landscape)
                        .placeholder(R.drawable.landscape)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(imageView);
            }
        };

        carouselView = view.findViewById(R.id.image2);
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(photoTable.size());
        carouselView.setCurrentItem(mPosition);

        sharePhoto.setOnClickListener(v12 -> {
            SendFile sendFile = new SendFile();
            sendFile.sendMyFile(ctx, (Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + photoTable.get(carouselView.getCurrentItem()).getPhotoName()));
        });
        imageView = view.findViewById(R.id.image);
        parent = (ViewGroup) imageView.getParent();
        // ImageUtils.setZoomable(imageView);
        //----------------------------
        if (bitmap != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                parent.setBackground(new BitmapDrawable(mContext.getResources(), Utils.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true))));// ));
            } else {
                onPalette(Palette.from(bitmap).generate());
            }
            imageView.setImageBitmap(bitmap);
        } else {
            Glide.with(ctx).asBitmap()
                    .load(imageUrl)
                    .fallback(R.drawable.ic_landscape)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (Build.VERSION.SDK_INT >= 16) {
                                parent.setBackground(new BitmapDrawable(mContext.getResources(), Utils.fastblur(Bitmap.createScaledBitmap(resource, 50, 50, true))));// ));
                            } else {
                                onPalette(Palette.from(resource).generate());

                            }
                            imageView.setImageBitmap(resource);

                            return false;
                        }
                    })
                    .transition(withCrossFade(500))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            showAtLocation(v, Gravity.CENTER, 0, 0);
        }
        //------------------------------

    }

    public void onPalette(Palette palette) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) imageView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }
}
