import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;


public class PlayGame extends JPanel
{

	ArrayList<Gram> list;
	ArrayList<String> mixLen, mixLenS;
	Scanner easySC, medSC, mixSC, insaneSC, customSC;
	String[] wordList;
	String scanLine;
	int count, streak = 0;
	int randomList=0;

	boolean win;

	public PlayGame(int diff) throws IOException {
		try {
			easySC = new Scanner(new File("WordLibrary.txt"));//size=7
			medSC = new Scanner(new File("OneWordLibrary.txt"));//size=14
			mixSC = new Scanner(new File("MixLibrary.txt"));//size=21
			customSC = new Scanner(new File("CustomList.txt"));//size
			insaneSC = new Scanner(new File("wordlist.txt"));//size


		} catch (IOException ex) {
			System.out.print("IO error: " + ex.getMessage());
		}

		list = new ArrayList<Gram>();
		fillList(diff);
	}
	String fileName;
	public void fillList(int diff)
	{
		Scanner scanner = null;
		switch(diff){
			case 1:
				scanner = easySC;
				fileName = "WordLibrary.txt";
				break;
			case 2:
				scanner = medSC;
				fileName = "OneWordLibrary.txt";
				break;
			case 3://hard/mixed
				scanner = mixSC;
				fileName="MixLibrary.txt";
				break;
			case 4:
				scanner = customSC;
				fileName="CustomList.txt";
				break;
			case 5:
				try {
					addCustom();
				} catch (IOException e) {

					e.printStackTrace();
				}
				return;
			case 6:
				gameExplain();
				return;
			default:
				System.out.println("Invalid choice");
				break;
		}

		if(scanner!=null){
			try {
				while (scanner.hasNext()) {
					scanLine = scanner.nextLine();
					wordList = scanLine.split(" ");
					if (diff == 1) {
						list.add(new Gram(wordList[0], wordList[1], wordList[2], wordList[3]));

					} else if (diff == 2) {
						list.add(new Gram(wordList[0], wordList[1]));

					} else if (diff == 3 || diff == 4) {
						StringBuilder newWord = new StringBuilder();
						for (int i = 1; i < wordList.length; i++) {        // takes list of solutions of range {wordList[1],wordList[n]} and
							// arranges them into one String seperated by commas (Ex: rat,tar,art,)
							newWord.append(",").append(wordList[i]);
						}
						list.add(new Gram(wordList[0], newWord.toString()));

					}
					count++;
				}
			}finally {
				scanner.close();
			}

		}
	}

