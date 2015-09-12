package com.guokrspace.cloudschoolbus.parents.event;

import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;

import net.soulwolf.image.picturelib.model.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbook on 15-8-9.
 */
public class PictureSelectionEvent{
    private ArrayList<Picture> pictures;

    public PictureSelectionEvent(ArrayList<Picture> pictures) {
        this.pictures = pictures;
    }

    public ArrayList<Picture> getPictures() {
        return pictures;
    }
}
