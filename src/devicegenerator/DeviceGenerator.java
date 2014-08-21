/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicegenerator;

import devicerestmodel.representations.DevicePropertyNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author root
 */
public class DeviceGenerator {

    private String resourcePath = "";

    public DeviceGenerator(Document deviceDescriptionDocument) {
        try {
            createRootClass(deviceDescriptionDocument.getRootElement());
            createDefaultApp(deviceDescriptionDocument.getRootElement());
        } catch (IOException ex) {
            Logger.getLogger(DeviceGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static Document getDeviceDescriptionDocument(String path) {
        SAXBuilder docBuilder = new SAXBuilder();
        Document deviceDescriptionDocument = null;
        File deviceDescriptionXmlFile = new File(path);
        if (!deviceDescriptionXmlFile.exists()) {
            System.out.println("Cannot find device description XML file.");
            System.exit(-1);
        }
        try {
            deviceDescriptionDocument = docBuilder.build(deviceDescriptionXmlFile);
        } catch (JDOMException | IOException ex) {
            System.out.println("ex: " + ex.getMessage());
            System.out.println("Cannot read device description XML file.");
            System.exit(-2);
        }

        return deviceDescriptionDocument;
    }

    private void putHeader(FileWriter writer, String className) throws IOException {
        writer.write("// Automatically generated device class file\n");
        writer.write("package representation;\n");
        writer.write("import devicerestmodel.representations.DevicePropertyNode;\n");
        writer.write("import java.beans.PropertyChangeListener;\n");
        writer.write("import org.jdom2.Element;\n");
        writer.write("\n\n");
        writer.write("public class " + className + " ");
        writer.write("extends DevicePropertyNode ");
        writer.write("{\n");
    }

    private void putConstructor(FileWriter writer, String className, boolean ids) throws IOException {
        if (ids) {
            writer.write("\tprivate final String id;\n\n");
        }
        writer.write("\tpublic ");
        writer.write(className);
        writer.write("(PropertyChangeListener listener");
        if (ids) {
            writer.write(", String id");
        }
        writer.write(")");
        writer.write("{\n");
        writer.write("\t\tsuper( listener );\n");
        if (ids) {
            writer.write("\t\tthis.id = id;\n");
        }

    }

    private String getIds(Element child) {
        return child.getAttributeValue("ids");
    }

    private void handleId(Element root, String classFileName, boolean isRoot) throws IOException {

        // Create child classes
        List<Element> children = root.getChildren();
        System.out.println("Creating class: " + classFileName + ".java");
        // Create this class file
        File classFile = new File(classFileName + ".java");
        FileWriter writer = null;
        writer = new FileWriter(classFile);
        putHeader(writer, root.getName());

        ArrayList<String> childIds = new ArrayList<>();
        for (Element child : root.getChildren()) {
            String ids = getIds(child);
            if (ids != null) {
                String varName = child.getName();
                childIds.add(varName);
                writer.write("\n");
                writer.write("private String[] ");
                writer.write("ids" + varName);
                writer.write(" = new String[]{");
                String[] myids = ids.split(",");
                boolean first = true;
                for (String eachid : myids) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(",");
                    }
                    writer.write("\"");
                    writer.write(eachid);
                    writer.write("\"");
                }
                writer.write("};\n\n");
            }
        }
        putConstructor(writer, root.getName(), root.getAttributeValue("ids") != null);
        for (String varName : childIds) {
            writer.write("\t\tfor (String id : ");
            writer.write("ids" + varName);
            writer.write(") {\n");
            writer.write("\t\t\taddChild(new " + varName
                    + "(this,id));\n");
            writer.write("\t\t}\n");
        }

        for (Element element : children) {
            if (element.getAttribute("ids") == null) {
                writer.write("\t\taddChild( new " + createDeviceClasses(element).getName() + "(this) );\n");
            } else {
                createDeviceClasses(element).getName();
            }
        }

        writer.write("\t}\n");
        writer.write("\n\n");

        if (root.getAttributeValue("ids") != null) {
            writer.write("\t@Override\n");
            writer.write("\tpublic String getName() {\n");
            writer.write("\t\treturn super.getName() + id;\n");
            writer.write("\t}\n\n");
        }

        // Comment out this if block to change root back to main xml
        if (isRoot) {
            writer.write("@Override\n");
            writer.write("public String getRootPath\n");
            writer.write("    () {\n");
            writer.write("return \"\";\n");
            writer.write("}\n");
        }

        analyzeClass(writer);

        writer.write("}\n");
        writer.close();

    }

    private void analyzeClass(FileWriter writer) throws IOException {
        Method[] methods = DevicePropertyNode.class.getMethods();

        for (Method myMeth : methods) {
            if ((myMeth.getModifiers() & Modifier.ABSTRACT) != 0) {
//                String method = myMeth.toString().replaceFirst("abstract ", "").replaceFirst("devicerestmodel.representations.DevicePropertyNode.", "");
                writer.write("\t@Override\n");
                writer.write("\tpublic " + myMeth.getReturnType().getSimpleName() + " " + myMeth.getName());
                writer.write("( ");
                Class<?>[] params = myMeth.getParameterTypes();
                for (Class<?> p : params) {
                    writer.write(p.getSimpleName() + " " + p.getName() + " ");
                }
                writer.write(") throws ");
                Type[] throwTypes = myMeth.getGenericExceptionTypes();
                for (Type tt : throwTypes) {
                    writer.write(tt + " ");
                }
                writer.write("{\n");

                writer.write("\t}\n\n");
            }
        }
    }

    private Element createRootClass(final Element root) throws IOException {
        File directory = new File("/tmp/src/representation");
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File mf : files) {
                mf.delete();
            }
        } else {
            directory.mkdirs();
        }

        String classFileName = "/tmp/src/representation/" + root.getName();

        handleId(root, classFileName, true);
        return root;
    }

    private Element createDeviceClasses(final Element root) throws IOException {

        String classFileName = "/tmp/src/representation/" + root.getName();

        handleId(root, classFileName, false);
        return root;
    }

    private void createDefaultApp(final Element root) throws IOException {

        String classFileName = "/tmp/src/representation/DefaultApp.java";

        System.out.println("Creating class: " + classFileName);
        // Create this class file
        File classFile = new File(classFileName);
        FileWriter writer = null;
        writer = new FileWriter(classFile);
        InputStream in = ClassLoader.getSystemResourceAsStream("devicegenerator/DefaultAppSkel.txt");
        BufferedReader inr = new BufferedReader(new InputStreamReader(in));
        for (int cnt = 0; cnt < 35; cnt++) {
            String line = inr.readLine();
            writer.write(line + "\n");
        }
        String nline = inr.readLine();
        writer.write("\t\t\t" + root.getName());
        while (nline != null) {
            nline = inr.readLine();
            if (nline != null) {
                writer.write(nline + "\n");
            }
        }
        writer.close();

    }

//    private String createMapProperty(Element root) {
//        // private Map<String, InputOutput> inputOutputMap = new HashMap<>();
//        // private String[] ids = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
//        
//        
//        
//    }
//   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        System.out.println("Franck char "+new String(new byte[]{(byte)0x2f}));
        Document deviceDescriptionDocument = getDeviceDescriptionDocument(args[0]);
        new DeviceGenerator(deviceDescriptionDocument);
    }
}
