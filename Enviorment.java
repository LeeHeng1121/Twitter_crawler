
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.text.TabableView;
import javax.tools.Tool;




public class Enviorment 
{
	//控制 printStep 跟 printBroadcast (通常是EVENT用  的文檔輸出 false 為不輸出
		public static boolean debug = false;
		
	static String configPath;
		
	static int tempx;
	static int tempy;
	static int sinknum;
	public static int Width;
	public static int Height;
	public static int Length;
	static point[] p;
	static sink[] s;
	static int sink_move[][];
	static event event[][];
	static Scanner k;
	static int communication_range=20;
	public static int success_rate;
	public static int success_count;
	static String filePath;
	
	static int closest_sink = -1;
	static sink[] nowSink[];
	
	
	static int  randomwalk_step;
	static int  open_randomwalk; //打開隨機部建
	static int  randomwalk_trailsteps; //隨機走幾步來找
	static int  randomwalk_trailtimes; //找不到時重來幾遍
	static int  X_touchX =0;
	static int  XY_touch_X =0;
	static int  XY_touch_Y =0;
	static int  setfireX,setfireY;
	static int  Extended_hop;
	private static Scanner config;
	static int sinkwalk=0;
	static int bufferSize = 1024; // HRDG 的buffer 能夠存放的封包筆數　假設一個封包為512bit。
	
	static enum Closest_sink_name{Closest_sink_time,Closest_sink_X,Closest_sink_XY,Closest_sink_1,Closest_sink_2,Closest_sink_3,Closest_sink_RandomWalk,Closest_sink_name_size };
	static int CountClosest_sink[] = new int[Closest_sink_name.Closest_sink_name_size.ordinal()];
	
	static enum BeconSize_name{BeconSize_time,BeconSize_X,BeconSize_XY,BeconSize_1,BeconSize_2,BeconSize_3,BeconSize_RandomWalk,BeconSize_name_size };
	static int CountBeconSize[] = new int[BeconSize_name.BeconSize_name_size.ordinal()];
	
	static enum RouteSize_name{RouteSize_time,RouteSize_X,RouteSize_XY,RouteSize_1,RouteSize_2,RouteSize_3,RouteSize_RandomWalk,RouteSize_name_size };
	static long CountRouteSize[] = new long[RouteSize_name.RouteSize_name_size.ordinal()];
	
	static enum GuideSize_name{GuideSize_time,GuideSize_X,GuideSize_XY,GuideSize_name_size};
	static int CountGuideSize[] = new int[GuideSize_name.GuideSize_name_size.ordinal()];
	
	static enum QuerySize_name{QuerySize_time,QuerySize_X,QuerySize_XY,QuerySize_name_size};
	static long CountQuerySize[] = new long[QuerySize_name.QuerySize_name_size.ordinal()];
	
	static int countRandom=0,RandomWalkhop10=0,RandomWalkhop20=0,RandomWalkhop30=0,RandomWalkhop40=0,RandomWalkhop50=0,RandomWalkhop60=0,RandomWalkhop70=0,RandomWalkhop80=0,RandomWalkhop90=0,RandomWalkhop100=0,RandomWalkhop1000=0;
	static int countTime=0,Timehop10=0,Timehop20=0,Timehop30=0,Timehop40=0,Timehop50=0,Timehop60=0,Timehop70=0,Timehop80=0,Timehop90=0,Timehop100=0;
	static int countX=0,Xhop10=0,Xhop20=0,Xhop30=0,Xhop40=0,Xhop50=0,Xhop60=0,Xhop70=0,Xhop80=0,Xhop90=0,Xhop100=0;
	static int countXY=0,XYhop10=0,XYhop20=0,XYhop30=0,XYhop40=0,XYhop50=0,XYhop60=0,XYhop70=0,XYhop80=0,XYhop90=0,XYhop100=0;
	static int count1=0,hop10_1=0,hop20_1=0,hop30_1=0,hop40_1=0,hop50_1=0,hop60_1=0,hop70_1=0,hop80_1=0,hop90_1=0,hop100_1=0,hop1000_1=0;
	static int count2=0,hop10_2=0,hop20_2=0,hop30_2=0,hop40_2=0,hop50_2=0,hop60_2=0,hop70_2=0,hop80_2=0,hop90_2=0,hop100_2=0,hop1000_2=0;
	static int count3=0,hop10_3=0,hop20_3=0,hop30_3=0,hop40_3=0,hop50_3=0,hop60_3=0,hop70_3=0,hop80_3=0,hop90_3=0,hop100_3=0;
	static PrintWriter pen;
	static String[] tableName = {"hop10=","hop20=","hop30=","hop40=","hop50=","hop60=","hop70=","hop80=","hop90=","hop100="};
	static String[] tableName1 = {"hop10=","hop20=","hop30=","hop40=","hop50=","hop60=","hop70=","hop80=","hop90=","hop100=","hop1000="};
	
	static int BeaconSize = 10; //footprint封包大小
	static int GuideSize = 13; //TAG直線封包大小  sink id  =4 hop count =4 direct =1 ? time =4?
//	static int QuerySize = 2; //TAG找GUIDE時的封包大小
//	static int RoutePacketSize = 512; //事件資料大小
	static int BaseQuerySize = 18;
	
	
	
	static int totalHop[] = new int[3];

	static int ShouldWalk = sinkwalk;
	
	

	// 18 x hop + (1+hop) x hop ->這是算Query的公式
	// (18 + hop(該半部最近guide的hop數，並不是實際走到的hop數) x 2) x hop ->這是算Route的公式
	//假設一個封包走了64步，在35步發現有guide，在第64步發現已經沒有接下來的guide
	//算式為 18*64 + (1+64)x 64 <//query + (18+35*2)x64 //route

	//hop^2+19hop 
	
	//沒找到guide也沒找到beacon的情況
	//18 x hop + (1+hop) x hop ->Query
	//18xhop ->Route
	//假設一個封包走了16步發現走到邊界沒有發現guide也沒找到beacon，回傳時會傳回一個空的list
	//算式為18 * 16 + (1+16)* 16 + 18*16
	
