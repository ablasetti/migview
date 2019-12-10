/******************************************************
 *  A. Blasetti 
 *  05/09/2006
 *  Micron Technology Italy
 *  RDA ENG
 *  SSA
 *
 ******************************************************/

import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.*;
import java.applet.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;

import javax.swing.table.*;
import java.awt.image.*;
import javax.imageio.ImageIO;



public class ssa {
	private static Pattern pattern;
	final static Color bg = Color.white;
	final static Color fg = Color.black;
	final static Color red = Color.red;
	final static Color white = Color.white;
	final static BasicStroke stroke = new BasicStroke(2.0f);
	final static BasicStroke wideStroke = new BasicStroke(8.0f);
	final static float dash1[] = {10.0f};
	final static BasicStroke dashed = new BasicStroke(1.0f,	BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
	// start with graphics
	JPanel mainPanel;  
	JMenuBar MenuBar;
	JToolBar toolBar;
	JToolBar toolBar_bottom;
	JTextArea textArea4;
	JScrollPane JSP;
	JPanel subpanel2;
	JPanel subpan3;
	JButton button_in_line;
	JButton button;
	JButton buttonpmap;
	JButton buttonfc;
	JButton button_match;
	JScrollPane scrollpane;
	JScrollPane sp_for_pixmatchtbl;
	JButton button_js;
	JButton button_db;
	JButton button_black;
	JButton button_loc_db_match;
	JButton button_wf2db_match;
	JButton button_show_db;
	JButton button_cluster_analysis;
	JButton button_baricenter;
	JButton button_image_analysis;
	JButton button_multi_map;
	JTextField ang;
	JSlider js;
	JTable match_table;
	match_TableModel myModel;
	JTable pix_match_tbl;
	pixmatch_TableModel pix_match_mdl;
	JTextField textforpath;
	JTextField textforpath2;
	JTextField textforbinout;
	Fillwafer_multi fwm;

	public  Object[][] match_data;
	public  Object[][] pix_match_data;

	double error;
	private static final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private static String currentLookAndFeel = windows;
	Connection db_con;
	Connection probe_db_con;
	db klar;
	probe_db probe;
	Fillwafer  reference;
	Fillwafer_w_probe reference2;

	int ROW_COUNT;
	String CUR_DIR;
	String[] tree = {"LINEAR/", "OTHER/", "BIG CLUSTER/", "BARICENTER/"};
	int MIN_CLUST_DEF = 8;
	String DB = "";
	double fact = 3.0;


	/****************************
	 * The constructor
	 * @throws IOException 
	 ****************************/
	public ssa()  {
		CUR_DIR = "tmp/";
		mainPanel = new JPanel();
		toolBar = new JToolBar(); 
		toolBar_bottom = new JToolBar(); 
		MenuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		subpanel2 = new JPanel(); 
		button_in_line = new JButton("Klarity map");
		button_multi_map = new JButton("PixelMatching");
		button  = new JButton("Get inline e probe map");
		buttonpmap  = new JButton("Probe MAP");
		button_match  = new JButton("Find it INLINE");
		button_wf2db_match = new JButton("Find it IN DB");
		button_js  = new JButton("Ok");
		buttonfc = new JButton("DB store ");
		button_db = new JButton("Get from DB ");
		button_black = new JButton(" Blacks ");
		button_loc_db_match = new JButton(" DB match ");
		button_show_db = new JButton(" SHOW DB ");
		button_cluster_analysis = new JButton(" Cluster analysis ");
		button_baricenter = new JButton(" Baricenter ");
		button_image_analysis = new JButton(" Image analysis ");
		match_data = new Object[100][6];
		pix_match_data = new Object[400][9];

		// Text area 4
		textArea4 = new JTextArea();
		textArea4.setEditable(false);
		textArea4.setRows(5); 
		db_con = null;

		/// Tables
		myModel = new match_TableModel();
		match_table = new JTable(myModel);
		match_table.setRowHeight(100);

		pix_match_mdl = new pixmatch_TableModel(); 
		pix_match_tbl = new JTable(pix_match_mdl);

		ROW_COUNT = 0;
		scrollpane = new JScrollPane(match_table);
		sp_for_pixmatchtbl = new JScrollPane(pix_match_tbl);
		///////////////////////////////////////////////

		// Scroll pane area 4
		JScrollPane areaScrollPane4 = new JScrollPane(textArea4);
		//areaScrollPane4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane4.setPreferredSize(new Dimension(500, 110));
		textArea4.setFont(new Font("Serif", Font.PLAIN, 12));
		textArea4.setCaretPosition(textArea4.getDocument().getLength());




		areaScrollPane4.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("Log Panel"),
								BorderFactory.createEmptyBorder()),
						areaScrollPane4.getBorder()));

		textforpath = new JTextField("FPP................");
		textforpath.setPreferredSize(new Dimension(80, 25));
		textforpath2 = new JTextField("QPP...............");
		textforpath2.setPreferredSize(new Dimension(80, 25));
		textforbinout = new JTextField();
		textforbinout.setEditable(false);

		JMenuItem menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});

		redirectSystemStreams();

		/*
		////////// redirect stdout /////////////////////////
		try {
			String outdir= new String("out/");
			// Tee standard output
			PrintStream out = new PrintStream(new FileOutputStream(outdir+"out.log"));
			PrintStream tee = new TeeStream(System.out, out);

			System.setOut(tee);

			// Tee standard error
			PrintStream err = new PrintStream(new FileOutputStream(outdir+"err.log"));
			tee = new TeeStream(System.err, err);

			System.setErr(tee);
		} catch (FileNotFoundException e) {
		}
		////////////////////////////////////////////////////////

		 */







		buttonpmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				//7482009.009
				subpanel2.removeAll();
				ProbeMap pmap = new ProbeMap(probe_db_con, "7482009.009", "05");
				pmap.setBackground(Color.white);
				subpanel2.add(pmap, BorderLayout.CENTER);

				subpanel2.revalidate();
				subpanel2.repaint();
			} 
		});



		button_image_analysis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String img_name = "";
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(mainPanel);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getPath() + "\n");

					img_name = chooser.getSelectedFile().getPath(); 
				}

				/*
				img_name = img_name.replace("\\", "/");
				System.out.println("THR: " + 0.13);
				LoadImageApp2 mymigview = new LoadImageApp2(0.13,img_name, "C2EB", "Bin_D", "D:y","FPP","");
				System.out.println("THR: " + 0.125);
				mymigview = new LoadImageApp2(0.125,img_name, "C2EB", "Bin_D", "D:y","FPP", "");
				 */
			} 
		});





		// Buttons actions listener
		button_in_line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				get_data gd = new get_data(db_con);
				gd.createConnectionDialog();
				js = new JSlider(500000,5000000);
				js.setValue(500000);
				subpanel2.removeAll();
				reference = new Fillwafer(klar, gd);
				reference.setBackground(Color.white);
				subpanel2.add(reference, BorderLayout.CENTER);
				JPanel appo = new JPanel();
				ang = new  JTextField("360");    
				appo.add(js);
				appo.add(button_js);
				appo.add(ang);
				subpanel2.revalidate();
				subpanel2.repaint();
			} 
		});



		// Buttons actions listener
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				get_data gd = new get_data(db_con);
				gd.createConnectionDialog();
				js = new JSlider(500000,5000000);
				js.setValue(500000);
				subpanel2.removeAll();
				reference2 = new Fillwafer_w_probe(klar, gd);
				reference2.setBackground(Color.white);
				subpanel2.add(reference2, BorderLayout.CENTER);
				JPanel appo = new JPanel();
				ang = new  JTextField("360");    
				appo.add(js);
				appo.add(button_js);
				appo.add(ang);
				subpanel2.revalidate();
				subpanel2.repaint();
			} 
		});




		// Buttons actions listener
		button_db.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  

				js = new JSlider(500000,5000000);
				js.setValue(500000);

				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(subpanel2);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getPath() + "\n");
					subpanel2.removeAll();
					reference = new Fillwafer(chooser.getSelectedFile().getPath());					
					reference.setBackground(Color.white);
					subpanel2.add(reference, BorderLayout.CENTER);
					JPanel appo = new JPanel();
					ang = new  JTextField("360");    
					appo.add(js);
					appo.add(button_js);
					appo.add(ang);

					subpanel2.revalidate();
					subpanel2.repaint();
				} 
			}
		});





		// Buttons actions listener
		button_js.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {   

				double rad = Double.valueOf(ang.getText()).doubleValue();
				reference.rotate(rad);                   

				subpanel2.revalidate();
				subpanel2.repaint();
			} 
		});


		// Buttons actions listener
		button_multi_map.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				subpanel2.removeAll();

				double[][] tag;


				get_data_3 gd = new get_data_3(db_con);
				gd.createConnectionDialog();

				//final Fillwafer_multi fwm = new Fillwafer_multi(gd);
				fwm = new Fillwafer_multi(gd);

				JButton match = new JButton("Match");
				//match.setPreferredSize(new Dimension(25,23));
				match.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {   
						fwm.match(textforpath.getText(), textforpath2.getText(), fwm);
						//fwm.final_report();
						//TableColumn IM = pix_match_tbl.getColumn("MIGView img");
						//IM.setPreferredWidth(200);

						pix_match_tbl.getRowCount();
						pix_match_tbl.revalidate();
						pix_match_tbl.repaint();
					} 
				});

				JButton classreport = new JButton("Defect class report");
				classreport.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {   
						fwm.defect_class_report();
					} 
				});


				JButton densityreport = new JButton("Defect density report");
				densityreport.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {   
						fwm.defect_density_report();
					} 
				});




				fwm.setBackground(Color.white);
				subpanel2.add(fwm, BorderLayout.CENTER);
				//subpanel2.add(button_image_analysis, BorderLayout.SOUTH);
				JPanel appo_pan = new JPanel();
				subpanel2.add(appo_pan, BorderLayout.SOUTH);

				appo_pan.add(textforpath);
				appo_pan.add(textforpath2);
				appo_pan.add(match);
				appo_pan.add(classreport);
				appo_pan.add(densityreport);

				subpanel2.revalidate();
				subpanel2.repaint();

			} 
		});


		// Buttons actions listener
		button_match.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				get_data_2 gd = new get_data_2(db_con);
				gd.createConnectionDialog();

				int mydim = gd.get_dim();
				System.out.println("Dimension is: " + mydim + "\n");

				String mydata[][] = gd.get_all_data();

				double[][] tag;
				double[][] rot_ref;
				double match_pct;
				double match_store;
				int rot_store=0;
				ROW_COUNT=0;

				for(int i = 0; i < mydim; i++) {

					tag = klar.get_db_data(mydata[i][1], mydata[i][2], mydata[i][4], 
							Double.valueOf(mydata[i][5]).doubleValue(), Double.valueOf(mydata[i][6]).doubleValue(),0,0);

					match_store = 0;

					if(tag.length < 20000)
						for(int rot = 0; rot < 360; rot=rot+90)
						{
							rot_ref = rotate(reference.get_matrix(), (double)rot);

							match_pct = check_match(rot_ref, tag);

							if(match_pct > match_store){
								match_store = match_pct;                                               
								rot_store = rot;
							}
						}


					if(match_store > 20.0) {

						getmap gm = new getmap(tag);
						ImageIcon imc2 = new ImageIcon(gm.getImg());
						match_table.setValueAt(imc2, ROW_COUNT, 0);

						match_table.setValueAt(gd.get_data_rc(i, 0), ROW_COUNT, 1);
						match_table.setValueAt(gd.get_data_rc(i,3), ROW_COUNT,  2);
						match_table.setValueAt(gd.get_data_rc(i,7), ROW_COUNT,  3);
						match_table.setValueAt(Double.toString(match_store), ROW_COUNT, 4);
						match_table.setValueAt("na", ROW_COUNT, 5);
						ROW_COUNT++;

						System.out.println("Matching is " + (float)match_store + "%" + " for lot " + mydata[i][0] + ":"
								+ mydata[i][3] + "  and theta= " + rot_store + "\n");
					}

					System.out.println("Check Left: " + (mydim-i) + "\n");
				} 

				TableColumn IM = match_table.getColumn("Img");
				IM.setPreferredWidth(200);

				TableColumn WF = match_table.getColumn("wafer");
				WF.setPreferredWidth(20);

				match_table.setRowHeight(200);
				match_table.getRowCount();
				match_table.revalidate();
				match_table.repaint();

			}
		});




		/////////////////////////////////////////////////////////////////////////
		// Buttons actions listener
		button_cluster_analysis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				File[] suffix = build_dir_tree();
				double xwf = 200 ;
				int SIZE = 5 ;
				int step = (int)(xwf/SIZE);
				get_data_2 gd = new get_data_2(db_con);
				gd.createConnectionDialog();
				int mydim = gd.get_dim();
				System.out.println("Dimension is: " + mydim + "\n");
				String mydata[][] = gd.get_all_data();
				double[][] tag;
				ROW_COUNT=0;
				double ra = 0;
				double[] r = {0,0,0};


				for(int i = 0; i < mydim; i++) {					
					tag = klar.get_db_data(mydata[i][1], mydata[i][2], mydata[i][4], 
							Double.valueOf(mydata[i][5]).doubleValue(), Double.valueOf(mydata[i][6]).doubleValue(),0,0);

					if(tag.length < 130000) {
						double[][] tag2 = adj_tag(tag, tag.length);
						cluster_analysis ca = new cluster_analysis(tag2,  SIZE, Integer.parseInt(mydata[i][4]), step, 0); 
						int dimens = ca.get_cluster_num();

						for(int y=2; y<dimens; y++)  
							if(ca.get_cluster_dim(y) > 2) {
								double[][] clusterxy = ca.analyze_cluster(y);
								r = ca.check_linearity(clusterxy);

								if(r[0] > 0.75) {
									getmap_3 gm = new getmap_3(tag, clusterxy);
									print_image(gm.getImg(), gd.get_data_rc(i, 0), gd.get_data_rc(i,3), gd.get_data_rc(i,7), ROW_COUNT, suffix[0]);
									ROW_COUNT++;
								}


								/*
							if(r[0]<0.6) {
								ra = ca.check_arc(clusterxy);
								if(ra > 0.6) {
									getmap_3 gm = new getmap_3(tag, clusterxy);                           
									print_image(gm.getImg(), gd.get_data_rc(i, 0), gd.get_data_rc(i,3), gd.get_data_rc(i,7), ROW_COUNT, suffix[1]);
									ROW_COUNT++;
								}
							}
								 */


								if(r[0]<0.75) {
									double rbig = ca.check_big_clust(ca.analyze_cluster(y));
									if(rbig > 0.75) {
										getmap_3 gm = new getmap_3(tag, clusterxy);
										print_image(gm.getImg(), gd.get_data_rc(i, 0), gd.get_data_rc(i,3), gd.get_data_rc(i,7), ROW_COUNT, suffix[2]);
										ROW_COUNT++;
									}
									else {
										getmap_3 gm = new getmap_3(tag, clusterxy);                           
										print_image(gm.getImg(), gd.get_data_rc(i, 0), gd.get_data_rc(i,3), gd.get_data_rc(i,7), ROW_COUNT, suffix[1]);
										ROW_COUNT++;
									}
								}


							}
					}

					System.out.println("Check Left: " + (mydim-i) + "\n");
				} 

			}
		});

		/////////////////////////////////////////////////////////////////////////











		// Buttons actions listener match local DB
		button_loc_db_match.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				get_data_2 gd = new get_data_2(db_con);
				gd.createConnectionDialog();
				int mydim = gd.get_dim();
				System.out.println("Dimension is: " + mydim + "\n");
				String mydata[][] = gd.get_all_data();
				double[][] tag;
				double[][] rot_ref;
				double match_pct;
				double match_store;
				int rot_store=0;
				ROW_COUNT=0;
				get_shade_db_data sdb = new get_shade_db_data("db");
				array_matrix[] am = sdb.get_array_mat();
				//new
				ImageIcon imc2;

				for(int i = 0; i < mydim; i++) {
					if(Integer.parseInt(mydata[i][4]) < 10000) {
						tag = klar.get_db_data(mydata[i][1], mydata[i][2], mydata[i][4], 
								Double.valueOf(mydata[i][5]).doubleValue(), Double.valueOf(mydata[i][6]).doubleValue(),0,0);

						for(int j=0; j<am.length; j++) {     
							match_store = 0;                   
							for(int rot = 0; rot < 360; rot=rot+90)
							{
								rot_ref = rotate(am[j].get_mat(), (double)rot);
								match_pct = check_match(rot_ref, tag);

								if(match_pct > match_store){
									match_store = match_pct;                                               
									rot_store = rot;
								}
							}


							if(match_store > 40.0) {
								getmap gm = new getmap(tag);
								imc2 = new ImageIcon(gm.getImg());
								match_table.setValueAt(imc2, ROW_COUNT, 0);
								match_table.setValueAt(gd.get_data_rc(i, 0), ROW_COUNT, 1);
								match_table.setValueAt(gd.get_data_rc(i,3), ROW_COUNT,  2);
								match_table.setValueAt(gd.get_data_rc(i,7), ROW_COUNT,  3);
								match_table.setValueAt(Double.toString(match_store), ROW_COUNT, 4);
								match_table.setValueAt(am[j].get_name(), ROW_COUNT, 5);
								ROW_COUNT++;
							}

							System.out.println("Matching is " + (float)match_store + "%" + " for lot " + mydata[i][0] + ":"
									+ mydata[i][3] + "  and theta= " + rot_store + "\n");
						} 
					}
					System.out.println("Check Left: " + (mydim-i) + "\n");
				}


				TableColumn IM = match_table.getColumn("Img");
				IM.setPreferredWidth(200);
				TableColumn WF = match_table.getColumn("wafer");
				WF.setPreferredWidth(20);
				match_table.setRowHeight(200);
				match_table.getRowCount();
				match_table.revalidate();
				match_table.repaint();
			}
		});














		// Buttons actions listener match local DB
		button_wf2db_match.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  

				match_db(reference.get_matrix());			


			}
		});














		// Buttons actions listener match local DB
		button_show_db.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				double match_store;
				int rot_store=0;
				ROW_COUNT=0;
				get_shade_db_data sdb = new get_shade_db_data("db");
				array_matrix[] am = sdb.get_array_mat();
				ImageIcon imc2;                            

				for(int j=0; j<am.length; j++) {     
					getmap gm = new getmap(am[j].get_mat());
					imc2 = new ImageIcon(gm.getImg());
					match_table.setValueAt(imc2, ROW_COUNT, 0);
					match_table.setValueAt("null", ROW_COUNT, 1);
					match_table.setValueAt("null", ROW_COUNT,  2);
					match_table.setValueAt("null", ROW_COUNT,  3);
					match_table.setValueAt("null", ROW_COUNT, 4);
					match_table.setValueAt(am[j].get_name(), ROW_COUNT, 5);
					ROW_COUNT++;

					System.out.println("SHOW DB " + "for " + am[j].get_name() + "\n");
				}

				TableColumn IM = match_table.getColumn("Img");
				IM.setPreferredWidth(200);
				TableColumn WF = match_table.getColumn("wafer");
				WF.setPreferredWidth(20);
				match_table.setRowHeight(200);
				match_table.getRowCount();
				match_table.revalidate();
				match_table.repaint();
			}
		});




		/////////////////////////////
		//BLACK
		// Buttons actions listener
		button_black.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				get_data_2 gd = new get_data_2(db_con);
				gd.createConnectionDialog();
				int mydim = gd.get_dim();
				System.out.println("Dimension is: " + mydim + "\n");
				String mydata[][] = gd.get_all_data();
				double[][] tag;
				double[][] rot_ref;
				double match_pct;
				double match_store;
				int rot_store=0;
				ROW_COUNT=0;

				for(int i = 0; i < mydim; i++) {

					tag = klar.get_db_data(mydata[i][1], mydata[i][2], mydata[i][4], 
							Double.valueOf(mydata[i][5]).doubleValue(), Double.valueOf(mydata[i][6]).doubleValue(),0,0);

					match_store = 0;

					if(tag.length > 10000)
					{

						getmap gm = new getmap(tag);
						ImageIcon imc2 = new ImageIcon(gm.getImg());
						match_table.setValueAt(imc2, ROW_COUNT, 0);

						match_table.setValueAt(gd.get_data_rc(i, 0), ROW_COUNT, 1);
						match_table.setValueAt(gd.get_data_rc(i,3), ROW_COUNT,  2);
						match_table.setValueAt(gd.get_data_rc(i,7), ROW_COUNT,  3);
						match_table.setValueAt(Double.toString(match_store), ROW_COUNT, 4);
						ROW_COUNT++;

					}

					System.out.println("Check Left: " + (mydim-i) + "\n");
				} 

				TableColumn IM = match_table.getColumn("Img");
				IM.setPreferredWidth(200);
				TableColumn WF = match_table.getColumn("wafer");
				WF.setPreferredWidth(20);
				match_table.setRowHeight(200);
				match_table.getRowCount();
				match_table.revalidate();
				match_table.repaint();
			}
		});




		// Buttons actions listener
		buttonfc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {                  
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(subpanel2);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getPath() + "\n");

					try {   
						File logfile = new File(chooser.getSelectedFile().getPath());
						FileWriter log = new FileWriter(logfile);
						boolean fc  = logfile.createNewFile();
						double[][] sel_mat = reference.get_matrix();

						for(int l=0; l < sel_mat.length; l++){  
							log.write(sel_mat[l][0] +","+ sel_mat[l][1] +"\n");
							log.flush();
						}

						log.close();

					} catch (Exception et) {
						System.out.println(et);
					}
				}

			} 
		}); 






		pix_match_tbl.addMouseListener(new  MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {                  
				if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
					int rowindex = pix_match_tbl.getSelectedRow(); 
					String ij = (String)pix_match_tbl.getValueAt(rowindex, 0);
					String REGEX=",";
					Pattern pattern = Pattern.compile(REGEX);
					String[] die_split = pattern.split(ij);

					// Mark die x selection
					fwm.set_selection(die_split[0], die_split[1]);

					//Need to redraw the map ...
					fwm.repaint();
				}


				if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) { 
					int rowindex = pix_match_tbl.getSelectedRow(); 
					//System.out.println(rowindex);

					int id = (int)(double)pix_match_tbl.getValueAt(rowindex, 4);
					String step = (String)pix_match_tbl.getValueAt(rowindex, 5);

					String id2 = Integer.toString(id);

					String ret = "null";

					if(! id2.equals("0"))
						ret = fwm.get_sem(id2,step);

					/*
					if(!ret.equals("null")){
						JFrame frametemp = new JFrame("SEMImage");
						//frametemp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frametemp.setSize(520,530);

						ShowImage panel = new ShowImage("sem/sem.jpg");
						frametemp.setContentPane(panel); 
						frametemp.setVisible(true); 
					}
					 */

					String ij = (String)pix_match_tbl.getValueAt(rowindex, 0);
					String coord = (String)pix_match_tbl.getValueAt(rowindex, 8);
					String bin = (String)pix_match_tbl.getValueAt(rowindex, 1);

					String REGEX=",";
					Pattern pattern = Pattern.compile(REGEX);
					String[] die_split = pattern.split(ij);
					int new_i = Integer.parseInt(die_split[0]);
					int new_j = Integer.parseInt(die_split[1]);

					String[] coord_split = pattern.split(coord);
					int new_x = Integer.parseInt(coord_split[0]);
					int new_y = Integer.parseInt(coord_split[1]);

					String REGEX2=":";
					pattern = Pattern.compile(REGEX2);
					String[] bin_split = pattern.split(bin);

					int dim = fwm.get_dieinf_dim();
					String temp = "";

					for(int i = 0; i< dim; i++)
					{
						String a = (String)fwm.get_dieinf(i,0);
						String b = (String)fwm.get_dieinf(i,1);
						int _i = Integer.parseInt(a);
						int _j = Integer.parseInt(b);

						if(new_i == _i && new_j == _j){
							temp = (String)fwm.get_dieinf(i,7);
							break;
						}

					}


					if(!temp.equals(""))
					{
						JFrame frametemp2 = new JFrame("MIGview");
						//frametemp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frametemp2.setSize(1210,830);
						//frametemp2.setLocation(0,0);
						//String temp = "//aptprbfs1b/vol18/1345601541.7585519.003.5519-01.QPP.00.C1EA.X02S/1345601541.7585519.003.5519-01.QPP.00.C1EA.+004.+001.X02S.0.1.Bin_D.png";
						String sem;
						if(!ret.equals("null"))
							sem = "sem/sem.jpg";
						else
							sem = "null";

						ShowMVImage panel2 = new ShowMVImage(temp, sem, new_x,  new_y, bin_split[0]);
						frametemp2.setContentPane(panel2); 
						frametemp2.setVisible(true); 
					}




				}
			}
		}); 




		///////////////////// layout //////////////////////////////  
		// Mainpanel layout
		mainPanel.setLayout(new BorderLayout());  
		subpanel2.setLayout(new BorderLayout());
		mainPanel.add(toolBar, BorderLayout.NORTH);
		mainPanel.add(subpanel2, BorderLayout.CENTER);
		subpan3 = new JPanel();
		subpan3.setLayout(new GridLayout(2,1));

		//subpan3.add(scrollpane);
		subpan3.add(sp_for_pixmatchtbl);
		//sp_for_pixmatchtbl.setPreferredSize(new Dimension(720,600)); 
		scrollpane.setPreferredSize(new Dimension(520,500)); 
		//mainPanel.add(areaScrollPane4, BorderLayout.SOUTH);
		subpan3.add(areaScrollPane4);
		mainPanel.add(toolBar_bottom, BorderLayout.SOUTH);
		mainPanel.add(subpan3, BorderLayout.EAST);

		// toolbar
		toolBar.add(button_multi_map);
		//toolBar.add(button_in_line);
		//toolBar.add(buttonpmap);  
		//toolBar.add(button);   
		//toolBar.add(button_match);  
		//toolBar.add(button_wf2db_match);
		//toolBar.add(buttonfc); 
		//toolBar.add(button_db); 
		//toolBar.add(button_black); 
		//toolBar.add(button_loc_db_match);
		//toolBar.add(button_show_db);
		toolBar.add(button_cluster_analysis);
		//toolBar.add(button_baricenter);
		//toolBar.add(button_image_analysis);

		toolBar_bottom.add(textforbinout);

		MenuBar.add(menu);
		menu.add(menuItem);



		////////////////////////////////////////////////////////////
		// INTRO...
		System.out.println("**************************************");
		System.out.println("************  Pixel matching  ************");
		System.out.println("**************************************");

		///// stablich klarity connection		
		klar = new db();
		db_con = klar.get_connection();

		///// establish probe connection
		//probe = new probe_db();
		//probe_db_con = probe.get_connection();

		/*
		// prova hot pxels
		try {
			hp myhp = new hp("//aptprbfs1b/vol6/1388209835.7753779.003.3779-05.RPP.00.C2GA.X07S");
		} catch (IOException ex2) {
			System.out.println(ex2);
		}
		 */

	}
	//=======================================================================================//



	private void updateTextArea(final String text) {  
		SwingUtilities.invokeLater(new Runnable() {  
			public void run() {     
				textArea4.append(text);     }   }); }  

	private void redirectSystemStreams() {   
		OutputStream out = new OutputStream() {     
			@Override   
			public void write(int b) throws IOException {    
				updateTextArea(String.valueOf((char) b));     }     

			@Override  
			public void write(byte[] b, int off, int len) 	throws IOException {     
				updateTextArea(new String(b, off, len));     
			}      

			@Override    
			public void write(byte[] b) throws IOException {       
				write(b, 0, b.length);     }   };   

				System.setOut(new PrintStream(out, true));   
				System.setErr(new PrintStream(out, true)); 
	} 



	///////////////////////////////////////////////////////////////////////
	public class ShowImage extends JPanel{
		BufferedImage image; // Declare a name for our Image object.
		public void paint(Graphics g) {  g.drawImage(image, 0, 0, null);     } 
		// Create a constructor method
		public ShowImage(String file){
			super();
			try{
				image = ImageIO.read(new File(file));
			} catch (IOException e) {
				System.out.println("exception in load img "+ e);
			}
		}
	}
	//////////////////////////////////////////////////////////




	//////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	public class ShowMVImage extends JPanel{
		BufferedImage image0;
		int defx;
		int defy;
		String o_file;
		String sem_file;
		String bin;

		// Create a constructor method
		public ShowMVImage(String file, String file2, int dx, int dy, String btype){
			super();
			try{
				defx = dx;
				defy = dy;
				o_file = file;
				sem_file = file2;
				bin = btype;

				image0 = drawdefect(defx, defy);


			} catch (Exception e) {
				System.out.println("exception in load img "+ e);
			}

		}



		public void paint(Graphics g) {  

			g.drawImage(image0, 0, 0, null);

		}  



		public BufferedImage getsubimg(int x, int y, BufferedImage img, int a, int b)
		{
			int minrow = x-a;
			int mincol = y-b;
			int colwidth = 2*a;
			int rowwidth = 2*b;
			int imgWidth = img.getWidth();    
			int imgHeight = img.getHeight();  

			if(minrow < 0) minrow = 0;
			if(mincol < 0) mincol = 0;
			if(minrow > imgHeight) minrow = imgHeight -a;
			if(mincol > imgWidth) mincol = imgWidth -b;

			if(minrow+rowwidth > imgHeight) rowwidth=imgHeight-minrow;
			if(mincol+colwidth > imgWidth) colwidth=imgWidth-mincol;

			BufferedImage fail_img = img.getSubimage(mincol, minrow, colwidth, rowwidth) ;

			return fail_img;
		}





		public BufferedImage drawdefect(int x, int y)
		{
			BufferedImage retImage=null;

			try {   

				//System.out.println("Getting: "+o_file);
				//BufferedImage pointer = ImageIO.read(new File("sem/point.png"));
				BufferedImage SEMimage;
				BufferedImage img = ImageIO.read(new File(o_file));
				int imgWidth = img.getWidth();    
				int imgHeight = img.getHeight();    
				//System.out.println("img size: " + imgWidth+","+imgHeight);


				//int type = img.getType() == 0? BufferedImage.TYPE_INT_ARGB : img.getType();
				int type = BufferedImage.TYPE_INT_RGB;
				//System.out.println("img type: " + type);

				// draw defect loc
				BufferedImage newImage = new BufferedImage(imgWidth, imgHeight, type);
				Graphics2D g3 = newImage.createGraphics(); 			
				g3.drawImage(img, 0, 0, imgWidth, imgHeight, null);

				float[] factors;
				float[] offsets;
				if(bin.equals("D")){
					factors = new float[] {	10.0f,10.0f,10.0f};
					offsets = new float[] {-25.0f,-25.0f,-25.0f};	
				} else {
					factors = new float[] {	1.1f,1.1f,1.1f};
					offsets = new float[] {10.0f,10.0f,10.0f};	
				}

				RescaleOp rescaleOp = new RescaleOp(factors, offsets, null); 
				rescaleOp.filter(newImage, newImage);  // Source and destination are the same. 

				BufferedImage image2 = getsubimg(x, y, newImage, 100, 100);
				BufferedImage image3 = getsubimg(x, y, newImage, 20, 20);

				g3.setPaint(Color.red);
				RectangularShape E1 = new Ellipse2D.Double(y-1, x-1, 2, 2);
				g3.fill(E1);

				BasicStroke mystroke = new BasicStroke(4.0f);
				g3.setStroke(mystroke);
				Rectangle2D.Double rg = new Rectangle2D.Double(y-50 ,x-50, 100, 100);
				g3.draw(rg);
				//g3.drawImage(pointer, y, x, 100, 100, null);
				g3.dispose();  

				int width = 1200;
				int height = 780;
				retImage = new BufferedImage(width, height, type);  			
				Graphics2D g2 = retImage.createGraphics();     	
				//g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);    
				//g.setBackground(background);    
				//g.clearRect(0, 0, width, height);      

				// Firts method x resize
				int s_width = 900;
				int s_height = 780;

				// ** add scaled img ** //
				g2.drawImage(newImage, 0, 0, s_width, s_height, null);

				// ** add SEM ** //
				if(!sem_file.equals("null")){
					SEMimage = ImageIO.read(new File(sem_file));
					g2.drawImage(SEMimage, 910, 0,250,250, null);
				}

				// **  add subimg ** //
				g2.setPaint(Color.blue);
				g2.drawImage(image2, 910, 260,250,250, null);
				g2.drawString("100 x 100 pixel", 920, 280);

				g2.drawImage(image3, 910, 520,250,250, null);
				g2.drawString("20 x 20 pixel", 920, 540);

				g2.dispose();

			} catch (Exception e) {

				System.out.println("exception in load img "+ e);
			}

			return retImage;

		}


		/*
		public BufferedImage scaleImage(BufferedImage img, int width, int height,  Color background) 
		{    
			int imgWidth = img.getWidth();    
			int imgHeight = img.getHeight();    

			if (imgWidth*height < imgHeight*width) {      
				width = imgWidth*height/imgHeight;     
			} else { 
			height = imgHeight*width/imgWidth;     }  

			int type = img.getType() == 0? BufferedImage.TYPE_INT_ARGB : img.getType();

			BufferedImage newImage = new BufferedImage(width, height, type);     
			Graphics2D g = newImage.createGraphics();     
			try {        
				//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);    
				g.setBackground(background);    
				g.clearRect(0, 0, width, height);      
				g.drawImage(img, 0, 0, width, height, null);     
			} finally { 
				g.dispose();     
			}   

			return newImage; 
		} 	
		 */


	}	






	void match_db(double[][] mat)
	{
		double xwf = 200 ;
		int SIZE = 5 ;
		int step = (int)(xwf/SIZE);

		build_db[] db_data =  build_linear_DB();

		cluster_analysis ca = new cluster_analysis(adj_tag_2(mat, mat.length),  SIZE, mat.length, step, 1); 
		int dimens = ca.get_cluster_num();

		for(int y=2; y<dimens; y++) {
			if(ca.get_cluster_dim(y) > 2) {
				double[] r = ca.check_linearity(ca.analyze_cluster(y));

				for(int i=0; i < db_data.length; i++){
					if(db_data[i].get_m() == -1) 
					{
						break;
					}else{
						//System.out.println("try to match " +  "\n");
						if(((db_data[i].get_m()/r[1]) < 1.2)  && ((db_data[i].get_m()/r[1]) > 0.8) && (db_data[i].get_q()/r[2] < 1.2)  && (db_data[i].get_q()/r[2] > 0.8)) { 
							System.out.println("Matching: " + db_data[i].get_name()+ "\n");
						}
					}
				}
			}

		}


	}



	///////////////////////////////////////////////////////////////////
	build_db[] build_linear_DB() 
	{	
		double xwf = 200 ;
		int SIZE = 5 ;
		int step = (int)(xwf/SIZE);
		int count = 0;
		get_shade_db_data sdb = new get_shade_db_data("db");
		array_matrix[] am = sdb.get_array_mat();

		build_db[] db_mq = new build_db[100];

		for(int j=0; j<100; j++)
			db_mq[j] = new build_db();
		// TODO check sulla dim < 100...

		for(int i=0; i<am.length; i++) {     

			//		System.out.println("before cluster analysis" + "\n");
			double[][] tag2 = adj_tag_2(am[i].get_mat(), am[i].get_def_num());
			cluster_analysis ca = new cluster_analysis(tag2,  SIZE, am[i].get_def_num(), step, 0); 
			int dimens = ca.get_cluster_num();

			for(int y=2; y<dimens; y++) {
				if(ca.get_cluster_dim(y) > 2) {
					double[] r = ca.check_linearity(ca.analyze_cluster(y));

					db_mq[count].set_r2(r[0]);
					db_mq[count].set_m(r[1]);
					db_mq[count].set_q(r[2]);
					db_mq[count].set_name(am[i].get_name());
					System.out.println(am[i].get_name() + " " + r[1] + " " +r[2] + "\n");
					count++;
				}

			}
		}
		return(db_mq);
	}



	class build_db {
		double my_m;
		double my_q;
		double my_r2;
		String name;

		build_db(){
			my_m =-1;
			my_q =-1;
			my_r2 =-1;
			name = "void";
		}

		void set_m(double m){
			my_m = m;
		}

		void set_q(double q){
			my_q = q;
		}

		void set_r2(double r2){
			my_r2 = r2;
		}

		void set_name(String my_name){
			name = my_name;
		}


		double get_m(){
			return(my_m);
		}

		double get_q(){
			return(my_q);
		}

		double get_r2(){
			return(my_r2);
		}

		String get_name(){
			return(name);
		}



	}

	///////////////////////////////////////////////////////////////////////////////





	////////////////////////////////////////////////////////////	
	String get_cur_time() {
		String curtime;
		Calendar rightNow = Calendar.getInstance();
		int y = rightNow.get(Calendar.YEAR);
		int m = rightNow.get(Calendar.MONTH);
		int d = rightNow.get(Calendar.DAY_OF_MONTH);
		curtime = Integer.toString(y) +Integer.toString(m) +Integer.toString(d);

		return(curtime);
	}










	//////////////////////////////////////////////////////
	File[] build_dir_tree()
	{
		File[] mydir = new File[tree.length];
		String locdir = get_cur_time();

		try {

			String temp = CUR_DIR + locdir + "/";
			File  dir = new File(temp);        
			dir.mkdir();

			for(int i = 0; i < tree.length; i++)  
			{
				mydir[i] = new File( temp + tree[i]);
				mydir[i].mkdir();
			}

		} catch (Exception ex5) {
			System.out.println(ex5);
		}

		return(mydir);
	}



	///////////////////////////////////////////////////////////	
	void print_image(BufferedImage bim, String lot, String wf, String lay, int count, File suf)
	{
		String fname =  lot + " " + wf + " " + lay + " " + Integer.toString(count) + ".jpeg"; 
		File file = new File(suf +"/"+ fname);

		try {
			ImageIO.write(bim, "jpeg", file);

		} catch (IOException ex2) {
			System.out.println(ex2);
		}
	}





	/////////////////////////////////////////////////////////////////////
	double[][] adj_tag(double[][] tag_33, int dim) {
		double[][] tag_ale = new double[dim][3];

		for(int l=0; l < dim; l++){   
			//tag_ale[l][0] =  (200 - (tag_33[l][0]/1000000));
			tag_ale[l][0] =  ((tag_33[l][0]/1000000));
			tag_ale[l][1] =  tag_33[l][1]/1000000;
			tag_ale[l][2] =  tag_33[l][2];
		}

		return(tag_ale);
	}


	double[][] adj_tag_2(double[][] tag_33, int dim) {
		double[][] tag_ale = new double[dim][2];

		for(int l=0; l < dim; l++){   
			//tag_ale[l][0] =  (200 - (tag_33[l][0]/1000000));
			tag_ale[l][0] =  ((tag_33[l][0]/1000000));
			tag_ale[l][1] =  tag_33[l][1]/1000000;
		}

		return(tag_ale);
	}



	/***************************
	 *
	 ***************************/
	public double[][] rotate(double[][] mat, double angle)
	{
		double deg = Math.toRadians(angle);
		double center = 100000000;

		for(int l=0; l < mat.length; l++){   
			double x = mat[l][0] - center;
			double y = mat[l][1] - center;
			double rho = Math.sqrt( (x * x) + (y * y) );
			double alpha = Math.atan2(x,y);
			double theta = Math.PI/2 - (alpha + deg);
			mat[l][0] =  rho * Math.cos(theta) + center;       
			mat[l][1] =  rho * Math.sin(theta) + center;
		}
		return(mat);
	}



	/*******************************
	 * 
	 *
	 ********************************/
	public double check_match(double[][] target, double[][] reference)
	{
		int target_def = target.length;
		int reference_def = reference.length;
		int match_num = 0;
		double match_pct = 0;
		//double error = 500000; 
		//error =  (double)js.getValue(); 
		error =  500000; 

		int match;

		for(int i = 0; i < target_def; i++){

			for(int j = 0; j < reference_def; j++){

				if(  ( (target[i][0] < (reference[j][0]+error))  && (target[i][0] > (reference[j][0]-error)) )   && 
						( (target[i][1] < (reference[j][1]+error))  && (target[i][1] > (reference[j][1]-error)) )     )
				{            
					//reference[j][2] = 1; //matched def
					match_num++;
					break;
				}
			}
		}         

		match_pct = ((double)match_num / (double)target_def) * 100;

		return(match_pct);
	}






	/*******************************
	 *
	 ********************************/


	class pixmatch_TableModel extends AbstractTableModel {
		public String[] columnNames = {"Die","Bin", "Type", "pix r/c","defid", "layer", "Defect class", "region", "def r/c"};


		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {            

			return(ROW_COUNT);
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {

			return pix_match_data[row][col];
		}

		public void setValueAt(Object value, int row, int col) {
			pix_match_data[row][col] = value; 
			fireTableCellUpdated(row, col);
		} 

		public boolean isCellEditable(int row, int col) {
			return false;

		}

		public Class getColumnClass(int c) { 
			return getValueAt(0, c).getClass(); 

		}


	}






	/*******************************
	 *
	 ********************************/
	class match_TableModel extends AbstractTableModel {
		public String[] columnNames = {"Img","Lot", "wafer", "layer", "GOF", "Type"};


		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {            

			return(ROW_COUNT);
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {

			return match_data[row][col];
		}

		public void setValueAt(Object value, int row, int col) {
			match_data[row][col] = value; 
			fireTableCellUpdated(row, col);
		} 

		public boolean isCellEditable(int row, int col) {
			return false;

		}

		public Class getColumnClass(int c) { 
			return getValueAt(0, c).getClass(); 

		}


	}







	/*******************************
	 * 
	 *
	 ********************************/
	public class get_data {

		JPanel      connectionPanel; 
		JPanel      mainPanel;
		JPanel      south_pan;
		JDialog     jd;
		JButton button;
		JTextField field1;
		JTextField field2;
		JTextField field3;
		JTextField field4;
		JTextField field5;
		JTextField field6;
		Connection dbc; 
		String it;
		String wk;
		String def_num;
		String Lot;
		String wf_id;
		String Layer;
		int ret;
		String cx, cy;
		String rk;

		public get_data(Connection mycon2)
		{
			ret = 0;
			dbc = mycon2;
			it = null;
			wk = null;
			def_num = null;
		}


		public String get_it(){
			return it;
		}


		public String get_lot(){
			return Lot;
		}


		public String get_wid(){
			return wf_id;
		}


		public String get_layer(){
			return Layer;
		}

		public String get_wk(){
			return wk;
		}



		public String get_def_num(){
			return def_num;
		}


		public String get_cx(){
			return cx;
		}

		public String get_cy(){
			return cy;
		}




		public void createConnectionDialog() {
			button = new JButton("GO");  
			field1 = new JTextField();               
			field1 = new JTextField("Lot");
			field1.setEditable(false);
			field2 = new JTextField(); 
			field3 = new JTextField(" Layer step id");
			field3.setEditable(false); 
			field4 = new JTextField();         
			field5 = new JTextField("wafer");
			field5.setEditable(false); 
			field6 = new JTextField();
			connectionPanel = new JPanel(false);
			connectionPanel.setLayout(new GridLayout(3,2));
			mainPanel = new JPanel(false);
			mainPanel.setLayout(new BorderLayout());
			south_pan = new JPanel();


			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Statement stmt = dbc.createStatement();
						// E' importante che la query abbia la data espansa con l'orario...
						// to_char, to_date.
						//String SQL_stmt = "select TO_CHAR(inspection_time, 'MM/DD/YYYY HH24:MI:SS'), wafer_key, wafer_id " +
						Lot = field2.getText();
						wf_id = field6.getText();
						Layer = field4.getText();	

						String SQL_stmt = "select inspection_time, wafer_key, wafer_id, defects, center_x, center_y , recipe_key" +
								" from INSP_WAFER_SUMMARY " +
								" WHERE LOT_ID="+ "\'" + field2.getText() + "\'" +
								" AND LAYER_ID = " + "\'" + field4.getText() + "\'" +
								" AND wafer_id =  " +  "\'" + field6.getText()+ "\'";

						System.out.println(SQL_stmt + "\n");
						ResultSet rs = stmt.executeQuery(SQL_stmt);

						while (rs.next()) {
							it = rs.getString("inspection_time");
							wk = rs.getString("wafer_key");
							String def_id = rs.getString("wafer_id");
							def_num = rs.getString("defects");
							cx = rs.getString("center_x");
							cy = rs.getString("center_y");
							rk = rs.getString("recipe_key");

							System.out.println("time = " + it);
							System.out.println(" | wk = " + wk);
							System.out.println(" | wid = " + def_id);
							System.out.println(" | defects = " + def_num );
							System.out.println(" | cx = " + cx);
							System.out.println(" | cy = " + cy + "\n");
						}


						String SQL_stmt2 = "select orientation_feature, rotation_angle" +
								" from INSP_RECIPE " +
								"WHERE recipe_key = " + rk;

						ResultSet rs2 = stmt.executeQuery(SQL_stmt2);

						while (rs2.next()) {
							String orientation = rs2.getString("orientation_feature");
							String angle = rs2.getString("rotation_angle");

							System.out.println("orientation = " + orientation );
							System.out.println(" | angle = " + angle +"\n");
						}


						if(ret==1)  {
							// Popup
							JOptionPane.showMessageDialog(jd,
									"The test",
									"Test out not found",
									JOptionPane.ERROR_MESSAGE);

							// Exit
							System.exit(0);
						}  else
							jd.dispose();



					} catch (SQLException ex2) {

						System.out.println(ex2);
					}

				} 
			});


			connectionPanel.add(field1);
			connectionPanel.add(field2);
			connectionPanel.add(field3);
			connectionPanel.add(field4);  
			connectionPanel.add(field5);      
			connectionPanel.add(field6);
			south_pan.add(button);
			mainPanel.add(connectionPanel, BorderLayout.CENTER);
			mainPanel.add(south_pan , BorderLayout.SOUTH);
			JFrame jf = new JFrame();
			jd = new JDialog(jf,"SSA", true); 
			jd.setContentPane(mainPanel);
			jd.setLocation(250,250);
			jd.setSize(new Dimension(280,200));

			jd.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {System.exit(0);}
			});

			jd.pack();
			jd.setVisible(true);      
		}
	}
















	/*******************************
	 * 
	 *
	 ********************************/
	public class get_data_2 {

		JPanel      connectionPanel; 
		JPanel      mainPanel;
		JPanel      south_pan;
		JCheckBox   box;
		JDialog     jd;
		JButton button;
		JTextField field1;
		JTextField field2;
		JTextField field3;
		JTextField field4;
		JTextField field5;
		JTextField field6;
		Connection dbc; 
		String it, lid;
		String wk;
		String def_num;
		String rk;
		String cx, cy;
		int ret;
		String [][] data;
		int dimension;
		String device;
		String layer;


		public get_data_2(Connection mycon2)
		{
			ret = 0;
			dbc = mycon2;
			it = null;
			wk = null;
			def_num = null;
			data = new String[10000][8];

			dimension = 0;

		}


		public String[][] get_all_data(){
			return data;
		}



		public int get_dim(){
			return dimension;
		}


		public String get_data_rc(int x, int y) {

			return(data[x][y]);
		}







		public void createConnectionDialog() {
			button = new JButton("GO");  

			field1 = new JTextField();               
			field1 = new JTextField("Starting date (YYYY/MM/DD HH24:MI:SS)");
			field1.setEditable(false);
			field2 = new JTextField(); 
			field3 = new JTextField("device");
			field3.setEditable(false); 
			field4 = new JTextField();         
			field5 = new JTextField("step");
			field5.setEditable(false); 
			field6 = new JTextField();

			box = new JCheckBox("Bare");

			connectionPanel = new JPanel(false);
			connectionPanel.setLayout(new GridLayout(3,2));

			mainPanel = new JPanel(false);
			mainPanel.setLayout(new BorderLayout());
			south_pan = new JPanel();




			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					try {

						Statement stmt = dbc.createStatement();

						//prepare the SQL string...

						if(! field4.getText().equals(""))
							device = " AND DEVICE =  "  +  "\'" + field4.getText() + "\'";
						else
							device = "";


						if(! field6.getText().equals(""))
							layer = " AND LAYER_ID = " + "\'" + field6.getText() + "\'" ;
						else
							layer = "";

						String SQL_stmt = "";

						if(box.isSelected()){
							SQL_stmt = "select lot_id, inspection_time, wafer_key, wafer_id, defects, center_x, center_y, recipe_key, layer_id" +
									" from INSP_WAFER_SUMMARY " +

							"WHERE inspection_time >= TO_DATE ('" + field2.getText() + "', 'YYYY/MM/DD HH24:MI:SS')" +
							device +
							layer +
							" AND INSPECT_EQUIP_ID IN ('KSPD9A0100','KSPD9A0200','KSPD9A0300','KSPD9A0400','KSPD9A0500')";

						} else {

							SQL_stmt = "select lot_id, inspection_time, wafer_key, wafer_id, defects, center_x, center_y, recipe_key, layer_id" +
									" from INSP_WAFER_SUMMARY " +

							"WHERE inspection_time >= TO_DATE ('" + field2.getText() + "', 'YYYY/MM/DD HH24:MI:SS')" +
							device +
							layer +
							" AND INSPECT_EQUIP_ID NOT IN ('KSPD9A0100','KSPD9A0200','KSPD9A0300','KSPD9A0400','KSPD9A0500')";
						}





						System.out.println(SQL_stmt + "\n");

						System.out.println(SQL_stmt + "\n");

						ResultSet rs = stmt.executeQuery(SQL_stmt);

						int c = 0;
						while (rs.next()) {
							String lot = rs.getString("lot_id");
							it = rs.getString("inspection_time");
							wk = rs.getString("wafer_key");
							String wf_id = rs.getString("wafer_id");
							def_num = rs.getString("defects");
							cx = rs.getString("center_x");
							cy = rs.getString("center_y");
							rk = rs.getString("recipe_key");
							lid = rs.getString("layer_id");

							data[c][0] = lot;
							data[c][1] = it;
							data[c][2] = wk;
							data[c][3] = wf_id;
							data[c][4] = def_num;
							data[c][5] = cx;
							data[c][6] = cy;
							data[c][7] = lid;


							System.out.println("time = " + it);
							System.out.println(" | wk = " + wk);
							System.out.println(" | wid = " + wf_id);
							System.out.println(" | rk = " + rk);
							System.out.println(" | layer = " + lid);
							System.out.println(" | defects = " + def_num + "\n");


							c++;
						}

						dimension = c;

						rs.close();
						stmt.close();


						if(ret==1)  {
							// Popup
							JOptionPane.showMessageDialog(jd,
									"The test",
									"Test out not found",
									JOptionPane.ERROR_MESSAGE);

							// Exit
							System.exit(0);
						}  else
							jd.dispose();


					} catch (SQLException ex2) {

						System.out.println(ex2);
					}

				} 
			});



			connectionPanel.add(field1);
			connectionPanel.add(field2);
			connectionPanel.add(field3);
			connectionPanel.add(field4);  
			connectionPanel.add(field5);      
			connectionPanel.add(field6);
			south_pan.add(box);
			south_pan.add(button);


			mainPanel.add(connectionPanel, BorderLayout.CENTER);
			mainPanel.add(south_pan , BorderLayout.SOUTH);

			JFrame jf = new JFrame();

			jd = new JDialog(jf,"SSA", true); 
			jd.setContentPane(mainPanel);
			jd.setLocation(250,250);
			jd.setSize(new Dimension(280,200));

			jd.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {System.exit(0);}
			});

			jd.pack();
			jd.setVisible(true);      
		}
	}

















	/*******************************
	 * 
	 *
	 ********************************/
	public class get_data_3  {

		JPanel      connectionPanel; 
		JPanel      mainPanel;
		JPanel      south_pan;
		JPanel      nord_pan;
		JCheckBox   box;
		JDialog     jd;
		JButton button;
		JTextField field1;
		JTextField field2;
		JTextField field3;
		JTextField field4;
		JTextField field5;
		JTextField field6;
		JComboBox combo_lot = null;
		JComboBox combo_wafer = null;
		JComboBox combo_steps = null;
		JComboBox combo_probe = null;
		JComboBox combo_db = null;
		JList list;
		DefaultListModel listModel;
		JButton button_lot;
		JButton button_wafer;
		JButton button_steps;
		Connection dbc; 
		String it, lid;
		String wk;
		String def_num;
		String rk;
		String cx, cy;
		int ret;
		String [][] data;
		int dimension;
		String device;
		String layer;
		String test_type;

		public get_data_3(Connection mycon2)
		{
			ret = 0;
			dbc = mycon2;
			it = null;
			wk = null;
			def_num = null;
			data = new String[10000][8];

			dimension = 0;

		}


		public String[][] get_all_data(){
			return data;
		}



		public int get_dim(){
			return dimension;
		}


		public String get_data_rc(int x, int y) {

			return(data[x][y]);
		}

		public String get_ptest() {

			return(test_type);
		}	


		public boolean not_in_combo(JComboBox comb, int index, String val){
			boolean r = true;


			for(int i=0; i<=index; i++){
				String cval = (String)comb.getItemAt(i);
				//System.out.println(cval);
				if(val.equals(cval)){
					r = false ;
					break;
				}
			}
			return r;
		}



		public void createConnectionDialog() {
			button = new JButton("GO");  

			//field1 = new JTextField();

			field1 = new JTextField("Lot");
			field1.setEditable(false);

			field2 = new JTextField(); 
			combo_lot = new JComboBox();
			combo_lot.setEditable(true); 
			button_lot = new JButton("<<<");


			field3 = new JTextField("wafer");
			field3.setEditable(false); 

			field4 = new JTextField();
			combo_wafer = new JComboBox();
			combo_wafer.setEditable(true); 
			button_wafer = new JButton("<<<");
			//button_wafer.setPreferredSize(new Dimension(10, 10));

			field5 = new JTextField("step");
			field5.setEditable(false); 

			field6 = new JTextField();
			button_steps = new JButton("<<<");

			listModel = new DefaultListModel();
			list = new JList(listModel); //data has type Object[]
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list.setLayoutOrientation(JList.VERTICAL);
			list.setVisibleRowCount(-1);
			//list.ensureIndexIsVisible(2); 

			JScrollPane listScroller = new JScrollPane(list);
			listScroller.setPreferredSize(new Dimension(150, 30));



			combo_probe = new JComboBox();
			combo_probe.addItem("FPP");
			combo_probe.addItem("FPXP");
			combo_probe.addItem("PPP");
			combo_probe.addItem("BWPP");
			combo_probe.addItem("QPP");
			combo_probe.addItem("QPXP");
			combo_probe.addItem("F1PP");
			combo_probe.addItem("F1PA");
			combo_probe.addItem("YPP");
			combo_probe.addItem("PPC");
			combo_probe.addItem("FRC");
			combo_probe.addItem("FILE");

			combo_db = new JComboBox();
			combo_db.addItem("F9 Klarity");
			//combo_db.addItem("F3 Klarity");


			box = new JCheckBox("Bare");

			connectionPanel = new JPanel(false);
			//connectionPanel.setLayout(new GridLayout(3,3));
			connectionPanel.setLayout(new GridLayout(3,3));
			mainPanel = new JPanel(false);
			mainPanel.setLayout(new BorderLayout());
			south_pan = new JPanel();
			nord_pan = new JPanel();




			////////////////////////////////////////////////////////////////
			////////////////// Action listeners
			////////////////// Action listeners
			combo_db.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("changing db...");

					String mydb = (String)combo_db.getSelectedItem();

					if(mydb.equals("F3 Klarity"))
						dbc = klar.change_server_connection("F3");

					if(mydb.equals("F9 Klarity"))
						dbc = klar.change_server_connection("F9");

				}

			});










			/////////////////////////////////////////////////////
			button_lot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {


					String lot = (String)combo_lot.getSelectedItem();
					lot = lot.trim();
					System.out.println(lot);
					try {
						Statement stmt = dbc.createStatement();
						String SQL_stmt = "";
						SQL_stmt = "select lot_id" + " from INSP_WAFER_SUMMARY " +
								" WHERE lot_id= "  + "\'" + lot + "\'";

						System.out.println(SQL_stmt + "\n");
						ResultSet rs = stmt.executeQuery(SQL_stmt);

						int c = 0;
						while (rs.next()) {
							String l_id = rs.getString("lot_id");
							if(not_in_combo(combo_lot, c, l_id))
							{
								combo_lot.addItem(l_id);
								c++;
							}
						}
					} catch (SQLException ex2) {

						System.out.println(ex2);
					}
				} 
			});






			button_wafer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {


					String lot = (String)combo_lot.getSelectedItem();
					lot = lot.trim();
					System.out.println(lot);
					try {
						Statement stmt = dbc.createStatement();
						String SQL_stmt = "";
						SQL_stmt = "select wafer_id" + " from INSP_WAFER_SUMMARY " +
								" WHERE lot_id= "  + "\'" + lot + "\'";

						System.out.println(SQL_stmt + "\n");
						ResultSet rs = stmt.executeQuery(SQL_stmt);

						int c = 0;
						while (rs.next()) {
							String wf_id = rs.getString("wafer_id");
							if(not_in_combo(combo_wafer, c, wf_id)){
								combo_wafer.addItem(wf_id);
								c++;
							}
						}
					} catch (SQLException ex2) {

						System.out.println(ex2);
					}
				} 
			});


			button_steps.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {


					String lot = (String)combo_lot.getSelectedItem();
					lot = lot.trim();

					String wf = (String)combo_wafer.getSelectedItem();
					wf = wf.trim();


					try {
						Statement stmt = dbc.createStatement();
						String SQL_stmt = "";
						SQL_stmt = "select layer_id" + " from INSP_WAFER_SUMMARY " +
								" WHERE lot_id= "  + "\'" + lot + "\'" +
								" AND wafer_id= " + "\'" + wf + "\'";

						System.out.println(SQL_stmt + "\n");
						ResultSet rs = stmt.executeQuery(SQL_stmt);

						int c = 0;
						while (rs.next()) {
							String l_id = rs.getString("layer_id");
							listModel.addElement(l_id);
							c++;
						}
					} catch (SQLException ex2) {

						System.out.println(ex2);
					}
				} 
			});



			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					try {
						test_type = (String)combo_probe.getSelectedItem();

						String mlot = (String)combo_lot.getSelectedItem();
						mlot = mlot.trim();

						String mwf = (String)combo_wafer.getSelectedItem();
						mwf = mwf.trim();

						String steplist="";
						Object[] mysteps =list.getSelectedValues();
						for(int i=0; i< mysteps.length;i++){

							steplist = steplist + "\'" + mysteps[i] + "\'";
							if(i< mysteps.length-1)
								steplist = steplist  + ",";

						}


						Statement stmt = dbc.createStatement();

						String SQL_stmt = "";

						SQL_stmt = "select lot_id, inspection_time, wafer_key, wafer_id, defects, center_x, center_y, recipe_key, layer_id" +
								" from INSP_WAFER_SUMMARY " +
								" WHERE lot_id= "  + "\'" + mlot + "\'" +
								" AND wafer_id= " + "\'" + mwf + "\'"  +
								" AND layer_id IN ("+ steplist + ")";

						//" AND INSPECT_EQUIP_ID NOT IN ('KSPD9A0100','KSPD9A0200','KSPD9A0300','KSPD9A0400','KSPD9A0500','SPBK01')";






						System.out.println(SQL_stmt + "\n");

						System.out.println(SQL_stmt + "\n");

						ResultSet rs = stmt.executeQuery(SQL_stmt);

						int c = 0;
						while (rs.next()) {
							String lot = rs.getString("lot_id");
							it = rs.getString("inspection_time");
							wk = rs.getString("wafer_key");
							String wf_id = rs.getString("wafer_id");
							def_num = rs.getString("defects");
							cx = rs.getString("center_x");
							cy = rs.getString("center_y");
							rk = rs.getString("recipe_key");
							lid = rs.getString("layer_id");

							data[c][0] = lot;
							data[c][1] = it;
							data[c][2] = wk;
							data[c][3] = wf_id;
							data[c][4] = def_num;
							data[c][5] = cx;
							data[c][6] = cy;
							data[c][7] = lid;


							System.out.println("time = " + it);
							System.out.println(" | wk = " + wk);
							System.out.println(" | wid = " + wf_id);
							System.out.println(" | rk = " + rk);
							System.out.println(" | layer = " + lid);
							System.out.println(" | defects = " + def_num + "\n");


							c++;
						}

						dimension = c;

						rs.close();
						stmt.close();


						if(ret==1)  {
							// Popup
							JOptionPane.showMessageDialog(jd,
									"The test",
									"Test out not found",
									JOptionPane.ERROR_MESSAGE);

							// Exit
							System.exit(0);
						}  else
							jd.dispose();


					} catch (SQLException ex2) {

						System.out.println(ex2);
					}

				} 
			});





			connectionPanel.add(field1);
			connectionPanel.add(combo_lot);
			connectionPanel.add(button_lot); 

			connectionPanel.add(field3);
			connectionPanel.add(combo_wafer);
			connectionPanel.add(button_wafer); 

			connectionPanel.add(field5);      
			//connectionPanel.add(combo_steps);
			connectionPanel.add(listScroller);
			connectionPanel.add(button_steps); 

			//south_pan.add(box);
			south_pan.add(combo_probe);
			south_pan.add(button);

			JLabel dblab = new JLabel("Choose the defect DB: ");
			nord_pan.add(dblab);
			nord_pan.add(combo_db);

			mainPanel.add(connectionPanel, BorderLayout.CENTER);
			mainPanel.add(south_pan , BorderLayout.SOUTH);


			mainPanel.add(nord_pan , BorderLayout.NORTH);

			JFrame jf = new JFrame();

			jd = new JDialog(jf,"SSA", true); 
			jd.setContentPane(mainPanel);
			jd.setLocation(250,250);
			//jd.setSize(new Dimension(150,200));

			jd.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {System.exit(0);}
			});

			jd.pack();
			jd.setVisible(true);      



		}
	}





	/*************************************
	 * multi steps maps...
	 *************************************/
	class Fillwafer_multi extends JPanel implements MouseListener, MouseMotionListener
	{
		Color bg = Color.white;
		Connection con = null;
		double[][] matrix_2;
		int count;
		private String DOT="\\.";
		int xdim=0;
		String df;
		String lot;
		String wf;
		String wk;
		String it;
		String layer;
		Rectangle r; 
		int or_x, or_y, dim_x, dim_y = 0;



		int selection=0;	
		double cx;
		double cy;
		int constr_type = 0;
		BufferedImage imc2;  
		getmap_2 gm ;
		getmap_centered gm2;
		double origxy[];
		double x_off=0;
		double y_off=0;

		double xwf = 200 * fact;
		double ywf = 200 * fact;
		double die_dim[];
		double dix;
		double diy;
		int stepx;
		int stepy;
		String[][] bin_info;
		String[][] binout_info;
		Object[][] all_die_info;
		Object[][] matrix;
		String mydata[][];
		String device;
		String FPP;
		String QPP;
		String probe_test;

		public Fillwafer_multi(get_data_3 gdata)
		{     
			addMouseListener(this);
			addMouseMotionListener(this);

			constr_type = 1;

			probe_test = gdata.get_ptest();


			// TODO ... getting value from the firts wafer		
			lot = gdata.get_data_rc(0,0);
			it = gdata.get_data_rc(0,1);
			wk = gdata.get_data_rc(0,2);
			wf = gdata.get_data_rc(0,3);
			cx = Double.valueOf(gdata.get_data_rc(0,5));
			cy = Double.valueOf(gdata.get_data_rc(0,6));
			
			///////////////////////////////////////////////////

			// da verificare che non usa una unpatterned...
			die_dim = klar.get_diesize(wk, it);
			dix = die_dim[0]/1000000 * fact;
			diy = die_dim[1]/1000000 * fact;
			stepx = (int)(xwf/dix)+1;
			stepy = (int)(xwf/diy)+1;
			System.out.println("die step x-y: "+ stepx +","+stepy+ "\n");

			//////////////////////////////////////////////////////////////
			/////////////// all_defect_info ///////////////////////////////
			//////////////////////////////////////////////////////////////
			int mydim = gdata.get_dim();
			System.out.println("MMAP Dimension is: " + mydim + "\n");
			mydata = gdata.get_all_data();
			int tot_defects=0;

			for(int i = 0; i < mydim; i++) 
				tot_defects = tot_defects + Integer.parseInt(mydata[i][4]);

			matrix = new Object[tot_defects][16];

			int seq = 0; 
			for(int i = 0; i < mydim; i++) {
				double[][] tag = klar.get_db_data(mydata[i][1], mydata[i][2], mydata[i][4], 
						Double.valueOf(mydata[i][5]).doubleValue(), Double.valueOf(mydata[i][6]).doubleValue(),
						die_dim[0],die_dim[1]);

				int xdim = Integer.parseInt(mydata[i][4]);	
				for(int j=0; j< xdim; j++){
					matrix[seq+j][0] = tag[j][0]; //  x in wafer 
					matrix[seq+j][1] = tag[j][1]; //  y in wafer trasformed x java
					matrix[seq+j][2] = tag[j][2]; // adder
					matrix[seq+j][3] = tag[j][3]; // x indie
					matrix[seq+j][4] = tag[j][4]; // y indie
					matrix[seq+j][5] = tag[j][5]; // die i position
					matrix[seq+j][6] = tag[j][6]; // die j position
					matrix[seq+j][7] = tag[j][7]; // defect id
					matrix[seq+j][8] = mydata[i][7]; // layer id
					matrix[seq+j][9] = tag[j][8]; // defect class
					matrix[seq+j][10] = 0; // matching flag
					matrix[seq+j][11] = tag[j][9]/1000; // defect size (um)
					matrix[seq+j][12] = tag[j][10]/1000000; // defect area
					matrix[seq+j][13] = tag[j][11]; // image count
					matrix[seq+j][14] =  mydata[i][1]; // it
					matrix[seq+j][15] =  mydata[i][2]; // wk

				}
				seq = seq+xdim;
			}
			/////////////////////////////////////////////////////////////////////


			////////////////////////////////////////////////////
			// fill the die matrix die_info with probe bins
			/////////////////////////////////////////////////////
			String newlot = lot.replace("_", ".");

			//device = probe.getpartfromlot(probe_db_con, newlot);
			//device = device.substring(0,4);

			device = klar.get_device(lot);
			System.out.println("Current device analysis: "+ device);

			bin_info = new String[stepy][stepx]; // used for paint
			binout_info = new String[stepy][stepx]; // used for paint

			all_die_info = new Object[stepx*stepy][9];

			// Create bin data with Y3 (se si sceglie FILE non rigenera aaa.csv)
			if(!probe_test.equals("FILE"))
				create_bin_file(lot, probe_test);
			////////////////////////////////

			get_probe_data(stepx, stepy, lot, wf, device);
			//////////////////////////////////////

			//matrix 2 is for paint
			matrix_2 = new double[matrix.length][4];
			System.out.println("Matrix lenght: " + matrix.length);

			for(int l=0; l < matrix.length; l++){   
				matrix_2[l][0] = (double)matrix[l][0]/1000000 * fact;
				matrix_2[l][1] = (double)matrix[l][1]/1000000  * fact;
				matrix_2[l][2] = (double)matrix[l][2];
				matrix_2[l][3] = 0;           //selection
			}


			gm2 = new getmap_centered(matrix_2, fact,  0.0, 0.0);
			imc2 = gm2.getImg();

			//
			//get_sem("51","C1EA_FIELD");

		}
		////////////////////////////////////////////////////////////



		public String get_sem(String def_id, String layer)
		{
			String img;

			String time="";
			String w_key="";

			//System.out.println(def_id+"  "+layer);

			for(int i = 0; i < mydata.length-1; i++){
				//System.out.println(mydata[i][7]);
				if(mydata[i][7].equals(layer)) {
					time = 	mydata[i][1];
					w_key = mydata[i][2];
					break;					
				}
			}

			img = klar.get_img_name(w_key, time, def_id);
			//System.out.println("image: " + img);
			ftp my_ftp = new ftp(img, DB);

			return(img);
		}







		//////////////////////////////////////////////////////////////
		public void get_probe_data(int stepx, int stepy, String lot, String wf, String dev)
		{
			String newlot = lot.replace("_", ".");
			//Object[][] provaoj = new Object[stepx*stepy][5];
			System.out.println("Gathering bin info...");

			int m=0;
			for(int i=0; i< stepy; i++){
				for(int j=0; j< stepx; j++){
					String[] newij = getijtrasform(j,i,dev);

					//FROM DB...
					//bin_info[i][j] = probe.getfailbin(probe_db_con, newlot, wf, newij[0], newij[1]);
					//binout_info[i][j] = probe.getfailbinout(probe_db_con, newlot, wf, newij[0], newij[1]);

					//from file..
					String[] mybins = getfailbin_file(newlot, wf, newij[0], newij[1]);
					bin_info[i][j] = mybins[0];
					binout_info[i][j] = mybins[1];

					///////
					all_die_info[m][0] = newij[0];  // probe i
					all_die_info[m][1] = newij[1];  // probe j
					all_die_info[m][2] = i;     // klarity i
					all_die_info[m][3] = j;     // klarity j
					all_die_info[m][4] = bin_info[i][j]; //bin
					all_die_info[m][5] = 0; //used x matched unmatched
					all_die_info[m][6] = binout_info[i][j]; //bin
					all_die_info[m][7] = "null"; //path to img
					all_die_info[m][8] = 0; //for selection management

					System.out.println(all_die_info[m][0] + ","+ all_die_info[m][1]+ ","+all_die_info[m][2]+ ","+all_die_info[m][3]+ ","+all_die_info[m][4]);

					m++;
				}
			}

		}



		public Object get_dieinf(int a, int b)
		{

			return all_die_info[a][b];

		}

		public int get_dieinf_dim()
		{
			return all_die_info.length;
		}


		public void create_bin_file(String lot, String test){
			String optimus = "C:/MTAPPS/IS_Frontend/Yield3/GlobalQuery.exe ";
			String BIN_DIR = "bin2/";
			String[] str = {optimus,BIN_DIR+"new.gql", BIN_DIR+"aaa.csv"};

			write_new_gql(lot, test);

			try {
				Process p = Runtime.getRuntime().exec(str);
				System.out.println("Executing GQ, pls wait..." + "\n");
				p.waitFor();
				System.out.println("GQ Done.");
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}



		public void write_new_gql(String lot, String test){
			String BIN_DIR = "bin2/";
			String p_file= BIN_DIR +"mygq.gql";
			String gq_file= BIN_DIR +"new.gql";
			String input;
			String test_code="";
			String newlot = lot.replace("_", ".");
			String lot_code = "List="+newlot;

			//List=7554199.009

			/*			
			[ITEMS]
			AttrItem=ProbeDiePREGLASS FUNCT PRODError Bin
			AttrItem=ProbeDiePREGLASS FUNCT PRODFail Bin
			TestItem=ProbeDiePREGLASS FUNCT PRODFAILING_BINOUTS
			 */
			switch(test){
			case "FPP":
				test_code = "FINAL FUNCT PROD";
				break;


			case "FPXP":
				test_code = "FINAL FUNCT EXTERNAL PROD";
				break;

			case "PPP":
				test_code = "PREGLASS FUNCT PROD";
				break;	

			case "BWPP":
				test_code = "BANDW PROBE FUNCT PROD";
				break;	



			case "F1PP":
				test_code = "PROBEPASS1 FUNCT PROD";
				break;	

			case "F1PA":
				test_code = "PROBEPASS1 FUNCT ABORT";
				break;	

			case "FRC":
				test_code = "FINAL PARAM CORR";
				break;	



			case "PPC":
				test_code = "PREGLASS FUNCT CORR";
				break;	



			case "QPP":
				test_code = "COLDDIESORT FUNCT PROD";
				break;


			case "QPXP":
				test_code = "COLDDIESORT FUNCT EXTERNAL PROD";
				break;


			case "YPP":
				test_code = "HOTDIESORT FUNCT PROD";
				break;

			case "FILE":
				test_code = "FILE";
				break;

			default:
				System.out.println("Ucknown test type...");
				break;

			}


			String a1 = "AttrItem=ProbeDie"+ test_code + "Error Bin";	
			String a2 = "AttrItem=ProbeDie"+ test_code + "Fail Bin";
			String a3 = "TestItem=ProbeDie"+ test_code +"FAILING_BINOUTS";


			try{
				File logfile = new File(gq_file);
				boolean fc  = logfile.createNewFile();
				//FileWriter log = new FileWriter(logfile);

				BufferedReader br = new BufferedReader(new FileReader(p_file));
				BufferedWriter brw = new BufferedWriter(new FileWriter(logfile));
				int i = 0;

				while(( (input = br.readLine()) != null))
				{

					brw.write(input);
					brw.newLine(); 
					if(input.equals("[ITEMS]")){
						brw.write(a1);
						brw.newLine(); 	
						brw.write(a2);
						brw.newLine(); 	
						brw.write(a3);
						brw.newLine(); 	
					}

					if(input.equals("Type=LOT")){
						brw.write(lot_code);
						brw.newLine(); 	
					}


					brw.flush();
					i++;
				}
				br.close();
				brw.close();
				//log.close();

			} catch (Exception ex) {
				System.out.println(ex)	;
			}

		}


		String[] getfailbin_file(String lot, String wf, String pi, String pj){
			String REGEX=",";
			Pattern pattern = Pattern.compile(REGEX);
			String BIN_DIR = "bin2/";
			String p_file= BIN_DIR +"aaa.csv";
			String[] ret = new String[2];
			ret[0] = ret[1] = "null";
			String input;
			String y3wf = lot.substring(3,7)+ "-" + wf;
			//System.out.println(y3wf);
			int ipi = Integer.parseInt(pi);
			int ipj = Integer.parseInt(pj);

			try{
				BufferedReader br = new BufferedReader(new FileReader(p_file));
				int i = 0;
				while(( (input = br.readLine()) != null))
				{
					if(i>0){
						input = input.replace("\"", "");
						String[] name_split = pattern.split(input);
						int new_i = Integer.parseInt(name_split[2]);
						int new_j = Integer.parseInt(name_split[3]);

						if(name_split[1].equals(y3wf)){	
							if(new_i == ipi && new_j == ipj){	
								if(name_split[5].equals("."))
									ret[0] = "..";
								else{
									ret[0] = name_split[5] + ":" + name_split[4];
									ret[1] = name_split[6];
								}
							}
						}
					}
					i++;
				}
				br.close();

			} catch (Exception e3) {
				System.out.println(e3);
			}

			return ret;
		}





		//////////////// i,j trasf x device ///////////////
		String[] getijtrasform(int j, int i, String dev)
		{
			String[] out = new String[2];	
			switch(device){
			case "C1CC":
				//row
				out[0] = Integer.toString(j-5);
				//col
				out[1] = Integer.toString(i-1);

				break;

			case "C1EA":
				//row
				out[0] = Integer.toString(j-4);
				//col
				out[1] = Integer.toString(i-1);
				break;


			case "C2GA":
				//row
				out[0] = Integer.toString(j-5);
				//col
				out[1] = Integer.toString(i-1);
				break;	

			case "C2GB":
				//col
				out[0] = Integer.toString(j-4);
				//row
				out[1] = Integer.toString(i-1);
				break;	

			case "C24C":
				//col
				out[0] = Integer.toString(i-10);
				//row
				out[1] = Integer.toString(j-2);
				break;	

			case "C24A":
				//col
				out[0] = Integer.toString(i-11);

				//row
				//out[1] = Integer.toString(j-2);
				out[1] = Integer.toString(24-j);
				break;		


			case "C25C":
				//col
				out[0] = Integer.toString(j-8);
				//row
				out[1] = Integer.toString(i-2);
				break;					

			case "C24D":
				//col
				//	out[0] = Integer.toString(j-4);
				out[0] = Integer.toString(j-12);
				//row
				out[1] = Integer.toString(i-1);
				break;	


			case "K22B":
				//row
				out[0] = Integer.toString(j-13);
				//col
				out[1] = Integer.toString(i-1);

				break;				

			case "C2EB":
				//row
				out[0] = Integer.toString(j-7);
				//col
				out[1] = Integer.toString(i-2);

				break;	

			case "C4BX":
				//row
				out[0] = Integer.toString(j-12);
				//col
				out[1] = Integer.toString(i-1);
				break;

			case "C4BA":
				//row
				out[0] = Integer.toString(20-i);
				//out[0] = Integer.toString(-(i-20));
				//col
				out[1] = Integer.toString(j-1);
				break;

			case "C4BD":
				//row
				out[0] = Integer.toString(17-j);
				//col
				out[1] = Integer.toString(28-i);
				break;




			case "K45A":
				//row
				out[0] = Integer.toString(j-17);
				//col
				out[1] = Integer.toString(i-2);
				break;


			case "C48B":
				//row
				out[0] = Integer.toString(j-12);
				//col
				out[1] = Integer.toString(i-2);
				break;

			case "C26E":
				//row
				out[0] = Integer.toString(18-i);

				out[1] = Integer.toString(j-1);
				break;			

			case "PVKA":
				out[0] = Integer.toString(17-j);
				//col
				out[1] = Integer.toString(35-i);
				break;

			case "PT9B":
				out[0] = Integer.toString(36-j);
				//col
				out[1] = Integer.toString(34-i);
				break;			


			case "PT9A":
				//out[0] = Integer.toString(36-j);
				out[0] = Integer.toString(26-j);
				//col
				//out[1] = Integer.toString(34-i);
				out[1] = Integer.toString(35-i);
				break;	


			case "PT4A":
				//out[0] = Integer.toString(36-j);
				out[0] = Integer.toString(36-j);
				//col
				//out[1] = Integer.toString(34-i);
				out[1] = Integer.toString(34-i);
				break;	


			case "PT4B":
				//out[0] = Integer.toString(36-j);
				//out[0] = Integer.toString(41-j);
				out[0] = Integer.toString(40-j);
				//col
				//out[1] = Integer.toString(34-i);
				out[1] = Integer.toString(47-i);
				break;	


			case "PL4A":
				out[0] = Integer.toString(37-j);

				//row
				out[1] = Integer.toString(48-i);
				break;		

				//J1Z3

			case "J1Z3":
				//horiz move
				out[0] = Integer.toString(20-j);

				//vert move
				out[1] = Integer.toString(27-i);
				break;		

			case "J2Z2":
				//horiz move 20
				out[0] = Integer.toString(35-j);

				//vert move
				//out[1] = Integer.toString(27-i);
				out[1] = Integer.toString(60-i);
				break;					

			default:
				out[0] = Integer.toString(j);
				//col
				out[1] = Integer.toString(i);

				System.out.println("This device is not configured...");
				break;

			}



			return out;
		}



		public void defect_class_report()
		{
			//			defect_class(matrix, all_die_info, lot, wf, device);
			defect_class2(matrix, all_die_info, lot, wf, device, it, wk);
		}

		public void defect_density_report()
		{
			defect_density(matrix, all_die_info, lot, wf, device);	
		}


		//////////////////////////////////////////////////////////////////////////////////////		
		public void defect_class(Object[][] defects_inf, Object[][] die_inf, String lot, String wf, String dev)
		{
			int diex;
			int diey;
			int dx;
			int dy;
			String  px="none";
			String  py="none";
			String fail = "null";

			System.out.println("--------------------------------------------------");
			System.out.println("----------- DEFECT CLASS REPORT ------------------");
			System.out.println("--------------------------------------------------");

			System.out.println("die_i" + "," + "die_j" + "," + "def_id"+","+"def_class"+","+"step"+","+"bin"+","+"match flag"+"," + 
					"region" + ","+"size (um)"+","+ "Area"+","+ "Added");

			for(int m=0; m< defects_inf.length; m++){
				diex = (int)(double)defects_inf[m][5];
				diey =  (int)(double)(defects_inf[m][6]);

				if(dev.equals("C4BX") && klar.need_flip(dev, (String)defects_inf[m][8])) 
					diex = 29-diex;

				if(dev.equals("C4BD") && klar.need_flip(dev, (String)defects_inf[m][8])) 
					diex = 31-diex;


				for(int n=0; n< die_inf.length; n++){
					dx = ((int)die_inf[n][2]); // klar die x
					dy = ((int)die_inf[n][3]); // klar die y

					if(dx == diey && dy == diex){ 

						fail = (String)die_inf[n][4];
						px = (String)die_inf[n][0];
						py = (String)die_inf[n][1];
						break;
					}

				}


				String reg = klar.get_idr(dev, (double)defects_inf[m][3], (double)defects_inf[m][4]);


				String myclass = Double.toString((double)defects_inf[m][9]);
				System.out.println(px +"," + py+ "," + defects_inf[m][7]+","+ klar.get_class(myclass)+","+
						defects_inf[m][8]+","+fail+","+defects_inf[m][10]+","+reg + "," + defects_inf[m][11] + ","+ defects_inf[m][12] + ","+ defects_inf[m][2]);
				//System.out.println(defects_inf[m][7]+","+defects_inf[m][9]+","+defects_inf[m][8]+","+fail+","+defects_inf[m][10]);

			}
		}





		//////////////////////////////////////////////////////////////////////////////////////		
		public void defect_class2(Object[][] defects_inf, Object[][] die_inf, String lot, String wf, String dev, String it, String wk)
		{
			String report = lot+wf+"YE_match_report.html";
			try {
				FileWriter htmllog = new FileWriter(new File(report));

				int diex;
				int diey;
				int dx;
				int dy;
				String  px="none";
				String  py="none";
				String fail = "null";

				System.out.println("--------------------------------------------------");
				System.out.println("----------- DEFECT CLASS REPORT ------------------");
				System.out.println("--------------------------------------------------");

				htmllog.write("<H1 style=\"text-align:center;\"> "+  " PASSING DIE REPORT </H1>");
				htmllog.write("<H1 style=\"text-align:center;\"> "+ dev+"_"+lot+":"+wf  + " </H1>");
				htmllog.write("<table border=\"1\" style=\"width:90%\"> ");

				System.out.println("die_i" + "," + "die_j" + "," + "def_id"+","+"def_class"+","+"step"+","+"bin"+","+"match flag"+"," + 
						"region" + ","+"size (um)"+","+ "Area"+","+ "Added");

				//htmllog.write("<tr> ");
				htmllog.write("<tr bgcolor=#ADD8E6 > ");
				htmllog.write("<td>  " + "die_i" + "</td>");
				htmllog.write("<td>  " +  "die_j" + "</td>");
				htmllog.write("<td>  " + "die_x" + "</td>");
				htmllog.write("<td>  " +  "die_y" + "</td>");
				htmllog.write("<td>  " + "def_id" + "</td>");
				
				htmllog.write("<td>  " + "def_class" + "</td>");
				
				htmllog.write("<td>  " + "step" + "</td>");
				htmllog.write("<td>  " + "bin" + "</td>");
//				htmllog.write("<td>  " + "PIX match flag" + "</td>");
				htmllog.write("<td>  " + "Die Region" + "</td>");
//				htmllog.write("<td>  " + "size (um)" + "</td>");
//				htmllog.write("<td>  " + "Area" + "</td>");
//				htmllog.write("<td>  " + "Added" + "</td>");
				htmllog.write("<td>  " + "SEM pic" + "</td>");
				htmllog.write("</tr> ");


				for(int m=0; m< defects_inf.length; m++){
					diex = (int)(double)defects_inf[m][5];
					diey =  (int)(double)(defects_inf[m][6]);

					if(dev.equals("C4BX") && klar.need_flip(dev, (String)defects_inf[m][8])) 
						diex = 29-diex;

					if(dev.equals("C4BD") && klar.need_flip(dev, (String)defects_inf[m][8])) 
						diex = 31-diex;


					for(int n=0; n< die_inf.length; n++){
						dx = ((int)die_inf[n][2]); // klar die x
						dy = ((int)die_inf[n][3]); // klar die y

						if(dx == diey && dy == diex){ 

							fail = (String)die_inf[n][4];
							px = (String)die_inf[n][0];
							py = (String)die_inf[n][1];
							break;
						}

					}


					String reg = klar.get_idr(dev, (double)defects_inf[m][3], (double)defects_inf[m][4]);


					String myclass = Double.toString((double)defects_inf[m][9]);
					System.out.println(px +"," + py+ "," + defects_inf[m][7]+","+ klar.get_class(myclass)+","+
							defects_inf[m][8]+","+fail+","+defects_inf[m][10]+","+reg + "," + defects_inf[m][11] + ","+ defects_inf[m][12] + ","+ defects_inf[m][2]);
		

					// HTML report

					if( (fail.equals("..") || fail.equals("2:2")) &&  (double)defects_inf[m][13]>0 && !klar.get_class(myclass).equals("No visible")) 
					{
						String imgname = "none";
						BufferedImage semimg = klar.get_sem2(Double.toString((double)defects_inf[m][7]),(String)defects_inf[m][8], (String)defects_inf[m][14], (String)defects_inf[m][15]);
						if((double)defects_inf[m][13]>0) {
							imgname = "reportimg/"+lot+wf+(String)defects_inf[m][8]+Double.toString((double)defects_inf[m][7])+".jpg";
							ImageIO.write(semimg, "jpeg", new File(imgname));
						}
						//				time e sono in mydata e vanno aggiunti a mtrix 

						htmllog.write("<tr> ");
						htmllog.write("<td>  " + px + "</td>");
						htmllog.write("<td>  " + py + "</td>");
						htmllog.write("<td>  " + defects_inf[m][3] + "</td>"); // x indie
						htmllog.write("<td>  " + defects_inf[m][4] + "</td>"); // y indie
						htmllog.write("<td>  " + defects_inf[m][7] + "</td>");
						htmllog.write("<td>  " + klar.get_class(myclass) + "</td>");
						htmllog.write("<td>  " + defects_inf[m][8] + "</td>");
						htmllog.write("<td>  " + fail + "</td>");
	//					htmllog.write("<td>  " + defects_inf[m][10] + "</td>");
						htmllog.write("<td>  " + reg + "</td>");
	//					htmllog.write("<td>  " + defects_inf[m][11] + "</td>");
	//					htmllog.write("<td>  " + defects_inf[m][12] + "</td>");
	//					htmllog.write("<td>  " + defects_inf[m][2] + "</td>");
						htmllog.write(" <td colspan = 4><img src="+ imgname + " height=200 width=200></img></td>");
						htmllog.write("</tr> ");
					}

				}
				htmllog.write("</table> ");
				htmllog.flush();
				htmllog.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}







		public void defect_density(Object[][] defects_inf, Object[][] die_inf, String lot, String wf, String dev){
			System.out.println("Density report...");
			for(int n=0; n< die_inf.length; n++){
				int d_count = 0;
				int dx = ((int)die_inf[n][2]); // klar die x
				int dy = ((int)die_inf[n][3]); // klar die y
				for(int m=0; m< defects_inf.length; m++){
					int diex = (int)(double)defects_inf[m][5];
					int diey =  (int)(double)(defects_inf[m][6]);
					if(dx == diey && dy == diex){ 
						d_count++;
					}

				}

				System.out.println(die_inf[n][0]+","+ die_inf[n][1]+","+die_inf[n][4]+","+d_count);

			}
		}






		////////////////////////////////////
		public void match(String fpp, String qpp, Fillwafer_multi fm){
			FPP=fpp;
			QPP=qpp;
			ROW_COUNT = 0;

			System.out.println("Starting matching analysis...");
			System.out.println(get_cur_time());
			defect_to_pixel dtp = new defect_to_pixel(matrix, all_die_info, lot, wf, device, FPP, QPP, probe_test, fm);
			new Thread(dtp).start();



			//			System.out.println(get_cur_time());

		}


		/////////////////////////////////////////////
		/////////// Final report
		public void final_report(){
			double die_yield_count=0;
			double die_fail_count=0;
			double die_justified_count=0;
			double die_missed_count=0;

			for(int m=0; m< all_die_info.length; m++){


				if(all_die_info[m][4] == (".."))
					die_yield_count++;
				else if (all_die_info[m][4] != ("..") && all_die_info[m][4] != ("null"))
					die_fail_count++;

				if((int)all_die_info[m][5] == 1)
					die_justified_count++;
				if((int)all_die_info[m][5] == 2)
					die_missed_count++;



			}


			double Yield = die_yield_count/(die_yield_count+die_fail_count);
			double Yield_loss = 1 - Yield;
			double Justified_YL = die_justified_count/(die_yield_count+die_fail_count);
			double missed_YL = die_missed_count/(die_yield_count+die_fail_count);

			System.out.println("--------------------------------------------------");
			System.out.println(" FINAL REPORT ");
			System.out.println("--------------------------------------------------");
			//System.out.println(die_yield_count + " " +die_fail_count+ " " +die_justified_count);
			System.out.println("Wafer Yield = "  + Yield*100 + "%" );
			//textArea4.append("Wafer Yield = "  + Yield*100 + "%"+"\n" );
			System.out.println("Wafer Yield Loss = "  + Yield_loss*100 + "%" );
			//textArea4.append("Wafer Yield Loss = "  + Yield_loss*100 + "%" +"\n");
			System.out.println("Wafer Justified YL = "  + Justified_YL*100 + "%" );
			//textArea4.append("Wafer Justified YL = "  + Justified_YL*100 + "%" +"\n");
			System.out.println("Wafer Justified dies = "  + die_justified_count);
			//textArea4.append("Wafer Justified dies = "  + die_justified_count +"\n");
			System.out.println("Potentially missed YL = "  + missed_YL*100 + "%" );
			//textArea4.append("Potentially missed YL = "  + missed_YL*100 + "%" +"\n");
			System.out.println("Pct of YL caught inline= "  + (Justified_YL/Yield_loss)*100 + "%" );
			//textArea4.append("Pct of YL caught inline= "  + (Justified_YL/Yield_loss)*100 + "%" +"\n");

			repaint();
		}




		public int is_justified(String i, String j){
			int ij = 0;

			for(int m=0; m< all_die_info.length; m++){

				if(all_die_info[m][0].equals(i) && all_die_info[m][1].equals(j)){
					if((int)all_die_info[m][5] == 1)
						ij = 1;
					else if  ((int)all_die_info[m][5] == 2)
						ij = 2;
					else if  ((int)all_die_info[m][5] == 3)
						ij = 3;
				}
			}
			return ij;			
		}


		public void  set_selection(String i, String j){

			for(int m=0; m< all_die_info.length; m++){

				if(all_die_info[m][0].equals(i) && all_die_info[m][1].equals(j))
					all_die_info[m][8] = 1;
				else
					all_die_info[m][8] = 0;

			}				
		}



		public int is_tab_selected(String i, String j){
			int ij = 0;

			for(int m=0; m< all_die_info.length; m++){

				if(all_die_info[m][0].equals(i) && all_die_info[m][1].equals(j)){
					return((int)all_die_info[m][8]);

				}
			}
			return ij;			
		}



		//////////////////////////////////////////////////
		public void mouseDragged(MouseEvent e){

			dim_x = e.getX()-or_x;
			dim_y = e.getY()-or_y;


			//System.out.println("Mouse drag...." + "\n");
			repaint();

		}



		public void mousePressed(MouseEvent e){

			double sx, sxini = (100000000 - cx)/1000000 *fact;
			double sy = ywf - (100000000 - cy)/1000000 *fact;
			or_x  = e.getX(); 
			or_y =  e.getY();
			//System.out.println(or_x +", " + or_y);


			for(int i=0; i< stepy; i++){
				sx = sxini;
				for(int j=0; j< stepx; j++){
					//System.out.println("click");	
					Rectangle2D.Double rg = new Rectangle2D.Double(sx ,sy-diy, dix, diy);
					if(rg.contains(or_x,or_y)){

						for(int n=0; n< all_die_info.length; n++){
							int dx = ((int)all_die_info[n][2]); // klar die x
							int dy = ((int)all_die_info[n][3]); // klar die y

							if(dx == i && dy == j){

								if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) { 
									textforbinout.setText("bin to end: "+ (String)all_die_info[n][6]
											+" || Probe die: " + all_die_info[n][0]+","+ all_die_info[n][1]
													+" || Klarity die: " + dx+","+ dy);
								}
								else if(e.getButton() == java.awt.event.MouseEvent.BUTTON3)
								{
									System.out.println(">>>> List of defects in die: " + dx +"," + dy + " <<<<");
									// List all defect inside the die...
									for(int m=0; m< matrix.length; m++){
										int diex = (int)(double)matrix[m][5];
										int diey =  (int)(double)(matrix[m][6]);
										if(dx == diey && dy == diex){


											String reg = klar.get_idr(device, (double)matrix[m][3], (double)matrix[m][4]);
											String myclass = Double.toString((double)matrix[m][9]);
											System.out.println(dx +"," + dy+ "," + matrix[m][7]+","+ klar.get_class(myclass)+","+matrix[m][8]+","+matrix[m][10] +","+ reg);


										}
									}
								}

								break;
							}

						}

					}
					sx = sx + dix; 
				}
				sy = sy - diy;
			}
		}



		public int[] get_ij_x_unpattern(double x, double y){
			double sx, sxini = (100000000 - cx)/1000000 *fact;
			double sy = ywf - (100000000 - cy)/1000000 *fact;

			int[] coord = new int[2];
			coord[0] = 1;
			coord[1] = 1;

			for(int i=0; i< stepy; i++){
				sx = sxini;
				for(int j=0; j< stepx; j++){
					//System.out.println("click");	
					Rectangle2D.Double rg = new Rectangle2D.Double(sx ,sy-diy, dix, diy);
					if(rg.contains(x,y)){
						coord[0] = j;
						coord[1] = i;
						System.out.println("Traforming ij: " +i+","+j);
						return coord;
					}
					sx = sx + dix; 
				}
				sy = sy - diy;
			}


			return coord;
		}








		// Handles the event of a user releasing the mouse button.
		public void mouseReleased(MouseEvent e){
			repaint();
		}


		public void mouseMoved(MouseEvent e){

		}


		public void mouseExited(MouseEvent e){}
		public void mouseEntered(MouseEvent e){

		}
		public void mouseClicked(MouseEvent e){}


		//////////////////////////////////////////////////////////////
		////// PAINT the WAFER MAP ///////////////////////////////////
		//////////////////////////////////////////////////////////////
		public void paintComponent(Graphics g) {
			super.paintComponent(g); 

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     

			Color fg3D = Color.white;
			g2.setBackground(Color.white);
			g2.drawImage(imc2, 0, 0, (int)xwf, (int)ywf, null);

			r = new Rectangle(or_x, or_y, dim_x, dim_y);
			g2.draw(r);

			//grid startig point //////////////////////////
			double sx, sxini = (100000000 - cx)/1000000 *fact;
			double sy = ywf - (100000000 - cy)/1000000 *fact;
			//System.out.println("sx,sy="+sxini+ "-" + sy);
			/////////////////////////////////////////////
			//Color myblue = new Color(25, 0, 0, 20);
			Color mygreen = new Color(0, 255, 0, 100);
			Color myred = new Color(255, 0, 0, 100);
			Color myblu = new Color(0, 0, 255, 100);


			//paint the grid  
			for(int i=0; i< stepy; i++){
				sx = sxini;
				for(int j=0; j< stepx; j++){
					g2.setPaint(Color.gray);  	
					Rectangle2D.Double rg = new Rectangle2D.Double(sx ,sy-diy, dix, diy);
					//g2.drawString(j+","+i, (float)sx, (float)(sy-diy/2));
					String[] newij = getijtrasform(j,i,device);
					//g2.drawString(newij[0]+","+newij[1], (float)sx, (float)(sy-diy/4));

					String mybin = bin_info[i][j];

					g2.setPaint(Color.black);
					Font big = new Font("SansSerif", Font.BOLD, 9);
					g2.setFont(big);
					if(mybin != "null"){
						if(mybin.equals(".."))
							mybin=" ";
						g2.drawString(mybin, (float)(sx), (float)(sy-diy/2));
						//g2.drawString(mybin, (float)(sx+dix/2), (float)(sy-diy/2));
					}

					//g2.setPaint(mygreen);
					if(is_justified(newij[0], newij[1])==1){
						g2.setPaint(mygreen);
						g2.fill(rg);
					}
					else if(is_justified(newij[0], newij[1])==2){
						g2.setPaint(myred);
						g2.fill(rg);
					}
					else if(is_justified(newij[0], newij[1])==3){
						g2.setPaint(myblu);
						g2.fill(rg);
					}


					double thickness = 4;
					double normal = 1;
					//Stroke oldStroke = g2.getStroke();

					if(is_tab_selected(newij[0], newij[1]) == 1){
						g2.setStroke(new BasicStroke((float) thickness));
						g2.setPaint(Color.yellow);

					}
					else
					{
						g2.setStroke(new BasicStroke((float) normal));
						g2.setPaint(Color.gray);
					}


					if(mybin != "null")
						g2.draw(rg);

					sx = sx + dix; 
				}
				sy = sy - diy;
			}


			if(constr_type == 1) {
				g2.setPaint(Color.red);          
				g2.drawString("Lot: " + lot, 0, 10);
				g2.drawString("wafer: " + wf, 0 ,20);
				//g2.drawString("Step: " + layer, 0, (float)ywf+30);
			}

		} // end of paint	





	} // end of FillWafer class




	// All writes to this print stream are copied to two print streams
	public class TeeStream extends PrintStream {
		PrintStream out;
		public TeeStream(PrintStream out1, PrintStream out2) {
			super(out1);
			this.out = out2;
		}
		public void write(byte buf[], int off, int len) {
			try {
				super.write(buf, off, len);
				out.write(buf, off, len);
			} catch (Exception e) {
			}
		}
		public void flush() {
			super.flush();
			out.flush();
		}
	}



	/////////////////////////////////////////////////////////
	// Matching defects with png pixel
	////////////////////////////////////////////////////////
	class defect_to_pixel implements Runnable
	{
		String FPP;
		String QPP;
		Object[][] defects_inf;
		Object[][] die_inf;
		String lot;
		String wf; 
		String dev;
		String t_type;
		boolean finish = false;
		Fillwafer_multi fm2;
		/*
		matrix[seq+j][0] = tag[j][0]; //  x in wafer 
		matrix[seq+j][1] = tag[j][1]; //  y in wafer trasformed x java
		matrix[seq+j][2] = tag[j][2]; // adder
		matrix[seq+j][3] = tag[j][3]; // x indie
		matrix[seq+j][4] = tag[j][4]; // y indie
		matrix[seq+j][5] = tag[j][5]; // die i position
		matrix[seq+j][6] = tag[j][6]; // die j position
		matrix[seq+j][7] = tag[j][7]; // defect id
		matrix[seq+j][8] = mydata[i][7]; // layer id
		[10] matching flag
		all_die_info[m][0] = newij[0];  // probe i
		all_die_info[m][1] = newij[1];  // probe j
		all_die_info[m][2] = i;     // klarity i
		all_die_info[m][3] = j;     // klarity j
		all_die_info[m][4] = bin_info[i][j]; //bin
		all_die_info[m][5] = 0; //used x matched unmatched

		all_failures[fail_num][0] = "COL";
		all_failures[fail_num][1] = i;
		all_failures[fail_num][2] = 0;
		all_failures[fail_num][3] = get_fail_img("col_"+Integer.toString(i) ,i, 0, 1, rownum);
		 */	   

		//costruttore
		public defect_to_pixel(Object[][] defects_, Object[][] die_, String lot_, String wf_, String dev_, String FPP_dir, String QPP_dir, String t_, Fillwafer_multi fm)
		{
			// System.out.println("in defect to pix...");
			FPP_dir = FPP_dir.replace("\\", "/") + "/";
			QPP_dir = QPP_dir.replace("\\", "/") + "/";		
			FPP = FPP_dir;
			QPP = QPP_dir;
			System.out.println(FPP);
			System.out.println(QPP);

			defects_inf = defects_;
			die_inf = die_;
			lot = lot_;
			wf = wf_;
			dev = dev_;
			t_type =  t_;
			fm2 = fm;
		}



		public void run()
		{
			LoadImageApp2 pixel_fails;
			Object[][] fails_pix;
			int x;
			int y;
			int good_x=0;
			int good_y=0;

			int k_x;
			int k_y;
			String[] types;
			String bin_type;
			String test_type;
			String f_img;
			String r_img;

			//get good reference die
			for(int j=0; j< die_inf.length; j++){
				if(die_inf[j][4].equals("..")) 
				{
					good_x = Integer.parseInt((String)die_inf[j][0]); // probe die x
					good_y = Integer.parseInt((String)die_inf[j][1]); //probe die y
					break;
				}

			}




			for(int j=0; j< die_inf.length; j++){
				//if(die_inf[j][4] != ("..") && die_inf[j][4] != ("null")) 
				if(die_inf[j][4] != ("null"))
				{
					x = Integer.parseInt((String)die_inf[j][0]); // probe die x
					y = Integer.parseInt((String)die_inf[j][1]); //probe die y
					k_x = ((int)die_inf[j][2]); // klar die x
					k_y = ((int)die_inf[j][3]); // klar die y

					System.out.println("***************************************************");
					System.out.println(die_inf[j][4]+" for die " + die_inf[j][0]+","+die_inf[j][1]);
					System.out.println("***************************************************");


					// types also contain THR
					types = find_bin_type(dev, (String)die_inf[j][4]);

					System.out.println("test type: "+types[0]);  // qui c'e' QPP o FPP
					System.out.println("bin type: "+types[1]);  // qui c'e' bin type Bin_D,...


					bin_type = types[1];
					test_type = types[0];				
					double sens = Double.valueOf(types[2]).doubleValue();
					double sens2 = Double.valueOf(types[3]).doubleValue();
					String strategy = types[4];



					if(bin_type != "null") {

						int clust_num = Integer.parseInt(types[5]);
						f_img = get_img_file(x, y, bin_type, test_type);
						r_img = get_img_file(good_x, good_y, bin_type, test_type);


						// get fails from png image...
						if(f_img.equals("null"))
							System.out.println("No image found for: " +(String)die_inf[j][4]);
						else{	 
							die_inf[j][7] = f_img;
							//System.out.println("f_img: " +(String)die_inf[j][7]);
							pixel_fails = new LoadImageApp2(sens,f_img, r_img, dev, bin_type, (String)die_inf[j][4], t_type, (String)die_inf[j][6],strategy,clust_num);
							fails_pix = pixel_fails.get_all_failures();

							if(fails_pix.length == 0){ // se non trova nulla spinge...
								pixel_fails = new LoadImageApp2(sens2,f_img, r_img, dev, bin_type, (String)die_inf[j][4], t_type, (String)die_inf[j][6],strategy,clust_num);
								fails_pix = pixel_fails.get_all_failures();
							}

							overlay_def_pix(defects_inf, fails_pix, k_x, k_y, x, y, (String)die_inf[j][4], dev, die_inf, j);
						}
					}
				}
				//repaint here table and map
				pix_match_tbl.getRowCount();
				pix_match_tbl.revalidate();
				pix_match_tbl.repaint();
				fm2.repaint();
			}
			finish = true;
			fm2.final_report();
			System.out.println("Analysis completed.");
		}




		public boolean is_finished()
		{
			return finish;
		}


		public void find_wf_dir(String mlot, String mwf){
			String[][]  flist;
			String root = "//aptprbfs1b/";
			int vol_num = 45;

			// COSI E" TROPPO LENTO...
			for(int j=0; j< vol_num; j++){
				String newroot = root+"vol"+Integer.toString(j)+"/";	
				File dir = new File(newroot);
				File[] children = dir.listFiles();

				if (children == null) {
					System.out.println("unable to list..."+ root);
				} else {
					for (int i=0; i<children.length; i++) {
						System.out.println("list..."+ children[i]);

					}
				}
			}

		}



		String[] find_bin_type(String dev, String failbin){
			String REGEX=",";
			Pattern pattern = Pattern.compile(REGEX);
			String[] bint = new String[6];
			bint[0] = bint[1] = "null";
			bint[2]= bint[3] = "0.0";
			String input;
			String DEV_DIR = "dev/";
			String conf_file = DEV_DIR + dev +"/" + "bin.txt";
			try{
				BufferedReader br = new BufferedReader(new FileReader(conf_file));
				while(( (input = br.readLine()) != null) )
				{
					String[] name_split = pattern.split(input);
					if(name_split[0].equals(failbin)){
						bint[0] = name_split[1];  
						bint[1] = name_split[2];
						bint[2] = name_split[3];
						bint[3] = name_split[4];
						bint[4] = name_split[5];
						bint[5] = name_split[6];
					}


				}
			} catch (Exception e3) {
				System.out.println(e3);
			}



			return bint;
		}



		//////////////////////////////////////////////////////
		///////////////// FING PIX TO DEF MATCHINGS ///////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////
		public void overlay_def_pix(Object[][] defects, Object[][] pix, int dx, int dy, int pdx, int pdy, String bin, String dev, Object[][] die_inf, int entry)
		{
			//System.out.println("in overlay_def_pix for die..."+ dx +","+dy);
			//ImageIcon imc2;
			int diex;
			int diey;
			int[] new_rc;
			String dieij;




			int i=0;
			//while(pix[i][0] != "null" ) { // for each pixel fail
			while(i< pix.length) { // for each pixel fail
				for(int j=0; j<defects.length; j++) {// all klar defects
					diex = (int)(double)defects[j][5];
					diey =  (int)(double)(defects[j][6]);

					//diex = (int)defects[j][5];
					//diey =  (int)(defects[j][6]);

					//System.out.println("diex, diey:" + diex+ "," +diey);


					//die flip for BSI
					// in die coord flip is in the db query

					boolean flip =  klar.need_flip(dev, (String)defects[j][8]);
					if(dev.equals("C4BX") && flip) 
						diex = 29-diex;

					if(dev.equals("C4BD") && flip) 
						diex = 31-diex;




					if(dx == diey && dy == diex){ 
						//System.out.println("in die analysis...");
						//System.out.println("defid: "+ (int)(double)defects[j][7]+ " x,y: " + defects[j][3]+","+ defects[j][4]);

						// serve la device qui...
						new_rc = klar_to_pix((double)defects[j][3], (double)defects[j][4], dev, flip, (String)defects[j][8]);
						String myclass = Double.toString((double)defects[j][9]);
						//System.out.println(klar.get_class(myclass));


						switch((String)pix[i][0]){

						case "ROW":
							if(new_rc[0] > (int)pix[i][2]-50 && new_rc[0] < (int)pix[i][2]+50){
								System.out.println("Matching def_id:"+ defects[j][7] +
										" rc:"+new_rc[0]+","+new_rc[1]+
										" @step:" + defects[j][8] + 
										" with row:" +(int)pix[i][2] );

								//System.out.println("\t"+" in die i/j: " + pdx +","+ pdy);
								//System.out.println("\t"+" BIN: " + bin);

								// FILL TBL .. In tabella mette solo il primo fail da original adder
								if((double)defects[j][2] == 1 ){// original adders
									//if(die_inf[entry][5] == 0){
									if((int)defects[j][10] == 0){
										dieij = Integer.toString(pdx)+","+Integer.toString(pdy);
										pix_match_tbl.setValueAt(dieij, ROW_COUNT,  0);
										pix_match_tbl.setValueAt(bin, ROW_COUNT,  1);
										pix_match_tbl.setValueAt(pix[i][0], ROW_COUNT,  2);
										pix_match_tbl.setValueAt(pix[i][2], ROW_COUNT,  3);
										pix_match_tbl.setValueAt(defects[j][7], ROW_COUNT,  4);
										pix_match_tbl.setValueAt(defects[j][8], ROW_COUNT,  5);
										// imc2 = new ImageIcon((BufferedImage)pix[i][3]);
										//pix_match_tbl.setValueAt(imc2, ROW_COUNT,  6);
										//pix_match_tbl.setValueAt(defects[j][9], ROW_COUNT,  6);
										pix_match_tbl.setValueAt(klar.get_class(myclass), ROW_COUNT,  6);
										pix_match_tbl.setValueAt(klar.get_idr(dev, (double)defects[j][3], (double)defects[j][4]), ROW_COUNT,  7);
										pix_match_tbl.setValueAt(new_rc[0]+","+new_rc[1], ROW_COUNT,  8);
										ROW_COUNT++;
										die_inf[entry][5] = 1; //match flag
									}
								}

								defects[j][10] = 1; //matching flag
							}	
							break;

						case "COL":
							if(new_rc[1] > (int)pix[i][1]-50 && new_rc[1] < (int)pix[i][1]+50){
								System.out.println("Matching def_id:"+ defects[j][7] + 
										" rc:"+new_rc[0]+","+new_rc[1]+
										" @step" + defects[j][8]
												+ " with col:" +(int)pix[i][1] );

								//System.out.println("in die x,y: " + defects[j][3]+","+ defects[j][4]);
								//System.out.println("\t"+" in die i/j: " + pdx +","+ pdy);
								//System.out.println("\t"+" BIN: " + bin);


								// FILL TBL
								if((double)defects[j][2] == 1){// original adders
									//if(die_inf[entry][5] == 0){
									if((int)defects[j][10] == 0){
										dieij = Integer.toString(pdx)+","+Integer.toString(pdy);
										pix_match_tbl.setValueAt(dieij, ROW_COUNT,  0);
										pix_match_tbl.setValueAt(bin, ROW_COUNT,  1);
										pix_match_tbl.setValueAt(pix[i][0], ROW_COUNT,  2);
										pix_match_tbl.setValueAt(pix[i][1], ROW_COUNT,  3);
										pix_match_tbl.setValueAt(defects[j][7], ROW_COUNT,  4);
										pix_match_tbl.setValueAt(defects[j][8], ROW_COUNT,  5);
										// imc2 = new ImageIcon((BufferedImage)pix[i][3]);
										//pix_match_tbl.setValueAt(imc2, ROW_COUNT,  6);
										//pix_match_tbl.setValueAt(defects[j][9], ROW_COUNT,  6);
										pix_match_tbl.setValueAt(klar.get_class(myclass), ROW_COUNT,  6);
										//pix_match_tbl.setValueAt("", ROW_COUNT,  7);
										pix_match_tbl.setValueAt(klar.get_idr(dev, (double)defects[j][3], (double)defects[j][4]), ROW_COUNT,  7);
										pix_match_tbl.setValueAt(new_rc[0]+","+new_rc[1], ROW_COUNT,  8);
										ROW_COUNT++;	
										die_inf[entry][5] = 1; //match flag
									}
								}

								defects[j][10] = 1; //matching flag
							}

							break;

						case "CLUSTER":
							if(new_rc[0] > (int)pix[i][2]-50 && new_rc[0] < (int)pix[i][2]+50 &&
									new_rc[1] > (int)pix[i][1]-50 && new_rc[1] < (int)pix[i][1]+50)
							{
								System.out.println("Matching def_id:"+ defects[j][7] + 
										" rc:"+new_rc[0]+","+new_rc[1]+
										" @step" + defects[j][8]
												+ " with clust r/c:" +(int)pix[i][2] + ","+ (int)pix[i][1]);

								//System.out.println("in die x,y: " + defects[j][3]+","+ defects[j][4]);
								//System.out.println("\t"+" in die i/j: " + pdx +","+ pdy);
								//System.out.println("\t"+" BIN: " + bin);


								// FILL TBL only original adders and the firts fail 
								if((double)defects[j][2] == 1 ){// original adders
									//if(die_inf[entry][5] == 0){
									if((int)defects[j][10] == 0){
										dieij = Integer.toString(pdx)+","+Integer.toString(pdy);
										pix_match_tbl.setValueAt(dieij, ROW_COUNT,  0);
										pix_match_tbl.setValueAt(bin, ROW_COUNT,  1);
										pix_match_tbl.setValueAt(pix[i][0], ROW_COUNT,  2);
										String die = Integer.toString((int)pix[i][2]) + ","+ Integer.toString((int)pix[i][1]);
										pix_match_tbl.setValueAt(die, ROW_COUNT,  3);
										pix_match_tbl.setValueAt(defects[j][7], ROW_COUNT,  4);
										pix_match_tbl.setValueAt(defects[j][8], ROW_COUNT,  5);
										//imc2 = new ImageIcon((BufferedImage)pix[i][3]);
										//pix_match_tbl.setValueAt(imc2, ROW_COUNT,  6);
										//pix_match_tbl.setValueAt(defects[j][9], ROW_COUNT,  6);
										pix_match_tbl.setValueAt(klar.get_class(myclass), ROW_COUNT,  6);
										//pix_match_tbl.setValueAt("", ROW_COUNT,  7);
										pix_match_tbl.setValueAt(klar.get_idr(dev, (double)defects[j][3], (double)defects[j][4]), ROW_COUNT,  7);
										pix_match_tbl.setValueAt(new_rc[0]+","+new_rc[1], ROW_COUNT,  8);
										ROW_COUNT++;
										die_inf[entry][5] = 1; //match flag
									}
								}


								defects[j][10] = 1; //matching flag

							}

							break;

						default:
							System.out.println("Unknown defect type...");
							break;

						}


					}
				}

				i++;	
			}

			if((int)die_inf[entry][5] == 0 && !(i==0))
			{

				dieij = Integer.toString(pdx)+","+Integer.toString(pdy);
				pix_match_tbl.setValueAt(dieij, ROW_COUNT,  0);
				pix_match_tbl.setValueAt(bin, ROW_COUNT,  1);

				pix_match_tbl.setValueAt(pix[0][0], ROW_COUNT,  2);
				String die = Integer.toString((int)pix[0][2]) + ","+ Integer.toString((int)pix[0][1]);
				pix_match_tbl.setValueAt(die, ROW_COUNT,  3);


				//	pix_match_tbl.setValueAt("", ROW_COUNT,  2);
				//	pix_match_tbl.setValueAt(0, ROW_COUNT,  3);
				pix_match_tbl.setValueAt(0.0, ROW_COUNT,  4);
				pix_match_tbl.setValueAt("Missed inline", ROW_COUNT,  5);
				pix_match_tbl.setValueAt("", ROW_COUNT,  6);
				pix_match_tbl.setValueAt("", ROW_COUNT,  7);
				pix_match_tbl.setValueAt(die, ROW_COUNT,  8);
				ROW_COUNT++;

				die_inf[entry][5] = 2; // analized & not justified
			}

			if (i==0) // no efails found
			{

				dieij = Integer.toString(pdx)+","+Integer.toString(pdy);
				pix_match_tbl.setValueAt(dieij, ROW_COUNT,  0);
				pix_match_tbl.setValueAt(bin, ROW_COUNT,  1);
				pix_match_tbl.setValueAt("", ROW_COUNT,  2);
				//String die = Integer.toString((int)pix[i][1]) + ","+ Integer.toString((int)pix[i][2]);
				pix_match_tbl.setValueAt("0,0", ROW_COUNT,  3);
				pix_match_tbl.setValueAt(0.0, ROW_COUNT,  4);
				pix_match_tbl.setValueAt("NO PIX-FAILS", ROW_COUNT,  5);
				//imc2 = new ImageIcon((BufferedImage)pix[i][3]);
				//pix_match_tbl.setValueAt(imc2, ROW_COUNT,  6);
				pix_match_tbl.setValueAt("", ROW_COUNT,  6);
				pix_match_tbl.setValueAt("", ROW_COUNT,  7);
				pix_match_tbl.setValueAt("0,0", ROW_COUNT,  8);
				ROW_COUNT++;

				die_inf[entry][5] = 3;
			}

		}



		///////////////////////////////////////////////////////////
		/// For each dev transform klarity coordinate in pixels
		///////////////////////////////////////////////////////////
		public int[] klar_to_pix(double dx, double dy, String device, boolean flip, String lay){
			int[] out = new int[2];
			out[0] = out[1] =0;


			//System.out.println("lay...: " + lay);

			switch(device){
			case "C1CC":
				//row
				out[0] = (int)(3628 - (dy/1000 - 3867)/3.4);
				//col
				out[1] = (int)(4043 - (dx/1000 - 2420)/3.4);

				break;

			case "C1EA":
				//row
				out[0] = (int)(3259 - (dy/1000 -3525-201)/2.86);
				//col
				out[1] = (int)(4633 -(dx/1000 -98.5- 1972 -341)/2.86);

				break;



			case "C2GA":
				//Row = 3694-((y-3832)/2.5)
				//Column = 5276-((x-2025)/2.5)

				//row
				out[0] = (int)(3694 - (dy/1000 - 3832)/2.5);
				//col
				out[1] = (int)(5276 - (dx/1000 - 2025)/2.5);

				break;

			case "C2GB":
				//row
				out[0] = (int)(3694 - (dy/1000 - 3832)/2.5);
				//col
				out[1] = (int)(5276 - (dx/1000 - 2025)/2.5);

				break;



			case "C24D":
				//				H row = (y_klarity-1828.12)/3-15
				//				V col = (x_klarity-95.6-1046.33)/3+29

				//row
				out[0] = (int)((dy/1000 - 1828.12)/3 -15);
				//col
				out[1] = (int)((dx/1000 - 95.6 - 1046.33)/3 + 29);

				break;			


			case "C24A":
				//				H row = (y_klarity-1969.2)/3.75-26.4
				//				V col = (x_klarity-94.5-1108.4)/3.75+8


				//row
//				out[0] = (int)((dy/1000 - 1969.2)/3.75 - 26.4);
				out[0] = (int)((dy/1000 - 1969.2)/3.75 - 36.4);
				
				//col
//				out[1] = (int)((dx/1000 - 94.5-1108.4)/3.75 + 8);
				out[1] = (int)((dx/1000 - 94.5-1108.4)/3.75 - 80);

				break;					


			case "C24C":
				//			row = 977 - ((x_klarity - 1495)/3.75)
				//				column = ((y_klarity - 1168)/3.75) - 6


				//row
				out[0] = (int)(977 - (dx/1000 - 1495)/3.75);
				//col
				out[1] = (int)((dy/1000 - 1168)/3.75) - 6;

				break;


			case "C25C":
				//				r=((y_klarity-2651)/3 – 16)
				//				c=((x_klarity-1402)/3 – 6)


				//row
				out[0] = (int)((dy/1000 - 2651)/3 -16);
				//col
				out[1] = (int)((dx/1000 - 1402)/3 -6);

				break;		



			case "C2EB":
				//row
				out[0] = (int)((dy/1000 - 2052-106.4)/1.42);
				//col
				out[1] = (int)((dx/1000 - 855-252)/1.42);
				break;

			case "C4BA":
				//row
				out[0] = (int)((dy/1000 - (1363+118))/1.4);
				//col
				out[1] = (int)((dx/1000 - (120+767+201))/1.4);
				break;

			case "C48B":
				//row
				//out[0] = (int)((dy/1000 - 1158.8)/1.4 - 44.3);
				//col
				//out[1] = (int)((dx/1000 - 766.4)/1.4 - 44.3);

				////////////////  x correzione shift 2367 /////////////////////////

				// list of steps with shift
				java.util.List<String> s = Arrays.asList("C48B_FIELD_B", "C48B_30_ACI_B", "C48B_60_TC_ETCH_B", "C48B_92_CU_CMP_B", "C48B_20_HDP_B");

				if(s.contains(lay)){  // correction..
					System.out.println("Applying correction for step " + lay);
					out[0] = (int)((dy/1000 - 1158.8)/1.4 - 108.3);
				}
				else //normal trasformation
					out[0] = (int)((dy/1000 - 1158.8)/1.4 - 44.3);

				//col
				out[1] = (int)((dx/1000 - 766.4)/1.4 - 44.3);	

				break;


			case "C4BX":
				//row
				out[0] = (int)((dy/1000 - (1363+118))/1.4);
				//col
				if(flip)
					out[1] = (int)(3279 - (dx/1000 - (120+767+201))/1.4); //img flip
				else 
					out[1] = (int)((dx/1000 - (120+767+201))/1.4);


				break;





			case "C4BD":
				//	FSI:        r=(2463-(x-1678)/1.4);  c=(3279-(y-949)/1.4)
				//	BSI:        r=(x-1252)/1.4); 		c=(3279-(y-949)/1.4)
				//row
				if(flip)
					out[0] = (int)(2463- (dx/1000 - 1678)/1.4);
				else
					out[0] = (int)((dx/1000 - 1252)/1.4);

				//col
				out[1] = (int)(3279- (dy/1000 - 949)/1.4);


				break;





			case "K22B": // col row inv????????
				//row
				out[0] = (int)((-dx/1000 + 6495 - 2361)/5.6);
				//col
				out[1] = (int)((dy/1000 - 1699)/5.6);
				break;

			case "K45A":
				//row
				out[0] = (int)((dx/1000 -95 - 1017)/1.4)+22;
				//col
				out[1] = (int)((dy/1000 - 1366)/1.4)-134;

				break;


			case "C26E":	
				//					r=2321-(x/2.2)
				//				c=(y/2.2)-245

				//row
				out[0] = (int)(2321 - (dx/1000)/2.2);
				//col
				out[1] = (int)((dy/1000)/2.2 -245);


				//row
				//out[0] = (int)(1536 - (dx/1000 - 1720)/2.2);
				//col
				//out[1] = (int)((dy/1000 - 530)/2.2);


				break;



			default:
				System.out.println("No coord trasform for this device...");
				break;

			}


			return out;	
		}





		//////////////////////////////////////////////////////////////////////////
		////////////// GET IMG PATH //////////////////////////////////////////////

		// NEW FORMAT FOR ALC SUPPORT
		public String get_img_file(int diex, int diey, String bin, String test)
		{
			String out_name = "null";
			String REGEX="\\.";
			Pattern pattern = Pattern.compile(REGEX);
			//String UNC_Path = "1332870552.7498299.003.8299-01.QPP.00.C1CC.X04S/";	
			//String UNC_Path = "//aptprbfs1b/vol19/1332870552.7498299.003.8299-01.QPP.00.C1CC.X04S/";
			String UNC_Path="null";

			if(test.equals("QPP"))
				UNC_Path = QPP;
			else if(test.equals("FPP"))
				UNC_Path = FPP;

			File dir = new File(UNC_Path);

			String[] children = dir.list();

			if (children == null) {
				// Either dir does not exist or is not a directory
			} else {
				for (int i=0; i<children.length; i++) {
					//System.out.println(children[i]);
					// example of image name
					//1332870552.7498299.003.8299-01.QPP.00.C1CC.+000.+003.X04S.0.1.Bin_D.png
					String[] name_split = pattern.split(children[i]);
					if(name_split.length >= 13){
						//System.out.println(name_split[7]+","+name_split[8]+","+name_split[12]);
						if(Integer.parseInt(name_split[7]) == diex && Integer.parseInt(name_split[8]) == diey 
								&& name_split[12].equals(bin))
						{
							switch (name_split[13]){
							case "png":
								System.out.println("found: "+children[i]);
								out_name = UNC_Path + children[i];
								break;

							case "alc":
								out_name = get_alc(UNC_Path + children[i], children[i]);
								break;


							default:
								System.out.println("Unmanaged file format: " + name_split[13]);
								break;
							}
						}
					}
				}
			}

			return out_name;	
		}




		///////////////////////////////////////////////////////////////////////
		////
		////  HANDLING OF ALC FILES...
		////
		///////////////////////////////////////////////////////////////////////
		public  String get_alc(String file, String name){
			String cdir = "alc/";
			String out ="";
			System.out.println("Transforming alc file to png...");

			try {
				BufferedReader br_conf = new BufferedReader(new FileReader("unalc.cfg"));
				String unalc = br_conf.readLine();
				br_conf.close();

				String[] myCall = {unalc,"-n", "-d", cdir, file};
				//System.out.println("Executing " + myCall);

				Process p = Runtime.getRuntime().exec(myCall);
				p.waitFor();
				System.out.println("Done.");

				String nam  = name.replace("alc", "png"); // on the file is done by unalc
				out= cdir+nam;

				System.out.println("File: " + out);

			} catch (Exception e2) {e2.printStackTrace();}

			return out;
		}





	} //end defect to pixel










	/*************************************
	 *
	 *************************************/
	class Fillwafer extends JPanel implements MouseListener, MouseMotionListener
	{
		Color bg = Color.white;
		Connection con = null;
		double[][] matrix, matrix_2;
		int count;
		private String DOT="\\.";
		int xdim=0;
		String df;
		String lot;
		String wf;
		String wk;
		String it;
		String layer;
		Rectangle r; 
		int or_x, or_y, dim_x, dim_y = 0;

		double fact = 3.2;

		int selection=0;	
		double cx;
		double cy;
		int constr_type = 0;
		BufferedImage imc2;  
		getmap_2 gm ;
		getmap_centered gm2;
		double origxy[];
		double x_off=0;
		double y_off=0;

		public Fillwafer(db klar, get_data gd)
		{     
			addMouseListener(this);
			addMouseMotionListener(this);

			constr_type = 1;

			it = gd.get_it();
			wk= gd.get_wk();
			df = gd.get_def_num();
			lot = gd.get_lot(); 
			wf= gd.get_wid();
			layer= gd.get_layer();

			cx = Double.valueOf(gd.get_cx()).doubleValue();
			cy = Double.valueOf(gd.get_cy()).doubleValue();     

			origxy = klar.get_origxy(wk, it);
			x_off = origxy[0] - cx;
			y_off = origxy[1] - cy;

			matrix_2 = new double[Integer.parseInt(df)][4]; // transformed x,y
			matrix = klar.get_db_data(it, wk, df, cx, cy,0,0);         //original x,y
			//matrix = klar.get_db_data(it, wk, df, x_off, y_off);         //original x,y

			for(int l=0; l < Integer.parseInt(df); l++){   

				// changing here...KLA said the data are notch down
				matrix_2[l][0] = ( ((matrix[l][0]/1000000)) ) * fact;
				matrix_2[l][1] = ( (matrix[l][1]/1000000) ) * fact;

				matrix_2[l][2] = matrix[l][2];
				matrix_2[l][3] = 0;           //selection
			}



			// matrice posizioni originali
			//gm2 = new getmap_centered(matrix_2, fact,  origxy[0], origxy[1]);
			gm2 = new getmap_centered(matrix_2, fact,  origxy[0], origxy[1]);
			imc2 = gm2.getImg();

		}



		// SECONDO COSTRUTTORE...
		public Fillwafer(String filename)
		{  
			constr_type = 0;

			String REGEX="\\,";
			String INPUT;
			String[] value;
			int i = 0;
			double [][] buff_mat;

			buff_mat= new double[10000][2];
			for(int k=0; k<10000; k++){
				buff_mat[k][0] = 0;
				buff_mat[k][1] = 0;
			}

			addMouseListener(this);
			addMouseMotionListener(this);


			try {

				BufferedReader br = new BufferedReader(new FileReader(filename));

				Pattern pattern = Pattern.compile(REGEX);



				while (( (INPUT = br.readLine()) != null) )
				{        

					value = pattern.split(INPUT);
					buff_mat[i][0] = Double.valueOf(value[0]).doubleValue();
					buff_mat[i][1] = Double.valueOf(value[1]).doubleValue();

					i++;
				} 
			} catch (Exception e) {
				System.out.println(e);
			}

			df = Integer.toString(i);
			selection = i;       


			matrix_2 = new double[i][4]; // transformed x,y
			matrix = new double[i][2];         //original x,y

			for(int j = 0; j<i; j++) {
				matrix[j][0] = buff_mat[j][0];
				matrix[j][1] = buff_mat[j][1];


				matrix_2[j][0] = ( ((matrix[j][0]/1000000)) ) * fact;
				matrix_2[j][1] = ( (matrix[j][1]/1000000) ) * fact;
				matrix_2[j][2] = 0;           // def_id non mi interessa
				matrix_2[j][3] = 1;           //selectio


			}

			gm = new getmap_2(matrix_2, fact);
			imc2 = gm.getImg();

		}






		public void rotate(double angle) {

			double deg = Math.toRadians(angle);
			double center = 100 * fact;



			for(int l=0; l < Integer.parseInt(df); l++){   


				double x = matrix_2[l][0] - center;
				double y = matrix_2[l][1] - center;

				double rho = Math.sqrt( (x * x) + (y * y) );
				double alpha = Math.atan2(x,y);
				double theta = Math.PI/2 - (alpha + deg);

				System.out.println("rho = " + rho +"\n");
				System.out.println("theta = " + theta +"\n");


				matrix_2[l][0] =  rho * Math.cos(theta) + center;       
				matrix_2[l][1] =  rho * Math.sin(theta) + center;

			}

		}



		public void mouseDragged(MouseEvent e){

			dim_x = e.getX()-or_x;
			dim_y = e.getY()-or_y;


			//System.out.println("Mouse drag...." + "\n");
			repaint();

		}



		public void mousePressed(MouseEvent e){
			or_x  = e.getX(); 
			or_y =  e.getY();
			//System.out.println("Mouse pressed" + or_x + or_y + "\n");
		}



		// Handles the event of a user releasing the mouse button.
		public void mouseReleased(MouseEvent e){
			//System.out.println("Mouse released" + "\n");
			selection = 0;
			for(int m=0; m < Integer.parseInt(df); m++) {
				if(matrix_2[m][3] == 1)  //gia' selezionati
					selection++;

				else if(r.contains(matrix_2[m][0], matrix_2[m][1]) ) {  
					matrix_2[m][3] = 1; 
					selection++;
				}
			}
			//gm = new getmap_2(matrix_2, fact);
			//imc2 = gm.getImg();
			gm2 = new getmap_centered(matrix_2, fact,  origxy[0], origxy[1]);
			imc2 = gm2.getImg();
			System.out.println("Selected: " + selection + "\n");

			or_x  =  or_y= dim_x= dim_y = 0;
			repaint();
		}


		public void mouseMoved(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseClicked(MouseEvent e){}





		//si e' beccato delle eccezioni senza f<selection. Da verificare.
		// Mi sa che e' legato al fatto che ho fatto 2 selezioni con il mouse!!!!
		// va gestita

		public double[][] get_matrix()
		{
			double[][] selected_matrix = new double[selection][2];

			int f = 0;
			for(int n=0; n < Integer.parseInt(df); n++)
				if((matrix_2[n][3] == 1) )
					//if((matrix_2[n][3] == 1) && (f < selection))       
				{
					selected_matrix[f][0] = matrix[n][0];
					selected_matrix[f][1] = matrix[n][1];

					f++;
				}

			return(selected_matrix);
		}





		/// HERE 
		/// NEW WITH DIE GRID

		public void paintComponent(Graphics g) {
			super.paintComponent(g); 

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     

			Color fg3D = Color.white;

			g2.setBackground(Color.white);

			double xwf = 200 * fact;
			double ywf = 200 * fact;

			//serve un controllo su unpatterned
			double die_dim[] = klar.get_diesize(wk, it);
			double dix = die_dim[0]/1000000 * fact;
			double diy = die_dim[1]/1000000 * fact;
			int stepx = (int)(xwf/dix)+1;
			int stepy = (int)(xwf/diy)+1;
			System.out.println("die step x-y: "+ stepx +","+stepy+ "\n");


			g2.drawImage(imc2, 0, 0, (int)xwf, (int)ywf, null);


			r = new Rectangle(or_x, or_y, dim_x, dim_y);
			//g2.setStroke(dashed);
			g2.draw(r);

			double sx, sxini = (100000000 - cx)/1000000 *fact;
			double sy = ywf - (100000000 - cy)/1000000 *fact;

			System.out.println("sx,sy="+sxini+ "-" + sy);

			String newlot = lot.replace("_", ".");
			System.out.println(newlot);
			//trial grid  
			for(int i=0; i< stepy; i++){
				sx = sxini;
				for(int j=0; j< stepx; j++){

					g2.setPaint(Color.gray);  	

					Rectangle2D.Double rg = new Rectangle2D.Double(sx ,sy-diy, dix, diy);
					g2.drawString(j+","+i, (float)sx, (float)(sy-diy/2));

					////////////////////////////////////////////////////////////////////////
					g2.setPaint(Color.gray);  

					g2.draw(rg);

					sx = sx + dix; 
				}
				sy = sy - diy;
			}


			if(constr_type == 1) {
				g2.setPaint(Color.red);          
				g2.drawString("Lot: " + lot, 0, (float)ywf+10);
				g2.drawString("wafer: " + wf, 0 , (float)ywf+20);
				g2.drawString("Step: " + layer, 0, (float)ywf+30);
			}

		} // end of paint	





	} // end of FillWafer class






	/*************************************
	 *
	 *************************************/
	class Fillwafer_w_probe extends JPanel implements MouseListener, MouseMotionListener
	{
		Color bg = Color.white;
		Connection con = null;
		double[][] matrix, matrix_2;
		int count;
		private String DOT="\\.";
		int xdim=0;
		String df;
		String lot;
		String wf;
		String wk;
		String it;
		String layer;
		Rectangle r; 
		int or_x, or_y, dim_x, dim_y = 0;

		double fact = 3.2;

		int selection=0;	
		double cx;
		double cy;
		int constr_type = 0;
		BufferedImage imc2;  
		getmap_2 gm ;
		getmap_centered gm2;
		double origxy[];
		double x_off=0;
		double y_off=0;

		public Fillwafer_w_probe(db klar, get_data gd)
		{     
			addMouseListener(this);
			addMouseMotionListener(this);

			constr_type = 1;

			it = gd.get_it();
			wk= gd.get_wk();
			df = gd.get_def_num();
			lot = gd.get_lot(); 
			wf= gd.get_wid();
			layer= gd.get_layer();

			cx = Double.valueOf(gd.get_cx()).doubleValue();
			cy = Double.valueOf(gd.get_cy()).doubleValue();     

			origxy = klar.get_origxy(wk, it);
			x_off = origxy[0] - cx;
			y_off = origxy[1] - cy;

			matrix_2 = new double[Integer.parseInt(df)][4]; // transformed x,y
			matrix = klar.get_db_data(it, wk, df, cx, cy,0,0);         //original x,y
			//matrix = klar.get_db_data(it, wk, df, x_off, y_off);         //original x,y

			for(int l=0; l < Integer.parseInt(df); l++){   

				// changing here...KLA said the data are notch down
				matrix_2[l][0] = ( ((matrix[l][0]/1000000)) ) * fact;
				matrix_2[l][1] = ( (matrix[l][1]/1000000) ) * fact;

				matrix_2[l][2] = matrix[l][2];
				matrix_2[l][3] = 0;           //selection
			}



			// matrice posizioni originali
			//gm2 = new getmap_centered(matrix_2, fact,  origxy[0], origxy[1]);
			gm2 = new getmap_centered(matrix_2, fact,  origxy[0], origxy[1]);
			imc2 = gm2.getImg();

		}



		// SECONDO COSTRUTTORE...
		public Fillwafer_w_probe(String filename)
		{  
			constr_type = 0;

			String REGEX="\\,";
			String INPUT;
			String[] value;
			int i = 0;
			double [][] buff_mat;

			buff_mat= new double[10000][2];
			for(int k=0; k<10000; k++){
				buff_mat[k][0] = 0;
				buff_mat[k][1] = 0;
			}

			addMouseListener(this);
			addMouseMotionListener(this);


			try {

				BufferedReader br = new BufferedReader(new FileReader(filename));

				Pattern pattern = Pattern.compile(REGEX);



				while (( (INPUT = br.readLine()) != null) )
				{        

					value = pattern.split(INPUT);
					buff_mat[i][0] = Double.valueOf(value[0]).doubleValue();
					buff_mat[i][1] = Double.valueOf(value[1]).doubleValue();

					i++;
				} 
			} catch (Exception e) {
				System.out.println(e);
			}

			df = Integer.toString(i);
			selection = i;       


			matrix_2 = new double[i][4]; // transformed x,y
			matrix = new double[i][2];         //original x,y

			for(int j = 0; j<i; j++) {
				matrix[j][0] = buff_mat[j][0];
				matrix[j][1] = buff_mat[j][1];


				matrix_2[j][0] = ( ((matrix[j][0]/1000000)) ) * fact;
				matrix_2[j][1] = ( (matrix[j][1]/1000000) ) * fact;
				matrix_2[j][2] = 0;           // def_id non mi interessa
				matrix_2[j][3] = 1;           //selectio


			}

			gm = new getmap_2(matrix_2, fact);
			imc2 = gm.getImg();

		}






		public void rotate(double angle) {

			double deg = Math.toRadians(angle);
			double center = 100 * fact;



			for(int l=0; l < Integer.parseInt(df); l++){   


				double x = matrix_2[l][0] - center;
				double y = matrix_2[l][1] - center;

				double rho = Math.sqrt( (x * x) + (y * y) );
				double alpha = Math.atan2(x,y);
				double theta = Math.PI/2 - (alpha + deg);

				System.out.println("rho = " + rho +"\n");
				System.out.println("theta = " + theta +"\n");


				matrix_2[l][0] =  rho * Math.cos(theta) + center;       
				matrix_2[l][1] =  rho * Math.sin(theta) + center;

			}

		}



		public void mouseDragged(MouseEvent e){

			dim_x = e.getX()-or_x;
			dim_y = e.getY()-or_y;


			//System.out.println("Mouse drag...." + "\n");
			repaint();

		}



		public void mousePressed(MouseEvent e){
			or_x  = e.getX(); 
			or_y =  e.getY();
			//System.out.println("Mouse pressed" + or_x + or_y + "\n");
		}



		// Handles the event of a user releasing the mouse button.
		public void mouseReleased(MouseEvent e){
			//System.out.println("Mouse released" + "\n");
			selection = 0;
			for(int m=0; m < Integer.parseInt(df); m++) {
				if(matrix_2[m][3] == 1)  //gia' selezionati
					selection++;

				else if(r.contains(matrix_2[m][0], matrix_2[m][1]) ) {  
					matrix_2[m][3] = 1; 
					selection++;
				}
			}
			//gm = new getmap_2(matrix_2, fact);
			//imc2 = gm.getImg();
			gm2 = new getmap_centered(matrix_2, fact,  origxy[0], origxy[1]);
			imc2 = gm2.getImg();
			System.out.println("Selected: " + selection + "\n");

			or_x  =  or_y= dim_x= dim_y = 0;
			repaint();
		}


		public void mouseMoved(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseClicked(MouseEvent e){}





		//si e' beccato delle eccezioni senza f<selection. Da verificare.
		// Mi sa che e' legato al fatto che ho fatto 2 selezioni con il mouse!!!!
		// va gestita

		public double[][] get_matrix()
		{
			double[][] selected_matrix = new double[selection][2];

			int f = 0;
			for(int n=0; n < Integer.parseInt(df); n++)
				if((matrix_2[n][3] == 1) )
					//if((matrix_2[n][3] == 1) && (f < selection))       
				{
					selected_matrix[f][0] = matrix[n][0];
					selected_matrix[f][1] = matrix[n][1];

					f++;
				}

			return(selected_matrix);
		}





		/// HERE 
		/// NEW WITH DIE GRID

		public void paintComponent(Graphics g) {
			super.paintComponent(g); 

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     

			Color fg3D = Color.white;

			g2.setBackground(Color.white);

			double xwf = 200 * fact;
			double ywf = 200 * fact;


			double die_dim[] = klar.get_diesize(wk, it);
			double dix = die_dim[0]/1000000 * fact;
			double diy = die_dim[1]/1000000 * fact;
			int stepx = (int)(xwf/dix)+1;
			int stepy = (int)(xwf/diy)+1;
			System.out.println("die step x-y: "+ stepx +","+stepy+ "\n");


			g2.drawImage(imc2, 0, 0, (int)xwf, (int)ywf, null);


			r = new Rectangle(or_x, or_y, dim_x, dim_y);
			//g2.setStroke(dashed);
			g2.draw(r);

			double sx, sxini = (100000000 - cx)/1000000 *fact;
			double sy = ywf - (100000000 - cy)/1000000 *fact;

			System.out.println("sx,sy="+sxini+ "-" + sy);

			String newlot = lot.replace("_", ".");
			System.out.println(newlot);
			//trial grid  
			for(int i=0; i< stepy; i++){
				sx = sxini;
				for(int j=0; j< stepx; j++){

					g2.setPaint(Color.gray);  	

					Rectangle2D.Double rg = new Rectangle2D.Double(sx ,sy-diy, dix, diy);
					g2.drawString(j+","+i, (float)sx, (float)(sy-diy/2));

					// Query the fail bin x each die //////////////////////////////////////////////////
					String newi = Integer.toString(j-5);
					String newj = Integer.toString(i-1);
					g2.drawString(newj+","+newi, (float)sx, (float)(sy-diy/4));

					String mybin = probe.getfailbin(probe_db_con, newlot, wf, newi, newj);
					g2.setPaint(Color.red);
					if(mybin != "null")
						g2.drawString(mybin, (float)(sx+dix/2), (float)(sy-diy/2));
					////////////////////////////////////////////////////////////////////////
					g2.setPaint(Color.gray);  
					if(mybin != "null")
						g2.draw(rg);

					sx = sx + dix; 
				}
				sy = sy - diy;
			}


			if(constr_type == 1) {
				g2.setPaint(Color.red);          
				g2.drawString("Lot: " + lot, 0, (float)ywf+10);
				g2.drawString("wafer: " + wf, 0 , (float)ywf+20);
				g2.drawString("Step: " + layer, 0, (float)ywf+30);
			}

		} // end of paint	





	} // end of FillWafer class










	//////////////===============================================////////////////////
	////////////////////////// CLASS CLUSTER ANALISYS ///////////////////////////////
	//////////////===============================================////////////////////

	public class cluster_analysis {

		double[][] mat;
		int size;
		int def_num;
		int step;
		int CLUST_NUM;
		int[][] vect_2;
		int LOG_LEVEL;


		//costruttore
		public cluster_analysis(double[][] mymat, int mysize, int mydef_num, int mystep, int loglev) {
			mat = mymat;
			size = mysize;
			def_num = mydef_num;
			step = mystep;
			CLUST_NUM = 2;
			LOG_LEVEL = loglev;
			vect_2 = clusterize();
		} // end costruttore




		int[][] clusterize()
		{
			int CUR_CLUST = 1;
			int ASS_CLUST = 1;
			int sx =0,sy = 0;
			int[][] vect = new int[step][step];

			for(int i=0; i< step; i++){
				sx = 0;
				for(int j=0; j< step; j++){
					vect[i][j] = 0;            
					Rectangle rg = new Rectangle(sx ,sy, size, size);
					int cont =0;
					for(int l=0; l<def_num; l++)
						if(rg.contains(mat[l][0],  mat[l][1]))
							cont++;

					if(cont > MIN_CLUST_DEF) { 
						vect[i][j] = 1;
					}                   

					sx = sx + size; 
				}
				sy = sy + size;
			}


			vect_2 = vect;


			for(int i=0; i< step; i++)
				for(int j=0; j< step; j++)
				{
					if(vect_2[i][j]==1)
					{
						CUR_CLUST = 1;

						// check primi vicini  ///////////////               
						for(int m=i-1; m<= i+1; m++){
							for(int n=j-1; n<= j+1; n++) {
								if(( m<step) && (m>=0) && (n<step) && (n>=0))
									if(vect_2[m][n] > 1) {
										CUR_CLUST = vect_2[m][n];
										//System.out.println("vect2 mag..."+ CUR_CLUST + "\n");
									} 

							}
						}


						// check secondi vicini  TBD rifa il chick sui primi ///////////////
						if(CUR_CLUST ==1 ) // non ha trovato nulla sui primi
							for(int m=i-2; m<= i+2; m++){
								for(int n=j-2; n<= j+2; n++) {
									if(( m<step) && (m>=0) && (n<step) && (n>=0))
										if(vect_2[m][n] > 1) {
											CUR_CLUST = vect_2[m][n];
											//System.out.println("vect2 mag..."+ CUR_CLUST + "\n");
										} 

								}
							}

						///////////////////////////////

						if(CUR_CLUST > 1)
						{   
							ASS_CLUST = CUR_CLUST;
						}
						else
						{              
							ASS_CLUST = CLUST_NUM;
							CLUST_NUM++;
						}              



						vect_2[i][j] = ASS_CLUST;


						//propaga a tutti i primi vicini ancora non assegnati
						for(int m=i-1; m<= i+1; m++){
							for(int n=j-1; n<= j+1; n++) {
								if(( m<step) && (m>=0) && (n<step) && (n>=0))
									if(vect_2[m][n] == 1)
										vect_2[m][n] = ASS_CLUST;
							}
						}


					}
				}  

			if(LOG_LEVEL == 1) {
				for(int a=0; a< step; a++){
					for(int b=0; b< step; b++){
						System.out.println(Integer.toString(vect_2[a][b]));
						if(b == step-1)
							System.out.println("\n");  
					} 
				}
			}


			return(vect_2);

		} //end clusterize





		////////////////////
		int get_cluster_num()
		{

			if(LOG_LEVEL == 1)
				System.out.println("Clusters num on wf: " +  (CLUST_NUM-2) +"\n");
			return(CLUST_NUM);
		}





		////////// DIMENSIONE DEL CLUSTER //////////////////////////////
		int get_cluster_dim(int the_cluster)
		{
			int CLUST_DIM = 0;

			// calculate cluster dimension
			for(int k=0; k< step; k++)
				for(int j=0; j< step; j++)
					if(vect_2[k][j] == the_cluster)              
						CLUST_DIM++;     


			return(CLUST_DIM);
		}





		//////////////////////////
		double[][] analyze_cluster(int the_cluster)
		{

			int CLUST_DIM = get_cluster_dim(the_cluster);
			int[][] my_cluster = new int[CLUST_DIM][2];  
			double[][] cluster_XY_mean;


			int dim=0;
			for(int k=0; k< step; k++)
				for(int j=0; j< step; j++)
					if(vect_2[k][j]==the_cluster) {

						my_cluster[dim][0] = k;            
						my_cluster[dim][1] = j;

						dim++;
					}              

			if(LOG_LEVEL == 1)
				System.out.println("Cluster num: " + the_cluster + "\n");


			cluster_XY_mean = new double[CLUST_DIM][2];  


			for(int a=0; a< CLUST_DIM; a++)  {
				cluster_XY_mean[a][0] =0;
				cluster_XY_mean[a][1] =0;
			}




			for(int a=0; a< CLUST_DIM; a++) {
				Rectangle rg = new Rectangle(my_cluster[a][1]*size, my_cluster[a][0]*size, size, size);
				for(int m=0; m < def_num; m++) 
					if(rg.contains(mat[m][0],  mat[m][1])) { // inversion x,y ????

						if(m==0){
							cluster_XY_mean[a][0] = mat[m][0];
							cluster_XY_mean[a][1] = mat[m][1];
						} 

						cluster_XY_mean[a][0] = (cluster_XY_mean[a][0] + mat[m][0])/2;
						cluster_XY_mean[a][1] = (cluster_XY_mean[a][1] + mat[m][1])/2;

					}
			}   

			if(LOG_LEVEL == 1) {
				for(int a=0; a< CLUST_DIM; a++)  
					System.out.println(cluster_XY_mean[a][0] + ", " + cluster_XY_mean[a][1] + "\n");
			}


			return(cluster_XY_mean);

		}







		//////////////////////////////////////////////////////////////////////////////
		double[] get_m_q(double[][] mean_points)
		{
			double q=0;
			double m=0;
			double dx;
			double dy;
			int dim = mean_points.length;

			/////  calcolo della retta  m, q //////////////////////////
			//     va fatto il sort lungo X o y per vedere dove inizia e dove finisce, puo' non coincidere con 0, dim-1
			int XMIN = 0;
			int XMAX = 0;
			int YMIN = 0;
			int YMAX = 0;
			double X_EXT = 0.0;
			double Y_EXT = 0.0;

			for(int a=0; a< dim; a++) {
				if(mean_points[a][0] < mean_points[XMIN][0])
					XMIN = a;
				if(mean_points[a][0] > mean_points[XMAX][0])
					XMAX = a;
				if(mean_points[a][1] < mean_points[YMIN][1])
					YMIN = a;
				if(mean_points[a][1] > mean_points[YMAX][1])
					YMAX = a;
			}

			X_EXT = mean_points[XMAX][0] - mean_points[XMIN][0];
			Y_EXT = mean_points[YMAX][1] - mean_points[YMIN][1]; 

			if(LOG_LEVEL == 1)  {
				System.out.println(XMIN  + "   " + XMAX + "\n");
				System.out.println(YMIN  + "   " + YMAX + "\n");
				System.out.println(X_EXT  + "   " + Y_EXT + "\n");
			}
			///////////////////////////////////////////////////////////////////////////////////////////


			if(X_EXT >= Y_EXT) {
				dx = mean_points[XMAX][0] - mean_points[XMIN][0]; 
				dy = mean_points[XMAX][1] - mean_points[XMIN][1];
			} else {
				dx = mean_points[YMAX][0] - mean_points[YMIN][0]; 
				dy = mean_points[YMAX][1] - mean_points[YMIN][1];
			}

			m = dy/dx;

			q=0;
			for(int i=0; i< dim; i++)   
				q = q + mean_points[i][1] - m * mean_points[i][0];
			q = q/dim; 

			if(LOG_LEVEL == 1)  
				System.out.println("y = " + m + "x + " + q + "\n");

			double[] mq = {m, q};
			return(mq);
		}





		///////////////////////////////////////////////////////////////

		double[] optimize_linear_param(double[][] array, double start_m, double start_q, 
				int split, double variability)
		{
			int dim = array.length;
			double SE = 0;
			double SSE = 0;
			double MINSSE = 0;			
			double q = start_q;
			double m = start_m;
			double qvar = Math.abs(variability*q/split);
			double mvar = Math.abs(variability*m/split);
			double newq = q - ((variability/2) * Math.abs(q));
			double newm = m - ((variability/2) * Math.abs(m));




			MINSSE=1.0E10;
			for(int k=0; k< split; k++) {
				SSE = 0;
				newm = newm + mvar;
				for(int i=0; i< dim; i++) {    
					SE = array[i][1] - (newm * array[i][0] + q );
					SSE = SSE + (SE*SE);
				}

				if(k==1)
					MINSSE = SSE;

				if(SSE < MINSSE) {
					m = newm; 
					MINSSE = SSE;
				}
				//  System.out.println(newm + " : " + m + " : "+  SSE +"\n");		
			}






			MINSSE=1.0E10;

			for(int k=0; k< split; k++) {
				SSE = 0;
				newq = newq + qvar;
				for(int i=0; i< dim; i++) {    
					SE = array[i][1] - (m * array[i][0] + newq);
					SSE = SSE + (SE*SE);
				}

				if(k==1)
					MINSSE = SSE;

				if(SSE < MINSSE) {
					q = newq; 
					MINSSE = SSE;
				}

				//System.out.println(newq + " : " + q + " : "+  SSE +"\n");

			}



			double[] r_val = {m,q};
			return(r_val);

		}

		////////////////////////////////////////////////////////////////////////////

		double[] optimize_quad_param(double[][] array, double start_m, double start_q, double start_c,
				int split, double x_, double variability)
		{
			int dim = array.length;
			double SE = 0;
			double SSE = 0;
			double MINSSE = 0;			
			double q = start_q;
			double m = start_m;
			double c = start_c;
			double qvar = Math.abs(variability*q/split);
			double mvar = Math.abs(variability*m/split);
			double cvar = Math.abs(variability*c/split);
			double newq = q - ((variability/2) * Math.abs(q));
			double newm = m - ((variability/2) * Math.abs(m));
			double newc = c - ((variability/2) * Math.abs(c));




			MINSSE=1.0E10;

			for(int k=0; k< split; k++) {
				SSE = 0;
				newc = newc + cvar;
				for(int i=0; i< dim; i++) {    
					SE = array[i][1] - (m * array[i][0] + q + newc * Math.pow(array[i][0]-x_, 2));
					SSE = SSE + (SE*SE);

				}

				//System.out.println(newc + " : " + c + " : "+  SSE +"\n");

				if(k==1)
					MINSSE = SSE;

				if(SSE < MINSSE) {
					c = newc; 
					MINSSE = SSE;
				}

			}





			MINSSE=1.0E10;
			for(int k=0; k< split; k++) {
				SSE = 0;
				newm = newm + mvar;
				for(int i=0; i< dim; i++) {    
					SE = array[i][1] - (newm * array[i][0] + q + c * Math.pow(array[i][0]-x_, 2));
					SSE = SSE + (SE*SE);
				}

				if(k==1)
					MINSSE = SSE;

				if(SSE < MINSSE) {
					m = newm; 
					MINSSE = SSE;
				}
				//  System.out.println(newm + " : " + m + " : "+  SSE +"\n");		
			}






			MINSSE=1.0E10;

			for(int k=0; k< split; k++) {
				SSE = 0;
				newq = newq + qvar;
				for(int i=0; i< dim; i++) {    
					SE = array[i][1] - (m * array[i][0] + newq + c * Math.pow(array[i][0]-x_, 2));
					SSE = SSE + (SE*SE);
				}

				if(k==1)
					MINSSE = SSE;

				if(SSE < MINSSE) {
					q = newq; 
					MINSSE = SSE;
				}

				//System.out.println(newq + " : " + q + " : "+  SSE +"\n");

			}



			double[] r_val = {m,q, c};
			return(r_val);

		}





		////////////////////////////////////////////////////
		double[] check_linearity(double[][] mean_points) {
			double q=0;
			double m=0;
			double[] gof={0,0,0};
			double SE = 0;
			double SSE = 0;
			double MINSSE = 0;
			double SSTO = 0;
			double SR = 0;
			double SSR = 0;
			double y_ = 0;
			int dim = mean_points.length;

			double[] mymq = get_m_q(mean_points);
			m = mymq[0];
			q = mymq[1];

			for(int i=0; i< dim; i++) {
				y_ = y_ + mean_points[i][1];
			}

			y_ = y_ / dim;


			double[] a = optimize_linear_param(mean_points, m, q, 500, 4.0);
			//System.out.println("NEW FUNC y = " + a[0] + "x + " + a[1] + "\n");
			double[] b = optimize_linear_param(mean_points, a[0], a[1], 500, 0.2);
			//System.out.println("NEW FINE FUNC y = " + b[0] + "x + " + b[1] + "\n");

			m=b[0];
			q=b[1];
			/////////////////////////////////////////////////////////////////////

			if(LOG_LEVEL == 1)  
				System.out.println("MIN Q y = " + m + "x + " + q + "\n");

			SSE = 0;

			for(int i=0; i< dim; i++) {    
				SE = mean_points[i][1] - (m * mean_points[i][0] + q);
				SSE = SSE + (SE*SE);

				SR = mean_points[i][1] - y_; // variation around the mean
				SSR = SSR + (SR*SR);
			}


			SSTO = SSR + SSE;   
			gof[0] = 1 - SSE/SSTO;
			gof[1] = m;
			gof[2] = q;

			if(LOG_LEVEL == 1) {
				System.out.println("SSE: " + SSE + "\n");
				System.out.println("SSTO: " + SSTO + "\n");
				System.out.println("R2: " + gof[0] + "\n\n");


				if(gof[0] > 0.75)
					System.out.println("Linear scratch. " + "\n\n");
			}

			return(gof);			
		}


		//////////////////////////////////////////////////////////////////////////////////////
		double check_arc(double[][] mean_points) {
			double q;
			double m;
			double gof;
			double SE = 0;
			double SSE = 0;
			double SSTO = 0;
			double SR = 0;
			double SSR = 0;
			double y_ = 0;
			double x_ = 0;


			int dim = mean_points.length;

			if(dim < 4)
				return(0.0);

			/////  calcolo della retta  m, q //////////////////////////
			double[] mymq = get_m_q(mean_points);
			m = mymq[0];
			q = mymq[1];
			///////////////////////////////////////////////////////////


			for(int i=0; i< dim; i++) {
				y_ = y_ + mean_points[i][1];
				x_ = x_ + mean_points[i][0];
			}

			y_ = y_ / dim;
			x_ = x_ / dim;

			//calculate cx2 coeff			
			double c =0; 
			for(int v=0; v<dim; v++)
				c = c + ((mean_points[v][1] - m * mean_points[v][0] - q)/Math.pow(mean_points[v][0]-x_, 2));

			c = c/dim;

			if(LOG_LEVEL == 1)
				System.out.println("Starting y = " + m + "x + " + q + "  " + c + " *(x-xmean)2  " + x_ +"\n");


			/* ========================== MINIMI QUADRATI =========================*/			
			double[] a = optimize_quad_param(mean_points, m, q, c, 1000, x_, 3.0);
			//System.out.println("NEW FUNC y = " + a[0] + "x + " + a[1] + " " + a[2] + "\n");
			double[] b = optimize_quad_param(mean_points, a[0], a[1], a[2], 1000, x_,  1.0);
			//System.out.println("NEW FINE FUNC y = " + b[0] + "x + " + b[1] + "  " + b[2] +"\n");

			m=b[0];
			q=b[1];
			c=b[2];
			/* ====================================================================*/

			if(LOG_LEVEL == 1)
				System.out.println("y = " + m + "x + " + q + "  " + c + " *(x-xmean)2  " + x_ +"\n");

			SSE = 0;



			// CALCOLO DELL'R2 e' OK!!!
			for(int i=0; i< dim; i++) {    
				SE = mean_points[i][1] - (m * mean_points[i][0] + q + c * Math.pow(mean_points[i][0]-x_, 2));
				SSE = SSE + (SE*SE);

				SR = mean_points[i][1] - y_; // variation around the mean
				SSR = SSR + (SR*SR);
			}


			SSTO = SSR + SSE;   
			gof = 1 - SSE/SSTO;

			if(LOG_LEVEL == 1) {
				System.out.println("SSE: " + SSE + "\n");
				System.out.println("SSTO: " + SSTO + "\n");
				System.out.println("R2: " + gof + "\n\n");

				if(gof > 0.75)
					System.out.println("Arc scratch. " + "\n\n");
			}

			return(gof);			
		}



		//////////////////////////////////////////////////////
		double check_center_clust(double[][] mean_points, double fact) {
			int dim = mean_points.length;
			double gof;
			double SE = 0;
			double SSE = 0;
			double SSTO = 0;
			double SR = 0;
			double SSR = 0;
			double fac = fact;			
			double dx = 15*fac;
			double dy = 15*fac;			
			double cx = 100 * fac;
			double cy = 100 * fac;

			Ellipse2D.Double  cs = new Ellipse2D.Double(cx-dx/2, cy-dy/2,  dx , dy);

			for(int i=0; i< dim; i++) {    

				if(cs.contains(mean_points[i][0], mean_points[i][1])) {
					SSTO++;
				} else {
					SSE++;
				}
			}


			gof = 1 - SSE/SSTO;

			if(LOG_LEVEL == 1) {
				System.out.println("SSE: " + SSE + "\n");
				System.out.println("SSTO: " + SSTO + "\n");
				System.out.println("R2: " + gof + "\n\n");

				if(gof > 0.75)
					System.out.println("Center spot " + "\n\n");
			}

			return(gof);


		}



		double check_big_clust(double[][] mean_points) {			
			double gof;
			int dim = mean_points.length;

			if(dim > 15)
				gof=1;
			else
				gof = 0;

			return(gof);

		}



	} // end of class

	//////////////////////////////////////////////////////////////////////////////////////






	/*************************************
	 *
	 *************************************/
	class array_matrix {

		String name;
		double[][] matrix;
		int defect_num = 0;      

		String REGEX="\\,";
		String INPUT;
		String[] value;
		int i,lnum = 0;
		int MAX_BUFF_DEF_SIZE = 4000;

		public array_matrix(String f_name) {
			name = f_name;


			try {				
				Pattern pattern = Pattern.compile(REGEX);				
				BufferedReader br = new BufferedReader(new FileReader(name));   

				while (( (INPUT = br.readLine()) != null))            
					lnum++;

				br.close();

				defect_num = lnum;
				matrix = new double[lnum][2];
				System.out.println(lnum);

				BufferedReader br2 = new BufferedReader(new FileReader(name));
				while (( (INPUT = br2.readLine()) != null))
				{            
					value = pattern.split(INPUT);
					matrix[i][0] = Double.valueOf(value[0]).doubleValue();
					matrix[i][1] = Double.valueOf(value[1]).doubleValue();

					i++;
				} 

				br2.close();

			} catch (Exception e) {
				System.out.println("In array_maytrix: " + f_name + e);
			}			
		}


		public String get_name() {
			return(name);
		}


		public double[][] get_mat() {

			return(matrix);
		}


		public int get_def_num() {
			return(defect_num);
		}


	}




	/*************************************
	 *
	 *************************************/
	public class get_shade_db_data {		
		File db_dir;
		String[] db_files;
		int  file_num;
		array_matrix[] am;

		//costruttore
		public get_shade_db_data(String name)
		{     
			db_dir = new File(name);
			db_files = db_dir.list();
			file_num = db_files.length;
			am = new array_matrix[file_num];

			for(int j=0; j< file_num; j++) {
				System.out.println(db_files[j]);
				am[j] = new array_matrix(db_dir + "/" + db_files[j]);
			}

		}



		public array_matrix[] get_array_mat() {        
			return(am);
		} 


		public String get_file_name(int pos){
			return(db_files[pos]);
		}

	}





	/*************************************
	 *
	 *************************************/
	class getmap 
	{
		Color bg = Color.white;  
		double fact = 1;
		BufferedImage bi; 
		int df;
		double[][] mycoord;

		public getmap(double[][] coord)
		{  
			bi = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
			df = coord.length;   
			mycoord = coord;   			
		}


		public BufferedImage getImg() {			
			Graphics2D g2 = bi.createGraphics(); 
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);             
			Color fg3D = Color.green;			
			double xwf = 200 * fact;
			double ywf = 200 * fact;			
			RectangularShape E1 = new Ellipse2D.Double(0, 0, xwf, ywf);
			g2.fill(E1);
			g2.setPaint(Color.white);
			g2.draw(E1);
			g2.clip(E1);

			for(int k=0; k<df; k++){            

				double margin = 0.1 * fact;
				g2.setPaint(Color.blue);

				//g2.draw(new Ellipse2D.Double(( 200 - ((mycoord[k][0]/1000000)) ) * fact,  ( (mycoord[k][1]/1000000) ) * fact, margin , margin));
				g2.draw(new Ellipse2D.Double(( ((mycoord[k][0]/1000000)) ) * fact,  ( (mycoord[k][1]/1000000) ) * fact, margin , margin));

			}   

			g2.dispose();
			return(bi);

		}
	}



	/*************************************
	 *
	 *************************************/
	class getmap_centered 
	{
		Color bg = Color.white;  
		double fact;
		BufferedImage bi; 
		int df;
		double[][] mycoord;
		double xwf ;
		double ywf ;
		double x_off;
		double y_off;

		public getmap_centered(double[][] coord, double fac, double xof, double yof)
		{  
			fact = fac;
			xwf = 200 * fact;
			ywf = 200 * fact;
			//x_off = (100000000- xof) *fact/1000000;
			//y_off = -(100000000- yof) * fact/1000000;
			bi = new BufferedImage((int)xwf, (int)ywf, BufferedImage.TYPE_INT_RGB);
			df = coord.length;   
			mycoord = coord;      
		}


		public BufferedImage getImg() {
			Graphics2D g2 = bi.createGraphics(); 
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     
			Color fg3D = Color.green;

			RectangularShape E1 = new Ellipse2D.Double(0, 0, xwf, ywf);
			g2.fill(E1);
			g2.setPaint(Color.white);
			g2.draw(E1);
			g2.clip(E1);

			//g2.setPaint(Color.blue);
			for(int k=0; k<df; k++){            
				double margin = 0.1 * fact;

				if(mycoord[k][2] == 1.0)
					g2.setPaint(Color.blue);
				else
					g2.setPaint(Color.green);


				if(mycoord[k][3] == 0)
					g2.setPaint(Color.blue);
				else
					g2.setPaint(Color.red);



				g2.draw(new Ellipse2D.Double(mycoord[k][0] , mycoord[k][1], 
						margin , margin));
			}   

			g2.dispose();
			return(bi);

		}
	}








	/*************************************
	 *
	 *************************************/
	class getmap_2 
	{
		Color bg = Color.white;  
		double fact;
		BufferedImage bi; 
		int df;
		double[][] mycoord;
		double xwf ;
		double ywf ;

		public getmap_2(double[][] coord, double fac)
		{  
			fact = fac;
			xwf = 200 * fact;
			ywf = 200 * fact;
			bi = new BufferedImage((int)xwf, (int)ywf, BufferedImage.TYPE_INT_RGB);
			df = coord.length;   
			mycoord = coord;      
		}


		public BufferedImage getImg() {
			Graphics2D g2 = bi.createGraphics(); 
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     
			Color fg3D = Color.green;

			RectangularShape E1 = new Ellipse2D.Double(0, 0, xwf, ywf);
			g2.fill(E1);
			g2.setPaint(Color.white);
			g2.draw(E1);
			g2.clip(E1);

			//g2.setPaint(Color.blue);
			for(int k=0; k<df; k++){            
				double margin = 0.1 * fact;

				if(mycoord[k][2] == 1.0)
					g2.setPaint(Color.blue);
				else
					g2.setPaint(Color.green);


				if(mycoord[k][3] == 0)
					g2.setPaint(Color.blue);
				else
					g2.setPaint(Color.red);

				//System.out.println(" : " + mycoord[k][0]+" : " +mycoord[k][1]+" : "+mycoord[k][2]);


				g2.draw(new Ellipse2D.Double(mycoord[k][0] , mycoord[k][1], 
						margin , margin));
			}   

			g2.dispose();
			return(bi);

		}
	}







	/*************************************
	 *
	 *************************************/
	class getmap_3 
	{
		Color bg = Color.white;  
		double fact = 2;
		double xwf = 200 * fact;
		double ywf = 200 * fact;
		BufferedImage bi; 
		int df;
		double[][] mycoord;
		double[][] defxy;


		public getmap_3(double[][] coord, double[][] mydefxy)
		{  
			bi = new BufferedImage((int)xwf+2, (int)ywf+2, BufferedImage.TYPE_INT_RGB);
			df = coord.length;   
			mycoord = coord;         
			defxy = mydefxy;
		}



		public BufferedImage getImg() {
			Graphics2D g2 = bi.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     
			//Color fg3D = Color.green;
			RectangularShape E1 = new Ellipse2D.Double(0, 0, xwf, ywf);  
			g2.fill(E1);        
			//g2.setPaint(Color.white);
			g2.draw(E1);
			g2.clip(E1);
			int clustsize = defxy.length;	    
			int XMIN = 0;
			int XMAX = 0;
			int YMIN = 0;
			int YMAX = 0;
			double X_EXT = 0.0;
			double Y_EXT = 0.0;

			for(int a=0; a< clustsize; a++) {
				if(defxy[a][0] < defxy[XMIN][0])
					XMIN = a;
				if(defxy[a][0] > defxy[XMAX][0])
					XMAX = a;
				if(defxy[a][1] < defxy[YMIN][1])
					YMIN = a;
				if(defxy[a][1] > defxy[YMAX][1])
					YMAX = a;
			}

			X_EXT = defxy[XMAX][0] - defxy[XMIN][0];
			Y_EXT = defxy[YMAX][1] - defxy[YMIN][1]; 

			Rectangle2D.Double rg;     
			rg = new Rectangle2D.Double((defxy[XMIN][0]-5)*fact ,(defxy[YMIN][1]-5)*fact, (X_EXT+10)*fact, (Y_EXT+10)*fact);

			g2.setPaint(Color.red);
			g2.draw(rg);

			for(int k=0; k<df; k++){            
				double margin = 0.1;
				if(mycoord[k][2] == 1.0)
					g2.setPaint(Color.blue);
				else
					g2.setPaint(Color.red);

				//g2.draw(new Ellipse2D.Double(( 200 - ((mycoord[k][0]/1000000)) ) * fact,  ( (mycoord[k][1]/1000000) ) * fact, margin , margin));
				g2.draw(new Ellipse2D.Double((((mycoord[k][0]/1000000)) ) * fact,  ( (mycoord[k][1]/1000000) ) * fact, margin , margin));
			}   

			g2.dispose();
			return(bi);
		}
	}





	/*********************************
	 *     db connection & util
	 *********************************/   
	public class db
	{
		Connection con = null;
		Connection f3con = null;
		Connection f9con = null;
		Statement stmt;

		public Connection get_connection ()
		{   
			try {

				DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
				System.out.println("Driver reg OK..." );

				// Step 2
				System.out.println("Connecting to F9 klarity..."); 				
				f9con = DriverManager.getConnection
						("jdbc:oracle:thin:@azklarity01:1521:udbfab9", 
								"udb", "udb");
				System.out.println("Connected.");   

				//	System.out.println("Connecting to F3 klarity...");
				//	f3con = DriverManager.getConnection
				//			("jdbc:oracle:thin:@f3klarity:1521:udbprod", 
				//					"udb", "udb");
				//	System.out.println("Connected."); 

				//	System.out.println("Setting default DB to F9..." );         
				con = f9con; 
				DB = "F9";

				// Step 3
				System.out.println("Creating statement...");   
				stmt = con.createStatement();    
				System.out.println("DONE"); 

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			} catch (java.lang.Exception exl) {
				System.out.println("lang excp: " +exl.getMessage());
			}

			return(con); 
		}   



		public Connection change_server_connection(String fab)
		{
			try {
				switch(fab){
				case "F9":
					con = f9con; 
					DB = fab;
					stmt.close();
					stmt = con.createStatement(); 
					break;

				case "F3":
					con = f3con; 
					DB = fab;
					stmt.close();
					stmt = con.createStatement(); 
					break;	

				default:
					System.out.println("Ucknown FAB...");
					break;

				}


			} catch (java.lang.Exception exl) {
				System.out.println("lang excp: " +exl.getMessage());
			}

			return(con); 
		}


		/************************************************************
		 * Ritorna la matrice x,y delle posizioni dei defects
		 ************************************************************/
		public double[][] get_db_data(String isp_t, String wafer_k, String defects, double cx, double cy, double dix, double diy)
		{
			String DOT="\\.";
			int xdim = Integer.parseInt(defects);
			double[][] matrix = new double[xdim][12];
			double dx = (100000000 - cx);
			double dy = (100000000 - cy);

			//System.out.println("dx=dy: "+dx+","+dy);
			//double die_dim[] = klar.get_diesize(wafer_k, isp_t);
			//double dix = die_dim[0];
			//double diy = die_dim[1];
			boolean flip = false;

			try {
				Pattern pattern = Pattern.compile(DOT);
				String[] str = pattern.split(isp_t);
				//Statement stmt = con.createStatement();

				String mydev = get_device_bywk(wafer_k, str[0], stmt);
				String mystep = get_step_bywk(wafer_k, str[0], stmt);
				System.out.println(mystep);

				flip = need_flip(mydev, mystep);

				String SQL_stmt = "SELECT wafer_x, wafer_y, adder, INDEX_X, INDEX_Y, DEFECT_ID, CLASS_NUMBER, SIZE_D, AREA, IMAGES from insp_defect " +
						"WHERE inspection_time = TO_DATE ('" + str[0] + "', 'YYYY/MM/DD HH24:MI:SS')" +
						" AND WAFER_KEY = " + wafer_k; 

				ResultSet rs = stmt.executeQuery(SQL_stmt);

				int count = 0;

				while (rs.next()) {
					String sx = rs.getString("wafer_x");
					String sy = rs.getString("wafer_y");
					String ad = rs.getString("adder");
					String di = rs.getString("INDEX_X");
					String dj = rs.getString("INDEX_Y");
					String defid = rs.getString("DEFECT_ID");
					String defclass = rs.getString("CLASS_NUMBER"); 
					String size = rs.getString("SIZE_D");
					String area = rs.getString("AREA");
					String img_count = rs.getString("IMAGES");

					//System.out.println("did: "+defid);
					//System.out.println("sx: "+sx);

					//calculate location indie					
					//	double dox = Double.valueOf(sx).doubleValue() - Double.valueOf(di).doubleValue() * dix;
					//	double doy = Double.valueOf(sy).doubleValue() - Double.valueOf(dj).doubleValue() * diy;

					// per eccezione qu coordinate 0.0, 0.0
					if(sx == null)
						sx = "0.0";
					if(sy == null)
						sy = "0.0";

					if(size == null)
						size = "0.0";

					if(area == null)
						area = "0.0";

					double n_sx = 	Double.valueOf(sx).doubleValue();
					double n_sy = 	Double.valueOf(sy).doubleValue();

					int new_di = (int)(n_sx/dix);
					int new_dy = (int)(n_sy/diy);

					double dox = n_sx - new_di * dix;
					double doy = n_sy - new_dy * diy;

					// x la CSAM dx e' grande
					if(mystep.equals("C4BX_2000")||mystep.equals("C4BD_2000")){
						new_di = (int)((n_sx+dx)/dix);
						new_dy = (int)((n_sy+dy)/diy);

						dox = (n_sx+dx) - new_di * dix;
						doy = (n_sy+dy) - new_dy * diy;	
					}

					//System.out.println("new i,j= "+new_di+ ","+new_dy);					
					//System.out.println(defid+": "+di + ","+ dj+ ","+sx+ ","+sy+ ","+dox+ ","+doy);


					// DON'T TOUCH THE POSITION IN MATRIX BUT APPEND

					if(flip)
						matrix[count][0] = 200000000 -(n_sx + dx);
					else
						matrix[count][0] = n_sx + dx;

					matrix[count][1] = 200000000 - (n_sy + dy);      
					matrix[count][2] = Double.valueOf(ad).doubleValue();   
					matrix[count][3] = dox; // x in die
					matrix[count][4] = doy; // y in die
					//matrix[count][5] = Double.valueOf(di).doubleValue();  // die i
					//matrix[count][6] = Double.valueOf(dj).doubleValue();;  // die j
					matrix[count][5] = new_di;
					matrix[count][6] = new_dy;
					matrix[count][7] = Double.valueOf(defid).doubleValue();;  // defect id
					matrix[count][8] = Double.valueOf(defclass).doubleValue();;  // defect id
					matrix[count][9] = Double.valueOf(size).doubleValue();;  // defect id
					matrix[count][10] = Double.valueOf(area).doubleValue();;  // area
					matrix[count][11] = Double.valueOf(img_count).doubleValue();
					count++;                         
				}

				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}


			//String p = get_idr("C1EA", 6297679.0, 4369713.0);
			//String p = get_idr("C1EA", matrix[0][3], matrix[0][4]);
			//System.out.println(p);

			return matrix;
		}



		// return region name by defect coordinaates
		public String get_idr(String dev, double x, double y)
		{
			String ret = "none";

			try{
				//Statement stmt = con.createStatement();
				String SQL_stmt = "SELECT IDRTYPE, IDRSTARTX, IDRSTARTY, IDRWIDTH, IDRHEIGHT from IDR_DEVICE_SCHEME " +
						" WHERE DEVICEID="+ "\'" + dev + "\'";


				ResultSet rs = stmt.executeQuery(SQL_stmt);

				while (rs.next()) {
					String type = rs.getString("IDRTYPE");
					double x_ini =  Double.valueOf(rs.getString("IDRSTARTX")).doubleValue();
					double y_ini =  Double.valueOf(rs.getString("IDRSTARTY")).doubleValue();
					double w =  Double.valueOf(rs.getString("IDRWIDTH")).doubleValue();
					double h =  Double.valueOf(rs.getString("IDRHEIGHT")).doubleValue();

					Rectangle2D.Double rg = new Rectangle2D.Double(x_ini, y_ini, w, h);
					if(rg.contains(x/1000,y/1000))
						return type;

				}

				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}


			return ret;


		}



		////////////////////////////////////////////
		public boolean need_flip(String dev, String step) {
			boolean  ret = false;
			String[] nof_steps = {"80_BOND_CMP","80_BOND_TRIM","C4BX_89_SCRIBE",
					"C4BX_76_ACI","C4BX_77_ACI","C4BX_77_SCRIBE_2",
					"C4BX_78_ACI","C4BX_79_PASS","C4BX_80_SI_ANNEAL","C4BX_80_SI_CMP","C4BX_80_SI_WETETCH",

					"C4BD_89_SCRIBE",
					"C4BD_76_ACI","C4BD_77_ACI","C4BD_77_SCRIBE_2", "C4BD_77_PRECOAT_PM",
					"C4BD_78_ACI","C4BD_79_PASS","C4BD_80_SI_ANNEAL","C4BD_80_SI_CMP","C4BD_80_SI_WETETCH"
			};


			if(dev.equals("C4BX")||dev.equals("C4BD")) {
				ret = true;
				for(int i=0; i < nof_steps.length; i++)
					if(step.equals(nof_steps[i]))
						return(false);
			}
			return ret;
		}


		/////////////////////////////////////////////
		public String get_device(String lot)
		{
			String dev = "null";


			try{
				//Statement stmt = con.createStatement();
				String SQL_stmt = "SELECT DEVICE from insp_wafer_summary " +
						" WHERE LOT_ID="+ "\'" + lot + "\'";


				ResultSet rs = stmt.executeQuery(SQL_stmt);

				while (rs.next()) {
					dev = rs.getString("DEVICE");
					if(!dev.equals("NONE"))
						return dev;
				}

				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}

			return dev;
		}



		public String get_device_bywk(String wk, String time, Statement stmt2)
		{
			String dev = "null";


			try{
				//Statement stmt = con.createStatement();
				String SQL_stmt = "SELECT DEVICE from insp_wafer_summary " +
						"WHERE inspection_time = TO_DATE ('" + time + "', 'YYYY/MM/DD HH24:MI:SS')" +
						" AND WAFER_KEY = " + wk; 


				ResultSet rs = stmt.executeQuery(SQL_stmt);

				while (rs.next()) {
					dev = rs.getString("DEVICE");
					if(!dev.equals("NONE"))
						return dev;
				}

				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}

			return dev;
		}




		public String get_step_bywk(String wk, String time, Statement stmt2)
		{
			String dev = "null";


			try{
				//Statement stmt = con.createStatement();
				String SQL_stmt = "SELECT LAYER_ID from insp_wafer_summary " +
						"WHERE inspection_time = TO_DATE ('" + time + "', 'YYYY/MM/DD HH24:MI:SS')" +
						" AND WAFER_KEY = " + wk; 



				ResultSet rs2 = stmt.executeQuery(SQL_stmt);

				while (rs2.next()) {
					dev = rs2.getString("LAYER_ID");
					if(!dev.equals("NONE"))
						return dev;
				}

				rs2.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}

			return dev;
		}



		public String get_class(String id)
		{
			String dev = "null";


			try{
				//Statement stmt = con.createStatement();
				String SQL_stmt3 = "SELECT NAME from CLASS " +
						"WHERE CLASS_ID = " + id; 

				ResultSet rs = stmt.executeQuery(SQL_stmt3);

				while (rs.next()) {
					dev = rs.getString("NAME");
				}

				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}

			return dev;
		}		



		public String get_img_name(String wafer_k, String time, String def_id)
		{
			String img = "null";
			String time2 = time.substring(0,19);


			try{
				//Statement stmt = con.createStatement();
				String SQL_stmt3 = "SELECT IMAGE_FILESPEC from INSP_WAFER_IMAGE " +
						"WHERE INSPECTION_TIME = TO_DATE ('" + time2 + "', 'YYYY/MM/DD HH24:MI:SS')" +
						" AND WAFER_KEY = " + wafer_k +
						" AND IMAGE_ID = 0" +
						" AND DEFECT_ID = " + def_id  
						; 


				ResultSet rs = stmt.executeQuery(SQL_stmt3);

				while (rs.next()) {
					img = rs.getString("IMAGE_FILESPEC");
				}

				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}

			return img;
		}	



		public BufferedImage get_sem2(String def_id, String layer, String time, String w_key)
		{
			String img;
			BufferedImage SEMimage =  null;
			String sem_file = "sem/sem.jpg";

			img = klar.get_img_name(w_key, time, def_id);
			//System.out.println("image: " + img);
			ftp my_ftp = new ftp(img, "F9");


			if(!sem_file.equals("null")){
				try {
					SEMimage = ImageIO.read(new File(sem_file));

					//NEW  //////////////////////////////
					//	String n = "sem/ALL/"+layer+"_"+ w_key+def_id+".jpg";
					//	File file = new File(n);
					//	ImageIO.write(SEMimage, "jpeg", file);
					/////////////////////////////////////////////////
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}


			return(SEMimage);
		}


		public double[] get_diesize(String wafer_k, String time)
		{
			String rk = "null";
			double ddim[] = new double[6];
			//per togliere il .0
			String time2 = time.substring(0,19);

			try{
				//Statement stmt = con.createStatement();
				String SQL_stmt = "SELECT RECIPE_KEY from insp_wafer_summary " +
						"WHERE inspection_time = TO_DATE ('" + time2 + "', 'YYYY/MM/DD HH24:MI:SS')" +
						" AND WAFER_KEY = " + wafer_k; 

				ResultSet rs = stmt.executeQuery(SQL_stmt);


				while (rs.next()) {
					rk = rs.getString("RECIPE_KEY");				                         
				}


				String SQL_stmt2 = "SELECT DIE_SIZE_X, DIE_SIZE_Y, ORIGIN_DIE_X, ORIGIN_DIE_Y, ORIGIN_INDEX_X, ORIGIN_INDEX_Y  from insp_recipe " +
						"WHERE RECIPE_KEY = " + rk; 

				rs = stmt.executeQuery(SQL_stmt2);
				while (rs.next()) {
					String dix = rs.getString("DIE_SIZE_X");
					String diy = rs.getString("DIE_SIZE_Y");
					String odix = rs.getString("ORIGIN_DIE_X");
					String odiy = rs.getString("ORIGIN_DIE_Y");
					String odindx = rs.getString("ORIGIN_INDEX_X");
					String odindy = rs.getString("ORIGIN_INDEX_Y");
					ddim[0] = Double.valueOf(dix).doubleValue();
					ddim[1] = Double.valueOf(diy).doubleValue();
					ddim[2] = Double.valueOf(odix).doubleValue();
					ddim[3] = Double.valueOf(odiy).doubleValue();
					ddim[4] = Double.valueOf(odindx).doubleValue();
					ddim[5] = Double.valueOf(odindy).doubleValue();
					//System.out.println("die size x-y : "+ ddim[0] +","+ddim[1]+ "\n");
					//System.out.println("orig die xy: "+ ddim[2] +","+ddim[3]+ "\n");
					//System.out.println("die orig index: "+ddim[4]+","+ddim[5]+ "\n");
				}


				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}

			return ddim;
		}






		public double[] get_origxy(String wafer_k, String time)
		{
			String rk = "null";
			double ddim[] = new double[2];
			ddim[0] = 0;
			ddim[1] = 0;

			try{
				//Statement stmt = con.createStatement();

				//per togliere il .0
				String time2 = time.substring(0,19);


				String SQL_stmt = "SELECT ORIGIN_X, ORIGIN_Y from insp_wafer_summary " +
						"WHERE inspection_time = TO_DATE ('" + time2 + "', 'YYYY/MM/DD HH24:MI:SS')" +
						" AND WAFER_KEY = " + wafer_k; 


				ResultSet rs = stmt.executeQuery(SQL_stmt);

				while (rs.next()) {
					String dix = rs.getString("ORIGIN_X");
					String diy = rs.getString("ORIGIN_Y");

					ddim[0] = Double.valueOf(dix).doubleValue();
					ddim[1] = Double.valueOf(diy).doubleValue();
					System.out.println("ORIGIN_X,Y: "+ ddim[0] +","+ddim[1]+ "\n");
				}


				rs.close();
				//stmt.close();

			} catch (SQLException ex) {
				System.out.println("SQL excp: "+ ex.getMessage());
			}

			return ddim;
		}

	}





	/*********************************
	 *     electrical data db connection
	 *********************************/   
	public class probe_db
	{
		Connection con2 = null;

		public Connection get_connection ()
		{   
			try {


				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				System.out.println("PROBE Driver reg OK...");

				//con2 = DriverManager.getConnection("jdbc:sqlserver://aiwymsdb;instanceName=SQLEXPRESS;database=Yms;user=ymsrdauser;password=ymsrdauser123");
				con2 = DriverManager.getConnection("jdbc:sqlserver://aiwymsdb;user=ymsrdauser;password=ymsrdauser123");
				Statement stmt = con2.createStatement();
				System.out.println("PROBE connection OK...");



			} catch (Exception exl) {
				System.out.println(" excp: " +exl.getMessage());
			}

			return(con2); 
		}   



		public void displayDbProperties(Connection con){
			java.sql.DatabaseMetaData dm = null;
			java.sql.ResultSet rs = null;


			try{

				if(con!=null){
					dm = con.getMetaData();
					System.out.println("Driver Information");
					System.out.println("\tDriver Name: "+ dm.getDriverName());
					System.out.println("\tDriver Version: "+ dm.getDriverVersion ());
					System.out.println("\nDatabase Information ");
					System.out.println("\tDatabase Name: "+ dm.getDatabaseProductName());
					System.out.println("\tDatabase Version: "+ dm.getDatabaseProductVersion());
					System.out.println("Avalilable Catalogs ");
					rs = dm.getCatalogs();
					while(rs.next()){
						System.out.println("\tcatalog: "+ rs.getString(1));
					} 
					rs.close();
					rs = null;

				}else System.out.println("Error: No active Connection");
			}catch(Exception e){
				e.printStackTrace();
			}
			dm=null;
		}     





		public String getpartfromlot(Connection con, String lot){

			String part = "null";

			try{

				if(con!=null){
					Statement stmt = con.createStatement();
					String SQL_stmt = "SELECT FldPartType from TblLotProbe " +
							"WHERE FldLot = '"+ lot +"'" ; 

					ResultSet rs = stmt.executeQuery(SQL_stmt);


					int dim =0;
					while(rs.next()){
						//   System.out.println("part "+ rs.getString(1));
						part = rs.getString(1);
						dim++;
					} 

					//System.out.println("probe data dimention= "+ dim);
					rs.close();
					rs = null;

				}else System.out.println("Error: No active Connection");
			}catch(Exception e){
				e.printStackTrace();
			}

			return part;
		}  





		public void getDieMatrix(Connection con){

			try{

				if(con!=null){
					Statement stmt = con.createStatement();
					String SQL_stmt = "SELECT FldI, FldJ, FldKind from TblDieKind " +
							"WHERE FldPartType = '7482009.009'" ; 

					ResultSet rs = stmt.executeQuery(SQL_stmt);


					int dim =0;
					while(rs.next()){
						System.out.println("probe data "+ rs.getString(1) + "," +rs.getString(2)+ "," +rs.getString(3));
						dim++;
					} 

					System.out.println("probe data dimention= "+ dim);
					rs.close();
					rs = null;

				}else System.out.println("Error: No active Connection");
			}catch(Exception e){
				e.printStackTrace();
			}

		}     




		public String getDieType(Connection con, String lot, String wafer, String Di, String Dj){
			String ret = "null";
			try{

				if(con!=null){
					Statement stmt = con.createStatement();

					String SQL_stmt = "SELECT FldKind from TblDieKind " +
							"WHERE FldPartType = '" + probe.getpartfromlot(con, lot) + "'" +
							" AND FldI=" + Di + " AND FldJ=" + Dj	; 	
					ResultSet rs = stmt.executeQuery(SQL_stmt);


					int dim =0;
					while(rs.next()){
						//System.out.println("die flavor: "+ rs.getString(1) );
						ret = rs.getString(1);
						dim++;
					} 


					rs.close();
					rs = null;

				}else System.out.println("Error: No active Connection");
			}catch(Exception e){
				e.printStackTrace();
			}

			return(ret);
		}   






		public String getfailbin(Connection con, String lot, String wafer, String Di, String Dj){
			String ret= "null";
			try{

				if(con!=null){
					Statement stmt = con.createStatement();

					String SQL_stmt = " select dp.FldFailBin + ':' + dp.FldFailError as FldFail" + 
							" from  DbaLfoundryProbe.dbo.TblDie dp"+
							" where" +
							" dp.FldLot = '"+ lot +"'"+
							" and dp.FldWafer = '"+ wafer+ "'"+ 
							" and dp.FldJ = " + Di +
							" and dp.FldI = " + Dj ; 	

					ResultSet rs = stmt.executeQuery(SQL_stmt);

					String die_type = probe.getDieType(con,lot,wafer,Di,Dj);
					System.out.println("die_type: " + Di +","+Dj+"      "+ die_type);

					if(die_type.equals(".")||die_type.equals("<")||die_type.equals("#"))
						ret="..";

					int dim =0;
					while(rs.next()){
						String bin = rs.getString(1);
						System.out.println("fail bin/type: "+ bin + " " + die_type);
						ret = bin;

						dim++;
					} 

					// System.out.println("probe data dimention= "+ dim);
					rs.close();
					rs = null;

				}else System.out.println("Error: No active Connection");
			}catch(Exception e){
				e.printStackTrace();
			}

			return(ret);
		}
		/////////////////////////////////////////     



		public String getfailbinout(Connection con, String lot, String wafer, String Di, String Dj){
			String ret= "null";
			try{

				if(con!=null){
					Statement stmt = con.createStatement();

					String SQL_stmt = " select FldFailBinOut" + 
							" from dbo.TblDieProbe" +
							" where" +
							" FldLot = '"+ lot +"'"+
							" and FldWafer = '"+ wafer+ "'"+ 
							" and FldJ = " + Di +
							" and FldI = " + Dj ; 	

					ResultSet rs = stmt.executeQuery(SQL_stmt);

					// String die_type = probe.getDieType(con,lot,wafer,Di,Dj);
					//System.out.println("die_type: " + Di +","+Dj+"      "+ die_type);

					//if(die_type.equals(".")||die_type.equals("<")||die_type.equals("#"))
					//     ret="..";

					int dim =0;
					while(rs.next()){
						String bin = rs.getString(1);
						System.out.println("fail binout: "+ bin);
						ret = bin;

						dim++;
					} 

					// System.out.println("probe data dimention= "+ dim);
					rs.close();
					rs = null;

				}else System.out.println("Error: No active Connection");
			}catch(Exception e){
				e.printStackTrace();
			}

			return(ret);
		}
		/////////////////////////////////////////     




	}








	/*************************************
	 *
	 *************************************/
	class ProbeMap extends JPanel 
	{
		Color bg = Color.white;
		Connection con = null;
		BufferedImage imc2;  
		String lot, wafer;

		public ProbeMap(Connection conn, String l, String w)
		{     
			con = conn;
			lot = l;
			wafer= w;
		}



		public void paintComponent(Graphics g) {
			super.paintComponent(g); 

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     

			Color fg3D = Color.white;

			g2.setBackground(Color.white);

			double xwafer =200;
			double ywafer =200;
			double xdie = 40.0 ;
			double ydie = 40.5;
			double sx=0, sy =0;

			int step = 10;
			/*
		    	RectangularShape E1 = new Ellipse2D.Double(0, 0, xwafer, ywafer);
			g2.fill(E1);
			g2.setPaint(Color.white);
			g2.draw(E1);
			 */


			try{

				if(con!=null){
					Statement stmt = con.createStatement();
					String SQL_stmt = "SELECT FldI, FldJ, FldKind from TblDieKind " +
							"WHERE FldPartType = '" + probe.getpartfromlot(con, lot) + "'" ; 

					ResultSet rs = stmt.executeQuery(SQL_stmt);


					int dim =0;
					double ioffset=0, joffset =0;

					while(rs.next()){

						if(dim==0){
							ioffset = Math.abs(Double.valueOf(rs.getString(1)).doubleValue()) ;  
							joffset = Math.abs(Double.valueOf(rs.getString(2)).doubleValue()) ;  

						}   

						sx = Double.valueOf(rs.getString(1)).doubleValue() + ioffset;  
						sy = Double.valueOf(rs.getString(2)).doubleValue() + joffset;  

						g2.setPaint(Color.gray);  

						Rectangle2D.Double rg = new Rectangle2D.Double(sx*xdie,sy*ydie, xdie, ydie);

						/*


						if(rs.getString(3).equals(".")){    
							g2.setPaint(Color.gray);                                

						}
						else 
						{
							g2.setPaint(Color.gray);                                
							g2.fill(rg);
						}
						 */     
						// if(rs.getString(3).equals("."))


						System.out.println("probe data "+ sx + "," +sy);

						String mybin = probe.getfailbin(probe_db_con, lot, wafer, rs.getString(1), rs.getString(2));
						//g2.drawString(rs.getString(1)+"," +rs.getString(2),  (float)(sx*xdie + xdie), (float)(sy*ydie + ydie));
						if(mybin != "null"){
							g2.setPaint(Color.black);  
							g2.draw(rg); 
							g2.setPaint(Color.blue); 
							g2.drawString(mybin,  (float)(sx*xdie + xdie/6), (float)(sy*ydie + ydie/1.5));

						}
						System.out.println("bin value "+ mybin);

						dim++;
					} 




					System.out.println("probe data dimention= "+ dim);

					rs.close();
					rs = null;

				}else System.out.println("Error: No active Connection");
			}catch(Exception e){
				e.printStackTrace();
			}

		}     

	}











	/*********************************
	 *             Main
	 *********************************/   
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(currentLookAndFeel);
		} catch (Exception ex) {
			System.out.println("Failed loading L&F: " + currentLookAndFeel);
		}

		ssa skeleton = new ssa();
		JFrame frame = new JFrame("Pixel matching");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add a menu bar to the frame
		frame.setJMenuBar(skeleton.MenuBar);
		frame.setContentPane(skeleton.mainPanel);

		frame.setLocation(5,5);

		frame.pack();
		frame.setSize(new Dimension(1150,800));
		frame.setVisible(true); 
	}	

}

