package com.github.cherimojava.orchidae.entity;

import com.github.cherimojava.data.mongo.entity.Entity;
import com.github.cherimojava.data.mongo.entity.annotation.Final;
import com.github.cherimojava.data.mongo.entity.annotation.Id;
import com.github.cherimojava.data.mongo.entity.annotation.Reference;
import org.joda.time.DateTime;

import java.util.List;

/**
 * identifies pictures uploaded within the same batch (identified by a common uuid)
 */
public interface BatchUpload extends Entity<BatchUpload> {

    @Id
    public String getId();

    public BatchUpload setId(String id);

    @Final
    public DateTime getUploadDate();

    public BatchUpload setUploadDate(DateTime uploaded);

    @Reference
    public List<Picture> getPictures();

    public BatchUpload setPictures(List<Picture> pictures);

    public BatchUpload addPictures(Picture picture);

}
