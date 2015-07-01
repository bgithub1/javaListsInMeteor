package com.billybyte.meteorjava.staticmethods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;

import com.thoughtworks.xstream.XStream;




public class Utils {
	/**
	 * 
	 * @param sourceClass - class name to print on message
	 * @param s - message to print
	 */
	public static void prtObMess(Class<?> sourceClass,String s){
		prt(sourceClass.getName()+" : "+s);
	}
	public static void prt(String s){
		System.out.println(s);
	}
	public static void prtNoNewLine(String s){
		System.out.print(s);
	}
	
	public static void prt(int i){
		System.out.println(new Integer(i).toString());
	}
	
	public  static void prtErrWithCsvLineData(Class<?> sourceClass,String errMess,String[] csvDataLine){
		Utils.prtObErrMess(sourceClass, errMess + " for line"+ Arrays.toString(csvDataLine));
	}

	public static IllegalStateException illStateWithCsvLineData(Class<?> sourceClass,String errMess,String[] csvDataLine){
		throw Utils.IllState(sourceClass, errMess + " for line"+ Arrays.toString(csvDataLine));
	}
	
	public static  IllegalArgumentException illArgWithCsvLineData(Class<?> sourceClass,String errMess,String[] csvDataLine){
		throw Utils.IllArg(sourceClass, errMess + " for line"+ Arrays.toString(csvDataLine));
	}


	public static void prt(long i){
		System.out.println(new Long(i).toString());
	}
	
	public static void prt(double d){
		System.out.println(new Double(d).toString());
	}

	
	public static void prtErr(String s){
		System.err.println(s);
	}
	public static void prtObErrMess(Class<?> sourceClass,String s){
		System.err.println(sourceClass.getName()+" " + s);
	}
	
	public static String ErMs(Object o,String mess){
		if(Class.class.isAssignableFrom(o.getClass())){
			return ((Class<?>)o).getName()+" "+ mess;
		}else{
			return o.getClass().getName()+" "+ mess;
			
		}
	}
	
	public static IllegalArgumentException IllArg(Object o, String mess){
		return new IllegalArgumentException(ErMs(o,mess));
	}
	
	public static IllegalStateException IllState(Object o, String mess){
		return new IllegalStateException(ErMs(o,mess));
	}

	public static IllegalStateException IllState(Throwable cause){
		return new IllegalStateException(cause);
	}

	public static List<String> getRegexMatches(String regexExpression,String stringToSearch){
//		List<String> ret = new ArrayList<String>();
		Pattern pattern =Pattern.compile(regexExpression);
		Matcher matcher = pattern.matcher(stringToSearch);
		return getRegexMatches(matcher);

	}
	
	public static List<String> getRegexMatches(Matcher matcher){
		List<String> ret = new ArrayList<String>();
		HashSet<String> foundTokens = new HashSet<String>();
		while (matcher.find()) {
			String token = matcher.group();
			if(foundTokens.contains(token)){
				continue;
			}
			ret.add(token);
			foundTokens.add(token);
//			System.out.format("I found the text \"%s\" starting at " +
//		       "index %d and ending at index %d.%n",
//		        token, matcher.start(), matcher.end());
		}
		return ret;
		
	}
	public static <E> void prtListItems(List<E> list){
		if(list==null || list.size()<1){
			Utils.prtObErrMess(Utils.class, "prtListItems: can't print null List or List with no items");
		}
		
		for(E element : list){
			if(Object[].class.isAssignableFrom(element.getClass())){
				Object[] oArr = (Object[])element;
				if(oArr.length>1){
					Utils.prt(Arrays.toString(oArr));
				}else{
					Utils.prt(element.toString());
				}
			}else{
				Utils.prt(element.toString());
			}
			//Utils.prt(element.toString());
		}
	}

