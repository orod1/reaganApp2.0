package com.example.oscar_.reaganapp;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Bitmap;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.io.File;
import java.io.IOException;


import android.graphics.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;



public class mapActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        Button buttonBack = findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(mapActivity.this);

        Button buttonMap = findViewById(R.id.btnMap);
        buttonBack.setOnClickListener(mapActivity.this);
    }

    TextView starting;
    TextView ending;
    String[] classrooms = {"FL110", "CA106", "CA107", "CA108", "CA109", "MDR"};



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnBack:
                startActivity(new Intent(mapActivity.this, MainActivity.class));
                break;

            case R.id.btnMap:
                boolean start = false;
                boolean end = false;
                starting = (TextView)findViewById(R.id.startClassroom);
                ending = (TextView)findViewById(R.id.endClassroom);
                for (int i = 0; i < starting.length(); i++) {
                    if(starting.equals(classrooms[i]))
                        start = true;
                    if(ending.equals(classrooms[i]))
                        end = true;
                }
                if(start && end){
                    try {
                        MappingMethods.mappingMain(starting.getText().toString(), ending.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                startActivity(new Intent(mapActivity.this, MainActivity.class));
                break;

        }
    }
}


class MappingMethods {

    public static void mappingMain(String start, String end) throws Exception {
        UnitTest Nevada = new UnitTest();
        if(Nevada.runAllTests()){
            return;
        }
        ArrayList<NodeTEST> Route = new ArrayList<NodeTEST>();
        Cluster Grid2 = new Cluster("C:\\Users\\oscar_\\AndroidStudioProjects\\reaganApp\\app\\src\\main\\res\\drawable");
        Route = Grid2.routeAstar(Grid2.getNode(start), Grid2.getNode(end));

        Grid2.drawRoute(Route);
    }

}


/**
 *
 * @author dhuant
 */

class Cluster extends ArrayList<NodeTEST>{

    private final Comparator<NodeTEST> c = new Comparator<NodeTEST>() {
        @Override
        public int compare(NodeTEST i, NodeTEST j) {
            //calculate f(n) = g(n) + h(n)
            //should make it so edge with lowest cost is chosen
            //shouldn't have to directly compare Nodes, just Edges (no g ohterwise)
            //include ISDESTINATION!!!
            int fi = i.f();
            int fj = j.f();
            if (fi > fj) {
                return 1;
            } else if (fj > fi) {
                return -1;
            } else {
                String namei = i.getRoomName();
                String namej = j.getRoomName();
                return 0;
            }
        }
    };
    PriorityQueue<NodeTEST> OPEN = new PriorityQueue<>(c);
    HashSet<NodeTEST> CLOSED = new HashSet<>();
    Predicate<NodeTEST> inCLOSED = new Predicate<NodeTEST>() {
        @Override
        public boolean test(NodeTEST N) {
            return CLOSED.contains(N);
        }
    };
    Predicate<NodeTEST> isClassroom = new Predicate<NodeTEST>(){
        @Override
        public boolean test(NodeTEST N){
            return N.isClassroom();
        }
    };
    private File F;
    private Bitmap Map;
    //0, 1 -> CN214


    public Cluster(){

    }

    public Cluster(NodeTEST a){
        this.add(a);
    }

    public Cluster(Collection<NodeTEST> a){
        this.addAll(a);
    }

    public Cluster(String fileLoc) throws Exception{
        this.generate(fileLoc);
    }

    public Cluster(Bitmap img) throws Exception{
        this.generate(img);
    }

    public void print(ArrayList<NodeTEST> Route){
        System.out.println("Begin:");
        for (int i = 0; i < Route.size(); i++) {
            System.out.println("[" + Route.get(i).getRoomName() + "]");
        }
        //Cole code for end node
        System.out.println("End:");
    }

    public String routeString(ArrayList<NodeTEST> Route){
        String A = "Begin:\n";
        for (int i = 0; i < Route.size(); i++) {
            A = A.concat("[" + Route.get(i).getRoomName() + "]");
        }
        //Cole code for end node*****
        A = A.concat("\nEnd:\n");
        int cc = Route.size() - 1;
        A = A.concat("[" + Route.get(cc).getRoomName() + "]");
        return A;
    }

    public void Astar (NodeTEST Best, NodeTEST Dest){
        if (Best.equals(Dest)){
            CLOSED.add(Best);
            return;
        }
        CLOSED.add(Best);
        openViable(Best, Dest);
//        if (Multiple ties) {
//            while   still a tie
//            Astar(Open.poll(), Dest)
//        }
        Astar(OPEN.poll(), Dest);
    }

    public void AstarB1 (NodeTEST Best, NodeTEST Dest){
        CLOSED.add(Best);
        openViable(Best, Dest);
    }

    public void AstarBORING (NodeTEST Best, NodeTEST Dest){
        int counter = 0;
        System.out.println(Dest.getRoomName());
        AstarB1(Best, Dest);
        System.out.println(OPEN.size());
        while(!OPEN.peek().equals(Dest)) {
            counter++;
            if (OPEN.size() == 1) {
                AstarB1(OPEN.poll(), Dest);
                continue;
            }
            NodeTEST N = OPEN.poll();
            if (OPEN.size() > 0 && N.f() == OPEN.peek().f()) {
                tieBreaker(N, Dest);
            } else {
                AstarB1(N, Dest);
            }
            if (counter > 500) {
                Dest.setParent(N);
                return;
            }
        }
        CLOSED.add(OPEN.poll());

    }

