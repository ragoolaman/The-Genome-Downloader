package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**The Genome Downloader
 * @author John Berger */

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class FTP {

    static String cwd = System.getProperty("user.dir");

    public static void FTPUpdate() throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException
    {
        System.out.println("    Initializing connection");
        FTPClient client = new FTPClient();
        client.connect("ftp.ncbi.nlm.nih.gov");
        client.login("anonymous", "abc123");
        client.changeDirectory("/genomes/genbank/");
        //FTPFile[] list = client.list();
        //System.out.println(list[2].getType());
        //System.out.println("Name: " + list[2].getName());
        //System.out.println("Size: " + list[2].getSize());
        //System.out.println("Last Modified: " + list[2].getModifiedDate());
        System.out.println("    Starting Download");
        client.download("assembly_summary_genbank.txt", new java.io.File("C:\\Users\\John\\Documents\\genbankRaw.txt"));
    }
    public static void FTPGet(String genomeName, String genomeCode) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
    {
        cwd = Main.directory;
        FTPClient client = new FTPClient();
        client.connect("ftp.ncbi.nlm.nih.gov");
        client.login("anonymous", "abc123");
        client.changeDirectory("/genomes/all/" + genomeCode);
        boolean newFolder = (new File(cwd + "\\" + genomeName).mkdir());
        if(!newFolder)
        {
            System.out.println("File creation failure.");
        }
        System.out.println("entering download phase");
        client.download(genomeCode + "_assembly_report.txt", new java.io.File(cwd + "\\" + genomeName + "\\" + "Assembly Report.txt"));
        client.download(genomeCode + "_assembly_stats.txt", new java.io.File(cwd + "\\" + genomeName + "\\" + "Assembly Stats.txt"));
        client.download(genomeCode + "_genomic.fna.gz", new java.io.File(cwd + "\\" + genomeName + "\\" + "Genomic FNA.gz"));
        client.download(genomeCode + "_protein.faa.gz", new java.io.File(cwd + "\\" + genomeName + "\\" + "Protein FAA.gz"));
        client.download(genomeCode + "_genomic.gbff.gz", new java.io.File(cwd + "\\" + genomeName + "\\" + "Genomic GBFF.gz"));
        client.disconnect(true);
        String[] fileList = {"Genomic FNA","Protein FAA","Genomic GBFF"};
        for(String i : fileList) {
            try{
                GZIPInputStream gzipInputStream;
                gzipInputStream = new GZIPInputStream(new FileInputStream(cwd + "\\" + genomeName + "\\" + i + ".gz"));
                String outFilename = i + ".txt";
                OutputStream out = new FileOutputStream(cwd + "\\" + genomeName + "\\" + outFilename);
                byte[] buf = new byte[1024];  //size can be changed according to programmer's need.
                int len;
                while ((len = gzipInputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                gzipInputStream.close();
                out.close();
                File toDelete = new File(cwd + "\\" + genomeName + "\\" + i + ".gz");
                toDelete.delete();
            }
            catch(IOException e){
                System.out.println("Exception has been thrown" + e);
            }
        }
    }
    public static void FTPFile() throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
    {
        FTPClient client = new FTPClient();
        client.connect("ftp.ncbi.nlm.nih.gov");
        client.login("anonymous", "abc123");
        client.changeDirectory("/genomes/all/GCF_000334875.1_ASM33487v1");
    }
    public static void checkDataFile() throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException
    {
        FTPClient client = new FTPClient();
        client.connect("ftp.ncbi.nlm.nih.gov");
        client.login("anonymous", "abc123");
        client.changeDirectory("/genomes/genkbank");
        FTPFile[] list = client.list();
        System.out.println(list[2]);
        System.out.println("Name: " + list[2].getName());
        System.out.println("Size: " + list[2].getSize());
        System.out.println("Last Modified: " + list[2].getModifiedDate());
        Date myDate = new Date();
        System.out.println(myDate);
        System.out.println(new SimpleDateFormat("MM-dd-yyyy").format(myDate));
    }
}