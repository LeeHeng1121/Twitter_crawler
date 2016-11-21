
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
	//���� printStep �� printBroadcast (�q�`�OEVENT��  �����ɿ�X false ������X
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
	static int  open_randomwalk; //���}�H������
	static int  randomwalk_trailsteps; //�H�����X�B�ӧ�
	static int  randomwalk_trailtimes; //�䤣��ɭ��ӴX�M
	static int  X_touchX =0;
	static int  XY_touch_X =0;
	static int  XY_touch_Y =0;
	static int  setfireX,setfireY;
	static int  Extended_hop;
	private static Scanner config;
	static int sinkwalk=0;
	static int bufferSize = 1024; // HRDG ��buffer ����s�񪺫ʥ]���ơ@���]�@�ӫʥ]��512bit�C
	
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
	
	static int BeaconSize = 10; //footprint�ʥ]�j�p
	static int GuideSize = 13; //TAG���u�ʥ]�j�p  sink id  =4 hop count =4 direct =1 ? time =4?
//	static int QuerySize = 2; //TAG��GUIDE�ɪ��ʥ]�j�p
//	static int RoutePacketSize = 512; //�ƥ��Ƥj�p
	static int BaseQuerySize = 18;
	
	
	
	static int totalHop[] = new int[3];

	static int ShouldWalk = sinkwalk;
	
	

	// 18 x hop + (1+hop) x hop ->�o�O��Query������
	// (18 + hop(�ӥb���̪�guide��hop�ơA�ä��O��ڨ��쪺hop��) x 2) x hop ->�o�O��Route������
	//���]�@�ӫʥ]���F64�B�A�b35�B�o�{��guide�A�b��64�B�o�{�w�g�S�����U�Ӫ�guide
	//�⦡�� 18*64 + (1+64)x 64 <//query + (18+35*2)x64 //route

	//hop^2+19hop 
	
	//�S���guide�]�S���beacon�����p
	//18 x hop + (1+hop) x hop ->Query
	//18xhop ->Route
	//���]�@�ӫʥ]���F16�B�o�{������ɨS���o�{guide�]�S���beacon�A�^�Ǯɷ|�Ǧ^�@�ӪŪ�list
	//�⦡��18 * 16 + (1+16)* 16 + 18*16
	
	public static void main(String args[])
	{
		
		
		int number,sinkstep,setfireTime,timelen,percent;
		k = new Scanner(System.in);
		
		configPath = "./500X500/8sink/"; 
		Enviorment.open_randomwalk =0; //�H������ 0=���� 1=�}��
		
		//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^���|�b�o��^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
		try {
			
			
			config = new Scanner(new File(configPath+"config.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 ���إ߳���
		 �M��߰ݭn�X��sensor
		 */
//		System.out.println("��J��:");
		config.nextLine();
		Height = config.nextInt();
		config.nextLine();
		
//		System.out.println("��J�e:");
		config.nextLine(); 
		Width = config.nextInt();
		config.nextLine();
		
		Length = (Height < Width)? Height : Width;
		
//		System.out.println("��J�q�T�d��:");
		config.nextLine();
		communication_range = config.nextInt();
		config.nextLine();
		
//		System.out.println("��JSensor�ƶq:");
		config.nextLine();
		number= config.nextInt();
		config.nextLine();
		
//		System.out.println("��JSink�ƶq:");
		config.nextLine();
		sinknum= config.nextInt();
		config.nextLine();
		
//		System.out.println("��JSink�C�L�h�[�n�o�@���W�U�ʥ]:");
		config.nextLine();
		setfireTime = config.nextInt();
		config.nextLine();
		
//		System.out.println("��JSink�b�a��X�b�C�X�Ӷ��j�n�o�@���W�U�ʥ]:");
		config.nextLine();
		setfireX = config.nextInt();
		config.nextLine();
		
//		System.out.println("��JSink�b�a��Y�b�C�X�Ӷ��j�n�o�@���W�U�ʥ]:");
		config.nextLine();
		setfireY = config.nextInt();
		config.nextLine();
		
		Extended_hop = setfireX/14;
//		System.out.println("��JSink�H���@�Ӥ�V���ᨫ�X�B:");
		config.nextLine();
		sinkwalk = config.nextInt();
		ShouldWalk = sinkwalk;
		config.nextLine();
		
//		System.out.println("��Jsensor�䤣��track��close�ɭn�H�����X�B�M��:");
		config.nextLine();
		randomwalk_step = config.nextInt();
		config.nextLine();
		
//		System.out.println("��Jsensor�䤣��footprint�ɭn�H�����X�B�M��(for trail):");
		config.nextLine();
		randomwalk_trailsteps = config.nextInt();
		config.nextLine();
		
//		System.out.println("��Jsensor�䤣��footprint�ɭn�H�����X���M��(for trail):");
		config.nextLine();
		randomwalk_trailtimes = config.nextInt();
		config.nextLine();
		
		
//		System.out.println("��J�ɶ�����");
		config.nextLine();
		timelen = config.nextInt();
		config.nextLine();
		
//		System.out.println("��J�ƥ�o�;��v(�ʤ���)");
		config.nextLine();
		percent = config.nextInt();
		config.nextLine();
		
//		System.out.println(�ϥΪ����ո�Ƨ�(���Ƨ��d�� \);
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
			System.out.println("���]");
			}
			else
			{
			System.out.println("GGGGGGGGGGGGGGGGGGGGGGGG");
			}
			
			System.out.println("�ϥΪ�config:"+configPath+"config.txt");
			System.out.println("�ϥΪ������ɮצs���m"+filePath);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	

	private static void start(int h,int w,int n,int sinkstep,int setfire,int sinknum,int tl,int percent,int fireX,int fireY,int randomwalk_step) throws IOException 
	{
		boolean a=false;
		 //���]�m�n�a��sensor���_�l��m
		sensor_place(h,w, n);
		//new SceneGraph(p,Enviorment.length);
		
		//�b�]�m�Usink���_�l��m
		
		//���H���n�����b�o��
		
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
			
			//�M��w���M�w�o�@������Ϊ� sink �b�Ҧ��ɶ��ӫ�򲾰�(�s�b�}�C��)  �T�O7�غt��k  sink�����ʤ覡���@��
			sink_move_set(sinkstep , tl);
			sink_move_set(sinknum,filePath+"sink_move_set_data");
			
			//�M�w�o�@��������X�Ӯɶ��I�|�o�ͨƥ�
			event_time(tl,percent);
			event_time(filePath+"event_time_data");
		}
		
		System.out.println("���ո��Ū������");
		
		TOOL.fileprefix=null;
		TOOL.printData(event,sink_move);
//		�U���T�ӴN�O�ھڦP�˪��a�� �P�˪����ʤ覡 �P�˪��ƥ�o���I�Ӽ���
	 
		TOOL.cleanLog(configPath+"TAG�έp.txt");
		TOOL.printLog("\tGuideSize\tQuerySize\tRouteSize", configPath+"TAG�έp.txt");
		
		
		TOOL.fileprefix = "By_X";
		TOOL.Broadcast_clean =true;
		TOOL.cleanLog(configPath+"By_X���\�v.txt");
		start_byX(h,w,n,sinkstep,fireX,sinknum,tl);
		TOOL.cleanLog(configPath+"By_XHOP�Ʋέp.txt");
		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_XHOP�Ʋέp.txt", true)));
		TOOL.PrintHop(1,pen, Enviorment.countX,tableName,""+Enviorment.totalHop[1],Xhop10,Xhop20,Xhop30,Xhop40,Xhop50,Xhop60,Xhop70,Xhop80,Xhop90,Xhop100);
		TOOL.printLog("By_X\t"+CountGuideSize[Enviorment.GuideSize_name.GuideSize_X.ordinal()]+"\t"+CountQuerySize[Enviorment.QuerySize_name.QuerySize_X.ordinal()]+"\t"+CountRouteSize[Enviorment.RouteSize_name.RouteSize_X.ordinal()], configPath+"TAG�έp.txt");
	
		
		
		TOOL.fileprefix = "By_Time";
		TOOL.cleanLog(configPath+"By_Time���\�v.txt");
		TOOL.Broadcast_clean =true;
		start_byTime(h,w,n,sinkstep,setfire,sinknum,tl);
		TOOL.cleanLog(configPath+"By_TimeHOP�Ʋέp.txt");
		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_TimeHOP�Ʋέp.txt", true)));
		TOOL.PrintHop(0,pen, Enviorment.countTime,tableName,""+Enviorment.totalHop[0],Timehop10,Timehop20,Timehop30,Timehop40,Timehop50,Timehop60,Timehop70,Timehop80,Timehop90,Timehop100 );
		TOOL.printLog("" , "TAG�έp.txt");
		TOOL.printLog("By_time\t"+CountGuideSize[Enviorment.GuideSize_name.GuideSize_time.ordinal()]+"\t"+CountQuerySize[Enviorment.QuerySize_name.QuerySize_time.ordinal()]+"\t"+CountRouteSize[Enviorment.RouteSize_name.RouteSize_time.ordinal()], configPath+"TAG�έp.txt");	
		
		
		
		
		
		TOOL.fileprefix = "By_XY";
		TOOL.Broadcast_clean =true;
		TOOL.cleanLog(configPath+"By_XY���\�v.txt");
		start_byXY(h,w,n,sinkstep,fireX,fireY,sinknum,tl);
		TOOL.cleanLog(configPath+"By_XYHOP�Ʋέp.txt");
		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_XYHOP�Ʋέp.txt", true)));
		TOOL.PrintHop(2,pen, Enviorment.countXY,tableName,""+Enviorment.totalHop[2],XYhop10,XYhop20,XYhop30,XYhop40,XYhop50,XYhop60,XYhop70,XYhop80,XYhop90,XYhop100);
		
		TOOL.printLog("" , "TAG�έp.txt");
		TOOL.printLog("By_XY\t"+CountGuideSize[Enviorment.GuideSize_name.GuideSize_XY.ordinal()]+"\t"+CountQuerySize[Enviorment.QuerySize_name.QuerySize_XY.ordinal()]+"\t"+CountRouteSize[Enviorment.RouteSize_name.RouteSize_XY.ordinal()], configPath+"TAG�έp.txt");
		TOOL.printLog("" , "TAG�έp.txt");
		TOOL.printLog(""+Enviorment.totalHop[1] , configPath+"TAG�έp.txt");
		TOOL.printLog(""+Enviorment.totalHop[0] , configPath+"TAG�έp.txt");
		TOOL.printLog(""+Enviorment.totalHop[2] , configPath+"TAG�έp.txt");
//		/*��track�B��close�B��random walk "DDRP"*/
//		TOOL.fileprefix = "By_1";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_1���\�v.txt");
//		start_by1(h,w,n,sinkstep,sinknum,tl);
//		TOOL.cleanLog(configPath+"By_1HOP�Ʋέp.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_1HOP�Ʋέp.txt", true)));
//		TOOL.PrintHop(3,pen,count1,tableName1,RouteSize_name.RouteSize_1.ordinal(),hop10_1,hop20_1,hop30_1,hop40_1,hop50_1,hop60_1,hop70_1,hop80_1,hop90_1,hop100_1,hop1000_1);
//		
//		
//
//		/*��random walk�B������ʥ]   "Trail based" */
//		TOOL.fileprefix = "By_2";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_2���\�v.txt");
//		start_by2(h,w,n,sinkstep,sinknum,tl,randomwalk_step);
//		TOOL.cleanLog(configPath+"By_2HOP�Ʋέp.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_2HOP�Ʋέp.txt", true)));
//		TOOL.PrintHop(4,pen,count2,tableName1,RouteSize_name.RouteSize_2.ordinal(),hop10_2,hop20_2,hop30_2,hop40_2,hop50_2,hop60_2,hop70_2,hop80_2,hop90_2,hop100_2,hop1000_2);
//		
//		
		/*��track�B��close�B�w���P��track  "HRDG"*/		
//		TOOL.fileprefix = "By_3";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_3���\�v.txt");
//		start_by3(h,w,n,sinkstep,sinknum,tl,randomwalk_step);
//		TOOL.cleanLog(configPath+"By_3HOP�Ʋέp.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_3HOP�Ʋέp.txt", true)));
//		TOOL.PrintHop(5,pen,count3,tableName,RouteSize_name.RouteSize_3.ordinal(),hop10_3,hop20_3,hop30_3,hop40_3,hop50_3,hop60_3,hop70_3,hop80_3,hop90_3,hop100_3);
//		
//		
//
//		TOOL.fileprefix = "By_RandomWalk";
//		TOOL.Broadcast_clean =true;
//		TOOL.cleanLog(configPath+"By_RandomWalk���\�v.txt");
//		start_byRandomWalk(h,w,n,sinkstep,sinknum,tl,randomwalk_step);
//		TOOL.cleanLog(configPath+"By_RandomWalkHOP�Ʋέp.txt");
//		pen = new PrintWriter(new BufferedWriter(new FileWriter(configPath+"By_RandomWalkHOP�Ʋέp.txt", true)));
//		TOOL.PrintHop(6,pen,countRandom,tableName1,RouteSize_name.RouteSize_RandomWalk.ordinal(),RandomWalkhop10,RandomWalkhop20,RandomWalkhop30,RandomWalkhop40,RandomWalkhop50,RandomWalkhop60,RandomWalkhop70,RandomWalkhop80,RandomWalkhop90,RandomWalkhop100,RandomWalkhop1000);
		
		
		
//			print(l,n,p);//�L�Xsensor��m
			
			
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
					switch (sink_move[j][i]) // 0�W  1��   2�U    3�k 
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
		//sinkstep = sink���@�B�h��
		//t1 = �`�@�ɶ�����
		//s = �ĴX��sink���s��
		sink_move = new int[s.length][(tl/sinkstep)+1];
		int temp_way=-1,stepcount=0,way = -1;
		for(int i = 0 ; i < s.length ; i++)
		{
			stepcount=0;
			int x = s[i].x,y = s[i].y; //x=sink��X�b    y=sink��Y�b
			for(int j = 0 ; j < sink_move[i].length ; j++)
			{
				if(stepcount % Enviorment.ShouldWalk == 0 ) //������M�w�U������V
				{
					stepcount = 0;
					
					way = move(x, y, Height,Width,temp_way); //�M�w��V�A���ˬd�U�Ӥ�V�|���|�X��
					temp_way = way;
					
				}
				
				
				switch(way) // 0�W  1��   2�U    3�k 
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
				s[k].x=((int) (Math.random()*w)*Enviorment.setfireX)%w;//�üƨM�w�y��
				s[k].y=((int) (Math.random()*h)*Enviorment.setfireY)%h;
//				s[k].x=(int) (Math.random()*w);//�üƨM�w�y��
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
			
				else//���\�G sink
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
//		p[0].x=(int) (Math.random()*l);//�üƨM�w�y��  �Ĥ@�����N��m
//		p[0].y=(int) (Math.random()*l);
//		p[0].data_buffer=new int [n];
//		p[0].data_bufferHopCount = new int [n];
		for(int i=0;i<n;i++) //Ū�ɮר��G�I�y��
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
		//�o��O�^�h�ˬd�O�_�u������S��
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = 0 ; i < p.length ; i++)
			if(p[i].nearby.size() == 0)
			{
				System.out.println("����S�H????+ " +i);
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
			
			System.out.println("�W�z�ʧ@����");
			(new Scanner(System.in)).nextLine();
		}
	}
	
	private static void start_byTime(int h,int w,int sensor,int step,int setfire,int sinknum, int tl) throws IOException 
	{
		sink.searchNum = 0;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //�T�wsensor �̤W�U���k�� sensor ��
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_Time'�k'�䪺"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_Time'��'�䪺"+"Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO �۰ʲ��ͪ� catch �϶�
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		int fp_step =1; // �C�B�o�@��footprint 
		
		while(time < tl)
		{
			
			if(time % step == 0) // ���ʨƥ�
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					int way =sink_move[i][time/step]; 
					if(time%fp_step==0){
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_time.ordinal());//�ofootprint
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
			
			if(time % setfire == 0 ) // sink �]���ɶ���F �n�o�W�U�ʥ]
			{
				for(int i = 0 ; i <s.length ; i++)
					sink.Broadcast(Sensor, Sink, i, time,GuideSize_name.GuideSize_time.ordinal());
			}
			if(time != 0)
			{
				
				for(int i = 0 ; i < event[time].length ; i++)
				{
					//TOOL.printMap(Sensor,Sink,l,time,"By_Time");
					TOOL.PrintEventHead("By_Time'�k'�䪺"+"Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
					TOOL.PrintEventHead("By_Time'��'�䪺"+"Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
					
				  	//�l�ܫʥ]���ʧ@�}�l				
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
		link_news(Sensor.length,Sensor); //�T�wsensor �̤W�U���k�� sensor ��
		
		for(int i = 0 ; i < Sink.length ; i++)
			sink.Broadcast(Sensor, Sink, i, 0,GuideSize_name.GuideSize_X.ordinal());
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'�k'�䪺"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'��'�䪺"+"Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO �۰ʲ��ͪ� catch �϶�
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
			if(time % step == 0) // ���ʨƥ�
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
					if(Sink[i].x % fireX == 0)  //sensor�]�� X�b���j��F�n�W�U�o  
						sink.Broadcast(Sensor, Sink, i, time,GuideSize_name.GuideSize_X.ordinal());
				}
			}
			
			if(time != 0)
			for(int i = 0 ; i < event[time].length ; i++)
			{
				//TOOL.printMap(Sensor,Sink,l,time,"By_X");
				try {
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'�k'�䪺"+"Event.txt", true)));
					pen.println();
					pen.println("�ƥ�ɶ�:"+time);
					pen.println("�o�ͨƥ�Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_X'��'�䪺"+"Event.txt", true)));
					pen.println();
					pen.println("�ƥ�ɶ�:"+time);
					pen.println("�o�ͨƥ�Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
				} catch (IOException e) {
					// TODO �۰ʲ��ͪ� catch �϶�
					e.printStackTrace();
				}
			  	//�l�ܫʥ]���ʧ@�}�l				
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
		link_news(Sensor.length,Sensor); //�T�wsensor �̤W�U���k�� sensor ��
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'�k'�䪺"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'��'�䪺"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'�W'����"+"Event.txt", false)));
			pen.close();
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'�U'����"+"Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO �۰ʲ��ͪ� catch �϶�
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		for(int i = 0 ; i < Sink.length;i++)
		{
			sink.BroadcastXY(Sensor, Sink, i, 0,GuideSize_name.GuideSize_XY.ordinal(),true);   //�_�l�N�n���|�Ӥ�V���ʥ]
			sink.BroadcastXY(Sensor, Sink, i, 0,GuideSize_name.GuideSize_XY.ordinal(),false);   //�_�l�N�n���|�Ӥ�V���ʥ]
		}
		
		while(time < tl)
		{
			if(time % step == 0) // ���ʨƥ�
			{
				for(int i = 0 ; i < sinknum ; i++)
				{
					//�C�@�����O������
					int way =sink_move[i][time/step];
					//�e���w�g�]�m�n���ʸ��u �o����X�Ӧ樫??
					sink.foot(Sensor,Sink[i],i,time,way,Enviorment.BeconSize_name.BeconSize_XY.ordinal());
					
					//way �N�O��V  2/3�ݭ��Ӥ�V�M�w���ʮy��
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
					
					if(Sink[i].x % fireX == 0)  //sensor�]�� X�b���j��F�n�W�U�o  
					{
						sink.BroadcastXY(Sensor, Sink, i, time,GuideSize_name.GuideSize_XY.ordinal(),true);									
					}
					if(Sink[i].y % fireY == 0)  //sensor�]�� y�b���j��F�n�W�U�o
					{
						sink.BroadcastXY(Sensor, Sink, i, time,GuideSize_name.GuideSize_XY.ordinal(),false);
					}
		
				}
			}
			if(time != 0)
			//���ʧ����� �ݳo�@��(�B)  ���S���ƥ�o�� 
			for(int i = 0 ; i < event[time].length ; i++)
			{
				//�o�O��s���Ǥ��Ϊ�
				//TOOL.printMap(Sensor,Sink,l,time,"By_XY");
				try {
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'�k'�䪺"+"Event.txt", true)));
					pen.println();
					pen.println("�ƥ�ɶ�:"+time);
					pen.println("�o�ͨƥ�Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'��'�䪺"+"Event.txt", true)));
					pen.println();
					pen.println("�ƥ�ɶ�:"+time);
					pen.println("�o�ͨƥ�Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'�W'����"+"Event.txt", true)));
					pen.println();
					pen.println("�ƥ�ɶ�:"+time);
					pen.println("�o�ͨƥ�Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
					
					pen = new PrintWriter(new BufferedWriter(new FileWriter("By_XY'�U'����"+"Event.txt", true)));
					pen.println();
					pen.println("�ƥ�ɶ�:"+time);
					pen.println("�o�ͨƥ�Sensor id:"+event[time][i].sensorId+":("+Sensor[event[time][i].sensorId].x+","+Sensor[event[time][i].sensorId].y+")");
					pen.close();
				} catch (IOException e) {
					// TODO �۰ʲ��ͪ� catch �϶�
					e.printStackTrace();
				}
			  	//�l�ܫʥ]���ʧ@�}�l				
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
		link_news(Sensor.length,Sensor); //�T�wsensor �̤W�U���k�� sensor ��
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_1Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO �۰ʲ��ͪ� catch �϶�
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // ���ʨƥ�
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
			  	//�l�ܫʥ]���ʧ@�}�l	
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.count1 ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchBy1(Sensor,0, time, "By_1");
				//�Y�@��sensor�o�ͪ��ƥ� �I�s�F�ۤv����k
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
		link_news(Sensor.length,Sensor); //�T�wsensor �̤W�U���k�� sensor ��
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_2Event.txt", false)));
			pen.close();
		} catch (IOException e1) {
			// TODO �۰ʲ��ͪ� catch �϶�
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // ���ʨƥ�
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
			  	//�l�ܫʥ]���ʧ@�}�l				
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.count2 ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchBy2(Sensor,0, time, "By_2");
				//�Y�@��sensor�o�ͪ��ƥ� �I�s�F�ۤv����k
			}
			time++;
			//k.nextLine();
			
		}
		
		System.out.println("start_by2 �]���F");
				
	}
	
	private static void start_by3(int h,int w,int sensor,int step,int sinknum, int tl,int randomwalk_step) 
	{
		sink.searchNum = 3;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //�T�wsensor �̤W�U���k�� sensor ��
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_3Event.txt", false)));
			pen = new PrintWriter(new BufferedWriter(new FileWriter("trackBy_3Event.txt", false)));
			pen = new PrintWriter(new BufferedWriter(new FileWriter("track�έpBy_3Event.txt", false)));
		} catch (IOException e1) {
			// TODO �۰ʲ��ͪ� catch �϶�
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // ���ʨƥ�
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
						if(Sensor[temp].foot.trackID!=-1 && time - Sensor[temp].eventTime >= Enviorment.Length)//�p�GtrackTime���ɶ��M�{�b���ɶ��t�F500�h�R��
						{
							TOOL.printStep("�ɶ�:"+time+"�N����H"+Sensor[temp].sensorid, "By_3", "Track");
							Sensor[temp].foot.trackID=-1; //�N��(track�L�Ĥ�)
							Sensor[temp].eventTime = Integer.MAX_VALUE; //�N��(track�L�Ĥ�)
							
						}
					}
				}
			}
			
			if(time != 0)
			for(int i = 0 ; i < event[time].length ; i++)
			{
				
				//TOOL.printMap(Sensor,Sink,l,time,"By_3");
				TOOL.PrintEventHead("By_3Event.txt", time, event[time][i].sensorId, Sensor[event[time][i].sensorId].x, Sensor[event[time][i].sensorId].y);
				
			  	//�l�ܫʥ]���ʧ@�}�l				
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.count3 ++;
				Enviorment.closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				
				Sensor[event[time][i].sensorId].searchBy3(Sensor,0, time, "By_3");
				//�Y�@��sensor�o�ͪ��ƥ� �I�s�F�ۤv����k
			}
			ArrayList<point> HasT = new ArrayList<point>();
			for(int temp=0;temp<Sensor.length;temp++)
			{
				if(Sensor[temp].foot.trackID > -1)
					HasT.add(Sensor[temp]);
				if(Sensor[temp].data_keep>=1)
				{
					Enviorment.count3 ++;
					//�O�d����Ƹչ϶ǥX�ɧ�s�̪� Sink (�����N�u�H�̪�ƥ�o�ͪ���U)
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
			TOOL.printStep("","By_3","track�έp");
			TOOL.printStep(t.toString(), "By_3","track�έp");
			
			time++;
			//k.nextLine();
			
		}
		for(int i = 0 ; i < Sink.length ; i++)
		{
			TOOL.printLog("Sink ID:" + i + "����F " +Sink[i].count , "By_3���\�v.txt");
			System.out.println(Sink[i].count);
		}

				
	}
	private static void start_byRandomWalk(int h,int w,int sensor,int step,int sinknum, int tl,int randomwalk_step) 
	{
		sink.searchNum = 4;
		point[] Sensor = TOOL.copyPoint(p);
		sink[] Sink = TOOL.copySink(s);
		link_news(Sensor.length,Sensor); //�T�wsensor �̤W�U���k�� sensor ��
		
		
		PrintWriter pen;
		try {
			pen = new PrintWriter(new BufferedWriter(new FileWriter("By_RandomWalkEvent.txt", false)));
		} catch (IOException e1) {
			// TODO �۰ʲ��ͪ� catch �϶�
			e1.printStackTrace();
		}
		
		int time = 0;
		int progress = 0;
		while(time < tl)
		{
			if(time % step == 0) // ���ʨƥ�
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
				
			  	//�l�ܫʥ]���ʧ@�}�l				
				Sensor[event[time][i].sensorId].randomwalk_step = randomwalk_step;
				Enviorment.countRandom ++;
				closest_sink = TOOL.computeTheClosestSink(Sensor[event[time][i].sensorId], Sink);
				Sensor[event[time][i].sensorId].searchByRandomWalk(Sensor,0, time, "By_RandomWalk");
				//�Y�@��sensor�o�ͪ��ƥ� �I�s�F�ۤv����k
			}
			time++;
			//k.nextLine();
			
		}
		

				
	}
	public static void clear_and_announce(sink s[], point map[])
	{
		for(int i=0; i < map.length;i++)
		{
			map[i].sink_nearhere =0; //��Ҧ���sinknearhere���
			map[i].foot.near = null;
		}
		for(int l=0;l<s.length;l++)
		{
			for(int k =0;k<map.length;k++)
			{
				if (TOOL.COMP_Dis(s[l], map[k])<=communication_range) //�p��q�T�d�򤺪�sensor
				{
					map[k].sink_nearhere =1; //��p��@�ʪ�sinknearhere =1
					map[k].foot.id = l;
				}
			}
		}
	}
	
	private static int move(int x , int y,int h,int w,int tempway) {
		// TODO Auto-generated method stub
		int way = -1; // 0�W  1��   2�U    3�k   �o��u�O��l��
		while(way == -1)
		{
			Enviorment.ShouldWalk = Enviorment.sinkwalk;
			//�M�w�n�W�U���k
			way = (int)(Math.random()*4);
			//�p�G�e�@�B�O���W�A�{�b�M�w����V�N���੹�U�Ω��W
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
			
			
			//���Ф@��temp�I�A�ˬd�U�@�B�|���|�]�X�ɥ~
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
			//�ˬd�o��temp�I���ʤ���|���|�W�X���
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
				//�ˬdtemp�I���ʤ���|���|�]�Xsensor����P�����d�򤧥~
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
		
		
		//�d����
		
	}

	private static void Simulation1(int timelen) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner config0 = new Scanner(new File("By_Time���\�v.txt"));
		Scanner configX = new Scanner(new File("By_X���\�v.txt"));
		Scanner configXY = new Scanner(new File("By_XY���\�v.txt"));
		Scanner config1 = new Scanner(new File("By_1���\�v.txt"));
		Scanner config2 = new Scanner(new File("By_2���\�v.txt"));
		Scanner config3 = new Scanner(new File("By_3���\�v.txt"));
		Scanner config4 = new Scanner(new File("By_RandomWalk���\�v.txt"));
		
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
					System.out.println(now_time+" round:"+"�|�L���");
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
		int upOrDown = 0; // -1�U   0�L  1�W
		int leftOrRight = 0; // -1��   0�L  1�k 
		int sinktime_UD = -1; //boradcast ���ɶ�(�p�G������)
		int sinktime_LR = -1; 
		int sinkid; //boradcast �� sink ��id
		int sensorid; //��sensor�� id
		int sink_nearhere = 0; //�ΨӰѦ�sink�O�_�b���� 0 =�S�� 1=��
		int data_buffer[] = null; //0 = �S���O�d��� 1=���O�d���
		int data_bufferHopCount[] =null; //�����ʥ]��hopcount��
		int popOut_Index = 0; //�O��buffer �̤U�@�ӦA���X�Ӫ��ɭԭn�Q��󪺸��;
		int BroadCast_hop_UD = -1;
		int BroadCast_hop_LR = -1;
		int eventTime = Integer.MAX_VALUE;
		
		int data_keep=0; // ����Ưd�bsensor�̭��٨S�� >1 , �S�� =0
		point traceBroadCast_UD;  //broadcast �ӷ�_�W�U
		point traceBroadCast_LR;  //broadcast �ӷ�_���k
		point up,down,right,left;  //�o��O�̻����W�U���k
		point topRight,topLeft,buttomRight,buttomLeft;  //�o��O�̻����k�W ���W �k�U ���U
		ArrayList<point> nearby = new ArrayList<point>();
		foot foot;
		
		point()
		{
			
			
		}	
		//�H�U�ODDRP algorithm
		public void searchBy1(point[] p ,int checkTime ,int lastTime , String prefix)
		{
			int hop = 0;
			boolean find = false;
			point now = this;
			if(!find && now.foot != null)
			{
				while(!find && hop < Enviorment.randomwalk_step)
				{
					if(now.foot != null && now.foot.closeID!=-1) //������CLOSE
					{
						TOOL.printStep(now.sensorid +"��C" , prefix, "");
						if(now.foot.closetime == lastTime)
						{
							TOOL.printStep(this.sensorid +"���F sink " +now.foot.id +"��"+now.sensorid, prefix, "");
							//���ƥ�X�h
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
							TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 1", prefix+"���\�v.txt");
						}
						else
						{
							
							int last_close=now.foot.closetime;
							//�T�{�ۤv���ɶ��I���O�̱ߪ�closetime�ɶ��I
							point last_closeID=null;
							
								
							//�����P��ɶ��̱ߪ�closetime�Mtracktime�H��ID
							//�@�}�l��close�Mtrack�]��-1���즳���~�令�����I��ID
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
									//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
								{
									last_close=next.foot.closetime;
									last_closeID = next;
								}
							}
							
							if(last_closeID!=null)
							{
								TOOL.printStep(now.sensorid +"����ߪ�close ID = " +last_closeID.sensorid , prefix, "");
								now=last_closeID;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
								hop++;
								now.data_keep = 0;
							}
							else
							{
								
								TOOL.printStep(now.sensorid +"close �l��F?" , prefix, "");
								break;
							}
						}
					}
					//�U���O�ۤv�S��CLOSE�B��TRACK
					else if (now.foot != null && now.foot.closeID==-1 && now.foot.trackID!=-1)
					{
						TOOL.printStep(now.sensorid +"��T" , prefix, "");
						int last_track=now.foot.tracktime,last_close=-1;
						//�T�{�ۤv���ɶ��I���O�̱ߪ��ɶ��I
						point last_trackID=null,last_closeID=null;
							
						//�����P��ɶ��̱ߪ�closetime�Mtracktime�H��ID
						//�@�}�l��close�Mtrack�]��-1���즳���~�令�����I��ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							
							
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
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
							TOOL.printStep(now.sensorid +"����ߪ�close ID = " +last_closeID.sensorid , prefix, "");
							now=last_closeID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
						}
						else if(last_trackID!=null)
						{
							TOOL.printStep(now.sensorid +"���󦭪�track ID = " +last_trackID.sensorid , prefix, "");
							now=last_trackID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
						}
					}
					
					//�ڨS��TRACK�]�S��CLOSE
					else 
					{
						TOOL.printStep(now.sensorid +"�ۤv���S�� �}�l�ݪ���" , prefix, "");
						//  System.out.println("��948��");
						int last_track=(int)1e6,last_close=-1;
						//�T�{�ۤv���ɶ��I���O�̱ߪ��ɶ��I
						point last_trackID=null,last_closeID=null;
						
							
						//�����P��ɶ��̱ߪ�closetime�Mtracktime�H��ID
						//�@�}�l��close�Mtrack�]��-1���즳���~�令�����I��ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
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
							TOOL.printStep(now.sensorid +"���C :"+last_closeID.sensorid  , prefix, "");
							this.foot.tracktime = 0;
							this.foot.trackID = last_closeID.sensorid;
							//�ۤv���OTRACK�]���OCLOSE�A���P�D��CLOSE�A�ҥH�ۤv�ܦ�TRACK
							now=last_closeID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
							//�ڵo�X�ʥ]����A�n��P��SENSOR�令TRACK�ATRACK�ɶ��n+1
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
							TOOL.printStep(now.sensorid +"���T :"+last_trackID.sensorid  , prefix, "");
							this.foot.tracktime = (last_trackID.foot.tracktime>=lastTime)?last_trackID.foot.tracktime+1:lastTime;
							this.foot.trackID = last_trackID.sensorid;
							//�ۤv���OTRACK�]���OCLOSE�A���P�D��TRACK�A�ҥH�ۤv�ܦ�TRACK
							now=last_trackID;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
							hop++;
							now.data_keep = 0;
							//�ڵo�X�ʥ]����A�n��P��SENSOR�令TRACK�ATRACK�ɶ��n+1
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
						else  //�ۤv�Ϊ��񳣨S��track �P close
						{
							Enviorment.success_rate = 0;
							Enviorment.success_count = 0;
							point now_node =now;
							
							
								//random walk (DDRP ��)
								now = now_node;
								long[] data = Enviorment.CountRouteSize;
								TOOL.printStep(now.sensorid +"�ۤv�Ϊ���ҵL �}�lrandom ", prefix, "");
								for(int j=0;j<Enviorment.randomwalk_trailtimes;j++)
								{
									for(int k = 0 ;k < Enviorment.randomwalk_trailsteps ; k++)
									{
										int random = (int)(Math.random()*now.nearby.size());
										point next = now.nearby.get(random); 
										//TOOL.printStep(now.sensorid +"-> "+next.sensorid, prefix, "");
										now = next;
										//�U���o�ӬOrandom �ɪ�total hop�ƭp�� �S���w���n���\�~�|+
										Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_1.ordinal()] += 1;
										hop++;
										if(now.foot.closeID!=-1 || now.foot.trackID!=-1)
										{
											TOOL.printStep(now.sensorid +"��Random�����C��T hop= "+hop, prefix, "");
											
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
							TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: "+Ratio_of_success, prefix+"���\�v.txt");
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

		//�H�U�OTrail
		public void searchBy2(point[] p ,int checkTime ,int lastTime , String prefix)
		{
			int hop = 0; //�o�@���n�Ǫ�;
			boolean find = false;
			point now = this;
			
			if(!find && now.foot != null)
			{
				while(!find && hop < Enviorment.randomwalk_trailsteps)
				{
					if(now.foot != null && now.foot.closeID!=-1) //������CLOSE
					{
						if(now.foot.closetime == lastTime)
						{
							//���ƥ�X�h
							this.data_keep = 0;
							now.data_keep=0;
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
							find = true;
						}
						else
						{
							int last_close=-1;
							//�T�{�ۤv���ɶ��I���O�̱ߪ�closetime�ɶ��I
							//�����P��ɶ��̱ߪ�closetime�H��ID
							//�@�}�l��close�]��-1���즳���~�令�����I��ID
							point last_closeID=null;
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
									//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
								{
									hop++;
									find = true;  //�u�O���F����
									last_close=next.foot.closetime;
									last_closeID = next;
								}
							}
							now = last_closeID;
							
							while(now.foot.closetime != lastTime)  //���ƤW��for���ʧ@ ����{�b�d�߫ʥ]���ɶ��ܦ��F�̱ߪ��ɶ�
							{
								for(int i = 0 ; i < now.nearby.size() ; i++)
								{
									point next = now.nearby.get(i);
									if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
										//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
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
						
						TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 1", prefix+"���\�v.txt");
						
					}

					//�ڨS��CLOSE
					else 
					{
						int last_close=-1;
						//�T�{�ۤv���ɶ��I���O�̱ߪ��ɶ��I
						point last_closeID=null;
						
							
						//�����P��ɶ��̱ߪ�closetime�H��ID
						//�@�}�l��close�]��-1���즳���~�令�����I��ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
							{
								last_close=next.foot.closetime;
								last_closeID = next;
							}
						}
						
						if(last_closeID==null)
						{
							System.out.printf("�H�����h %d\n",now.sensorid);
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
									
									while(now.foot.closetime != lastTime)  //���ƤW��for���ʧ@ ����{�b�d�߫ʥ]���ɶ��ܦ��F�̱ߪ��ɶ�
									{
										last_close = now.foot.closetime;
										for(int i = 0 ; i < now.nearby.size() ; i++)
										{
											point next = now.nearby.get(i);
											if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
												//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
											{
												hop++;
												Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_2.ordinal()] += 1;
												last_close=next.foot.closetime;
												last_closeID = next;
											}
										}
										if(last_close == now.foot.closetime)
											System.out.println("����䤣���ߪ�close");
										if(last_closeID != null)
											now = last_closeID;
										else
										{
											System.out.println("close �l��F???");
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
							TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: "+Ratio_of_success, prefix+"���\�v.txt");
							break;
						}
						
						else if(last_closeID !=null) //�ۤv�@�}�l�S�� ���P�D���H�� close
						{
							
							if(last_closeID.foot.closetime != lastTime)
							{
								while(now.foot.closetime != lastTime)  //���ƤW��for���ʧ@ ����{�b�d�߫ʥ]���ɶ��ܦ��F�̱ߪ��ɶ�
								{
									for(int i = 0 ; i < now.nearby.size() ; i++)
									{
										point next = now.nearby.get(i);
										if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
											//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
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
							TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 1", prefix+"���\�v.txt");
							break;
						}
							
					}
				}
			}
		}
			
			
		//�H�U�OHRDG 
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

					if(now.foot != null && now.foot.closeID!=-1) //������CLOSE
					{
						
						if(now.foot.closetime == lastTime && now.foot.isInnerClose)
						{
							if(this.eventTime == Integer.MAX_VALUE)
								this.eventTime = lastTime; //�o�̥N��F���ƶǨ�sink
							StringBuilder r = new StringBuilder();
							for(int i = 0 ; i < route.size() ; i++)
								r.append(route.get(i).sensorid+" > ");
							
							TOOL.printStep(this.sensorid +"���F sink " +now.foot.id +"��"+now.sensorid, prefix, "");
							TOOL.printStep("route: " +r.toString(), prefix, "");
							//System.out.println(now.data_keep);
							now.foot.near.count += now.data_keep;
							if(now.foot.id == Enviorment.closest_sink)
								Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_3.ordinal()] += 1;
							//���ƥ�X�h
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
								
								
								//�ڦ�close �ӥBclose ���ɶ��Mevent�o�ͪ��ɶ��@�ˡA��HOP COUNT�[�@
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							find = true;
						}
						else if(now.foot.closetime == lastTime && !now.foot.isInnerClose)
						{
							//���̱ߪ�close ���O�b�~��
							
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
						else //��close �����O�̱ߪ�
						{
							TOOL.printStep(now.sensorid +"���F ���O�̱ߪ�close �}�l����ߪ�close����" , prefix, "");
							int last_track=now.foot.closetime,last_close_i=now.foot.closetime,last_close_o=now.foot.closetime;
							//�T�{�ۤv���ɶ��I���O�̱ߪ�closetime�ɶ��I
							point last_trackID=null,last_closeID_i=null,last_closeID_o=null;
							
								
							//�����P��ɶ��̱ߪ�closetime(inner & outer)�Mtracktime�H��ID
							//�@�}�l��close�Mtrack�]��-1���즳���~�令�����I��ID
							for(int i = 0 ; i < now.nearby.size() ; i++)
							{
								point next = now.nearby.get(i);
								if(next.foot != null && next.foot.closeID!=-1 && ((next.foot.closetime>last_close_i && next.foot.isInnerClose) || (!now.foot.isInnerClose && next.foot.closetime==last_close_i && next.foot.isInnerClose)))
									//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
								{
									last_close_i=next.foot.closetime;
									last_closeID_i = next;
								}
								if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close_o && !next.foot.isInnerClose)
									//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
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
									
									//�ڦ�close �ӥBclose ���ɶ��O�̱ߪ��A���ƥ�X�h�C
									
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
									
									//�ڦ�close ���O�ڦ����󦭪�TRACK�ɶ��A�B�S������ߪ�close�C
									
								}
								now.data_keep = 0;
								now.popOut_Index = 0;
								now=last_trackID;
								hop++;
							}
							else
								System.out.println("��close �ٰl����?");
						}
					}
					//�U���O�ۤv�S��CLOSE�B��TRACK
					else if (now.foot != null && now.foot.closeID==-1 && now.foot.trackID!=-1)
					{
						TOOL.printStep(now.sensorid +"�SC��T" , prefix, "");
						int last_track=now.foot.tracktime,last_close=-1;
						//�T�{�ۤv���ɶ��I���O�̱ߪ��ɶ��I
						point last_trackID=null,last_closeID=null;
							
						//�����P��ɶ��̱ߪ�closetime�Mtracktime�H��ID
						//�@�}�l��close�Mtrack�]��-1���즳���~�令�����I��ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							
							
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
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
								this.eventTime = lastTime; //�o�̥N��F���ƶǨ����close
							TOOL.printStep(now.sensorid +"���C:"+last_closeID.sensorid , prefix, "");
							route.add(last_closeID);
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_closeID.data_buffer[last_closeID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_closeID.data_bufferHopCount[last_closeID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_closeID.popOut_Index++;
								
								if (last_closeID.data_keep < 1024)  last_closeID.data_keep++;
								//�����ɶ����ߪ�CLOSE�A���ƥᵹ�L
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							now=last_closeID;
							hop++;										
						}
						else if(last_trackID!=null)
						{
							if(this.eventTime == Integer.MAX_VALUE)
								this.eventTime = lastTime; //�o�̥N��F���ƶǨ����track
							route.add(last_trackID);
							TOOL.printStep(now.sensorid +"���T:"+last_trackID.sensorid , prefix, "");
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_trackID.data_buffer[last_trackID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_trackID.data_bufferHopCount[last_trackID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_trackID.popOut_Index++;
								
								if (last_trackID.data_keep < 1024)  last_trackID.data_keep++;
								
								//���ɶ�������TRACK
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							now=last_trackID;
							hop++;
						}
						else //track �_�F
						{
							break;
						}
						
					}
					
					//�ڨS��TRACK�]�S��CLOSE
					else 
					{
						TOOL.printStep(now.sensorid +"����]�S��", prefix, "");
						int last_track=(int)1e6,last_close=-1;
						//�T�{�ۤv���ɶ��I���O�̱ߪ��ɶ��I
						point last_trackID=null,last_closeID=null;
						
							
						//�����P��ɶ��̱ߪ�closetime�Mtracktime�H��ID
						//�@�}�l��close�Mtrack�]��-1���즳���~�令�����I��ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime>last_close)
								//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
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
								this.eventTime = lastTime; //�o�̥N��F���ƶǨ����close
							route.add(last_closeID);
							TOOL.printStep(now.sensorid +"�o�{C�F ��"+last_closeID.sensorid+"�֦�", prefix, "");
							now.foot.tracktime = 0;
							now.foot.trackID = last_closeID.sensorid;
							TOOL.printStep("�ɶ�:"+lastTime+" "+now.sensorid+"���C �ۤv��T ", "By_3", "Track");
							//�ۤv���OTRACK�]���OCLOSE�A���P�D��CLOSE�A�ҥH�ۤv�ܦ�TRACK
							
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_closeID.data_buffer[last_closeID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_closeID.data_bufferHopCount[last_closeID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_closeID.popOut_Index++;
								
								if (last_closeID.data_keep < 1024)  last_closeID.data_keep++;
								//�����ɶ����ߪ�CLOSE�A���ƥᵹ�L
								
							}
							
							now.data_keep = 0;
							now.popOut_Index = 0;
							
							
//							//�åB�q���P�D�ۤv�ܦ�track
							//�ڵo�X�ʥ]����A�n��P��SENSOR�令TRACK�ATRACK�ɶ��n+1
//							for(int i = 0 ; i < now.nearby.size() ; i++)
//							{
//								point next = now.nearby.get(i);
//								
//								if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
//								{
//									TOOL.printStep("�ɶ�:"+lastTime+" "+next.sensorid+"�Q"+now.sensorid+"�q��", "By_3", "Track");
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
								this.eventTime = lastTime; //�o�̥N��F���ƶǨ����track
							route.add(last_trackID);
							TOOL.printStep(now.sensorid +"�o�{T�F ��"+last_trackID.sensorid+"�֦�", prefix, "");
							now.foot.tracktime = last_trackID.foot.tracktime+1;
							now.foot.trackID = last_trackID.sensorid;
							TOOL.printStep("�ɶ�:"+lastTime+" "+now.sensorid+"���T �ۤv��T ", "By_3", "Track");
							//�ۤv���OTRACK�]���OCLOSE�A���P�D��TRACK�A�ҥH�ۤv�ܦ�TRACK
							
							for(int temp1=0;temp1<now.data_keep;temp1++)
							{
								now.data_bufferHopCount[temp1]++;
								Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_3.ordinal()] += 1;
								last_trackID.data_buffer[last_trackID.popOut_Index%Enviorment.bufferSize] = now.data_buffer[temp1];
								last_trackID.data_bufferHopCount[last_trackID.popOut_Index%Enviorment.bufferSize]= now.data_bufferHopCount[temp1];
								last_trackID.popOut_Index++;
								
								if (last_trackID.data_keep < 1024)  last_trackID.data_keep++;
								//�����ɶ����ߪ�CLOSE�A���ƥᵹ�L
								
							}
							now.data_keep = 0;
							now.popOut_Index = 0;
							
							

							//�ڵo�X�ʥ]����A�n��P��SENSOR�令TRACK�ATRACK�ɶ��n+1
//							for(int i = 0 ; i < now.nearby.size() ; i++)
//							{
//								point next = now.nearby.get(i);
//								if(next.foot == null)
//									next.foot = new foot();
//								if(next.foot != null && next.foot.closeID==-1 && next.foot.trackID==-1)
//								{
//									TOOL.printStep("�ɶ�:"+lastTime+" "+next.sensorid+"�Q"+now.sensorid+"�q��", "By_3", "Track");
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
					if(now.foot != null && now.foot.closeID!=-1 && now.foot.closetime == lastTime) //������CLOSE
					{
							Enviorment.CountRouteSize[Enviorment.RouteSize_name.RouteSize_RandomWalk.ordinal()] += 1;
							//���ƥ�X�h
							this.data_keep = 0;
							now.data_keep=0;
							find = true;
							if(now.foot.id == Enviorment.closest_sink)
								Enviorment.CountClosest_sink[Enviorment.Closest_sink_name.Closest_sink_RandomWalk.ordinal()] += 1;
							Enviorment.RandomWalkhop10++;
							TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 1", prefix+"���\�v.txt");
						
					}

					//�ڨS��CLOSE
					else 
					{
						int last_close=-1;
						//�T�{�ۤv���ɶ��I���O�̱ߪ��ɶ��I
						point last_closeID=null;
						//�����P��ɶ��̱ߪ�closetime�H��ID
						//�@�}�l��close�]��-1���즳���~�令�����I��ID
						for(int i = 0 ; i < now.nearby.size() ; i++)
						{
							point next = now.nearby.get(i);
							if(next.foot != null && next.foot.closeID!=-1 && next.foot.closetime ==lastTime)
								//�کP��Y�ӤH��FOOT�A�B�L�t��CLOSE�ʥ]�A�ӥB�L��CLOSE�ɶ��O�ڥثe�ݨ쪺�̱ߪ�
							{
								last_close=next.foot.closetime;
								last_closeID = next;
								
							}
						}
						
						if(last_closeID==null)
						{
							System.out.printf("�H�����h ~\n",now.sensorid);
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
							
							TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: "+Ratio_of_success, prefix+"���\�v.txt");
							break;
						}
						
						else if(last_closeID !=null)
						{
							Enviorment.RandomWalkhop10++;
							find = true;
							TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 1", prefix+"���\�v.txt");
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
				
				if(checkWE_andTime(searchE,lastTime,"'�k'�䪺",prefix,searchE.hop,true,RouteSizeName)) //�A���e�n�����Ǳ��� �򥻤W���b�̭� true �N�����̭��S��������
				{
					if(searchE.now.right != null) 
					{
						
						TOOL.printStep("'�k'�䪺�ʥ]���k��l�� ->" + "�B��:"+ searchE.hop  +" "+searchE.now.right.sensorid+"("+searchE.now.right.x+","+searchE.now.right.y+")","'�k'�䪺",prefix);
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
						
						TOOL.printStep("'�k'�䪺"+"�ʥ] �o�{�k��S�H ����V�k��l��","'�k'�䪺",prefix);
						if(searchE.findedLast_BroadCastTime > -1 && searchE.findedLast_BroadCastTime >= searchE.findedLast_footprintTime)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_BroadCastHop)*searchE.hop;
							int lastStep = searchE.RouteForLastBroadCast.size()-1;
							while(searchE.now != searchE.lastBroadcast)
							{
								TOOL.printStep("'�k'�䪺"+"�ʥ] ���̫�o�{��broadcast����  ->"+searchE.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchE.hop,"'�k'�䪺",prefix);
								searchE.now = searchE.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
								
							}
							
						}
						else if(searchE.findedLast_footprintTime > -1)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_footprintHop)*searchE.hop;
							int lastStep = searchE.RouteForLastFoot.size()-1;
							while(searchE.now != searchE.lastFoot)
							{
								TOOL.printStep("'�k'�䪺"+"�ʥ] ���̫�o�{��footprint����  ->"+searchE.RouteForLastFoot.get(lastStep).sensorid,"'�k'�䪺",prefix);
								searchE.now = searchE.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
								
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
				else if(searchE.finded && !searchE.endSearchBroadcast) //�b�ݭn���e���(���|�W�����ݨ�̫᪺foot)
				{
					
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
					Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchE.hop;
				}
				searchE.hop++;
			}	
				
			while(searchW.hop <  Enviorment.randomwalk_step && !searchW.finded)
			{
				
				if(checkWE_andTime(searchW,lastTime,"'��'�䪺",prefix,searchW.hop,true,RouteSizeName))
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
						
						TOOL.printStep("'��'�䪺"+"�ʥ]��'��'��l��  ->" + "�B��:" + searchW.hop+" "+searchW.now.left.sensorid+"("+searchW.now.left.x+","+searchW.now.left.y+")","'��'�䪺",prefix);
						searchW.now = searchW.now.left;
					}
					else if(searchW.endSearchBroadcast == false)
					{
						searchW.endSearchBroadcast = true;
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
						TOOL.printStep("Query+"+TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize),"'��'�䪺",prefix);
						TOOL.printStep("'��'�䪺"+"�ʥ] �o�{����S�H ����V����l��","'��'�䪺",prefix);
						
						if(searchW.findedLast_BroadCastTime > -1 && searchW.findedLast_BroadCastTime >= searchW.findedLast_footprintTime)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_BroadCastHop)*searchW.hop;
							TOOL.printStep("Route+"+searchW.findedLast_BroadCastHop,"'��'�䪺",prefix);
							int lastStep = searchW.RouteForLastBroadCast.size()-1;
							while(searchW.now != searchW.lastBroadcast)
							{
								TOOL.printStep("'��'�䪺"+"�ʥ] ���̫�o�{��broadcast����  -> "+searchW.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchW.hop,"'��'�䪺",prefix);
								searchW.now = searchW.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
							}
						}
						else if(searchW.findedLast_footprintTime > -1)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop;
							TOOL.printStep("Route+"+(Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop,"'��'�䪺",prefix);
							int lastStep = searchW.RouteForLastFoot.size()-1;
							while(searchW.now != searchW.lastFoot)
							{
								TOOL.printStep("'��'�䪺"+"�ʥ] ���̫�o�{��footprint����  ->"+searchW.RouteForLastFoot.get(lastStep).sensorid,"'��'�䪺",prefix);
								searchW.now = searchW.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
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
					else if(searchW.finded && !searchW.endSearchBroadcast) //�b�ݭn���e���(���|�W�����ݨ�̫᪺foot)
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
				//��ܥΥk�䪺
			{
				
				if(searchE.findedLast_BroadCastTime_inQuery > searchE.findedLast_footprintTime_inQuery) //�k��O���^Guide
				{
//					truehop = searchE.hop-searchE.RouteForLastBroadCast.size();
					
				}
				else //�k��O���^���W�o�{���̫�Ӯɶ���foot
				{
//					truehop = searchE.hop-searchE.RouteForLastFoot.size();
					
				}
				TOOL.printStep("'�k'�䪺�ʥ]���l�ܨ�F���("+searchE.now.foot.x+","+searchE.now.foot.y+")��sink,�ϥ�hop ��:"+searchE.hop,"'�k'�䪺",prefix);
				
				if(Enviorment.closest_sink == searchE.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				TOOL.printStep("'�k'�䪺�ʥ]���l�ܨ�F","'��'�䪺",prefix);
				TOOL.printStep("sink ID =" + searchE.now.foot.id,"'�k'�䪺" , prefix);
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
				
				TOOL.printStep("'��'�䪺�ʥ]���l�ܨ�F���("+searchW.now.foot.x+","+searchW.now.foot.y+")��sink,�ϥ�hop ��:"+truehop,"'��'�䪺",prefix);
				if(Enviorment.closest_sink == searchW.now.foot.id)
					Enviorment.CountClosest_sink[closestName] += 1;
				
				TOOL.printStep("'��'�䪺�ʥ]���l�ܨ�F","'�k'�䪺",prefix);
				TOOL.printStep("sink ID =" + searchW.now.foot.id,"'��'�䪺" , prefix);
				TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 1", prefix+"���\�v.txt");
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
				else if( a==b  ) //�ۤv
				{
//					truehop = searchE.hop;
				}
			}
			else
			{
				Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
				Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
				
				TOOL.printStep("hop�ƶW�L"+searchE.hop,"'��'�䪺",prefix);
				TOOL.printStep("hop�ƶW�L"+searchW.hop,"'�k'�䪺",prefix);
				TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 0", prefix+"���\�v.txt");
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
		
		//��xy ��
		public void searchXY(int checkTime , int lastTime , String prefix,int QueryName , int RouteSizeName ,int closestName) throws IOException
		{
			// TODO �۰ʲ��ͪ���k Stub
			
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
				
				if(checkWE_andTime(searchE,lastTime,"'�k'�䪺",prefix,searchE.hop,true,RouteSizeName)) //�A���e�n�����Ǳ��� �򥻤W���b�̭� true �N�����̭��S��������
				{
					if(searchE.now.right != null)
					{
						
						TOOL.printStep("'�k'�䪺�ʥ]���k��l�� ->" + "�B��:"+ searchE.hop  +" "+searchE.now.right.sensorid+"("+searchE.now.right.x+","+searchE.now.right.y+")","'�k'�䪺",prefix);
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
						
						TOOL.printStep("'�k'�䪺"+"�ʥ] �o�{�k��S�H ����V�k��l��","'�k'�䪺",prefix);
						if(searchE.findedLast_BroadCastTime > -1 && searchE.findedLast_BroadCastTime >= searchE.findedLast_footprintTime)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_BroadCastHop)*searchE.hop;
							int lastStep = searchE.RouteForLastBroadCast.size()-1;
							while(searchE.now != searchE.lastBroadcast)
							{
								TOOL.printStep("'�k'�䪺"+"�ʥ] ���̫�o�{��broadcast����  ->"+searchE.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchE.hop,"'�k'�䪺",prefix);
								searchE.now = searchE.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
								
							}
							
						}
						else if(searchE.findedLast_footprintTime > -1)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchE.findedLast_footprintHop)*searchE.hop;
							int lastStep = searchE.RouteForLastFoot.size()-1;
							while(searchE.now != searchE.lastFoot)
							{
								TOOL.printStep("'�k'�䪺"+"�ʥ] ���̫�o�{��footprint����  ->"+searchE.RouteForLastFoot.get(lastStep).sensorid,"'�k'�䪺",prefix);
								searchE.now = searchE.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
								
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
				else if(searchE.finded && !searchE.endSearchBroadcast) //�b�ݭn���e���(���|�W�����ݨ�̫᪺foot)
				{
					
					Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchE.hop, Enviorment.BaseQuerySize);
					Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchE.hop;
				}
				searchE.hop++;
			}
			
			while(searchW.hop <  Enviorment.randomwalk_step && !searchW.finded)
			{
				
				if(checkWE_andTime(searchW,lastTime,"'��'�䪺",prefix,searchW.hop,true,RouteSizeName))
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
						
						TOOL.printStep("'��'�䪺"+"�ʥ]��'��'��l��  ->" + "�B��:" + searchW.hop+" "+searchW.now.left.sensorid+"("+searchW.now.left.x+","+searchW.now.left.y+")","'��'�䪺",prefix);
						searchW.now = searchW.now.left;
					}
					else if(searchW.endSearchBroadcast == false)
					{
						searchW.endSearchBroadcast = true;
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
						TOOL.printStep("Query+"+TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize),"'��'�䪺",prefix);
						TOOL.printStep("'��'�䪺"+"�ʥ] �o�{����S�H ����V����l��","'��'�䪺",prefix);
						
						if(searchW.findedLast_BroadCastTime > -1 && searchW.findedLast_BroadCastTime >= searchW.findedLast_footprintTime)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_BroadCastHop)*searchW.hop;
							TOOL.printStep("Route+"+searchW.findedLast_BroadCastHop,"'��'�䪺",prefix);
							int lastStep = searchW.RouteForLastBroadCast.size()-1;
							while(searchW.now != searchW.lastBroadcast)
							{
								TOOL.printStep("'��'�䪺"+"�ʥ] ���̫�o�{��broadcast����  -> "+searchW.RouteForLastBroadCast.get(lastStep).sensorid+"hop:"+searchW.hop,"'��'�䪺",prefix);
								searchW.now = searchW.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
							}
						}
						else if(searchW.findedLast_footprintTime > -1)
						{
//							Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop;
							TOOL.printStep("Route+"+(Enviorment.BaseQuerySize + 2* searchW.findedLast_footprintHop)*searchW.hop,"'��'�䪺",prefix);
							int lastStep = searchW.RouteForLastFoot.size()-1;
							while(searchW.now != searchW.lastFoot)
							{
								TOOL.printStep("'��'�䪺"+"�ʥ] ���̫�o�{��footprint����  ->"+searchW.RouteForLastFoot.get(lastStep).sensorid,"'��'�䪺",prefix);
								searchW.now = searchW.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
							}
							searchW.traceFoot = true;
						}
						else
						{
							
//							Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchW.hop;
							break;
						}
						
					}
					else if(searchW.finded && !searchW.endSearchBroadcast) //�b�ݭn���e���(���|�W�����ݨ�̫᪺foot)
					{
						
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchW.hop, Enviorment.BaseQuerySize);
						
						Enviorment.CountRouteSize[RouteSizeName] += Enviorment.BaseQuerySize*searchW.hop;
						
					}
				}
				searchW.hop++;
					
			}
			
			while(searchN.hop <  Enviorment.randomwalk_step && !searchN.finded) 
			{
				if(checkWE_andTime(searchN,lastTime,"'�W'����",prefix,searchN.hop,false,RouteSizeName))
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
						TOOL.printStep("'�W'����"+"�ʥ]��'�W'���l��  ->"+searchN.now.up.sensorid+"("+searchN.now.up.x+","+searchN.now.up.y+")","'�W'����",prefix);
						searchN.now = searchN.now.up;
					}
					else if(searchN.endSearchBroadcast == false)
					{
						searchN.endSearchBroadcast = true;
						TOOL.printStep("'�W'����"+"�ʥ] �o�{�W���S�H ����V����l��","'�W'����",prefix);
						Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchN.hop, Enviorment.BaseQuerySize);
						if(searchN.findedLast_BroadCastTime > -1 && searchN.findedLast_BroadCastTime >= searchN.findedLast_footprintTime)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchN.findedLast_BroadCastHop)*searchN.hop;
							int lastStep = searchN.RouteForLastBroadCast.size()-1;
							while(searchN.now != searchN.lastBroadcast)
							{
								TOOL.printStep("'�W'����"+"�ʥ] ���̫�o�{��broadcast����  ->"+searchN.RouteForLastBroadCast.get(lastStep).sensorid,"'�W'����",prefix);
								searchN.now = searchN.RouteForLastBroadCast.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
							}
						}
						else if(searchN.findedLast_footprintTime > -1)
						{
							//Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchN.findedLast_footprintHop)*searchN.hop;
							int lastStep = searchN.RouteForLastFoot.size()-1;
							while(searchN.now != searchN.lastFoot)
							{
								TOOL.printStep("'�W'����"+"�ʥ] ���̫�o�{��footprint����  ->"+searchN.RouteForLastFoot.get(lastStep).sensorid,"'�W'����",prefix);
								searchN.now = searchN.RouteForLastFoot.get(lastStep);
								lastStep--;
								//hop++;
								//hop�ƦbQuery�����q�ɤ��ݭn�W�[
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
				else if(searchN.finded && !searchN.endSearchBroadcast) //�b�ݭn���e���(���|�W�����ݨ�̫᪺foot)
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
					if(checkWE_andTime(searchS,lastTime,"'�U'����",prefix,searchS.hop,false,RouteSizeName))
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
							TOOL.printStep("'�U'����"+"�ʥ]��'�U'���l��  ->"+searchS.now.down.sensorid+"("+searchS.now.down.x+","+searchS.now.down.y+")","'�U'����",prefix);
							searchS.now = searchS.now.down;
						}
						else if(searchS.endSearchBroadcast == false)
						{
							searchS.endSearchBroadcast = true;
							TOOL.printStep("'�U'����"+"�ʥ] �o�{����S�H ����V�U���l��","'�U'����",prefix);
							Enviorment.CountQuerySize[QueryName] += TOOL.BaseQueryToQuerySize(searchS.hop, Enviorment.BaseQuerySize);
							if(searchS.findedLast_BroadCastTime > -1 && searchS.findedLast_BroadCastTime >= searchS.findedLast_footprintTime)
							{
								Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchS.findedLast_BroadCastHop)*searchS.hop;
								int lastStep = searchS.RouteForLastBroadCast.size()-1;
								while(searchS.now != searchS.lastBroadcast)
								{
									TOOL.printStep("'�U'����"+"�ʥ] ���̫�o�{��broadcast����  ->"+searchS.RouteForLastBroadCast.get(lastStep).sensorid,"'�U'����",prefix);
									searchS.now = searchS.RouteForLastBroadCast.get(lastStep);
									lastStep--;
									//hop++;
									//hop�ƦbQuery�����q�ɤ��ݭn�W�[
								}
							}
							else if(searchS.findedLast_footprintTime > -1)
							{
								Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* searchS.findedLast_footprintHop)*searchS.hop;
								int lastStep = searchS.RouteForLastFoot.size()-1;
								while(searchS.now != searchS.lastFoot)
								{
									TOOL.printStep("'�U'����"+"�ʥ] ���̫�o�{��footprint����  ->"+searchS.RouteForLastFoot.get(lastStep).sensorid,"'�U'����",prefix);
									searchS.now = searchS.RouteForLastFoot.get(lastStep);
									lastStep--;
									//hop++;
									//hop�ƦbQuery�����q�ɤ��ݭn�W�[
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
					else if(searchS.finded && !searchS.endSearchBroadcast) //�b�ݭn���e���(���|�W�����ݨ�̫᪺foot)
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
			// a �k  b�� c�W   d �U ��̱ߪ�b/f �ɶ�
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
			//��ɶ��@�˪� hop�ƬD�X��
			int index = 0;
			
			int min = Integer.MAX_VALUE;
			int arr2[] = {a,b,c,d};
			for(int i = 0 ; i < arr2.length ; i++)
				if(min > arr2[i])
				{
					min = arr2[i];
					index = i;
				}
			//�A�Dhop�Ƴ̤p��
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
				
				TOOL.printStep("'�k'�䪺�ʥ]���l�ܨ�F���("+searchE.now.foot.x+","+searchE.now.foot.y+")��sink,�ϥ�hop ��:"+searchE.hop,"'�k'�䪺",prefix);
				TOOL.printStep("'�k'�䪺�ʥ]���l�ܨ�F","'��'�䪺",prefix);
				TOOL.printStep("'�k'�䪺�ʥ]���l�ܨ�F","'�U'����",prefix);
				TOOL.printStep("'�k'�䪺�ʥ]���l�ܨ�F","'�W'����",prefix);
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
				
				TOOL.printStep("'��'�䪺�ʥ]���l�ܨ�F���("+searchW.now.foot.x+","+searchW.now.foot.y+")��sink,�ϥ�hop ��:"+searchW.hop,"'��'�䪺",prefix);
				TOOL.printStep("'��'�䪺�ʥ]���l�ܨ�F","'�k'�䪺",prefix);
				TOOL.printStep("'��'�䪺�ʥ]���l�ܨ�F","'�W'����",prefix);
				TOOL.printStep("'��'�䪺�ʥ]���l�ܨ�F","'�U'����",prefix);
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
				TOOL.printStep("'�U'�����ʥ]���l�ܨ�F���("+searchS.now.foot.x+","+searchS.now.foot.y+")��sink,�ϥ�hop ��:"+searchS.hop,"'�U'����",prefix);
				TOOL.printStep("'�U'�����ʥ]���l�ܨ�F","'��'�䪺",prefix);
				TOOL.printStep("'�U'�����ʥ]���l�ܨ�F","'�k'�䪺",prefix);
				TOOL.printStep("'�U'�����ʥ]���l�ܨ�F","'�W'����",prefix);
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
				TOOL.printStep("'�W'�䪺�ʥ]���l�ܨ�F���("+searchN.now.foot.x+","+searchN.now.foot.y+")��sink,�ϥ�hop ��:"+searchN.hop,"'�W'����",prefix);
				TOOL.printStep("'�W'�䪺�ʥ]���l�ܨ�F","'�U'����",prefix);
				TOOL.printStep("'�W'�䪺�ʥ]���l�ܨ�F","'��'�䪺",prefix);
				TOOL.printStep("'�W'�䪺�ʥ]���l�ܨ�F","'�k'�䪺",prefix);
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
				
				TOOL.printStep("hop�ƶW�L"+hop,"'��'�䪺",prefix);
				TOOL.printStep("hop�ƶW�L"+hop,"'�k'�䪺",prefix);
				TOOL.printStep("hop�ƶW�L"+hop,"'�U'����",prefix);
				TOOL.printStep("hop�ƶW�L"+hop,"'�W'����",prefix);
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
				TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 0", prefix+"���\�v.txt");
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
				TOOL.printLog("�ɶ�:" + lastTime + "  sensor ID:"+this.sensorid +"���\�v: 1", prefix+"���\�v.txt");	
			}
		}
			
		
		private int checkWE(point target , int time,String prefix,String filePrefix)
		{
			
			// TODO �۰ʲ��ͪ���k Stub
			if(target.foot!= null)
			{
				if(target.foot.time != time)
				{
					switch(target.foot.way)
					{
						case 0:
							TOOL.printStep(prefix+"�ʥ]��'�W'���l�ܨ���  ->"+target.up.sensorid,prefix,filePrefix);
							return 0;
						case 1:
							TOOL.printStep(prefix+"�ʥ]��'��'��l�ܨ���  ->"+target.left.sensorid,prefix,filePrefix);
							return 1;
						case 2:
							TOOL.printStep(prefix+"�ʥ]�� '�U'���l�ܨ���  ->"+target.down.sensorid,prefix,filePrefix);
							return 2;
						case 3:
							TOOL.printStep(prefix+"�ʥ]��'�k'��l�ܨ��� ->"+target.right.sensorid,prefix,filePrefix);
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
				TOOL.printStep(prefix+"'�ʥ]��'�W'���l�ܼs�� ->"+target.up.sensorid,prefix);
				return 0;
			}
			else if(target.upOrDown == -1)
			{
				TOOL.printStep(prefix+"'�ʥ]�� '�U'���l�ܼs��  ->"+target.down.sensorid,prefix);
				return 2;
			}
			*/
			else if(target.traceBroadCast_UD != null)
			{
				TOOL.printStep(prefix+"�ʥ]��broadcast�Ӫ���V��->"+target.traceBroadCast_UD.sensorid,prefix,filePrefix);
				return 5;
			}
			else
				return -2;
		}
		
		//�ˬd�O�_�l��ƥ�o�ͮɶ��I��foot(�N��l��sink) �� �Y�Ӯɶ��I���᪺ foot �� boradcast �ò��ʤ�
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
				
				TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+target.now.sensorid+"�㦳footprint �ɶ�:"+target.findedLast_footprintTime +" ID : "+target.lastFoot.sensorid,prefix,filePrefix);
			}
			
			
			if(isUD && target.now.traceBroadCast_UD != null && target.now.sinktime_UD > lastBroadcastTime)
			{
				lastBroadcastTime = target.now.sinktime_UD;
				lastb = target.now;
				target.RouteForLastBroadCast = new ArrayList<point>();
				target.RouteForLastBroadCast.add(lastb);
				target.findedLast_BroadCastHop = hop;
				
				TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+target.now.sensorid+"�㦳broadcast UD �ɶ�:"+lastBroadcastTime +" ID : "+target.now.traceBroadCast_UD.sensorid,prefix,filePrefix);
				
			}
			else if(!isUD && target.now.traceBroadCast_LR != null && target.now.sinktime_LR > lastBroadcastTime)
			{
				lastBroadcastTime = target.now.sinktime_LR;
				lastb = target.now;
				target.RouteForLastBroadCast = new ArrayList<point>();
				target.RouteForLastBroadCast.add(lastb);
				target.findedLast_BroadCastHop = hop;
				TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+target.now.sensorid+"�㦳broadcast LR �ɶ�:"+lastBroadcastTime +" ID : "+target.now.traceBroadCast_LR.sensorid,prefix,filePrefix);
				
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
					TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+target.now.nearby.get(i).sensorid+"�㦳footprint �ɶ�:"+target.findedLast_footprintTime +" ID : "+target.lastFoot.sensorid,prefix,filePrefix);
					
					
				}
				
				if(isUD && !target.endSearchBroadcast && target.now.nearby.get(i).traceBroadCast_UD != null && target.now.nearby.get(i).sinktime_UD > lastBroadcastTime)
				{
					lastBroadcastTime = target.now.nearby.get(i).sinktime_UD;
					lastb = target.now.nearby.get(i);
					target.RouteForLastBroadCast = new ArrayList<point>();
					target.RouteForLastBroadCast.add(lastb);
					target.findedLast_BroadCastHop = hop;
					TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+target.now.nearby.get(i).sensorid+"�㦳broadcast_UD �ɶ�:"+lastBroadcastTime +" ID : "+target.now.nearby.get(i).traceBroadCast_UD.sensorid,prefix,filePrefix);
					
				}
				else if(!isUD && !target.endSearchBroadcast && target.now.nearby.get(i).traceBroadCast_LR != null && target.now.nearby.get(i).sinktime_UD > lastBroadcastTime)
				{
					lastBroadcastTime = target.now.nearby.get(i).sinktime_UD;
					lastb = target.now.nearby.get(i);
					target.RouteForLastBroadCast = new ArrayList<point>();
					target.RouteForLastBroadCast.add(lastb);
					target.findedLast_BroadCastHop = hop;
					TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+target.now.nearby.get(i).sensorid+"�㦳broadcast_LR �ɶ�:"+lastBroadcastTime +" ID : "+target.now.nearby.get(i).traceBroadCast_LR.sensorid,prefix,filePrefix);
					
				}
				
			}
			
			if(target.now == null) //�l��F
			{
				TOOL.printStep(prefix+"�l��F",prefix,filePrefix);
				return false;
			}
			
			//foot sinknearhere =1: �l��sink 
			if(target.now.foot.time == lastTime)
			{
				int trueHop = TOOL.getTrueHop(target, lastTime);
				Enviorment.CountRouteSize[RouteSizeName] += (Enviorment.BaseQuerySize + 2* trueHop)*trueHop;
				Enviorment.CountQuerySize[target.QueryName] += TOOL.BaseQueryToQuerySize(trueHop, Enviorment.BaseQuerySize);
				TOOL.printStep(prefix+"�ʥ] �o�{ sink:"+target.now.foot.id +"�b("+target.now.foot.x+","+target.now.foot.y+")",prefix,filePrefix);
				target.finded = true;
				
				return false;
			}
			//broadcast ���̫�ɶ��I : ���t���̫�ɶ��I��foot
			else if(target.now.traceBroadCast_UD != null && target.now.sinktime_UD == lastTime)
			{
				TOOL.printStep(prefix+"�ʥ]��broadcast�Ӫ���V��->"+target.now.traceBroadCast_UD.sensorid+"("+target.now.traceBroadCast_UD.x+","+target.now.traceBroadCast_UD.y+")",prefix,filePrefix);
				target.now = target.now.traceBroadCast_UD;
				return false;
			}
			//�w�T�{�����~�򩹫��O�_����߮ɶ��I��broadcast �åB�̫�o�{��foot�ɶ��I��̫�o�{��broadcast�ɶ��I��
			else if(target.traceFoot || target.findedLast_footprintTime == lastTime)
			{
				if(target.now != target.lastFoot)
				{
					
					TOOL.printStep(prefix+"�ʥ]�� �o�{������ʥ]����  ->"+target.lastFoot.sensorid,prefix,filePrefix);
					target.now = target.lastFoot;
				}
				else
				switch(target.lastFoot.foot.quadrant)
				{
					case 1:
						if(target.now.right==null)
						{
							TOOL.printStep(prefix+"�ʥ]��'���k'���l�ܨ���  ->�S�H",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"�ʥ]��'���k'���l�ܨ���  ->"+target.now.right.sensorid,prefix,filePrefix);
						target.now=target.now.right;
						break;
					case 3:
						if(target.now.up==null)
						{
							TOOL.printStep(prefix+"�ʥ]��'���W'���l�ܨ���  ->�S�H",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"�ʥ]��'���W'���l�ܨ���  ->"+target.now.up.sensorid,prefix,filePrefix);
						target.now=target.now.up;
						break;
					case 5:
						if(target.now.left==null)
						{
							TOOL.printStep(prefix+"�ʥ]��'����'���l�ܨ���  ->�S�H",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"�ʥ]��'����'���l�ܨ���  ->"+target.now.left.sensorid,prefix,filePrefix);
						target.now=target.now.left;
						break;
					case 7:
						if(target.now.down==null)
						{
							TOOL.printStep(prefix+"�ʥ]��'���U'���l�ܨ���  ->�S�H",prefix,filePrefix);
							target.now = null;
							break;
						}
						TOOL.printStep(prefix+"�ʥ]��'���U'���l�ܨ���  ->"+target.now.down.sensorid,prefix,filePrefix);
						target.now=target.now.down;
						break;
					case 2:
						if(target.now.topRight != null)
						{
							TOOL.printStep(prefix+"�ʥ]��'�k�W'�l�ܨ���  ->"+target.now.topRight.sensorid,prefix,filePrefix);
							target.now = target.now.topRight;
						}
						else
						{
							TOOL.printStep(prefix+"�ʥ]����l��F",prefix,filePrefix);
							break;
						}
						break;
					case 4:
						if(target.now.topLeft != null)
						{
							TOOL.printStep(prefix+"�ʥ]��'���W'�l�ܨ���  ->"+target.now.topLeft.sensorid,prefix,filePrefix);
							target.now = target.now.topLeft;
						}
						else
						{
							TOOL.printStep(prefix+"�ʥ]����l��F",prefix,filePrefix);
							break;
						}
						break;
					case 6:
						if(target.now.buttomLeft != null)
						{
							TOOL.printStep(prefix+"�ʥ]��'���U'�l�ܨ���  ->"+target.now.buttomLeft.sensorid,prefix,filePrefix);
							target.now = target.now.buttomLeft;
						}
						else
						{
							TOOL.printStep(prefix+"�ʥ]����l��F",prefix,filePrefix);
							break;
						}
						break;
					case 8:
						if(target.now.buttomRight != null)
						{
							TOOL.printStep(prefix+"�ʥ]��'�k�U'�l�ܨ���  ->"+target.now.buttomRight.sensorid,prefix,filePrefix);
							target.now = target.now.buttomRight;
						}
						else
						{
							TOOL.printStep(prefix+"�ʥ]����l��F",prefix,filePrefix);
							break;
						}
						break;
						
				}
				target.lastFoot = target.now;
				return false;
			}
			else if(isUD && target.endSearchBroadcast && target.findedLast_BroadCastTime > -1 && target.now.BroadCast_hop_UD > 0)
			{
				
				TOOL.printStep(prefix+"�ʥ]��broadcast�Ӫ���V��(UD)->"+target.now.traceBroadCast_UD.sensorid+"("+target.now.traceBroadCast_UD.x+","+target.now.traceBroadCast_UD.y+")",prefix,filePrefix);
				target.now = target.now.traceBroadCast_UD;
				if(target.now.BroadCast_hop_UD == 0)
					target.traceFoot = true;
				
				
				return false;
			}
			else if(!isUD && target.endSearchBroadcast && target.findedLast_BroadCastTime > -1 && target.now.BroadCast_hop_LR > 0)
			{
				TOOL.printStep(prefix+"�ʥ]��broadcast�Ӫ���V��(LR)->"+target.now.traceBroadCast_LR.sensorid+"("+target.now.traceBroadCast_LR.x+","+target.now.traceBroadCast_LR.y+")",prefix,filePrefix);
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
						TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+lastb.sensorid+"�㦳broadcast �ɶ�:"+lastb.sinktime_UD +" ID : "+lastb.traceBroadCast_UD.sensorid,prefix,filePrefix);
					else
						TOOL.printStep(prefix+"�ʥ] �o�{ sensor:"+lastb.sensorid+"�㦳broadcast �ɶ�:"+lastb.sinktime_LR +" ID : "+lastb.traceBroadCast_LR.sensorid,prefix,filePrefix);
					
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
					TOOL.printStep(prefix+"�ʥ] �o�{��e��broadcast �ɶ������ ����V"+prefix+"�l��",prefix,filePrefix);
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
			// TODO �۰ʲ��ͪ���k Stub
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
			// TODO �۰ʲ��ͪ���k Stub
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
			// TODO �۰ʲ��ͪ���k Stub
		
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
		public int count = 0; //�p���F�ƶq��
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
							TOOL.printStep("�ɶ�:"+time+" "+next.sensorid+"�Q(��F)"+p[j].sensorid+"�q�� �ܦ�close", "By_3", "Track");
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
			
			//���令�C�@�ؤ�k ���O�NBeacon ���P�D�Ҧ���sensor close �Ǥ��Oclose������track
//			p[senId].foot.closeID = senId;
//			p[senId].foot.closetime = time;
//			//��CLOSE���ʥ]�|��P��SENSOR�ܦ�TRACK�A���O���T�w���S���o�@��A���M������like a shit
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
			if(Id >= 0)//sor�̪�
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