    public ArrayList<NodeTEST> routeAstar (NodeTEST Start, NodeTEST Dest){
        Start.setOrigin(true);
        Start.setParent(Start);
        Start.updateg();
        Dest.seth(Dest);
        AstarBORING(Start, Dest);
        ArrayList<NodeTEST> Retable = new ArrayList<NodeTEST>();
        Retable.add(Dest);
        while(!Retable.contains(Start)) {
            NodeTEST Last = Retable.get(0);
            Retable.add(0, Last.getParent());
        }
        reset();
        return Retable;
    }

    public void tieBreaker(NodeTEST First, NodeTEST Dest) {
        NodeTEST Second = OPEN.poll();
        boolean flagf = First.getNeighborNodes().contains(Dest);
        boolean flags = Second.getNeighborNodes().contains(Dest);
        if (flagf ^ flags) {
            if (flagf) {
                AstarB1(First, Dest);
            } else {
                AstarB1(Second, Dest);
            }
        } else if (flagf && flags) {
            AstarB1(First, Dest);
        } else if (!flagf && !flags) {
            AstarB1(First, Dest);
            AstarB1(Second, Dest);
        }
    }

    public void openViable(NodeTEST Curr, NodeTEST Dest) {
        ArrayList<NodeTEST> ApplicableNeighbors = Curr.getNeighborNodes();
        //System.out.println("Curr is " + Curr.getRoomName());
        //System.out.println(Curr.getNeighbors().size());
        ApplicableNeighbors.removeIf(inCLOSED);
        for (int i = 0; i < ApplicableNeighbors.size(); i++) {
            NodeTEST N = ApplicableNeighbors.get(i);
            if ((N.isWormhole() || N.isClassroom()) && !N.equals(Dest)) {//not checked with different floors
                ApplicableNeighbors.remove(i);
                i--;
            }
        }
        for (NodeTEST N : ApplicableNeighbors) {
            //N.updateg();
            if (OPEN.contains(N)) {
                if (N.g(Curr) <= N.g()) {
                    OPEN.remove(N);
                } else if (N.g(Curr) > N.g()) {
                    continue;
                }
            }
            N.setParent(Curr);
            N.updateg();
            N.seth(Dest);
            OPEN.add(N);
        }
    }

    public void connect(NodeTEST A, NodeTEST B){
        if (!A.getNeighborNodes().contains(B) && !B.getNeighborNodes().contains(A)) {
            int distance = (int) Math.round(Math.hypot(B.x() - A.x(), B.y() - A.y()));
            A.getNeighbors().add(new Edge(B, distance));
            B.getNeighbors().add(new Edge(A, distance));
        } else if (!A.getNeighborNodes().contains(B) && B.getNeighborNodes().contains(A)) {
            System.out.println("Yikes");
        } else if (A.getNeighborNodes().contains(B) && !B.getNeighborNodes().contains(A)) {
            System.out.println("Yikes (Pt. 2)");
        }
        //Lazy error control is ok for early implementation
    }

