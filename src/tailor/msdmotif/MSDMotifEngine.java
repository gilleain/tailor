package tailor.msdmotif;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import tailor.datasource.Structure;
import tailor.datasource.StructureSource;
import tailor.description.Description;
import tailor.description.ProteinDescription;
import tailor.engine.Engine;
import tailor.engine.Match;
import tailor.engine.Run;
import tailor.measurement.Measure;
import tailor.measurement.Measurement;

public class MSDMotifEngine implements Engine {
	
	public static String queryURL = "http://www.ebi.ac.uk/msd-srv/msdmotif/hitListXML";
	
	private MSDMotifResultsPrinter printer;
	private PrintStream err;
	
	public MSDMotifEngine(MSDMotifResultsPrinter printer, PrintStream err) {
		this.printer = printer;
		this.err = err;
	}
	
	@Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRun(Run run) {
        // TODO Auto-generated method stub
        
    }

    public void run(Run run) {
        for (Description description : run.getDescriptions()) {
            this.runDescription(description);
        }
	}
	
	public void run(ProteinDescription description, List<Measure<? extends Measurement>> measures) {
		String outfileName = "tmp.out";	//FIXME
		
		String xmlQuery = DescriptionToXmlQueryTranslator.translate(description);
		this.doQueryFromXmlString(xmlQuery, outfileName);
		this.processResult(outfileName);
	}

	@Override
    public List<Match> match(Description description, Structure structure) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void runDescription(Description description) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run(Description description, StructureSource source) {
        // TODO Auto-generated method stub
        
    }

    public void doQueryFromXmlString(String xmlString, String outputFilename) {
		try {
			HttpURLConnection connection = this.getConnection(err);
	    	this.writeXmlStringToConnection(connection, err, xmlString);
	    	this.readDataFromConnection(connection, outputFilename, err);
		} catch (Exception e) {
			this.err.print(e);
		}
	}

	public void doQueryFromFile(String inputFilename, String outputFilename) {
		try {
			HttpURLConnection connection = this.getConnection(err);
	    	this.writeFileToConnection(connection, err, inputFilename);
	    	this.readDataFromConnection(connection, outputFilename, err);
		} catch (Exception e) {
			this.err.print(e);
		}
	}

	public void processResult(String filename) {
		try {
			File file = new File(filename);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, new ResultProcessor(this.printer));
		} catch (SAXException s) {
			System.out.println(s);
		} catch (IOException ioe) {
			System.out.println(ioe);
		} catch (ParserConfigurationException pce) {
			System.out.println(pce);
		}
	}
    
    public void writeFileToConnection(HttpURLConnection connection, PrintStream err, String filename) {
    	try {
    		err.println("Reading xml file...");
    		
    		FileInputStream fio = new FileInputStream(filename);
    		this.writeToConnection(connection, err, fio);
    		
    	} catch (IOException ioe) {
    		err.println(ioe);
    	}
    }
    
    public void writeXmlStringToConnection(HttpURLConnection connection, PrintStream err, String data) {
    	try {
    		err.println("Converting xml string to bytes...");
    		
    		ByteArrayInputStream bio = new ByteArrayInputStream(data.getBytes());
    		this.writeToConnection(connection, err, bio);
    	} catch (IOException ioe) {
    		err.println(ioe);
    	}
    }
    
    public void writeToConnection(HttpURLConnection connection, PrintStream err, InputStream is) throws IOException {
    	err.println("...and writing to connection");
		GZIPOutputStream out = new GZIPOutputStream(connection.getOutputStream());
		
		byte buf[] = new byte[4096];
		int l = 0;
		while ((l = is.read(buf, 0, 4096)) > 0) {
			out.write(buf, 0, l);
		}
		is.close();
		
		out.finish();
		out.flush();
		out.close();
		
		err.println("Finished writing xml data to connection");
    }
    
    public HttpURLConnection getConnection(PrintStream err) throws Exception{
    	URL urlHttp = new URL(queryURL);
    	err.println("Made url object");

    	HttpURLConnection connection = (HttpURLConnection) urlHttp.openConnection();
    	err.println("Opened connection to " + queryURL);

    	connection.setRequestMethod("POST");
    	connection.setDoOutput(true);
    	connection.setRequestProperty("Content-Type", "text/xml");
    	connection.setRequestProperty("Content-Encoding", "gzip");
    	return connection;
    }
    
    public void readDataFromConnection(HttpURLConnection connection, String outputFilename, PrintStream err) throws Exception {
    	String responseMessage = connection.getResponseMessage();
    	err.println("Response message : " + responseMessage);

    	// read data
    	InputStream in = null;
    	OutputStream fileOut = null;
    	
    	try {
    	    in = connection.getInputStream();
            fileOut = new FileOutputStream(new File(outputFilename));
            int l = 0;
            byte[] buf = new byte[4096];
            while ((l = in.read(buf, 0, 4096)) > 0) {
                fileOut.write(buf, 0, l);
            }
    	} finally {
    	    if (in != null) in.close();
    	    if (fileOut != null) fileOut.close();
    	}
    	connection.disconnect();
    }
    
    
    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.err.println("usage:");
            System.err.println("\"java tailor.engine.MSDMotifEngine <queryfile> <outputfile>\"");
            System.err.println("where <queryfile> is any xml file compliant with query.dtd");
            System.err.println("and <outputfile> is where the xml results will be put");
            return;
        }
        
        try {
        	String queryFile = args[0];
        	String outFile = args[1];
        	MSDMotifResultsPrinter out = new MSDMotifStreamResultsPrinter(System.out);
        	MSDMotifEngine engine = new MSDMotifEngine(out, System.err);
        	engine.doQueryFromFile(queryFile, outFile);
        	engine.processResult(outFile);
        } catch(Exception ex) {
        	System.err.println(ex);
        }
    }

}
