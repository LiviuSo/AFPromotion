package com.example.lsoco_user.app.afpromotion.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.lsoco_user.app.afpromotion.util.HtmlLinkExtractor;

import java.util.Vector;

/**
 * Model to hold the promotion feed
 */
public class Promotion implements Parcelable {

    private String            title;
    private String            image;
    private String            description;
    private String            footer;
    private PromotionButton   button;
    private HtmlLinkExtractor htmlExtr;

    public Promotion() {
        htmlExtr = new HtmlLinkExtractor();
    }

    protected Promotion(Parcel in) {
        title = in.readString();
        image = in.readString();
        description = in.readString();
        footer = in.readString();
        button = in.readParcelable(PromotionButton.class.getClassLoader());
    }

    public static final Creator<Promotion> CREATOR = new Creator<Promotion>() {
        @Override
        public Promotion createFromParcel(Parcel in) {
            return new Promotion(in);
        }

        @Override
        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFooter() {
        return footer;
    }

    public String getFooterContent() {
        int indexStartLink = footer.indexOf('<');
        return footer.substring(0, indexStartLink);
    }

    public String getFooterLink() {
        int indexStartLink = footer.indexOf('<');
        return footer.substring(indexStartLink, footer.length());
    }

    public String getFooterLinkText() {
        Vector<HtmlLinkExtractor.HtmlLink> links = htmlExtr.grabHTMLLinks(getFooterLink());
        return links.get(0).getLinkText();
    }

    public String getFooterWebLink() {
        Vector<HtmlLinkExtractor.HtmlLink> links = htmlExtr.grabHTMLLinks(getFooterLink());
        return links.get(0).getLink();
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public PromotionButton getButton() {
        return button;
    }

    public void setButton(PromotionButton button) {
        this.button = button;
    }

    @Override
    public String toString() {
        return "Title: " + title + '\n' + "Image: " + image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeString(footer);
        dest.writeParcelable(button, flags);
    }

    /**
     * Promotion button; has a title and a target (a web-link)
     */
    public static class PromotionButton implements Parcelable {

        private String target;
        private String title;

        public PromotionButton() {
        }

        protected PromotionButton(Parcel in) {
            target = in.readString();
            title = in.readString();
        }

        public static final Creator<PromotionButton> CREATOR = new Creator<PromotionButton>() {
            @Override
            public PromotionButton createFromParcel(Parcel in) {
                return new PromotionButton(in);
            }

            @Override
            public PromotionButton[] newArray(int size) {
                return new PromotionButton[size];
            }
        };

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("BUTTON\n")
                    .append("Title: ").append(title).append('\n')
                    .append("Target: ").append(target);
            return stringBuilder.toString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(target);
        }
    }
}