    public void connectAdjacent(NodeTEST N, NodeTEST[][] tempGrid, int i, int j){
        for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                try {
                    if (!N.equals(tempGrid[k + i][j + l])) {
                        this.connect(N, tempGrid[k + i][j + l]);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public void reset() {
        this.forEach((N) -> N.setOrigin(false));
        OPEN = new PriorityQueue<>(c);
        CLOSED = new HashSet<>();
    }

    public NodeTEST closest(double x, double y){
        double min = Double.MAX_VALUE;
        double dist;
        NodeTEST retable = this.get(0);
        for (int i = 0; i < this.size(); i++) {
            NodeTEST N = this.get(i);
            dist = (Math.abs(N.x() - x)+Math.abs(N.y() - y));
            if (dist < min) {
                min = dist;
                retable = N;
            }
        }
        return retable;
    }

    public double distance(NodeTEST A, NodeTEST B){
        return Math.hypot(Math.abs(A.x() - B.x()), Math.abs(A.y() - B.y()));
    }

    public void closestStairs() {
        ArrayList<NodeTEST> Parsable = new ArrayList<NodeTEST>();
        for (NodeTEST N : this) {
            if (N.isWormhole()) {
                Parsable.add(N);
            }
        }
        int currMin = Integer.MAX_VALUE;
        //accumulation of distance
    }

    private Bitmap generateBuferredImage (String fileLoc) throws Exception {
        Bitmap img = null;
        File f = null;

        //read image

            f = new File(fileLoc);
            for (File map : f.listFiles()) {

                img = BitmapFactory.decodeFile("C:\\Users\\oscar_\\AndroidStudioProjects\\reaganApp\\app\\src\\main\\res\\drawable\\map");
                this.setF(map);
            }

        this.setMap(img);
        return img;
    }

    public NodeTEST[][] generateNodeArray (Bitmap img) throws Exception{ //
        String[][] classLocation = new String[26][17];
        classLocation[9][0] = "FL110";
        classLocation[3][1] = "CA106";
        classLocation[4][1] = "CA107";
        classLocation[5][1] = "CA108";
        classLocation[6][1] = "CA109";
        classLocation[7][1] = "MDR";
        classLocation[9][2] = "FL111";
        classLocation[5][3] = "CA105";
        classLocation[6][3] = "STORAGE";
        classLocation[7][3] = "WDR";
        classLocation[14][3] = "LIBRARY";
        classLocation[21][3] = "GA122";
        classLocation[23][3] = "GA123";
        classLocation[9][4] = "FL112";
        classLocation[0][5] = "COUNSELOR";
        classLocation[11][5] = "IND114";
        classLocation[21][5] = "GA121";
        classLocation[23][5] = "CTDO";
        classLocation[15][6] = "STORE";
        classLocation[4][7] = "IND101";
        classLocation[5][7] = "IND102";
        classLocation[6][7] = "IND103";
        classLocation[7][7] = "IND104";
        classLocation[9][7] = "FL113";
        classLocation[17][7] = "IND116";
        classLocation[19][7] = "IND118";
        classLocation[1][9] = "OFFICE";
        classLocation[3][9] = "NURSE";
        classLocation[11][9] = "ACR";
        classLocation[12][9] = "AO";
        classLocation[15][9] = "IND115";
        classLocation[17][9] = "SOED";
        classLocation[18][9] = "IND117";
        classLocation[19][9] = "IND117B";
        classLocation[20][9] = "IND119";
        classLocation[21][9] = "IND120";
        classLocation[24][9] = "CUSTODIAN";
        classLocation[25][9] = "IND124";
        classLocation[5][10] = "VA143";
        classLocation[6][10] = "VA141";
        classLocation[7][10] = "VA139";
        classLocation[9][10] = "VA137";
        classLocation[3][11] = "BACKHALL";
        classLocation[3][12] = "TX145";
        classLocation[5][12] = "VA144";
        classLocation[6][12] = "VA142";
        classLocation[7][12] = "VA140";
        classLocation[9][12] = "VA138";
        classLocation[11][12] = "CLERKCOPY";
        classLocation[17][12] = "LA135";
        classLocation[22][12] = "HI125";
        classLocation[24][12] = "HI126";
        classLocation[11][13] = "LA135";
        classLocation[3][14] = "TX146";
        classLocation[5][14] = "OK148";
        classLocation[6][14] = "OK150";
        classLocation[7][14] = "OK152";
        classLocation[9][14] = "OK154";
        classLocation[11][14] = "NM156";
        classLocation[15][14] = "LA134";
        classLocation[17][14] = "KY132";
        classLocation[18][14] = "KY130";
        classLocation[21][14] = "KY128";
        classLocation[3][15] = "TX147";
        classLocation[11][15] = "NM155";
        classLocation[15][15] = "LA133";
        classLocation[24][15] = "HI127";
        classLocation[5][16] = "OK149";
        classLocation[6][16] = "OK151";
        classLocation[7][16] = "OK153";
        classLocation[18][16] = "STUCO";
        classLocation[19][16] = "KY131";
        classLocation[20][16] = "KYSTORAGE";
        classLocation[21][16] = "KY129";


        int rgb;
        int a;
        int r;
        int g;
        int b;
        int height = img.getHeight();
        int width = img.getWidth();
        //System.out.println("Height is " + height + "\nWidth is " + width);
        boolean started = false;
        boolean ended = false;
        NodeTEST[][] retable = new NodeTEST[width][height];
        //Cole new code for end node
        //these lines define beginning and end of each step
        //need to write code to define overall beginning and end


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rgb = img.getPixel(i, j);
                a = (rgb>>24) & 0xff;
                r = (rgb>>16) & 0xff;
                g = (rgb>>8) & 0xff;
                b = rgb & 0xff;
                if ((r + g + b) == (255*3)) { //white hallway
                    retable[i][j] = new NodeTEST("TNLA (" + i + "," + j + ")"/*collection.(i,j)*/, i * 10, j * 10);
                }
                else if ((b == 0) && (g == 100) && (r == 100)){ // yellow staircase
                    retable[i][j] = new NodeTEST("Staircase At (" + i + "," + j + ")", i * 10, j * 10, false, true);
                    started = true;
                }
                else if (((r + g) == 0) && (b == 255)){ // blue classroom
                    try {
                        if(classLocation[i][j] != null){
                            retable[i][j] = new NodeTEST(classLocation[i][j], i * 10, j * 10, true, 'a');
                        }
                        started = true;
                    } catch (Exception e) {
                    }
                }
            }
        }

        return retable;
    }

    private ArrayList<NodeTEST> generateMap (NodeTEST[][] tempGrid) throws Exception{
        ArrayList<NodeTEST> retable = new ArrayList<>();
        boolean hit = false;
        for (int i = 0; i < tempGrid.length; i++) {
            for (int j = 0; j < tempGrid[i].length; j++) {
                NodeTEST N = tempGrid[i][j];
                if (N == null) {
                    continue;
                }
                this.connectAdjacent(N, tempGrid, i, j);
                retable.add(N);
                hit = true;
            }
        }
        if (hit) {
            return retable;
        } else {
            throw new Exception("generateMap couldn't make a map");
        }
    }

    private void generate(Bitmap img) throws Exception {
        this.addAll(generateMap(generateNodeArray(img)));
    }

    private void generate(String fileLoc) throws Exception {
        this.addAll(generateMap(generateNodeArray(generateBuferredImage(fileLoc))));
    }

    public NodeTEST getStart(){
        for (NodeTEST N : this) {
            if (N.getRoomName() == "Start") {
                return N;
            }
        }
        return null;
    }

    public NodeTEST getDest(){
        for (NodeTEST N : this) {
            if (N.getRoomName() == "Dest") {
                return N;
            }
        }
        return null;
    }

    /**
     * @return the F
     */
    public File getF() {
        return F;
    }

    /**
     * @param F the F to set
     */
    public void setF(File F) {
        this.F = F;
    }

    /**
     * @return the Map
     */
    public Bitmap getMap() {
        return Map;
    }

    /**
     * @param Map the Map to set
     */
    public void setMap(Bitmap Map) {
        this.Map = Map;
    }

    public void drawRoute(ArrayList<NodeTEST> Route) throws IOException{
        for (NodeTEST N : Route) {
            if (N.getRoomName() == "Dest") {
                break;
            }
            int a = 255;
            int r = 255;
            int g = 175;
            int b = 0;
            int p = (a<<24) | (r<<16) | (g<<8) | b;
            if (N.x()/10 == 1) {
                if (N.y()/10 == 2) {
                    //Start isn't included in Route (PERHAPS) and so it's xy isnt correct but its never come up
                    //EXCEPT When drawing the route when the ghost pixel is actual where Start preports to be
                    System.out.println("What is your problem " + N.getRoomName().hashCode());
                    System.out.println("What is your problem " + this.getStart().getRoomName().hashCode());
                }
            }
            Map.setPixel(N.x()/10, N.y()/10, p);
        }
        File drawn = new File("C:\\Users\\oscar_\\AndroidStudioProjects\\reaganApp\\app\\src\\main\\res\\drawable" + F.getName().replaceAll(".png", "") + "DrawnRoute.png");
        //File drawn = new File("C:\\Users\\cnewby5283\\Documents\\NetBeansProjects\\AStarRouting\\maps\\drawnMaps\\" + F.getName().replaceAll(".png", "") + "DrawnRoute.png");


        //ImageIO.write(Map, "png", drawn);

    }

    public NodeTEST getNode(String desiredStartRoom){
        NodeTEST A =  new NodeTEST("Empty Room", 1, 1);;
        for (NodeTEST N : this) {
            if (N.getRoomName().equals(desiredStartRoom)) {
                A = N;
            }
        }
        return A;
    }

}

class Edge {

