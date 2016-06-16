package com.example.lsoco_user.app.afpromotion;

import com.example.lsoco_user.app.afpromotion.util.HtmlLinkExtractor;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Vector;


/**
 * Tests for the HtmlLinkExtractor
 */

public class HtmlLinkExtractorTest {

    private HtmlLinkExtractor htmlLinkExtractor;
    private final String TEST_LINK = "http://www.google.com";
    private String[] dataSingleLink;
    private String dataMultipleLinks;

    @Before
    public void initData() {
        htmlLinkExtractor = new HtmlLinkExtractor();
        dataSingleLink = new String[] {
                "abc hahaha <a href='" + TEST_LINK + "'>google</a>",
                "abc hahaha <a HREF='" + TEST_LINK + "'>google</a>",
                "abc hahaha <A HREF='" + TEST_LINK + "' target='_blank'>google</A>",
                "abc hahaha <A target='_blank' HREF='" + TEST_LINK + "'>google</A>",
                "abc hahaha <A target='_blank' HREF=\"" + TEST_LINK + "\">google</A>",
                "abc hahaha <a HREF=" + TEST_LINK + ">google</a>" };
        dataMultipleLinks = "abc hahaha <A HREF='" + TEST_LINK + "'>google</A> , "
                + "abc hahaha <A HREF='" + TEST_LINK + "' target='_blank'>google</A>";
    }

    @Test
    public void validSingleLinkTest() {
        for(String html : dataSingleLink) {
            Vector<HtmlLinkExtractor.HtmlLink> links = htmlLinkExtractor.grabHTMLLinks(html);
            Assert.assertTrue(links.size() != 0);
            Assert.assertEquals(links.get(0).getLink(), TEST_LINK);
        }
    }

    @Test
    public void validMultipleLinksTest() {
        Vector<HtmlLinkExtractor.HtmlLink> links = htmlLinkExtractor.grabHTMLLinks(dataMultipleLinks);
        Assert.assertTrue(links.size() != 0);
        for(HtmlLinkExtractor.HtmlLink link : links) {
            Assert.assertEquals(link.getLink(), TEST_LINK);
        }
    }
}