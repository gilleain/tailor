package tailor.msdmotif;

public class MSDMotifResult {
	
	private String pdbId;
	private String date;
	private String resolution;
	private String chainId;
	private String start;
	private String end;
	private String sequence;
	private String ligandName;
	private String ligandNumber;
	
	public MSDMotifResult() {
		this.pdbId = "";
		this.date = "";
		this.resolution = "";
		this.chainId = "";
		this.start = "";
		this.end = "";
		this.sequence = "";
		this.ligandName = "";
		this.ligandNumber = "";
	}
	
	public String get(int i) {
		switch(i) {
			case 0 : return this.pdbId;
			case 1 : return this.chainId;
			case 2 : return this.resolution;
			case 3 : return this.date;
			case 4 : return this.start;
			case 5 : return this.end;
			case 6 : return this.sequence;
			case 7 : return this.ligandName;
			case 8 : return this.ligandNumber;
			default : return "";
		}
	}
	
	public static String getFieldNameByNumber(int i) {
		switch(i) {
			case 0 : return "pdbId";
			case 1 : return "chainId";
			case 2 : return "resolution";
			case 3 : return "date";
			case 4 : return "start";
			case 5 : return "end";
			case 6 : return "sequence";
			case 7 : return "ligName";
			case 8 : return "ligNumber";
			default: return "";
		}
	}

	/**
	 * @return the chainId
	 */
	public String getChainId() {
		return this.chainId;
	}

	/**
	 * @param chainId the chainId to set
	 */
	public void setChainId(String chainId) {
		this.chainId = chainId;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return this.date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return this.end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * @return the ligandName
	 */
	public String getLigandName() {
		return this.ligandName;
	}

	/**
	 * @param ligandName the ligandName to set
	 */
	public void setLigandName(String ligandName) {
		this.ligandName = ligandName;
	}

	/**
	 * @return the ligandNumber
	 */
	public String getLigandNumber() {
		return this.ligandNumber;
	}

	/**
	 * @param ligandNumber the ligandNumber to set
	 */
	public void setLigandNumber(String ligandNumber) {
		this.ligandNumber = ligandNumber;
	}

	/**
	 * @return the pdbId
	 */
	public String getPdbId() {
		return this.pdbId;
	}

	/**
	 * @param pdbId the pdbId to set
	 */
	public void setPdbId(String pdbId) {
		this.pdbId = pdbId;
	}

	/**
	 * @return the resolution
	 */
	public String getResolution() {
		return this.resolution;
	}

	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return this.sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return this.start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}
	
	public String toString() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", this.pdbId, this.chainId, this.resolution, this.date, this.start, this.end, this.sequence, this.ligandName, this.ligandNumber);
	}
}