    private final int g;
    private final NodeTEST connection;
    private boolean parent;


    public Edge(NodeTEST connection, int g) {
        this.connection = connection;
        this.g = g;
    }

    /**
     * @return the g
     */
    public int g() {
        return g;
    }

    /**
     * @return the connection
     */
    public NodeTEST getConnection() {
        return connection;
    }

    /**
     * @return the parent
     */
    public boolean isParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(boolean parent) {
        this.parent = parent;
    }

}



/**
 *
 * @author dhuant
 */
class LayeredCluster extends ArrayList<Cluster>{
    public LayeredCluster() {

    }

    public LayeredCluster(List<Cluster> Areas) {
        this.addAll(Areas);
    }

    public ArrayList<NodeTEST> Route(NodeTEST A, NodeTEST B) throws Exception {
        //find the floor they're on -> save index of Cluster, do not generalize
        ArrayList<NodeTEST> ARoute = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> BRoute = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> Retable = new ArrayList<NodeTEST>();
        Cluster A_Layer = new Cluster();
        Cluster B_Layer = new Cluster();
        for (Cluster Layer : this) {
            if (Layer.contains(A)) {
                A_Layer = Layer;
            } else if (Layer.contains(B)) {
                B_Layer = Layer;
            }
        }
        if (A_Layer == B_Layer) {
            return A_Layer.routeAstar(A, B);
        } else {
            NodeTEST commonPoint = closestWormhole(A_Layer, B_Layer, A, B);
            ARoute = A_Layer.routeAstar(A, commonPoint);
            BRoute = B_Layer.routeAstar(commonPoint, B);
            B_Layer.remove(0);
            ARoute.addAll(B_Layer);
            return ARoute;
        }
        //thx1138
    }

    /**
     * @param A the Cluster of Origin
     * @param B the Cluster of Destination
     * @param Origin the starting NodeTEST, located in A
     * @param Destination the ending NodeTEST, located in B
     */
    public NodeTEST closestWormhole(Cluster A, Cluster B, NodeTEST Origin, NodeTEST Destination) throws Exception {
        ArrayList<NodeTEST> Wormholes = new ArrayList<>();
        if (A.size() >= B.size()) {
            for (NodeTEST N : B) {
                if (N.isWormhole() && A.contains(N)) {
                    Wormholes.add(N);
                }
            }
        } else {
            for (NodeTEST N : A) {
                if (N.isWormhole() && B.contains(N)) {
                    Wormholes.add(N);
                }
            }
        }

        if (Wormholes.isEmpty()) {
            throw new Exception("Wormholes was empty... look into this");
        }

        //accumulation
        double currMin = Integer.MAX_VALUE;
        double currVal;
        NodeTEST Best = new NodeTEST(-1, -1);
        for (NodeTEST W : Wormholes) {
            currVal = A.distance(W, Origin) + B.distance(W, Destination);
            if (currVal < currMin) {
                currMin = currVal;
                Best = W;
            }
        }
        if (Best.x() == -1 && Best.y() == -1) {
            throw new Exception("The two Areas do not contain a wormhole in common");
        }
        return Best;
    }
}



class NodeTEST {

