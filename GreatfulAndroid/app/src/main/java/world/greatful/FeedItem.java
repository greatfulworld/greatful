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

package world.greatful;

import android.database.Cursor;

import world.greatful.sync.FeedContract;

public class FeedItem {

    private String title;

    public FeedItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static FeedItem fromCursor(Cursor cursor) {
        int index = cursor.getColumnIndex(FeedContract.Entry.COLUMN_NAME_TITLE);
        return new FeedItem(cursor.getString(index));
    }
}
