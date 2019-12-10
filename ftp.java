import java.net.*;
import java.io.*;

public class ftp {
	String img;
	String currentdb;
		
	
	public ftp(String myimg, String db){
	
	img = myimg;
	currentdb = db;
	w_file();
	getimg();
	};
	
	
	
	public void w_file()
	{
		String file = "ftpCommands.txt";
		File logfile = new File(file);
		
		
		try{
			boolean fc  = logfile.createNewFile();
			BufferedWriter brw = new BufferedWriter(new FileWriter(logfile));
			if(currentdb.equals("F9"))
				brw.write("open azklarity01");
			
			if(currentdb.equals("F3"))
				brw.write("open f3klarity"); 
			
			brw.newLine();
			brw.write("udb"); brw.newLine();		
			brw.write("udb"); brw.newLine();	
			brw.write("bin"); brw.newLine();			
			brw.write("get " + img + " sem/sem.jpg"); brw.newLine();	
			brw.write("bye"); brw.newLine();	
			brw.flush();
			brw.close();
		} catch (Exception ex) {
				System.out.println(ex)	;
			}
	}
	
	
		public void getimg() {
		try {	
			
			//ftp my_ftp = new ftp("/kla-tencor/data/images/azklarity/20120507/15/D050712@155952W0002390741F00000039I00.jpg");
			
			Runtime runtimeContext = Runtime.getRuntime();

			String[] myCall = {
				"ftp",
				"-s:ftpCommands.txt"
			};

			Process newProcess = runtimeContext.exec(myCall);
			newProcess.waitFor();
			
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}