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

/**
 * This class demonstrates how to load an Image from an external file
 */
public class LoadImageApp  {

	BufferedImage img;
	BufferedImage img2;
	BufferedImage img3;



	public LoadImageApp() {
		try {
			img = ImageIO
					.read(new File(
							"1316649738.7413149.003.3149-01.FPP.00.C1CC.+005.+005.X02S.1.1.Bin_d.png"));
		
			img3 = ImageIO
					.read(new File(
							"1316649738.7413149.003.3149-01.FPP.00.C1CC.+005.+005.X02S.1.1.Bin_d.png"));
			
			int rgb = 0;
			int avg_lsb = 0;

			int colnum = img.getWidth(null);
			int rownum = img.getHeight(null);

			File logfile = new File("out.txt");
			FileWriter log = new FileWriter(logfile);
			boolean fc = logfile.createNewFile();

			File logfile2 = new File("clustering.txt");
			FileWriter log2 = new FileWriter(logfile2);
			boolean fc2 = logfile2.createNewFile();

			// Histogtam arrays definition and init
			int[] ColHisto = new int[colnum];
			int[] RowHisto = new int[rownum];

			// all pixel in one array for x,y,clustnum
			int[][] PIXMTAT = new int[colnum][rownum];
			int[][] CLUSTMAT = new int[colnum][rownum];

			for (int i = 0; i < colnum; i++)
				ColHisto[i] = 0;
			for (int j = 0; j < rownum; j++)
				RowHisto[j] = 0;
			// /////////////////////////////////////////
			
			Raster ras = img.getData();
			Raster ras3 = img3.getData();

			// Bright pixel estraction
			for (int i = 0; i < colnum; i++) {
				for (int j = 0; j < rownum; j++) {

					
					int val = ras.getSample(i,j,0);
					val = val>>4;

					avg_lsb = (avg_lsb + val) / 2;

					if (val > 900) {
						ColHisto[i]++;
						RowHisto[j]++;
						log.write(i + "," + j + ","  + val + "\n");
						// System.out.println(i+" "+ j +" "+ green);
						log.flush();
						PIXMTAT[i][j] = 1;
					} else
						PIXMTAT[i][j] = 0;
				}
			}

			System.out.println("avg lsb= " + avg_lsb);

			// /////////// Dx Dy ////////////////////
			for (int i = 0; i < colnum; i++) {
				if (ColHisto[i] > 100)
					System.out.println("Dy failure at col; " + i);
			}

			for (int i = 0; i < rownum; i++) {
				if (RowHisto[i] > 100)
					System.out.println("Dx failure at row; " + i);
			}

			// clustering
			CLUSTMAT = PIXMTAT;
			int clustnum = 2;
			int sw = 1; // search windows

			for (int i = sw; i < colnum - sw; i++) {
				for (int j = sw; j < rownum - sw; j++) {

					if ( CLUSTMAT[i][j] ==1) // unclusterd
																	// pixel
					{
						// check primi vicini

						   for(int m=i-sw; m<= i+sw; m++){
			                   for(int n=j-sw; n<= j+sw; n++) {
			                	   if(CLUSTMAT[m][n] > 1)
			                		   CLUSTMAT[i][j] = CLUSTMAT[m][n];
						
			                   }

							}
						   
							if ( CLUSTMAT[i][j] ==1)
								CLUSTMAT[i][j] = clustnum;

						clustnum++;
					}

				}
			}

			// ////////// D3 //////////////////////////////
			int[] clustHisto = new int[clustnum];
			for (int i = 0; i < colnum; i++)
				for (int j = 0; j < rownum; j++)
					clustHisto[CLUSTMAT[i][j]]++;

			//int cx, cy, mink=rownum,minj=colnum,maxk=0,maxj=0;
			for (int i = 2; i < clustnum; i++)
				if (clustHisto[i] > 5) {
					int cx, cy, mink=colnum,minj=rownum,maxk=0,maxj=0;
					cx = cy = 0;
					System.out.println("D3 failure for clust: " + i
							+ " with pixels= " + clustHisto[i]);
					log2.write("D3 failure for clust: " + i + " with pixels= "
							+ clustHisto[i] + "\n");
					for (int k = 0; k < colnum; k++) {
						for (int j = 0; j < rownum; j++) {
							if (CLUSTMAT[k][j] == i) {
								log2.write("Pixel  " + k + " " + j + "\n");
								log2.flush();
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
					img2 = img.getSubimage(mink, minj, Math.abs(maxk-mink)+1, Math.abs(maxj-minj)+1) ;
                    // print sub image in a file
					String fname = Integer.toString(i) + ".png";
					File file = new File(fname);
					ImageIO.write(img2, "png", file);
				}

		} catch (IOException e) {
		}

	}

	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {
			return new Dimension(300, 300);
			// return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}

	public static void main(String[] args) {

       for(int i =0; i < 1000; i++){		
		 LoadImageApp lp = new LoadImageApp();
		 
		 
		// Get current size of heap in bytes
		 long heapSize = Runtime.getRuntime().totalMemory();

		 // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
		 // Any attempt will result in an OutOfMemoryException.
		 long heapMaxSize = Runtime.getRuntime().maxMemory();

		 // Get amount of free memory within the heap in bytes. This size will increase
		 // after garbage collection and decrease as new objects are created.
		 long heapFreeSize = Runtime.getRuntime().freeMemory();
		 System.out.println(heapSize +"   " +heapMaxSize+"   " +heapFreeSize);
       }

	}
}
