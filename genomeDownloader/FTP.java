/*This project was developed and created by ragoolaman.
 * Any and all code in these files cannot be claimed as the work of anyone but ragoolaman.*/package genomeDownloader;

import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class FTP {

	static String cwd = System.getProperty("user.dir");
	public static void FTPStart(String genomeCode) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException 
	{
    	FTPClient client = new FTPClient();
    	// firstly connect 
    	client.connect("ftp.ncbi.nlm.nih.gov");
    	// login
    	client.login("anonymous", "abc123");
    	// change directory if you need
    	client.changeDirectory("/genomes/all/" + genomeCode);
    	// list all the files
    	FTPFile[] list = client.list();
    	// you can get the type of file
    	System.out.println(list[0].getType());
    	// you can get the name of file
    	System.out.println(list[0].getName());
    	// you can get the size of file
    	System.out.println(list[0].getSize());
    	// you can get the last modified date of file
    	System.out.println(list[0].getModifiedDate());
    	client.download(genomeCode + "_assembly_report.txt", new java.io.File("C:\\users\\jwber\\Downloads\\Bacillus_Megaterium.txt"));
	}
}