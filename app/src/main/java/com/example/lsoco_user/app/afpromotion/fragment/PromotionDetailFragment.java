package com.example.lsoco_user.app.afpromotion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lsoco_user.app.afpromotion.util.Constants;
import com.example.lsoco_user.app.afpromotion.model.Promotion;
import com.example.lsoco_user.app.afpromotion.R;
import com.squareup.picasso.Picasso;

/**
 * Shows the details of a promotion
 */
public class PromotionDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_fragment, container, false);
        final Promotion promotion = getArguments().getParcelable(Constants.KEY_SELECTED_ITEM);
        if(promotion != null) {
            ImageView image = (ImageView) view.findViewById(R.id.frag_detail_image);
            Picasso.with(getActivity())
                    .load(promotion.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(image);

            TextView title = (TextView) view.findViewById(R.id.frag_detail_title);
            title.setText(promotion.getTitle());

            TextView descr = (TextView) view.findViewById(R.id.frag_detail_descr);
            descr.setText(promotion.getDescription());

            if(promotion.getFooter() != null) {
                TextView footerContent = (TextView) view.findViewById(R.id.frag_detail_footer_content);
                footerContent.setText(promotion.getFooterContent());
                TextView footerLink = (TextView) view.findViewById(R.id.frag_detail_footer_link);
                footerLink.setText(promotion.getFooterLinkText());
                footerLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launchWebViewFragment(promotion.getFooterWebLink());
                    }
                });

            }

            Button button = (Button) view.findViewById(R.id.frag_detail_button);
            button.setText(promotion.getButton().getTitle());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchWebViewFragment(promotion.getButton().getTarget());
                }
            });
        }
        return view;
    }

    private void launchWebViewFragment(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_URL, url);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.main_frag_holder, fragment)
                .addToBackStack(null)
                .commit();
    }
}