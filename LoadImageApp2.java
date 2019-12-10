/*
 *MIGVIEW
 *
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.regex.*;

/**
 * Analyze failures in dark
 */
public class LoadImageApp2  {
	int MIN_PIXEL_IN_CLUSTER ;
	int MIN_PIXEL_IN_ROW;
	int MIN_PIXEL_IN_COL;
	
	int FAIL_ARRAY_DIM = 200;
	//double ERROR = 0.1;
	
	String FAIL_DIR = "fails/";
	String GOOD_IMG_DIR = "dev/";
	
	BufferedImage img;
	BufferedImage img2;
	BufferedImage ref_img;
        Object[][] all_failures;
        int colnum;
        int rownum;
	int[][] PIXMTAT;
	//int[][] CLUSTMAT;
	int fail_num;
	Raster rs;
	Raster rs_ref;
	
	
	public LoadImageApp2(double ERROR,String pngname, String ref_png, String dev, String bin_type, String bin, String t_type, String binout, String strategy, int c_num) 
	{
		try {
			System.out.println("Binout: " + binout);
			System.out.println("Starting img analysis... ");
			
			img = ImageIO.read(new File(pngname));
			//ref_img = get_ref_img(dev, bin_type, t_type);
			ref_img = ImageIO.read(new File(ref_png));
			
			String last_letter = "null";
			
			if(!bin.equals(".."))
			 last_letter = bin.substring(2,3);
			
			//System.out.println("last letter;" + last_letter);
			CharSequence cs_x = "x";
			CharSequence cs_y = "y";
			boolean x_inbinout = binout.contains(cs_x);
			boolean y_inbinout = binout.contains(cs_y);
		
			///////////////// THR for defect detection ////////////
			double error = ERROR;
			/////////////////////////////////////////////////////
			colnum = img.getWidth(null);
			rownum = img.getHeight(null);

		        ////////////// # pix in row. col ////////////////
			//MIN_PIXEL_IN_ROW = 500;
			//MIN_PIXEL_IN_COL = 400;	
			
			MIN_PIXEL_IN_ROW = (int)(rownum * 0.15);
			MIN_PIXEL_IN_COL = (int)(colnum * 0.15);
				
			//////////////// MIN PIX IN A CLUSTER /////////////////
			MIN_PIXEL_IN_CLUSTER = c_num;
			/*
			if(bin_type.equals("Bin_Z") || bin.equals("D:6")|| bin.equals("U:U")|| bin.equals("U:3"))
				MIN_PIXEL_IN_CLUSTER =2;
			else
				MIN_PIXEL_IN_CLUSTER =5;
						
			if(dev.equals("C2EB")||dev.equals("C4BA"))
				MIN_PIXEL_IN_CLUSTER =10;
		
			if(dev.equals("C4BX"))
				MIN_PIXEL_IN_CLUSTER =13;
			*/
			////////////////////////////////////////////////////////
			
			// search windows  x clustering ///////////////////////
			int sw = 2; 			
			if(dev.equals("C2EB")||dev.equals("C4BA")||dev.equals("C4BX"))
				sw = 1;
			else
				sw = 2;
			///////////////////////////////////////////////////////
			
			int rgb = 0;
			int avg_lsb = 0;
			int avg_lsb_ref = 0;

			colnum = img.getWidth(null);
			rownum = img.getHeight(null);
		
			// Histogtam arrays definition and init
			int[] ColHisto = new int[colnum];
			int[] RowHisto = new int[rownum];

			// all pixel in one array for x,y,clustnum
			PIXMTAT = new int[colnum][rownum];
			//CLUSTMAT = new int[colnum][rownum];

			for (int i = 0; i < colnum; i++)
				ColHisto[i] = 0;
			for (int j = 0; j < rownum; j++)
				RowHisto[j] = 0;
			// /////////////////////////////////////////

			//trace_mem();
                        // get a raster file			
			rs = img.getData();
			rs_ref = ref_img.getData();
			/////////////////////////////////////////
			
			int mypix;
			int mypix_ref;
			int mypix_ref_adj_pix;
			int lsb;
			int lsb_ref;
			int lsb_ref_adj_pix;
			double delta;
			double range;
			
			// Bright pixel estraction for dark images
			for (int i = 0; i < colnum; i++) {
				for (int j = 0; j < rownum; j++) {
					mypix = rs.getSample(i, j, 0);
					mypix_ref = rs_ref.getSample(i, j, 0);
					
					// looking at adj pixel
					if(i>2 && j>2)
					  mypix_ref_adj_pix = rs.getSample(i-2, j-2, 0);
					else
					  mypix_ref_adj_pix = mypix_ref;	
					
					// right bit shift required because the out PNG is 16 BIT depth....
					if(dev.equals("C4BA")||dev.equals("C4BX")||dev.equals("C4BD")||dev.equals("K22B")||dev.equals("K45A")||dev.equals("C48B")){
						lsb = mypix>>6; //10 bit
						lsb_ref = mypix_ref>>6;	
						lsb_ref_adj_pix = mypix_ref_adj_pix>>6;
					} else { //12 bit
						lsb = mypix>>4;
						lsb_ref = mypix_ref>>4;
						lsb_ref_adj_pix = mypix_ref_adj_pix>>4;
					}
							
					avg_lsb = (avg_lsb + lsb)/2;
					avg_lsb_ref = (avg_lsb_ref + lsb_ref)/2;
					
							
				//	if(j == 1263)
				//		System.out.println(i+","+j+","+lsb);
					
					
					//log.write(i + "," + j + "||" + green + "," +red+ "," +blue+ "," + val + "\n");
	
					switch(strategy){
						case "GOODIE_PIX":
						delta = Math.abs(lsb-lsb_ref);					
						range = lsb_ref * error;				
						break;
						
						case "GOODIE_AVG":
						delta = Math.abs(lsb-avg_lsb_ref);					
						range = lsb_ref * error;	
						
						break;
						
						case "INDIE_PIX":
						delta = Math.abs(lsb-lsb_ref_adj_pix);					
						range = lsb_ref_adj_pix * error;				
						break;
						
						default:
						delta = Math.abs(lsb-lsb_ref);					
						range = lsb_ref * error;										
						break;
						
					}
					
					
					
/*			
					if(dev.equals("C4BX")&& bin_type.equals("Bin_M")){
						delta = Math.abs(lsb-lsb_ref_adj_pix);					
						range = lsb_ref_adj_pix * error;
					}
					else
					{
						delta = Math.abs(lsb-lsb_ref);					
						range = lsb_ref * error;						
					}
*/

					
					if(delta > range)	
					{
						ColHisto[i]++;
						RowHisto[j]++;
						//log.write(i + "," + j + "||" + green + "," +red+ "," +blue+ "," + val+ "," + pval+ "," +pval2 + "\n");
						// System.out.println(i+" "+ j +" "+ lsb);
						//log.flush();
						PIXMTAT[i][j] = 1;
					} else
						PIXMTAT[i][j] = 0;
				}
			}

			System.out.println("avg lsb= " + avg_lsb);
			
			//TDB init all_allfail and dyn allocation
			all_failures = new Object[FAIL_ARRAY_DIM][3];
			for(int k=0; k< FAIL_ARRAY_DIM; k++)
				all_failures[k][0]="null";
			
			fail_num = 0;
			
			// /////////// ROW/COL ////////////////////
			for (int i = 0; i < rownum; i++) {
				if (RowHisto[i] > MIN_PIXEL_IN_ROW) {
					System.out.println("inhibit_row; " + i);
					inhibit_row(i); // avoid reclustering for row
					//if((fail_num< FAIL_ARRAY_DIM) && last_letter.equals("x"))
					if((fail_num< FAIL_ARRAY_DIM) && (last_letter.equals("x")|| x_inbinout) && !(bin.equals("A:y")))
					{
						System.out.println("ROW failure at row; " + i);
						all_failures[fail_num][0] = "ROW";
						all_failures[fail_num][1] = 0;
						all_failures[fail_num][2] = i;
						//all_failures[fail_num][3] = get_fail_img("row_"+Integer.toString(i) ,0, i, colnum, 1);
						fail_num++;
					}
				}
			}
			
			for (int i = 0; i < colnum; i++) {
				if (ColHisto[i] > MIN_PIXEL_IN_COL){
					System.out.println("inhibit_col; " + i);
					inhibit_col(i); // avoid reclustering for cols
					//if(fail_num< FAIL_ARRAY_DIM && last_letter.equals("y"))
//					if(fail_num< FAIL_ARRAY_DIM && (last_letter.equals("y")|| y_inbinout))
					if(fail_num< FAIL_ARRAY_DIM)
					{
						System.out.println("COL failure at col; " + i);
						all_failures[fail_num][0] = "COL";
						all_failures[fail_num][1] = i;
						all_failures[fail_num][2] = 0;
						//all_failures[fail_num][3] = get_fail_img("col_"+Integer.toString(i) ,i, 0, 1, rownum);
						fail_num++;
					}
				}
			}

			
			///////////////////////////////////////////////////////////
			///////////////////////// clustering //////////////////////
			int clustnum = 2; // clust numbering starting point

			for (int i = sw; i < colnum - sw; i++) {
			    for (int j = sw; j < rownum - sw; j++) {
				if ( PIXMTAT[i][j] ==1) // unclusterd
				{
				       // check primi vicini
				       for(int m=i-sw; m<= i+sw; m++){
			                  for(int n=j-sw; n<= j+sw; n++) {
			               	   if(PIXMTAT[m][n] > 1)
			               		   PIXMTAT[i][j] = PIXMTAT[m][n];
			                   }
					}
						  
					if ( PIXMTAT[i][j] ==1)
						PIXMTAT[i][j] = clustnum;

					clustnum++;
				}
			    }
			}
			////////////////////////////////////////////////////////////

			// ////////// D3 //////////////////////////////
			int[] clustHisto = new int[clustnum];
			for (int i = 0; i < colnum; i++)
				for (int j = 0; j < rownum; j++)
					if(PIXMTAT[i][j] != -1) //row & col exclusion
					  clustHisto[PIXMTAT[i][j]]++;

						
			
			for (int i = 2; i < clustnum; i++)
			//if (clustHisto[i] >= MIN_PIXEL_IN_CLUSTER && fail_num< FAIL_ARRAY_DIM
			//	&& !last_letter.equals("x")  && !last_letter.equals("y")    ) 
			if (clustHisto[i] >= MIN_PIXEL_IN_CLUSTER && fail_num< FAIL_ARRAY_DIM && !(bin.equals("A:y")))
			{ //more than 5 pixel in a cluster
				int cx, cy, mink=colnum,minj=rownum,maxk=0,maxj=0;
				cx = cy = 0;
				System.out.println("CLUSTER failure for clust: " + i
				+ " with pixels= " + clustHisto[i]);
				//log2.write("D3 failure for clust: " + i + " with pixels= "
				//		+ clustHisto[i] + "\n");
				
				for (int k = 0; k < colnum; k++) {
					for (int j = 0; j < rownum; j++) {
						if (PIXMTAT[k][j] == i) {
							//log2.write("Pixel  " + k + " " + j + "\n");
							//log2.flush();
							cx = cx + k;
							cy = cy + j;
							
							if(k<mink)
								mink = k;
							if(j<minj)
								minj = j;
							if(k>maxk)
								maxk = k;
							if(j>maxj)
								maxj = j;
							
							// System.out.println( "Pixel  " + k + " " + j);
						}
					}
				}
				
				cx = cx / clustHisto[i];
				cy = cy / clustHisto[i];
				System.out.println("Cluster center: " + cx + " - " + cy);
				
				all_failures[fail_num][0] = "CLUSTER";
				all_failures[fail_num][1] = cx;
				all_failures[fail_num][2] = cy;
				//all_failures[fail_num][3] = get_fail_img("clust_"+Integer.toString(i) ,mink, minj, Math.abs(maxk-mink)+1, Math.abs(maxj-minj)+1);
				
				fail_num++;
			}
			
		} catch (IOException e) {
			System.out.println("exception in load img "+ e);
		}
		
	}

	
	
	
	public void inhibit_col(int mycol)
	{
	for(int i=0; i< rownum; i++)	
		PIXMTAT[mycol][i] = -1;
	}
	
	
	public void inhibit_row(int myrow)
	{
	for(int i=0; i< colnum; i++)	
		PIXMTAT[i][myrow] = -1;
	}
	
	
	
