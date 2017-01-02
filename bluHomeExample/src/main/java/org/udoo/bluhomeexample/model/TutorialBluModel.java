package org.udoo.bluhomeexample.model;

/**
 * Created by harlem88 on 02/01/17.
 */

public class TutorialBluModel {
    public String title;
    public String text;
    public int imageRes;

    public static TutorialBluModel Builder(String title, String text, int imageRes){
        TutorialBluModel tutorialBluModel = new TutorialBluModel();
        tutorialBluModel.title = title;
        tutorialBluModel.text = text;
        tutorialBluModel.imageRes = imageRes;
        return tutorialBluModel;
    }
}
