/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.app;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author matt
 */
public class MdUtil {

    public static String element2XmlString(Element element) throws IOException {
        StringWriter string = new StringWriter();
        XMLOutputter output = new XMLOutputter();
        output.setFormat(Format.getPrettyFormat());
        output.output(element, string);
        return string.toString();
    }

    public static Element xmlString2Element(String string) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(string));
        return document.getRootElement();
    }

    public static String document2XmlString(Document myDocument) throws IOException {
        StringWriter string = new StringWriter();
        XMLOutputter output = new XMLOutputter();
        output.setFormat(Format.getPrettyFormat());
        output.output(myDocument, string);
        return string.toString();
    }

}
