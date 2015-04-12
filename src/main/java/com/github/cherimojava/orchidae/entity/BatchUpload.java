/**
 * Copyright (C) 2014 cherimojava (http://github.com/cherimojava/orchidae)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