	public static void main(String args[])
	{
		
		
		int number,sinkstep,setfireTime,timelen,percent;
		k = new Scanner(System.in);
		
		configPath = "./500X500/8sink/"; 
		Enviorment.open_randomwalk =0; //隨機部建 0=關閉 1=開啟
		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^路徑在這裡^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
		try {
			
			
			config = new Scanner(new File(configPath+"config.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 先建立場景
		 然後詢問要幾顆sensor
		 */
//		System.out.println("輸入長:");
		config.nextLine();
		Height = config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入寬:");
		config.nextLine(); 
		Width = config.nextInt();
		config.nextLine();
		
		Length = (Height < Width)? Height : Width;
		
//		System.out.println("輸入通訊範圍:");
		config.nextLine();
		communication_range = config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入Sensor數量:");
		config.nextLine();
		number= config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入Sink數量:");
		config.nextLine();
		sinknum= config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入Sink每過多久要發一次上下封包:");
		config.nextLine();
		setfireTime = config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入Sink在地圖X軸每幾個間隔要發一次上下封包:");
		config.nextLine();
		setfireX = config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入Sink在地圖Y軸每幾個間隔要發一次上下封包:");
		config.nextLine();
		setfireY = config.nextInt();
		config.nextLine();
		
		Extended_hop = setfireX/14;
//		System.out.println("輸入Sink隨機一個方向之後走幾步:");
		config.nextLine();
		sinkwalk = config.nextInt();
		ShouldWalk = sinkwalk;
		config.nextLine();
		
//		System.out.println("輸入sensor找不到track或close時要隨機走幾步尋找:");
		config.nextLine();
		randomwalk_step = config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入sensor找不到footprint時要隨機走幾步尋找(for trail):");
		config.nextLine();
		randomwalk_trailsteps = config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入sensor找不到footprint時要隨機走幾次尋找(for trail):");
		config.nextLine();
		randomwalk_trailtimes = config.nextInt();
		config.nextLine();
		
		
//		System.out.println("輸入時間長度");
		config.nextLine();
		timelen = config.nextInt();
		config.nextLine();
		
//		System.out.println("輸入事件發生機率(百分比)");
		config.nextLine();
		percent = config.nextInt();
		config.nextLine();
		
//		System.out.println(使用的測試資料夾(原資料夾留個 \);
		config.nextLine();
		filePath = config.nextLine();
		filePath = filePath.replaceAll("/", "\\\\");
		p = new point[number];
		s = new sink[sinknum];
		
		
		try {
			start(Height,Width,number,1,setfireTime,sinknum,timelen,percent,setfireX,setfireY,randomwalk_step);
			TOOL.getAllHop(configPath);
//			Simulation1(timelen);
			
//			System.out.println("By_time Total Hop = " +Enviorment.totalHop[0] );
//			System.out.println("By_time GuideSize = " +CountGuideSize[Enviorment.GuideSize_name.GuideSize_time.ordinal()]);
//			System.out.println("By_time QuerySize = " +CountQuerySize[Enviorment.QuerySize_name.QuerySize_time.ordinal()]);
//			System.out.println("By_time RouteSize = " +CountRouteSize[Enviorment.RouteSize_name.RouteSize_time.ordinal()]);
//			
//			
			System.out.println("By_X Total Hop = " +Enviorment.totalHop[1] );
//			System.out.println("By_X GuideSize = " +CountGuideSize[Enviorment.GuideSize_name.GuideSize_X.ordinal()]);
//			System.out.println("By_X QuerySize = " +CountQuerySize[Enviorment.QuerySize_name.QuerySize_X.ordinal()]);
//			System.out.println("By_X RouteSize = " +CountRouteSize[Enviorment.RouteSize_name.RouteSize_X.ordinal()]);
//			
//			
			System.out.println("By_XY Total Hop = " +Enviorment.totalHop[2] );
//			System.out.println("By_XY GuideSize = " +CountGuideSize[Enviorment.GuideSize_name.GuideSize_XY.ordinal()]);
//			System.out.println("By_XY QuerySize = " +CountQuerySize[Enviorment.QuerySize_name.QuerySize_XY.ordinal()]);
//			System.out.println("By_XY RouteSize = " +CountRouteSize[Enviorment.RouteSize_name.RouteSize_XY.ordinal()]);
			
			if(Enviorment.totalHop[2] > Enviorment.totalHop[1] )
			{
			System.out.println("重跑");
			}
			else
			{
			System.out.println("GGGGGGGGGGGGGGGGGGGGGGGG");
			}
			
			System.out.println("使用的config:"+configPath+"config.txt");
			System.out.println("使用的測試檔案存放位置"+filePath);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	

	private static void start(int h,int w,int n,int sinkstep,int setfire,int sinknum,int tl,int percent,int fireX,int fireY,int randomwalk_step) throws IOException 
	{
		boolean a=false;
		 //先設置好地圖sensor的起始位置
		sensor_place(h,w, n);
		//new SceneGraph(p,Enviorment.length);
		
		//在設置各sink的起始位置
		
		//有隨機要素的在這裡
		
		if(Enviorment.open_randomwalk == 0)
		{
			sink_place(sinknum,filePath+"sink_place_data");  
			sink_move_set(sinknum,filePath+"sink_move_set_data");
			event_time(filePath+"event_time_data");
		}
		
		else if(Enviorment.open_randomwalk == 1)
		{
			sink_place(h,w, sinknum);
			sink_place(sinknum,filePath+"sink_place_data");  
			
			//然後預先決定這一次實驗用的 sink 在所有時間該怎麼移動(存在陣列裡)  確保7種演算法  sink的移動方式都一樣
			sink_move_set(sinkstep , tl);
			sink_move_set(sinknum,filePath+"sink_move_set_data");
			
			//決定這一次實驗哪幾個時間點會發生事件
			event_time(tl,percent);
			event_time(filePath+"event_time_data");
		}
		
		System.out.println("測試資料讀取完畢");
		
		TOOL.fileprefix=null;
		TOOL.printData(event,sink_move);
//		下面三個就是根據同樣的地圖 同樣的移動方式 同樣的事件發生點來模擬
	 
		TOOL.cleanLog(configPath+"TAG統計.txt");
		TOOL.printLog("\tGuideSize\tQuerySize\tRouteSize", configPath+"TAG統計.txt");
		
		
		TOOL.fileprefix = "By_X";
		TOOL.Broadcast_clean =true;
		TOOL.cleanLog(configPath+"By_X成功率.txt");
		start_byX(h,w,n,sinkstep,fireX,sinknum,tl);
		TOOL.cleanLog(configPath+"By_XHOP數統計.txt");
		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_XHOP數統計.txt", true)));
		TOOL.PrintHop(1,pen, Enviorment.countX,tableName,""+Enviorment.totalHop[1],Xhop10,Xhop20,Xhop30,Xhop40,Xhop50,Xhop60,Xhop70,Xhop80,Xhop90,Xhop100);
		TOOL.printLog("By_X\t"+CountGuideSize[Enviorment.GuideSize_name.GuideSize_X.ordinal()]+"\t"+CountQuerySize[Enviorment.QuerySize_name.QuerySize_X.ordinal()]+"\t"+CountRouteSize[Enviorment.RouteSize_name.RouteSize_X.ordinal()], configPath+"TAG統計.txt");
	
		
		
		TOOL.fileprefix = "By_Time";
		TOOL.cleanLog(configPath+"By_Time成功率.txt");
		TOOL.Broadcast_clean =true;
		start_byTime(h,w,n,sinkstep,setfire,sinknum,tl);
		TOOL.cleanLog(configPath+"By_TimeHOP數統計.txt");
		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_TimeHOP數統計.txt", true)));
		TOOL.PrintHop(0,pen, Enviorment.countTime,tableName,""+Enviorment.totalHop[0],Timehop10,Timehop20,Timehop30,Timehop40,Timehop50,Timehop60,Timehop70,Timehop80,Timehop90,Timehop100 );
		TOOL.printLog("" , "TAG統計.txt");
		TOOL.printLog("By_time\t"+CountGuideSize[Enviorment.GuideSize_name.GuideSize_time.ordinal()]+"\t"+CountQuerySize[Enviorment.QuerySize_name.QuerySize_time.ordinal()]+"\t"+CountRouteSize[Enviorment.RouteSize_name.RouteSize_time.ordinal()], configPath+"TAG統計.txt");	
		
		
		
		
		
		TOOL.fileprefix = "By_XY";
		TOOL.Broadcast_clean =true;
		TOOL.cleanLog(configPath+"By_XY成功率.txt");
		start_byXY(h,w,n,sinkstep,fireX,fireY,sinknum,tl);
		TOOL.cleanLog(configPath+"By_XYHOP數統計.txt");
		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_XYHOP數統計.txt", true)));
		TOOL.PrintHop(2,pen, Enviorment.countXY,tableName,""+Enviorment.totalHop[2],XYhop10,XYhop20,XYhop30,XYhop40,XYhop50,XYhop60,XYhop70,XYhop80,XYhop90,XYhop100);
		
		TOOL.printLog("" , "TAG統計.txt");
		TOOL.printLog("By_XY\t"+CountGuideSize[Enviorment.GuideSize_name.GuideSize_XY.ordinal()]+"\t"+CountQuerySize[Enviorment.QuerySize_name.QuerySize_XY.ordinal()]+"\t"+CountRouteSize[Enviorment.RouteSize_name.RouteSize_XY.ordinal()], configPath+"TAG統計.txt");
		TOOL.printLog("" , "TAG統計.txt");
		TOOL.printLog(""+Enviorment.totalHop[1] , configPath+"TAG統計.txt");
		TOOL.printLog(""+Enviorment.totalHop[0] , configPath+"TAG統計.txt");
		TOOL.printLog(""+Enviorment.totalHop[2] , configPath+"TAG統計.txt");
//		/*有track、有close、有random walk "DDRP"*/
//		TOOL.fileprefix = "By_1";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_1成功率.txt");
//		start_by1(h,w,n,sinkstep,sinknum,tl);
//		TOOL.cleanLog(configPath+"By_1HOP數統計.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_1HOP數統計.txt", true)));
//		TOOL.PrintHop(3,pen,count1,tableName1,RouteSize_name.RouteSize_1.ordinal(),hop10_1,hop20_1,hop30_1,hop40_1,hop50_1,hop60_1,hop70_1,hop80_1,hop90_1,hop100_1,hop1000_1);
//		
//		
//
//		/*有random walk、有足跡封包   "Trail based" */
//		TOOL.fileprefix = "By_2";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_2成功率.txt");
//		start_by2(h,w,n,sinkstep,sinknum,tl,randomwalk_step);
//		TOOL.cleanLog(configPath+"By_2HOP數統計.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_2HOP數統計.txt", true)));
//		TOOL.PrintHop(4,pen,count2,tableName1,RouteSize_name.RouteSize_2.ordinal(),hop10_2,hop20_2,hop30_2,hop40_2,hop50_2,hop60_2,hop70_2,hop80_2,hop90_2,hop100_2,hop1000_2);
//		
//		
		/*有track、有close、定期銷毀track  "HRDG"*/		
//		TOOL.fileprefix = "By_3";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_3成功率.txt");
//		start_by3(h,w,n,sinkstep,sinknum,tl,randomwalk_step);
//		TOOL.cleanLog(configPath+"By_3HOP數統計.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_3HOP數統計.txt", true)));
//		TOOL.PrintHop(5,pen,count3,tableName,RouteSize_name.RouteSize_3.ordinal(),hop10_3,hop20_3,hop30_3,hop40_3,hop50_3,hop60_3,hop70_3,hop80_3,hop90_3,hop100_3);
//		
//		
//
//		TOOL.fileprefix = "By_RandomWalk";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_RandomWalk成功率.txt");
//		start_byRandomWalk(h,w,n,sinkstep,sinknum,tl,randomwalk_step);
//		TOOL.cleanLog(configPath+"By_RandomWalkHOP數統計.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_RandomWalkHOP數統計.txt", true)));
//		TOOL.PrintHop(6,pen,countRandom,tableName1,RouteSize_name.RouteSize_RandomWalk.ordinal(),RandomWalkhop10,RandomWalkhop20,RandomWalkhop30,RandomWalkhop40,RandomWalkhop50,RandomWalkhop60,RandomWalkhop70,RandomWalkhop80,RandomWalkhop90,RandomWalkhop100,RandomWalkhop1000);
		
		
		
//			print(l,n,p);//印出sensor位置
			
			
	}
	private static void event_time(String FileName)
	{
		try {
			ObjectInputStream pen = new ObjectInputStream(new FileInputStream(FileName) );
			event = (event[][]) pen.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void event_time(int tl, int percent) {
		// TODO Auto-generated method stub
		int eventPerRound = (p.length*percent)/100;
		event = new event[tl][eventPerRound];
		
		for(int i = 1 ; i < tl ; i++)
		{
			
			boolean check[] = new boolean[p.length];
			for(int j = 0 ; j < event[i].length ; j++)
			{
				
				int id = (int)(Math.random()*p.length);
				if(!check[id])
				{
					event[i][j] = new event();
					event[i][j].sensorId = id;
					check[id] =true;
				}
				else
					j--;
			}
			
		}
		
		try {
			ObjectOutputStream pen = new ObjectOutputStream(new FileOutputStream(filePath+"event_time_data") );
			pen.writeObject(event);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sink_move_set(int sinknum,String FileName)
	{
		try {
			ObjectInputStream pen = new ObjectInputStream(new FileInputStream(FileName) );
			sink_move =  (int[][]) pen.readObject();
			
//			for(int i = 0 ; i < sink_move[0].length;i++)
//			{
//				int temp = sink_move[0][i];
//				sink_move[0][i] = sink_move[1][i];
//				sink_move[1][i] = temp;
//			}
			
			int j = 0;
			PrintWriter a = new PrintWriter(new BufferedWriter(
					new FileWriter(filePath+"sink_move_set.txt", false)));

			while (j < sinknum) {
				int x = s[j].x;
				int y = s[j].y;
				a.printf("time = 0 %d %d\n", x, y);
				a.println();
				for (int i = 0; i < sink_move[j].length; i++) {
					switch (sink_move[j][i]) // 0上  1左   2下    3右 
					{
					case 0:
						y--;
						break;
					case 1:
						x--;
						break;
					case 2:
						y++;
						break;
					case 3:
						x++;
						break;
					}
					a.printf("time = %d  %d %d\n", i, x, y);
					a.println();
				}
				
				j++;
			}
			a.close();
			
//			ObjectOutputStream pen1 = new ObjectOutputStream(new FileOutputStream(filePath+"sink_move_set_data") );
//			pen1.writeObject(sink_move);
//			pen1.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sink_move_set(int sinkstep, int tl) {
		//sinkstep = sink走一步多遠
		//t1 = 總共時間長度
		//s = 第幾個sink的編號
		sink_move = new int[s.length][(tl/sinkstep)+1];
		int temp_way=-1,stepcount=0,way = -1;
		for(int i = 0 ; i < s.length ; i++)
		{
			stepcount=0;
			int x = s[i].x,y = s[i].y; //x=sink的X軸    y=sink的Y軸
			for(int j = 0 ; j < sink_move[i].length ; j++)
			{
				if(stepcount % Enviorment.ShouldWalk == 0 ) //走完後決定下次的方向
				{
					stepcount = 0;
					
					way = move(x, y, Height,Width,temp_way); //決定方向，並檢查下個方向會不會出界
					temp_way = way;
					
				}
				
				
				switch(way) // 0上  1左   2下    3右 
				{
					case 0:
						y--;
						stepcount ++;
						break;
					case 1:
						x--;
						stepcount ++;
						break;
					case 2:
						y++;
						stepcount ++;
						break;
					case 3:
						x++;
						stepcount ++;
						break;
				}
				sink_move[i][j] = way;
			}
			
		}
		
		try {
			ObjectOutputStream pen = new ObjectOutputStream(new FileOutputStream(filePath+"sink_move_set_data") );
			pen.writeObject(sink_move);
			pen.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void sink_place(int sinknum,String FileName)
	{
		try {
			ObjectInputStream pen = new ObjectInputStream(new FileInputStream(FileName) );
			s =  (sink[]) pen.readObject();
			
			sink temp[] = new sink[sinknum];
			for(int i = 0 ; i < temp.length ; i++)
			{
//				int k = i;
//				if(i== 0)
//					k = 1;
//				else if(i==1)
//					k = 0;
				temp[i] = s[i];
			}
			s = temp;
			
//			ObjectOutputStream pen1 = new ObjectOutputStream(new FileOutputStream(filePath+"sink_place_data") );
//			pen1.writeObject(s);
//			pen1.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void sink_place(int h,int w, int sinknum) {
		boolean a;
		s = new sink[sinknum];
		for(int k=0;k<sinknum;k++)
		{
			a=false;
			do
			{
				a = false;
				s[k] = new sink();
				
//				s[k].x = p[k*50].x+1;
//				s[k].y = p[k*50].y+1;
				s[k].x=((int) (Math.random()*w)*Enviorment.setfireX)%w;//亂數決定座標
				s[k].y=((int) (Math.random()*h)*Enviorment.setfireY)%h;
//				s[k].x=(int) (Math.random()*w);//亂數決定座標
//				s[k].y=(int) (Math.random()*h);
//				s[k].x = k*(Height/(sinknum*2));
//				s[k].y = k*(Width/(sinknum*2));
				for(int i = 0 ; i < k ; i++)
				{
					if(s[i].x==s[k].x&&s[i].y==s[k].y)
					{
							a=true;
					}
				}
				
				
				boolean isInside = false;
				for(int i = 0 ; i < p.length ; i++)
				{
					sink t = s[k];
					point q = p[i];
					if(TOOL.COMP_Dis(s[k], p[i]) <= communication_range)
						isInside = true;
				}
				if(isInside == false)
				{
					a=true;
				}
			
				else//成功佈 sink
				{
					sink.Broadcast(p, s, k, 0,GuideSize_name.GuideSize_time.ordinal());
					sink.Broadcast(p, s, k, 0,GuideSize_name.GuideSize_X.ordinal());
					sink.BroadcastXY(p, s, k, 0,GuideSize_name.GuideSize_XY.ordinal(),true);
					sink.BroadcastXY(p, s, k, 0,GuideSize_name.GuideSize_XY.ordinal(),false);
				}
			}while(a==true);
		}
		
		try {
			ObjectOutputStream pen = new ObjectOutputStream(new FileOutputStream(filePath+"sink_place_data") );
			pen.writeObject(s);
			pen.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void sensor_place(int h,int w, int n) throws IOException {
		boolean a;
	
		BufferedReader br=new BufferedReader(new FileReader(("Sensor000.txt")));
		Scanner config = new Scanner(new File("Sensor000.txt"));
		int[] lineX = new int[n];
		int[] lineY = new int[n];
		int k=0;
		String String_buffer;
//		p[0] = new point();
//		p[0].foot = new foot();
//		p[0].x=(int) (Math.random()*l);//亂數決定座標  第一顆任意位置
//		p[0].y=(int) (Math.random()*l);
//		p[0].data_buffer=new int [n];
//		p[0].data_bufferHopCount = new int [n];
		for(int i=0;i<n;i++) //讀檔案取佈點座標
		{
//			do
//			{
				a=false;
				
				p[i] = new point();
				p[i].data_buffer =new int[n];
				p[i].data_bufferHopCount =new int[n];
				p[i].foot = new foot();
				p[i].sensorid = i;
				String_buffer=br.readLine();
				p[i].x=(int) Float.parseFloat(String_buffer.substring(0, String_buffer.indexOf(' ')));
				p[i].y=(int) Float.parseFloat(String_buffer.substring(String_buffer.indexOf(' ')+1,String_buffer.length()));
				
				
		}
		
	}

	private static void link_news(int n,point p[]) {
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0 ; j < n ; j ++)
			{
				if(j != i)
				{
					if(TOOL.COMP_Dis(p[i], p[j]) <= communication_range) 
					{
						if(p[i].x > p[j].x)
						{
							if(p[i].left != null && TOOL.COMP_Dis(p[i].left, p[i]) < TOOL.COMP_Dis(p[i], p[j]) )
								p[i].left = p[j];
							else if(p[i].left == null)
								p[i].left = p[j];
							
							if(p[i].y > p[j].y && p[i].topLeft != null && TOOL.COMP_Dis(p[i].topLeft, p[i]) < TOOL.COMP_Dis(p[i], p[j]))
								p[i].topLeft = p[j];
							else if(p[i].y > p[j].y && p[i].topLeft == null)
								p[i].topLeft = p[j];
							
							if(p[i].y < p[j].y && p[i].buttomLeft != null && TOOL.COMP_Dis(p[i].buttomLeft, p[i]) < TOOL.COMP_Dis(p[i], p[j]))
								p[i].buttomLeft = p[j];
							else if(p[i].y > p[j].y && p[i].buttomLeft == null)
								p[i].buttomLeft = p[j];
						}
						else if(p[i].x < p[j].x)
						{
							if(p[i].right != null && TOOL.COMP_Dis(p[i].right, p[i]) < TOOL.COMP_Dis(p[i], p[j]) )
								p[i].right = p[j];
							else if(p[i].right == null)
								p[i].right = p[j];
							
							if(p[i].y > p[j].y && p[i].topRight != null && TOOL.COMP_Dis(p[i].topRight, p[i]) < TOOL.COMP_Dis(p[i], p[j]))
								p[i].topRight = p[j];
							else if(p[i].y > p[j].y && p[i].topRight == null)
								p[i].topRight = p[j];
							
							if(p[i].y < p[j].y && p[i].buttomRight != null && TOOL.COMP_Dis(p[i].buttomRight, p[i]) < TOOL.COMP_Dis(p[i], p[j]))
								p[i].buttomRight = p[j];
							else if(p[i].y > p[j].y && p[i].buttomRight == null)
								p[i].buttomRight = p[j];
						}	
						
						if(p[i].y > p[j].y)
						{
							if(p[i].up != null && TOOL.COMP_Dis(p[i].up, p[i]) < TOOL.COMP_Dis(p[i], p[j]) )
								p[i].up = p[j];
							else if(p[i].up == null)
								p[i].up = p[j];
						}
						else if(p[i].y < p[j].y)
						{
							if(p[i].down != null && TOOL.COMP_Dis(p[i].down, p[i]) < TOOL.COMP_Dis(p[i], p[j]) )
								p[i].down = p[j];
							else if(p[i].down == null)
									p[i].down = p[j];
						}
						
						p[i].nearby.add(p[j]);
					}
					
				}
			}
		}
		//這邊是回去檢查是否真的旁邊沒有
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = 0 ; i < p.length ; i++)
			if(p[i].nearby.size() == 0)
			{
				System.out.println("旁邊沒人????+ " +i);
				temp.add(i);
			}
		if(temp.size()>0)
		{
			int test = temp.get(0);
	
			for(int i = 0 ; i < p.length ; i++)
			{
				if(TOOL.COMP_Dis(p[i],p[test]) <= communication_range && i != test)
					System.out.printf("test = %d %d  i = %d %d",p[test].x,p[test].y,p[i].x,p[i].y);
			}
			
			System.out.println("上述動作完成");
			(new Scanner(System.in)).nextLine();
		}
	}
	
	private static void start_byTime(int h,int w,int sensor,int step,int setfire,int sinknum, int tl) throws IOException 
	{
		sink.searchNum = 0;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //確定sensor 裡上下左右的 sensor 用
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_Time'右'邊的"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_Time'左'邊的"+"Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO 自動產生的 catch 區塊
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		int fp_step =1; // 每步發一個footprint 
		
		while(time < tl)
		{
			
			if(time % step == 0) // 移動事件
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					int way =sink_move[i][time/step]; 
					if(time%fp_step==0){
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_time.ordinal());//發footprint
					}
					switch(way)
					{
						
						case 0:
							Sink[i].y--;
							break;
						case 1:
							Sink[i].x--;
							break;
						case 2:
							Sink[i].y++;
							break;
						case 3:
							Sink[i].x++;
							break;
					}
				}
				clear_and_announce(Sink,Sensor);
			}
			
			if(time % setfire == 0 ) // sink 因為時間到了 要發上下封包
			{
				for(int i = 0 ; i <s.length ; i++)
					sink.Broadcast(Sensor, Sink, i, time,GuideSize_name.GuideSize_time.ordinal());
			}
			if(time != 0)
			{
				
				for(int i = 0 ; i < event[time].length ; i++)
				{
					//TOOL.printMap(Sensor,Sink,l,time,"By_Time");
					TOOL.PrintEventHead("By_Time'右'邊的"+"Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
					TOOL.PrintEventHead("By_Time'左'邊的"+"Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
					
				  	//追蹤封包的動作開始				
					int checkTime = ((int)( time / setfire))*setfire ;  //  ((int)(26/4)) *4
					Enviorment.countTime ++;
					
					closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
					Sensor[event[time][i].sensorId].searchByWE_andTime( checkTime,time,"By_Time",QuerySize_name.QuerySize_time.ordinal(),Enviorment.RouteSize_name.RouteSize_time.ordinal(),Enviorment.Closest_sink_name.Closest_sink_time.ordinal());
					
				}
			}
			if( (time*100/tl) == progress)
			{
				System.out.printf("By_T = %d%%\n",progress);
				progress+=10;
			}
			time++;
			//k.nextLine();
			
		}
		

				
	}
	
	private static void start_byX(int h,int w,int sensor,int step,int fireX,int sinknum, int tl) throws IOException 
	{
		sink.searchNum = 0;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //確定sensor 裡上下左右的 sensor 用
		
		for(int i = 0 ; i < Sink.length ; i++)
			sink.Broadcast(Sensor, Sink, i, 0,GuideSize_name.GuideSize_X.ordinal());
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'右'邊的"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'左'邊的"+"Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO 自動產生的 catch 區塊
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		for(int i = 0 ; i < sinknum ; i++)
		{
			sink.Broadcast(Sensor, Sink, i, time,GuideSize_name.GuideSize_X.ordinal());
		}
		while(time < tl)
		{
			if(time % step == 0) // 移動事件
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					int way =sink_move[i][time/step]; 
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_X.ordinal());
					
					switch(way)
					{
						
						case 0:
							Sink[i].y--;
							break;
						case 1:
							Sink[i].x--;
							break;
						case 2:
							Sink[i].y++;
							break;
						case 3:
							Sink[i].x++;
							break;
					}
					clear_and_announce(Sink,Sensor);
					int X = Sink[0].x;
					if(Sink[i].x % fireX == 0)  //sensor因為 X軸間隔到了要上下發  
						sink.Broadcast(Sensor, Sink, i, time,GuideSize_name.GuideSize_X.ordinal());
				}
			}
			
			if(time != 0)
			for(int i = 0 ; i < event[time].length ; i++)
			{
				//TOOL.printMap(Sensor,Sink,l,time,"By_X");
				try {
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'右'邊的"+"Event.txt", true)));
					pen.println();
					pen.println("事件時間:"+time);
					pen.println("發生事件的Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'左'邊的"+"Event.txt", true)));
					pen.println();
					pen.println("事件時間:"+time);
					pen.println("發生事件的Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
				} catch (IOException e) {
					// TODO 自動產生的 catch 區塊
					e.printStackTrace();
				}
			  	//追蹤封包的動作開始				
				Enviorment.countX ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchByWE_andTime(0, time, "By_X",QuerySize_name.QuerySize_X.ordinal(),Enviorment.RouteSize_name.RouteSize_X.ordinal(),Enviorment.Closest_sink_name.Closest_sink_X.ordinal());
			}
			if( (time*100/tl) == progress)
			{
				System.out.printf("By_X = %d%%\n",progress);
				progress+=10;
			}
			time++;
			//k.nextLine();
			
		}
		

				
	}
	
	private static void start_byXY(int h,int w,int sensor,int step,int fireX,int fireY,int sinknum, int tl) throws IOException 
	{
		sink.searchNum = 0;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //確定sensor 裡上下左右的 sensor 用
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'右'邊的"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'左'邊的"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'上'面的"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'下'面的"+"Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO 自動產生的 catch 區塊
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		for(int i = 0 ; i < Sink.length;i++)
		{
			sink.BroadcastXY(Sensor, Sink, i, 0,GuideSize_name.GuideSize_XY.ordinal(),true);   //起始就要往四個方向打封包
			sink.BroadcastXY(Sensor, Sink, i, 0,GuideSize_name.GuideSize_XY.ordinal(),false);   //起始就要往四個方向打封包
		}
		
		while(time < tl)
		{
			if(time % step == 0) // 移動事件
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					//每一次都是先移動
					int way =sink_move[i][time/step];
					//前面已經設置好移動路線 這邊取出來行走??
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_XY.ordinal());
					
					//way 就是方向  2/3看哪個方向決定怎麼動座標
					switch(way)
					{
						
						case 0:
							Sink[i].y--;
							break;
						case 1:
							Sink[i].x--;
							break;
						case 2:
							Sink[i].y++;
							break;
						case 3:
							Sink[i].x++;
							break;
					}
					clear_and_announce(Sink,Sensor);
					
					if(Sink[i].x % fireX == 0)  //sensor因為 X軸間隔到了要上下發  
					{
						sink.BroadcastXY(Sensor, Sink, i, time,GuideSize_name.GuideSize_XY.ordinal(),true);									
					}
					if(Sink[i].y % fireY == 0)  //sensor因為 y軸間隔到了要上下發
					{
						sink.BroadcastXY(Sensor, Sink, i, time,GuideSize_name.GuideSize_XY.ordinal(),false);
					}
		
				}
			}
			if(time != 0)
			//移動完之後 看這一秒(步)  有沒有事件發生 
			for(int i = 0 ; i < event[time].length ; i++)
			{
				//這是更新那些文件用的
				//TOOL.printMap(Sensor,Sink,l,time,"By_XY");
				try {
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'右'邊的"+"Event.txt", true)));
					pen.println();
					pen.println("事件時間:"+time);
					pen.println("發生事件的Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'左'邊的"+"Event.txt", true)));
					pen.println();
					pen.println("事件時間:"+time);
					pen.println("發生事件的Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'上'面的"+"Event.txt", true)));
					pen.println();
					pen.println("事件時間:"+time);
					pen.println("發生事件的Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'下'面的"+"Event.txt", true)));
					pen.println();
					pen.println("事件時間:"+time);
					pen.println("發生事件的Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
				} catch (IOException e) {
					// TODO 自動產生的 catch 區塊
					e.printStackTrace();
				}
			  	//追蹤封包的動作開始				
				Enviorment.countXY ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchXY(0,time,"By_XY",QuerySize_name.QuerySize_XY.ordinal(),Enviorment.RouteSize_name.RouteSize_XY.ordinal(),Enviorment.Closest_sink_name.Closest_sink_XY.ordinal());
			}
			
			if( (time*100/tl) == progress)
			{
				System.out.printf("By_XY = %d%%\n",progress);
				progress+=10;
			}
			time++;
			//k.nextLine();
			
		}

				
	}
	
	private static void start_by1(int h,int w,int sensor,int step,int sinknum, int tl) 
	{
		sink.searchNum = 1;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //確定sensor 裡上下左右的 sensor 用
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_1Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO 自動產生的 catch 區塊
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // 移動事件
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					int way =sink_move[i][time/step]; 
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_1.ordinal());
					
					switch(way)
					{
						
						case 0:
							Sink[i].y--;
							break;
						case 1:
							Sink[i].x--;
							break;
						case 2:
							Sink[i].y++;
							break;
						case 3:
							Sink[i].x++;
							break;
					}
					
				}
			}
			
			if(time != 0)
			for(int i = 0 ; i < event[time].length ; i++)
			{
				//TOOL.printMap(Sensor,Sink,l,time,"By_1");
				TOOL.PrintEventHead("By_1Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
			  	//追蹤封包的動作開始	
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.count1 ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchBy1(Sensor,0, time, "By_1");
				//某一顆sensor發生的事件 呼叫了自己的方法
			}
			time++;
			//k.nextLine();
			
		}
		

				
	}
	
	private static void start_by2(int h,int w,int sensor,int step,int sinknum, int tl,int randomwalk_step) 
	{
		sink.searchNum = 2;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //確定sensor 裡上下左右的 sensor 用
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_2Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO 自動產生的 catch 區塊
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // 移動事件
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					int way =sink_move[i][time/step]; 
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_2.ordinal());
					
					switch(way)
					{
						
						case 0:
							Sink[i].y--;
							break;
						case 1:
							Sink[i].x--;
							break;
						case 2:
							Sink[i].y++;
							break;
						case 3:
							Sink[i].x++;
							break;
					}
					
				}
			}
			
			if(time != 0)
			for(int i = 0 ; i < event[time].length ; i++)
			{
				//TOOL.printMap(Sensor,Sink,l,time,"By_2");
				TOOL.PrintEventHead("By_2Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
			  	//追蹤封包的動作開始				
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.count2 ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchBy2(Sensor,0, time, "By_2");
				//某一顆sensor發生的事件 呼叫了自己的方法
			}
			time++;
			//k.nextLine();
			
		}
		
		System.out.println("start_by2 跑完了");
				
	}
	
	private static void start_by3(int h,int w,int sensor,int step,int sinknum, int tl,int randomwalk_step) 
	{
		sink.searchNum = 3;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //確定sensor 裡上下左右的 sensor 用
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_3Event.txt", false)));
			pen = new PrintWriter(new BufferedWriter(new FileWriter("trackBy_3Event.txt", false)));
			pen = new PrintWriter(new BufferedWriter(new FileWriter("track統計By_3Event.txt", false)));
		} catch (IOException e1) {
			// TODO 自動產生的 catch 區塊
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // 移動事件
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					int way =sink_move[i][time/step]; 
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_3.ordinal());
					
					switch(way)
					{
						
						case 0:
							Sink[i].y--;
							break;
						case 1:
							Sink[i].x--;
							break;
						case 2:
							Sink[i].y++;
							break;
						case 3:
							Sink[i].x++;
							break;
					}
					
					for(int temp=0;temp<Sensor.length;temp++)
					{
						if(Sensor[temp].foot.trackID!=-1 && time - Sensor[temp].eventTime >= Enviorment.Length)//如果trackTime的時間和現在的時間差了500則刪除
						{
							TOOL.printStep("時間:"+time+"燒毀對象"+Sensor[temp].sensorid, "By_3", "Track");
							Sensor[temp].foot.trackID=-1; //燒毀(track無效化)
							Sensor[temp].eventTime = Integer.MAX_VALUE; //燒毀(track無效化)
							
						}
					}
				}
			}
			
			if(time != 0)
			for(int i = 0 ; i < event[time].length ; i++)
			{
				
				//TOOL.printMap(Sensor,Sink,l,time,"By_3");
				TOOL.PrintEventHead("By_3Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
				
			  	//追蹤封包的動作開始				
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.count3 ++;
				Enviorment.closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				
				Sensor[event[time][i].sensorId].searchBy3(Sensor,0, time, "By_3");
				//某一顆sensor發生的事件 呼叫了自己的方法
			}
			ArrayList<point> HasT = new ArrayList<point>();
			for(int temp=0;temp<Sensor.length;temp++)
			{
				if(Sensor[temp].foot.trackID > -1)
					HasT.add(Sensor[temp]);
				if(Sensor[temp].data_keep>=1)
				{
					Enviorment.count3 ++;
					//保留的資料試圖傳出時更新最近的 Sink (拿掉就只以最初事件發生的當下)
					//Enviorment.closest_sink = TOOL.computeTheClosestSink(Sensor[temp], Sink);
					
					TOOL.PrintEventHead("By_3Event.txt", time,Sensor[temp].sensorid, Sensor[temp].x, Sensor[temp].y);
					Sensor[temp].searchBy3(Sensor, 0, time, "By_3");
				}
			}
			StringBuilder t = new StringBuilder(time+"\n");
			for(int temp = 0 ; temp < HasT.size() ;temp++)
			{
				t.append(HasT.get(temp).sensorid+" ");
				
			}
			TOOL.printStep("","By_3","track統計");
			TOOL.printStep(t.toString(), "By_3","track統計");
			
			time++;
			//k.nextLine();
			
		}
		for(int i = 0 ; i < Sink.length ; i++)
		{
			TOOL.printLog("Sink ID:" + i + "收到了 " +Sink[i].count , "By_3成功率.txt");
			System.out.println(Sink[i].count);
		}

				
	}
	private static void start_byRandomWalk(int h,int w,int sensor,int step,int sinknum, int tl,int randomwalk_step) 
	{
		sink.searchNum = 4;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //確定sensor 裡上下左右的 sensor 用
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_RandomWalkEvent.txt", false)));
		} catch (IOException e1) {
			// TODO 自動產生的 catch 區塊
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // 移動事件
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					int way =sink_move[i][time/step]; 
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_RandomWalk.ordinal());
					
					switch(way)
					{
						
						case 0:
							Sink[i].y--;
							break;
						case 1:
							Sink[i].x--;
							break;
						case 2:
							Sink[i].y++;
							break;
						case 3:
							Sink[i].x++;
							break;
					}
					
				}
			}
			
			if(time != 0)
			for(int i = 0 ; i < event[time].length ; i++)
			{
				//TOOL.printMap(Sensor,Sink,l,time,"By_RandomWalk");
				
				TOOL.PrintEventHead("By_RandomWalkEvent.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
				
			  	//追蹤封包的動作開始				
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.countRandom ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchByRandomWalk(Sensor,0, time, "By_RandomWalk");
				//某一顆sensor發生的事件 呼叫了自己的方法
			}
			time++;
			//k.nextLine();
			
		}
		

				
	}
	public static void clear_and_announce(sink s[], point map[])
	{
		for(int i=0; i < map.length;i++)
		{
			map[i].sink_nearhere =0; //把所有的sinknearhere刷空
			map[i].foot.near = null;
		}
		for(int l=0;l<s.length;l++)
		{
			for(int k =0;k<map.length;k++)
			{
				if (TOOL.COMP_Dis(s[l], map[k])<=communication_range) //小於通訊範圍內的sensor
				{
					map[k].sink_nearhere =1; //把小於一百的sinknearhere =1
					map[k].foot.id = l;
				}
			}
		}
	}
	
	private static int move(int x , int y,int h,int w,int tempway) {
		// TODO Auto-generated method stub
		int way = -1; // 0上  1左   2下    3右   這邊只是初始用
		while(way == -1)
		{
			Enviorment.ShouldWalk = Enviorment.sinkwalk;
			//決定要上下左右
			way = (int)(Math.random()*4);
			//如果前一步是往上，現在決定的方向就不能往下或往上
			if(tempway == 0 && (way==2 || way ==0))
			{
				way =-1;
			}
			else if (tempway == 1 &&(way==3 || way ==1))
			{
				way =-1;
			}
			else if (tempway == 2 && (way ==0|| way ==2))
			{
				way =-1;
			}
			else if (tempway == 3 && (way ==1|| way ==3))
			{
				way =-1;
			}
			
			
			//先創一個temp點，檢查下一步會不會跑出界外
			point temp = new point();
			temp.x = x;
			temp.y = y;
			
			switch(way)
			{
				
				case 0:
					temp.y-=Enviorment.sinkwalk;
					break;
				case 1:
					temp.x-=Enviorment.sinkwalk;
					break;
				case 2:
					temp.y+=Enviorment.sinkwalk;
					break;
				case 3:
					temp.x+=Enviorment.sinkwalk;
					break;
			}
			//檢查這個temp點移動之後會不會超出邊界
			boolean isInside = false;
			if(temp.x < 0 )
			{
				
				Enviorment.ShouldWalk = x;
				temp.x = 0;
			}
			else if (temp.x >= h )
			{
				
				Enviorment.ShouldWalk = h-x-1;
				temp.x = h-1;
			}
			else if(temp.y < 0 )
			{
				
				Enviorment.ShouldWalk = y;
				temp.y = 0;
			}
			else if(temp.y >= w ) 
			{
				
				Enviorment.ShouldWalk = w-y-1;
				temp.y= w-1;
			}
				//way = -1;
				
			for(int i = 0 ; i < p.length ; i++)
			{
				//檢查temp點移動之後會不會跑出sensor能夠感測的範圍之外
				if(TOOL.COMP_Dis(p[i], temp) <= communication_range)
				{
					isInside = true;
				}
			}
			if(isInside == false)
				way = -1;
			if(Enviorment.ShouldWalk == 0)
				way = -1;
		}
		return way;
		
		
		//留足跡
		
	}

	private static void Simulation1(int timelen) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner config0 = new Scanner(new File("By_Time成功率.txt"));
		Scanner configX = new Scanner(new File("By_X成功率.txt"));
		Scanner configXY = new Scanner(new File("By_XY成功率.txt"));
		Scanner config1 = new Scanner(new File("By_1成功率.txt"));
		Scanner config2 = new Scanner(new File("By_2成功率.txt"));
		Scanner config3 = new Scanner(new File("By_3成功率.txt"));
		Scanner config4 = new Scanner(new File("By_RandomWalk成功率.txt"));
		
		int now_time = 1;
		String[][] line = new String [6][];
		String round;
		int total[] = new int[6];
		double rate[] = new double[6];