	/**
	 * 
	 * @param classOfItems Class<T>
	 * @param csvData List<String[]>
	 * @return List<T>
	 */
	public static <T> List<T> listFromCsv(Class<T> classOfItems,List<String[]> csvData) {
		try {
			if(csvData==null || csvData.size()<1)return null;
			String[] header = csvData.get(0);
			String listBeg = "<list>";
			String listEnd = "</list>";
			String clBeg = "<"+classOfItems.getCanonicalName()+">";
			String clEnd = "</"+classOfItems.getCanonicalName()+">";
			String xml=listBeg;
			
			for(int i =1;i<csvData.size();i++){
				String[] line = csvData.get(i);
				if(header.length>line.length)continue;
				xml = xml+clBeg;
				for(int j = 0;j<header.length;j++){
					String begToken = "<"+header[j].trim()+">";
					String endToken = "</"+header[j].trim()+">";
					String value = line[j].trim();
					xml = xml+begToken+value+endToken;
				}
				xml = xml+clEnd;
			}
			xml = xml+listEnd;
			XStream xs = new XStream();
			Object o = xs.fromXML(xml);
			if(!List.class.isAssignableFrom(o.getClass())){
				return null;
			}
			List<T> ret = new ArrayList<T>();

			@SuppressWarnings("rawtypes")
			List l = (List)o;
			if(l.size()<1){
//				return null;
				return ret;
			}
			Object lo = l.get(0);
			if(!classOfItems.isAssignableFrom(lo.getClass())){
				return null;
			}
			for(Object obj:l){
				T t = classOfItems.cast(obj);
				ret.add(t);
			}

			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	/**
	 * This will process annotations in the class specified by
	 *   classOfPackage.
	 * @param returnClass
	 * @param classOfPackage
	 * @param xmlDataPath  - Full Path of xml data 
	 * 		THIS IS NOT THE FILENAME ONLY AS IN OTHER GET DATA METHODS
	 * 			WHICH SPECIFY A CLASS, AND FIND THE FILE AS A RESOURCE.
	 * 
	 * 		FOR THAT, SEE the method getXmlData.
	 * @return
	 */
	public static  <T,C>  T getFromXml(Class<T> returnClass,
			Class<C> classOfPackage, String xmlDataPath){
		
		return getXmlData(returnClass, classOfPackage, xmlDataPath);
	}
	@SuppressWarnings("unchecked")
	/**
	 * Get xml data from either a resource file, or a regular file with
	 * 		the full path of the file specified int the xmlDataPath argument
	 * @param returnClass
	 * @param classInPackageWhereFileIsLocated - any class in the same package
	 * 		as where the file (as a resource is located)  If this is null,
	 * 		then the normal file lookup will occur.
	 * @param xmlDataPath - if classInPackageWhereFileIsLocated is not null,
	 * 		then just the file name with no other path info, unless the file is
	 * 		in a subfolder in the package of theclassInPackageWhereFileIsLocated.
	 * 		Otherwise, a full file path.
	 * @return T
	 */
	public static  <T,C>  T getXmlData(
			Class<T> returnClass,
			Class<C> classInPackageWhereFileIsLocated, 
			String xmlDataPath){
		
		XStream xs = new XStream();
		InputStream is;
		if(classInPackageWhereFileIsLocated!=null){
			is = getInputStreamAsResource(classInPackageWhereFileIsLocated, xmlDataPath);
		}else{
			try {
				is = new FileInputStream(new File(xmlDataPath));
			} catch (FileNotFoundException e) {
				throw IllState(Utils.class, e.getMessage());
			}
		}
		//read the file 
		Object o = xs.fromXML(is);
		if(returnClass.isAssignableFrom(o.getClass())){
			return (T)o;
		}else{
			return null;
		}

	}
	public static InputStream getInputStreamAsResource(Class<?> clazz,String fileName){
		InputStream stream = clazz.getResourceAsStream(fileName);
		return stream;
	}
	public static List<String[]> getCSVData(Class<?> classInPackageOfFile, String csvFileName){
		if(classInPackageOfFile==null)return getCSVData(csvFileName);
		BufferedReader bf = getBufferedReaderAsResource(classInPackageOfFile, csvFileName);
		CSVReader reader = new CSVReader(bf);
		return getCSVData(reader);
	}
	public static ArrayList<String[]> getCSVData(String csvFileName){
		CSVReader reader = getCSV(csvFileName);
		String [] nextline;
		ArrayList<String[]> retList = new ArrayList<String[]>();
		try {
				while((nextline = reader.readNext())!=null){
					retList.add(nextline);
				}
				return retList;
			} catch (IOException e) {
			
				e.printStackTrace();
				return null;
			}
	}

	public static ArrayList<String[]> getCSVData(CSVReader reader){
		String [] nextline;
		ArrayList<String[]> retList = new ArrayList<String[]>();

		try {
				while((nextline = reader.readNext())!=null){
					retList.add(nextline);
				}
				return retList;
			} catch (IOException e) {
			
				e.printStackTrace();
				return null;
			}
	}
	public static InputStreamReader getInputStreamReaderAsResource(Class<?> clazz,String fileName){
		return new InputStreamReader(getInputStreamAsResource(clazz,fileName));
	}
	
	public static BufferedReader getBufferedReaderAsResource(Class<?> clazz,String fileName){
		return new BufferedReader(getInputStreamReaderAsResource(clazz,fileName));
	}
	
	public static BufferedReader getBufferedReaderFromInputStreamReader(InputStreamReader isReader){
		return new BufferedReader(isReader);
	}

	public static CSVReader getCSV(String filename)  {
//		String filename="http://www.barchart.com/historicaldata.php?sym=IBM&view=historicalfiles&txtDate=10/28/10#";
		Reader bufferedReader=null;
		CSVReader reader;

		if(filename.substring(0,3).compareTo("ftp")==0 || filename.substring(0,4).compareTo("http")==0 ){
			try{
				URL url = 
				    new URL(filename);
				URLConnection con = url.openConnection();
				bufferedReader = 
					new BufferedReader(new InputStreamReader(con.getInputStream()));
			}catch(Exception e){
				String s = stackTraceAsString(e);
				Utils.prtObErrMess(Utils.class,s);
				Utils.prtObErrMess(Utils.class,"ftp connection failed to :"+filename);	
				return null;
			}
		}else try{
			bufferedReader = new FileReader(filename);
		}catch(Exception e){
			String s = stackTraceAsString(e);
			Utils.prtObErrMess(Utils.class,s);
			Utils.prtObErrMess(Utils.class,"CSV input file not found:" + filename);	
//			Utils.prt(e.getMessage());
//			e.printStackTrace();
			
			return null;
			
		}
		reader = new CSVReader(bufferedReader);
		return reader;
	}
	public static String stackTraceAsString(Throwable e){
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    e.printStackTrace(printWriter);
	    String s =  result.toString();
	    return s;
	}
	public static Map<String,String> getArgPairsSeparatedByChar(String[] args,String separator){
		Map<String, String> argPairs = new HashMap<String, String>();
		if(args!=null){
			// find pairs separated by the = sign
			for(String argPair : args){
				String[] pair = argPair.split("=");
				if(pair.length>1){
					argPairs.put(pair[0],pair[1]);
				}
			}
		}
		return argPairs;
	}
	public static Set<String> readSetData(String fn){
		Set<String> ret = new TreeSet<String>();
		File file = new File(fn);
		try {
			Reader fr  = new FileReader( file);
			String data=null;
			BufferedReader br = new BufferedReader(fr);
			while((data =br.readLine())!= null){
				ret.add(data);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}		
	

	

}
