package programmieraufgaben;

import java.sql.SQLSyntaxErrorException;
import java.util.*;

public class PackageCreator {

    private int maxDataPackageLength;
    private int IPVersion;
    private String absender;
    private String empfaenger;
    private List<String> buffer = new LinkedList<>();
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
        boolean ifEnd = false;
        while (!ifEnd) {
            String[] tempBuffer;
            tempBuffer = input.next().split("\\s+");    // geteilt durch ein oder mehrere Leerzeichen
            for (String a:tempBuffer
                 ) {
                buffer.add(a);
            }
            if(input.nextLine().equals(".")){
                ifEnd = true;      // Falls <CR><LF>.<CR><LF>, schließen die Eingabe ab
            }else {
                buffer.add("\\n");
            }
        }
        for (int i = 0; i < buffer.size(); i++){
            while (buffer.get(i).contains("-")){
                String[] tempBuffer = buffer.get(i).split("-");
                buffer.remove(i);
                for(int a = 0; a < tempBuffer.length; a++){
                    buffer.add(i + 2* a,tempBuffer[a]);
                    buffer.add(i + 2 * a + 1,"-");
                }
                buffer.remove(i + 2 * tempBuffer.length - 1);
            }
            while (buffer.get(i).contains("/")){
                String[] tempBuffer = buffer.get(i).split("/");
                buffer.remove(i);
                for(int a = 0; a < tempBuffer.length; a++){
                    buffer.add(i + 2* a,tempBuffer[a]);
                    buffer.add(i + 2 * a + 1,"/");
                }
                buffer.remove(i + 2 * tempBuffer.length - 1);
            }
        }
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


//------------------------此线之下尚未完成---------------------------------
        int usedWordCounter = 0;
        //while (usedWordCounter < buffer.size()){
            //System.out.println(wordCounter);

            //System.out.println(wordEndIndex.get(2));
            //usedWordCounter++;
            int dataPackageLength = 0;
            int packageCounter = 0;
            for (int i = 0; i < buffer.size(); i++){
                String tempBuffer = null;
                if (usedWordCounter == i){
                    if (buffer.get(i).length() >= maxDataPackageLength){
                        System.out.println("Die Nachricht kann nicht versendet werden, da sie ein Wort mit Länge " + buffer.get(i).length() + " > " +maxDataPackageLength + " enthält.");
                        throw new RuntimeException();
                    }else {
                        tempBuffer = buffer.get(i);
                        dataPackageLength += buffer.get(i).length();
                    }
                }else {
                    if ("-" == buffer.get(i) | "/" == buffer.get(i)){
                        if (dataPackageLength + buffer.get(i).length() >= maxDataPackageLength){
                            dataPackages.add(new DataPackage(dataPackageLength, packageCounter, IPVersion, absender, empfaenger, tempBuffer));
                            usedWordCounter = i;
                            i--;
                        }else {
                            tempBuffer = tempBuffer + buffer.get(i);
                            dataPackageLength += buffer.get(i).length();
                        }
                    }else {
                        if ('/' == tempBuffer.charAt(tempBuffer.length() - 1) | '-' == tempBuffer.charAt(tempBuffer.length() - 1)){
                            if (dataPackageLength + buffer.get(i).length() >= maxDataPackageLength){
                                dataPackages.add(new DataPackage(dataPackageLength, packageCounter, IPVersion, absender, empfaenger, tempBuffer));
                                usedWordCounter = i;
                                i--;
                            }else {
                                tempBuffer = tempBuffer + buffer.get(i);
                                dataPackageLength += buffer.get(i).length();
                            }
                        }else {
                            if (dataPackageLength + buffer.get(i).length() + 1 >= maxDataPackageLength){
                                dataPackages.add(new DataPackage(dataPackageLength, packageCounter, IPVersion, absender, empfaenger, tempBuffer));
                                usedWordCounter = i;
                                i--;
                            }else {
                                tempBuffer = tempBuffer + " " + buffer.get(i);
                                dataPackageLength = dataPackageLength + 1 + buffer.get(i).length();
                            }
                        }
                    }
                }
            }
        //}

//------------------------此线之上尚未完成---------------------------------
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
