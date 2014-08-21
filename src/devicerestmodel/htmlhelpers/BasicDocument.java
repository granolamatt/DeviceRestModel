/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.htmlhelpers;

import devicerestmodel.html.HTMLBody;
import devicerestmodel.html.HTMLHead;
import devicerestmodel.html.HTMLMETA;
import devicerestmodel.html.HTMLP;
import devicerestmodel.html.HTMLhtml;

/**
 *
 * @author root
 */
public class BasicDocument {

//    private final StringBuilder buffer = new StringBuilder();
//    private int refresh = -1;
    private HTMLHead head = new HTMLHead();
    private HTMLMETA meta = null;
    private HTMLP para = new HTMLP();

    public BasicDocument() {
    }

    public BasicDocument(int refresh) {
        this();
        meta = new HTMLMETA().setRefresh(refresh);
    }

    public void setRefresh(int seconds) {
        meta = new HTMLMETA().setRefresh(seconds);
    }

    public void addHeadContent(String content) {
        head.addContent(content);
    }
    public void addLine(String line) {
        para.addLine(line);
    }

    public void addContent(String content) {
        para.addText(content);
    }

    public void addContent(StringBuilder content) {
        addContent(content.toString());
    }

    @Override
    public String toString() {
        StringBuilder cont = new StringBuilder();
        HTMLhtml document = new HTMLhtml();
        if (head != null) {
            document.addHTMLContent(head);
        }
        if (meta != null) {
            document.addHTMLContent(meta);
        }
        document.addHTMLContent(new HTMLBody()).addHTMLContent(para);
        document.getHTML(cont);
        return cont.toString();
    }
}
