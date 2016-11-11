/*
 * Copyright 2016 Greatful World. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package world.greatful.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.InstrumentationRegistry;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FeedProviderTest extends ProviderTestCase2<FeedProvider> {

    public FeedProviderTest() {
        super(FeedProvider.class, FeedContract.CONTENT_AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @Test
    public void shouldCreateAndRetrieveEntry() {
        String entryId = "a1b2c3";
        String title = "Test Title";
        createEntry(entryId, title);

        Cursor cursor = getMockContentResolver().query(FeedContract.Entry.CONTENT_URI, null, null, null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());

        assertEquals(entryId, cursor.getString(cursor.getColumnIndex(FeedContract.Entry.COLUMN_NAME_ENTRY_ID)));
        assertEquals(title, cursor.getString(cursor.getColumnIndex(FeedContract.Entry.COLUMN_NAME_TITLE)));
        cursor.close();
    }

    private Uri createEntry(String entryId, String title) {
        ContentValues values = new ContentValues();
        values.put(FeedContract.Entry.COLUMN_NAME_ENTRY_ID, entryId);
        values.put(FeedContract.Entry.COLUMN_NAME_TITLE, title);
        return getMockContentResolver().insert(FeedContract.Entry.CONTENT_URI, values);
    }
}
