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
package com.github.cherimojava.orchidae.controller.api;

import com.github.cherimojava.data.mongo.entity.Entity;

import java.util.List;

/**
 * ResponseEntity for PictureUpload
 * @author philnate
 */
public interface UploadResponse extends Entity<UploadResponse> {
    public List<String> getIds();

    public UploadResponse setIds(List<String> ids);

    public UploadResponse addIds(String id);

    public String getMsg();

    public UploadResponse setMsg(String msg);
}
