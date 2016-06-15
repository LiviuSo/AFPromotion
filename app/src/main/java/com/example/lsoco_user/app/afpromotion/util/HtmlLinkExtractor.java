package com.example.lsoco_user.app.afpromotion.util;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts web-links links from a string
 * Taken from : http://www.mkyong.com/regular-expressions/how-to-extract-html-links-with-regular-expression/
 * Thanks!
 */
public class HtmlLinkExtractor {

    private Pattern patternTag, patternLink;

    private static final String HTML_A_TAG_PATTERN      = "(?i)<a([^>]+)>(.+?)</a>";
    private static final String HTML_A_HREF_TAG_PATTERN =
            "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";

    public HtmlLinkExtractor() {
        patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
        patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
    }

    /**
     * Validate html with regular expression
     *
     * @param html html content for validation
     * @return Vector links and link text
     */
    public Vector<HtmlLink> grabHTMLLinks(final String html) {

        Vector<HtmlLink> result = new Vector<>();

        Matcher matcherTag;
        matcherTag = patternTag.matcher(html);

        while (matcherTag.find()) {

            String href = matcherTag.group(1); // href
            String linkText = matcherTag.group(2); // link text

            Matcher matcherLink = patternLink.matcher(href);

            while (matcherLink.find()) {

                String link = matcherLink.group(1); // link
                HtmlLink obj = new HtmlLink();
                obj.setLink(link);
                obj.setLinkText(linkText);

                result.add(obj);
            }
        }

        return result;
    }

    public static class HtmlLink {

        String link;
        String linkText;

        HtmlLink() {
        }

        @Override
        public String toString() {
            return new StringBuffer("Link : ").append(this.link)
                    .append(" Link Text : ").append(this.linkText).toString();
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = replaceInvalidChar(link);
        }

        public String getLinkText() {
            return linkText;
        }

        public void setLinkText(String linkText) {
            this.linkText = linkText;
        }

        private String replaceInvalidChar(String link) {
            link = link.replaceAll("'", "");
            link = link.replaceAll("\"", "");
            return link;
        }
    }
}