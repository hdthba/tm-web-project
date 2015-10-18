package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tm.ArgPackage;
import tm.AttentionFrame;
import tm.SwingInputter;
import tm.cpp.CPlusPlusLangPIFactory;
import tm.evaluator.Evaluator;
import tm.evaluator.Evaluator.Refreshable;
import tm.interfaces.CommandInterface;
import tm.interfaces.StatusConsumer;
import tm.interfaces.TMStatusCode;
import tm.languageInterface.Language;
import tm.scripting.ScriptManager;
import tm.test.test3;
import tm.utilities.Assert;
import tm.utilities.ConcurUtilities;
import tm.utilities.Debug;
import tm.utilities.ResultThunk;
import tm.utilities.StringFileSource;
import tm.utilities.TMFile;
import tm.virtualMachine.SelectionParser;
public class Myservlet extends HttpServlet implements StatusConsumer {


	//private tm.interfaces.DisplayManagerInterface dispMan ;
	private static  Evaluator evaluator ;
	private boolean testMode = false ;
	private final CurrentFileManager currentFileManager = new CurrentFileManager() ;

	// The following var is used by reConfigure
	static final int boStatic = 0 ;
	static final int toStatic = 16384-1 ;
	static final int boHeap = toStatic+1 ;
	static final int toHeap = boHeap+16384-1 ;
	static final int boStack = toHeap+1 ;
	static final int toStack = boStack+16384-1 ;
	static final int boScratch = toStack+1 ;
	static final int toScratch = boScratch+16384-1 ;

	private Refreshable refreshMole = new Refreshable() {
		public void refresh() { Myservlet.this.refresh() ; }
	} ;
	// CONSTRUCTORS //
	//////////////////begin
	public Myservlet(){
		this(new ArgPackage() ) ;

	}

	public Myservlet(ArgPackage argPackage){
	}
	//  Implementing StatusConsumer  //
	///////////////////////////////////


	public void setStatus(int statusCode, String message) {
		if( statusCode == TMStatusCode.NO_EVALUATOR ) {
		} else {
			evaluator.setStatusCode( statusCode ) ; 
			evaluator.setStatusMessage(message) ; 
		} }

	public void attention(String message, Throwable th ) {
		if( ! testMode ) {
			java.awt.Frame d = new AttentionFrame( "Attention", message, th ) ;
			d.setVisible( true ) ; }

	}

	public void attention(String message ) {
		if( ! testMode ) {
			java.awt.Frame d = new AttentionFrame( "Attention", message ) ;
			d.setVisible( true ) ; }
	}

	public int getStatusCode() {
		if( evaluator==null ) return TMStatusCode.NO_EVALUATOR ;
		else return evaluator.getStatusCode() ;
	}

	public String getStatusMessage() {
		if( evaluator==null) return null ;
		else return evaluator.getStatusMessage() ; }

	/** Get output */
	public String getOutputString( ) {
		try {
			return ConcurUtilities.doOnSwingThread( new ResultThunk<String>() {
				@Override public String run() {
					if( evaluator != null ) {
						StringBuffer buf = new StringBuffer() ;
						for( int i = 0, sz = evaluator.getNumOutputLines() ; i < sz ; i++ ) {
							buf.append( evaluator.getOutputLine( i ) ) ;
							if( i != sz-1 ) buf.append( "\n" ) ; }
						return buf.toString() ; }

					else {
						return "" ; }
				}} ) ; }
		catch (InvocationTargetException e1) {
			e1.getTargetException().printStackTrace();
			return "" ; }
	}




	/** Should be called at the start of any example
	 * @param language The name of the language or UNKNOWN_LANG. */
	private boolean startNewProject( int language) {
		//set the programText, filename and create an instance of 
		//the TMfile which receive a source file and its name as parameters
		String fileName="test1";

		//LanguagePIFactoryIntf languageFactory = null ;
		String languageName = null ;
		languageName = "C++" ;

		// Create the language object
		Language lang = CPlusPlusLangPIFactory.createInstance(fileName).createPlugIn() ;

		currentFileManager.clearCurrentFile() ;

		// Restart the script manager
		ScriptManager scriptManager = ScriptManager.getManager();
		scriptManager.reset();


		//create the evaluator instance.
		try {
			evaluator = new Evaluator( lang, this, refreshMole,
					SelectionParser.parse(CommandInterface.DEFAULT_SELECTION),
					new SwingInputter(),
					boStatic, toStatic,
					boHeap, toHeap,
					boStack, toStack,
					boScratch, toScratch ) ; 
		}
		catch( Throwable e ) {
			setStatus( TMStatusCode.NO_EVALUATOR, "Could not build evaluator" ) ;
			reportException( e, "a failure while building the evaluator" ) ;
			return false ; }

		return true ;
	}   

