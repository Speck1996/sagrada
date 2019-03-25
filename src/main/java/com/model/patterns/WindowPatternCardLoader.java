package com.model.patterns;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading the windows pattern card form a file.
 * The file is accessed only during the constructions, when the method {@code getWindows()} is invoked a cloned list of these windows is returned.
 */
public class WindowPatternCardLoader {
    private static final String fileName = "configfiles/pattern/windows.txt";
    private List<WindowPatternCard> windows;


    /**
     * Constructs a new WindowPatternCardLoader.
     */
    public WindowPatternCardLoader(){
        loadWindows();
    }


    /**
     * Load windows from file.
     */
    private  void loadWindows() {
        FileReader fr = null;
        BufferedReader br = null;
        windows = new ArrayList<>();

        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            while (br.ready()) {
                String line = br.readLine();
                System.out.println("Building: " + line);

                WindowPatternCard newWindow;
                try {
                    newWindow = new WindowPatternCard(line);
                    windows.add(newWindow);
                } catch (WindowSyntaxException e) {
                    System.out.println("Failed to load "+line + " due to: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if(fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //Create a new cloned coupledList o of windows

    /**
     * Returns a new cloned list of the windows pattern card, coupled by id.
     * Only windows with even id are actually added to the list, the ones with odd id are accessible by the respective coupled windows.
     * @return a new cloned list of the windows pattern card, coupled by id.
     */
    public List<WindowPatternCard> getWindows(){

        List<WindowPatternCard> coupledWindows = new ArrayList<>();
        List<WindowPatternCard> clonedWindows = new ArrayList<>();

        //clone all the windows
        for(int i=0; i<windows.size(); i++) {
            clonedWindows.add(new WindowPatternCard(windows.get(i)));
        }

        //couple the windows
        for(int i=0; i<clonedWindows.size(); i++) {
            if(i%2 == 0 && i+1 < clonedWindows.size()) {
                clonedWindows.get(i).setPairedWindow(clonedWindows.get(i+1));
            }
            else {
                clonedWindows.get(i).setPairedWindow(clonedWindows.get(i-1));
            }
        }

        //add the couples in a new list
        for(int i=0; i<clonedWindows.size(); i += 2) {
            coupledWindows.add(clonedWindows.get(i));
        }

        //debug
//        System.out.println("DEBUG2");
//        for(WindowPatternCard wpc: coupledWindows) {
//
//            System.out.println(wpc.toString());
//            System.out.println(wpc.getPairedWindow().toString());
//        }

        return coupledWindows;
    }




}
