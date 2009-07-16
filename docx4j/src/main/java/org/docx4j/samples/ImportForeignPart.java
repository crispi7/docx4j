/*
 *  Copyright 2007-2008, Plutext Pty Ltd.
 *   
 *  This file is part of docx4j.

    docx4j is licensed under the Apache License, Version 2.0 (the "License"); 
    you may not use this file except in compliance with the License. 

    You may obtain a copy of the License at 

        http://www.apache.org/licenses/LICENSE-2.0 

    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, 
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
    See the License for the specific language governing permissions and 
    limitations under the License.

 */


package org.docx4j.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.docx4j.JcrNodeMapper.NodeMapper;
import org.docx4j.convert.out.flatOpcXml.FlatOpcXmlCreator;
import org.docx4j.dml.Inline;
import org.docx4j.docProps.extended.Properties;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.Base;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.io.Load;
import org.docx4j.openpackaging.io.SaveToZipFile;

import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.contenttype.ContentTypeManager;
import org.docx4j.openpackaging.contenttype.ContentTypes;
import org.docx4j.relationships.Relationship;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * Import foreign parts 
 * 
 * @author Jason Harrop
 * @version 1.0
 */
public class ImportForeignPart {

	public static void main(String[] args) throws Exception {
		
		System.out.println( "Creating package..");
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		
		// Need to know how what type of part to map to		
		InputStream in = new FileInputStream("/home/dev/workspace/docx4j/foregin_parts/[Content_Types].xml");
		SAXReader xmlReader = new SAXReader();
		Document ctmDocument = null;
		try {
			ctmDocument = xmlReader.read(in);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw e;
		}
		ContentTypeManager externalCtm = new ContentTypeManager();
		externalCtm.parseContentTypesFile(ctmDocument);
		
		// Example of a part which become a rel of the word document
		in = new FileInputStream("/home/dev/workspace/docx4j/foreign_parts/word/settings.xml");
		attachForeignPart(wordMLPackage, wordMLPackage.getMainDocumentPart(),
				externalCtm, "word/settings.xml", in);

		// Example of a part which become a rel of the package
		in = new FileInputStream("/home/dev/workspace/docx4j/foreign_parts/docProps/app.xml");
		attachForeignPart(wordMLPackage, wordMLPackage,
				externalCtm, "docProps/app.xml", in);
		
		// Now save it 
		wordMLPackage.save(new java.io.File(System.getProperty("user.dir") + "/out.docx") );
				
		System.out.println("Done.");
				
	}
	
	
	public static void attachForeignPart( WordprocessingMLPackage wordMLPackage, 
			Base attachmentPoint,
			ContentTypeManager foreignCtm, 
			String resolvedPartUri, InputStream is) throws Exception{
		
		
		Part foreignPart = Load.getRawPart(is, foreignCtm,  resolvedPartUri);
		attachmentPoint.addTargetPart(foreignPart);
		// Add content type
		ContentTypeManager packageCtm = wordMLPackage.getContentTypeManager();
		packageCtm.addOverrideContentType(foreignPart.getPartName().getURI(), foreignPart.getContentType());
		
		System.out.println("Attached foreign part: " + resolvedPartUri);
		
	}
	
}
