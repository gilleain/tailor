package tailor.msdmotif;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


public class ResultProcessor extends DefaultHandler {
	
    private MSDMotifResultsPrinter printer;
    private MSDMotifResult currentResult;
    private String dbResNum;
    private String dbResName;
    private String entityId;
    private boolean inResolution = false;
    private boolean inEntity = false;
    private boolean inSequence = false;
    private boolean inSegmentType = false;

    public ResultProcessor(MSDMotifResultsPrinter printer) {
    	this.printer = printer;
        this.resetFlags();
        this.dbResNum = new String();
        this.dbResName = new String();
        this.entityId = new String();
    }

    public void resetFlags() {
        this.inResolution = false;
        this.inEntity = false;
        this.inSequence = false;
        this.inSegmentType = false;
    }
    
    public boolean isInResolution() {
    	return this.inResolution;
    }
    
    public boolean isInEntity() {
    	return this.inEntity;
    }
    
    public boolean isInSequence() {
    	return this.inSequence;
    }
    
    public boolean isInSegmentType() {
    	return this.inSegmentType;
    }
    
    public void startDocument() throws SAXException {
    }

    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        if (qName.equals("entry")) {
        	this.currentResult = new MSDMotifResult();
            this.currentResult.setPdbId(attrs.getValue("dbAccessionId"));
            this.currentResult.setDate(attrs.getValue("date"));
        } else if (qName.equals("entryDetail")) {
            if (attrs.getValue("property").equals("resolution")) {
                this.inResolution = true;
            }
        } else if (qName.equals("entity")) {
            this.inEntity = true;
            this.entityId = attrs.getValue("entityId");
        } else if (qName.equals("segment")) {
            int indexOfStartAttribute = attrs.getIndex("start");
            if (indexOfStartAttribute != -1) {
                this.currentResult.setChainId(this.entityId);
                this.currentResult.setStart(attrs.getValue(indexOfStartAttribute)); 
                this.currentResult.setEnd(attrs.getValue("end")); 
            }
        } else if (qName.equals("segmentDetail")) {
            if (attrs.getValue("property").equals("sequence")) {
                this.inSequence = true;
            } 

            else if (attrs.getValue("property").equals("type")) {
                this.inSegmentType = true;
            }
        } else if (qName.equals("residue")) {
            this.dbResNum = attrs.getValue("dbResNum");
            this.dbResName = attrs.getValue("dbResName");
        }
    }

    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
        if (qName.equals("entry")) {
        	this.printer.printResult(this.currentResult);
            this.resetFlags();
        }
    }

    public void characters(char[] buf, int offset, int len) throws SAXException {
        if (this.inResolution) {
            this.currentResult.setResolution(new String(buf, offset, len));
            this.inResolution = false;
        } else if (this.inSequence) {
            this.currentResult.setSequence(new String(buf, offset, len));
            this.inSequence = false;
        } else if (this.inSegmentType) {
            if ("ligand".equals(new String(buf, offset, len))) {
                this.currentResult.setLigandName(this.dbResName);
                this.currentResult.setLigandNumber(this.dbResNum);
                this.inSegmentType = false;
            }
        }
    }

}