	private void compile( TMFile tmFile) {
		// Precondition evaluator != null
		Assert.check( evaluator != null  ) ;
		java.awt.Frame dialog = new AttentionFrame( "Standby",
				"The Teaching Machine is loading the file.") ;
		dialog.setVisible( true ) ;

		evaluator.compile( tmFile );

	}


	private void loadTMFile( int language, TMFile tmFile ) {
		setStatus( TMStatusCode.NO_EVALUATOR, "Loading..." ) ;
		boolean ok = startNewProject( language ) ;
		if( ok ) {
			currentFileManager.setCurrentFile( tmFile, language ) ;
			compile( tmFile ) ; }
	}

	private class CurrentFileManager implements Observer {
		private TMFile currentFile = null ;
		private int currentLang = UNKNOWN_LANG ;

		boolean hasCurrentFile() { return currentFile != null ; }

		TMFile getCurrentFile() { return currentFile ; }

		int getLanguage() { return currentLang ; }

		void setCurrentFile( TMFile file, int language) {
			if( currentFile != null ) clearCurrentFile() ;
			file.addObserver( this ) ;
			currentFile = file ;
			currentLang = language ;
		}

		void clearCurrentFile() {
			if( currentFile != null ) {
				currentFile.deleteObserver( this ) ;
				currentFile = null ; }
		}

		public void update(Observable o, Object arg) {
			reStart() ;
		}
	}
	public void reStart() {
		try {
			ConcurUtilities.doOnSwingThread( new Runnable() {
				@Override public void run() {
					if( currentFileManager.hasCurrentFile()  ) {
						loadTMFile( currentFileManager.getLanguage(), currentFileManager.getCurrentFile() ) ; }
				}} ) ; }
		catch (InvocationTargetException e1) {
			e1.getTargetException().printStackTrace(); } }


	//main program in the following
	public String doServletExecution(String programTextHttp){
		String programText = programTextHttp ;
		StringFileSource fs = new StringFileSource() ;
		String fileName="test1";
		fs.addString( fileName, programText ) ;
		TMFile tmf = new TMFile( fs, fileName ) ;

		Debug debug = Debug.getInstance() ;
		debug.deactivate(); 


		test3 mytest = new test3();
		mytest.loadTMFile(CPP_LANG,tmf);
		
		//Assert.check(evaluator.getStatusCode() == TMStatusCode.COMPILED );
		evaluator.initialize(); 
		//Assert.check( evaluator.getStatusCode() == TMStatusCode.READY );

		while(  evaluator.getStatusCode() == TMStatusCode.READY ) {
			evaluator.goForward();
			String exp = evaluator.getExpression() ;
			System.out.println( "<" + exp + ">" ) ;
		}
		System.out.println(""+evaluator.getStatusCode()+"");
		String myOutPut = mytest.getOutputString() ;
		System.out.println(""+myOutPut+"");
		return myOutPut;

	}




	// PRIVATE AND PACKAGE METHODS //
	/////////////////////////////////

	void reportException( Throwable e, String message, String explanation ) {
		e.printStackTrace( System.out ) ;
		attention( message + explanation + ".", e ) ;
	}

	void reportException( Throwable e, String explanation ) {
		reportException( e, "The Teaching Machine could not execute your\n"
				+"request because of ", explanation) ;
	}
	private void refresh() {
		if( evaluator != null ) 
			System.out.println( "Status is " + evaluator.getStatusMessage() ) ;
	}	

	public static final int UNKNOWN_LANG = 0;
	public static final int CPP_LANG = 1 ;
	public static final int JAVA_LANG = 2 ;



	
	
	
	
	
	

    /**
     * Constructor of the object.
     */
    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
            super.destroy(); // Just puts "destroy" string in log
            // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

            doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {

            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            // here is the method to get the codes that the user enter in the html pages
            String programText = req.getParameter("name");
           String answer= doServletExecution(programText);
            
            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<HTML>");
            out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
            out.println("  <BODY>");
        	out.println( "<h1>Here is your codes,"+programText+"</h1>");
        	out.println( "<h1>Here is your result,"+answer+"</h1>");
   
            out.println("  </BODY>");
            out.println("</HTML>");
         //   out.flush();
            out.close();
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
            // Put your code here
    	
    	
    }

}


