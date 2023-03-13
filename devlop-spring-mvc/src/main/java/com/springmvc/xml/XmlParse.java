package com.springmvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

public class XmlParse {

    public static String getBasePackages(String xml) {
        try {
            SAXReader saxReader = new SAXReader();
            InputStream inputStream = XmlParse.class.getClassLoader().getResourceAsStream(xml);
            // XML文档对象
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            Element componentScan = rootElement.element("component-scan");
            Attribute attribute = componentScan.attribute("base-package");
            return attribute.getText();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }
}
