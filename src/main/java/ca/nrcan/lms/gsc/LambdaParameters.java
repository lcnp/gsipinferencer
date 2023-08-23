package ca.nrcan.lms.gsc;

public class LambdaParameters {
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	public String getInputFolder() {
		return inputFolder;
	}
	public void setInputFolder(String inputFolder) {
		this.inputFolder = inputFolder;
	}
	public String getOutputFolder() {
		return outputFolder;
	}
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	private String bucket;
	private String inputFolder;
	private String outputFolder;

}
