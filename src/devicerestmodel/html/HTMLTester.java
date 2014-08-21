/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.html;

import devicerestmodel.htmlhelpers.FileChooser;

/**
 *
 * @author root
 */
public class HTMLTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        FileChooser doc = new FileChooser();
        System.out.println("Doc is " + doc.getContent());

        HTMLForm form = new HTMLForm();
        form.addAttribute("action", "/");
        form.addAttribute("enctype", "multipart/form-data");
        form.addAttribute("method", "post");
        HTMLP para = new HTMLP();
        form.addHTMLContent(para);
        para.addText("files: ");
        HTMLInput finput = new HTMLInput();
        para.addHTMLContent(finput);
        finput.addAttribute("type", "file");
        finput.addAttribute("name", "datafile");
        finput.addAttribute("size", "40");


        HTMLInput input = new HTMLInput();
        input.addAttribute("type", "submit");
        input.addAttribute("value", "submit");
        form.addHTMLContent(new HTMLDiv()).addHTMLContent(input);

        StringBuilder s = new StringBuilder();
        form.getHTML(s);
        System.out.println("MyForm is " + s);


    }
}
