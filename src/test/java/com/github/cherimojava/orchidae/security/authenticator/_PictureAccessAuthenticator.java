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
package com.github.cherimojava.orchidae.security.authenticator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.orchidae.TestBase;
import com.github.cherimojava.orchidae.entity.Access;
import com.github.cherimojava.orchidae.entity.Picture;
import com.github.cherimojava.orchidae.entity.User;

public class _PictureAccessAuthenticator extends TestBase {
    @Mock
    EntityFactory factory;

    Picture pic;

    PictureAccessAuthenticator paa;

    String id = "id";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pic = EntityFactory.instantiate(Picture.class);

        pic.setUser(EntityFactory.instantiate(User.class).setUsername(ownr));
        when(factory.load(Picture.class, id)).thenReturn(pic);
        paa = new PictureAccessAuthenticator();
        paa.factory = factory;
    }

    @Test
    public void userAccessOwnPictures() {
        setAuthentication(owner);
        assertTrue(paa.hasAccess(id));
        pic.setAccess(Access.PRIVATE);
        assertTrue(paa.hasAccess(id));
        pic.setAccess(Access.PUBLIC);
        assertTrue(paa.hasAccess(id));
    }

    @Test
    public void otherUserOnlyPublic() {
        setAuthentication(other);
        assertFalse(paa.hasAccess(id));
        pic.setAccess(Access.PRIVATE);
        assertFalse(paa.hasAccess(id));
        pic.setAccess(Access.PUBLIC);
        assertTrue(paa.hasAccess(id));
    }
}
