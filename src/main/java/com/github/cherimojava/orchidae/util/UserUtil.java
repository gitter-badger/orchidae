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
package com.github.cherimojava.orchidae.util;

import java.util.concurrent.ExecutionException;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.cherimojava.data.mongo.entity.Entity;
import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.entity.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Utility class for working with Users
 * @author philnate
 */
public class UserUtil {

    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    EntityFactory factory;

    int cacheSize;

    private LoadingCache<String, User> registry;

    @SuppressWarnings("unchecked")
    public UserUtil(int cacheSize, int concurrencyLevel) {
        registry = CacheBuilder.newBuilder().maximumSize(cacheSize).concurrencyLevel(concurrencyLevel).removalListener(new RemovalListener
                () {
            @Override
            public void onRemoval(RemovalNotification notification) {
                //save user before exit
                if (notification.getValue() != null) {
                    ((Entity) notification.getValue()).save();
                }
            }
        }).build(new CacheLoader<String, User>() {
            @Override
            public User load(String key) throws Exception {
                return factory.load(User.class, key);
            }
        });
        this.cacheSize = cacheSize;
    }

    /**
     * get the requested user or null if the user couldn't be loaded(not found or somethign else happened)
     * @param userId
     * @return
     */
    public User getUser(String userId) {
        try {
            return registry.get(userId);
        } catch (ExecutionException e) {
            LOG.warn("failed to load user", e);
            return null;
        }
    }

    /**
     * on shutdown save all users and clear cache
     */
    @PreDestroy
    public void _clear() {
        for (User user : registry.asMap().values()) {
            user.save();
        }
        registry.invalidateAll();
    }
}