	public void play() {
		randomList = (int)(Math.random()*count);
		Gram gameS = list.get(randomList);
		System.out.println("\n"+gameS.getScram());

		try (Scanner scanGuess = new Scanner(System.in)) {
			System.out.println("Enter a possible anagram of the scrambled word:\nCurrent Streak: " + streak);
			String userGuess = scanGuess.nextLine();

			anagramChecker aC = new anagramChecker(gameS.getScram(), userGuess);

			if(userGuess.equalsIgnoreCase("DB")){			//Debug
				System.out.println("listSize: " + list.size());
				System.out.println("count: " + count);
				System.out.println("randomList: " + randomList);
				System.out.println("streak: " + streak+"\n");
				listPrint(new File(fileName));
				play();
			} else if(aC.isAnagram()) {
				list.remove(gameS);
				count--;
				if(count>=0){
					streak++;
				}
				System.out.println("\nCorrect! The word "+ userGuess + " is a valid solution.");
				System.out.println("***************************\nPlay Again?:\tY\tN\n***************************");
					try (Scanner playAgain = new Scanner(System.in)) {
						String stat = playAgain.nextLine(); //

						if(stat.equalsIgnoreCase("y")) {
							if(list.size()==0)															// size==0 means no more options
								System.out.println("All anagrams solved!");
							else
								play();
						} else if(stat.equalsIgnoreCase("n")) {
							try {
								AnagramGame.main(wordList);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}

			} else {
				streak=0;
				System.out.println("You lose! " + "\""+userGuess+"\"" + " does not match a valid solution \n***************************\nPlay Again:\tY\tN\n***************************\n");

				try (Scanner playAgain = new Scanner(System.in)) {
					String stat = playAgain.nextLine();
				if(stat.equalsIgnoreCase("y")) {
					play();
				} else if(stat.equalsIgnoreCase("n")) {
					try {
						AnagramGame.main(wordList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else{
					System.out.println("Invalid Input");
					stat = playAgain.nextLine();
					}
				}
			}
		}
	}


	public void gameExplain()
	{
		System.out.println("\nAn"+"\u00B7"+"a"+"\u00B7"+"gram - a word, phrase, or name formed by rearranging the letters of another, such as cinema, formed from iceman.");
		System.out.println("\nGive it a try! Enter a word and then try to make an anagram of it.");
		try (Scanner scan1 = new Scanner(System.in)) {
		try (Scanner scan2 = new Scanner(System.in)) {

			System.out.println("Enter a word:");
				String wordX = scan1.nextLine();
				System.out.println("\nEnter a case-sensitive anagram of that word:");
				String wordY = scan2.nextLine();

				anagramChecker aC=new anagramChecker(wordX, wordY);							// Checks if WordY is a valid anagram of WordX
				if(aC.isAnagram()){
					System.out.println("\nCorrect! " + wordY + " is an angagram of " + wordX+"!");

					System.out.println("***************************\nPlay Again?:\tY\tN\n***************************");
					try (Scanner playAgain = new Scanner(System.in)) {
						String stat = playAgain.nextLine();

						if(stat.equalsIgnoreCase("y"))
						{
							gameExplain();											// runs method if user agrees to play again
						}
						else if(stat.equalsIgnoreCase("n"))
						{
								
							try {
								AnagramGame.main(wordList);
							} catch (Exception e) {
							
								e.printStackTrace();
							}
							
							
						}
					}
				}
				else
				{
					System.out.println("\nIncorrrect! " + wordY + " is not an anagram of " + wordX + " because" + aC.failReason()+"!");

					System.out.println("***************************\nPlay Again?:\tY\tN\n***************************");
					try (Scanner playAgain = new Scanner(System.in)) {
						String stat = playAgain.nextLine();

						if(stat.equalsIgnoreCase("y"))
						{
							gameExplain();
						}
						else if(stat.equalsIgnoreCase("n"))
						{
								
							try {
								AnagramGame.main(wordList);
							} catch (Exception e) {
							
								e.printStackTrace();
							}
							
							
						}
					}
			}
		}
	}

	}

	public void addCustom() throws IOException
	{
		listPrint(new File(fileName));


		System.out.println("Want to add an anagram? First, enter a word (scrambled or otherwise).\nNext, enter the case-sensitve solutions that will be guessed by the user.");
		Scanner cWordScan = new Scanner(System.in);
		Scanner cSolScan = new Scanner(System.in);
		Scanner doneScan = new Scanner(System.in);
				String cWord="";
				String lastWord = "";
			//	String cSol ="";
				int solAmt = 0;
				int i = 0;
				boolean valid = false;

				System.out.println("Enter word to be Anagrammed:");
					 cWord = cWordScan.nextLine();
				System.out.println("Now enter amount of solutions:");
					solAmt = doneScan.nextInt();


				System.out.println("Now enter valid anagrams of that word.");
				while(i<solAmt)
				{

					String oneSol = cSolScan.nextLine();

					anagramChecker customChk = new anagramChecker(cWord, oneSol);
					if(customChk.isAnagram())
					{
						lastWord+=" "+oneSol;
						i++;
						valid = true;
					}
					else if(!(customChk.isAnagram()))
					{
						valid = false;
						System.out.println("Selected word is not an anagram. Please enter a different one or check the spelling.");

					}

				}
				if(i==solAmt && valid )
				{
					CustomWriter cw = new CustomWriter(cWord, lastWord);
					cw.writeGram();
					System.out.println("Custom Anagram Created");
				}
	}

	public void listPrint(File file) {
		try (Scanner fileSC = new Scanner(file)) {
			// First, find the maximum anagram length
			int maxAnagramLength = 0;
			while (fileSC.hasNextLine()) {
				String line = fileSC.nextLine();
				if(!line.trim().isEmpty()) {
					String[] words = line.split(" ");
					if (words.length > 0) {
						maxAnagramLength = Math.max(maxAnagramLength, words[0].length());
					}
				}
			}
			fileSC.close();
			Scanner newFileSC = new Scanner(file);

			System.out.printf("%-" + (maxAnagramLength) + "s\t\t%s\n", "Anagram", "Solutions");
			System.out.printf("%-" + (maxAnagramLength) + "s\t\t%s\n", "*******", "*********");

			//print table
			while (newFileSC.hasNextLine()) {
				String scanLine = newFileSC.nextLine();

				if(scanLine.trim().isEmpty()) {
					continue;
				}
				String[] wordList = scanLine.split(" ");
				String anagram = wordList[0];
				StringBuilder solutions = new StringBuilder();
				if (wordList.length > 1) {
					for (int i = 1; i < wordList.length; i++) {
						solutions.append(wordList[i]);
						if (i < wordList.length - 1) {
							solutions.append("|");
						}
					}
				}
				// Use printf with calculated width
				System.out.printf("%-" + (maxAnagramLength) + "s:\t\t%s\n", anagram, solutions.toString());
			}
			//System.out.println();
			newFileSC.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}