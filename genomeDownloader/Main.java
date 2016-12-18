package sample;

import it.sauronsoftware.ftp4j.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Main extends Application{

    //Initalize necessary variables
    private int counter;
    private ResultSet rs;
    private TextField input;
    private List<String> sqlName = new ArrayList<>();
    private List<String> sqlCode = new ArrayList<>();
    private List<String> sqlStrain = new ArrayList<>();
    private ObservableList<String> genomeList = FXCollections.observableArrayList();
    private Button downloadPane;
    private Button add,down,remove,back, destination;
    private String availListSel;
    private String genomeListSel;
    private boolean containsQuote;
    private String genomeDownListComp;
    public static String downDir;
    private ProgressBar bar;
    private ProgressIndicator ind;
    private Label downLabel;
    private double counter1;
    private double genomeLists;

    public void FTPGet(String genomeName, String genomeCode) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {

        genomeCode = genomeCode.replaceAll("#", "_");
        String dir = Main.downDir;
        FTPClient client = new FTPClient();
        client.connect("ftp.ncbi.nlm.nih.gov");
        client.login("anonymous", "abc123");
        client.changeDirectory("/genomes/all/" + genomeCode);
        String folderNew;
        folderNew = genomeName;
        folderNew = folderNew.replace("/", "_");
        folderNew = folderNew.replace(":", "_");
        //downLabel.setText(genomeName);
        bar.setProgress(0.0);
        ind.setProgress(0.0);
        boolean newFolder = (new File(dir + "\\" + folderNew).mkdir());
        if (!newFolder) {
        }
        client.download(genomeCode + "_assembly_report.txt", new java.io.File(dir + "\\" + genomeName + "\\" + "Assembly Report.txt"));
        bar.setProgress(0.2);
        ind.setProgress(0.2);
        client.download(genomeCode + "_assembly_stats.txt", new java.io.File(dir + "\\" + genomeName + "\\" + "Assembly Stats.txt"));
        bar.setProgress(0.4);
        ind.setProgress(0.4);
        client.download(genomeCode + "_genomic.fna.gz", new java.io.File(dir + "\\" + genomeName + "\\" + "Genomic FNA.gz"));
        bar.setProgress(0.6);
        ind.setProgress(0.6);
        client.download(genomeCode + "_genomic.gbff.gz", new java.io.File(dir + "\\" + genomeName + "\\" + "Genomic GBFF.gz"));
        bar.setProgress(0.8);
        ind.setProgress(0.8);
        client.download(genomeCode + "_protein.faa.gz", new java.io.File(dir + "\\" + genomeName + "\\" + "Protein FAA.gz"));
        bar.setProgress(1.0);
        ind.setProgress(1.0);
        client.disconnect(true);
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("Genomic FNA");
        fileList.add("Genomic GBFF");
        fileList.add("Protein FAA");
        for (String i : fileList) {
            try {
                GZIPInputStream gzipInputStream;
                gzipInputStream = new GZIPInputStream(new FileInputStream(dir + "\\" + genomeName + "\\" + i + ".gz"));
                String outFilename = i + ".txt";
                OutputStream out = new FileOutputStream(dir + "\\" + genomeName + "\\" + outFilename);
                byte[] buf = new byte[1024];
                int len;
                while ((len = gzipInputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                gzipInputStream.close();
                out.close();
                File toDelete = new File(dir + "\\" + genomeName + "\\" + i + ".gz");
                toDelete.delete();
            } catch (IOException e) {
            }
        }
    }
    private void getSQLDesignation()
    {
        sqlName.clear();
        sqlCode.clear();
        sqlStrain.clear();
        String url = "jdbc:mysql://isratosh.net:3306/genbank?useSSL=false";
        String username = "scifair";
        String password = "johnsux";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM genbankfile");
            while(rs.next())
            {
                sqlName.add((rs.getString(1)));
                sqlStrain.add(rs.getString(2));
                sqlCode.add(rs.getString(3) + "_" + rs.getString(4));
            }
            connection.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
    private void sqlSearch(String genome)
    {
        sqlName.clear();
        sqlStrain.clear();
        sqlCode.clear();
        String url = "jdbc:mysql://isratosh.net:3306/genbank?useSSL=false";
        String username = "scifair";
        String password = "johnsux";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();
            if(containsQuote)
            {
                rs = stmt.executeQuery("SELECT * FROM genbankfile WHERE designation LIKE'" + genome.replaceAll("\"", "") + "%'");
            } else {
                rs = stmt.executeQuery("SELECT * FROM genbankfile WHERE designation LIKE'%" + genome + "%'");
            }
            while(rs.next())
            {
                sqlName.add((rs.getString(1)));
                sqlStrain.add(rs.getString(2));
                sqlCode.add((rs.getString(3)) + "_" + (rs.getString(4)));
            }
            connection.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public static void main(String[] args) throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException, SQLException
    {
        launch(args);
    }
    @Override
    public void start(Stage myStage) throws Exception {
        //Create the Stage, and set it's title
        myStage.setTitle("The Genome Downloader");
        //Create and align the rootNode
        FlowPane rootNode = new FlowPane(10,10);
        rootNode.setAlignment(Pos.CENTER);
        //Add the Scene, and set the Stage Scene
        Scene myScene = new Scene(rootNode,1000,700);
        myStage.setScene(myScene);
        //Create the buttons, translate them into correct positions, and set enable/disable
        add = new Button("add");
            add.setTranslateY(-40);
        remove = new Button("remove");
            remove.setTranslateY(70);
            remove.setTranslateX(-144);
        down = new Button("Get Genomes");
            down.setTranslateX(-46);
            down.setDisable(true);
        downloadPane = new Button("Enter Download Mode");
        back = new Button("<-Back To Menu");
            back.setTranslateY(-320);
            back.setTranslateX(-863);
        input = new TextField();
            input.setPromptText("Search for Genome by Name");
            input.setPrefColumnCount(15);
            input.setTranslateX(-145);
            input.setTranslateY(-645);
        destination = new Button("Select Download Location");
            destination.setTranslateX(213);
            destination.setTranslateY(-295);
        bar = new ProgressBar(0);
            bar.setTranslateX(45);
            bar.setTranslateY(-430);
        ind = new ProgressIndicator(0);
            ind.setTranslateX(45);
            ind.setTranslateY(-430);
        downLabel = new Label("");
            downLabel.setTranslateX(150);
            downLabel.setTranslateY(-460);


        //Create the List view that the Download genome view reads from
        ObservableList<String> availableGenomes = FXCollections.observableArrayList();
        ListView<String> genomeView = new ListView<>(availableGenomes);
        genomeView.setPrefSize(300, 600);
        MultipleSelectionModel<String> lvSelModel = genomeView.getSelectionModel();
        lvSelModel.selectedItemProperty().addListener((changed, oldVal, newVal) -> availListSel = newVal);

        //Create the List that the download queue runs off of
        ListView<String> genomes = new ListView<>(genomeList);
        genomes.setPrefSize(300, 600);
        MultipleSelectionModel<String> selectedGenome = genomes.getSelectionModel();
        selectedGenome.selectedItemProperty().addListener((changed, oldValue, newValue) -> genomeListSel = newValue);

        //Add action listener to add the selected genome to the download queue
        add.setOnAction(ae -> {
            String[] genomeArray = availListSel.split("\t");
            String[] finalGenomeArray = genomeArray[0].split(" ");
            switch (finalGenomeArray.length)
            {
                case 1 :
                    genomeDownListComp = finalGenomeArray[0];
                    break;
                case 2 :
                    genomeDownListComp = finalGenomeArray[0] + " " + finalGenomeArray[1];
                    break;
                case 3:
                    genomeDownListComp = finalGenomeArray[0] + " " + finalGenomeArray[1] + " " + finalGenomeArray[2];
                    break;
                case 4:
                    genomeDownListComp = finalGenomeArray[0] + " " + finalGenomeArray[1] + " " + finalGenomeArray[2] + " " + finalGenomeArray[3];
                    break;
                case 5:
                    genomeDownListComp = finalGenomeArray[0] + " " + finalGenomeArray[1] + " " + finalGenomeArray[2] + " " + finalGenomeArray[3] + " " + finalGenomeArray[4];
                    break;
            }
            if(!genomeList.contains(genomeDownListComp)) {
                genomeList.add(genomeDownListComp);
            }
        });
        //Add the remove function for the download Queue
        remove.setOnAction(action -> {
            // TODO Auto-generated method stub
            genomeList.remove(genomeListSel);
        });
        //Add the initial nodes to the root, show the rootNode
        rootNode.getChildren().addAll(downloadPane/*,update,updateText*/);
        myStage.show();

        //This will initialize the download pane, and display all genomes on the left most panel
        downloadPane.setOnAction(arg0 -> {
            counter = 0;
            rootNode.getChildren().removeAll(downloadPane/*,update,updateText*/);
            rootNode.getChildren().addAll(genomeView,genomes,add,down,remove,back,input,destination,downLabel,bar,ind);
            getSQLDesignation();
            for(String i : sqlName)
            {
                availableGenomes.add(i + "\t" + sqlStrain.get(counter));
                counter++;
            }
            Collections.sort(availableGenomes);
        });
        //Back action will return the user to the main page, by adding and removing nodes from the root
        back.setOnAction(arg0 -> {
            // TODO Auto-generated method stub
            rootNode.getChildren().removeAll(genomeView,genomes,add,down,remove,back,input,destination);
            rootNode.getChildren().addAll(downloadPane/*,update,updateText*/);

        });
        //The input text field will detect wether the text contains a quotation mark, if it does, the input is taken literally, instead of take figuratively
        input.setOnAction(arg0 -> {
            if (input.getText().contains("\"")) containsQuote = true;
            else containsQuote = false;
            sqlSearch(input.getText());
            availableGenomes.clear();
            counter = 0;
                for(String o : sqlName)
                {
                    availableGenomes.add(o + "\t" + sqlStrain.get(counter));
                    counter++;
                }
                Collections.sort(availableGenomes);
        });
        //Add the download action, wherein the FTP method is informed to Get the genomes in the download queue
        down.setOnAction((ActionEvent ae) -> {
            // TODO Auto-generated method stub
            Task task = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    //Declare location for JDBC Drivers, sql username and sql password
                    String url = "jdbc:mysql://isratosh.net:3306/genbank?useSSL=false";
                    String username = "scifair";
                    String password = "johnsux";
                    //Establish the connection to the SQL Database
                    try (Connection connection = DriverManager.getConnection(url, username, password)) {
                        Statement stmt = connection.createStatement();
                        for (String i : genomeList) {
                            rs = stmt.executeQuery("SELECT * FROM genbankfile WHERE designation LIKE '" + i + "'");
                            while (rs.next())
                                try {
                                    Platform.runLater(() -> {
                                        try {
                                            downLabel.setText(rs.getString(1));
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    FTPGet(rs.getString(1), rs.getString(3) + "_" + rs.getString(4).trim());/*).start();*/
                                    counter1++;
                                    genomeLists = genomeList.size();
                                    bar.setProgress(counter1 / genomeLists);
                                    System.out.println(counter1 / genomeLists);
                                    ind.setProgress(counter1 / genomeLists);
                                } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException
                                        | FTPDataTransferException | FTPAbortedException | FTPListParseException e) {
                                    e.printStackTrace();
                                }
                        }
                        connection.close();
                        stmt.close();
                        rs.close();
                        Platform.runLater(() -> downLabel.setText(""));
                    } catch (SQLException e) {
                        throw new IllegalStateException("Cannot connect the database!", e);
                    }
                    return null;
                }
            };
            Thread th = new Thread(task);
            th.start();
        });
        //The current function is to tell the FTP method where to download the genomes to
        destination.setOnAction(arg0 -> {
            // TODO Auto-generated method stub
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Download Location");
            File defaultDirectory = new File(System.getProperty("user.dir"));
            chooser.setInitialDirectory(defaultDirectory);
            File selectedDirectory = chooser.showDialog(myStage);
            downDir = selectedDirectory.toString();
            down.setDisable(false);
        });
    }

}