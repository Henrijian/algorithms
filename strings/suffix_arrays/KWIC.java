package strings.suffix_arrays;
/******************************************************************************
 *  Compilation:  javac KWIC.java
 *  Execution:    java KWIC file.txt
 *  Dependencies: SuffixArray.java
 *
 *  Keyword-in-context search.
 *
 *  %  java KWIC tale.txt 15
 *  majesty
 *   most gracious majesty king george th
 *  rnkeys and the majesty of the law fir
 *  on against the majesty of the people
 *  se them to his majestys chief secreta
 *  h lists of his majestys forces and of
 *
 *  the worst
 *  w the best and the worst are known to y
 *  f them give me the worst first there th
 *  for in case of the worst is a friend in
 *  e roomdoor and the worst is over then a
 *  pect mr darnay the worst its the wisest
 *  is his brother the worst of a bad race
 *  ss in them for the worst of health for
 *   you have seen the worst of her agitati
 *  cumwented into the worst of luck buuust
 *  n your brother the worst of the bad rac
 *   full share in the worst of the day pla
 *  mes to himself the worst of the strife
 *  f times it was the worst of times it wa
 *  ould hope that the worst was over well
 *  urage business the worst will be over i
 *  clesiastics of the worst world worldly
 *
 ******************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *  The {@code KWIC} class provides a {@link SuffixArray} client for computing
 *  all occurrences of a keyword in a given string, with surrounding context.
 *  This is known as keyword-in-context search.
 */
public class KWIC {
    /**
     * Reads a string from a file specified as the first
     * command-line argument; read an integer k specified as the
     * second command line argument; then repeatedly processes
     * use queries, printing all occurrences of the given query
     * string in the text string with k characters of surrounding
     * context on either side.
     *
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        try {
            // read in text
            File file = new File(argv[0]);
            Scanner fileReader = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (fileReader.hasNextLine()) {
                stringBuilder.append(fileReader.nextLine() + " ");
            }
            fileReader.close();
            String text = stringBuilder.toString().replaceAll("\\s+", " ");
            int contextLength = Integer.parseInt(argv[1]); // context length of keyword occurrence
            int textLength = text.length();
            // build suffix array
            SuffixArray suffixArray = new SuffixArray(text);
            // find all occurrences of queries and give context
            Scanner stdReader = new Scanner(System.in);
            while (stdReader.hasNextLine()) {
                String query = stdReader.nextLine();
                for (int i = suffixArray.rank(query); i < textLength; i++) {
                    int from1 = suffixArray.index(i);
                    int to1 = Math.min(textLength, from1 + query.length());
                    if (!query.equals(text.substring(from1, to1))) {
                        break;
                    }
                    int from2 = Math.max(0, suffixArray.index(i) - contextLength);
                    int to2 = Math.min(textLength, suffixArray.index(i) + query.length() + contextLength);
                    System.out.println(text.substring(from2, to2));
                }
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open specified file");
            e.printStackTrace();
        }
    }
}
