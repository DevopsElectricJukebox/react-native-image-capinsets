package dk.madslee.imageCapInsets;

import android.util.Log;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.NinePatchDrawable;
import android.widget.ImageView;
import dk.madslee.imageCapInsets.utils.NinePatchBitmapFactory;
import dk.madslee.imageCapInsets.utils.RCTImageLoaderListener;
import dk.madslee.imageCapInsets.utils.RCTImageLoaderTask;


public class RCTImageCapInsetView extends ImageView {

    private static final String TAG = "RCTImageCapInsetView";
    private Rect mCapInsets;
    private String mUri;

    public RCTImageCapInsetView(Context context) {
        super(context);

        mCapInsets = new Rect();
    }

    public void setCapInsets(Rect insets) {
        mCapInsets = insets;
        reload();
    }

    public void setSource(String uri) {
        mUri = uri;
        reload();
    }

    public void reload() {
        final String key = mUri + "-" + mCapInsets.toShortString();
        final RCTImageCache cache = RCTImageCache.getInstance();

        if (cache.has(key)) {
            setBackground(cache.get(key).getConstantState().newDrawable());
            return;
        }

        RCTImageLoaderTask task = new RCTImageLoaderTask(mUri, getContext(), new RCTImageLoaderListener() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                if (bitmap == null) {
                    Log.w(TAG, "failed to load bitmap from " + mUri);
                    return;
                }
                int top = mCapInsets.top;
                int right = bitmap.getWidth() - mCapInsets.right;
                int bottom = bitmap.getHeight() - mCapInsets.bottom;
                int left = mCapInsets.left;

                NinePatchDrawable ninePatchDrawable = NinePatchBitmapFactory.createNinePathWithCapInsets(getResources(), bitmap, top, left, bottom, right, null);
                setBackground(ninePatchDrawable);

                cache.put(key, ninePatchDrawable);
            }
        });

        task.execute();
    }
}
