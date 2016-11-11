/*
 * Copyright 2016 Greatful World.
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

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.firebase.crash.FirebaseCrash;

/**
 * Main activity.
 */
public class MainActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_AVAILABLE_REQUEST = 9000;
    private static final int RESOLVE_CONNECTION_REQUEST = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mAppFolderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mAppFolderText = (TextView) findViewById(R.id.app_folder_text);

        if (BuildConfig.DEBUG) {
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock(TAG);
            //noinspection MissingPermission
            keyguardLock.disableKeyguard();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result.toString());
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            checkPlayServices();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case PLAY_AVAILABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            case RESOLVE_CONNECTION_REQUEST:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");
        checkAppFolder();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended");
    }

    private void checkAppFolder() {
        FirebaseCrash.log("checkAppFolder");
        ResultCallback<DriveApi.MetadataBufferResult> cb;
        cb = new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if (result == null) {
                    Log.e(TAG, "checkAppFolder MetadataBufferResult is null");
                    return;
                }
                MetadataBuffer buffer = result.getMetadataBuffer();
                if (buffer == null) {
                    Log.e(TAG, "checkAppFolder MetadataBuffer is null");
                    return;
                }
                String appFolderText = "MetadataBuffer.getCount: ";
                appFolderText += buffer.getCount() + "\n";
                for (Metadata m : buffer) {
                    String title = m.getTitle();
                    String driveId = m.getDriveId().toString();
                    appFolderText += "DriveId: " + driveId + ", Title: "
                            + title + "\n";
                }
                mAppFolderText.setText(appFolderText);
                Log.d(TAG, "appFolderText:\n" + appFolderText);
            }
        };
        Drive.DriveApi.getAppFolder(mGoogleApiClient)
                .listChildren(mGoogleApiClient)
                .setResultCallback(cb);
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int result = availability.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(availability.isUserResolvableError(result)) {
                availability.getErrorDialog(this, result,
                        PLAY_AVAILABLE_REQUEST).show();
            }
            return false;
        }
        return true;
    }
}
