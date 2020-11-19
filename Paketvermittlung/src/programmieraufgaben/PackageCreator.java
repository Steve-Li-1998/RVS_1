package programmieraufgaben;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PackageCreator {

    private int maxDataPackageLength;
    private int IPVersion;
    private String absender;
    private String empfaenger;
    private String buffer;
    private int serialNumberCounter;

    /**
     * Hier sollen die Kommandozeilen-Abfragen abgefragt und die Antworten
     * gespeichert werden
     * Es sollte auf Fehlerbehandlung geachtet werden (falsche Eingaben, ...)
     *
     * @param dataPackage Hier wird das Objekt übergeben in das die abgefragten Werte gespeichert werden sollen
     * @return Gibt das als Parameter übergebene Objekt, dass mit den abgefragten Werten befüllt wurde zurück
     */
    public DataPackage fillParameters(DataPackage dataPackage) {
        maxDataPackageLength = dataPackage.getDataPackageLength();
        String temp;
        Scanner input = new Scanner(System.in);
        System.out.println("Bitte geben Sie Ihre gewünschte IP Version ein:");
        boolean setIPVersionSuccessful = false;     //Diese Variable ändert sich wahr, nachdem die IP-Version erfolgreich eingesetzt wurde.
        while (!setIPVersionSuccessful){
            temp = input.nextLine();
            if(0 == temp.compareTo("4") | 0 == temp.compareTo("v4")){
                IPVersion = 4;
                setIPVersionSuccessful = true;
            }else if(0 == temp.compareTo("6") | 0 == temp.compareTo("v6")){
                IPVersion = 6;
                setIPVersionSuccessful = true;
            }else {
                System.out.println("Die IP Version ist nur 4 und 6! Bitte geben Sie \"4\" oder \"6\" ein:");
            }
        }
        System.out.println("Bitte Geben Sie Ihre IP-Adresse ein:");
        absender = input.nextLine();
        System.out.println("Bitte Geben Sie die IP-Adresse Ihres gewünschten Empfängers ein:");
        empfaenger = input.nextLine();
        System.out.println("Bitte geben Sie Ihre Nachricht ein:");
        buffer = input.nextLine();

        return dataPackage;
    }

    /**
     * Aus dem als Parameter übergebenen Paket sollen die Informationen
     * ausgelesen und in einzelne Datenpakete aufgeteilt werden
     *
     * @param dataPackage Hier wird das Objekt übergeben in das das Resultat gespeichert werden soll
     * @return Gibt das als Parameter übergebene Objekt mit den aufgeteiltet Datenpaketen zurück
     */
    public List<DataPackage> splitPackage(DataPackage dataPackage) {
        List<DataPackage> dataPackages = new LinkedList<>();
        ArrayList wordStartIndex = new ArrayList(0);    // diese Array speichert immer den Index erstes Buchstabens eines Wortes
        ArrayList wordEndIndex = new ArrayList(0);      // diese Array speichert immer den Index letztes Buchstabens eines Wortes + 1
        int wordCounter = 0;
        buffer = buffer.replace("<CR><LF>", "\n");
        for(int i = 0; i < buffer.length(); i++){
            char temp = buffer.charAt(i);
            if (' ' == temp){
                if (wordStartIndex.size() != wordEndIndex.size()){
                    wordEndIndex.add(i);
                }

            }else if ('-' == temp | '/' == temp){
                wordCounter++;
                wordStartIndex.add(i);
                if (wordStartIndex.size() != wordEndIndex.size()){
                    wordEndIndex.add(i);
                }
                wordEndIndex.add(i+1);

            }else if ('\\' == temp){
                if ('n' == buffer.charAt(i + 1)){
                    wordCounter++;
                    wordStartIndex.add(i);
                    if (wordStartIndex.size() != wordEndIndex.size()){
                        wordEndIndex.add(i);
                    }
                    wordEndIndex.add(i+2);

                    i++;
                }else {
                    if (wordStartIndex.size() == wordEndIndex.size()){
                        wordCounter++;
                        wordStartIndex.add(i);
                    }
                }
            }else{
                if (wordStartIndex.size() == wordEndIndex.size()){
                    wordCounter++;
                    wordStartIndex.add(i);
                }

            }
            //if ((temp >= 'a' & temp <= 'z') | (temp >= 'A' & temp <= 'Z') | (temp >= '0' & temp <= '9') | 'ä'==temp){
            //    System.out.println(temp);
            //}

        }
        int usedWordCounter = 0;
        while (usedWordCounter < wordCounter){
            //System.out.println(wordCounter);
            usedWordCounter++;
            int dataPackageLength = -1;
            int packageCounter = 0;
            for (int i = usedWordCounter; i <= wordCounter; i++){
                if (dataPackageLength + (int)wordEndIndex.get(i - 1) - (int)wordStartIndex.get(i - 1) + 1 > maxDataPackageLength){
                    if (-1 == dataPackageLength){
                        int temp = (int)wordEndIndex.get(i - 1) - (int)wordStartIndex.get(i - 1);
                        System.out.println("Die Nachricht kann nicht versendet werden, da sie ein Wort mit Länge " + temp + " > " +maxDataPackageLength + "enthält.");
                    }else{
                        String temp = null;
                        for (int a=usedWordCounter - 1; a<=i - 1;a++){
                            temp += buffer.substring((int)wordStartIndex.get(a), (int)wordEndIndex.get(a)) + " ";
                        }
                        temp = temp.substring(0,temp.length());
                        packageCounter++;
                        dataPackages.add(new DataPackage(dataPackageLength, packageCounter, IPVersion, absender, empfaenger, temp));
                        usedWordCounter = i - 1;
                    }
                    dataPackageLength = -1;
                    break;
                }else{
                    dataPackageLength = dataPackageLength + (int)wordEndIndex.get(i - 1) - (int)wordStartIndex.get(i - 1) + 1;
                }
            }
        }


        return dataPackages;
    }

    /**
     * Diese Methode gibt den Inhalt der empfangenen Pakete in der Komandozeile aus
     *
     * @param dataPackages Hier wird die Liste übergeben, deren Elemente in die Kommandozeile ausgegeben werden sollen
     */
    public void printOutPackage(List<DataPackage> dataPackages) {
        System.out.println("Es sind " + dataPackages.size() +" Datenpakete notwendig.\n");
        for (int i = 0; i < dataPackages.size(); i++){
            dataPackages.get(i).show();
        }

    }
}