    private int x;
    private int y;
    private int g;
    private int h;
    private boolean origin;
    private boolean classroom;
    private boolean wormhole;
    private String RoomName;
    private String ClassName;
    private NodeTEST Parent;
    private final Comparator<Edge> c = new Comparator<Edge>() {
        @Override
        public int compare(Edge i, Edge j) {
            //calculate f(n) = g(n) + h(n)
            //should make it so edge with lowest cost is chosen
            //shouldn't have to directly compare Nodes, just Edges (no g ohterwise)
            //include ISDESTINATION!!!
            int fi = i.g();
            int fj = j.g();
//            if (!(i.isParent() && j.isParent()) && (i.isParent() || j.isParent())) {
//                if (i.isParent()) {
//                    return -1;
//                } else if (j.isParent()) {
//                    return 1;
//                }
//            }
            if (fi > fj) {
                return 1;
            } else if (fj > fi) {
                return -1;
            } else {
                return 0;
            }
        }
    };
    private PriorityQueue<Edge> Neighbors = new PriorityQueue<>(c);

    public NodeTEST(String RoomName, String ClassName, int x, int y, int h, ArrayList<Edge> Neighbors, boolean destination) {
        this.RoomName = RoomName;
        this.ClassName = ClassName;
        this.x = x;
        this.y = y;
        this.h = h;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
        this.origin = destination;
    }

    public NodeTEST(String RoomName, int x, int y, ArrayList<Edge> Neighbors, boolean destination) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
        this.origin = destination;
    }

    public NodeTEST(String RoomName, int x, int y, boolean destination) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
    }

    //MAIN NODE CONSTRUCTOR
    public NodeTEST(String RoomName, int x, int y, boolean destination, boolean wormhole) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
        this.wormhole = wormhole;
    }

    //MAIN NODE CONSTRUCTOR CLASSROOMS
    public NodeTEST(String RoomName, int x, int y) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = false;
    }

    public NodeTEST(String RoomName, int x, int y, boolean classroom, char doesNothing) {
        this.RoomName = RoomName;
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = false;
        this.classroom = classroom;
    }



    public NodeTEST(int x, int y, boolean destination, ArrayList<Edge> Neighbors) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
    }

    public NodeTEST(int x, int y, ArrayList<Edge> Neighbors) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = origin;
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
    }

    public NodeTEST(int x, int y, boolean destination) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = destination;
    }

    public NodeTEST(int x, int y) {
        this.RoomName = "Bucharest";
        this.x = x;
        this.y = y;
        this.h = Integer.MAX_VALUE;
        this.origin = false;
    }

    public NodeTEST(NodeTEST N) {
        this.x = N.x();
        this.y = N.y();
        this.g = N.g();
        this.h = N.h();
        this.origin = N.origin;
        this.classroom = N.classroom;
        this.RoomName = N.RoomName;
        this.ClassName = N.ClassName;
        this.Parent = N.Parent;
        this.Neighbors = N.getNeighbors();
    }

    /**
     * @return the RoomName
     */
    public String getRoomName() {
        return RoomName;
    }

    /**
     * @return the ClassName
     */
    public String getClassName() {
        return ClassName;
    }

    /**
     * @return the Neighbors
     */
    public PriorityQueue<Edge> getNeighbors() {
        return Neighbors;
    }

    public ArrayList<NodeTEST> getNeighborNodes(){
        ArrayList<NodeTEST> Retable = new ArrayList<>();
        for (Edge edge : Neighbors) {
            Retable.add(edge.getConnection());
        }
        return Retable;
    }

    /**
     * @return the h
     */
    public int h() {
        return h;
    }

    /**
     * @param h the h to set
     */
    private void seth(int h) {
        this.h = h;
    }

    /**
     * @return the origin
     */
    public boolean isOrigin() {
        return origin;
    }

    /**
     *  the h to set
     */
    public void sethManhattan(NodeTEST Dest) {
        seth(Math.abs(Dest.x() - this.x()) + Math.abs(Dest.y() - this.y()));
    }

    public void seth(NodeTEST Dest) {
        sethManhattan(Dest);
    }

    public int f() {
        return 3 * this.g() + this.h;
    }

    public int f(NodeTEST Dest) {
        return 3 * this.g() + (Math.abs(Dest.x() - this.x()) + Math.abs(Dest.y() - this.y()));
    }

    /**
     * @return the x
     */
    public int x() {
        return x;
    }

    /**
     * @return the y
     */
    public int y() {
        return y;
    }

    /**
     * @param Neighbors the Neighbors to set
     */
    public void setNeighbors(ArrayList<Edge> Neighbors) {
        this.Neighbors = new PriorityQueue<>(c);
        this.Neighbors.addAll(Neighbors);
    }

    /**
     * @param RoomName the RoomName to set
     */
    public void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }

    /**
     * @param ClassName the ClassName to set
     */
    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public int g() {
        return this.g;
    }

    public void updateg() {
        //isOrigin add? or use isDestination to get around
        //the beginning not being able to be it's own parent
        //bc it's not in it's own neighbor's queue
        if (origin) {
            this.setg(0);
            return;
        }
        Edge ParentEdge = new Edge(this, 0);
        for (Edge Neighbor : Neighbors) {
            if (Neighbor.getConnection().equals(this.Parent)) {
                ParentEdge = Neighbor;
            }
        }
        this.setg(ParentEdge.getConnection().g() + ParentEdge.g());
    }

    public int g(NodeTEST N) {
        //give g if parameter was parent
        //return N.g + Neighbors.peek().g();
        for (Edge Neighbor : Neighbors) {
            if (Neighbor.getConnection().equals(N)) {
                return this.g() + Neighbor.g();
            }
        }
        return -1;
    }
    //Watch Forever Amazon Prime
    //t(t-4)^(1/3)

    /**
     * @return the Parent
     */
    public NodeTEST getParent() {
        return Parent;
    }

    /**
     * @param Parent the Parent to set
     */
    public void setParent(NodeTEST Parent) {
        this.Parent = Parent;
    }

    /**
     * @param g the g to set
     */
    public void setg(int g) {
        this.g = g;
    }

    /**
     * @return the classroom
     */
    public boolean isClassroom() {
        return classroom;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(boolean origin) {
        this.origin = origin;
    }

    /**
     * @param classroom the classroom to set
     */
    public void setClassroom(boolean classroom) {
        this.classroom = classroom;
    }

    /**
     * @return the wormhole
     */
    public boolean isWormhole() {
        return wormhole;
    }

    /**
     * @param wormhole the wormhole to set
     */
    public void setWormhole(boolean wormhole) {
        this.wormhole = wormhole;
    }
}