	public void trace_mem(){
		long maxHeapSize = Runtime.getRuntime().maxMemory();  
		long freeHeapSize = Runtime.getRuntime().freeMemory();  
		long totalHeapSize = Runtime.getRuntime().totalMemory();  
		System.err.println("Max Heap Size = " + maxHeapSize/1000000+ " Mbyte");  
		System.err.println("Free Heap Size = " + freeHeapSize/1000000+ " Mbyte");  
		System.err.println("Total Heap Size = " + totalHeapSize/1000000+ " Mbyte");  
	}
	
	
	//load the reference image
	public BufferedImage get_ref_img(String device, String bin_type, String t_type){
		BufferedImage out=img;
		String ref_img="";
		
		try{
			if(t_type.equals("PPP"))
				ref_img = GOOD_IMG_DIR + device +  "/PPP/" + bin_type+ "/";
			else
				ref_img = GOOD_IMG_DIR + device +  "/" + bin_type+ "/";	
			
			
			File dir = new File(ref_img);
			String[] children = dir.list();
			if (children == null) {
				System.out.println("Path for ref img not found...");
			} else {
				for (int i=0; i<children.length; i++) {
					//String ref_type = get_t_type_from_img(children[i]);
					String path = ref_img + children[i];
					System.out.println("reference img: " + path);
					out =  ImageIO.read(new File(path));
				}
			}
			
		}catch (IOException e) {
		        System.out.println("img not found..." + e);
			
		}
		
		return out;
	}
	
	
	/////////////////////////////////////////////////////////
	public String get_t_type_from_img(String img_name) {
		String ret="null";
		String REGEX="\\.";
		Pattern pattern = Pattern.compile(REGEX);		
		String[] name_split = pattern.split(img_name);
		System.out.println("img type..." + name_split[4]);
		return name_split[4];
	}
	
	
	///////////////////////////////////////////////////////////////////
	public BufferedImage get_fail_img(String name, int mincol, int minrow, int colwidth, int rowwidth){
		BufferedImage fail_img;
		
		try{
			
		 fail_img = img.getSubimage(mincol, minrow, colwidth, rowwidth) ;
		 String fname = FAIL_DIR + name + ".png";
		 File file = new File(fname);
		 ImageIO.write(fail_img, "png", file);
		 
		}catch (IOException e) {
			fail_img = img;
		}
		 
		   return fail_img;	
	}
		
		
	 public Object[][] get_all_failures()
	{      
		Object[][] myfails = new Object[fail_num][3];
		
		for(int i=0; i<fail_num; i++){
			myfails[i][0] = all_failures[i][0];
			myfails[i][1] = all_failures[i][1];
			myfails[i][2] = all_failures[i][2];

		}
		   //return all_failures;
		    return myfails;
	}
	////////////////////////////////////////////////////////
}