//		line[0]=configTime.nextLine().split(" ");
//		line[1]=configX.nextLine().split(" ");
//		line[2]=configXY.nextLine().split(" ");
//		line[3]=config1.nextLine().split(" ");
//		line[4]=config2.nextLine().split(" ");
//		line[5]=config3.nextLine().split(" ");
			
		while(configX.hasNextLine())
		{
			line[0]=configX.nextLine().split(" ");
					
			round = line[0][0].split(":")[1];
			if(Integer.parseInt(round) /10 > now_time)
			{
				if(total[0] != 0)
					System.out.printf("%d round:%.2f\n",now_time,rate[0]/total[0]);
				else
					System.out.println(now_time+" round:"+"尚無資料");
				now_time=Integer.parseInt(round) /10;
				total[0]++;
				rate[0]+= Double.parseDouble(line[0][4]);
			}
			else
			{
				total[0]++;
				rate[0]+= Double.parseDouble(line[0][4]);
			}
			
			
		}
			
		
		
	}
}
	class foot
	{
		int x,y,time = -1,id = -1
		,way,quadrant,closeID=-1,closetime =-1,trackID =-1,tracktime = (int)Integer.MAX_VALUE;
		boolean isInnerClose = false;
		sink near = null;
		
		public foot(foot foot) {
			// TODO Auto-generated constructor stub
			x = foot.x;
			y = foot.y;
			time = foot.time;
			id = foot.id;
			way = foot.way;
			quadrant = foot.quadrant;
		}
		public foot() {
			// TODO Auto-generated constructor stub
		}

		
	}
	class event implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5604130994347155785L;
		boolean happen = true;
		int sensorId;
		
	}
	class searchPackage
	{
		
		int findedLast_BroadCastTime = -1;
		int findedLast_footprintTime = -1;
		int findedLast_BroadCastTime_inQuery = -1;
		int findedLast_footprintTime_inQuery = -1;
		int findedLast_BroadCastHop = -1;
		int findedLast_footprintHop = -1;
		int hop = 0;
		int QueryName = -1;
		boolean endSearchBroadcast = false;
		boolean traceFoot = false;
		boolean finded = false;
		point now;
		point lastFoot;
		point lastBroadcast;
		ArrayList<point> RouteForLastFoot;
		ArrayList<point> RouteForLastBroadCast;
		public searchPackage(point what)
		{
			now = what;
		}
		
	}
	class point
	{
		
		
		public static int randomwalk_step =5;
		int x=0;
		int y=0;
		int upOrDown = 0; // -1下   0無  1上
		int leftOrRight = 0; // -1左   0無  1右 
		int sinktime_UD = -1; //boradcast 的時間(如果有的話)
		int sinktime_LR = -1; 
		int sinkid; //boradcast 的 sink 的id
		int sensorid; //此sensor的 id
		int sink_nearhere = 0; //用來參考sink是否在附近 0 =沒有 1=有
		int data_buffer[] = null; //0 = 沒有保留資料 1=有保留資料
		int data_bufferHopCount[] =null; //紀錄封包的hopcount數
		int popOut_Index = 0; //記錄buffer 裡下一個再滿出來的時候要被丟棄的資料;
		int BroadCast_hop_UD = -1;
		int BroadCast_hop_LR = -1;
		int eventTime = Integer.MAX_VALUE;
		
		int data_keep=0; // 有資料留在sensor裡面還沒傳 >1 , 沒有 =0
		point traceBroadCast_UD;  //broadcast 來源_上下
		point traceBroadCast_LR;  //broadcast 來源_左右
		point up,down,right,left;  //這邊是最遠的上下左右
		point topRight,topLeft,buttomRight,buttomLeft;  //這邊是最遠的右上 左上 右下 左下
		ArrayList<point> nearby = new ArrayList<point>();
		foot foot;
		
		point()
		{
			
			
		}	
		//以下是DDRP algorithm
		public void searchBy1(point[] p ,int checkTime ,int lastTime , String prefix)
		{
			int hop = 0;
			boolean find = false;
			point now = this;
			if(!find && now.foot != null)
			{
				while(!find && hop < Enviorment.randomwalk_step)
				{
					if(now.foot != null && now.foot.closeID!=-1) //本身有CLOSE
					{
						TOOL.printStep(now.sensorid +"有C" , prefix, "");
						if(now.foot.closetime == lastTime)
						{
							TOOL.printStep(this.sensorid +"找到了 sink " +now.foot.id +"由"+now.sensorid, prefix, "");
							//把資料丟出去
							this.data_keep = 0;
							now.data_keep=0;
							find = true;
							
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							if(hop>=0 && hop<10)
								Enviorment.hop10_1 ++;
							else if(hop>=10 && hop<20)
								Enviorment.hop20_1 ++;
							else if(hop>=20 && hop<30)
								Enviorment.hop30_1 ++;
							else if(hop>=30 && hop<40)
								Enviorment.hop40_1 ++;
							else if(hop>=40 && hop<50)
								Enviorment.hop50_1 ++;
							else if(hop>=50 && hop<60)
								Enviorment.hop60_1 ++;
							else if(hop>=60 && hop<70)
								Enviorment.hop70_1 ++;
							else if(hop>=70 && hop<80)
								Enviorment.hop80_1 ++;
							else if(hop>=80 && hop<90)
								Enviorment.hop90_1 ++;
							else if(hop>=90 && hop<100)
								Enviorment.hop100_1 ++;
							else if(hop>=100 && hop<1000)
								Enviorment.hop1000_1 ++;
							TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 1", prefix+"成功率.txt");
						}
						else
						{
							
							int last_close=now.foot.closetime;
							//確認自己的時間點不是最晚的closetime時間點
							point last_closeID=null;
							
								
							//紀錄周圍時間最晚的closetime和tracktime以及ID
							//一開始把close和track設成-1直到有找到才改成那個點的ID
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
									//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
								{
									last_close=next.foot.closetime;
									last_closeID = next;
								}
							}
							
							if(last_closeID!=null)
							{
								TOOL.printStep(now.sensorid +"往更晚的close ID = " +last_closeID.sensorid , prefix, "");
								now=last_closeID;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
								hop++;
								now.data_keep = 0;
							}
							else
							{
								
								TOOL.printStep(now.sensorid +"close 追丟了?" , prefix, "");
								break;
							}
						}
					}
					//下面是自己沒有CLOSE且有TRACK
					else if (now.foot != null && now.foot.closeID==-1 && now.foot.trackID!=-1)
					{
						TOOL.printStep(now.sensorid +"有T" , prefix, "");
						int last_track=now.foot.tracktime,last_close=-1;
						//確認自己的時間點不是最晚的時間點
						point last_trackID=null,last_closeID=null;
							
						//紀錄周圍時間最晚的closetime和tracktime以及ID
						//一開始把close和track設成-1直到有找到才改成那個點的ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							
							
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
							{
								last_close=next.foot.closetime;
								last_closeID = next;
							}
							if(next.foot != null && next.foot.trackID!=-1 && next.foot.tracktime<last_track)
							{
								last_track=next.foot.tracktime;
								last_trackID = next;
							}
						}
						if(last_closeID!=null)
						{
							TOOL.printStep(now.sensorid +"往更晚的close ID = " +last_closeID.sensorid , prefix, "");
							now=last_closeID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
						}
						else if(last_trackID!=null)
						{
							TOOL.printStep(now.sensorid +"往更早的track ID = " +last_trackID.sensorid , prefix, "");
							now=last_trackID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
						}
					}
					
					//我沒有TRACK也沒有CLOSE
					else 
					{
						TOOL.printStep(now.sensorid +"自己都沒有 開始看附近的" , prefix, "");
						//  System.out.println("第948行");
						int last_track=(int)1e6,last_close=-1;
						//確認自己的時間點不是最晚的時間點
						point last_trackID=null,last_closeID=null;
						
							
						//紀錄周圍時間最晚的closetime和tracktime以及ID
						//一開始把close和track設成-1直到有找到才改成那個點的ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
							{
								last_close=next.foot.closetime;
								last_closeID = next;
							}
							if(next.foot != null && next.foot.trackID!=-1 && next.foot.tracktime<last_track)
							{
								last_track=next.foot.tracktime;
								last_trackID = next;
							}
						}
						if(last_closeID!=null)
						{
							TOOL.printStep(now.sensorid +"找到C :"+last_closeID.sensorid  , prefix, "");
							this.foot.tracktime = 0;
							this.foot.trackID = last_closeID.sensorid;
							//自己不是TRACK也不是CLOSE，找到周遭有CLOSE，所以自己變成TRACK
							now=last_closeID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
							//我發出封包之後，要把周圍的SENSOR改成TRACK，TRACK時間要+1
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot == null)
									next.foot = new foot();
								
								if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
								{
									next.foot.trackID = last_closeID.sensorid;
									next.foot.tracktime = lastTime+1;
								}
							}
						}
						else if(last_trackID!=null)
						{
							TOOL.printStep(now.sensorid +"找到T :"+last_trackID.sensorid  , prefix, "");
							this.foot.tracktime = (last_trackID.foot.tracktime>=lastTime)?last_trackID.foot.tracktime+1:lastTime;
							this.foot.trackID = last_trackID.sensorid;
							//自己不是TRACK也不是CLOSE，找到周遭有TRACK，所以自己變成TRACK
							now=last_trackID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
							//我發出封包之後，要把周圍的SENSOR改成TRACK，TRACK時間要+1
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot == null)
									next.foot = new foot();
								if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
								{
									next.foot.trackID = last_trackID.sensorid;
									next.foot.tracktime = this.foot.tracktime+1;
								}
							}
						}
						else  //自己及附近都沒有track 與 close
						{
							Enviorment.success_rate = 0;
							Enviorment.success_count = 0;
							point now_node =now;
							
							
								//random walk (DDRP 的)
								now = now_node;
								long[] data = Enviorment.CountRouteSize;
								TOOL.printStep(now.sensorid +"自己及附近皆無 開始random ", prefix, "");
								for(int j=0;j<Enviorment.randomwalk_trailtimes;j++)
								{
									for(int k = 0 ;k < Enviorment.randomwalk_trailsteps ; k++)
									{
										int random = (int)(Math.random()*now.nearby.size());
										point next = now.nearby.get(random); 
										//TOOL.printStep(now.sensorid +"-> "+next.sensorid, prefix, "");
										now = next;
										//下面這個是random 時的total hop數計算 沒限定說要成功才會+
										Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
										hop++;
										if(now.foot.closeID!=-1 || now.foot.trackID!=-1)
										{
											TOOL.printStep(now.sensorid +"於Random中找到C或T hop= "+hop, prefix, "");
											
											Enviorment.success_rate++;
											break;
										}
										
									}
									if(now.foot.closeID!=-1 || now.foot.trackID!=-1)
									{
										
										break;
									}
								}
							double Ratio_of_success=Enviorment.success_rate;
							TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: "+Ratio_of_success, prefix+"成功率.txt");
							if(now.foot.closeID==-1 && now.foot.trackID ==-1)
							{
								
								break;
							}
						}
							
					}
				}
			}
			else 
			{
				this.data_keep=1;
			}
			
			if(find && now.foot != null)
			{
				if(now.foot.id == Enviorment.closest_sink)
					Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_1.ordinal()] += 1;
				System.out.printf("sensor %d(%d,%d) find %d,%d sink   hop: %d\n",this.sensorid,now.x,now.y,now.foot.x,now.foot.y,hop);
			}
				
			else 
				System.out.printf("sensor %d miss\n",this.sensorid);
		}

		//以下是Trail
		public void searchBy2(point[] p ,int checkTime ,int lastTime , String prefix)
		{
			int hop = 0; //這一次要傳的;
			boolean find = false;
			point now = this;
			
			if(!find && now.foot != null)
			{
				while(!find && hop < Enviorment.randomwalk_trailsteps)
				{
					if(now.foot != null && now.foot.closeID!=-1) //本身有CLOSE
					{
						if(now.foot.closetime == lastTime)
						{
							//把資料丟出去
							this.data_keep = 0;
							now.data_keep=0;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
							find = true;
						}
						else
						{
							int last_close=-1;
							//確認自己的時間點不是最晚的closetime時間點
							//紀錄周圍時間最晚的closetime以及ID
							//一開始把close設成-1直到有找到才改成那個點的ID
							point last_closeID=null;
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
									//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
								{
									hop++;
									find = true;  //只是找到了足跡
									last_close=next.foot.closetime;
									last_closeID = next;
								}
							}
							now = last_closeID;
							
							while(now.foot.closetime != lastTime)  //重複上面for的動作 直到現在查詢封包的時間變成了最晚的時間
							{
								for(int i = 0 ; i < now.nearby.size() ; i++)
								{
									point next = now.nearby.get(i);
									if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
										//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
									{
										Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
										hop++;
										last_close=next.foot.closetime;
										last_closeID = next;
									}
								}
								now = last_closeID;
							}
							
							
						}
						if(now.foot.id == Enviorment.closest_sink)
							Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_2.ordinal()] += 1;
						if(hop>=0 && hop<10)
							Enviorment.hop10_2 ++;
						else if(hop>=10 && hop<20)
							Enviorment.hop20_2 ++;
						else if(hop>=20 && hop<30)
							Enviorment.hop30_2 ++;
						else if(hop>=30 && hop<40)
							Enviorment.hop40_2 ++;
						else if(hop>=40 && hop<50)
							Enviorment.hop50_2 ++;
						else if(hop>=50 && hop<60)
							Enviorment.hop60_2 ++;
						else if(hop>=60 && hop<70)
							Enviorment.hop70_2 ++;
						else if(hop>=70 && hop<80)
							Enviorment.hop80_2 ++;
						else if(hop>=80 && hop<90)
							Enviorment.hop90_2 ++;
						else if(hop>=90&& hop<100)
							Enviorment.hop100_2 ++;
						else if(hop>=100 && hop<1000)
							Enviorment.hop1000_2 ++;
						
						TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 1", prefix+"成功率.txt");
						
					}

					//我沒有CLOSE
					else 
					{
						int last_close=-1;
						//確認自己的時間點不是最晚的時間點
						point last_closeID=null;
						
							
						//紀錄周圍時間最晚的closetime以及ID
						//一開始把close設成-1直到有找到才改成那個點的ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
							{
								last_close=next.foot.closetime;
								last_closeID = next;
							}
						}
						
						if(last_closeID==null)
						{
							System.out.printf("隨機走去 %d\n",now.sensorid);
							Enviorment.success_rate = 0;
							Enviorment.success_count = 0;
							point now_node =now;
							
							
								//random walk
								now = now_node;
								hop++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
									for(int k = 0 ;k < Enviorment.randomwalk_trailsteps ; k++)
									{
										int random = (int)(Math.random()*now.nearby.size());
										now = now.nearby.get(random);
										
										if(now.foot.closeID!=-1)
										{
											find = true;
											Enviorment.success_rate++;
											System.out.printf("sensor %d find %d sink\n",this.sensorid,now.foot.id);
											break;
										}
										Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
										hop++;
									}
								
								if(find && now.foot.closetime != lastTime)
								{
									
									while(now.foot.closetime != lastTime)  //重複上面for的動作 直到現在查詢封包的時間變成了最晚的時間
									{
										last_close = now.foot.closetime;
										for(int i = 0 ; i < now.nearby.size() ; i++)
										{
											point next = now.nearby.get(i);
											if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
												//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
											{
												hop++;
												Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
												last_close=next.foot.closetime;
												last_closeID = next;
											}
										}
										if(last_close == now.foot.closetime)
											System.out.println("旁邊找不到更晚的close");
										if(last_closeID != null)
											now = last_closeID;
										else
										{
											System.out.println("close 追丟了???");
											break;
										}
									}
								}
								
								if (find) 
								{
									if(now.foot.id == Enviorment.closest_sink)
										Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_2.ordinal()] += 1;
									if (hop >= 0 && hop < 10)
										Enviorment.hop10_2++;
									else if (hop >= 10 && hop < 20)
										Enviorment.hop20_2++;
									else if (hop >= 20 && hop < 30)
										Enviorment.hop30_2++;
									else if (hop >= 30 && hop < 40)
										Enviorment.hop40_2++;
									else if (hop >= 40 && hop < 50)
										Enviorment.hop50_2++;
									else if (hop >= 50 && hop < 60)
										Enviorment.hop60_2++;
									else if (hop >= 60 && hop < 70)
										Enviorment.hop70_2++;
									else if (hop >= 70 && hop < 80)
										Enviorment.hop80_2++;
									else if (hop >= 80 && hop < 90)
										Enviorment.hop90_2++;
									else if (hop >= 90 && hop < 100)
										Enviorment.hop100_2++;
									else if (hop >= 100 && hop < 1000)
										Enviorment.hop1000_2++;
								}
							double Ratio_of_success=Enviorment.success_rate;
							TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: "+Ratio_of_success, prefix+"成功率.txt");
							break;
						}
						
						else if(last_closeID !=null) //自己一開始沒有 但周遭有人有 close
						{
							
							if(last_closeID.foot.closetime != lastTime)
							{
								while(now.foot.closetime != lastTime)  //重複上面for的動作 直到現在查詢封包的時間變成了最晚的時間
								{
									for(int i = 0 ; i < now.nearby.size() ; i++)
									{
										point next = now.nearby.get(i);
										if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
											//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
										{
											Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
											hop++;
											last_close=next.foot.closetime;
											last_closeID = next;
										}
									}
									now = last_closeID;
								}
							}
							if(now.foot.id == Enviorment.closest_sink)
								Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_2.ordinal()] += 1;
							if(hop>=0 && hop<10)
								Enviorment.hop10_2 ++;
							else if(hop>=10 && hop<20)
								Enviorment.hop20_2 ++;
							else if(hop>=20 && hop<30)
								Enviorment.hop30_2 ++;
							else if(hop>=30 && hop<40)
								Enviorment.hop40_2 ++;
							else if(hop>=40 && hop<50)
								Enviorment.hop50_2 ++;
							else if(hop>=50 && hop<60)
								Enviorment.hop60_2 ++;
							else if(hop>=60 && hop<70)
								Enviorment.hop70_2 ++;
							else if(hop>=70 && hop<80)
								Enviorment.hop80_2 ++;
							else if(hop>=80 && hop<90)
								Enviorment.hop90_2 ++;
							else if(hop>=90 && hop<100)
								Enviorment.hop100_2 ++;
							
							find = true;
							TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 1", prefix+"成功率.txt");
							break;
						}
							
					}
				}
			}
		}
			
			
		//以下是HRDG 
		public void searchBy3(point[] p ,int checkTime ,int lastTime , String prefix)
		{
			int hop = 0;
			boolean find = false;
			point now = this;
			if(now.data_keep == 0)
				now.data_keep = 1;
			
			this.data_buffer[this.popOut_Index%Enviorment.bufferSize] =  this.sensorid;
			this.data_bufferHopCount[this.popOut_Index%Enviorment.bufferSize] = 0;
			this.popOut_Index++;
			
			ArrayList<point> route = new ArrayList<>();
			route.add(now);
			
			if(!find && now.foot != null)
			{
				while(!find && hop < Integer.MAX_VALUE) //
				{

					if(now.foot != null && now.foot.closeID!=-1) //本身有CLOSE
					{
						
						if(now.foot.closetime == lastTime && now.foot.isInnerClose)
						{
							if(this.eventTime == Integer.MAX_VALUE)
								this.eventTime = lastTime; //這裡代表了把資料傳到sink
							StringBuilder r = new StringBuilder();
							for(int i = 0 ; i < route.size() ; i++)
								r.append(route.get(i).sensorid+" > ");
							
							TOOL.printStep(this.sensorid +"找到了 sink " +now.foot.id +"由"+now.sensorid, prefix, "");
							TOOL.printStep("route: " +r.toString(), prefix, "");
							//System.out.println(now.data_keep);
							now.foot.near.count += now.data_keep;
							if(now.foot.id == Enviorment.closest_sink)
								Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_3.ordinal()] += 1;
							//把資料丟出去
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								now.data_buffer[temp1] = 0;
								now.data_bufferHopCount[temp1]++;
								
								
								if(now.data_bufferHopCount[temp1]>=0 && now.data_bufferHopCount[temp1]<10)
									Enviorment.hop10_3 ++;
								else if(now.data_bufferHopCount[temp1]>=10 && now.data_bufferHopCount[temp1]<20)
									Enviorment.hop20_3 ++;
								else if(now.data_bufferHopCount[temp1]>=20 && now.data_bufferHopCount[temp1]<30)
									Enviorment.hop30_3 ++;
								else if(now.data_bufferHopCount[temp1]>=30 && now.data_bufferHopCount[temp1]<40)
									Enviorment.hop40_3 ++;
								else if(now.data_bufferHopCount[temp1]>=40 && now.data_bufferHopCount[temp1]<50)
									Enviorment.hop50_3 ++;
								else if(now.data_bufferHopCount[temp1]>=50 && now.data_bufferHopCount[temp1]<60)
									Enviorment.hop60_3 ++;
								else if(now.data_bufferHopCount[temp1]>=60 && now.data_bufferHopCount[temp1]<70)
									Enviorment.hop70_3 ++;
								else if(now.data_bufferHopCount[temp1]>=70 && now.data_bufferHopCount[temp1]<80)
									Enviorment.hop80_3 ++;
								else if(now.data_bufferHopCount[temp1]>=80 && now.data_bufferHopCount[temp1]<90)
									Enviorment.hop90_3 ++;
								else if(now.data_bufferHopCount[temp1]>=90)
									Enviorment.hop100_3 ++;
								
								
								//我有close 而且close 的時間和event發生的時間一樣，把HOP COUNT加一
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							find = true;
						}
						else if(now.foot.closetime == lastTime && !now.foot.isInnerClose)
						{
							//有最晚的close 但是在外圈
							
							point finded_inner_close = null;
							
							for(int i = 0 ; i < now.nearby.size() ; i++)
								if(now.nearby.get(i).foot.isInnerClose && now.nearby.get(i).foot.closetime == lastTime)
									finded_inner_close = now.nearby.get(i);
							
							route.add(finded_inner_close);
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								finded_inner_close.data_buffer[finded_inner_close.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								finded_inner_close.data_bufferHopCount[finded_inner_close.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								finded_inner_close.popOut_Index++;
								
								if (finded_inner_close.data_keep < 1024)  finded_inner_close.data_keep++;  
								
								
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							now=finded_inner_close;
							hop++;
						}
						else //有close 但不是最晚的
						{
							TOOL.printStep(now.sensorid +"找到了 不是最晚的close 開始往更晚的close移動" , prefix, "");
							int last_track=now.foot.closetime,last_close_i=now.foot.closetime,last_close_o=now.foot.closetime;
							//確認自己的時間點不是最晚的closetime時間點
							point last_trackID=null,last_closeID_i=null,last_closeID_o=null;
							
								
							//紀錄周圍時間最晚的closetime(inner & outer)和tracktime以及ID
							//一開始把close和track設成-1直到有找到才改成那個點的ID
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot != null && next.foot.closeID!=-1 && ((next.foot.closetime>last_close_i && next.foot.isInnerClose) || (!now.foot.isInnerClose && next.foot.closetime==last_close_i && next.foot.isInnerClose)))
									//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
								{
									last_close_i=next.foot.closetime;
									last_closeID_i = next;
								}
								if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close_o && !next.foot.isInnerClose)
									//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
								{
									last_close_o=next.foot.closetime;
									last_closeID_o = next;
								}
								if(next.foot != null && next.foot.trackID!=-1 && next.foot.tracktime<last_track)
								{
									last_track=next.foot.tracktime;
									last_trackID = next;
								}
							}
							if(last_closeID_i!=null && last_close_i >= last_close_o)
							{
								TOOL.printStep(now.sensorid +"->(i)"+last_closeID_i.sensorid , prefix, "");
								route.add(last_closeID_i);
								for(int temp1=0;temp1<now.data_keep;temp1++)
								{
									now.data_bufferHopCount[temp1]++;
									Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
									last_closeID_i.data_buffer[last_closeID_i.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
									last_closeID_i.data_bufferHopCount[last_closeID_i.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
									last_closeID_i.popOut_Index++;
									
									if (last_closeID_i.data_keep < 1024)  last_closeID_i.data_keep++;  
									
									//我有close 而且close 的時間是最晚的，把資料丟出去。
									
								}
								now.data_keep = 0;
								now.popOut_Index = 0;
								now=last_closeID_i;
								hop++;
							}
							else if(last_closeID_o!=null && last_close_i < last_close_o)
							{
								TOOL.printStep(now.sensorid +"->(o)"+last_closeID_o.sensorid , prefix, "");
								route.add(last_closeID_o);
								for(int temp1=0;temp1<now.data_keep;temp1++)
								{
									now.data_bufferHopCount[temp1]++;
									Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
									last_closeID_o.data_buffer[last_closeID_o.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
									last_closeID_o.data_bufferHopCount[last_closeID_o.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
									last_closeID_o.popOut_Index++;
									
									if (last_closeID_i.data_keep < 1024)  last_closeID_o.data_keep++;  
									
									
									
								}
								now.data_keep = 0;
								now.popOut_Index = 0;
								now=last_closeID_o;
								hop++;
							}
							else if(last_trackID!=null)
							{
								TOOL.printStep(now.sensorid +"(trackID?)->"+last_trackID.sensorid , prefix, "");
								route.add(last_trackID);
								for(int temp1=0;temp1<now.data_keep;temp1++)
								{
									
									now.data_bufferHopCount[temp1]++;
									Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
									last_trackID.data_buffer[last_trackID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
									last_trackID.data_bufferHopCount[last_trackID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
									last_trackID.popOut_Index++;
									
									if (last_trackID.data_keep < 1024)  last_trackID.data_keep++;  
									
									//我有close 但是我有找到更早的TRACK時間，且沒有找到更晚的close。
									
								}
								now.data_keep = 0;
								now.popOut_Index = 0;
								now=last_trackID;
								hop++;
							}
							else
								System.out.println("有close 還追不到?");
						}
					}
					//下面是自己沒有CLOSE且有TRACK
					else if (now.foot != null && now.foot.closeID==-1 && now.foot.trackID!=-1)
					{
						TOOL.printStep(now.sensorid +"沒C有T" , prefix, "");
						int last_track=now.foot.tracktime,last_close=-1;
						//確認自己的時間點不是最晚的時間點
						point last_trackID=null,last_closeID=null;
							
						//紀錄周圍時間最晚的closetime和tracktime以及ID
						//一開始把close和track設成-1直到有找到才改成那個點的ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							
							
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
							{
								last_close=next.foot.closetime;
								last_closeID = next;
							}
							if(next.foot != null && next.foot.trackID!=-1 && next.foot.tracktime<last_track)
							{
								last_track=next.foot.tracktime;
								last_trackID = next;
							}
						}
						if(last_closeID!=null)
						{
							if(this.eventTime == Integer.MAX_VALUE)
								this.eventTime = lastTime; //這裡代表了把資料傳到附近的close
							TOOL.printStep(now.sensorid +"找到C:"+last_closeID.sensorid , prefix, "");
							route.add(last_closeID);
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_closeID.data_buffer[last_closeID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_closeID.data_bufferHopCount[last_closeID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_closeID.popOut_Index++;
								
								if (last_closeID.data_keep < 1024)  last_closeID.data_keep++;
								//有找到時間較晚的CLOSE，把資料丟給他
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							now=last_closeID;
							hop++;										
						}
						else if(last_trackID!=null)
						{
							if(this.eventTime == Integer.MAX_VALUE)
								this.eventTime = lastTime; //這裡代表了把資料傳到附近的track
							route.add(last_trackID);
							TOOL.printStep(now.sensorid +"找到T:"+last_trackID.sensorid , prefix, "");
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_trackID.data_buffer[last_trackID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_trackID.data_bufferHopCount[last_trackID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_trackID.popOut_Index++;
								
								if (last_trackID.data_keep < 1024)  last_trackID.data_keep++;
								
								//找到時間較早的TRACK
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							now=last_trackID;
							hop++;
						}
						else //track 斷了
						{
							break;
						}
						
					}
					
					//我沒有TRACK也沒有CLOSE
					else 
					{
						TOOL.printStep(now.sensorid +"什麼也沒有", prefix, "");
						int last_track=(int)1e6,last_close=-1;
						//確認自己的時間點不是最晚的時間點
						point last_trackID=null,last_closeID=null;
						
							
						//紀錄周圍時間最晚的closetime和tracktime以及ID
						//一開始把close和track設成-1直到有找到才改成那個點的ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
							{
								last_close=next.foot.closetime;
								last_closeID = next;
							}
							if(next.foot != null && next.foot.trackID!=-1 && next.foot.tracktime<last_track)
							{
								last_track=next.foot.tracktime;
								last_trackID = next;
							}
						}
						if(last_closeID!=null)
						{
							if(this.eventTime == Integer.MAX_VALUE)
								this.eventTime = lastTime; //這裡代表了把資料傳到附近的close
							route.add(last_closeID);
							TOOL.printStep(now.sensorid +"發現C了 由"+last_closeID.sensorid+"擁有", prefix, "");
							now.foot.tracktime = 0;
							now.foot.trackID = last_closeID.sensorid;
							TOOL.printStep("時間:"+lastTime+" "+now.sensorid+"找到C 自己變T ", "By_3", "Track");
							//自己不是TRACK也不是CLOSE，找到周遭有CLOSE，所以自己變成TRACK
							
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_closeID.data_buffer[last_closeID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_closeID.data_bufferHopCount[last_closeID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_closeID.popOut_Index++;
								
								if (last_closeID.data_keep < 1024)  last_closeID.data_keep++;
								//有找到時間較晚的CLOSE，把資料丟給他
								
							}
							
							now.data_keep = 0;
							now.popOut_Index = 0;
							
							
//							//並且通知周遭自己變成track
							//我發出封包之後，要把周圍的SENSOR改成TRACK，TRACK時間要+1
//							for(int i = 0 ; i < now.nearby.size() ; i++)
//							{
//								point next = now.nearby.get(i);
//								
//								if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
//								{
//									TOOL.printStep("時間:"+lastTime+" "+next.sensorid+"被"+now.sensorid+"通知", "By_3", "Track");
//									next.foot.trackID = now.sensorid;
//									next.foot.tracktime = now.foot.tracktime+1;
//								}
//							}
							
							now=last_closeID;
							hop++;
							
							
						}
						else if(last_trackID!=null)
						{
							if(this.eventTime == Integer.MAX_VALUE)
								this.eventTime = lastTime; //這裡代表了把資料傳到附近的track
							route.add(last_trackID);
							TOOL.printStep(now.sensorid +"發現T了 由"+last_trackID.sensorid+"擁有", prefix, "");
							now.foot.tracktime = last_trackID.foot.tracktime+1;
							now.foot.trackID = last_trackID.sensorid;
							TOOL.printStep("時間:"+lastTime+" "+now.sensorid+"找到T 自己變T ", "By_3", "Track");
							//自己不是TRACK也不是CLOSE，找到周遭有TRACK，所以自己變成TRACK
							
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_trackID.data_buffer[last_trackID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_trackID.data_bufferHopCount[last_trackID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_trackID.popOut_Index++;
								
								if (last_trackID.data_keep < 1024)  last_trackID.data_keep++;
								//有找到時間較晚的CLOSE，把資料丟給他
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							
							

							//我發出封包之後，要把周圍的SENSOR改成TRACK，TRACK時間要+1
//							for(int i = 0 ; i < now.nearby.size() ; i++)
//							{
//								point next = now.nearby.get(i);
//								if(next.foot == null)
//									next.foot = new foot();
//								if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
//								{
//									TOOL.printStep("時間:"+lastTime+" "+next.sensorid+"被"+now.sensorid+"通知", "By_3", "Track");
//									next.foot.trackID = now.sensorid;
//									next.foot.tracktime = now.foot.tracktime+2;
//								}
//							}
							
							now=last_trackID;
							hop++;
							
						}
						else
						{
							break;
						}
							
					}
				}
			}
			else 
			{

				this.data_buffer[this.sensorid] =1;
				this.data_keep=1;
			}
			
			if(find && now.foot != null)
			{
				System.out.printf("sensor %d(%d,%d) find %d,%d sink   hop: %d\n",this.sensorid,now.x,now.y,now.foot.x,now.foot.y,hop);
			
			}
				
			else 
			{
				System.out.printf("sensor %d miss\n",this.sensorid);
			}
		}

		public void searchByRandomWalk(point[] p ,int checkTime ,int lastTime , String prefix)
		{
			int hop = 0;
			boolean find = false;
			point now = this;
			
			if(!find)
			{
				while(!find && hop < Enviorment.randomwalk_step)
				{
					if(now.foot != null && now.foot.closeID!=-1 && now.foot.closetime == lastTime) //本身有CLOSE
					{
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_RandomWalk.ordinal()] += 1;
							//把資料丟出去
							this.data_keep = 0;
							now.data_keep=0;
							find = true;
							if(now.foot.id == Enviorment.closest_sink)
								Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_RandomWalk.ordinal()] += 1;
							Enviorment.RandomWalkhop10++;
							TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 1", prefix+"成功率.txt");
						
					}

					//我沒有CLOSE
					else 
					{
						int last_close=-1;
						//確認自己的時間點不是最晚的時間點
						point last_closeID=null;
						//紀錄周圍時間最晚的closetime以及ID
						//一開始把close設成-1直到有找到才改成那個點的ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime ==lastTime)
								//我周圍某個人有FOOT，且他含有CLOSE封包，而且他的CLOSE時間是我目前看到的最晚的
							{
								last_close=next.foot.closetime;
								last_closeID = next;
								
							}
						}
						
						if(last_closeID==null)
						{
							System.out.printf("隨機走去 ~\n",now.sensorid);
							Enviorment.success_rate = 0;
							Enviorment.success_count = 0;
							point now_node =now;
							int HopOf100=0;
							
								//random walk
								now = now_node;
								for(int k = 0 ;k < Enviorment.randomwalk_step ; k++)
								{
									int random = (int)(Math.random()*now.nearby.size());
									now = now.nearby.get(random);
									hop++;
									Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_RandomWalk.ordinal()] += 1;
									if(now.foot.closeID!=-1 && now.foot.closetime == lastTime)
									{
										HopOf100 += hop;
										find = true;
										Enviorment.success_rate++;
										System.out.printf("sensor %d find %d sink(%d %d)\n",this.sensorid,now.foot.id,now.foot.x,now.foot.y);
										System.out.println((int)HopOf100);
										if(now.foot.id == Enviorment.closest_sink)
											Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_RandomWalk.ordinal()] += 1;
										if(HopOf100<10)
											Enviorment.RandomWalkhop10++;
										else if(HopOf100>=10 && HopOf100<20)
											Enviorment.RandomWalkhop20++;
										else if(HopOf100>=20 && HopOf100<30)
											Enviorment.RandomWalkhop30++;
										else if(HopOf100>=30 && HopOf100<40)
											Enviorment.RandomWalkhop40++;
										else if(HopOf100>=40 && HopOf100<50)
											Enviorment.RandomWalkhop50++;
										else if(HopOf100>=50 && HopOf100<60)
											Enviorment.RandomWalkhop60++;
										else if(HopOf100>=60 && HopOf100<70)
											Enviorment.RandomWalkhop70++;
										else if(HopOf100>=70 && HopOf100<80)
											Enviorment.RandomWalkhop80++;
										else if(HopOf100>=80 && HopOf100<90)
											Enviorment.RandomWalkhop90++;
										else if(HopOf100>=90 && HopOf100<100)
											Enviorment.RandomWalkhop100++;
										else if (HopOf100>=100 && HopOf100<1000)
											Enviorment.RandomWalkhop1000++;
										break;
									}
									
									
								}
								
							
							double Ratio_of_success=Enviorment.success_rate;
							
							TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: "+Ratio_of_success, prefix+"成功率.txt");
							break;
						}
						
						else if(last_closeID !=null)
						{
							Enviorment.RandomWalkhop10++;
							find = true;
							TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 1", prefix+"成功率.txt");
							break;
						}
							
					}
				}
			}
		}
		
		public void searchByWE_andTime(int checkTime ,int lastTime , String prefix,int QueryName , int RouteSizeName ,int closestName) throws IOException
		{
			
			searchPackage searchW = new searchPackage(this);
			searchPackage searchE = new searchPackage(this);
			searchW.QueryName = QueryName;
			searchE.QueryName = QueryName;
			while(searchE.hop <  Enviorment.randomwalk_step && !searchE.finded)
			{
				
				if(checkWE_andTime(searchE,lastTime,"'右'邊的",prefix,searchE.hop,true,RouteSizeName)) //你之前要的那些條件 基本上都在裡面 true 代表此次裡面沒有做移動
				{
					if(searchE.now.right != null) 
					{
						
						TOOL.printStep("'右'邊的封包往右邊追蹤 ->" + "步數:"+ searchE.hop  +" "+searchE.now.right.sensorid+"("+searchE.now.right.x+","+searchE.now.right.y+")","'右'邊的",prefix);
						if(searchE.findedLast_BroadCastTime != -1 && searchE.RouteForLastBroadCast.get(searchE.RouteForLastBroadCast.size()-1) != searchE.now )
						{
							searchE.RouteForLastBroadCast.add(searchE.now);
						}
						
						if(searchE.findedLast_footprintTime != -1 && searchE.RouteForLastFoot.get(searchE.RouteForLastFoot.size()-1) != searchE.now)
						{
							searchE.RouteForLastFoot.add(searchE.now);
						}

						searchE.now = searchE.now.right;
					}
					else if(searchE.endSearchBroadcast ==false)
					{
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
						
						TOOL.printStep("'右'邊的"+"封包 發現右邊沒人 停止向右邊追蹤","'右'邊的",prefix);
						if(searchE.findedLast_BroadCastTime > -1 && searchE.findedLast_BroadCastTime >= searchE.findedLast_footprintTime)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_BroadCastHop)*searchE.hop;
							int lastStep = searchE.RouteForLastBroadCast.size()-1;
							while(searchE.now != searchE.lastBroadcast)
							{
								TOOL.printStep("'右'邊的"+"封包 往最後發現的broadcast移動  ->"+searchE.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchE.hop,"'右'邊的",prefix);
								searchE.now = searchE.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
								
							}
							
						}
						else if(searchE.findedLast_footprintTime > -1)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_footprintHop)*searchE.hop;
							int lastStep = searchE.RouteForLastFoot.size()-1;
							while(searchE.now != searchE.lastFoot)
							{
								TOOL.printStep("'右'邊的"+"封包 往最後發現的footprint移動  ->"+searchE.RouteForLastFoot.get(lastStep).sensorid,"'右'邊的",prefix);
								searchE.now = searchE.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
								
							}
							searchE.traceFoot = true;
						}
						else
						{
//							Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize *searchE.hop;
							break;
						}
						searchE.endSearchBroadcast = true;
					}
				}
				else if(searchE.finded && !searchE.endSearchBroadcast) //在需要折返前找到(路徑上直接看到最後的foot)
				{
					
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
					Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchE.hop;
				}
				searchE.hop++;
			}	
				
			while(searchW.hop <  Enviorment.randomwalk_step && !searchW.finded)
			{
				
				if(checkWE_andTime(searchW,lastTime,"'左'邊的",prefix,searchW.hop,true,RouteSizeName))
				{
					if(searchW.now.left != null)
					{
						
						if(searchW.findedLast_BroadCastTime != -1 && searchW.RouteForLastBroadCast.get(searchW.RouteForLastBroadCast.size()-1) != searchW.now )
						{
							searchW.RouteForLastBroadCast.add(searchW.now);
						}
						
						if(searchW.findedLast_footprintTime != -1 && searchW.RouteForLastFoot.get(searchW.RouteForLastFoot.size()-1) != searchW.now)
						{
							searchW.RouteForLastFoot.add(searchW.now);
						}
						
						TOOL.printStep("'左'邊的"+"封包往'左'邊追蹤  ->" + "步數:" + searchW.hop+" "+searchW.now.left.sensorid+"("+searchW.now.left.x+","+searchW.now.left.y+")","'左'邊的",prefix);
						searchW.now = searchW.now.left;
					}
					else if(searchW.endSearchBroadcast == false)
					{
						searchW.endSearchBroadcast = true;
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
						TOOL.printStep("Query+"+TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize),"'左'邊的",prefix);
						TOOL.printStep("'左'邊的"+"封包 發現左邊沒人 停止向左邊追蹤","'左'邊的",prefix);
						
						if(searchW.findedLast_BroadCastTime > -1 && searchW.findedLast_BroadCastTime >= searchW.findedLast_footprintTime)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_BroadCastHop)*searchW.hop;
							TOOL.printStep("Route+"+searchW.findedLast_BroadCastHop,"'左'邊的",prefix);
							int lastStep = searchW.RouteForLastBroadCast.size()-1;
							while(searchW.now != searchW.lastBroadcast)
							{
								TOOL.printStep("'左'邊的"+"封包 往最後發現的broadcast移動  -> "+searchW.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchW.hop,"'左'邊的",prefix);
								searchW.now = searchW.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
							}
						}
						else if(searchW.findedLast_footprintTime > -1)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop;
							TOOL.printStep("Route+"+(Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop,"'左'邊的",prefix);
							int lastStep = searchW.RouteForLastFoot.size()-1;
							while(searchW.now != searchW.lastFoot)
							{
								TOOL.printStep("'左'邊的"+"封包 往最後發現的footprint移動  ->"+searchW.RouteForLastFoot.get(lastStep).sensorid,"'左'邊的",prefix);
								searchW.now = searchW.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
							}
							searchW.traceFoot = true;
						}
						else
						{
							Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
							Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchW.hop;
							break;
						}
						
					}
					else if(searchW.finded && !searchW.endSearchBroadcast) //在需要折返前找到(路徑上直接看到最後的foot)
					{
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
					
						Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchW.hop;
					
					}
				}
				searchW.hop++;
					
			}
			
			int k = Enviorment.totalHop[QueryName];
			int truehop = -1;
			int Rtime = TOOL.max(searchE.findedLast_BroadCastTime_inQuery,searchE.findedLast_footprintTime_inQuery);
			int Ltime = TOOL.max(searchW.findedLast_BroadCastTime_inQuery,searchW.findedLast_footprintTime_inQuery);
			
			if(Rtime >	Ltime)
				//選擇用右邊的
			{
				
				if(searchE.findedLast_BroadCastTime_inQuery > searchE.findedLast_footprintTime_inQuery) //右邊是折返回Guide
				{
//					truehop = searchE.hop-searchE.RouteForLastBroadCast.size();
					
				}
				else //右邊是折返回路上發現的最後個時間的foot
				{
//					truehop = searchE.hop-searchE.RouteForLastFoot.size();
					
				}
				TOOL.printStep("'右'邊的封包往追蹤到了位於("+searchE.now.foot.x+","+searchE.now.foot.y+")的sink,使用hop 數:"+searchE.hop,"'右'邊的",prefix);
				
				if(Enviorment.closest_sink == searchE.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				TOOL.printStep("'右'邊的封包往追蹤到了","'左'邊的",prefix);
				TOOL.printStep("sink ID =" + searchE.now.foot.id,"'右'邊的" , prefix);
			}
			else if(Rtime <	Ltime)
			{
				if(searchW.findedLast_BroadCastTime_inQuery > searchW.findedLast_footprintTime_inQuery) 
				{
//					truehop = searchW.hop-searchW.RouteForLastBroadCast.size();
				}
				else 
				{
//					truehop = searchW.hop-searchW.RouteForLastFoot.size();
				}
				
				TOOL.printStep("'左'邊的封包往追蹤到了位於("+searchW.now.foot.x+","+searchW.now.foot.y+")的sink,使用hop 數:"+truehop,"'左'邊的",prefix);
				if(Enviorment.closest_sink == searchW.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				
				TOOL.printStep("'左'邊的封包往追蹤到了","'右'邊的",prefix);
				TOOL.printStep("sink ID =" + searchW.now.foot.id,"'左'邊的" , prefix);
				TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 1", prefix+"成功率.txt");
			}
			else if(Rtime == Ltime)
			{
				int a = Integer.MAX_VALUE,b = Integer.MAX_VALUE;
				
				if(searchE.findedLast_BroadCastTime_inQuery >= searchE.findedLast_footprintTime_inQuery)
					a = searchE.findedLast_BroadCastHop;
				else
					a = searchE.findedLast_footprintHop;
				
				if(searchW.findedLast_BroadCastTime_inQuery >= searchW.findedLast_footprintTime_inQuery)
					b = searchW.findedLast_BroadCastHop;
				else
					b = searchW.findedLast_footprintHop;
				if(a<b)
				{
					
					if(searchE.findedLast_BroadCastTime_inQuery >= searchE.findedLast_footprintTime_inQuery)
					{
//						truehop = searchE.hop-searchE.RouteForLastBroadCast.size();
					}
					else
					{
//						truehop = searchE.hop-searchE.RouteForLastFoot.size();
					}
					
				}
				else if(b < a) 
				{
					if(searchW.findedLast_BroadCastTime_inQuery >= searchW.findedLast_footprintTime_inQuery)
						truehop = searchW.hop-searchW.RouteForLastBroadCast.size();
					else
						truehop = searchW.hop-searchW.RouteForLastFoot.size();
					
				}
				else if( a==b  ) //自己
				{
//					truehop = searchE.hop;
				}
			}
			else
			{
				Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
				Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
				
				TOOL.printStep("hop數超過"+searchE.hop,"'左'邊的",prefix);
				TOOL.printStep("hop數超過"+searchW.hop,"'右'邊的",prefix);
				TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 0", prefix+"成功率.txt");
			}
			int hop_E = TOOL.getTrueHop(searchE, lastTime);
			int hop_W = TOOL.getTrueHop(searchW, lastTime);
			
			
			
			truehop = (hop_E>hop_W) ? hop_W : hop_E; 	
				
			Enviorment.totalHop[QueryName] += truehop;
				
			if(searchE.finded||searchW.finded)
			{
				if(prefix.equals("By_Time"))
				{
					if(truehop<=10)
						Enviorment.Timehop10++;
					else if(truehop>10 && truehop<=20)
						Enviorment.Timehop20++;
					else if(truehop>20 && truehop<=30)
						Enviorment.Timehop30++;
					else if(truehop>30 && truehop<=40)
						Enviorment.Timehop40++;
					else if(truehop>40 && truehop<=50)
						Enviorment.Timehop50++;
					else if(truehop>50 && truehop<=60)
						Enviorment.Timehop60++;
					else if(truehop>60 && truehop<=70)
						Enviorment.Timehop70++;
					else if(truehop>70 && truehop<=80)
						Enviorment.Timehop80++;
					else if(truehop>80 && truehop<=90)
						Enviorment.Timehop90++;
					else if(truehop>90)
						Enviorment.Timehop100++;
				}
				else 
				{
					TOOL.printStep("totalHop"+truehop,"HOP",prefix);
					if(truehop<=10)
						Enviorment.Xhop10++;
					else if(truehop>10 && truehop<=20)
						Enviorment.Xhop20++;
					else if(truehop>20 && truehop<=30)
						Enviorment.Xhop30++;
					else if(truehop>30 && truehop<=40)
						Enviorment.Xhop40++;
					else if(truehop>40 && truehop<=50)
						Enviorment.Xhop50++;
					else if(truehop>50 && truehop<=60)
						Enviorment.Xhop60++;
					else if(truehop>60 && truehop<=70)
						Enviorment.Xhop70++;
					else if(truehop>70 && truehop<=80)
						Enviorment.Xhop80++;
					else if(truehop>80 && truehop<=90)
						Enviorment.Xhop90++;
					else if(truehop>90)
						Enviorment.Xhop100++;
				}
			}
		}
		
		//給xy 用
		public void searchXY(int checkTime , int lastTime , String prefix,int QueryName , int RouteSizeName ,int closestName) throws IOException
		{
			// TODO 自動產生的方法 Stub
			
			searchPackage searchW = new searchPackage(this);
			searchPackage searchE = new searchPackage(this);
			searchPackage searchN = new searchPackage(this);
			searchPackage searchS = new searchPackage(this);
			searchW.QueryName = QueryName;
			searchE.QueryName = QueryName;
			searchN.QueryName = QueryName;
			searchS.QueryName = QueryName;
			
			while(searchE.hop <  Enviorment.randomwalk_step && !searchE.finded)
			{
				
				if(checkWE_andTime(searchE,lastTime,"'右'邊的",prefix,searchE.hop,true,RouteSizeName)) //你之前要的那些條件 基本上都在裡面 true 代表此次裡面沒有做移動
				{
					if(searchE.now.right != null)
					{
						
						TOOL.printStep("'右'邊的封包往右邊追蹤 ->" + "步數:"+ searchE.hop  +" "+searchE.now.right.sensorid+"("+searchE.now.right.x+","+searchE.now.right.y+")","'右'邊的",prefix);
						if(searchE.findedLast_BroadCastTime != -1 && searchE.RouteForLastBroadCast.get(searchE.RouteForLastBroadCast.size()-1) != searchE.now )
						{
							searchE.RouteForLastBroadCast.add(searchE.now);
						}
						
						if(searchE.findedLast_footprintTime != -1 && searchE.RouteForLastFoot.get(searchE.RouteForLastFoot.size()-1) != searchE.now)
						{
							searchE.RouteForLastFoot.add(searchE.now);
						}

						searchE.now = searchE.now.right;
					}
					else if(searchE.endSearchBroadcast ==false)
					{
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
						
						TOOL.printStep("'右'邊的"+"封包 發現右邊沒人 停止向右邊追蹤","'右'邊的",prefix);
						if(searchE.findedLast_BroadCastTime > -1 && searchE.findedLast_BroadCastTime >= searchE.findedLast_footprintTime)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_BroadCastHop)*searchE.hop;
							int lastStep = searchE.RouteForLastBroadCast.size()-1;
							while(searchE.now != searchE.lastBroadcast)
							{
								TOOL.printStep("'右'邊的"+"封包 往最後發現的broadcast移動  ->"+searchE.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchE.hop,"'右'邊的",prefix);
								searchE.now = searchE.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
								
							}
							
						}
						else if(searchE.findedLast_footprintTime > -1)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_footprintHop)*searchE.hop;
							int lastStep = searchE.RouteForLastFoot.size()-1;
							while(searchE.now != searchE.lastFoot)
							{
								TOOL.printStep("'右'邊的"+"封包 往最後發現的footprint移動  ->"+searchE.RouteForLastFoot.get(lastStep).sensorid,"'右'邊的",prefix);
								searchE.now = searchE.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
								
							}
							searchE.traceFoot = true;
						}
						else
						{
//							Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize *searchE.hop;
							break;
						}
						searchE.endSearchBroadcast = true;
					}
				}
				else if(searchE.finded && !searchE.endSearchBroadcast) //在需要折返前找到(路徑上直接看到最後的foot)
				{
					
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
					Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchE.hop;
				}
				searchE.hop++;
			}
			
			while(searchW.hop <  Enviorment.randomwalk_step && !searchW.finded)
			{
				
				if(checkWE_andTime(searchW,lastTime,"'左'邊的",prefix,searchW.hop,true,RouteSizeName))
				{
					if(searchW.now.left != null)
					{
						
						if(searchW.findedLast_BroadCastTime != -1 && searchW.RouteForLastBroadCast.get(searchW.RouteForLastBroadCast.size()-1) != searchW.now )
						{
							searchW.RouteForLastBroadCast.add(searchW.now);
						}
						
						if(searchW.findedLast_footprintTime != -1 && searchW.RouteForLastFoot.get(searchW.RouteForLastFoot.size()-1) != searchW.now)
						{
							searchW.RouteForLastFoot.add(searchW.now);
						}
						
						TOOL.printStep("'左'邊的"+"封包往'左'邊追蹤  ->" + "步數:" + searchW.hop+" "+searchW.now.left.sensorid+"("+searchW.now.left.x+","+searchW.now.left.y+")","'左'邊的",prefix);
						searchW.now = searchW.now.left;
					}
					else if(searchW.endSearchBroadcast == false)
					{
						searchW.endSearchBroadcast = true;
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
						TOOL.printStep("Query+"+TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize),"'左'邊的",prefix);
						TOOL.printStep("'左'邊的"+"封包 發現左邊沒人 停止向左邊追蹤","'左'邊的",prefix);
						
						if(searchW.findedLast_BroadCastTime > -1 && searchW.findedLast_BroadCastTime >= searchW.findedLast_footprintTime)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_BroadCastHop)*searchW.hop;
							TOOL.printStep("Route+"+searchW.findedLast_BroadCastHop,"'左'邊的",prefix);
							int lastStep = searchW.RouteForLastBroadCast.size()-1;
							while(searchW.now != searchW.lastBroadcast)
							{
								TOOL.printStep("'左'邊的"+"封包 往最後發現的broadcast移動  -> "+searchW.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchW.hop,"'左'邊的",prefix);
								searchW.now = searchW.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
							}
						}
						else if(searchW.findedLast_footprintTime > -1)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop;
							TOOL.printStep("Route+"+(Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop,"'左'邊的",prefix);
							int lastStep = searchW.RouteForLastFoot.size()-1;
							while(searchW.now != searchW.lastFoot)
							{
								TOOL.printStep("'左'邊的"+"封包 往最後發現的footprint移動  ->"+searchW.RouteForLastFoot.get(lastStep).sensorid,"'左'邊的",prefix);
								searchW.now = searchW.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
							}
							searchW.traceFoot = true;
						}
						else
						{
							
//							Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchW.hop;
							break;
						}
						
					}
					else if(searchW.finded && !searchW.endSearchBroadcast) //在需要折返前找到(路徑上直接看到最後的foot)
					{
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
						
						Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchW.hop;
						
					}
				}
				searchW.hop++;
					
			}
			
			while(searchN.hop <  Enviorment.randomwalk_step && !searchN.finded) 
			{
				if(checkWE_andTime(searchN,lastTime,"'上'面的",prefix,searchN.hop,false,RouteSizeName))
				{
					if(searchN.now.up != null)
					{
						if(searchN.findedLast_BroadCastTime != -1 && searchN.RouteForLastBroadCast.get(searchN.RouteForLastBroadCast.size()-1) != searchN.now )
						{
							searchN.RouteForLastBroadCast.add(searchN.now);
						}
						
						if(searchN.findedLast_footprintTime != -1 && searchN.RouteForLastFoot.get(searchN.RouteForLastFoot.size()-1) != searchN.now)
						{
							searchN.RouteForLastFoot.add(searchN.now);
						}
						TOOL.printStep("'上'面的"+"封包往'上'面追蹤  ->"+searchN.now.up.sensorid+"("+searchN.now.up.x+","+searchN.now.up.y+")","'上'面的",prefix);
						searchN.now = searchN.now.up;
					}
					else if(searchN.endSearchBroadcast == false)
					{
						searchN.endSearchBroadcast = true;
						TOOL.printStep("'上'面的"+"封包 發現上面沒人 停止向左邊追蹤","'上'面的",prefix);
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchN.hop, Enviorment.BaseQuerySize);
						if(searchN.findedLast_BroadCastTime > -1 && searchN.findedLast_BroadCastTime >= searchN.findedLast_footprintTime)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchN.findedLast_BroadCastHop)*searchN.hop;
							int lastStep = searchN.RouteForLastBroadCast.size()-1;
							while(searchN.now != searchN.lastBroadcast)
							{
								TOOL.printStep("'上'面的"+"封包 往最後發現的broadcast移動  ->"+searchN.RouteForLastBroadCast.get(lastStep).sensorid,"'上'面的",prefix);
								searchN.now = searchN.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
							}
						}
						else if(searchN.findedLast_footprintTime > -1)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchN.findedLast_footprintHop)*searchN.hop;
							int lastStep = searchN.RouteForLastFoot.size()-1;
							while(searchN.now != searchN.lastFoot)
							{
								TOOL.printStep("'上'面的"+"封包 往最後發現的footprint移動  ->"+searchN.RouteForLastFoot.get(lastStep).sensorid,"'上'面的",prefix);
								searchN.now = searchN.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop數在Query的階段時不需要增加
							}
							searchN.traceFoot = true;
						}
						else
						{
							//Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchN.hop;
							break;
						}
								
					}
				}
				else if(searchN.finded && !searchN.endSearchBroadcast) //在需要折返前找到(路徑上直接看到最後的foot)
				{
					
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchN.hop, Enviorment.BaseQuerySize);
					Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchN.hop;
				}
				searchN.hop++;
			}
			while(searchS.hop <  Enviorment.randomwalk_step && !searchS.finded)
			{
				if(searchS != null )
				{
					if(checkWE_andTime(searchS,lastTime,"'下'面的",prefix,searchS.hop,false,RouteSizeName))
					{
						if(searchS.now.down != null)
						{
							if(searchS.findedLast_BroadCastTime != -1 && searchS.RouteForLastBroadCast.get(searchS.RouteForLastBroadCast.size()-1) != searchS.now )
							{
								searchS.RouteForLastBroadCast.add(searchS.now);
							}
							
							if(searchS.findedLast_footprintTime != -1 && searchS.RouteForLastFoot.get(searchS.RouteForLastFoot.size()-1) != searchS.now)
							{
								searchS.RouteForLastFoot.add(searchS.now);
							}
							TOOL.printStep("'下'面的"+"封包往'下'面追蹤  ->"+searchS.now.down.sensorid+"("+searchS.now.down.x+","+searchS.now.down.y+")","'下'面的",prefix);
							searchS.now = searchS.now.down;
						}
						else if(searchS.endSearchBroadcast == false)
						{
							searchS.endSearchBroadcast = true;
							TOOL.printStep("'下'面的"+"封包 發現左邊沒人 停止向下面追蹤","'下'面的",prefix);
							Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchS.hop, Enviorment.BaseQuerySize);
							if(searchS.findedLast_BroadCastTime > -1 && searchS.findedLast_BroadCastTime >= searchS.findedLast_footprintTime)
							{
								Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchS.findedLast_BroadCastHop)*searchS.hop;
								int lastStep = searchS.RouteForLastBroadCast.size()-1;
								while(searchS.now != searchS.lastBroadcast)
								{
									TOOL.printStep("'下'面的"+"封包 往最後發現的broadcast移動  ->"+searchS.RouteForLastBroadCast.get(lastStep).sensorid,"'下'面的",prefix);
									searchS.now = searchS.RouteForLastBroadCast.get(lastStep);
									lastStep--;
									//hop++;
									//hop數在Query的階段時不需要增加
								}
							}
							else if(searchS.findedLast_footprintTime > -1)
							{
								Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchS.findedLast_footprintHop)*searchS.hop;
								int lastStep = searchS.RouteForLastFoot.size()-1;
								while(searchS.now != searchS.lastFoot)
								{
									TOOL.printStep("'下'面的"+"封包 往最後發現的footprint移動  ->"+searchS.RouteForLastFoot.get(lastStep).sensorid,"'下'面的",prefix);
									searchS.now = searchS.RouteForLastFoot.get(lastStep);
									lastStep--;
									//hop++;
									//hop數在Query的階段時不需要增加
								}
								searchS.traceFoot = true;
							}
							else
							{
								Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchS.hop;
								break;
							}
								
						}
					}
					else if(searchS.finded && !searchS.endSearchBroadcast) //在需要折返前找到(路徑上直接看到最後的foot)
					{
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchS.hop, Enviorment.BaseQuerySize);
						Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchS.hop;
					}
				}
				searchS.hop++;
					
			}
			

			int a = TOOL.max(searchE.findedLast_BroadCastTime_inQuery,searchE.findedLast_footprintTime_inQuery);
			int b = TOOL.max(searchW.findedLast_BroadCastTime_inQuery,searchW.findedLast_footprintTime_inQuery);
			int c = TOOL.max(searchN.findedLast_BroadCastTime_inQuery,searchN.findedLast_footprintTime_inQuery);
			int d = TOOL.max(searchS.findedLast_BroadCastTime_inQuery,searchS.findedLast_footprintTime_inQuery);
			// a 右  b左 c上   d 下 找最晚的b/f 時間
			int arr[] = {a,b,c,d};
			int max = -1;
			for(int i : arr)
			{
				if(i > max)
					max = i;
			}
			
			a = (a == max)? searchE.findedLast_BroadCastHop:Integer.MAX_VALUE;
			b = (b == max)? searchW.findedLast_BroadCastHop:Integer.MAX_VALUE;
			c = (c == max)? searchN.findedLast_BroadCastHop:Integer.MAX_VALUE;
			d = (d == max)? searchS.findedLast_BroadCastHop:Integer.MAX_VALUE;
			//把時間一樣的 hop數挑出來
			int index = 0;
			
			int min = Integer.MAX_VALUE;
			int arr2[] = {a,b,c,d};
			for(int i = 0 ; i < arr2.length ; i++)
				if(min > arr2[i])
				{
					min = arr2[i];
					index = i;
				}
			//再挑hop數最小的
			int hop = 0;
			if(index == 0)
			{
				
				if(searchE.findedLast_BroadCastTime_inQuery >= searchE.findedLast_footprintTime_inQuery)
				{
//					TOOL.printStep("totalHop+" +(searchE.hop-searchE.RouteForLastBroadCast.size()), "HOP", prefix);
					
//					hop = searchE.hop-searchE.RouteForLastBroadCast.size();
				}
				else
				{
//					TOOL.printStep("totalHop+" +(searchE.hop-searchE.RouteForLastFoot.size()), "HOP", prefix);
					
//					hop = searchE.hop-searchE.RouteForLastFoot.size();
				}
				
				if(Enviorment.closest_sink == searchE.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				
				TOOL.printStep("'右'邊的封包往追蹤到了位於("+searchE.now.foot.x+","+searchE.now.foot.y+")的sink,使用hop 數:"+searchE.hop,"'右'邊的",prefix);
				TOOL.printStep("'右'邊的封包往追蹤到了","'左'邊的",prefix);
				TOOL.printStep("'右'邊的封包往追蹤到了","'下'面的",prefix);
				TOOL.printStep("'右'邊的封包往追蹤到了","'上'面的",prefix);
			}
			else if(index == 1)
			{
				
				if(searchW.findedLast_BroadCastTime_inQuery >= searchW.findedLast_footprintTime_inQuery)
				{
//					TOOL.printStep("totalHop+" +(searchW.hop-searchW.RouteForLastBroadCast.size()), "HOP", prefix);
					
//					hop = searchW.hop-searchW.RouteForLastBroadCast.size();
				}
				else
				{
//					TOOL.printStep("totalHop+" +(searchW.hop-searchW.RouteForLastFoot.size()), "HOP", prefix);
					
//					hop = searchW.hop-searchW.RouteForLastFoot.size();
				}
				
				if(Enviorment.closest_sink == searchW.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				
				TOOL.printStep("'左'邊的封包往追蹤到了位於("+searchW.now.foot.x+","+searchW.now.foot.y+")的sink,使用hop 數:"+searchW.hop,"'左'邊的",prefix);
				TOOL.printStep("'左'邊的封包往追蹤到了","'右'邊的",prefix);
				TOOL.printStep("'左'邊的封包往追蹤到了","'上'面的",prefix);
				TOOL.printStep("'左'邊的封包往追蹤到了","'下'面的",prefix);
			}
			else if(index == 3)
			{
				
				if(searchS.findedLast_BroadCastTime_inQuery >= searchS.findedLast_footprintTime_inQuery)
				{
//					TOOL.printStep("totalHop+" +(searchS.hop-searchS.RouteForLastBroadCast.size()), "HOP", prefix);
					
//					hop = searchS.hop-searchS.RouteForLastBroadCast.size();
				}
				else
				{
//					TOOL.printStep("totalHop+" +(searchS.hop-searchS.RouteForLastFoot.size()), "HOP", prefix);
					
//					hop = searchS.hop-searchS.RouteForLastFoot.size();
				}
				
				if(Enviorment.closest_sink == searchS.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				TOOL.printStep("'下'面的封包往追蹤到了位於("+searchS.now.foot.x+","+searchS.now.foot.y+")的sink,使用hop 數:"+searchS.hop,"'下'面的",prefix);
				TOOL.printStep("'下'面的封包往追蹤到了","'左'邊的",prefix);
				TOOL.printStep("'下'面的封包往追蹤到了","'右'邊的",prefix);
				TOOL.printStep("'下'面的封包往追蹤到了","'上'面的",prefix);
			}
			else if(index == 2)
			{
				
				if(searchN.findedLast_BroadCastTime_inQuery >= searchN.findedLast_footprintTime_inQuery)
				{
//					TOOL.printStep("totalHop+" +(searchN.hop-searchN.RouteForLastBroadCast.size()), "HOP", prefix);
					
//					hop = searchN.hop-searchN.RouteForLastBroadCast.size();
				}
				else
				{
//					TOOL.printStep("totalHop+" +(searchN.hop-searchN.RouteForLastFoot.size()), "HOP", prefix);
					
//					hop = searchN.hop-searchN.RouteForLastFoot.size();
				}
				
				if(Enviorment.closest_sink == searchN.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				TOOL.printStep("'上'邊的封包往追蹤到了位於("+searchN.now.foot.x+","+searchN.now.foot.y+")的sink,使用hop 數:"+searchN.hop,"'上'面的",prefix);
				TOOL.printStep("'上'邊的封包往追蹤到了","'下'面的",prefix);
				TOOL.printStep("'上'邊的封包往追蹤到了","'左'邊的",prefix);
				TOOL.printStep("'上'邊的封包往追蹤到了","'右'邊的",prefix);
			}
			else
			{
				if(searchE.endSearchBroadcast == false)
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchN.hop, Enviorment.BaseQuerySize);
				if(searchN.endSearchBroadcast == false)
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
				if(searchW.endSearchBroadcast == false)
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchS.hop, Enviorment.BaseQuerySize);
				if(searchS.endSearchBroadcast == false)
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
				
				TOOL.printStep("hop數超過"+hop,"'左'邊的",prefix);
				TOOL.printStep("hop數超過"+hop,"'右'邊的",prefix);
				TOOL.printStep("hop數超過"+hop,"'下'面的",prefix);
				TOOL.printStep("hop數超過"+hop,"'上'面的",prefix);
			}
			
			
			int arr3[] = {TOOL.getTrueHop(searchE, lastTime),TOOL.getTrueHop(searchW, lastTime),TOOL.getTrueHop(searchN, lastTime),TOOL.getTrueHop(searchS, lastTime)};
			min = Integer.MAX_VALUE;
			for(int i : arr3)
				if(i < min)
					min = i;
			hop = min;
			
			
			
			
			
			Enviorment.totalHop[QueryName] += hop;
			
			if(searchN.finded != true && searchW.finded != true && searchE.finded != true && searchS.finded != true)
			{
				TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 0", prefix+"成功率.txt");
			}
			else
			{
				if(hop<=10)
					Enviorment.XYhop10++;
				else if(hop>10 && hop<=20)
					Enviorment.XYhop20++;
				else if(hop>20 && hop<=30)
					Enviorment.XYhop30++;
				else if(hop>30 && hop<=40)
					Enviorment.XYhop40++;
				else if(hop>40 && hop<=50)
					Enviorment.XYhop50++;
				else if(hop>50 && hop<=60)
					Enviorment.XYhop60++;
				else if(hop>60 && hop<=70)
					Enviorment.XYhop70++;
				else if(hop>70 && hop<=80)
					Enviorment.XYhop80++;
				else if(hop>80 && hop<=90)
					Enviorment.XYhop90++;
				else if(hop>90)
					Enviorment.XYhop100++;
				TOOL.printLog("時間:" + lastTime + "  sensor ID:"+this.sensorid +"成功率: 1", prefix+"成功率.txt");	
			}
		}
			
		
		private int checkWE(point target , int time,String prefix,String filePrefix)
		{
			
			// TODO 自動產生的方法 Stub
			if(target.foot!= null)
			{
				if(target.foot.time != time)
				{
					switch(target.foot.way)
					{
						case 0:
							TOOL.printStep(prefix+"封包往'上'面追蹤足跡  ->"+target.up.sensorid,prefix,filePrefix);
							return 0;
						case 1:
							TOOL.printStep(prefix+"封包往'左'邊追蹤足跡  ->"+target.left.sensorid,prefix,filePrefix);
							return 1;
						case 2:
							TOOL.printStep(prefix+"封包往 '下'面追蹤足跡  ->"+target.down.sensorid,prefix,filePrefix);
							return 2;
						case 3:
							TOOL.printStep(prefix+"封包往'右'邊追蹤足跡 ->"+target.right.sensorid,prefix,filePrefix);
							return 3;
					}
					return 0;
				}
				else
					return -1;
			}
			/*
			else if(target.upOrDown == 1)
			{
				TOOL.printStep(prefix+"'封包往'上'面追蹤廣播 ->"+target.up.sensorid,prefix);
				return 0;
			}
			else if(target.upOrDown == -1)
			{
				TOOL.printStep(prefix+"'封包往 '下'面追蹤廣播  ->"+target.down.sensorid,prefix);
				return 2;
			}
			*/
			else if(target.traceBroadCast_UD != null)
			{
				TOOL.printStep(prefix+"封包往broadcast來的方向走->"+target.traceBroadCast_UD.sensorid,prefix,filePrefix);
				return 5;
			}
			else
				return -2;
		}
		
		//檢查是否追到事件發生時間點的foot(代表追到sink) 或 某個時間點之後的 foot 或 boradcast 並移動之
		private boolean checkWE_andTime(searchPackage target  , int lastTime ,String prefix,String filePrefix,int hop,boolean isUD, int RouteSizeName) throws IOException
		{
			
			
			int lastBroadcastTime = target.findedLast_BroadCastTime;
			point lastb = null;
			
			if(target.now.foot.time > target.findedLast_footprintTime)
			{
				if(!target.endSearchBroadcast)
					target.findedLast_footprintTime_inQuery = target.now.foot.time;
				target.findedLast_footprintTime = target.now.foot.time;
				target.lastFoot = target.now;
				target.RouteForLastFoot = new ArrayList<point>();
				target.RouteForLastFoot.add(target.lastFoot);
				target.findedLast_footprintHop = hop;
				
				TOOL.printStep(prefix+"封包 發現 sensor:"+target.now.sensorid+"具有footprint 時間:"+target.findedLast_footprintTime +" ID : "+target.lastFoot.sensorid,prefix,filePrefix);
			}
			
			
			if(isUD && target.now.traceBroadCast_UD != null && target.now.sinktime_UD > lastBroadcastTime)
			{
				lastBroadcastTime = target.now.sinktime_UD;
				lastb = target.now;
				target.RouteForLastBroadCast = new ArrayList<point>();
				target.RouteForLastBroadCast.add(lastb);
				target.findedLast_BroadCastHop = hop;
				
				TOOL.printStep(prefix+"封包 發現 sensor:"+target.now.sensorid+"具有broadcast UD 時間:"+lastBroadcastTime +" ID : "+target.now.traceBroadCast_UD.sensorid,prefix,filePrefix);
				
			}
			else if(!isUD && target.now.traceBroadCast_LR != null && target.now.sinktime_LR > lastBroadcastTime)
			{
				lastBroadcastTime = target.now.sinktime_LR;
				lastb = target.now;
				target.RouteForLastBroadCast = new ArrayList<point>();
				target.RouteForLastBroadCast.add(lastb);
				target.findedLast_BroadCastHop = hop;
				TOOL.printStep(prefix+"封包 發現 sensor:"+target.now.sensorid+"具有broadcast LR 時間:"+lastBroadcastTime +" ID : "+target.now.traceBroadCast_LR.sensorid,prefix,filePrefix);
				
			}
			
			for(int i = 0 ; i < target.now.nearby.size() ; i++)
			{
				if(target.now.nearby.get(i).foot.time > target.findedLast_footprintTime)
				{
					if(!target.endSearchBroadcast)
						target.findedLast_footprintTime_inQuery = target.now.nearby.get(i).foot.time;
					target.findedLast_footprintTime = target.now.nearby.get(i).foot.time;
					target.lastFoot = target.now.nearby.get(i);
					target.RouteForLastFoot = new ArrayList<point>();
					target.RouteForLastFoot.add(target.lastFoot);
					target.findedLast_footprintHop = hop;
					TOOL.printStep(prefix+"封包 發現 sensor:"+target.now.nearby.get(i).sensorid+"具有footprint 時間:"+target.findedLast_footprintTime +" ID : "+target.lastFoot.sensorid,prefix,filePrefix);
					
					
				}
				
				if(isUD && !target.endSearchBroadcast && target.now.nearby.get(i).traceBroadCast_UD != null && target.now.nearby.get(i).sinktime_UD > lastBroadcastTime)
				{
					lastBroadcastTime = target.now.nearby.get(i).sinktime_UD;
					lastb = target.now.nearby.get(i);
					target.RouteForLastBroadCast = new ArrayList<point>();
					target.RouteForLastBroadCast.add(lastb);
					target.findedLast_BroadCastHop = hop;
					TOOL.printStep(prefix+"封包 發現 sensor:"+target.now.nearby.get(i).sensorid+"具有broadcast_UD 時間:"+lastBroadcastTime +" ID : "+target.now.nearby.get(i).traceBroadCast_UD.sensorid,prefix,filePrefix);
					
				}
				else if(!isUD && !target.endSearchBroadcast && target.now.nearby.get(i).traceBroadCast_LR != null && target.now.nearby.get(i).sinktime_UD > lastBroadcastTime)
				{
					lastBroadcastTime = target.now.nearby.get(i).sinktime_UD;
					lastb = target.now.nearby.get(i);
					target.RouteForLastBroadCast = new ArrayList<point>();
					target.RouteForLastBroadCast.add(lastb);
					target.findedLast_BroadCastHop = hop;
					TOOL.printStep(prefix+"封包 發現 sensor:"+target.now.nearby.get(i).sensorid+"具有broadcast_LR 時間:"+lastBroadcastTime +" ID : "+target.now.nearby.get(i).traceBroadCast_LR.sensorid,prefix,filePrefix);
					
				}
				
			}
			
			if(target.now == null) //追丟了
			{
				TOOL.printStep(prefix+"追丟了",prefix,filePrefix);
				return false;
			}
			
			//foot sinknearhere =1: 追到sink 
			if(target.now.foot.time == lastTime)
			{
				int trueHop = TOOL.getTrueHop(target, lastTime);
				Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* trueHop)*trueHop;
				Enviorment.CountQuerySize[target.QueryName] += TOOL.BaseQueryToQuerySize(trueHop, Enviorment.BaseQuerySize);
				TOOL.printStep(prefix+"封包 發現 sink:"+target.now.foot.id +"在("+target.now.foot.x+","+target.now.foot.y+")",prefix,filePrefix);
				target.finded = true;
				
				return false;
			}
			//broadcast 有最後時間點 : 內含有最後時間點的foot
			else if(target.now.traceBroadCast_UD != null && target.now.sinktime_UD == lastTime)
			{
				TOOL.printStep(prefix+"封包往broadcast來的方向走->"+target.now.traceBroadCast_UD.sensorid+"("+target.now.traceBroadCast_UD.x+","+target.now.traceBroadCast_UD.y+")",prefix,filePrefix);
				target.now = target.now.traceBroadCast_UD;
				return false;
			}
			//已確認不用繼續往後找是否有更晚時間點的broadcast 並且最後發現的foot時間點比最後發現的broadcast時間點晚
			else if(target.traceFoot || target.findedLast_footprintTime == lastTime)
			{
				if(target.now != target.lastFoot)
				{
					
					TOOL.printStep(prefix+"封包往 發現的足跡封包移動  ->"+target.lastFoot.sensorid,prefix,filePrefix);
					target.now = target.lastFoot;
				}
				else
				switch(target.lastFoot.foot.quadrant)
				{
					case 1:
						if(target.now.right==null)
						{
							TOOL.printStep(prefix+"封包往'正右'面追蹤足跡  ->沒人",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"封包往'正右'面追蹤足跡  ->"+target.now.right.sensorid,prefix,filePrefix);
						target.now=target.now.right;
						break;
					case 3:
						if(target.now.up==null)
						{
							TOOL.printStep(prefix+"封包往'正上'面追蹤足跡  ->沒人",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"封包往'正上'面追蹤足跡  ->"+target.now.up.sensorid,prefix,filePrefix);
						target.now=target.now.up;
						break;
					case 5:
						if(target.now.left==null)
						{
							TOOL.printStep(prefix+"封包往'正左'面追蹤足跡  ->沒人",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"封包往'正左'面追蹤足跡  ->"+target.now.left.sensorid,prefix,filePrefix);
						target.now=target.now.left;
						break;
					case 7:
						if(target.now.down==null)
						{
							TOOL.printStep(prefix+"封包往'正下'面追蹤足跡  ->沒人",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"封包往'正下'面追蹤足跡  ->"+target.now.down.sensorid,prefix,filePrefix);
						target.now=target.now.down;
						break;
					case 2:
						if(target.now.topRight != null)
						{
							TOOL.printStep(prefix+"封包往'右上'追蹤足跡  ->"+target.now.topRight.sensorid,prefix,filePrefix);
							target.now = target.now.topRight;
						}
						else
						{
							TOOL.printStep(prefix+"封包足跡追丟了",prefix,filePrefix);
							break;
						}
						break;
					case 4:
						if(target.now.topLeft != null)
						{
							TOOL.printStep(prefix+"封包往'左上'追蹤足跡  ->"+target.now.topLeft.sensorid,prefix,filePrefix);
							target.now = target.now.topLeft;
						}
						else
						{
							TOOL.printStep(prefix+"封包足跡追丟了",prefix,filePrefix);
							break;
						}
						break;
					case 6:
						if(target.now.buttomLeft != null)
						{
							TOOL.printStep(prefix+"封包往'左下'追蹤足跡  ->"+target.now.buttomLeft.sensorid,prefix,filePrefix);
							target.now = target.now.buttomLeft;
						}
						else
						{
							TOOL.printStep(prefix+"封包足跡追丟了",prefix,filePrefix);
							break;
						}
						break;
					case 8:
						if(target.now.buttomRight != null)
						{
							TOOL.printStep(prefix+"封包往'右下'追蹤足跡  ->"+target.now.buttomRight.sensorid,prefix,filePrefix);
							target.now = target.now.buttomRight;
						}
						else
						{
							TOOL.printStep(prefix+"封包足跡追丟了",prefix,filePrefix);
							break;
						}
						break;
						
				}
				target.lastFoot = target.now;
				return false;
			}
			else if(isUD && target.endSearchBroadcast && target.findedLast_BroadCastTime > -1 && target.now.BroadCast_hop_UD > 0)
			{
				
				TOOL.printStep(prefix+"封包往broadcast來的方向走(UD)->"+target.now.traceBroadCast_UD.sensorid+"("+target.now.traceBroadCast_UD.x+","+target.now.traceBroadCast_UD.y+")",prefix,filePrefix);
				target.now = target.now.traceBroadCast_UD;
				if(target.now.BroadCast_hop_UD == 0)
					target.traceFoot = true;
				
				
				return false;
			}
			else if(!isUD && target.endSearchBroadcast && target.findedLast_BroadCastTime > -1 && target.now.BroadCast_hop_LR > 0)
			{
				TOOL.printStep(prefix+"封包往broadcast來的方向走(LR)->"+target.now.traceBroadCast_LR.sensorid+"("+target.now.traceBroadCast_LR.x+","+target.now.traceBroadCast_LR.y+")",prefix,filePrefix);
				target.now = target.now.traceBroadCast_LR;
				if(BroadCast_hop_LR == 0)
					target.traceFoot = true;
				
				return false;
			}
			
			
			if(lastb != null)
			{
				if(  (isUD && lastb.sinktime_UD < target.findedLast_BroadCastTime) || (!isUD && lastb.sinktime_LR < target.findedLast_BroadCastTime))
				{
					if(isUD)
						TOOL.printStep(prefix+"封包 發現 sensor:"+lastb.sensorid+"具有broadcast 時間:"+lastb.sinktime_UD +" ID : "+lastb.traceBroadCast_UD.sensorid,prefix,filePrefix);
					else
						TOOL.printStep(prefix+"封包 發現 sensor:"+lastb.sensorid+"具有broadcast 時間:"+lastb.sinktime_LR +" ID : "+lastb.traceBroadCast_LR.sensorid,prefix,filePrefix);
					
					target.endSearchBroadcast = true;
					if(target.findedLast_BroadCastTime > -1 && target.findedLast_BroadCastTime > target.findedLast_footprintTime)
					{
						if(target.findedLast_footprintTime <= target.findedLast_BroadCastTime)
						{
							target.now = target.lastBroadcast;
						}
						else
						{
							target.RouteForLastBroadCast = null;
							target.now = target.lastFoot;
						}
					}
					TOOL.printStep(prefix+"封包 發現當前的broadcast 時間比較晚 停止向"+prefix+"追蹤",prefix,filePrefix);
					Enviorment.CountQuerySize[target.QueryName] += TOOL.BaseQueryToQuerySize(hop, Enviorment.BaseQuerySize);
					return false;
				}
				if(!target.endSearchBroadcast)
					target.findedLast_BroadCastTime_inQuery = lastBroadcastTime;
				target.findedLast_BroadCastTime = lastBroadcastTime;
				target.lastBroadcast = lastb;
			}
			
				
			return true;
		}
		
		point (int x , int y)
		{
			this.x = x;
			this.y = y;
		}	
		public point(point point) {
			this.x= point.x;
			this.y= point.y;
			this.upOrDown = point.upOrDown;
			this.leftOrRight = point.leftOrRight;
			this.sinktime_UD = point.sinktime_UD;
			this.sinkid = point.sinktime_UD;
			this.sensorid = point.sensorid;
			this.up = point.up;
			this.down = point.down;
			this.right = point.right;
			this.left = point.left;
			this.foot = new foot(point.foot);
			this.data_buffer = new int[point.data_buffer.length];
			this.data_bufferHopCount = new int[point.data_bufferHopCount.length];
			this.traceBroadCast_UD=point.traceBroadCast_UD;
		}
		
		void Broadcast_up(point base , int sinknum , int time,int GuideSizename)
		{
			TOOL.printBroadcast(this,time, false);
			this.upOrDown = -1;
			this.sinkid = sinknum;
			sinktime_UD = time;
			this.traceBroadCast_UD = base;
			Enviorment.CountGuideSize[GuideSizename] += Enviorment.GuideSize;
			if(up != null  && this.y >= 0)
			{
				up.BroadCast_hop_UD = this.BroadCast_hop_UD+1;
				up.Broadcast_up(this , sinknum ,  time,GuideSizename);
			}
			
		}
		void Broadcast_down(point base, int sinknum , int time,int GuideSizename)
		{
			TOOL.printBroadcast(this,time, false);
			this.upOrDown = 1;
			this.sinkid = sinknum;
			sinktime_UD = time;
			this.traceBroadCast_UD = base;
			Enviorment.CountGuideSize[GuideSizename] += Enviorment.GuideSize;
			if(down != null && this.y <= Enviorment.Width)
			{
				down.BroadCast_hop_UD = this.BroadCast_hop_UD+1;
				down.Broadcast_down(this , sinknum ,  time,GuideSizename);
			}
		}
		void Broadcast_left(point base, int sinknum , int time,int GuideSizename)
		{
			TOOL.printBroadcast(this,time, false);
			this.leftOrRight = 1;
			this.sinkid = sinknum;
			sinktime_LR = time;
			this.traceBroadCast_LR = base;
			Enviorment.CountGuideSize[GuideSizename] += Enviorment.GuideSize;
			
			if(left != null && this.x >= 0)
			{
				left.BroadCast_hop_LR = this.BroadCast_hop_LR+1;
				left.Broadcast_left(this , sinknum ,  time,GuideSizename);
			}
		}
		void Broadcast_right(point base, int sinknum , int time,int GuideSizename)
		{
			TOOL.printBroadcast(this,time, false);
			this.leftOrRight = -1;
			this.sinkid = sinknum;
			sinktime_LR = time;
			this.traceBroadCast_LR = base;
			Enviorment.CountGuideSize[GuideSizename] += Enviorment.GuideSize;
			
			if(right != null && this.x <= Enviorment.Height)
			{
				right.BroadCast_hop_LR = this.BroadCast_hop_LR+1;
				right.Broadcast_right(this , sinknum ,  time,GuideSizename);
			}
		}
		public void BroadcastX(int x, int y, int sinknum, int time , int GuideSizename) {
			// TODO 自動產生的方法 Stub
			this.traceBroadCast_UD = this;
			TOOL.printBroadcast(this,time, true);
			Enviorment.CountGuideSize[GuideSizename] += Enviorment.GuideSize;
			this.BroadCast_hop_UD = 0;
			this.sinktime_UD = time;
			if(up!=null)
			{
				up.BroadCast_hop_UD = this.BroadCast_hop_UD+1;
				up.Broadcast_up(this, sinknum, time,GuideSizename);
			}
			if(down != null)
			{
				down.BroadCast_hop_UD = this.BroadCast_hop_UD+1;
				down.Broadcast_down(this, sinknum, time,GuideSizename);
			}
		}
		public void BroadcastXY_UD(int x, int y, int sinknum, int time ,int GuideSizename) {
			// TODO 自動產生的方法 Stub
			this.traceBroadCast_UD = this;
			TOOL.printBroadcast(this,time, true);
			Enviorment.CountGuideSize[GuideSizename] += Enviorment.GuideSize;
			this.BroadCast_hop_UD = 0;
			this.sinktime_UD = time;
			if(up!=null)
			{
				up.BroadCast_hop_UD = this.BroadCast_hop_UD+1;
				up.Broadcast_up(this, sinknum, time,GuideSizename);
			}
			if(down != null)
			{
				down.BroadCast_hop_UD = this.BroadCast_hop_UD+1;
				down.Broadcast_down(this, sinknum, time,GuideSizename);
			}
		}
		public void BroadcastXY_LR(int x, int y, int sinknum, int time ,int GuideSizename) {
			// TODO 自動產生的方法 Stub
		
			this.traceBroadCast_LR = this;
			TOOL.printBroadcast(this,time, true);
			Enviorment.CountGuideSize[GuideSizename] += Enviorment.GuideSize;
			this.sinktime_LR = time;
			this.BroadCast_hop_LR = 0;
			if(right!=null)
			{
				right.BroadCast_hop_LR = this.BroadCast_hop_LR+1;
				right.Broadcast_right(this, sinknum, time,GuideSizename);
			}
			if(left != null)
			{
				left.BroadCast_hop_LR = this.BroadCast_hop_LR+1;
				left.Broadcast_left(this, sinknum, time,GuideSizename);
			}
		}
	}
	
	
	class sink implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 646383477297902526L;
		public static int searchNum=0;
		public int count = 0; //計算到達數量用
		int x=0;
		int y=0;
		sink()
		{
			
		}
		
		public static void foot(point[] p,sink k,int i,int time ,int way ,int BeconSizeName) {
			double dis = 200;
			int senId = -1;
			for(int j = 0 ; j < p.length ; j++)
			{
				if(TOOL.COMP_Dis(k,p[j]) <= Enviorment.communication_range&& dis> TOOL.COMP_Dis(k,p[j]))
				{
					dis = TOOL.COMP_Dis(k,p[j]);
					senId = j;
				}
				
				if(TOOL.COMP_Dis(k,p[j]) <= Enviorment.communication_range)
				{
					
					Enviorment.CountBeconSize[BeconSizeName] += Enviorment.BeaconSize;
					
					if(p[j].foot == null)
						p[j].foot = new foot();
					
					p[j].foot.closeID = senId;
					p[j].foot.closetime = time;
					p[j].foot.id=i;
					p[j].foot.time = time;
					p[j].foot.way=way;
					p[j].foot.x=k.x;
					p[j].foot.y=k.y;
					p[j].foot.near=k;
					p[j].foot.isInnerClose=true;
					p[j].foot.quadrant=TOOL.decideQuadrant(p[j], k);
					
					for(int fuck = 0 ; fuck < p[j].nearby.size() ; fuck++)
					{
						point next = p[j].nearby.get(fuck);
						
						if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
						{
							TOOL.printStep("時間:"+time+" "+next.sensorid+"被(於F)"+p[j].sensorid+"通知 變成close", "By_3", "Track");
							if( BeconSizeName ==Enviorment.BeconSize_name.BeconSize_3.ordinal())
							{
								next.foot.closeID = p[j].sensorid;
								next.foot.closetime = time;
								next.foot.isInnerClose = false;
							}
							
						}
					}
					
				}
				
			}
			
			//先改成每一種方法 都是將Beacon 給周遭所有的sensor close 旁不是close的成為track
//			p[senId].foot.closeID = senId;
//			p[senId].foot.closetime = time;
//			//有CLOSE的封包會把周圍的SENSOR變成TRACK，但是不確定有沒有這一行，不然很難找like a shit
//			for(int fuck = 0 ; fuck < p[senId ].nearby.size() ; fuck++)
//			{
//				point next = p[senId].nearby.get(fuck);
//				
//				if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
//				{
//					next.foot.trackID = next.sensorid;
//					next.foot.tracktime = time;
//				}
//			}
			
			
		}
		sink (int x , int y)
		{
			this.x = x;
			this.y = y;
		}
		static void Broadcast(point p[],sink s[], int sinknum , int time ,int GuideSizename)
		{
			double Dis = Double.MAX_VALUE;
			int Id = -1;
			for(int i = 0 ; i < p.length ; i++)
			{
				if(TOOL.COMP_Dis(s[sinknum], p[i]) <= Enviorment.communication_range)
				{
					
					if(TOOL.COMP_Dis(s[sinknum], p[i])< Dis)
					{
						Dis = TOOL.COMP_Dis(s[sinknum], p[i]);
						Id = i;
					}
				}
					
			}
			if(Id >= 0)
//			if(Id >= 0 && (p[Id].sinktime_UD == -1 || time-p[Id].sinktime_UD > Enviorment.sinkwalk))
			{
				p[Id].BroadcastX(s[sinknum].x,s[sinknum].y, sinknum, time,GuideSizename);
			}
			
		}
		static void BroadcastXY(point p[],sink s[], int sinknum , int time ,int GuideSizename,boolean isUD)
		{
			double Dis = Double.MAX_VALUE;
			int Id = -1;
			for(int i = 0 ; i < p.length ; i++)
			{
				if(TOOL.COMP_Dis(s[sinknum], p[i]) <= Enviorment.communication_range)
				{
					
					if(TOOL.COMP_Dis(s[sinknum], p[i])< Dis)
					{
						Dis = TOOL.COMP_Dis(s[sinknum], p[i]);
						Id = i;
					}
				}
					
			}
			if(Id >= 0)//sor最近的
			{
				if(isUD)
//				if(isUD && (p[Id].sinktime_UD == -1 || time-p[Id].sinktime_UD > Enviorment.sinkwalk))
				{
					p[Id].BroadCast_hop_UD = 0;
					p[Id].BroadcastXY_UD(s[sinknum].x,s[sinknum].y, sinknum, time,GuideSizename);
				}
				else
//				else if(!isUD && (p[Id].sinktime_LR == -1 || time-p[Id].sinktime_LR > Enviorment.sinkwalk))
				{
					p[Id].BroadCast_hop_LR = 0;
					p[Id].BroadcastXY_LR(s[sinknum].x,s[sinknum].y, sinknum, time,GuideSizename);
				}
			}
		}
	}
