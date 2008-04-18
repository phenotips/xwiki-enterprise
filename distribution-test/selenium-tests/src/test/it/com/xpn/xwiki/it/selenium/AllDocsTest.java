/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.it.selenium;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;
import junit.framework.Assert;
import junit.framework.Test;

/**
 * Verify the table view for AllDocs wiki document.
 *
 * @version $Id: $
 */
public class AllDocsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite =
            new XWikiTestSuite("Verify the table view for AllDocs wiki document");
        suite.addTestSuite(AllDocsTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    public void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        open(getUrl("Main", "AllDocs"));
    }

    /**
     * Validate presence of "Actions" column in table view for administrator. Validate absence of
     * "Actions" column for users without administration rights.
     */
    public void testTableViewActionsPresence()
    {
        assertElementPresent("//td[text()='Actions']");

        // Verify that the user is logged in and log out
        if (isAuthenticated()) {
            logout();
        }
        assertElementNotPresent("//td[text()='Actions']");
    }

    /**
     * Validate input suggest for Page field.
     */
    public void testTableViewSuggestForPage()
    {
        getSelenium().typeKeys("page", "Treeview");
        // The table is updated via Ajax, we give it the time to make this call
        getSelenium().setSpeed("1000");
        assertElementPresent("//td[@class='pagename']/a[text()='Treeview']");
        getSelenium().setSpeed("0");
    }

    /**
     * Validate input suggest for Space field.
     */
    public void testTableViewSuggestForSpace()
    {
        getSelenium().typeKeys("space", "XWiki");
        getSelenium().typeKeys("page", "treeview");
        // The table is updated via Ajax, we give it the time to make this call
        getSelenium().setSpeed("1000");
        assertElementPresent("//td[@class='pagename']/a[text()='Treeview']");
        getSelenium().setSpeed("0");
    }

    /**
     * Validate input suggest for Last Author field.
     */
    public void testTableViewSuggestForLastAuthor()
    {
        getSelenium().typeKeys("author", "Admin");
        // The table is updated via Ajax, we give it the time to make this call
        getSelenium().setSpeed("1000");
        assertElementNotPresent("//td[@class='pagename']/a[text()='AggregatorURLClass']");
        getSelenium().setSpeed("0");
    }

    /**
     * Validate Copy link action.
     */
    public void testTableViewCopyAction()
    {
        getSelenium().typeKeys("page", "treeview");
        assertElementPresent("//td[@class='pagename']/a[text()='Treeview']");
        assertElementPresent("link=Copy");
        clickLinkWithText("Copy");
        setFieldValue("targetdoc", "New.TreeviewCopy");
        getSelenium().click("//input[@value='Copy']");
        open(getUrl("Main", "AllDocs"));
        getSelenium().typeKeys("space", "New");
        getSelenium().typeKeys("page", "treeviewcopy");
        assertElementPresent("//td[@class='pagename']/a[text()='TreeviewCopy']");
    }

    /**
     * Validate Rename link action.
     */
    public void testTableViewRenameAction()
    {
        getSelenium().typeKeys("page", "TreeviewCopy");
        clickLinkWithLocator("//tbody/tr/td/a[text()='Rename']");
        setFieldValue("newPageName", "TreeviewCopyRenamed");
        clickLinkWithLocator("//input[@value='Rename']");
        open(getUrl("Main", "AllDocs"));
        getSelenium().typeKeys("page", "TreeviewCopyRenamed");
        assertElementPresent("//td[@class='pagename']/a[text()='TreeviewCopyRenamed']");
    }

    /**
     * Validate Delete link action.
     */
    public void testTableViewDeleteAction()
    {
        getSelenium().typeKeys("page", "Treeviewcopyrenamed");
        clickLinkWithLocator("//tbody/tr/td/a[text()='Delete']");
        clickLinkWithLocator("//input[@value='yes']");
        assertTextPresent("The document has been deleted.");
        open(getUrl("Main", "AllDocs"));
        getSelenium().typeKeys("page", "treeview");
        assertElementNotPresent("//td[@class='pagename']/a[text()='TreeviewCopyRenamed']");
    }

    /**
     * Validate Rights link action.
     */
    public void testTableViewRightsAction()
    {
        getSelenium().typeKeys("page", "Treeview");
        clickLinkWithLocator("//tbody/tr/td/a[text()='Rights']");
        Assert.assertEquals("Editing rights for Treeview", getTitle());
    }
}
