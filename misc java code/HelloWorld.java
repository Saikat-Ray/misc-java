import org.apache.pdfbox.multipdf.PDFMergerUtility;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.io.MemoryUsageSetting;
import java.util.List;
import org.apache.commons.io.FileUtils;
import java.util.*;

public class HelloWorld {

    public static void main(String[] args) throws IOException{
        System.out.println("This utility merges all pdf files in a directory...");

        //Source directory
        File dir = new File("C:\\Users\\sairay\\java");

        //extensions of files to be merged
        String[] extensions = new String[] { "pdf", "PDF" };

        //get list of all PDF files in the source directory and store them in ArrayList object
        ArrayList<String> fList = new ArrayList<String>();
        String fileName = "";
        List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
        HashMap<Integer, String> fileNames = new HashMap<>();
        ArrayList<Integer> seq = new ArrayList<Integer>();
        Integer fSeq; // = new Integer();
        fileNames.clear();
        seq.clear();
        for (File file : files) {
			fileName = file.getCanonicalPath().toString();
			fList.add(fileName);
			fSeq = Integer.valueOf(fileName.substring(fileName.indexOf("Syn")+3,fileName.indexOf("pdf")-1));
			//System.out.println(fileName.indexOf("Syn")+" "+fileName.indexOf("pdf"));
			fileNames.put(fSeq,fileName);
			seq.add(fSeq);
		}
		Collections.sort(seq);
		for (int i=0; i < seq.size(); i++) {
			System.out.println(seq.get(i));
			System.out.println(fileNames.get(seq.get(i)));
		}

		//count of number of files
		int no_of_files = fList.size();

		//create target(merged) file
/*		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		PDFmerger.setDestinationFileName("C:\\Users\\sairay\\java\\Merged.pdf");

		//get all files from ArrayList and add them to the target file
		File file1;
		String fName = "";
		for (int i=0; i < no_of_files; i++){
			fName = fList.get(i);
			System.out.println("file Name: "+ fName);
			PDFmerger.addSource(fName);
		}
        PDFmerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());*/

    }
}