/**
 *
 * @author dhuant
 */
class UnitTest {

    public UnitTest() {

    }

    public boolean runAllTests() {
        try {
            manhattanCorrect();
            gCostCorrect();
            chooseLowerf();
            assignParents();
            tieBreaker();
            intendedRoute();
            anUnreachableDestination();
            //teleportCorrect();
            //classCheck();
        } catch (Exception e) {
            System.out.println("UnitTest has encountered an error:\n" + e.getMessage());
            e.printStackTrace();
            return true;
        }
        System.out.println("Unit Test has succesfully completed, no errors were detected");
        return false;
    }

    public void manhattanCorrect() throws Exception {
        NodeTEST A = new NodeTEST("51", 0, 0, true);
        NodeTEST B = new NodeTEST("57", 3, 4);
        A.sethManhattan(B);
        if (!(A.h() == 7)) {
            throw new Exception("Manhattan distance is not being calculated correctly");
        }
    }

    public void gCostCorrect() throws Exception {
        NodeTEST A = new NodeTEST("A", 0, 0, true);
        NodeTEST B = new NodeTEST("B", 2, 0);
        NodeTEST C = new NodeTEST("C", 2, 2);

        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();

        Ae.add(new Edge(B, 2));
        Be.add(new Edge(A, 2));
        Be.add(new Edge(C, 2));
        Ce.add(new Edge(B, 2));

        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);

        A.setParent(A);
        B.setParent(A);
        C.setParent(B);

        A.updateg();
        B.updateg();
        C.updateg();

