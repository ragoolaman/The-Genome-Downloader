package sample;
/**The Genome Downloader
  @author John Berger
 * @version 0.0.4*/
import it.sauronsoftware.ftp4j.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class Main extends Application{

    //Initalize necessary variables
    private static int counter = 0;
    private ResultSet rs;
    private TextField input;
    private List<String> sqlName = new ArrayList<>();
    private List<String> sqlCode = new ArrayList<>();
    private List<String> sqlStrain = new ArrayList<>();
    private ObservableList<String> genomeList = FXCollections.observableArrayList();
    private Label updateText, downText;
    private Button downloadPane,update;
    private Button add,down,remove,back, destination;
    private String availListSel;
    private String genomeListSel;
    private boolean containsQuote;
    private String genomeDownListComp;
    public static String downDir;

    private void downGenomes() {
        //Declare location for JDBC Drivers, sql username and sql password
        String url = "jdbc:mysql://isratosh.net:3306/genbank?useSSL=false";
        String username = "scifair";
        String password = "johnsux";
        //Establish the connection to the SQL Database
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();
            for(String i : genomeList)
            {
                System.out.println(i);
                rs = stmt.executeQuery("SELECT * FROM genbankfile WHERE designation LIKE '" + i + "'");
                while(rs.next())
                {

                    try {
                        FTP.FTPGet(rs.getString(1), rs.getString(3) + "_" + rs.getString(4).trim());
                        System.out.println("Finished Downloading " + i);
                    } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException
                            | FTPDataTransferException | FTPAbortedException e) {
                        e.printStackTrace();
                    }
                }

            }
            connection.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
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
                //System.out.println(rs.getString(1));
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
        System.out.println(genome);
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
                System.out.println("has quote");
                rs = stmt.executeQuery("SELECT * FROM genbankfile WHERE designation LIKE'" + genome.replaceAll("\"", "") + "%'");
            } else {
                System.out.println("has no quote");
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
    /*private static boolean populateSQL(String aSQLScriptFilePath) throws IOException,SQLException {
        //Thread sqlProc = new Thread();
        System.out.println("    Establishing connection with localhost");
        Connection connection = DriverManager.getConnection("jdbc:mysql://isratosh.net:3306/genbank?useSSL=false","scifair","johnsux");
        System.out.println("    Creating blank statement");
        Statement stmt = connection.createStatement();
        boolean isScriptExecuted = false;
        try {
            System.out.println("    Starting BufferedReader");
            BufferedReader in = new BufferedReader(new FileReader(aSQLScriptFilePath));
            String str;
            System.out.println("    Starting StringBuilder");
            StringBuilder sb = new StringBuilder();
            System.out.println("    Starting population while loop");
            while ((str = in.readLine()) != null) sb.append(str);
            in.close();
            stmt.executeUpdate("DROP TABLE genbankfile");
            stmt.executeUpdate("CREATE TABLE genbankfile (designation TEXT, strain TEXT, gcf TEXT, asm TEXT, id MEDIUMINT NOT NULL AUTO_INCREMENT, PRIMARY KEY (id))");
            stmt.executeUpdate(sb.toString());
            isScriptExecuted = true;
        } catch (Exception e) {
            System.err.println("Failed to Execute" + aSQLScriptFilePath +". The error is"+ e.getMessage());
        }
        System.out.println("    Finished populatSQL");
        return isScriptExecuted;
    }*/

    /*private static void processDataFile() throws IOException
    {
        System.out.println("    Initializing FileReader input for genbankRaw.txt");
        FileReader input = new FileReader("C:\\Users\\John\\Documents\\genbankRaw.txt");
        System.out.println("    Initializing BufferedReader bufRead for genbankRaw.txt");
        BufferedReader bufRead = new BufferedReader(input);
        String myLine;
        System.out.println("    Initializing PrintWriter write for genbankFile.txt");
        PrintWriter write = new PrintWriter("C:\\Users\\John\\Documents\\genbankFile.txt", "UTF-8");
        System.out.println("    Initializing FilReader proc for genbankFile.txt");
        FileReader proc = new FileReader("C:\\Users\\John\\Documents\\genbankFile.txt");
        BufferedReader procRead = new BufferedReader(proc);
        System.out.println("    Beginning print while loop");
        while ( (myLine = bufRead.readLine()) != null) {
            String[] array1 = myLine.split("\n");
            // check to make sure you have valid data
            for (String i : array1) {
                String[] array2 = i.split("\t");
                if (!array2[0].contains("#")) {
                    if (array2[17].contains("na")) {
                        write.println(array2[7] + "\t" + array2[8] + "\t" + array2[0] + "\t" + array2[15]);
                    } else {
                        write.println(array2[7] + "\t" + array2[8] + "\t" + array2[17] + "\t" + array2[15]);
                    }
                }
            }
        }
        System.out.println("    Exiting the print while loop");
        write.close();
        proc.close();
        procRead.close();
    }*/

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
        //update = new Button("Update Database");
        //Create label, and set text
        //updateText = new Label("Update Progress:");
        //Add the texfields, set their prompt text, and translate them into position
        input = new TextField();
            input.setPromptText("Search for Genome by Name");
            input.setPrefColumnCount(15);
            input.setTranslateX(-150);
            input.setTranslateY(-640);
        destination = new Button("Select Download Location");
            destination.setTranslateX(190);
            destination.setTranslateY(-290);
        downText = new Label("Download Progress:");
            downText.setTranslateX(200);
            downText.setTranslateY(-290);

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
            //System.out.println("AvailListSel is" + availListSel);
            String[] genomeArray = availListSel.split("\t");
            //System.out.println("GenomeArray holds:" + genomeArray[0] + genomeArray[1]);
            String[] finalGenomeArray = genomeArray[0].split(" ");
            System.out.println(finalGenomeArray.length);
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
            rootNode.getChildren().addAll(genomeView,genomes,add,down,remove,back,input,destination, downText);
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
            rootNode.getChildren().removeAll(genomeView,genomes,add,down,remove,back,input,destination,downText);
            rootNode.getChildren().addAll(downloadPane/*,update,updateText*/);

        });
        //Update button runs FTP.Update(), and attempts to give the user updates on progress. No luck as of yet... Should try Threading next...
        /*update.setOnAction(arg0 -> {
                    updateText.setText("Update in progress...\nBeginning file download...");
                    try {
                        FTP.FTPUpdate();
                    } catch (IllegalStateException | IOException | FTPIllegalReplyException | FTPException
                            | FTPDataTransferException | FTPAbortedException | FTPListParseException e) {
                        e.printStackTrace();
                    }
                    updateText.setText("Update in progress...\nProcessing file...");
                    try {
                        processDataFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    updateText.setText("Update in progress...\nPopulating SQL Database...");
                    try {
                        populateSQL("C:\\Users\\John\\Documents\\sqlForJava.sql");
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                    updateText.setText("Update Progress:\nUpdate Complete");
        });*/
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
        //Add the download action, wherein the FTP Class is informed to Get the genomes in the download queue
        down.setOnAction(ae -> {
            // TODO Auto-generated method stub
            downGenomes();
            downText.setText("Download Progress: \n Download Completed");
        });
        //The destination box will most likely be replaced the something like a JFileChooser to select a directory location instead of typing it in
        //The current function is to tell the FTP Class where to download the genomes to
        destination.setOnAction(arg0 -> {
            // TODO Auto-generated method stub
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("JavaFX Projects");
            File defaultDirectory = new File(System.getProperty("user.dir"));
            chooser.setInitialDirectory(defaultDirectory);
            File selectedDirectory = chooser.showDialog(myStage);
            System.out.println(selectedDirectory.toString());
            downDir = selectedDirectory.toString();
            down.setDisable(false);
        });
    }

}