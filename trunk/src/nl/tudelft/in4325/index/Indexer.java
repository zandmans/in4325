package nl.tudelft.in4325.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.tudelft.in4325.main.Main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Indexer
{
	
	private Hashtable<String, Vector<String>> index;

	public Indexer()
	{
		index = new Hashtable<String, Vector<String>>();
	}
	
	public void init() throws FileNotFoundException
	{
		Vector<String> values;
		File dir = new File("src/docs");
		String[] children = dir.list();
		if(children != null)
		{
			int i = 0;
			for(String filename : children)
			{
				if(filename.charAt(0) != '.')
				{
					i++;
					Scanner sc = new Scanner(new File("src/docs/" + filename));
					
					while(sc.hasNext())
					{
						String key = Main.formatString(sc.next());
						//System.out.println(filename + ": " + key);
						if(index.containsKey(key))
						{
							values = index.get(key);
							if(!values.contains(filename))
								values.add(filename);
							index.put(key, values);
						}
						else
						{
							values = new Vector<String>();
							values.add(filename);
							index.put(key, values);
						}	
					}
				}
			}
		}
	}
	
	public void indexData()
	{
		try
		{
			File xmlfile = new File("src/reuters/reut2-000.sgm");
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlfile);
			doc.getDocumentElement().normalize();
			
			NodeList list = doc.getElementsByTagName("REUTERS");
			for(int i = 0; i < list.getLength(); i++)
			{
				Node node = list.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element elMain = (Element)node;
					
					NodeList listText = elMain.getElementsByTagName("TEXT");
					
					Element elTitle = (Element)listText.item(0);
					NodeList listTitle = elTitle.getElementsByTagName("TITLE");
					Element elTitleValue = (Element)listTitle.item(0);
					NodeList listTitleValue = elTitleValue.getChildNodes();
					System.out.println("title: " + ((Node) listTitleValue.item(0)).getNodeValue());
					
					//Element elBody = (Element)listText.item(1);
					//NodeList listBody = elBody.getElementsByTagName("BODY");
				}
			}
		}
		catch(Exception ex)
		{
			System.err.println(ex);
		}
	}
	
	public Hashtable<String, Vector<String>> getIndex()
	{
		return index;
	}
	
}