        if (!(C.g() == 4)) {
            System.out.println(A.g());
            System.out.println(B.g());
            System.out.println(C.g());
            throw new Exception("g Cost is not being calculated correctly");
        }
    }

    public void chooseLowerf() throws Exception {
        NodeTEST A = new NodeTEST("Origin", 0, 0, true);
        NodeTEST B = new NodeTEST("Better", 1, 0);
        NodeTEST C = new NodeTEST("Worse", 0, 1);
        NodeTEST D = new NodeTEST("Destination", 3, 0);

        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();

        A.seth(D);
        B.seth(D);
        C.seth(D);

        Ae.add(new Edge(B, 1));
        Ae.add(new Edge(C, 1));
        Be.add(new Edge(A, 1));
        Ce.add(new Edge(A, 1));

        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);

        Cluster Grid = new Cluster(Arrays.asList(A, B, C, D));
        Grid.CLOSED.add(A);
        Grid.openViable(A, D);
        if (!Grid.OPEN.peek().equals(B)) {
            throw new Exception("issue with f(N) cost not correctly calculated\n"
                    + "Program decided '" + Grid.OPEN.peek().getRoomName() + "' had lowest cost");
        }
    }

    public void assignParents() throws Exception {
        boolean fA, fB, fC, fD;
        //Make a line of 4 nodes, preform Astar
        NodeTEST A = new NodeTEST("A", 0, 1, true);     //
        NodeTEST B = new NodeTEST("B", 1, 1);           //A---B---C--- --- ---D
        NodeTEST C = new NodeTEST("C", 2, 1);           //
        NodeTEST D = new NodeTEST("D", 5, 1);

        A.seth(D);
        B.seth(D);
        C.seth(D);
        D.seth(D);

        Cluster Grid = new Cluster(Arrays.asList(A, B, C, D));

        Grid.connect(A, B);
        Grid.connect(B, C);
        Grid.connect(C, D);

        A.setParent(A);
        Grid.AstarBORING(A, D);

        fA = (A.getParent().equals(A));
        fB = (B.getParent().equals(A));
        fC = C.getParent().equals(B);
        fD = (D.getParent().equals(C));
        if (!(fA && fB && fC && fD)) {
            throw new Exception("Parents were not assigned correctly\nA's parent was " + A.getParent().getRoomName()
                    + "\nB's parent was " + B.getParent().getRoomName() + "\nC's parent was " + C.getParent().getRoomName()
                    + "\nD's parent was " + D.getParent().getRoomName());
        }
    }

    public void intendedRoute() throws Exception {
        NodeTEST A = new NodeTEST("A", 0, 1, true);     //    D               H
        NodeTEST B = new NodeTEST("B", 0, 2);           //    |               |
        NodeTEST C = new NodeTEST("C", 1, 1);           //A---C---E--- --- ---G---I
        NodeTEST D = new NodeTEST("D", 1, 0);           //|       |
        NodeTEST E = new NodeTEST("E", 2, 1);           //B       F
        NodeTEST F = new NodeTEST("F", 2, 2);
        NodeTEST G = new NodeTEST("G", 5, 1);
        NodeTEST H = new NodeTEST("H", 5, 0);
        NodeTEST I = new NodeTEST("I", 6, 1);

        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();
        ArrayList<Edge> De = new ArrayList<Edge>();
        ArrayList<Edge> Ee = new ArrayList<Edge>();
        ArrayList<Edge> Fe = new ArrayList<Edge>();
        ArrayList<Edge> Ge = new ArrayList<Edge>();
        ArrayList<Edge> He = new ArrayList<Edge>();
        ArrayList<Edge> Ie = new ArrayList<Edge>();

        ArrayList<NodeTEST> Map = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> RouteTheo = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> RouteActual = new ArrayList<NodeTEST>();

        Cluster Grid;

        A.seth(I);
        B.seth(I);
        C.seth(I);
        D.seth(I);
        E.seth(I);
        F.seth(I);
        G.seth(I);
        H.seth(I);
        I.seth(I);

        Ae.add(new Edge(B, 1));
        Ae.add(new Edge(C, 1));
        Be.add(new Edge(A, 1));
        Ce.add(new Edge(A, 1));
        Ce.add(new Edge(D, 1));
        Ce.add(new Edge(E, 1));
        De.add(new Edge(C, 1));
        Ee.add(new Edge(C, 1));
        Ee.add(new Edge(F, 1));
        Ee.add(new Edge(G, 3));
        Fe.add(new Edge(E, 1));
        Ge.add(new Edge(E, 3));
        Ge.add(new Edge(H, 1));
        Ge.add(new Edge(I, 1));
        He.add(new Edge(G, 1));
        Ie.add(new Edge(G, 1));

        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);
        D.setNeighbors(De);
        E.setNeighbors(Ee);
        F.setNeighbors(Fe);
        G.setNeighbors(Ge);
        H.setNeighbors(He);
        I.setNeighbors(Ie);

        Map.add(A);
        Map.add(B);
        Map.add(C);
        Map.add(D);
        Map.add(E);
        Map.add(F);
        Map.add(G);
        Map.add(H);
        Map.add(I);

        Grid = new Cluster(Map);

        RouteTheo.add(A);
        RouteTheo.add(C);
        RouteTheo.add(E);
        RouteTheo.add(G);
        RouteTheo.add(I);

        try {
            RouteActual = Grid.routeAstar(A, I);
            if (!RouteTheo.equals(RouteActual)) {
                throw new Exception("Anticipated Route was not taken\nRoute taken was:\n" + Grid.routeString(RouteActual));
            }

        } catch (StackOverflowError e) {
            throw new Exception("Inteded Destination was never reached");
        }
    }

    public void tieBreaker() throws Exception {
        //work with comparator in OPEN priorityQueue to preform openviable (and maybe something else?)
        //on everything that ties
        NodeTEST A = new NodeTEST("A", 0, 1, true);     //B---C---D---E
        NodeTEST B = new NodeTEST("B", 0, 0);           //|           |
        NodeTEST C = new NodeTEST("C", 1, 0);           //A           F
        NodeTEST D = new NodeTEST("D", 2, 0);           //|
        NodeTEST E = new NodeTEST("E", 3, 0);           //G---H---I---J
        NodeTEST F = new NodeTEST("F", 3, 1);
        NodeTEST G = new NodeTEST("G", 0, 2);
        NodeTEST H = new NodeTEST("H", 1, 2);
        NodeTEST I = new NodeTEST("I", 2, 2);
        NodeTEST J = new NodeTEST("J", 3, 2);

        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();
        ArrayList<Edge> Ce = new ArrayList<Edge>();
        ArrayList<Edge> De = new ArrayList<Edge>();
        ArrayList<Edge> Ee = new ArrayList<Edge>();
        ArrayList<Edge> Fe = new ArrayList<Edge>();
        ArrayList<Edge> Ge = new ArrayList<Edge>();
        ArrayList<Edge> He = new ArrayList<Edge>();
        ArrayList<Edge> Ie = new ArrayList<Edge>();
        ArrayList<Edge> Je = new ArrayList<Edge>();

        ArrayList<NodeTEST> Map = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> Route = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> RouteTheo = new ArrayList<NodeTEST>();

        Cluster Grid;

        A.seth(F);
        B.seth(F);
        C.seth(F);
        D.seth(F);
        E.seth(F);
        F.seth(F);
        G.seth(F);
        H.seth(F);
        I.seth(F);
        J.seth(F);

        Ae.add(new Edge(B, 1));
        Ae.add(new Edge(G, 1));
        Be.add(new Edge(A, 1));
        Be.add(new Edge(C, 1));
        Ce.add(new Edge(B, 1));
        Ce.add(new Edge(D, 1));
        De.add(new Edge(C, 1));
        De.add(new Edge(E, 1));
        Ee.add(new Edge(D, 1));
        Ee.add(new Edge(F, 1));
        Fe.add(new Edge(E, 1));
        Ge.add(new Edge(A, 1));
        Ge.add(new Edge(H, 1));
        He.add(new Edge(G, 1));
        He.add(new Edge(I, 1));
        Ie.add(new Edge(H, 1));
        Ie.add(new Edge(J, 1));
        Je.add(new Edge(I, 1));

        A.setNeighbors(Ae);
        B.setNeighbors(Be);
        C.setNeighbors(Ce);
        D.setNeighbors(De);
        E.setNeighbors(Ee);
        F.setNeighbors(Fe);
        G.setNeighbors(Ge);
        H.setNeighbors(He);
        I.setNeighbors(Ie);
        J.setNeighbors(Je);

        Map.add(A);
        Map.add(B);
        Map.add(C);
        Map.add(D);
        Map.add(E);
        Map.add(F);
        Map.add(G);
        Map.add(H);
        Map.add(I);
        Map.add(J);

        RouteTheo.add(A);
        RouteTheo.add(B);
        RouteTheo.add(C);
        RouteTheo.add(D);
        RouteTheo.add(E);
        RouteTheo.add(F);

        Grid = new Cluster(Map);

//        try {
        Route = Grid.routeAstar(A, F);
//        } catch (Exception e) {
//            System.out.println("An error was caused by a long tie");
//        }

        if (!Route.equals(RouteTheo)) {
            Grid.print(Route);
            //why is this route empty
            throw new Exception("tieBreaker resulted in a nonintended route being taken");
        }

    }

    public void anUnreachableDestination() throws Exception {
        NodeTEST A = new NodeTEST("Origin", 0, 0, true);
        NodeTEST B = new NodeTEST("Step", 1, 0);
        NodeTEST C = new NodeTEST("Destination", 2, 0);

        ArrayList<Edge> Ae = new ArrayList<Edge>();
        ArrayList<Edge> Be = new ArrayList<Edge>();

        ArrayList<NodeTEST> Map = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> Route = new ArrayList<NodeTEST>();
        Cluster Grid;

        A.seth(C);
        B.seth(C);
        C.seth(C);

        Ae.add(new Edge(B, 1));
        Be.add(new Edge(A, 1));

        A.setNeighbors(Ae);
        B.setNeighbors(Be);

        Route.add(A);

        Grid = new Cluster();
        Grid.addAll(Arrays.asList(A, B, C));

        try {
            Route = Grid.routeAstar(A, C);
        } catch (NullPointerException e) {
            return;
        }
        throw new Exception("No issue was caused by an unreachable destination");
    }

    public void teleportCorrect() throws Exception {
        NodeTEST A = new NodeTEST("A", 0, 0);
        NodeTEST B = new NodeTEST("B", 0, 1);
        NodeTEST C = new NodeTEST("C", 56, 42, false, true);
        NodeTEST D = new NodeTEST("D", 48, 50);

        A.seth(C);
        B.seth(C);
        C.seth(C);

        C.seth(D);
        D.seth(D);

        Cluster ZoneA = new Cluster(Arrays.asList(A, B, C));
        Cluster ZoneB = new Cluster(Arrays.asList(C, D));

        ZoneA.connect(A, B);
        ZoneA.connect(B, C);
        ZoneB.connect(C, D);

        LayeredCluster Universe = new LayeredCluster(Arrays.asList(ZoneA, ZoneB));

        ArrayList<NodeTEST> InterdimensionalTraversal = new ArrayList<NodeTEST>();
        ArrayList<NodeTEST> Actual = new ArrayList<NodeTEST>();
        InterdimensionalTraversal.addAll(Arrays.asList(A, B, C, D));
        Actual = Universe.Route(A, D);
        if (!Actual.equals(InterdimensionalTraversal)) {
            throw new Exception("InterdimensionalTraversal took an unexpected route!\nRoute taken was: " + ZoneA.routeString(Actual));
        }
    }

    public void classCheck() throws Exception {
        Cluster mapLoc = new Cluster("C:\\Users\\cnewby5283\\Documents\\NetBeansProjects\\AStarRouting\\maps\\reagan_maps");
        if (!mapLoc.closest(90, 140).getRoomName().equals("OK154")) {
            throw new Exception("jacked jacked it up");
        }
    }
}

